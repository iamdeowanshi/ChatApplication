package com.mtvindia.connect.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.ResultResponse;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.custom.CircleStrokeTransformation;
import com.mtvindia.connect.ui.custom.UbuntuButton;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.PreferenceUtil;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sibi on 22/10/15.
 */
public class ResultFragment extends BaseFragment {

    @Inject
    PreferenceUtil preferenceUtil;

    @Bind(R.id.circle1)
    View circle1;
    @Bind(R.id.circle2)
    View circle2;
    @Bind(R.id.circle3)
    View circle3;
    @Bind(R.id.circle4)
    View circle4;
    @Bind(R.id.circle5)
    View circle5;
    @Bind(R.id.circle6)
    View circle6;
    @Bind(R.id.circle7)
    View circle7;
    @Bind(R.id.circle8)
    View circle8;
    @Bind(R.id.circle9)
    View circle9;
    @Bind(R.id.circle10)
    View circle10;
    @Bind(R.id.img_dp_big_1)
    ImageView imgDpBig;
    @Bind(R.id.txt_left_questions)
    UbuntuTextView txtLeftQuestions;
    @Bind(R.id.txt_second_quest)
    UbuntuTextView txtQuestion;
    @Bind(R.id.txt_question)
    UbuntuTextView txtOption;
    @Bind(R.id.txt_other_people)
    UbuntuTextView txtOtherPeople;
    @Bind(R.id.btn_continue)
    UbuntuButton btnContinue;

    private CircleStrokeTransformation circleStrokeTransformation;
    private int strokeColor;
    private static int count;
    private View[] progressBarView;
    private ResultResponse response;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.answer_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        response = (ResultResponse) preferenceUtil.read(PreferenceUtil.RESULT_RESPONSE, ResultResponse.class);
        progressBarView = new View[]{circle1, circle2, circle3, circle4, circle5, circle6, circle7, circle8, circle9, circle10};

        count = preferenceUtil.readInt(PreferenceUtil.QUESTIONS_ANSWERED, 0);
        setProgress(count);

        strokeColor = getContext().getResources().getColor(android.R.color.white);
        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), strokeColor, 1);

        txtQuestion.setText(response.getQuestion());
        txtOption.setText(response.getOption().getOption());
        txtOtherPeople.setText(response.getMatchingUserCount() + " other people answered option " + response.getOption().getOptionId());
        Picasso.with(getContext()).load(response.getOption().getOptionUrl()).transform(circleStrokeTransformation).into(imgDpBig);
        if (count < 10) {
            txtLeftQuestions.setText((10 - count) + " more to go");
        } else {
            txtLeftQuestions.setText("Finished");
        }
    }

    public static Fragment getInstance(Bundle bundle) {
        ResultFragment resultFragment = new ResultFragment();
        resultFragment.setArguments(bundle);

        return resultFragment;
    }

    @OnClick(R.id.btn_continue)
    void setBtnContinue() {
        NavigationActivity navigationActivity = (NavigationActivity) getContext();

        if (count < 10) {
            btnContinue.setText("Continue");
            Bundle bundle = new Bundle();
            Fragment fragment = SecondaryQuestionFragment.getInstance(null);
            fragment.setArguments(bundle);
            navigationActivity.addFragment(fragment);
        } else if (count == 10) {
            btnContinue.setText("Let's Find Matches");
            Fragment fragment = ChooseFragment.getInstance(null);
            navigationActivity.addFragment(fragment);
        }
    }

    void setProgress(int position) {
        int i = 0;

        while (i < position) {
            progressBarView[i].setActivated(true);
            i++;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}