package com.mtvindia.connect.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseActivity;
import com.mtvindia.connect.ui.activity.LaunchActivity;
import com.mtvindia.connect.ui.adapter.WalkThroughAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Aaditya Deowanshi
 *
 *         On first launch of application, walk through screen displays how to use application.
 */
public class WalkThroughActivity extends BaseActivity implements WalkThroughAdapter.OnBoardClickListener {

    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.indicator) CirclePageIndicator indicator;

    private WalkThroughAdapter onBoardPagerAdapter;

    /**
     * Adding walk through images.
     */
    private List<Integer> slideResources = new ArrayList<Integer>() {{
        add(R.drawable.img_walkthrough_1);
        add(R.drawable.img_walkthrough_2);
        add(R.drawable.img_walkthrough_3);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_through);
        ButterKnife.bind(this);
        onBoardPagerAdapter = new WalkThroughAdapter(slideResources);
        onBoardPagerAdapter.setOnBoardClickListener(this);
        viewPager.setAdapter(onBoardPagerAdapter);
        indicator.setViewPager(viewPager);
    }

    @Override
    public void onCloseClicked(int position) {
        startActivityClearTop(LaunchActivity.class, null);
    }

    @Override
    public void onNextClicked(int position) {
        // if Last image
        if (position == slideResources.size() - 1) {
            startActivityClearTop(LaunchActivity.class, null);

            return;
        }

        viewPager.setCurrentItem(position + 1);
    }

}
