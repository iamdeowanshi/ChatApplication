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
public class ChooseFragment extends BaseFragment {

    @Bind(R.id.img_1)
    ImageView img1;
    @Bind(R.id.img_2)
    ImageView img2;
    @Bind(R.id.btn_skip)
    Button btnSkip;

    private int strokeColor;
    private CircleStrokeTransformation circleStrokeTransformation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        strokeColor = getContext().getResources().getColor(android.R.color.white);
        circleStrokeTransformation = new CircleStrokeTransformation(getContext(), strokeColor, 1);

        Picasso.with(getContext()).load(R.drawable.img_dp_big).transform(circleStrokeTransformation).into(img1);
        Picasso.with(getContext()).load(R.drawable.img_dp_big).transform(circleStrokeTransformation).into(img2);
    }

    @OnClick(R.id.img_1)
    void clickedFirst() {
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = ChatFragment.getInstance(null);
        navigationActivity.addFragment(fragment);
        toastShort("clicked first option");
    }

    @OnClick(R.id.img_2)
    void clickedSecond() {
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = ChatFragment.getInstance(null);
        navigationActivity.addFragment(fragment);
        toastShort("clicked second option");
    }

    @OnClick(R.id.btn_skip)
    void clickedSkip() {
        NavigationActivity navigationActivity = (NavigationActivity) getContext();
        Fragment fragment = PrimaryQuestionFragment.getInstance(null);
        navigationActivity.addFragment(fragment);
    }

    public static Fragment getInstance(Bundle bundle) {
        ChooseFragment chooseFragment = new ChooseFragment();
        chooseFragment.setArguments(bundle);

        return chooseFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
