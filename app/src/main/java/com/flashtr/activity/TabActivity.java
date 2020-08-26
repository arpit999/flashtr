package com.flashtr.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flashtr.R;
import com.flashtr.adapter.PagerAdapter;
import com.flashtr.fragment.CameraFragment;
import com.flashtr.fragment.DemoFragment;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.myInterface.OnClickEvent;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;

import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * Created by 24B on 5/3/2016.
 */
public class TabActivity extends GlobalActivity {
    public CustomViewPager viewPager;
    public PagerAdapter adapter;
    RelativeLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
*/
        setContentView(R.layout.tablayout_act);

        ButterKnife.inject(this);

        viewPager = (CustomViewPager) findViewById(R.id.pager);

        if (checkPrmissions()) {
            adapter = new PagerAdapter(getSupportFragmentManager(), 2, TabActivity.this);
            viewPager.setAdapter(adapter);


            if (getIntent().hasExtra("pos") && getIntent().getIntExtra("pos", 0) == 1) {
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }
            viewPager.setOffscreenPageLimit(1);

        } else {
            Toast.makeText(TabActivity.this, "Permission is not given, Please allow it from setting menu.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        if (checkPrmissions()) {
            if (viewPager.getCurrentItem() == 0) {
                CaptureActivity.getInstance().onBackPressed();
            } else {
                DemoFragment.getInstance().onBackPressed();
            }
        } else {
            finish();
        }
//        super.onBackPressed();
    }

    public void shareApp() {
        String shareBody = "Hey I'm using Flashtr\nDownload App: https://play.google.com/store/apps/developer?id=" + getPackageName();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Constant.AppName);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        if (sharingIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(sharingIntent, "Share"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPrmissions()) {
            viewPager.setAdapter(null);
            Toast.makeText(TabActivity.this, "Permission is not given, Please allow it from setting menu.", Toast.LENGTH_LONG).show();
        }

    }

    public boolean checkPrmissions() {
        if (Util.checkPermission("android:camera", TabActivity.this) && Util.checkPermission("android:read_contacts", TabActivity.this) && Util.checkPermission("android:read_external_storage", TabActivity.this) && Util.checkPermission("android:write_external_storage", TabActivity.this)) {
            return true;
        } else {
            return false;
        }
    }
}
