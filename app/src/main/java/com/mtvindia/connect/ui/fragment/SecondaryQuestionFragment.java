package com.mtvindia.connect.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 *         Secondary question screen to load secondary questions.
 */

public class SecondaryQuestionFragment extends BaseFragment implements QuestionViewInteractor, ResultViewInteractor {

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject QuestionRequestPresenter questionRequestPresenter;
    @Inject ResultPresenter resultPresenter;

    @Bind(R.id.img_dp_big_1) ImageView picOption1;
    @Bind(R.id.img_dp_big_2) ImageView picOption2;
    @Bind(R.id.img_dp_big_3) ImageView picOption3;
    @Bind(R.id.img_dp_big_4) ImageView picOption4;
    @Bind(R.id.layer2) RelativeLayout layer2;
    @Bind(R.id.linear_layout) LinearLayout linearLayout;
    @Bind(R.id.txt_second_quest) TextView txtFirstHigh;
    @Bind(R.id.blank_view) View blankView;
    @Bind(R.id.txt_option_1) UbuntuTextView txtOption1;
    @Bind(R.id.txt_option_2) UbuntuTextView txtOption2;
    @Bind(R.id.txt_option_3) UbuntuTextView txtOption3;
    @Bind(R.id.txt_option_4) UbuntuTextView txtOption4;
    @Bind(R.id.progress) ProgressBar progress;
    @Bind(R.id.view) View view;
    @Bind(R.id.view2) View view2;

    private int count;
    private User user;
    private ResultRequest resultRequest = new ResultRequest();

    private Question question;
    private List<Option> options;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.secondary_question_fragment, container, false);

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
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkYellow));
        }

        questionRequestPresenter.setViewInteractor(this);
        resultPresenter.setViewInteractor(this);

        user = userPreference.readUser();
        question = questionPreference.readQuestionResponse();
        count = questionPreference.readQuestionCount();

        if (isQuestionAnswered()) {
            questionRequestPresenter.getSecondaryQuestion(questionPreference.readPrimaryQuestionId(), user.getAuthHeader());

            return;
        }

        loadQuestion(question);
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
    public void showQuestion(Question question) {
        questionPreference.saveQuestionResponse(question);
        loadQuestion(question);
    }

    @Override
    public void onError(Throwable throwable) {
        Timber.e(throwable, "Error");
        toastShort("Error: " + throwable);
        hideProgress();
    }

    @OnClick(R.id.img_dp_big_1)
    void option1() {
        optionSelected(0);
        questionPreference.saveOptionSelected(1);
    }

    @OnClick(R.id.img_dp_big_2)
    void option2() {
        optionSelected(1);
        questionPreference.saveOptionSelected(2);
    }

    @OnClick(R.id.img_dp_big_3)
    void option3() {
        optionSelected(2);
        questionPreference.saveOptionSelected(3);
    }

    @OnClick(R.id.img_dp_big_4)
    void option4() {
        optionSelected(3);
        questionPreference.saveOptionSelected(4);
    }

    /**
     * Method to check whether question is answeres or not.
     *
     * @return
     */
    boolean isQuestionAnswered() {
        return question.isAnswered();
    }

    /**
     * Method to load secondary questions on the basis of option selected.
     *
     * @param option
     */
    void optionSelected(int option) {
        count++;
        question.setIsAnswered(true);
        questionPreference.saveQuestionCount(count);
        questionPreference.saveQuestionResponse(question);

        resultRequest.setOptionId(options.get(option).getOptionId());
        resultRequest.setPrimaryQuestionId(questionPreference.readPrimaryQuestionId());

        resultPresenter.requestResult(resultRequest, user.getAuthHeader());
    }

    /**
     * Method to load questions.
     *
     * @param question
     */
    private void loadQuestion(Question question) {
        options = question.getOptions();
        txtFirstHigh.setText(question.getQuestion());
        resultRequest.setQuestionId(question.getQuestionId());
        setOptionsView(options.size());
    }

    /**
     * Method to set option view for 2 or 4 options.
     *
     * @param size
     */
    private void setOptionsView(int size) {
        if (size >= 2 && size < 4) {
            layer2.setVisibility(View.GONE);
            blankView.setVisibility(View.GONE);
            linearLayout.setGravity(Gravity.CENTER);

            txtOption1.setText(options.get(0).getOption());
            txtOption2.setText(options.get(1).getOption());

            view.setVisibility(View.VISIBLE);

            Picasso.with(getContext()).load(options.get(0).getOptionUrl()).fit().into(picOption1);
            picOption1.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(options.get(1).getOptionUrl()).fit().into(picOption2);
            picOption2.setVisibility(View.VISIBLE);

            return;
        }

        if (size == 4) {
            view.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);

            txtOption1.setText(options.get(0).getOption());
            Picasso.with(getContext()).load(options.get(0).getOptionUrl()).fit().into(picOption1);
            picOption1.setVisibility(View.VISIBLE);

            txtOption2.setText(options.get(1).getOption());
            Picasso.with(getContext()).load(options.get(1).getOptionUrl()).fit().into(picOption2);
            picOption2.setVisibility(View.VISIBLE);

            txtOption3.setText(options.get(2).getOption());
            Picasso.with(getContext()).load(options.get(2).getOptionUrl()).fit().into(picOption3);
            picOption3.setVisibility(View.VISIBLE);

            txtOption4.setText(options.get(3).getOption());
            Picasso.with(getContext()).load(options.get(3).getOptionUrl()).fit().into(picOption4);
            picOption4.setVisibility(View.VISIBLE);
        }
    }

    public static Fragment getInstance(Bundle bundle) {
        SecondaryQuestionFragment secondaryQuestionFragment = new SecondaryQuestionFragment();
        secondaryQuestionFragment.setArguments(bundle);

        return secondaryQuestionFragment;
    }

}
