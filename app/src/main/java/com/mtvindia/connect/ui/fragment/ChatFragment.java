package com.mtvindia.connect.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.AboutUser;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.AboutUserViewInteractor;
import com.mtvindia.connect.presenter.concrete.AboutUserPresenterImpl;
import com.mtvindia.connect.ui.custom.CircleStrokeTransformation;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.UserPreference;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Sibi on 20/11/15.
 */
public class ChatFragment extends BaseFragment implements AboutUserViewInteractor {

    @Inject
    AboutUserPresenterImpl presenter;
    @Inject
    UserPreference userPreference;

    @Bind(R.id.img_dp)
    ImageView imgDp;
    @Bind(R.id.txt_name)
    UbuntuTextView txtName;
    @Bind(R.id.txt_about)
    UbuntuTextView txtAbout;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.txt_common)
    UbuntuTextView txtCommon;

    private int strokeColor;
    private int userId;
    private CircleStrokeTransformation circleStrokeTransformation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        presenter.setViewInteractor(this);

        strokeColor = getContext().getResources().getColor(android.R.color.white);
        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), strokeColor, 1);

        userId = getArguments().getInt("UserId");

        User user = userPreference.readUser();

        presenter.getAboutUser(userId, user.getAuthHeader());

    }


    public static Fragment getInstance(Bundle bundle) {
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);

        return chatFragment;
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
    public void onError(Throwable throwable) {
        Timber.e(throwable, "Error");
        toastShort("Error: " + throwable);
        hideProgress();
    }

    @Override
    public void aboutUser(AboutUser aboutUser) {
        txtName.setText(aboutUser.getFullName());
        txtAbout.setText(aboutUser.getAbout());
        txtCommon.setText("You and " + aboutUser.getFirstName() + " have " + aboutUser.getCommonThingsCount() + " things in common");

        Picasso.with(getContext()).load(aboutUser.getProfilePic()).transform(circleStrokeTransformation).fit().into(imgDp);
    }
}
