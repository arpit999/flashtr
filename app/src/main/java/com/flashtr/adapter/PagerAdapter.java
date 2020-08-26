package com.flashtr.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.flashtr.activity.CaptureActivity;
import com.flashtr.activity.HomeActivity;
import com.flashtr.fragment.AllAlbumsFragment;
import com.flashtr.fragment.CameraFragment;
import com.flashtr.fragment.DemoFragment;
import com.flashtr.util.Constant;

public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    Context mContext;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Context ctx) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.mContext = mContext;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CaptureActivity cameraFragment = new CaptureActivity();
                Constant.map.put(0,cameraFragment);
                return cameraFragment;
            case 1:
//                AllAlbumsFragment cameraFragment2 = new AllAlbumsFragment();
                DemoFragment demoFrag = /*DemoFragment.newInstance(mContext);*/ new DemoFragment();
                Constant.map.put(1,demoFrag);
                return demoFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


    public int getItemPosition(Object object) {
        if(DemoFragment.getInstance()!=null){
            return 1;
        }else{
            return 0;
        }
    }
}
