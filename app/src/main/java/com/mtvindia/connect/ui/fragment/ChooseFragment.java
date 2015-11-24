package com.mtvindia.connect.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.FindMatchPresenter;
import com.mtvindia.connect.presenter.FindMatchViewInteractor;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.custom.CircleStrokeTransformation;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.QuestionPreference;
import com.mtvindia.connect.util.UserPreference;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by Sibi on 22/10/15.
 */
public class ChooseFragment extends BaseFragment implements FindMatchViewInteractor {

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject FindMatchPresenter presenter;

    @Bind(R.id.img_1)
    ImageView img1;
    @Bind(R.id.img_2)
    ImageView img2;
    @Bind(R.id.btn_skip)
    Button btnSkip;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.txt_user1_name)
    UbuntuTextView txtUser1Name;
    @Bind(R.id.txt_user2_name)
    UbuntuTextView txtUser2Name;

    private int strokeColor;
    private CircleStrokeTransformation circleStrokeTransformation;
    private List<User> matchedUser;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        presenter.setViewInteractor(this);

        User user = userPreference.readUser();

        strokeColor = getContext().getResources().getColor(android.R.color.white);
        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), strokeColor, 1);

        matchedUser = userPreference.readMatchedUser();

        if(matchedUser.size() == 0) {
            presenter.findMatches(user.getAuthHeader());
        } else {
            loadMatches(matchedUser);
        }

    }

    @OnClick(R.id.img_1)
    void clickedFirst() {
        questionPreference.saveQuestionCount(0);
        questionPreference.savePrimaryQuestionId(0);
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Bundle bundle = new Bundle();
        bundle.putInt("UserId", matchedUser.get(0).getId());
        Fragment fragment = ChatFragment.getInstance(bundle);
        navigationActivity.addFragment(fragment);
        changePreferences();
        toastShort("clicked first option");
    }

    @OnClick(R.id.img_2)
    void clickedSecond() {
        questionPreference.saveQuestionCount(0);
        questionPreference.savePrimaryQuestionId(0);
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Bundle bundle = new Bundle();
        bundle.putInt("UserId", matchedUser.get(1).getId());
        Fragment fragment = ChatFragment.getInstance(bundle);
        navigationActivity.addFragment(fragment);
        changePreferences();
        toastShort("clicked second option");
    }

    @OnClick(R.id.btn_skip)
    void clickedSkip() {
        questionPreference.saveQuestionCount(0);
        questionPreference.savePrimaryQuestionId(0);
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = PrimaryQuestionFragment.getInstance(null);
        navigationActivity.addFragment(fragment);
        changePreferences();
    }

    public static Fragment getInstance(Bundle bundle) {
        ChooseFragment chooseFragment = new ChooseFragment();
        chooseFragment.setArguments(bundle);

        return chooseFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
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
    public void showUsers(List<User> users) {
        matchedUser = users;
        userPreference.saveMatchedUser(users);
        loadMatches(matchedUser);

    }

    void changePreferences() {
        userPreference.removeMatchedUser();
    }

    void loadMatches(List<User> users) {
        if (users.size() <= 0) return;

        txtUser1Name.setText(users.get(0).getFullName());
        txtUser2Name.setText(users.get(1).getFullName());
        Picasso.with(getContext()).load(users.get(0).getProfilePic()).transform(circleStrokeTransformation).fit().into(img1);
        Picasso.with(getContext()).load(users.get(1).getProfilePic()).transform(circleStrokeTransformation).fit().into(img2);
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Error");
        toastShort("Error: " + e);
        hideProgress();
    }
}
