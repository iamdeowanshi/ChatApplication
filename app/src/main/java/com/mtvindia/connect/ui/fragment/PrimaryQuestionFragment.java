package com.mtvindia.connect.ui.fragment;

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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.Option;
import com.mtvindia.connect.data.model.Question;
import com.mtvindia.connect.data.model.ResultRequest;
import com.mtvindia.connect.data.model.ResultResponse;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.QuestionRequestPresenter;
import com.mtvindia.connect.presenter.QuestionViewInteractor;
import com.mtvindia.connect.presenter.ResultPresenter;
import com.mtvindia.connect.presenter.ResultViewInteractor;
import com.mtvindia.connect.ui.activity.NavigationActivity;
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
 * @author Aaditya Deowanshi
 *
 *         Primary question screen, which displays first high level question.
 */

public class PrimaryQuestionFragment extends BaseFragment implements QuestionViewInteractor, ResultViewInteractor{

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject QuestionRequestPresenter questionRequestPresenter;
    @Inject ResultPresenter resultPresenter;

    @Bind(R.id.img_dp) ImageView userPic;
    @Bind(R.id.img_dp_big_1) ImageView picOption1;
    @Bind(R.id.img_dp_big_2) ImageView picOption2;
    @Bind(R.id.progress) ProgressBar progress;
    @Bind(R.id.txt_hello) UbuntuTextView txtHello;
    @Bind(R.id.txt_option_1) UbuntuTextView txtOption1;
    @Bind(R.id.txt_option_2) UbuntuTextView txtOption2;
    @Bind(R.id.txt_prim_quest) UbuntuTextView txtPrimQuest;
    @Bind(R.id.view) View view;

    private User user;
    private Question question;
    private List<Option> option;
    private ResultRequest resultRequest = new ResultRequest();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.primary_question_fragment, container, false);

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
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkYellow));
        }

        questionRequestPresenter.setViewInteractor(this);
        resultPresenter.setViewInteractor(this);

        user = userPreference.readUser();

        questionRequestPresenter.getPrimaryQuestion(user.getAuthHeader());
        loadUserDetails(user);
    }

    @Override
    public void onResume() {
        super.onResume();
        questionRequestPresenter.resume();
        resultPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        questionRequestPresenter.pause();
        resultPresenter.pause();
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
    public void showResult(ResultResponse response) {
        questionPreference.saveResultResponse(response);

        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = ResultFragment.getInstance(null);
        navigationActivity.addFragment(fragment);
    }

    @Override
    public void showQuestion(Question response) {
        question = response;
        questionPreference.saveQuestionResponse(question);
        questionPreference.savePrimaryQuestionId(question.getQuestionId());

        resultRequest.setQuestionId(question.getQuestionId());
        option = question.getOptions();

        txtPrimQuest.setText(question.getQuestion());
        txtOption1.setText(option.get(0).getOption());
        txtOption2.setText(option.get(1).getOption());

        view.setVisibility(View.VISIBLE);

        Picasso.with(getContext()).load(option.get(0).getOptionUrl()).fit().into(picOption1);
        picOption1.setVisibility(View.VISIBLE);
        Picasso.with(getContext()).load(option.get(1).getOptionUrl()).fit().into(picOption2);
        picOption2.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(Throwable throwable) {
        hideProgress();
        Timber.e(throwable, "Error");
        toastShort("Error: " + throwable);
    }

    @OnClick(R.id.img_dp_big_1)
    void option1() {
        optionSelected(option.get(0).getOptionId());
        questionPreference.saveOptionSelected(1);
    }

    @OnClick(R.id.img_dp_big_2)
    void option2() {
        optionSelected(option.get(1).getOptionId());
        questionPreference.saveOptionSelected(2);
    }

    /**
     * Method to make api call for secondary question on the basis of primary question selected.
     * @param option
     */
    private void optionSelected(int option) {
        question.setIsAnswered(true);
        questionPreference.saveQuestionResponse(question);
        questionPreference.saveQuestionCount(1);
        resultRequest.setPrimaryQuestionId(0);
        resultRequest.setOptionId(option);

        resultPresenter.requestResult(resultRequest, user.getAuthHeader());
    }

    /**
     * Load user details.
     * @param user
     */
    private void loadUserDetails(User user) {
        txtHello.setText("Hello " + user.getFirstName() + "!");
        Picasso.with(getContext()).load(user.getProfilePic()).fit().into(userPic);
    }

    public static Fragment getInstance(Bundle bundle) {
        PrimaryQuestionFragment primaryQuestionFragment = new PrimaryQuestionFragment();
        primaryQuestionFragment.setArguments(bundle);

        return primaryQuestionFragment;
    }

}
