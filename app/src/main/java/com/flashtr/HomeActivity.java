package com.flashtr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.adapter.FragmentAdapter;
import com.flashtr.fragment.AllAlbumsFragment;
import com.flashtr.fragment.CameraFragment;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.fragment.SettingsFragmnet;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HomeActivity extends FragmentActivity implements OnItemClickListener {

    public static DrawerLayout mDrawerLayout;
    public static ListView mDrawerList;
    public static LinearLayout rlHeader;
    public static int gridSelected = 0;
    public static int selectedPosition = 0;
    //public static ArrayList<HashMap<String, String>> listRecentPhotos = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> listSharedPhotos = new ArrayList<HashMap<String, String>>();
    public static boolean flag = false;
    public static ViewPager pager;
    public static FragmentManager fragmentManager;
    @InjectView(R.id.rlMenu)
    MaterialRippleLayout rlMenu;
    @InjectView(R.id.rlMain)
    RelativeLayout rlMain;
    @InjectView(R.id.rlLogo)
    MaterialRippleLayout rlLogo;
    @InjectView(R.id.rlAlbumHome)
    MaterialRippleLayout rlAlbumHome;
    @InjectView(R.id.rlGrid)
    RelativeLayout rlGrid;
    public static ImageButton imgGrid;
    @InjectView(R.id.tvAlbumName)
    TextView tvAlbumName;
    @InjectView(R.id.back)
    ImageView backButton;
    @InjectView(R.id.mlEditButton)
    MaterialRippleLayout mlEditButton;

    @InjectView(R.id.tvUserName)
    TextView userName;
    @InjectView(R.id.imgEditProfile)
    ImageView profilePic;
    @InjectView(R.id.ivEditButton)
    ImageView ivEditButton;
    @InjectView(R.id.rlTopLogo)
    RelativeLayout rlTopLogo;
    FragmentTransaction fragmentTransaction;
    Fragment fragment;
    ArrayList<HashMap<String, String>> listAlbums = new ArrayList<HashMap<String, String>>();
    FragmentAdapter pageAdapter;
    int pagerPosition = 0;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private ArrayList<String> listMenu = new ArrayList<>();
    private ArrayList<Integer> listIcons = new ArrayList<>();
    private ArrayList<android.support.v4.app.Fragment> fragmentList;
    private MenuListAdapter menuListAdapter;
    private Drawable backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_home);
        rlMain.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        imgGrid = (ImageButton)findViewById(R.id.imgGrid);
        ButterKnife.inject(this);

        options = new DisplayImageOptions.Builder()
//                .displayer(new RoundedBitmapDisplayer(300))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));

        init();
        setMenuList();
        setUserData();

        if (Util.isOnline(this)) {
            new getAlbums().execute();
        } else {
            loadOfflineAlbums();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = false;

        final String result = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_Albums);
        if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_RELOAD_Home).equals("1")) {
            if (Util.isOnline(getApplicationContext()) && TextUtils.isEmpty(result)) {
                new getAlbums().execute();
            } else
                loadOfflineAlbums();
        }
        if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_RELOAD_Home).equals("2")) {
            if (Util.isOnline(getApplicationContext()) && TextUtils.isEmpty(result)) {
                new getAlbums().execute();
            } else
                loadOfflineAlbums();
        }
        Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_RELOAD_Home, "0");
        if(fragment instanceof HomeFragment) {
            if ((HomeFragment.mRecyclerView!=null&&HomeFragment.mRecyclerView.getVisibility() == View.VISIBLE) || (HomeFragment.lvRecentPhotos1!=null&&HomeFragment.lvRecentPhotos1.getVisibility() == View.VISIBLE)) {
                gridSelected = 0;
//                imgGrid.setBackgroundResource(R.drawable.icon_grid);
            } else if ((HomeFragment.gridRecentPhotos!=null&&HomeFragment.gridRecentPhotos.getVisibility() == View.VISIBLE) || (HomeFragment.gridSharedPhotos!=null&&HomeFragment.gridSharedPhotos.getVisibility() == View.VISIBLE)) {
                gridSelected = 1;
                imgGrid.setBackgroundResource(R.drawable.ic_listview);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.mlEditButton)
    public void editProfile(View view) {
        displayView(7);
    }

    @OnClick(R.id.rlLogo)
    @SuppressWarnings("unused")
    public void Camera(View view) {

        final String title = tvAlbumName.getText().toString().trim();
        if (title.equalsIgnoreCase("All Albums")) {
            onBackPressed();
        } else if (title.equalsIgnoreCase("Profile")) {
            onBackPressed();
        } else if (title.equalsIgnoreCase("Select Album")) {
            onBackPressed();
        } else if (title.equalsIgnoreCase("Settings")) {
            onBackPressed();
        } else {
            displayView(8);
        }
    }

    @OnClick(R.id.rlAdd)
    public void showImageSelecion(View view){

    }

    @OnClick(R.id.rlGrid)
    public void Grid(View view) {
        if (gridSelected == 0) {
            gridSelected = 1;
            imgGrid.setBackgroundResource(R.drawable.ic_listview);
        } else {
            gridSelected = 0;
//            imgGrid.setBackgroundResource(R.drawable.icon_grid);
        }
        Intent intent1 = new Intent();
        intent1.setAction(ACTIVITY_SERVICE);
        intent1.putExtra("type", 1);
        sendBroadcast(intent1);
    }

    @OnClick(R.id.rlMenu)
    public void Menu(View view) {
        hideKeyboard();

        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawers();
        } else {
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    @OnClick(R.id.rlAlbumHome)
    public void AlbumDetails(View view) {
        if (selectedPosition == 0 || selectedPosition == 4 || selectedPosition == 5 || selectedPosition == 1 || selectedPosition == 6) {
            for (int i = 0; i < listAlbums.size(); i++) {
                Log.e("DATA" + 1, "" + listAlbums.get(i));
                if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Current_AlbumID).equals(listAlbums.get(i).get("album_id"))) {
                    Intent intent = new Intent(HomeActivity.this, AlbumDetailsActivity.class);
                    intent.putExtra("map", listAlbums.get(i));
                    intent.putExtra("activity", "HomeActivity");
                    startActivity(intent);
                }
            }
        }
    }


//    @OnClick(R.id.flCameraView)
//    public void flCameraView(View view){
//        if (CameraFragment.getInstance() != null) {
//            CameraFragment.getInstance().getmCameraPreview().startCamera();
//            CameraFragment.getInstance().getmCameraPreview().startPreview();
//        }
//        flCameraView.setVisibility(View.GONE);
//        rlHeader.setVisibility(View.GONE);
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//
//    }

    private void init() {

        flag = false;
        selectedPosition = 0;
        backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        backArrow.setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        backButton.setBackground(backArrow);

        fragmentManager = getFragmentManager();
        pager = (ViewPager) findViewById(R.id.viewpager);
        rlHeader = (LinearLayout) findViewById(R.id.rlHeader);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
        tvAlbumName.setTypeface(typeface);
//        ViewGroup root = (ViewGroup) findViewById(R.id.rlMain);
//        Util.setFont(root, Util.getFont(1,getApplicationContext()));
//        backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        backArrow.setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP);


        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pagerPosition = position;
                switch (position) {
                    case 0:
                        Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PAGER_POSITION, "0");
                        if (CameraFragment.getInstance() != null) {
                            CameraFragment.getInstance().setLayoutParams(false);
                            CameraFragment.getInstance().getmCameraPreview().startCamera();
                            CameraFragment.getInstance().setLayoutParams(false);
                            rlGrid.setVisibility(View.GONE);

                        }
                        break;
                    case 1:
                        Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PAGER_POSITION, "1");
                        if (CameraFragment.getInstance() != null) {
                            CameraFragment.getInstance().getmCameraPreview().stopPreview();
                            CameraFragment.getInstance().getmCameraPreview().destroyCamera();
                            rlGrid.setVisibility(View.VISIBLE);
                        }
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (CameraFragment.getInstance() != null) {
                    CameraFragment.getInstance().setLayoutParams(false);
                }
                int photoCnt = Util.getOfflinePic(getApplicationContext());
                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
            }
        });

    }

    private ArrayList<android.support.v4.app.Fragment> getFragments() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        if (fragment instanceof HomeFragment) {
            fragmentList.add(CameraFragment.newInstance());
        }
        fragmentList.add(fragment);
        return fragmentList;
    }

    private void reload(String result) {

        boolean flag = true;
        listAlbums.clear();
        try {
            JSONObject jObj = new JSONObject(result);
            int status = jObj.optInt("success");

            if (status == 1) {
                Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_Albums, "" + result);
                JSONArray jsonArray = jObj.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("album_id", "" + jsonObject.optString("album_id"));
                    hashMap.put("album_name", "" + jsonObject.optString("album_name"));
                    hashMap.put("album_cover_image", "" + jsonObject.optString("album_cover_image"));
                    hashMap.put("shared_with_me", "" + jsonObject.optString("shared_with_me"));
                    hashMap.put("created_by_me", "" + jsonObject.optString("created_by_me"));
                    hashMap.put("admin_id", "" + jsonObject.optString("admin_id"));
                    hashMap.put("admin_dp", "" + jsonObject.optString("admin_dp"));
                    hashMap.put("admin_name", "" + jsonObject.optString("admin_name"));
                    hashMap.put("admin_mobileno", "" + jsonObject.optString("admin_mobileno"));
                    hashMap.put("member_details", "" + jsonObject.optString("member_details"));
                    listAlbums.add(hashMap);

                    if (Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_Current_AlbumID).equals("" + hashMap.get("album_id"))) {
                        flag = false;
                        Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_Current_AlbumID, "" + hashMap.get("album_id"));
                        Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_Current_AlbumName, "" + hashMap.get("album_name"));
                    }

                }
                displayView(selectedPosition);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (flag) {
            if (listAlbums.size() > 0) {
                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Current_AlbumID, "" + listAlbums.get(0).get("album_id"));
                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Current_AlbumName, "" + listAlbums.get(0).get("album_name"));
            } else {
                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Current_AlbumID, "");
                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Current_AlbumName, "");
            }
        }

        String albumName = "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Current_AlbumName);
        if (albumName.length() > 0)
            tvAlbumName.setText("" + albumName);
        else tvAlbumName.setText("No Album");

        displayView(selectedPosition);
    }


    private void setMenuList() {
        listMenu.clear();
        listIcons.clear();

        listMenu.add("Home");
        listIcons.add(R.drawable.ic_home);

        listMenu.add("New Album");
        listIcons.add(R.drawable.ic_new_album);

        listMenu.add("All Album");
//        listIcons.add(R.drawable.ic_view_all_album);

        listMenu.add("Settings");
//        listIcons.add(R.drawable.ic_setting);

        listMenu.add("Tips");
//        listIcons.add(R.drawable.ic_tips);

        listMenu.add("Logout");
        listIcons.add(R.drawable.logout);

        listMenu.trimToSize();
        listIcons.trimToSize();


        menuListAdapter = new MenuListAdapter(getApplicationContext(), listMenu, selectedPosition);
        mDrawerList.setDivider(null);
        mDrawerList.setAdapter(menuListAdapter);
        mDrawerList.setOnItemClickListener(this);
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        selectedPosition = position;
        Intent intent;
        flag = false;
        switch (position) {
            case 0:
//                Toast.makeText(HomeActivity.this, ""+position, Toast.LENGTH_SHORT).show();
//                backButton.setBackgroundResource(R.drawable.logo);
                rlTopLogo.setBackgroundColor(getResources().getColor(R.color.yellow));
                backButton.setVisibility(View.GONE);
                rlLogo.setBackgroundColor(getResources().getColor(R.color.transparent));
                rlAlbumHome.setVisibility(View.VISIBLE);
                rlGrid.setVisibility(View.VISIBLE);
                String albumName = "" + Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_Current_AlbumName);
                if (albumName.length() > 0)
                    tvAlbumName.setText("" + albumName);
//                else tvAlbumName.setText("No Album");
                tvAlbumName.setTextColor(getResources().getColor(R.color.text_color));
                String albumId = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_Current_AlbumID);
                fragment = HomeFragment.newInstance(albumId);
                break;
            case 1:
//                Toast.makeText(HomeActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                rlTopLogo.setBackgroundColor(getResources().getColor(R.color.yellow));
                backButton.setVisibility(View.GONE);
                intent = new Intent(HomeActivity.this, NewAlbumActivity.class);
                intent.putExtra("activity", "HomeActivity");
                startActivity(intent);
//                startActivity(NewAlbumActivity.class);
                rlLogo.setBackgroundColor(getResources().getColor(R.color.transparent));
                mDrawerLayout.closeDrawers();
                break;
            case 2:
//                Toast.makeText(HomeActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                rlTopLogo.setBackgroundColor(getResources().getColor(R.color.background_color));
                rlLogo.setBackgroundColor(getResources().getColor(R.color.transparent));
                backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
                backArrow.setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.SRC_ATOP);
                backButton.setBackground(backArrow);
                backButton.setVisibility(View.VISIBLE);
                rlGrid.setVisibility(View.GONE);
                rlAlbumHome.setVisibility(View.VISIBLE);
                tvAlbumName.setText("All Albums");
                tvAlbumName.setTextColor(getResources().getColor(R.color.text_color));
                fragment = AllAlbumsFragment.newInstance();
                break;
            case 3:
                rlTopLogo.setBackgroundColor(getResources().getColor(R.color.background_color));
                rlLogo.setBackgroundColor(getResources().getColor(R.color.transparent));
                backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
                backArrow.setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.SRC_ATOP);
                backButton.setBackground(backArrow);
                backButton.setVisibility(View.VISIBLE);
                rlGrid.setVisibility(View.GONE);
                tvAlbumName.setText("Settings");
                tvAlbumName.setTextColor(getResources().getColor(R.color.text_color));
                fragment = SettingsFragmnet.newInstance();
                break;
            case 4:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    flag = false;
                    intent = new Intent(HomeActivity.this, TipsActivity.class);
                    startActivity(intent);
                    finish();
                    mDrawerLayout.closeDrawers();
                }
                break;
            case 5:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    flag = false;
                    mDrawerLayout.closeDrawers();
                    AlertDialog.Builder alert1 = new AlertDialog.Builder(HomeActivity.this);
                    alert1.setTitle("" + Constant.AppName);
                    alert1.setMessage("Are you sure do you want to logout ?");
                    alert1.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @SuppressLint("InlinedApi")
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_IS_LOGGEDIN, "0");
                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_Albums, "");
                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_Current_AlbumID, "");
                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_Current_AlbumName, "");
                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_GET_PHOTOS, "");

                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_USERID, "");
                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_USERNAME, "");
                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_USERIMAGE, "");
                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_MOBILE, "");
                                    Util.WriteSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_EMAIL, "");

                                    Intent i = new Intent(HomeActivity.this,
                                            LoginActivity.class);
                                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    } else {
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    }
                                    startActivity(i);
                                }
                            });
                    alert1.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                    dialog.cancel();
                                }
                            });
                    alert1.create();
                    alert1.show();
                }


                break;
            case 6:
//                Toast.makeText(HomeActivity.this, ""+position, Toast.LENGTH_SHORT).show();

                break;
            case 7:
                rlTopLogo.setBackgroundColor(getResources().getColor(R.color.background_color));
                rlLogo.setBackgroundColor(getResources().getColor(R.color.transparent));
                backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
                backArrow.setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.SRC_ATOP);
                backButton.setBackground(backArrow);
                backButton.setVisibility(View.VISIBLE);
                rlGrid.setVisibility(View.GONE);
                rlAlbumHome.setVisibility(View.VISIBLE);
                tvAlbumName.setText("Profile");
                tvAlbumName.setTextColor(getResources().getColor(R.color.text_color));
                intent = new Intent(HomeActivity.this, RegisterActivity.class);
                intent.putExtra("activity", "HomeActivity");

                startActivity(intent);
//                fragment = EditProfileFragment.newInstance();
                break;

            case 8:
                selectedPosition = 0;
                intent = new Intent(HomeActivity.this, SelectAlbumActivity.class);
                startActivity(intent);
                break;
            default:
                fragment = null;
                break;
        }

        if (fragment != null) {
            fragmentList = getFragments();
            fragmentList.trimToSize();
            pageAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
            pager.setAdapter(pageAdapter);
            if (fragment instanceof HomeFragment) {
                if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PAGER_POSITION).equals("0")) {
                    rlGrid.setVisibility(View.GONE);
                    pagerPosition = 0;
                    pager.setCurrentItem(pagerPosition);
                } else {
                    rlGrid.setVisibility(View.VISIBLE);
                    pagerPosition = 1;
                    pager.setCurrentItem(pagerPosition);
                }
            } else {
                pagerPosition = 1;
                pager.setCurrentItem(pagerPosition);
            }


            if (getIntent() != null) {
                try {
                    Log.e("INT", "" + getIntent().getIntExtra(Constant.SHRED_PR.KEY_IS_USER_ADDED, 0));
                    final int isAddedUser = getIntent().getIntExtra(Constant.SHRED_PR.KEY_IS_USER_ADDED, 0);
                    if (isAddedUser == 1) {
                        pager.setCurrentItem(1);
                    } else {
                        if (fragment instanceof HomeFragment) {
                            if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PAGER_POSITION).equals("0")) {
                                rlGrid.setVisibility(View.GONE);
                                pagerPosition = 0;
                                pager.setCurrentItem(pagerPosition);
                            } else {
                                rlGrid.setVisibility(View.VISIBLE);
                                pagerPosition = 1;
                                pager.setCurrentItem(pagerPosition);
                            }
                        } else {
                            pagerPosition = 1;
                            pager.setCurrentItem(pagerPosition);
                        }
                    }
//                    else{
//                        int pagerPosi = Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PAGER_POSITION));
//                        pager.setCurrentItem(pagerPosi);
//                    }
                    //go on as normal
                } catch (NumberFormatException e) {
                    //handle error

                }

            }

            mDrawerLayout.closeDrawers();
        }
    }

    protected void startActivity(Class klass) {
        startActivity(new Intent(this, klass));
    }

    protected void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            flag = false;
            mDrawerLayout.closeDrawers();
        } else {
            final String title = tvAlbumName.getText().toString().trim();
            if (title.equalsIgnoreCase("All Albums")) {
                displayView(0);
            } else if (title.equalsIgnoreCase("Profile")) {
                displayView(0);
            } else if (title.equalsIgnoreCase("Select Album")) {
                displayView(0);
            } else if (title.equalsIgnoreCase("Settings")) {
                displayView(0);
            } else {
                if (flag) {
                    finish();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.back_press), Toast.LENGTH_SHORT).show();
                }
                flag = true;
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedPosition = position;
        displayView(selectedPosition);
    }

    public void setUserData() {
        if (!Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERIMAGE).equalsIgnoreCase("")) {
            imageLoader.displayImage("" + Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERIMAGE), profilePic, options);
        }

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "SEGOEUI.TTF");
        userName.setTypeface(font);
        userName.setText(Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERNAME));

    }

    private void loadOfflineAlbums() {
        final String result = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_Albums);
        if (!TextUtils.isEmpty(result)) {
            reload(result);
        }
    }


    static class ViewHolder {
        @InjectView(R.id.tv_title)
        TextView tvTitle;
        @InjectView(R.id.iv_icon)
        ImageView ivIcon;
        @InjectView(R.id.rlHome)
        RelativeLayout rlHome;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    class getAlbums extends AsyncTask<Void, String, String> {

        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            CustomDialog.getInstance().show(HomeActivity.this,"");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "getalbum");
                jData.put("userid", "" + Util.ReadSharePrefrence(HomeActivity.this, Constant.SHRED_PR.KEY_USERID));

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("data", jData.toString()));
                response = Util.makeServiceCall(Constant.URL, 1, params1);
                Log.e("params1", ">>" + params1);

                Log.e("** response is:- ", ">>" + response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//            CustomDialog.getInstance().hide();

            if (!TextUtils.isEmpty(result)) {
                reload(result);
            }


        }
    }

    public class MenuListAdapter extends BaseAdapter {

        private ArrayList<String> locallist;
        private int selectedPosition;
        private Context mContext;
        private LayoutInflater inflater = null;

        public MenuListAdapter(Context mContext, ArrayList<String> locallist, int selectedPosition) {
            this.mContext = mContext;
            this.locallist = locallist;
            this.selectedPosition = selectedPosition;
            inflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return locallist.size();
        }

        @Override
        public Object getItem(int i) {
            return locallist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {

            final ViewHolder holder;

            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = inflater.inflate(R.layout.menulist_row, viewGroup, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            if (position == 1) {
                convertView.setBackgroundResource(R.color.blue);
                holder.tvTitle.setTextColor(getResources().getColor(R.color.white));
            }

            holder.tvTitle.setText(locallist.get(position));
            Typeface font = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
            holder.tvTitle.setTypeface(font);
            holder.ivIcon.setImageResource(listIcons.get(position));

            return convertView;
        }
    }
}


