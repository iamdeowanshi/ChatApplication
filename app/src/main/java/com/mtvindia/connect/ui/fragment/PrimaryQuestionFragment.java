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
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.QuestionRequestPresenter;
import com.mtvindia.connect.presenter.QuestionViewInteractor;
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
 * Created by Sibi on 21/10/15.
 */
public class PrimaryQuestionFragment extends BaseFragment implements QuestionViewInteractor {

    @Inject
    PreferenceUtil preferenceUtil;
    @Inject
    QuestionRequestPresenter presenter;

    @Bind(R.id.img_dp)
    ImageView imgDp;
    @Bind(R.id.img_dp_big_1)
    ImageView imgDpBig;
    @Bind(R.id.img_dp_big_2)
    ImageView imgDpBig2;
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

    private CircleStrokeTransformation circleStrokeTransformation;

    private User user;
    private List<Option> option;

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
        presenter.setViewInteractor(this);

        user = (User) preferenceUtil.read(PreferenceUtil.USER, User.class);

        presenter.primaryQuestionRequest();

        preferenceUtil.save(PreferenceUtil.QUESTIONS_ANSWERED, 0);

        int strokeColor = getContext().getResources().getColor(android.R.color.white);
        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), strokeColor, 1);

        txtHello.setText("Hello " + user.getFirstName() + "!");

        Picasso.with(getContext()).load(user.getProfilePic()).transform(circleStrokeTransformation).into(imgDp);
        Picasso.with(getContext()).load(R.drawable.img_dp_big).transform(circleStrokeTransformation).into(imgDpBig);
        Picasso.with(getContext()).load(R.drawable.img_dp_big).transform(circleStrokeTransformation).into(imgDpBig2);


    }

    @OnClick(R.id.img_dp_big_1)
    void option1() {
        preferenceUtil.save(PreferenceUtil.PRIMARY_OPTION_ID, option.get(1).getOptionId());
        optionSelected();
    }

    @OnClick(R.id.img_dp_big_2)
    void option2() {
        preferenceUtil.save(PreferenceUtil.PRIMARY_OPTION_ID, option.get(2).getOptionId());
        optionSelected();
    }

    void optionSelected() {

        preferenceUtil.save(PreferenceUtil.QUESTIONS_ANSWERED, 1);

        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = AnswerFragment.getInstance(null);
        navigationActivity.addFragment(fragment);
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
    public void display(Question question) {
        preferenceUtil.save(PreferenceUtil.PRIMARY_QUESTION_ID, question.getQuestionId());

        option = question.getOptions();

        txtPrimQuest.setText(question.getQuestion());
        txtOption1.setText(option.get(0).getOption());
        txtOption2.setText(option.get(1).getOption());

        Picasso.with(getContext()).load(option.get(0).getOptionUrl()).transform(circleStrokeTransformation).into(imgDpBig);
        Picasso.with(getContext()).load(option.get(1).getOptionUrl()).transform(circleStrokeTransformation).into(imgDpBig2);

    }

    @Override
    public void onError(Throwable throwable) {
        toastShort(String.valueOf(throwable));
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
