package com.flashtr.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flashtr.NewAlbumActivity;
import com.flashtr.R;
import com.flashtr.app.MyApp;
import com.flashtr.fragment.FragNewAlbum;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.fragment.SettingsFragmnet;
import com.flashtr.myInterface.OnBackApiResponse;
import com.flashtr.myInterface.OnBackPressed;
import com.flashtr.myInterface.OnClickEvent;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class HomeActivity extends GlobalActivity {

    Bundle bundle;
    //    XML Components
    TextView txtTab1, txtTab2, txtTab3, txtTab4, txtTab5;
    LinearLayout lin_tab1,lin_tab2,lin_tab3,lin_tab4,lin_tab5;
    FrameLayout container;
    LinearLayout linTabLayoutMain;
    public LinearLayout laybgcolor;

    //    Fragment Management related Variables
    FragmentManager fragmentManager;
    public Fragment currentFragment;
    HashMap<String, Stack<Fragment>> mStacks;
    public String currentTab;
    RelativeLayout main;

    Typeface type;

    /**
     * Api response and click event handle interface
     */
    public OnBackApiResponse backApiResponse;
    public OnClickEvent onClickEvent;
    public OnBackPressed onBackPressed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_home_tab);
        main = (RelativeLayout) findViewById(R.id.main);

        main.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        init();

        setTabSelection(findViewById(R.id.lin_tab5));
        pushFragments(Constant.TAB_FRAGMENT_SETS, new SettingsFragmnet(), false, false, true, true);
    }

    /*Initialize all required variables*/
    public void init() {

        //Initialize FragementManager
        fragmentManager = getSupportFragmentManager();

        //		Init Tab stack
        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put(Constant.TAB_FRAGMENT_ALBUM, new Stack<Fragment>());
        mStacks.put(Constant.TAB_FRAGMENT_NEW_ALBUM, new Stack<Fragment>());
        mStacks.put(Constant.TAB_FRAGMENT_HOME, new Stack<Fragment>());
        mStacks.put(Constant.TAB_FRAGMENT_FAVS, new Stack<Fragment>());
        mStacks.put(Constant.TAB_FRAGMENT_SETS, new Stack<Fragment>());
        currentTab = Constant.TAB_FRAGMENT_SETS;

        container = (FrameLayout) findViewById(R.id.container);
        linTabLayoutMain = (LinearLayout) findViewById(R.id.linTabLayoutMain);
//        laybgcolor= (LinearLayout) findViewById(R.id.laybgcolor);

//            Initialize Tab variables
        txtTab1 = (TextView) findViewById(R.id.txt_tab1);
        txtTab2 = (TextView) findViewById(R.id.txt_tab2);
        txtTab3 = (TextView) findViewById(R.id.txt_tab3);
        txtTab4 = (TextView) findViewById(R.id.txt_tab4);
        txtTab5 = (TextView) findViewById(R.id.txt_tab5);

//        Set tag to clicking view so we know that which layout has been click
        lin_tab1 = (LinearLayout) findViewById(R.id.lin_tab1);
        lin_tab1.setTag(Constant.TAB_FRAGMENT_ALBUM);
        lin_tab2 = (LinearLayout) findViewById(R.id.lin_tab2);
        lin_tab2.setTag(Constant.TAB_FRAGMENT_NEW_ALBUM);
        lin_tab3 = (LinearLayout) findViewById(R.id.lin_tab3);
        lin_tab3.setTag(Constant.TAB_FRAGMENT_HOME);
        lin_tab4 = (LinearLayout) findViewById(R.id.lin_tab4);
        lin_tab4.setTag(Constant.TAB_FRAGMENT_FAVS);
        lin_tab5 = (LinearLayout) findViewById(R.id.lin_tab5);
        lin_tab5.setTag(Constant.TAB_FRAGMENT_SETS);

        type = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
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

                    break;
                case R.id.lin_tab2:
                    /*Intent intent = new Intent(HomeActivity.this, NewAlbumActivity.class);
                    intent.putExtra("activity", "HomeActivity");
                    startActivity(intent);*/
                    FragNewAlbum fragmentNewAlbum = new FragNewAlbum();
                    bundle = new Bundle();
                    bundle.putString("activity","HomeActivity");
                    fragmentNewAlbum.setArguments(bundle);
                    pushFragments(Constant.TAB_FRAGMENT_NEW_ALBUM, fragmentNewAlbum, false, false, true, true);
                    break;
                case R.id.lin_tab3:
                    String albumName = "" + Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_Current_AlbumName);
                    String albumId = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_Current_AlbumID);
                    pushFragments(Constant.TAB_FRAGMENT_HOME, HomeFragment.newInstance(albumId), false, false, true, true);
                    break;
                case R.id.lin_tab4:
                    break;
                case R.id.lin_tab5:
                    pushFragments(Constant.TAB_FRAGMENT_SETS, new SettingsFragmnet(), false, false, true, true);
                    break;
            }

        } else {

            Fragment fragment = mStacks.get(currentTab).lastElement();
            pushFragments(currentTab, fragment, false, false, false, false);
        }

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
            default:
                onClickEvent.clickEvent(v);
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

        if (getSupportFragmentManager().getBackStackEntryCount() == 0 && getVisibleFragment() instanceof HomeFragment) {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }

    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
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

}
