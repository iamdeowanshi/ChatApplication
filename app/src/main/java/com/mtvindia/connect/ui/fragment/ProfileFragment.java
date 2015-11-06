package com.mtvindia.connect.ui.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.UpdatePresenter;
import com.mtvindia.connect.presenter.UpdateViewInteractor;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.activity.NavigationItem;
import com.mtvindia.connect.ui.custom.CircleStrokeTransformation;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.QuestionPreference;
import com.mtvindia.connect.util.UserPreference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.StringTokenizer;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sibi on 16/10/15.
 */
public class ProfileFragment extends BaseFragment implements UpdateViewInteractor {

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject UpdatePresenter presenter;

    @Bind(R.id.img_dp)
    ImageView imgDp;
    @Bind(R.id.edt_name)
    EditText edtName;
    @Bind(R.id.edt_about)
    EditText edtAbout;
    @Bind(R.id.txt_day)
    TextView txtDay;
    @Bind(R.id.txt_month)
    UbuntuTextView txtMonth;
    @Bind(R.id.txt_year)
    UbuntuTextView txtYear;
    @Bind(R.id.progress)
    ProgressBar progress;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private int date;
    private int month;
    private int year;

    private User user;

    private CircleStrokeTransformation circleStrokeTransformation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        presenter.setViewInteractor(this);
        user = userPreference.readUser();

        String birthDay = user.getBirthDay();
        if(birthDay != null) {
            String[] string = birthDay.split("-");
            year = Integer.parseInt(string[0]);
            month = Integer.parseInt(string[1]);
            date = Integer.parseInt(string[2]);
            txtYear.setText(string[0]);
            txtDay.setText(string[2]);
            txtMonth.setText(getMonthName(month));
        }


        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), android.R.color.transparent, 1);

        Picasso.with(getContext()).load(user.getProfilePic()).transform(circleStrokeTransformation).into(imgDp);
        edtName.setText(user.getFirstName() + " " + user.getLastName());
        edtAbout.setText(user.getAbout());

    }

    @OnClick(R.id.txt_date_picker)
    void selectDate() {
        Calendar currentDate = Calendar.getInstance();
        if(year == 0) {
            year = currentDate.get(Calendar.YEAR);
            month = currentDate.get(Calendar.MONTH);
            date = currentDate.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                txtDay.setText(String.valueOf(selectedday));
                txtMonth.setText(getMonthName(selectedmonth));
                txtYear.setText(String.valueOf(selectedyear));
                year = selectedyear;
                month = selectedmonth;
                date = selectedday;
            }
        }, year, month, date);
        datePicker.setTitle("Select date");
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR) - 18, calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        datePicker.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePicker.getDatePicker().setSpinnersShown(true);
        datePicker.getDatePicker().getSpinnersShown();
        datePicker.getDatePicker().setCalendarViewShown(false);
        datePicker.show();
    }

    @OnClick(R.id.btn_save)
    void save() {
        String name = edtName.getText().toString();
        StringTokenizer tokenizer = new StringTokenizer(name);

        if (tokenizer.hasMoreElements()) {
            user.setFirstName(tokenizer.nextElement().toString());
            user.setLastName(tokenizer.nextElement().toString());
        } else {
            user.setFirstName(name);
        }

        user.setAbout(edtAbout.getText().toString());
        user.setBirthDay(year + "-" + month + "-" + date);

        presenter.update(user);
    }

    public static Fragment getInstance(Bundle bundle) {
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);

        return profileFragment;
    }

    @OnClick(R.id.img_dp)
    void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setIcon(android.R.drawable.ic_menu_camera);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), MEDIA_TYPE_IMAGE);
                }
            }
        });
        builder.show();
    }

    public static String getMonthName(int month) {
        switch (month + 1) {
            case 1:
                return "Jan";

            case 2:
                return "Feb";

            case 3:
                return "Mar";

            case 4:
                return "Apr";

            case 5:
                return "May";

            case 6:
                return "Jun";

            case 7:
                return "Jul";

            case 8:
                return "Aug";

            case 9:
                return "Sep";

            case 10:
                return "Oct";

            case 11:
                return "Nov";

            case 12:
                return "Dec";
        }

        return "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), photo, "Title", null);
                Uri uri = Uri.parse(path);
                user.setProfilePic(uri.toString());
                Picasso.with(getContext()).load(uri).transform(circleStrokeTransformation).fit().into(imgDp);
                break;

            case MEDIA_TYPE_IMAGE:
                Uri selectedImage = data.getData();
                user.setProfilePic(selectedImage.toString());
                Picasso.with(getContext()).load(selectedImage).transform(circleStrokeTransformation).fit().into(imgDp);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void updateDone(User user) {
        userPreference.saveUser(user);

        if(userPreference.readLoginStatus()) {
            userPreference.saveLoginStatus(false);
            questionPreference.saveQuestionCount(0);
            NavigationActivity navigationActivity = (NavigationActivity) getContext();
            Fragment fragment = PrimaryQuestionFragment.getInstance(null);
            navigationActivity.addFragment(fragment);

            NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
            navigationDrawerFragment.onItemSelected(NavigationItem.FIND_PEOPLE);
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
