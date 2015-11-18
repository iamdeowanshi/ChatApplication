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
 * Created by Sibi on 21/10/15.
 */
public class PrimaryQuestionFragment extends BaseFragment implements QuestionViewInteractor, ResultViewInteractor{

    @Inject UserPreference userPreference;
    @Inject QuestionPreference questionPreference;
    @Inject QuestionRequestPresenter questionRequestPresenter;
    @Inject
    ResultPresenter resultPresenter;

    @Bind(R.id.img_dp)
    ImageView userPic;
    @Bind(R.id.img_dp_big_1)
    ImageView picOption1;
    @Bind(R.id.img_dp_big_2)
    ImageView picOption2;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.txt_hello)
    UbuntuTextView txtHello;
    @Bind(R.id.txt_option_1)
    UbuntuTextView txtOption1;
    @Bind(R.id.txt_option_2)
    UbuntuTextView txtOption2;
    @Bind(R.id.txt_prim_quest)
    UbuntuTextView txtPrimQuest;
    @Bind(R.id.view)
    View view;

    private CircleStrokeTransformation circleStrokeTransformation;
    private CircleStrokeTransformation circleStrokeTransformationDp;

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

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        questionRequestPresenter.setViewInteractor(this);
        resultPresenter.setViewInteractor(this);

        user = userPreference.readUser();

        questionRequestPresenter.getPrimaryQuestion(user.getAuthHeader());

        questionPreference.savePrimaryQuestionId(0);

        int strokeColor = getContext().getResources().getColor(android.R.color.white);
        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), strokeColor, 1);
        circleStrokeTransformationDp = new CircleStrokeTransformation(getContext(), strokeColor, 2);

        txtHello.setText("Hello " + user.getFirstName() + "!");

        Picasso.with(getContext()).load(user.getProfilePic()).transform(circleStrokeTransformationDp).into(userPic);


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

    void optionSelected(int option) {
        question.setIsAnswered(true);
        questionPreference.saveQuestionResponse(question);
        questionPreference.saveQuestionCount(1);
        resultRequest.setPrimaryQuestionId(0);
        resultRequest.setOptionId(option);

        resultPresenter.requestResult(resultRequest, user.getAuthHeader());
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

        Picasso.with(getContext()).load(option.get(0).getOptionUrl()).transform(circleStrokeTransformation).into(picOption1);
        picOption1.setVisibility(View.VISIBLE);
        Picasso.with(getContext()).load(option.get(1).getOptionUrl()).transform(circleStrokeTransformation).into(picOption2);
        picOption2.setVisibility(View.VISIBLE);

    }

    @Override
    public void onError(Throwable throwable) {
        Timber.e(throwable, "Error");
        toastShort("Error: " + throwable);
        hideProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        questionRequestPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        questionRequestPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public static Fragment getInstance(Bundle bundle) {
        PrimaryQuestionFragment primaryQuestionFragment = new PrimaryQuestionFragment();
        primaryQuestionFragment.setArguments(bundle);

        return primaryQuestionFragment;
    }

}
