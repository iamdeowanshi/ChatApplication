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

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.custom.CircleStrokeTransformation;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sibi on 22/10/15.
 */
public class AnswerFragment extends BaseFragment {

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
    @Bind(R.id.btn_continue)
    Button btnContinue;
    @Bind(R.id.img_dp_big_1)
    ImageView imgDpBig;

    private CircleStrokeTransformation circleStrokeTransformation;
    private int strokeColor;
    private static int i = 1;
    private int option;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.answer_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        strokeColor = getContext().getResources().getColor(android.R.color.white);
        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), strokeColor, 1);

        Picasso.with(getContext()).load(R.drawable.img_dp_big).transform(circleStrokeTransformation).into(imgDpBig);
        circle1.setActivated(true);
        option = getArguments().getInt("value");
    }

    public static Fragment getInstance(Bundle bundle) {
        AnswerFragment answerFragment = new AnswerFragment();
        answerFragment.setArguments(bundle);

        return answerFragment;
    }

    @OnClick(R.id.btn_continue)
    void setBtnContinue() {
        i++;
        setProgress(i);

        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Bundle bundle = new Bundle();
        Fragment fragment = SecondaryQuestionFragment.getInstance(bundle);
        bundle.putInt("value", option);
        fragment.setArguments(bundle);
        navigationActivity.addFragment(fragment);
    }

    void setProgress(int position) {
        switch (position) {
            case 1:
                circle1.setActivated(true);
                break;
            case 2:
                circle2.setActivated(true);
                break;
            case 3:
                circle3.setActivated(true);
                break;
            case 4:
                circle4.setActivated(true);
                break;
            case 5:
                circle5.setActivated(true);
                break;
            case 6:
                circle6.setActivated(true);
                break;
            case 7:
                circle7.setActivated(true);
                break;
            case 8:
                circle8.setActivated(true);
                break;
            case 9:
                circle9.setActivated(true);
                break;
            case 10:
                circle10.setActivated(true);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
