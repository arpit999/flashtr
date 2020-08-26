package com.flashtr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.activity.*;
import com.flashtr.adapter.TipsPagerAdapter;
import com.flashtr.util.Util;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by hirenkanani on 02/01/16.
 */
public class TipsActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @InjectView(R.id.viewPagerCountDots)
    LinearLayout pager_indicator;
    @InjectView(R.id.pager)
    ViewPager viewPager;
    @InjectView(R.id.mainRel)
    RelativeLayout mainParent;
    @InjectView(R.id.tvfinish)
    TextView tvFinish;
    @InjectView(R.id.rlFinish)
    MaterialRippleLayout rlFinish;
    int pagerposition = 0;
    private TipsPagerAdapter mAdapter;
    private ImageView[] dots;
    private int dotsCount;
    private boolean isFromSettings;

    private int[] listPhotos  = new int[]{
                R.drawable.tab1,
                R.drawable.tab2,
                R.drawable.tab3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tips);

        viewPager = (ViewPager) findViewById(R.id.pager);

        mainParent.setPadding(0, Util.getStatusBarHeight(this), 0, 0);

        if(getIntent().hasExtra("isFromSettings")){
            isFromSettings = getIntent().getBooleanExtra("isFromSettings",false);
        }


        mAdapter = new TipsPagerAdapter(this, listPhotos);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(this);
        setUiPageViewController();
        ButterKnife.inject(this);
    }


    @OnClick(R.id.rlFinish)
    public void finishTips(){
//        toast("Go to Home Screen");
        if(!isFromSettings) {
            final Intent in = new Intent(this, com.flashtr.activity.TabActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        }
        finish();
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }





    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
        if (position + 1 == dotsCount) {
            rlFinish.setVisibility(View.VISIBLE);
        } else {
            rlFinish.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if(isFromSettings){
            finish();
        }
    }
}
