package com.mtvindia.connect.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mtvindia.connect.ui.custom.CircleStrokeTransformation;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.PreferenceUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sibi on 22/10/15.
 */
public class SecondaryQuestionFragment extends BaseFragment implements QuestionViewInteractor, ResultViewInteractor {

    @Inject
    PreferenceUtil preferenceUtil;
    @Inject
    QuestionRequestPresenter questionRequestPresenter;
    @Inject
    ResultPresenter resultPresenter;

    @Bind(R.id.img_dp_big_1)
    ImageView picOption1;
    @Bind(R.id.img_dp_big_2)
    ImageView picOption2;
    @Bind(R.id.img_dp_big3)
    ImageView picOption3;
    @Bind(R.id.img_dp_big4)
    ImageView picOption4;
    @Bind(R.id.layer2)
    RelativeLayout layer2;
    @Bind(R.id.linear_layout)
    LinearLayout linearLayout;
    @Bind(R.id.txt_second_quest)
    TextView txtFirstHigh;
    @Bind(R.id.blank_view)
    View blankView;
    @Bind(R.id.txt_option_1)
    UbuntuTextView txtOption1;
    @Bind(R.id.txt_option_2)
    UbuntuTextView txtOption2;
    @Bind(R.id.txt_option_3)
    UbuntuTextView txtOption3;
    @Bind(R.id.txt_option_4)
    UbuntuTextView txtOption4;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.view2)
    View view2;

    private CircleStrokeTransformation circleStrokeTransformation;
    private int strokeColor;
    private int count;
    private User user;
    private ResultRequest resultRequest = new ResultRequest();

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
        questionRequestPresenter.setViewInteractor(this);
        resultPresenter.setViewInteractor(this);

        user = (User) preferenceUtil.read(PreferenceUtil.USER, User.class);

        questionRequestPresenter.getSecondaryQuestion(preferenceUtil.readInt(PreferenceUtil.PRIMARY_QUESTION_ID, 0), user.getAuthHeader());

        count = preferenceUtil.readInt(PreferenceUtil.QUESTIONS_ANSWERED, 0);


        strokeColor = getContext().getResources().getColor(android.R.color.white);
        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), strokeColor, 1);

    }

    @OnClick(R.id.img_dp_big_1)
    void option1() {
        optionSelected(0);
    }

    @OnClick(R.id.img_dp_big_2)
    void option2() {
        optionSelected(1);
    }

    @OnClick(R.id.img_dp_big3)
    void option3() {
        optionSelected(2);
    }

    @OnClick(R.id.img_dp_big4)
    void option4() {
        optionSelected(3);
    }

    void optionSelected(int option) {
        count++;
        preferenceUtil.save(PreferenceUtil.QUESTIONS_ANSWERED, count);

        resultRequest.setOptionId(options.get(option).getOptionId());
        resultRequest.setPrimaryQuestionId(preferenceUtil.readInt(PreferenceUtil.PRIMARY_QUESTION_ID, 0));

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
        preferenceUtil.save(PreferenceUtil.RESULT_RESPONSE, response);

        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = ResultFragment.getInstance(null);
        navigationActivity.addFragment(fragment);
    }

    @Override
    public void showQuestion(Question question) {
        options = question.getOptions();
        txtFirstHigh.setText(question.getQuestion());
        resultRequest.setQuestionId(question.getQuestionId());
        setView(options.size());
    }

    void setView(int size) {
        if (size == 2) {
            layer2.setVisibility(View.GONE);
            blankView.setVisibility(View.GONE);
            linearLayout.setGravity(Gravity.CENTER);

            txtOption1.setText(options.get(0).getOption());
            txtOption2.setText(options.get(1).getOption());

            view.setVisibility(View.VISIBLE);

            Picasso.with(getContext()).load(options.get(0).getOptionUrl()).transform(circleStrokeTransformation).into(picOption1);
            Picasso.with(getContext()).load(options.get(1).getOptionUrl()).transform(circleStrokeTransformation).into(picOption2);
        } else {
            txtOption1.setText(options.get(0).getOption());
            txtOption2.setText(options.get(1).getOption());
            txtOption3.setText(options.get(2).getOption());
            txtOption4.setText(options.get(3).getOption());

            view.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);

            Picasso.with(getContext()).load(options.get(0).getOptionUrl()).transform(circleStrokeTransformation).into(picOption1);
            Picasso.with(getContext()).load(options.get(1).getOptionUrl()).transform(circleStrokeTransformation).into(picOption2);
            Picasso.with(getContext()).load(options.get(2).getOptionUrl()).transform(circleStrokeTransformation).into(picOption3);
            Picasso.with(getContext()).load(options.get(3).getOptionUrl()).transform(circleStrokeTransformation).into(picOption4);

        }


    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        toastShort(throwable.toString());
    }

    public static Fragment getInstance(Bundle bundle) {
        SecondaryQuestionFragment secondaryQuestionFragment = new SecondaryQuestionFragment();
        secondaryQuestionFragment.setArguments(bundle);

        return secondaryQuestionFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
