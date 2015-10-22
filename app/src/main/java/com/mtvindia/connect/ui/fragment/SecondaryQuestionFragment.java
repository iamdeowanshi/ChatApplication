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
import android.widget.RelativeLayout;
import android.widget.TextView;

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
public class SecondaryQuestionFragment extends BaseFragment {


    @Bind(R.id.img_dp_big_1)
    ImageView imgDpBig;
    @Bind(R.id.img_dp_big_2)
    ImageView imgDpBig2;
    @Bind(R.id.layer1)
    RelativeLayout layer1;
    @Bind(R.id.img_dp_big3)
    ImageView imgDpBig3;
    @Bind(R.id.img_dp_big4)
    ImageView imgDpBig4;
    @Bind(R.id.layer2)
    RelativeLayout layer2;
    @Bind(R.id.linear_layout)
    LinearLayout linearLayout;
    @Bind(R.id.txt_first_high)
    TextView txtFirstHigh;
    @Bind(R.id.blank_view)
    View blankView;

    private CircleStrokeTransformation circleStrokeTransformation;
    private int strokeColor;
    private int options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.secondary_question_fragment, container, false);

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
        Picasso.with(getContext()).load(R.drawable.img_dp_big).transform(circleStrokeTransformation).into(imgDpBig2);

        options = getArguments().getInt("value");

        if (options == 2) {
            layer2.setVisibility(View.GONE);
            blankView.setVisibility(View.GONE);
            linearLayout.setGravity(Gravity.CENTER);
        }
    }

    @OnClick(R.id.img_dp_big_1)
    void option1() {
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        int option = 2;
        Bundle bundle = new Bundle();
        bundle.putInt("value", option);
        Fragment fragment = AnswerFragment.getInstance(bundle);
        fragment.setArguments(bundle);
        navigationActivity.addFragment(fragment);
    }

    @OnClick(R.id.img_dp_big_2)
    void option2() {
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        int option = 4;
        Bundle bundle = new Bundle();
        bundle.putInt("value", option);
        Fragment fragment = AnswerFragment.getInstance(bundle);
        fragment.setArguments(bundle);
        navigationActivity.addFragment(fragment);
    }

    @OnClick(R.id.img_dp_big3)
    void option3() {
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        int option = 2;
        Bundle bundle = new Bundle();
        bundle.putInt("value", option);
        Fragment fragment = AnswerFragment.getInstance(bundle);
        fragment.setArguments(bundle);
        navigationActivity.addFragment(fragment);
    }

    @OnClick(R.id.img_dp_big4)
    void option4() {
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = ChooseFragment.getInstance(null);
        navigationActivity.addFragment(fragment);
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
