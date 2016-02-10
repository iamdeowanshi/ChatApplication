package com.mtvindia.connect.ui.fragment;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.presenter.ChatListPresenter;
import com.mtvindia.connect.presenter.FindMatchPresenter;
import com.mtvindia.connect.presenter.FindMatchViewInteractor;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.DialogUtil;
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
 * @author Aaditya Deowanshi
 *
 *         Choose fragmwnt screen, where user gets two options to choose from to chat with.
 */

public class ChooseFragment extends BaseFragment implements FindMatchViewInteractor {

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject FindMatchPresenter presenter;
    @Inject ChatListPresenter chatListPresenter;
    @Inject ChatListRepository chatListRepository;
    @Inject DialogUtil dialogUtil;

    @Bind(R.id.img_1) ImageView img1;
    @Bind(R.id.img_2) ImageView img2;
    @Bind(R.id.progress) ProgressBar progress;
    @Bind(R.id.txt_user1_name) UbuntuTextView txtUser1Name;
    @Bind(R.id.txt_user2_name) UbuntuTextView txtUser2Name;

    private final static int FIRST = 0;
    private final static int SECOND = 1;

    private boolean userPresent;
    private List<User> matchedUser;
    private ChatList chatList = new ChatList();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_fragment, container, false);
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
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkPurple));
        }

        presenter.setViewInteractor(this);
        matchedUser = userPreference.readMatchedUser();

        if (matchedUser.size() == 0) {
            presenter.findMatches(userPreference.readUser().getAuthHeader());

            return;
        }

        loadMatches(matchedUser);
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
        if (users.size() != 2) {
            final android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialogUtil.createAlertDialog(getActivity(), "Sorry", "No compatible matches found", "Try Again", "");
            alertDialog.show();
            Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    questionPreference.saveQuestionCount(0);
                    questionPreference.savePrimaryQuestionId(0);
                    NavigationActivity navigationActivity = (NavigationActivity) getContext();
                    Fragment fragment = PrimaryQuestionFragment.getInstance(null);
                    navigationActivity.addFragment(fragment);
                    clearPreferences();
                    alertDialog.dismiss();
                }
            });

        }

        matchedUser = users;
        userPreference.saveMatchedUser(users);
        loadMatches(matchedUser);
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "Error");
        hideProgress();
    }

    @OnClick(R.id.img_1)
    void clickedFirst() {
        setNextScreen(FIRST);
    }

    @OnClick(R.id.img_2)
    void clickedSecond() {
        setNextScreen(SECOND);
    }

    @OnClick(R.id.btn_skip)
    void clickedSkip() {
        questionPreference.saveQuestionCount(0);
        questionPreference.savePrimaryQuestionId(0);
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = PrimaryQuestionFragment.getInstance(null);
        navigationActivity.addFragment(fragment);
        clearPreferences();
    }

    /**
     * If user is present in database, load chat activity
     * else display about user screen.
     * @param position
     */
    private void setNextScreen(int position) {
        int id = matchedUser.get(position).getId();
        questionPreference.saveQuestionCount(0);
        questionPreference.savePrimaryQuestionId(0);
        Bundle bundle = new Bundle();

        if (userPresent(id)) {
            bundle.putInt("userId", id);
            startActivity(ChatActivity.class, bundle);
            getActivity().finish();

            return;
        }

        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        bundle.putInt("UserId", id);
        Fragment fragment = DisplayUserFragment.getInstance(bundle);
        navigationActivity.addFragment(fragment);
        saveUserToDb(position);

        clearPreferences();

    }

    /**
     * Checking whether user is present in database or not.
     * @param id
     * @return
     */
    private boolean userPresent(final int id) {
        userPresent = chatListRepository.searchUser(id, userPreference.readUser().getId());

        return userPresent;
    }

    /**
     * Saving user to database.
     * @param position
     */
    private void saveUserToDb(int position) {
        chatList.setUserId(matchedUser.get(position).getId());
        chatList.setImage(matchedUser.get(position).getProfilePic());
        chatList.setName(matchedUser.get(position).getFullName());
        chatList.setLastMessage("");
        chatList.setTime("");
        chatList.setLogedinUser(userPreference.readUser().getId());
        chatListRepository.save(chatList);
        chatListPresenter.addUser(userPreference.readUser().getId(), chatList.getUserId(), userPreference.readUser().getAuthHeader());
    }

    /**
     * Clearing preferences for matched user.
     */
    void clearPreferences() {
        userPreference.removeMatchedUser();
    }

    /**
     * Display Mtached users.
     * @param users
     */
    void loadMatches(List<User> users) {
        if (users.size() <= 0) return;

        txtUser1Name.setText(users.get(0).getFullName());
        txtUser2Name.setText(users.get(1).getFullName());
        Picasso.with(getContext()).load(users.get(0).getProfilePic()).fit().into(img1);
        Picasso.with(getContext()).load(users.get(1).getProfilePic()).fit().into(img2);
    }

    public static Fragment getInstance(Bundle bundle) {
        ChooseFragment chooseFragment = new ChooseFragment();
        chooseFragment.setArguments(bundle);

        return chooseFragment;
    }

}
