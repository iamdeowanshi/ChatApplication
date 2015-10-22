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
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.ui.custom.CircleStrokeTransformation;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sibi on 21/10/15.
 */
public class PrimaryQuestionFragment extends BaseFragment {

    @Bind(R.id.img_dp)
    ImageView imgDp;
    @Bind(R.id.img_dp_big_1)
    ImageView imgDpBig;
    @Bind(R.id.img_dp_big_2)
    ImageView imgDpBig2;

    private CircleStrokeTransformation circleStrokeTransformationDp;
    private CircleStrokeTransformation circleStrokeTransformation;

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

        int strokeColor = getContext().getResources().getColor(android.R.color.white);
        circleStrokeTransformationDp = new CircleStrokeTransformation(getContext(), strokeColor, 3);
        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), strokeColor, 1);

        Picasso.with(getContext()).load(R.drawable.img_dp).transform(circleStrokeTransformationDp).into(imgDp);
        Picasso.with(getContext()).load(R.drawable.img_dp_big).transform(circleStrokeTransformation).into(imgDpBig);
        Picasso.with(getContext()).load(R.drawable.img_dp_big).transform(circleStrokeTransformation).into(imgDpBig2);

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

    public static Fragment getInstance(Bundle bundle) {
        PrimaryQuestionFragment primaryQuestionFragment = new PrimaryQuestionFragment();
        primaryQuestionFragment.setArguments(bundle);

        return primaryQuestionFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
