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

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.ResultResponse;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.custom.UbuntuButton;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.QuestionPreference;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author Aaditya Deowanshi
 *
 *         Result screen shows the details about selected answer and how many question are left to be answered.
 */

public class ResultFragment extends BaseFragment {

    @Inject QuestionPreference questionPreference;

    @Bind(R.id.circle1) View circle1;
    @Bind(R.id.circle2) View circle2;
    @Bind(R.id.circle3) View circle3;
    @Bind(R.id.circle4) View circle4;
    @Bind(R.id.circle5) View circle5;
    @Bind(R.id.circle6) View circle6;
    @Bind(R.id.circle7) View circle7;
    @Bind(R.id.circle8) View circle8;
    @Bind(R.id.circle9) View circle9;
    @Bind(R.id.circle10) View circle10;
    @Bind(R.id.img_dp_big_1) ImageView imgDpBig;
    @Bind(R.id.txt_left_questions) UbuntuTextView txtLeftQuestions;
    @Bind(R.id.txt_second_quest) UbuntuTextView txtQuestion;
    @Bind(R.id.txt_question) UbuntuTextView txtOption;
    @Bind(R.id.txt_other_people) UbuntuTextView txtOtherPeople;
    @Bind(R.id.btn_continue) UbuntuButton btnContinue;

    private static int count;
    private View[] progressBarView;
    private ResultResponse response;
    private NavigationActivity navigationActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.answer_fragment, container, false);
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

        response = questionPreference.readResultResponse();
        progressBarView = new View[]{circle1, circle2, circle3, circle4, circle5, circle6, circle7, circle8, circle9, circle10};

        count = questionPreference.readQuestionCount();
        updateProgressBar(count);
        loadAnswerDetails();
    }

    @OnClick(R.id.btn_continue)
    void setBtnContinue() {
        navigationActivity = (NavigationActivity) getContext();

        if (count < 10) {
            btnContinue.setText("Continue");
            loadSecondaryQuestionFragment();

            return;
        }

        if (count == 10) {
            btnContinue.setText("Let's Find Matches");
            Fragment fragment = ChooseFragment.getInstance(null);
            navigationActivity.addFragment(fragment);
        }
    }

    /**
     * Method to load details about answer.
     */
    private void loadAnswerDetails() {
        txtQuestion.setText(response.getQuestion());
        txtOption.setText(response.getOption().getOption());
        txtOtherPeople.setText(response.getMatchingUserCount() + " other people answered option " + questionPreference.readOptionSelected());
        Picasso.with(getContext()).load(response.getOption().getOptionUrl()).fit().into(imgDpBig);

        if (count < 10) {
            txtLeftQuestions.setText((10 - count) + " more to go");

            return;
        }

        txtLeftQuestions.setText("Finished");
    }

    /**
     * Updates the progress bar to show number of questions answered.
     * @param position
     */
    private void updateProgressBar(int position) {
        int i = 0;
        while (i < position) {
            progressBarView[i].setActivated(true);
            i++;
        }
    }

    /**
     * Loads next secondary question.
     */
    private void loadSecondaryQuestionFragment() {
        Bundle bundle = new Bundle();
        Fragment fragment = SecondaryQuestionFragment.getInstance(null);
        fragment.setArguments(bundle);
        navigationActivity.addFragment(fragment);
    }

    public static Fragment getInstance(Bundle bundle) {
        ResultFragment resultFragment = new ResultFragment();
        resultFragment.setArguments(bundle);

        return resultFragment;
    }

}
