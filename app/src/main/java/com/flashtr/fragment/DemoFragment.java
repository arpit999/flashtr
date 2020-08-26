package com.flashtr.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flashtr.R;
import com.flashtr.activity.TabActivity;
import com.flashtr.app.MyApp;
import com.flashtr.myInterface.OnBackApiResponse;
import com.flashtr.myInterface.OnBackPressed;
import com.flashtr.myInterface.OnClickEvent;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 24B on 5/3/2016.
 */
public class DemoFragment  extends Fragment implements OnBackApiResponse,OnClickEvent,OnBackPressed {
    Context ctx;

    /*static DemoFragment Demofragment;
    public static DemoFragment newInstance(Context ctx) {
        Demofragment = new DemoFragment();
        return Demofragment;
    }*/

    Bundle bundle;
    //    XML Components
    TextView txtTab1, txtTab2, txtTab3, txtTab4, txtTab5;
    LinearLayout lin_tab1,lin_tab2,lin_tab3,lin_tab4,lin_tab5;
    FrameLayout container;
    LinearLayout linTabLayoutMain;

    //    Fragment Management related Variables
    FragmentManager fragmentManager;
    public Fragment currentFragment;
    HashMap<String, Stack<Fragment>> mStacks;
    public String currentTab;

    Typeface type;

    /**
     * Api response and click event handle interface
     */
    public OnBackApiResponse backApiResponse;
    public OnClickEvent onClickEvent;
    public OnBackPressed onBackPressed;

    private static DemoFragment instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*View mainView = inflater.inflate(R.layout.fragment_new_album, null);
        ButterKnife.inject(getActivity());*/
        View view = inflater.inflate(R.layout.activity_home_tab, container, false);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        view.setPadding(0,Util.getStatusBarHeight(getActivity()),0,0);

        ButterKnife.inject(this, view);
        instance = this;


        return view;
    }

    public static DemoFragment getInstance() {
        return instance;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        setTabSelection(getView().findViewById(R.id.lin_tab3));
        currentTab = Constant.TAB_FRAGMENT_HOME;
        pushFragments(Constant.TAB_FRAGMENT_HOME, new HomeFragment(), false, false, true, true);
    }



    /*Initialize all required variables*/
    public void init() {

        //Initialize FragementManager
        fragmentManager = getFragmentManager();

        //		Init Tab stack
        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put(Constant.TAB_FRAGMENT_ALBUM, new Stack<Fragment>());
        mStacks.put(Constant.TAB_FRAGMENT_NEW_ALBUM, new Stack<Fragment>());
        mStacks.put(Constant.TAB_FRAGMENT_HOME, new Stack<Fragment>());
        mStacks.put(Constant.TAB_FRAGMENT_FAVS, new Stack<Fragment>());
        mStacks.put(Constant.TAB_FRAGMENT_SETS, new Stack<Fragment>());
        currentTab = Constant.TAB_FRAGMENT_SETS;

        container = (FrameLayout) getView().findViewById(R.id.container);
        linTabLayoutMain = (LinearLayout) getView().findViewById(R.id.linTabLayoutMain);

//            Initialize Tab variables
        txtTab1 = (TextView) getView().findViewById(R.id.txt_tab1);
        txtTab2 = (TextView) getView().findViewById(R.id.txt_tab2);
        txtTab3 = (TextView) getView().findViewById(R.id.txt_tab3);
        txtTab4 = (TextView) getView().findViewById(R.id.txt_tab4);
        txtTab5 = (TextView) getView().findViewById(R.id.txt_tab5);

//        Set tag to clicking view so we know that which layout has been click
        lin_tab1 = (LinearLayout) getView().findViewById(R.id.lin_tab1);
        lin_tab1.setTag(Constant.TAB_FRAGMENT_ALBUM);
        lin_tab2 = (LinearLayout) getView().findViewById(R.id.lin_tab2);
        lin_tab2.setTag(Constant.TAB_FRAGMENT_NEW_ALBUM);
        lin_tab3 = (LinearLayout) getView().findViewById(R.id.lin_tab3);
        lin_tab3.setTag(Constant.TAB_FRAGMENT_HOME);
        lin_tab4 = (LinearLayout) getView().findViewById(R.id.lin_tab4);
        lin_tab4.setTag(Constant.TAB_FRAGMENT_FAVS);
        lin_tab5 = (LinearLayout) getView().findViewById(R.id.lin_tab5);
        lin_tab5.setTag(Constant.TAB_FRAGMENT_SETS);

        type = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");
//            Set Font Type
        txtTab1.setTypeface(type);
        txtTab2.setTypeface(type);
        txtTab3.setTypeface(type);
        txtTab4.setTypeface(type);
        txtTab5.setTypeface(type);


    }


    /*Handle Bottom Tab Click Event*/
    public void tabClickEvent(View view) {
        //Utils.ButtonClickEffect(view);
        setTabSelection(view);

        currentTab = view.getTag().toString();

        JSONObject json = null;

        String title = "";
        String menuurl = "";
        Bundle bundle = new Bundle();
//        CommonWebviewFragment commonWebviewFragment = new CommonWebviewFragment();

        if (mStacks.get(currentTab).size() == 0) {
            switch (view.getId()) {
                case R.id.lin_tab1:
                    ((TabActivity)getActivity()).viewPager.setPagingEnabled(false);
                    currentTab = Constant.TAB_FRAGMENT_ALBUM;
                    pushFragments(Constant.TAB_FRAGMENT_ALBUM, new FragmentAlbumListing(), false, false, true, true);
                    break;
                case R.id.lin_tab2:
                    /*Intent intent = new Intent(HomeActivity.this, NewAlbumActivity.class);
                    intent.putExtra("activity", "HomeActivity");
                    startActivity(intent);*/
                    ((TabActivity)getActivity()).viewPager.setPagingEnabled(false);
                    FragNewAlbum fragmentNewAlbum = new FragNewAlbum();
                    bundle = new Bundle();
                    bundle.putString("activity","HomeActivity");
                    fragmentNewAlbum.setArguments(bundle);
                    currentTab = Constant.TAB_FRAGMENT_NEW_ALBUM;
                    pushFragments(Constant.TAB_FRAGMENT_NEW_ALBUM, fragmentNewAlbum, false, false, true, true);
                    break;
                case R.id.lin_tab3:
                    ((TabActivity)getActivity()).viewPager.setPagingEnabled(true);
                    String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
                    String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
//                    pushFragments(Constant.TAB_FRAGMENT_HOME, HomeFragment.newInstance(albumId), false, false, true, true);
                    currentTab = Constant.TAB_FRAGMENT_HOME;
                    pushFragments(Constant.TAB_FRAGMENT_HOME, new HomeFragment(), false, false, true, true);
                    break;
                case R.id.lin_tab4:
                    ((TabActivity)getActivity()).viewPager.setPagingEnabled(false);
                    currentTab = Constant.TAB_FRAGMENT_FAVS;
                    pushFragments(Constant.TAB_FRAGMENT_FAVS, new FavFragmentNew(), false, false, true, true);
                    break;
                case R.id.lin_tab5:
                    ((TabActivity)getActivity()).viewPager.setPagingEnabled(false);
                    currentTab = Constant.TAB_FRAGMENT_SETS;
                    pushFragments(Constant.TAB_FRAGMENT_SETS, new SettingsFragmnet(), false, false, true, true);
                    break;
            }

        } else {

            if(currentTab==Constant.TAB_FRAGMENT_HOME){
                ((TabActivity)getActivity()).viewPager.setPagingEnabled(true);
            }else{
                ((TabActivity)getActivity()).viewPager.setPagingEnabled(false);
            }
            Fragment fragment = mStacks.get(currentTab).lastElement();
            pushFragments(currentTab, fragment, false, false, false, false);
        }

    }

    @OnClick(R.id.lin_tab1)
    public void Tab1Click1(View v){
        tabClickEvent(v);
    }

    @OnClick(R.id.lin_tab2)
    public void Tab1Click2(View v){
        tabClickEvent(v);
    }

    @OnClick(R.id.lin_tab3)
    public void Tab1Click3(View v){
        tabClickEvent(v);
    }

    @OnClick(R.id.lin_tab4)
    public void Tab1Click4(View v){
        tabClickEvent(v);
    }

    @OnClick(R.id.lin_tab5)
    public void Tab1Click5(View v){
        tabClickEvent(v);
    }

    @Override
    public void clickEvent(View v) {
        switch (v.getId()) {
           /* case R.id.btnOkPopup:
                customDialog.hide();
                break;

            case R.id.imgMenu:
                slidingMenu.toggle(true);
                break;*/
            /*case R.id.listviewSlidingmenu:
                slidingMenu.toggle(true);
                break;*/
            case R.id.lin_tab1:
            case R.id.lin_tab2:
            case R.id.lin_tab3:
            case R.id.lin_tab4:
            case R.id.lin_tab5:
                onClickEvent.clickEvent(v);
                break;
            default:
                onClickEvent.clickEvent(v);
                break;
        }
    }

    public void setTabColor(int position){
        lin_tab1.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));
        lin_tab2.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));
        lin_tab3.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));
        lin_tab4.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));
        lin_tab5.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));
        switch(position){
            case 0:
                lin_tab1.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
            case 1:
                lin_tab2.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
            case 3:
                lin_tab3.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
            case 4:
                lin_tab4.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
            case 5:
                lin_tab5.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
        }
    }

    /*Set Tab Selection View*/
    public void setTabSelection(View view) {


        lin_tab1.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));
        lin_tab2.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));
        lin_tab3.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));
        lin_tab4.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));
        lin_tab5.setBackgroundColor(getResources().getColor(R.color.tabColorNormal));


        switch (view.getId()) {
            case R.id.lin_tab1:
                lin_tab1.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
            case R.id.lin_tab2:
                lin_tab2.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
            case R.id.lin_tab3:
                lin_tab3.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
            case R.id.lin_tab4:
                lin_tab4.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
            case R.id.lin_tab5:
                lin_tab5.setBackgroundColor(getResources().getColor(R.color.tabColorSelected));
                break;
        }
    }

    /*Replace Fragment into framelayout and maintain backstack
    * @param tag Tab name
    * @param fragment Fragment you need to add/replace
    * @param shouldAnimate animate fragment on trasition
    * @param isReverse animate fragment reverse mode on trasition
    * @param shouldAddBackStack true add to backstack,false not add to backstack
    * @param shouldAddStack Add to Tabstack*/
    public void pushFragments(String tag, Fragment fragment, boolean shouldAnimate, boolean isReverse, boolean shouldAddBackStack, boolean shouldAddStack) {
        try {
           /* if(fragment instanceof CommentFragment || fragment instanceof BlogCommentFragment
                    || fragment instanceof ChatFragment)
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            else
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);*/

            if(fragment instanceof HomeFragment){
                ((TabActivity)getActivity()).viewPager.setPagingEnabled(true);
            }else{
                ((TabActivity)getActivity()).viewPager.setPagingEnabled(false);
            }

            backApiResponse = (OnBackApiResponse) fragment;
            onClickEvent = (OnClickEvent) fragment;
            onBackPressed = (OnBackPressed) fragment;
            //			FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            currentFragment = fragment;

            if (shouldAnimate) {
                //				ft.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit, R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
                //				ft.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit);

				/*if (!isReverse) {
                    ft.setCustomAnimations(R.animator.slide_fragment_horizontal_right_in, R.animator.slide_fragment_horizontal_left_out, R.animator.slide_fragment_horizontal_left_in, R.animator.slide_fragment_horizontal_right_out);
				} else {
					ft.setCustomAnimations(R.animator.slide_fragment_horizontal_left_in, R.animator.slide_fragment_horizontal_right_out, R.animator.slide_fragment_horizontal_right_in, R.animator.slide_fragment_horizontal_left_out);
				}*/

                if (!isReverse) {
                    ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
//					ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                } else
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
            }
            Log.e("pushFragment", "Fragemnet class Name: " + fragment.getClass().getName());
            ft.replace(R.id.container, fragment, fragment.getClass().getName());

            if (shouldAddBackStack)
                ft.addToBackStack(fragment.getClass().getName());

            if (shouldAddStack)
                mStacks.get(tag).push(fragment);

            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Pop Last Fragment from stack*/
    public void popFragments() {
        /*
         * Select the second last fragment in current tab's stack.. which will
		 * be shown after the fragment transaction given below
		 */
        FragmentTransaction ft = null;
        try {
            Fragment fragment = mStacks.get(currentTab).elementAt(mStacks.get(currentTab).size() - 2);

			/* pop current fragment from stack.. */
            mStacks.get(currentTab).pop();

			/*
             * We have the target fragment in hand.. Just show it.. Show a standard
			 * navigation animation
			 */
            //FragmentManager manager = getSupportFragmentManager();
            ft = fragmentManager.beginTransaction();
            //ft.setCustomAnimations(R.animator.slide_fragment_horizontal_left_in, R.animator.slide_fragment_horizontal_right_out);
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

            onClickEvent = (OnClickEvent) fragment;
            backApiResponse = (OnBackApiResponse) fragment;
            onBackPressed = (OnBackPressed) fragment;
            currentFragment = fragment;
            ft.replace(R.id.container, fragment);

            ft.commit();

        } catch (Exception e) {
            e.printStackTrace();
            ft.commitAllowingStateLoss();
        }
    }

    /*Remove Current Fragment*/
    public void remvoveCurrentFragmentFromStack() {
        try {
                /* pop current fragment from stack.. */
            mStacks.get(currentTab).pop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Remove fragment by fragment object from back stack*/
    public void removeFragment(Fragment myFrag) {
        //		FragmentManager manager = getFragmentManager();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.remove(myFrag);
        ft.commit();
//		fragmentManager.popBackStack();
        fragmentManager.popBackStackImmediate();
    }

    /*Remove all fragments from back stack*/
    public void removeAllFragment() {

        int fragmentsCount = fragmentManager.getBackStackEntryCount();

        if (fragmentsCount > 0) {
            MyApp.disableFragmentAnimations = true;
            FragmentTransaction ft = fragmentManager.beginTransaction();
            //		manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.commit();

            MyApp.disableFragmentAnimations = false;
        }


        //		FragmentManager fragmentManager = getFragmentManager();

		/*int backStackCount=fragmentManager.getBackStackEntryCount();
        FragmentTransaction ft = fragmentManager.beginTransaction();
		for(int i=0;i<backStackCount;i++)
		{
			String fragmentTag = fragmentManager.getBackStackEntryAt(i).getName();
			Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
			ft.remove(currentFragment);
			fragmentManager.popBackStack();
		}
		ft.commit();*/
    }

    @Override
    public void onBackPressed() {
//        int fragmentsCount = getFragmentManager().getBackStackEntryCount();
//        onBackPressed.onBackPressed();
//        if (getFragmentManager().getBackStackEntryCount() == 0 && getVisibleFragment() instanceof HomeFragment) {
//            onBackPressed();
//        }
//        if(currentTab == Constant.TAB_FRAGMENT_HOME || currentTab == Constant.TAB_FRAGMENT_ALBUM || currentTab == Constant.TAB_FRAGMENT_NEW_ALBUM || currentTab == Constant.TAB_FRAGMENT_SETS || currentTab == Constant.TAB_FRAGMENT_FAVS){
//
//        }
        getActivity().finish();
//        switch (currentTab){
//            case Constant.TAB_FRAGMENT_HOME:
//                HomeFragment.getInstance().onBackPressed();
//                break;
//            case Constant.TAB_FRAGMENT_SETS:
//                break;
//            case Constant.TAB_FRAGMENT_FAVS:
//                break;
//            case Constant.TAB_FRAGMENT_ALBUM:
//                break;
//            case Constant.TAB_FRAGMENT_NEW_ALBUM:
//                break;
//        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) return fragment;
        }
        return null;
    }

    /*Hide and show tab layout*/
    public void showTabLayout(boolean isShow) {
        if (isShow) {
            linTabLayoutMain.setVisibility(View.VISIBLE);
        } else {
            linTabLayoutMain.setVisibility(View.GONE);
        }

    }


    @Override
    public void setBackApiResponse(String result, int ApiId) {

    }

    @Override
    public void setBackApiResponse(String result, int APIID, int pos) {

    }

    @Override
    public void setBackApiResponse(String result, int APIID, String userType) {

    }

    @Override
    public void setBackApiResponse(String result, int APIID, int pos, String userType) {

    }
}
