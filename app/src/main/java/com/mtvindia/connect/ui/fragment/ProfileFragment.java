package com.mtvindia.connect.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.mtvindia.connect.util.Bakery;
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.PermissionUtil;
import com.mtvindia.connect.util.QuestionPreference;
import com.mtvindia.connect.util.UserPreference;
import com.mtvindia.connect.util.ViewUtil;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormatSymbols;
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
public class ProfileFragment extends BaseFragment implements UpdateViewInteractor, PictureUpdateViewInteractor, ActivityCompat.OnRequestPermissionsResultCallback {

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject UpdatePresenter presenter;
    @Inject ProfilePicUpdatePresenter picUpdatePresenter;
    @Inject DialogUtil dialogUtil;
    @Inject ViewUtil viewUtil;
    @Inject Bakery bakery;
    @Inject PermissionUtil permissionUtil;

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
    @Bind(R.id.txt_gender)
    UbuntuTextView txtGender;
    @Bind(R.id.layout_dialog_gender)
    RelativeLayout layoutDialogGender;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private static String[] PERMISSION = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA };

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

        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkRed));
        }

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

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSION[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), PERMISSION[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), PERMISSION[2]) != PackageManager.PERMISSION_GRANTED) {
            // Display a SnackBar with an explanation and a button to trigger the request.
            requestPermission();
        } else {
            showPictureDialog();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), PERMISSION[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), PERMISSION[1])
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), PERMISSION[2])) {
            bakery.snack(getContentView(), "Camera and storage permission are required to change image", Snackbar.LENGTH_INDEFINITE,  "Try Again", new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSION, CAMERA_REQUEST_CODE);
                }
            });
        } else {
            requestPermissions(PERMISSION, CAMERA_REQUEST_CODE);
        }
    }


    void loadData() {
        user = userPreference.readUser();

        String birthDay = user.getBirthDate();
        DateTime dateTime = DateTime.parse(birthDay);
        if (birthDay != null) {
            year = dateTime.getYear();
            month = dateTime.getMonthOfYear();
            date = dateTime.getDayOfMonth();

            txtYear.setText(String.valueOf(year));
            txtDay.setText(String.valueOf(date));
            txtMonth.setText(new DateFormatSymbols().getMonths()[month - 1].substring(0,3));
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
        DateTime dateTime = DateTime.now();
        Calendar calendar = Calendar.getInstance();
        if (year == 0) {
            year = dateTime.getYear();
            month = dateTime.getMonthOfYear();
            date = dateTime.getDayOfMonth();
        }


        DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                txtDay.setText(String.valueOf(selectedday));
                txtMonth.setText(new DateFormatSymbols().getMonths()[selectedmonth].substring(0,3));
                txtYear.setText(String.valueOf(selectedyear));
                year = selectedyear;
                month = selectedmonth + 1;
                date = selectedday;
            }
        }, year, month, date);
        datePicker.setTitle("Select date");
        if (user.getBirthDate().isEmpty() || user.getBirthDate() == null) {
            calendar.set(calendar.get(Calendar.YEAR) - 18, calendar.get(Calendar.MONTH) -1, calendar.get(Calendar.DATE));
        } else {
            DateTime birthday = DateTime.parse(user.getBirthDate());
            calendar.set(birthday.getYear(), birthday.getMonthOfYear() - 1, birthday.getDayOfMonth());
        }
        //datePicker.getDatePicker().setMaxDate(DateTime.now().minusYears(18).toDate().getTime());
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
        checkPermissions();
    }

    private void showPictureDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setIcon(android.R.drawable.ic_menu_camera);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    openCamera();
                } else if (items[item].equals("Choose from Gallery")) {
                    openGallery();
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), MEDIA_TYPE_IMAGE);
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
        bakery.dismiss();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (permissionUtil.verifyPermissions(grantResults)) {
                Timber.d("come here");
                bakery.snackShort(getContentView(),"Permissions have been granted");
                showPictureDialog();
            } else {
                /*bakery.snack(getContentView(), "Camera and storage permission are required to change image", Snackbar.LENGTH_INDEFINITE, "Try Again", new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        ActivityCompat.requestPermissions(getActivity(), PERMISSION, REQUEST_CODE);
                    }
                });*/
                bakery.snackShort(getContentView(), "Permissions were not granted");
            }
        }
    }

}
