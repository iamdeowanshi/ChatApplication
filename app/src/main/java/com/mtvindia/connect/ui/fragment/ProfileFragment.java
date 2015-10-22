package com.mtvindia.connect.ui.fragment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.activity.NavigationItem;
import com.mtvindia.connect.ui.custom.CircleStrokeTransformation;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sibi on 16/10/15.
 */
public class ProfileFragment extends BaseFragment {

    @Bind(R.id.img_dp)
    ImageView imgDp;
    @Bind(R.id.edt_name)
    EditText edtName;
    @Bind(R.id.date_picker)
    DatePicker datePicker;
    @Bind(R.id.edt_about)
    EditText edtAbout;
    @Bind(R.id.btn_save)
    Button save;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private CircleStrokeTransformation circleStrokeTransformation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), android.R.color.transparent, 1);

        Picasso.with(getContext()).load(R.drawable.img_dp).fit().transform(circleStrokeTransformation).into(imgDp);

    }

    @OnClick(R.id.btn_save)
    void save() {
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = PrimaryQuestionFragment.getInstance(null);
        navigationActivity.addFragment(fragment);

        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragment.onItemSelected(NavigationItem.FIND_PEOPLE);
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
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), MEDIA_TYPE_IMAGE);
                }
            }
        });
        builder.show();
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
                Picasso.with(getContext()).load(uri).transform(circleStrokeTransformation).fit().into(imgDp);
                break;

            case MEDIA_TYPE_IMAGE:
                Uri selectedImage = data.getData();
                Picasso.with(getContext()).load(selectedImage).transform(circleStrokeTransformation).fit().into(imgDp);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
