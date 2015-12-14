package com.mtvindia.connect.ui.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.NavigationItem;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.PictureUpdateViewInteractor;
import com.mtvindia.connect.presenter.ProfilePicUpdatePresenter;
import com.mtvindia.connect.presenter.UpdatePresenter;
import com.mtvindia.connect.presenter.UpdateViewInteractor;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.QuestionPreference;
import com.mtvindia.connect.util.UserPreference;
import com.mtvindia.connect.util.ViewUtil;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.mime.TypedFile;
import timber.log.Timber;

/**
 * Created by Sibi on 16/10/15.
 */
public class ProfileFragment extends BaseFragment implements UpdateViewInteractor, PictureUpdateViewInteractor{

    @Inject
    UserPreference userPreference;
    @Inject
    QuestionPreference questionPreference;
    @Inject
    UpdatePresenter presenter;
    @Inject
    ProfilePicUpdatePresenter picUpdatePresenter;
    @Inject
    DialogUtil dialogUtil;
    @Inject
    ViewUtil viewUtil;

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
    @Bind(R.id.txt_gender)
    UbuntuTextView txtGender;
    @Bind(R.id.layout_dialog_gender)
    RelativeLayout layoutDialogGender;

    private int date;
    private int month;
    private int year;

    private User user;

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
        picUpdatePresenter.setViewInteractor(this);

        layoutDialogGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(" Select Gender:");
                builder.setIcon(R.drawable.icon_gender);
                builder.setItems(R.array.gender_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] interested = getResources().getStringArray(R.array.gender_array);
                        txtGender.setText(interested[which]);
                    }
                });
                builder.show();
            }
        });

        loadData();
    }

    void loadData() {
        user = userPreference.readUser();

        String birthDay = user.getBirthDate();
        if (birthDay != null) {
            String[] string = birthDay.split("-");
            year = Integer.parseInt(string[0]);
            month = Integer.parseInt(string[1]);

            String[] stringDay = string[2].split("T");
            date = Integer.parseInt(stringDay[0]);
            txtYear.setText(string[0]);
            txtDay.setText(stringDay[0]);
            txtMonth.setText(getMonthName(month));
        }

        if( ! userPreference.readLoginStatus()) {
            txtGender.setText(user.getGender());
            edtAbout.setText(user.getAbout());
        }

        Picasso.with(getContext()).load(user.getProfilePic()).fit().into(imgDp);
        edtName.setText(user.getFullName());
    }

    @OnClick(R.id.txt_date_picker)
    void selectDate() {
        Calendar currentDate = Calendar.getInstance();
        if (year == 0) {
            year = currentDate.get(Calendar.YEAR);
            month = currentDate.get(Calendar.MONTH) + 1;
            date = currentDate.get(Calendar.DAY_OF_MONTH);
        }


        DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                txtDay.setText(String.valueOf(selectedday));
                txtMonth.setText(getMonthName(selectedmonth + 1));
                txtYear.setText(String.valueOf(selectedyear));
                year = selectedyear;
                month = selectedmonth + 1;
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
        viewUtil.hideKeyboard(getView());

        if(validation()) {

        String name = edtName.getText().toString();
        StringTokenizer tokenizer = new StringTokenizer(name);
        user.setFirstName(tokenizer.nextElement().toString());
        if (tokenizer.hasMoreElements()) {
            user.setLastName(tokenizer.nextElement().toString());
        } else {
            user.setLastName("");
        }

        //user.setAbout(edtAbout.getText().toString().replaceAll("(?m)^[ \t]*\r?\n", ""));
            user.setAbout(edtAbout.getText().toString().trim());
              user.setBirthDate(year + "-" + (month) + "-" + date);
              user.setGender(txtGender.getText().toString());

              presenter.update(user);
        }
    }

    boolean validation() {
        if (edtName == null || edtName.getText().toString().trim().equals("")) {
            return validationDialogName();
        } else if(edtAbout == null || edtAbout.getText().toString().trim().equals("")) {
            return validationDialogAbout();
        } else if(txtDay == null || txtMonth == null || txtYear == null || txtDay.getText().equals("") || txtMonth.getText().equals("") || txtYear .getText().equals("")) {
            return validationDialogBirthDate();
        } else if(txtGender == null || txtGender.getText().toString().equals("")) {
            return validationDialogGender();
        }
        return true;
    }

    boolean validationDialogName() {
        final android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialogUtil.createAlertDialog(getActivity(), "Invalid Name", "Please enter valid name", "Ok", "");
        alertDialog.show();
        Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        return false;
    }

    boolean validationDialogAbout() {
        final android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialogUtil.createAlertDialog(getActivity(), "Invalid Details", "Please enter valid information about you", "Ok", "");
        alertDialog.show();
        Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        return false;
    }

    boolean validationDialogBirthDate() {
        final android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialogUtil.createAlertDialog(getActivity(), "Select Birth Date", "Please select your birthday", "Ok", "");
        alertDialog.show();
        Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        return false;
    }

    boolean validationDialogGender() {
        final android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialogUtil.createAlertDialog(getActivity(), "Select Gender", "Please select your gender", "Ok", "");
        alertDialog.show();
        Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        return false;
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
        switch (month) {
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
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                Uri uri = saveBitmapToMedia(bitmap);
                uploadFile(new File(getRealPathFromUri(uri)));
                break;

            case MEDIA_TYPE_IMAGE:
                Uri selectedImageUri = data.getData();
                uploadFile(new File(getRealPathFromUri(selectedImageUri)));
                break;
        }
    }

    private Uri saveBitmapToMedia(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), inImage, "MTVConnectProfile", null);

        return Uri.parse(path);
    }

    private String getRealPathFromUri(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

        return cursor.getString(idx);
    }

    void uploadFile(File file) {
        TypedFile typedFile=new TypedFile("image/jpeg", file);
        picUpdatePresenter.updateProfilePic(user.getId(), typedFile, user.getAuthHeader());
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
        toastShort("Saved");

        if (userPreference.readLoginStatus()) {
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
        Timber.e(throwable, "Error");
        toastShort("Error: " + throwable);
        hideProgress();
    }

    @Override
    public void showPicUpdateProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePicUpdateProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onPicUpdateError(Throwable throwable) {
        Timber.e(throwable, "Error");
        toastShort("Error: " + throwable);
        hideProgress();
    }

    @Override
    public void showUpdatedPic(User result) {
        user.setProfilePic(result.getProfilePic());
        Picasso.with(getContext()).load(user.getProfilePic()).fit().into(imgDp);
        userPreference.saveUser(user);
        toastShort("Picture Updated");
    }

}
