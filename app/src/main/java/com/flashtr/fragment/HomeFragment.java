package com.flashtr.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.flashtr.ImagePagerActivity;
import com.flashtr.R;
import com.flashtr.UploadPhotoIntentService;
import com.flashtr.activity.CaptureActivity;
import com.flashtr.activity.CustomGallery;
import com.flashtr.activity.ReactionScreen;
import com.flashtr.activity.SelectAlbum;
import com.flashtr.activity.TabActivity;
import com.flashtr.adapter.AdpRecentPic;
import com.flashtr.adapter.AdpSyncedPhotoNew;
import com.flashtr.adapter.GridRecentPicsAdapter;
import com.flashtr.adapter.GridSharedPicsAdapter;
import com.flashtr.app.MyApp;
import com.flashtr.asyncTask.uploadPhotoTask;
import com.flashtr.customgallery.Action;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.activity.BucketHomeFragmentActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by BVK on 09-07-2015.
 */
public class HomeFragment extends BaseFragment {

    public static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301, EDIT_IMAGE_WITH_AVIERY = 401;
    public static int photoCnt;
    @InjectView(R.id.view1)
    View view1;
    @InjectView(R.id.view2)
    View view2;
    /*@InjectView(R.id.rlNewPhoto)
    FrameLayout rlNewPhoto;*/
    @InjectView(R.id.rlShared)
    RelativeLayout rlShared;
    @InjectView(R.id.rlRecent)
    RelativeLayout rlRecent;

    public boolean isOnline = true;
    /*@InjectView(R.id.lvRecentPhotos)*/
    /*ListView lvRecentPhotos;*/
    public static RecyclerView lvRecentPhotos1;
    private AdpRecentPic recyclerAdpRecent;
    /*@InjectView(R.id.pull_refresh_list)
    PullToRefreshView mPullRefreshListView;
    ListView lvSharedPhotos;*/
    public static SwipeRefreshLayout swipeRefreshLayout1;
    public static RecyclerView mRecyclerView;
    public static GridView gridRecentPhotos;
    public static GridView gridSharedPhotos;
    @InjectView(R.id.tvError)
    ImageView tvError;
    @InjectView(R.id.tvErrorTxt)
    TextView tvErrorTxt;
    @InjectView(R.id.tvCount)
    TextView tvCount;
    /*@InjectView(R.id.flCameraView)
    FrameLayout flCameraView;*/
    @InjectView(R.id.flSelectOption)
    FrameLayout flSelectOption;
    @InjectView(R.id.rlGallery)
    RelativeLayout rlGallery;
    @InjectView(R.id.rlCamera)
    RelativeLayout rlCamera;
    @InjectView(R.id.flTopLayer)
    FrameLayout flTopLayer;
    /*@InjectView(R.id.ivPluse)
    ImageView ivPluse;*/
    String TAG = "HomeFragment";
    MyReceiver myReceiver;
    /*ProgressDialog progressDialog;*/
    int selectedTab;
    String camera_pathname = "";
    String strImage = "";
    Uri globalUri = null;
    //HomeActivity homeActivity = null;
    Cursor cursor = null;
    ArrayList<HashMap<String, String>> listRecentPhotos = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> listSharedPhotos = new ArrayList<HashMap<String, String>>();
    Typeface font;
    Animation anticlock;
    private DemoFragment parent;
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    private UploadPhotoReceiver uploadPhotoReceiver;
    private DownloadManager downloadManager;
    private long enqueue;
    /*private RotateAnimation rotateAnim;*/

    @InjectView(R.id.iv_imGrid)
    public ImageView mImgGrid;

    @InjectView(R.id.tvAlbubName)
    TextView tvAlbubName;
    @InjectView(R.id.tvChangeAlbum)
    TextView tvChangeAlbum;
    Dialog dialogImageSelection;
    Dialog dialogPhotoCapture;
    private PopupWindow popupWindow;
    private DisplayMetrics metrics = new DisplayMetrics();
    int ViewPosition;
    int LikeStatus; /* 0 means will call unlike services
                       1 means will call like services */
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int TotalRecord = 0;
    private int page = 0;
    private int pageLimit = 0;
    private boolean isRequestCallled = false;
    private int lastMomentListDataCount = 0;
    LinearLayoutManager manager;
    private boolean isClear;


    private BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null && (intent.getAction().equals(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER) || intent.getAction().equals(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER))) {
                final ArrayList<String> paths = intent.getStringArrayListExtra("list");
                if (!paths.isEmpty() && paths.size() <= 10) {
                    final String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                    final String album_id = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
                    for (int i = 0; i < paths.size(); i++) {
                        if (Util.isOnline(getActivity())) {
                        /*Intent uploadPhotoIntent = new Intent(getActivity(), UploadPhotoIntentService.class);
                        uploadPhotoIntent.putExtra(Constant.SHRED_PR.KEY_USERID, userId);
                        uploadPhotoIntent.putExtra(Constant.SHRED_PR.KEY_Current_AlbumID, album_id);
                        uploadPhotoIntent.putExtra(Constant.SHRED_PR.KEY_Photo_Name, paths.get(i));
                        uploadPhotoIntent.putExtra(Constant.SHRED_PR.KEY_Data, HomeFragment.class.getSimpleName());
                        getActivity().startService(uploadPhotoIntent);*/


                            uploadPhotoTask task = new uploadPhotoTask(context, userId, album_id, paths.get(i), HomeFragment.class.getSimpleName());
                            task.execute();
                        }
                        Log.e("imageBroadcastReceiver", paths.get(i));
                    }
                } else {
                    Toast.makeText(getActivity(), "Not allowed to upload more than 10 image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private BroadcastReceiver downloadImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                final DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(enqueue);
                if (downloadManager == null) {
                    downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                }
                final Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)) {
                        Toast.makeText(getActivity(), "Photo saved", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    public static HomeFragment newInstance(String AlbumId) {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    public static HomeFragment instance;

    public static HomeFragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        ViewGroup root = (ViewGroup) view.findViewById(R.id.header);
        Util.setFont(root, Util.getFont(1, getActivity()));
        instance = this;
        gridSharedPhotos = (GridView) view.findViewById(R.id.gridSharedPhotos);
        gridRecentPhotos = (GridView) view.findViewById(R.id.gridRecentPhotos);
        swipeRefreshLayout1 = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.pull_refresh_list);
        lvRecentPhotos1 = (RecyclerView) view.findViewById(R.id.lvRecentPhotos1);

        swipeRefreshLayout1.setVisibility(View.GONE);
        lvRecentPhotos1.setVisibility(View.GONE);
        gridRecentPhotos.setVisibility(View.GONE);
        gridSharedPhotos.setVisibility(View.GONE);


        ButterKnife.inject(this, view);
        tvCount.setVisibility(View.GONE);
        rlShared.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        rlRecent.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        parent = (DemoFragment) ((TabActivity) getActivity()).adapter.getItem(1);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

       /* if (selectedTab == 0) {
            showHideSynced();
        } else {
            showHideRecent();
        }*/
        String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
        if (listSharedPhotos != null && listSharedPhotos.size() > 0) {
            recyclerAdpRecent = (AdpRecentPic) lvRecentPhotos1.getAdapter();
            if (recyclerAdpRecent != null && recyclerAdpRecent.getItemCount() > 0) {
                recyclerAdpRecent.setListItem(listRecentPhotos);
            } else {

                recyclerAdpRecent = new AdpRecentPic(this, listRecentPhotos, tvCount, instance);
                lvRecentPhotos1.setAdapter(recyclerAdpRecent);
            }

            AdpSyncedPhotoNew recyclerAdp = (AdpSyncedPhotoNew) mRecyclerView.getAdapter();
            if (recyclerAdp != null && recyclerAdp.getItemCount() > 0) {
                recyclerAdp.setData(listSharedPhotos, page, pageLimit);
            } else {

                recyclerAdp = new AdpSyncedPhotoNew(getActivity(), listSharedPhotos, page, pageLimit, instance);
                mRecyclerView.setAdapter(recyclerAdp);
            }
            gridRecentPhotos.setAdapter(new GridRecentPicsAdapter(getActivity(), listRecentPhotos));
            gridSharedPhotos.setAdapter(new GridSharedPicsAdapter(getActivity(), listSharedPhotos));
            tvError.setVisibility(View.GONE);
            tvCount.setVisibility(View.GONE);

            if (selectedTab == 0) {
                if (page == 1) {
                    if (listSharedPhotos.size() == 0) {
                        listSharedPhotos.clear();
                        tvError.setVisibility(View.VISIBLE);
                        if (Util.isOnline(getActivity())) {
                        } else {
                        }
                    } else {
                        tvError.setVisibility(View.GONE);
                    }
                }
            } else {
                if (listRecentPhotos.size() == 0) {
                    tvError.setVisibility(View.VISIBLE);
                    tvCount.setVisibility(View.GONE);
                    photoCnt = Util.getOfflinePic(getActivity());
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
                } else {
                    tvError.setVisibility(View.GONE);
                    tvCount.setVisibility(View.VISIBLE);
                    // get offile pic count
                    photoCnt = Util.getOfflinePic(getActivity());
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
                    tvCount.setText("" + photoCnt + " " + getActivity().getResources().getString(R.string.photos_awaiting));

                }
            }

            setNoData();
        } else {
            new getPhotos(userId, true).execute();
        }
        uploadPhotoReceiver = new UploadPhotoReceiver();

        IntentFilter imageIntentFilter = new IntentFilter(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
        getActivity().registerReceiver(imageBroadcastReceiver, imageIntentFilter);
        getActivity().registerReceiver(downloadImageReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        final String imagePath = Environment.getExternalStorageDirectory() + "/Pictures/temp.jpg";
        final File imageFile = new File(imagePath);
        deleteImageFromGallery(imageFile);


    }

    /*private void showHideSynced() {
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.GONE);
        *//*rlNewPhoto.setVisibility(View.VISIBLE);*//*
        swipeRefreshLayout1.setVisibility(View.VISIBLE);
        lvRecentPhotos1.setVisibility(View.GONE);
        gridSharedPhotos.setVisibility(View.GONE);
        gridRecentPhotos.setVisibility(View.GONE);
        tvCount.setVisibility(View.GONE);
    }

    private void showHideRecent() {
        flSelectOption.setVisibility(View.GONE);
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.VISIBLE);
        *//*rlNewPhoto.setVisibility(View.GONE);*//*
        swipeRefreshLayout1.setVisibility(View.GONE);
        lvRecentPhotos1.setVisibility(View.VISIBLE);
        gridSharedPhotos.setVisibility(View.GONE);
        gridRecentPhotos.setVisibility(View.GONE);
        tvCount.setVisibility(View.GONE);
    }*/

    /*@OnClick(R.id.flCameraView)
    public void flCameraView(View view) {
        Toast.makeText(getActivity(), "Slide to go to camera", Toast.LENGTH_SHORT).show();

    }*/

    public void downloadImage(String image, boolean isDownloaded) {
        if (!TextUtils.isEmpty(image)) {

            final String[] title = image.split("/");
            final String imageName = title[title.length - 1];
            Boolean isSDPresent = isExternalStorageAvailable();
            Log.e("isSDPresent", "" + isSDPresent);

            if (!isDownloaded) {
                downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

                final Uri downloadUri = Uri.parse(image);
                final DownloadManager.Request request = new DownloadManager.Request(
                        downloadUri);
                if (isSDPresent) {
                    final File root = new File(Environment.getExternalStorageDirectory()
                            + "/Pictures/" + getString(R.string.app_name));
                    if (!root.exists()) root.mkdir();
                } else {
                    final File root = new File(Environment.getDataDirectory()
                            + "/Pictures/" + getString(R.string.app_name));
                    if (!root.exists()) root.mkdir();
                }
                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI
                                | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(true).setTitle(getString(R.string.app_name)).setDescription(imageName)
                        .setDestinationInExternalPublicDir("/Pictures/" + getString(R.string.app_name), imageName);
                enqueue = downloadManager.enqueue(request);
            } else {
                final File sourceFile = new File(image);
                File destinationFile;
                if (isSDPresent) {
                    destinationFile = new File(Environment.getExternalStorageDirectory() + "/Pictures/" + getString(R.string.app_name), imageName);
                } else {
                    destinationFile = new File(Environment.getDataDirectory() + "/Pictures/" + getString(R.string.app_name), imageName);
                }

                try {
                    copyFile(sourceFile, destinationFile);
                    Toast.makeText(getActivity(), "Photo saved", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            Log.e(TAG, "isResumed called here..");

            if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_SHARED_PHOTOS).equals("1")) {
                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_SHARED_PHOTOS, "0");
//                final String result = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTOS);
//                if (Util.isOnline(getActivity()) && TextUtils.isEmpty(result)) {
//                    String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
//                    new getPhotos(userId, false).execute();
//                } else {
//                    loadOfflinePhotos();
//                }
            }

            if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home).equals("5")) {
                if (selectedTab == 0) {
                    rlShared.performClick();
                } else {
                    rlRecent.performClick();
                }
            }

        } else if (!visible) {
            Log.e(TAG, "isPaused called here..");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("onResume", "called here..");
        String tabVal = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB);
        if (tabVal.equalsIgnoreCase("")) {
            selectedTab = 0;
            Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB, "0");
        } else {
            selectedTab = Integer.parseInt(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB));
        }
//        selectedTab = Integer.parseInt(Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_SELECTED_TAB));
//        final String result = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTOS);
        if (selectedTab == 0) {
            if (Util.isOnline(getActivity())) {
                /*showHideSynced();*/
                String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
//                if (Util.isOnline(getActivity()) && TextUtils.isEmpty(result)) {
//                    new getPhotos(userId, true).execute();
//                    refreshList();
//                } else
//                    loadOfflinePhotos();

            } else {
                /*showHideSynced();*/
//                loadOfflinePhotos();
            }
        } else {
            /*showHideRecent();*/
            rlRecent.performClick();
        }

        /*rotateAnim = new RotateAnimation(45, 0, ivPluse.getHeight() / 2, ivPluse.getWidth() / 2);
        rotateAnim.setDuration(200);
        rotateAnim.setInterpolator(new OvershootInterpolator());
        rotateAnim.setFillAfter(true);
        ivPluse.startAnimation(rotateAnim);*/

        try {
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(TabActivity.ACTIVITY_SERVICE);
            getActivity().registerReceiver(myReceiver, intentFilter);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(uploadPhotoReceiver, new IntentFilter(Constant.SHRED_PR.KEY_Intent_Filter));
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (MyApp.gridSelected == 0) {
            mImgGrid.setImageResource(R.drawable.grid_icon);
        } else if (MyApp.gridSelected == 1) {
            mImgGrid.setImageResource(R.drawable.list_icon);
        }
        String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);

        if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Albums) == "15") {
            String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
            tvAlbubName.setText(albumName);
            String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
//            Toast.makeText(getActivity(),albumId+"",Toast.LENGTH_SHORT).show();
            gridSharedPhotos.setVisibility(View.GONE);
            swipeRefreshLayout1.setVisibility(View.GONE);
            lvRecentPhotos1.setVisibility(View.GONE);
            gridRecentPhotos.setVisibility(View.GONE);
            refreshList();
            if (Util.isOnline(getActivity()))
                LodingShow();
        } else {
            new getAlbums(userId).execute();
        }
        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Albums, "0");

    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(uploadPhotoReceiver);

        try {
            getActivity().unregisterReceiver(myReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
        super.onPause();
    }

    /*@OnClick(R.id.iv_imgAdd)
    @SuppressWarnings("unused")
    public void NewPhoto(View view) {

        flTopLayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        rotateAnim = null;
        if (flSelectOption.getVisibility() == View.VISIBLE) {
            rotateAnim = new RotateAnimation(45, 0, ivPluse.getHeight() / 2, ivPluse.getWidth() / 2);
        } else {
            rotateAnim = new RotateAnimation(0, 45, ivPluse.getHeight() / 2, ivPluse.getWidth() / 2);
        }
        rotateAnim.setDuration(200);
        rotateAnim.setInterpolator(new OvershootInterpolator());
        rotateAnim.setFillAfter(true);
        ivPluse.startAnimation(rotateAnim);

        Animation animShow = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_up);
        //Load animation
        Animation animHide = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_down);
        Animation clock = AnimationUtils.loadAnimation(getActivity(),
                R.anim.zoom_in);
        //Load animation
        anticlock = AnimationUtils.loadAnimation(getActivity(),
                R.anim.zoom_out);

        if (flSelectOption.getVisibility() == View.VISIBLE) {
//            animHide.setFillAfter(false);
            flSelectOption.startAnimation(anticlock);
//            flSelectOption.startAnimation(animHide);
            flSelectOption.setVisibility(View.GONE);
            flTopLayer.setVisibility(View.GONE);
        } else {
            // Start animation
//            animShow.setFillAfter(true);

            flTopLayer.setVisibility(View.VISIBLE);
            flSelectOption.setVisibility(View.VISIBLE);
//            flSelectOption.startAnimation(animShow);
            flSelectOption.startAnimation(clock);
        }

//        selectImageOption();
    }*/

//    @OnClick(R.id.rlGallery)
//    public void Gallery(View view) {
//        /*rotateAnim = new RotateAnimation(45, 0, ivPluse.getHeight() / 2, ivPluse.getWidth() / 2);*/
////            flSelectOption.startAnimation(animHide);
//        /*flSelectOption.startAnimation(anticlock);
//
//        flSelectOption.setVisibility(View.GONE);
//        flTopLayer.setVisibility(View.GONE);*/
//        MediaChooser.showOnlyImageTab();
//        MediaChooser.setSelectedMediaCount(0);
//        MediaChooser.setSelectionLimit(10);
//        final Intent intent = new Intent(getActivity(), BucketHomeFragmentActivity.class);
//        startActivity(intent);
//    }

    @OnClick(R.id.rlGrid)
    public void Grid(View view) {
        if (MyApp.gridSelected == 0) {
            MyApp.gridSelected = 1;
            mImgGrid.setImageResource(R.drawable.list_icon);
        } else {
            MyApp.gridSelected = 0;
            mImgGrid.setImageResource(R.drawable.grid_icon);
        }
        Intent intent1 = new Intent();
        intent1.setAction(getActivity().ACTIVITY_SERVICE);
        intent1.putExtra("type", 1);
        getActivity().sendBroadcast(intent1);
        setNoData();
    }

    @OnClick(R.id.rlResend)
    public void changeAlbum(View v) {
        Intent i = new Intent(getActivity(), SelectAlbum.class);
        startActivity(i);
    }

//    @OnClick(R.id.iv_imgAdd)
//    public void Camera(View view) {
//        /*rotateAnim = new RotateAnimation(45, 0, ivPluse.getHeight() / 2, ivPluse.getWidth() / 2);*/
//        /*flSelectOption.startAnimation(anticlock);
//        flSelectOption.setVisibility(View.GONE);
//        flTopLayer.setVisibility(View.GONE);*/
//       /* CameraFragment.newInstance();
//        if (CameraFragment.getInstance() != null) {
//            CameraFragment.getInstance().setLayoutParams(true);
//            CameraFragment.getInstance().getmCameraPreview().startCamera();
//            CameraFragment.getInstance().setLayoutParams(false);
//        }*/
//        /*if (pager != null) {
//            pager.setCurrentItem(0);
//        }*/
//
//        CameraFragment cameraFragment = new CameraFragment();
//        parent.pushFragments(Constant.TAB_FRAGMENT_HOME, cameraFragment, false, false, true, true);
//    }


    public void clickEvent(View v) {
        super.clickEvent(v);
        String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
        switch (v.getId()) {
            case R.id.iv_albumPhoto:
                if (!swipeRefreshLayout1.isRefreshing()) {
                    ViewPosition = (int) v.getTag();
                    Log.d("POSITION: ", ViewPosition + "");
                    Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
                    intent.putExtra("from", Constant.SharedPics);
                    intent.putExtra("pos", ViewPosition);
                    intent.putExtra("list", listSharedPhotos);
                    startActivity(intent);
                }
                break;
            case R.id.iv_imgMenu:
                if (!swipeRefreshLayout1.isRefreshing()) {
//                    showPopupWindow(v);
                    final int Viewposition1 = (int) v.getTag();
                    showAddPhotoDialog(Viewposition1);
                }


                break;
            case R.id.relRec:
                int ViewPos = (int) v.getTag();
                Intent i = new Intent(getActivity(), ReactionScreen.class);
                i.putExtra("num", listSharedPhotos.get(ViewPos).get("total_likes"));
                i.putExtra("PhotoId", listSharedPhotos.get(ViewPos).get("photo_id"));
                startActivity(i);
                break;
            case R.id.iv_imo1:
                if (!swipeRefreshLayout1.isRefreshing()) {
                    ViewPosition = (int) v.getTag();

                    int LikeStatus1; /* 0 means will call unlike services
                       1 means will call like services */
                    if (listSharedPhotos.get(ViewPosition).get("isliked").contains(userId)) {
                        LikeStatus1 = 0;
                    } else {
                        LikeStatus1 = 1;
                    }
                    setLikeViews(1, userId);
                    addLiketoPhoto(1, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus1);
                }
                break;
            case R.id.iv_imo2:
                if (!swipeRefreshLayout1.isRefreshing()) {
                    ViewPosition = (int) v.getTag();
                    int LikeStatus2; /* 0 means will call unlike services
                       1 means will call like services */
                    if (listSharedPhotos.get(ViewPosition).get("isloved").contains(userId)) {
                        LikeStatus2 = 0;
                    } else {
                        LikeStatus2 = 1;
                    }
                    setLikeViews(2, userId);
                    addLiketoPhoto(2, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus2);
                }
                break;
            case R.id.iv_imo3:
                if (!swipeRefreshLayout1.isRefreshing()) {
                    ViewPosition = (int) v.getTag();
                    int LikeStatus3; /* 0 means will call unlike services
                       1 means will call like services */
                    if (listSharedPhotos.get(ViewPosition).get("ishaha").contains(userId)) {
                        LikeStatus3 = 0;
                    } else {
                        LikeStatus3 = 1;
                    }
                    setLikeViews(3, userId);
                    addLiketoPhoto(3, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus3);
                }
                break;
            case R.id.iv_imo4:
                if (!swipeRefreshLayout1.isRefreshing()) {
                    ViewPosition = (int) v.getTag();
                    int LikeStatus4; /* 0 means will call unlike services
                       1 means will call like services */
                    if (listSharedPhotos.get(ViewPosition).get("iswow").contains(userId)) {
                        LikeStatus4 = 0;
                    } else {
                        LikeStatus4 = 1;
                    }
                    setLikeViews(4, userId);
                    addLiketoPhoto(4, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus4);
                }
                break;
            case R.id.iv_imo5:
                if (!swipeRefreshLayout1.isRefreshing()) {
                    ViewPosition = (int) v.getTag();

                    int LikeStatus5; /* 0 means will call unlike services
                       1 means will call like services */
                    if (listSharedPhotos.get(ViewPosition).get("isangry").contains(userId)) {
                        LikeStatus5 = 0;
                    } else {
                        LikeStatus5 = 1;
                    }
                    setLikeViews(5, userId);
                    addLiketoPhoto(5, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus5);
                }
                break;
            case R.id.iv_imo6:
                if (!swipeRefreshLayout1.isRefreshing()) {
                    ViewPosition = (int) v.getTag();
                    int LikeStatus6; /* 0 means will call unlike services
                       1 means will call like services */
                    if (listSharedPhotos.get(ViewPosition).get("issad").contains(userId)) {
                        LikeStatus6 = 0;
                    } else {
                        LikeStatus6 = 1;
                    }
                    setLikeViews(6, userId);
                    addLiketoPhoto(6, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus6);
                }
                break;
            case R.id.iv_imgFav:
                if (!swipeRefreshLayout1.isRefreshing()) {
                    ViewPosition = (int) v.getTag();

                    String photoId = listSharedPhotos.get(ViewPosition).get("photo_id");
                    String likedUnliked = listSharedPhotos.get(ViewPosition).get("favourite_photo");

                    for (int i1 = 0; i1 < listSharedPhotos.size(); i1++) {
                        if (listSharedPhotos.get(i1).get("photo_id") == photoId) {
                            listSharedPhotos.get(i1).put("favourite_photo", "" + (likedUnliked.equalsIgnoreCase("0") ? "1" : "0"));
                            AdpSyncedPhotoNew recyclerAdp = (AdpSyncedPhotoNew) mRecyclerView.getAdapter();
                            if (recyclerAdp != null && recyclerAdp.getItemCount() > 0) {
                                recyclerAdp.setData(listSharedPhotos, page, pageLimit);
                            } else {

                                recyclerAdp = new AdpSyncedPhotoNew(getActivity(), listSharedPhotos, page, pageLimit, instance);
                                mRecyclerView.setAdapter(recyclerAdp);
                            }
                        }
                    }

                    addFavs task = new addFavs(photoId);
                    task.execute();
                }
                break;
            case R.id.tv_txtLoadMore:
                int lastVisibleItem = manager.findLastVisibleItemPosition();
                if ((lastVisibleItem + 1) == listSharedPhotos.size() && !isRequestCallled && /*!(lastMomentListDataCount >= 0 && lastMomentListDataCount < 10)*/ !(page >= pageLimit)) {
                    if (Util.isOnline(getActivity())) {

                        String userId1 = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                        new getPhotos(userId1, false).execute();
                    } else {
//                        loadOfflinePhotos();
                    }
                }
                break;
        }
    }

    public void ImageClick(View view) {
        int ViewPosition = (int) view.getTag();
        Log.d("POSITION: ", ViewPosition + "");
    }

    @OnClick(R.id.iv_imgAdd)
    public void showImageSelectionDialog(View view1) {
        if (dialogImageSelection != null && dialogImageSelection.isShowing()) {

        } else {
            dialogImageSelection = new Dialog(getActivity(), R.style.DialogTheme1);
            dialogImageSelection.setCancelable(true);
            dialogImageSelection.setCanceledOnTouchOutside(true);
            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.diag_image_selector, null);


            TextView mTxtCamera = (TextView) view.findViewById(R.id.tv_txtCamera);
            TextView mTxtGallery = (TextView) view.findViewById(R.id.tv_txtGallery);
            TextView mTxtClose = (TextView) view.findViewById(R.id.tv_txtClose);

            mTxtClose.setVisibility(View.GONE);

            font = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");

            mTxtCamera.setTypeface(font);
            mTxtGallery.setTypeface(font);
            mTxtClose.setTypeface(font);

//            LinearLayout mLinearClose = (LinearLayout) view.findViewById(R.id.layClose);

            mTxtCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogImageSelection.dismiss();
//                    CameraFragment cameraFragment = new CameraFragment();
                    ((TabActivity) getActivity()).viewPager.setCurrentItem(0);
                }
            });

            mTxtGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogImageSelection.dismiss();
                    /*MediaChooser.showOnlyImageTab();
                    MediaChooser.setSelectedMediaCount(0);
                    MediaChooser.setSelectionLimit(10);
                    final Intent intent = new Intent(getActivity(), BucketHomeFragmentActivity.class);
                    startActivity(intent);*/
                    /*Intent intent = new Intent(getActivity(),CustomGallery.class);
                    startActivityForResult(intent, GALLERY_CODE);*/
                    Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                    intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                    startActivityForResult(intent, GALLERY_CODE);

                }
            });

//            mLinearClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialogImageSelection.dismiss();
//                }
//            });

            dialogImageSelection.setContentView(view);
            dialogImageSelection.show();
        }
    }

    private void showAddPhotoDialog(final int Viewposition) {
        if (dialogImageSelection != null && dialogImageSelection.isShowing()) {

        } else {
            dialogImageSelection = new Dialog(getActivity(), R.style.DialogTheme1);
            dialogImageSelection.setCancelable(true);
            dialogImageSelection.setCanceledOnTouchOutside(true);
            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.diag_image_selector, null);

            View view3 = view.findViewById(R.id.view3);
            View view2 = view.findViewById(R.id.view2);
            TextView mTxtCamera = (TextView) view.findViewById(R.id.tv_txtCamera);
            TextView mTxtGallery = (TextView) view.findViewById(R.id.tv_txtGallery);
            TextView mTxtClose = (TextView) view.findViewById(R.id.tv_txtClose);

            mTxtCamera.setText("Delete");
            mTxtGallery.setText("Download");
            mTxtClose.setText("Share");
            view3.setVisibility(View.VISIBLE);


            if (listSharedPhotos != null && !listSharedPhotos.isEmpty()) {
                String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                String created_by_me = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_CREATEDBYME);
                if (listSharedPhotos.get(Viewposition).get("user_id").equalsIgnoreCase(userId)) {
                    mTxtCamera.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                } else {
                    view2.setVisibility(View.GONE);
                    mTxtCamera.setVisibility(View.GONE);
                }
            }

            font = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");

            mTxtCamera.setTypeface(font);
            mTxtGallery.setTypeface(font);
            mTxtClose.setTypeface(font);

            mTxtCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogImageSelection.dismiss();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("Delete this photo?");
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Util.isOnline(getActivity())) {
                                new deletePhoto(listSharedPhotos.get(Viewposition).get("album_id"), listSharedPhotos.get(Viewposition).get("photo_id"), ViewPosition).execute();
                            } else
                                Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });

            mTxtGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogImageSelection.dismiss();
                    popupWindow.dismiss();
                    String image = "" + listSharedPhotos.get(Viewposition).get("photo");
                    if (!TextUtils.isEmpty(image)) {
                        downloadImage(image, false);
                    }
                }
            });

            mTxtClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogImageSelection.dismiss();
                    String image = "" + listSharedPhotos.get(Viewposition).get("photo");
                    if (!TextUtils.isEmpty(image)) {
                        new Share(image).execute();
                    }
                }
            });

            dialogImageSelection.setContentView(view);
            dialogImageSelection.show();
        }
    }


    // OFFLINE
    @OnClick(R.id.rlRecent)
    public void Recent(View view) {
//        if (progressDialog != null && !progressDialog.isShowing()) {
//            selectedTab = 1;
        if (!swipeRefreshLayout1.isRefreshing()) {
            isOnline = true;
            Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB, "1");
            Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home, "5");
            rlShared.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            rlRecent.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            flSelectOption.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
        /*rlNewPhoto.setVisibility(View.GONE);*/
            if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home).equals("5")) {
                //For Recent Photos:
                try {
                    listRecentPhotos.clear();
                    String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);

                    File[] files = new File(getActivity().getExternalCacheDir().toString() + "/Album/" + albumId + "/").listFiles();
                    File[] files1 = new File(getActivity().getExternalCacheDir().toString() + "/Albums/" + albumId + "/").listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            String[] sep = files[i].toString().split("/");
                            String filename = sep[sep.length - 1];
                            for (int j = 0; j < files1.length; j++) {
                                String[] sep1 = files1[j].toString().split("/");
                                String filename1 = sep1[sep1.length - 1];
                                if (filename.equals(filename1)) {
                                    HashMap<String, String> hashMap = new HashMap<String, String>();
                                    hashMap.put("user_id", "");
                                    hashMap.put("date", "");
                                    hashMap.put("album_id", "" + albumId);
                                    hashMap.put("photo", "" + files[i].toString());
                                    hashMap.put("photo1", "" + files1[j].toString());
                                    hashMap.put("shared", "0");
                                    hashMap.put("isShowinList", "1");

                                /* Check description of MyApp.uploadingImage variable in Myapp file*/
                                    if (!MyApp.uploadingImage.contains(files[i].toString()))
                                        listRecentPhotos.add(hashMap);

                                    Collections.reverse(listRecentPhotos);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                }
                }
                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home, "0");
                lvRecentPhotos1.setAdapter(new AdpRecentPic(this, listRecentPhotos, tvCount, instance));
                gridRecentPhotos.setAdapter(new GridRecentPicsAdapter(getActivity(), listRecentPhotos));

                swipeRefreshLayout1.setVisibility(View.GONE);
                lvRecentPhotos1.setVisibility(View.VISIBLE);
                gridSharedPhotos.setVisibility(View.GONE);
                gridRecentPhotos.setVisibility(View.GONE);
                tvCount.setVisibility(View.GONE);


                if (listRecentPhotos.size() == 0) {
                    tvError.setVisibility(View.VISIBLE);
                    tvCount.setVisibility(View.GONE);
                    photoCnt = listRecentPhotos.size();
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
                } else {
                    tvError.setVisibility(View.GONE);
                    tvCount.setVisibility(View.VISIBLE);

                    photoCnt = listRecentPhotos.size();
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
                    tvCount.setText("" + photoCnt + " " + getActivity().getResources().getString(R.string.photos_awaiting));
                }

                if (MyApp.gridSelected == 0) {
                    gridRecentPhotos.setVisibility(View.GONE);
                    lvRecentPhotos1.setVisibility(View.VISIBLE);
                    mImgGrid.setImageResource(R.drawable.grid_icon);
                } else {
                    lvRecentPhotos1.setVisibility(View.GONE);
                    gridRecentPhotos.setVisibility(View.VISIBLE);
                    mImgGrid.setImageResource(R.drawable.list_icon);
                }

            }
        }
        setNoData();
    }


    //SYNCED
    @OnClick(R.id.rlShared)
    public void Shared(View view) {
        if (!swipeRefreshLayout1.isRefreshing()) {
            isOnline = false;
//        if (progressDialog != null && !progressDialog.isShowing()) {

            Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB, "" + 0);
            rlShared.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            rlRecent.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);

        /*rlNewPhoto.setVisibility(View.VISIBLE);*/
        /*lvSharedPhotos.setDivider(null);
        lvSharedPhotos.setAdapter(new SharedPicsAdapter(this, HomeActivity.listSharedPhotos));*/
            AdpSyncedPhotoNew recyclerAdp = (AdpSyncedPhotoNew) mRecyclerView.getAdapter();
            if (recyclerAdp != null && recyclerAdp.getItemCount() > 0) {
                recyclerAdp.setData(listSharedPhotos, page, pageLimit);
            } else {

                recyclerAdp = new AdpSyncedPhotoNew(getActivity(), listSharedPhotos, page, pageLimit, instance);
                mRecyclerView.setAdapter(recyclerAdp);
            }
            gridSharedPhotos.setAdapter(new GridSharedPicsAdapter(getActivity(), listSharedPhotos));

            swipeRefreshLayout1.setVisibility(View.VISIBLE);
            lvRecentPhotos1.setVisibility(View.GONE);
            gridSharedPhotos.setVisibility(View.GONE);
            gridRecentPhotos.setVisibility(View.GONE);
            tvCount.setVisibility(View.GONE);


            if (listSharedPhotos.size() == 0) {
                tvError.setVisibility(View.VISIBLE);
                if (Util.isOnline(getActivity())) {
                } else {
                }
            } else tvError.setVisibility(View.GONE);
//        }

            if (MyApp.gridSelected == 0) {
                gridSharedPhotos.setVisibility(View.GONE);
                swipeRefreshLayout1.setVisibility(View.VISIBLE);
                mImgGrid.setImageResource(R.drawable.grid_icon);
            } else {
                swipeRefreshLayout1.setVisibility(View.GONE);
                gridSharedPhotos.setVisibility(View.VISIBLE);
                mImgGrid.setImageResource(R.drawable.list_icon);
            }
        }
        setNoData();
    }

    private void init() {
        font = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");
        tvCount.setTypeface(font);
        tvAlbubName.setTypeface(font);
        tvChangeAlbum.setTypeface(font);
        popupWindow = new PopupWindow(getActivity());

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    return true;
                }
                return false;
            }
        });

        String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
        if (albumName.length() > 0)
            tvAlbubName.setText("" + albumName);

        String tabVal = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB);
        if (tabVal.equalsIgnoreCase("")) {
            selectedTab = 0;
            Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB, "0");
        } else {
            selectedTab = Integer.parseInt(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB));
        }


        /*lvSharedPhotos = mPullRefreshListView.getRefreshableView();*/
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        LinearLayoutManager manager1 = new LinearLayoutManager(getActivity());
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        lvRecentPhotos1.setLayoutManager(manager1);

        /*progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);*/

        outPutFile = new File(Constant.storageDirectory, "temp.jpg");

        /*mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (selectedTab == 0) {
                    if (Util.isOnline(getActivity())) {

                        String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                        new getPhotos(userId, false).execute();
                    } else {
                        loadOfflinePhotos();
                    }
                }
            }
        });*/

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (page > 2) {

                } else {
                    int lastVisibleItem = manager.findLastVisibleItemPosition();


                    if ((lastVisibleItem + 1) == listSharedPhotos.size() && !isRequestCallled && /*!(lastMomentListDataCount >= 0 && lastMomentListDataCount < 10)*/ !(page >= pageLimit)) {
                        if (Util.isOnline(getActivity())) {

                            String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                            new getPhotos(userId, false).execute();
                        } else {
//                        loadOfflinePhotos();
                        }
                    } else {
                   /* visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    firstVisibleItem = manager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount && TotalRecord > listSharedPhotos.size()) {
                        page++;

                    }*/
                    }
                }
            }
        });


        swipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (selectedTab == 0) {

                    refreshList();

                }

            }
        });

        if (mRecyclerView.getVisibility() == View.VISIBLE || lvRecentPhotos1.getVisibility() == View.VISIBLE) {
            MyApp.gridSelected = 0;
            mImgGrid.setImageResource(R.drawable.grid_icon);
        } else if (gridRecentPhotos.getVisibility() == View.VISIBLE || gridSharedPhotos.getVisibility() == View.VISIBLE) {
            MyApp.gridSelected = 1;
            mImgGrid.setImageResource(R.drawable.list_icon);
        }

    }

    public void refreshList() {
        setNoData();
        page = 0;
        lastMomentListDataCount = 0;
        isRequestCallled = false;
        isClear = true;
        /*listSharedPhotos = new ArrayList<HashMap<String, String>>();*/

        if (Util.isOnline(getActivity())) {

            String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            new getPhotos(userId, false).execute();
        } else {
//            loadOfflinePhotos();

        }


        if (lvRecentPhotos1.getVisibility() == View.VISIBLE || gridRecentPhotos.getVisibility() == View.VISIBLE) {
            tvCount.setVisibility(View.VISIBLE);
        } else {
            tvCount.setVisibility(View.GONE);
        }
    }

    private void reload(String result) {
//        selectedTab = Integer.parseInt(Util.ReadSharePrefrence(getActivity(),Constant.SHRED_PR.KEY_SELECTED_TAB));
        listRecentPhotos.clear();
//        if((pageLimit-1 == page))
//            listSharedPhotos.clear();
//        if (selectedTab == 0) {

        if (isClear) {
            isClear = false;
            listSharedPhotos = new ArrayList<HashMap<String, String>>();
        }

        if (Util.isOnline(getActivity())) {
            try {
                JSONObject jObj = new JSONObject(result);
                int status = jObj.optInt("success");
                pageLimit = jObj.optInt("totalpage");
                if (status == 1) {
//                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTOS, "" + result);
                    JSONArray jsonArray = jObj.getJSONArray("data");
                    lastMomentListDataCount = jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.optJSONObject(i);

                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("album_id", "" + jsonObject.optString("album_id"));
                        hashMap.put("photo_id", "" + jsonObject.optString("photo_id"));
                        hashMap.put("photo", "" + jsonObject.optString("photo"));
                        hashMap.put("user_id", "" + jsonObject.optString("user_id"));
                        hashMap.put("user_name", "" + jsonObject.optString("user_name"));
                        hashMap.put("user_image", "" + jsonObject.optString("user_image"));
                        hashMap.put("shared", "" + jsonObject.optString("shared"));
                        hashMap.put("date", "" + jsonObject.optString("date"));
                        hashMap.put("thumbImage", "" + jsonObject.optString("thumb_image"));

                        hashMap.put("isliked", "" + jsonObject.optString("isliked"));
                        hashMap.put("isloved", "" + jsonObject.optString("isloved"));
                        hashMap.put("ishaha", "" + jsonObject.optString("ishaha"));
                        hashMap.put("iswow", "" + jsonObject.optString("iswow"));
                        hashMap.put("isangry", "" + jsonObject.optString("isangry"));
                        hashMap.put("issad", "" + jsonObject.optString("issad"));
                        hashMap.put("shared", "" + jsonObject.optString("shared"));
                        hashMap.put("photo_status", "" + jsonObject.optString("photo_status"));
                        hashMap.put("favourite_photo", "" + jsonObject.optString("favourite_photo"));
                        hashMap.put("like_performed", "" + jsonObject.optString("like_performed"));
                        hashMap.put("total_likes", "" + jsonObject.optString("total_likes"));

                        if (!hasAlreadyAdded(jsonObject.optString("photo_id"))) {
                            listSharedPhotos.add(hashMap);
                        }

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        }

        //For Recent Photos:
//        if (selectedTab == 1) {

        try {
            listRecentPhotos.clear();
            String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);

            File[] files = new File(getActivity().getExternalCacheDir().toString() + "/Album/" + albumId + "/").listFiles();
            File[] files1 = new File(getActivity().getExternalCacheDir().toString() + "/Albums/" + albumId + "/").listFiles();

            files = SortFiles(files);
            files1 = SortFiles(files1);

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    String[] sep = files[i].toString().split("/");
                    String filename = sep[sep.length - 1];
                    for (int j = 0; j < files1.length; j++) {
                        String[] sep1 = files1[j].toString().split("/");
                        String filename1 = sep1[sep1.length - 1];
                        if (filename.equals(filename1)) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("user_id", "");
                            hashMap.put("date", "");
                            hashMap.put("album_id", "" + albumId);
                            hashMap.put("photo", "" + files[i].toString());
                            hashMap.put("photo1", "" + files1[j].toString());
                            hashMap.put("shared", "0");
                            hashMap.put("isShowinList", "1");

                            /* Check description of MyApp.uploadingImage variable in Myapp file*/
                            if (!MyApp.uploadingImage.contains(files[i].toString()))
                                listRecentPhotos.add(hashMap);

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }

        if (getActivity() != null) {
            /*mRecyclerView.setDivider(null);*/
            /*lvRecentPhotos.setDivider(null);*/
            /*lvRecentPhotos1.setAdapter(new AdpRecentPic(this, listRecentPhotos));*/
            /*lvSharedPhotos.setAdapter(new SharedPicsAdapter(this, HomeActivity.listSharedPhotos));*/

            recyclerAdpRecent = (AdpRecentPic) lvRecentPhotos1.getAdapter();
            if (recyclerAdpRecent != null && recyclerAdpRecent.getItemCount() > 0) {
                recyclerAdpRecent.setListItem(listRecentPhotos);
            } else {

                recyclerAdpRecent = new AdpRecentPic(this, listRecentPhotos, tvCount, instance);
                lvRecentPhotos1.setAdapter(recyclerAdpRecent);
            }
            AdpSyncedPhotoNew recyclerAdp = (AdpSyncedPhotoNew) mRecyclerView.getAdapter();
            recyclerAdp = (AdpSyncedPhotoNew) mRecyclerView.getAdapter();
            if (recyclerAdp != null && recyclerAdp.getItemCount() > 0) {
                recyclerAdp.setData(listSharedPhotos, page, pageLimit);
            } else {

                recyclerAdp = new AdpSyncedPhotoNew(getActivity(), listSharedPhotos, page, pageLimit, instance);
                mRecyclerView.setAdapter(recyclerAdp);
            }
            gridRecentPhotos.setAdapter(new GridRecentPicsAdapter(getActivity(), listRecentPhotos));
            gridSharedPhotos.setAdapter(new GridSharedPicsAdapter(getActivity(), listSharedPhotos));
            tvError.setVisibility(View.GONE);
            tvCount.setVisibility(View.GONE);

            if (selectedTab == 0) {
                if (page == 1) {
                    if (listSharedPhotos.size() == 0) {
                        listSharedPhotos.clear();
                        tvError.setVisibility(View.VISIBLE);
                        if (Util.isOnline(getActivity())) {
                        } else {
                        }
                    } else {
                        tvError.setVisibility(View.GONE);
                    }
                }
            } else {
                if (listRecentPhotos.size() == 0) {
                    tvError.setVisibility(View.VISIBLE);
                    tvCount.setVisibility(View.GONE);
                    photoCnt = Util.getOfflinePic(getActivity());
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
                } else {
                    tvError.setVisibility(View.GONE);
                    tvCount.setVisibility(View.VISIBLE);
                    // get offile pic count
                    photoCnt = Util.getOfflinePic(getActivity());
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
                    tvCount.setText("" + photoCnt + " " + getActivity().getResources().getString(R.string.photos_awaiting));

                }
            }
        }
        setNoData();

    }

    public boolean hasAlreadyAdded(String photo_id) {
        boolean a = false;

        for (int i = 0; i < listSharedPhotos.size(); i++) {
            if (listSharedPhotos.get(i).get("photo_id").equalsIgnoreCase(photo_id)) {
                a = true;
            }
        }
        return a;
    }

    private void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {
                    if (CameraFragment.getInstance() != null) {
                        CameraFragment.getInstance().setLayoutParams(true);
                        CameraFragment.getInstance().getmCameraPreview().startCamera();
                        CameraFragment.getInstance().setLayoutParams(false);
                    }

                    /*if (pager != null) {
                        pager.setCurrentItem(0);
                    }*/

                } else if (items[item].equals("Choose from Gallery")) {

                    /*Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                    startActivityForResult(i, GALLERY_CODE);*/
                    MediaChooser.showOnlyImageTab();
                    final Intent intent = new Intent(getActivity(), BucketHomeFragmentActivity.class);
                    startActivity(intent);


                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void startCameras() {
        if (globalUri != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, globalUri);
            startActivityForResult(intent, CAMERA_CODE);
        } else {
//            createURI();
            globalUri = Uri.fromFile(outPutFile);
        }
    }

    private void createURI() {
        Random random = new Random();
        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        globalUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void deleteImageFromGallery(File file) {
        try {

            getActivity().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.Media.DATA
                            + "='"
                            + file.getPath()
                            + "'", null);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(imageBroadcastReceiver);
        getActivity().unregisterReceiver(downloadImageReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:

                   /* final String[] all_path = data.getStringArrayExtra("all_path");

                    final String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                    final String album_id = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
                    for (String string : all_path) {

                        if (Util.isOnline(getActivity())) {
                            Intent intent = new Intent(getActivity(), UploadPhotoIntentService.class);
                            intent.putExtra(Constant.SHRED_PR.KEY_USERID, userId);
                            intent.putExtra(Constant.SHRED_PR.KEY_Current_AlbumID, album_id);
                            intent.putExtra(Constant.SHRED_PR.KEY_Photo_Name, string);
                            intent.putExtra(Constant.SHRED_PR.KEY_Data, HomeFragment.class.getSimpleName());
                            getActivity().startService(intent);
                        }

                    }*/
                    /*ArrayList<String> imagesPathList = new ArrayList<String>();
                    String[] imagesPath = data.getStringExtra("data").split("\\|");
                    for (int i=0;i<imagesPath.length;i++){
                        imagesPathList.add(imagesPath[i]);
                    }

                    imagesPathList.trimToSize();
                    Intent imageIntent = new Intent();
                    imageIntent.setAction(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
                    imageIntent.putStringArrayListExtra("list", imagesPathList);
                    getActivity().sendBroadcast(imageIntent);*/

                    ArrayList<Image> paths = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                    if (!paths.isEmpty() && paths.size() <= 10) {
                        final String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                        final String album_id = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
                        for (int i = 0; i < paths.size(); i++) {
                            if (Util.isOnline(getActivity())) {
                        /*Intent uploadPhotoIntent = new Intent(getActivity(), UploadPhotoIntentService.class);
                        uploadPhotoIntent.putExtra(Constant.SHRED_PR.KEY_USERID, userId);
                        uploadPhotoIntent.putExtra(Constant.SHRED_PR.KEY_Current_AlbumID, album_id);
                        uploadPhotoIntent.putExtra(Constant.SHRED_PR.KEY_Photo_Name, paths.get(i));
                        uploadPhotoIntent.putExtra(Constant.SHRED_PR.KEY_Data, HomeFragment.class.getSimpleName());
                        getActivity().startService(uploadPhotoIntent);*/

                                String path = saveBitmap(rotatephoto(paths.get(i).path));

                                uploadPhotoTask task = new uploadPhotoTask(getActivity(), userId, album_id, path, HomeFragment.class.getSimpleName());
                                task.execute();
                            }
                            Log.e("imageBroadcastReceiver", paths.get(i).path);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Not allowed to upload more than 10 image", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case CAMERA_CODE:
                    String imageId = convertImageUriToFile(globalUri, getActivity());
                    final String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
                    new SavePhotoTask(albumId, camera_pathname).execute();

                    break;
                case EDIT_IMAGE_WITH_AVIERY:
                    Uri editedImageUri = data.getData();
                    /*mEditedImageView.setImageURI(editedImageUri);*/

                    Toast.makeText(getActivity(), editedImageUri + "", Toast.LENGTH_SHORT).show();
                    break;

            }
        } else {
            Log.e("TAG", "data null");
        }
    }

    public String convertImageUriToFile(Uri imageUri, Activity activity) {
        int imageID = 0;
        try {
            /*********** Which columns values want to get *******/
            String[] proj = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };
            cursor = activity.managedQuery(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );
            //  Get Query Data
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            int size = cursor.getCount();
            /*******  If size is 0, there are no images on the SD Card. *****/
            if (size == 0) {

            } else {
                int thumbID = 0;
                if (cursor.moveToFirst()) {
                    /**************** Captured image details ************/
                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID = cursor.getInt(columnIndex);
                    thumbID = cursor.getInt(columnIndexThumb);
                    String Path = cursor.getString(file_ColumnIndex);
                    camera_pathname = Path;

                    //String orientation =  cursor.getString(orientation_ColumnIndex);
                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            + " ImageID :" + imageID + "\n"
                            + " ThumbID :" + thumbID + "\n"
                            + " Path :" + Path + "\n";
                    // Show Captured Image detail on activity
                    //imageDetails.setText( CapturedImageDetails );
                }
            }
        } finally {
            /*if (cursor != null) {
                cursor.close();
			}*/
        }
        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )
        return "" + imageID;
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }

    }

//    private void loadOfflinePhotos() {
//        /*mPullRefreshListView.onRefreshComplete();*/
//        swipeRefreshLayout1.setRefreshing(false);
//        final String response = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTOS);
//        if (!TextUtils.isEmpty(response)) {
//            reload(response);
//        }
//    }

    private boolean isExternalStorageAvailable() {

        String state = Environment.getExternalStorageState();
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable == true
                && mExternalStorageWriteable == true) {
            return true;
        } else {
            return false;
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            Log.e("MyReceiver", "here..");
            Bundle extras = arg1.getExtras();
            if (extras != null) {
                int type = extras.getInt("type");
                if (type == 0) {
                    if (Util.isOnline(getActivity())) {
                        String userId = Util.ReadSharePrefrence(getContext(), Constant.SHRED_PR.KEY_USERID);
//                        new getPhotos(userId, false).execute();
                    } else {
//                        loadOfflinePhotos();
                    }
                } else {
                    String tabVal = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB);
                    if (tabVal.equalsIgnoreCase("")) {
                        selectedTab = 0;
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB, "0");
                    } else {
                        selectedTab = Integer.parseInt(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB));
                    }
                    if (selectedTab == 0) {
                        rlShared.performClick();
                    } else {
                        rlRecent.performClick();
                    }
                }
            }
            setNoData();

        }
    }

    class getPhotos extends AsyncTask<Void, String, String> {

        String response;
        String userId;
        boolean flag;

        private getPhotos(String userId, boolean flag) {
            this.userId = userId;
            this.flag = flag;
            isRequestCallled = true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            if (flag)
//                progressDialog.show();


        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                final String album_id = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
                JSONObject jData = new JSONObject();
                jData.put("method", "getphotos");
                jData.put("userid", "" + userId);
                jData.put("album_id", "" + album_id);
                jData.put("page", "" + page);

                page++;

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

            swipeRefreshLayout1.setRefreshing(false);
//            if (progressDialog != null) {
//                if (progressDialog.isShowing()) progressDialog.dismiss();
//            }
            isRequestCallled = false;
            String selectedTab = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_SELECTED_TAB);
            if (Util.isOnline(getActivity())) {

                if (selectedTab.equalsIgnoreCase("0")) {
                    isOnline = false;
                    if (MyApp.gridSelected == 1) {
                        gridSharedPhotos.setVisibility(View.VISIBLE);
                        swipeRefreshLayout1.setVisibility(View.GONE);
                        lvRecentPhotos1.setVisibility(View.GONE);
                        gridRecentPhotos.setVisibility(View.GONE);
                    } else {
                        swipeRefreshLayout1.setVisibility(View.VISIBLE);
                        gridSharedPhotos.setVisibility(View.GONE);
                        lvRecentPhotos1.setVisibility(View.GONE);
                        gridRecentPhotos.setVisibility(View.GONE);
                    }
                } else {
                    isOnline = true;
                    if (MyApp.gridSelected == 1) {
                        gridRecentPhotos.setVisibility(View.VISIBLE);
                        gridSharedPhotos.setVisibility(View.GONE);
                        swipeRefreshLayout1.setVisibility(View.GONE);
                        lvRecentPhotos1.setVisibility(View.GONE);
                    } else {
                        swipeRefreshLayout1.setVisibility(View.GONE);
                        lvRecentPhotos1.setVisibility(View.VISIBLE);
                        gridSharedPhotos.setVisibility(View.GONE);
                        gridRecentPhotos.setVisibility(View.GONE);
                    }
                }


                if (!TextUtils.isEmpty(result)) {

                    reload(result);
                }
            } else {
                setNoData();
            }


        }
    }

    class addPhoto extends AsyncTask<Void, String, String> {

        //        ProgressDialog progressDialog;
        String albumId;

        private addPhoto(String albumId) {
            this.albumId = albumId;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

//            progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
//            progressDialog.setCancelable(false);
//            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//            progressDialog.show();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String resp = "";
            try {

                JSONObject jData = new JSONObject();
                jData.put("method", "addphoto");
                jData.put("userid", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
                jData.put("album_id", "" + albumId);
                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Last_Updated_Album, albumId);
                Log.e("DATA", "Add Photo @@ " + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID) + " @@ " + albumId);

                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = new HttpPost(
                        Constant.URL);

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                Log.e("camera_pathname", ">>" + camera_pathname);
                if (camera_pathname.length() > 0) {
                    File image = new File(camera_pathname);
                    fbody = new FileBody(image);
                    entity.addPart("file", fbody);
                }
                entity.addPart("data", new StringBody(jData.toString()));
                poster.setEntity(entity);

                response = client.execute(poster);
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity()
                                .getContent()));
                String line = null;
                while ((line = rd.readLine()) != null) {
                    resp += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Resp Upload", "@@@@@@@@ " + resp);
            return resp;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

//            if (progressDialog != null)
//                if (progressDialog.isShowing()) progressDialog.dismiss();
            Log.e("result", ">>" + result);

            try {
                JSONObject jObj = new JSONObject(result);

                int status = jObj.optInt("success");
                if (status == 1) {
                    camera_pathname = "";
                    Intent intent1 = new Intent();
                    intent1.setAction(getActivity().ACTIVITY_SERVICE);
                    intent1.putExtra("type", 0);
                    getActivity().sendBroadcast(intent1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class SavePhotoTask extends AsyncTask<Void, Void, String> {

        //        ProgressDialog progressDialog;
        String albumId, sourcePath;
        String strDate;
        File file;

        private SavePhotoTask(String albumId, String sourcePath) {
            this.albumId = albumId;
            this.sourcePath = sourcePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.show();*/


            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            strDate = sdf.format(date);

            final File cacheDir = new File(getActivity().getExternalCacheDir().toString() + "/Album/");
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }

            file = new File(cacheDir.getAbsolutePath() + "/" + albumId);
            if (!file.exists()) file.mkdir();

        }


        @Override
        protected String doInBackground(Void... str) {

            File photo = new File(file.toString(), "image" + strDate + ".jpg");

            if (photo.exists()) {
                photo.delete();
            }


            try {
                copyFile(new File(sourcePath), photo);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return (null);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            /*if (progressDialog != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }*/

            Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home, "5");
            camera_pathname = "";
            Intent intent1 = new Intent();
            intent1.setAction(getActivity().ACTIVITY_SERVICE);
            intent1.putExtra("type", 1);
            getActivity().sendBroadcast(intent1);

        }
    }

    private class UploadPhotoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (getActivity() != null) {
                if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_SHARED_PHOTOS).equals("1")) {
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_SHARED_PHOTOS, "0");
                    if (Util.isOnline(getActivity())) {
                        final String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);

                        refreshList();
                        reloadOfflinePhoto();
//                        new getPhotos(userId, false).execute();
                    } else {
//                        loadOfflinePhotos();
                    }
                } else if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home).equals("5")) {
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home, "0");
                    if (Util.isOnline(getActivity())) {
//                        final String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                        refreshList();
                        reloadOfflinePhoto();
//                        new getPhotos(userId, false).execute();
                    } else {
//                        loadOfflinePhotos();
                    }
                }


            }

            /*LodingShow();*/
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /* to Open popup window */
    private void showPopupWindow(View v) {

        final int Viewposition = (int) v.getTag();

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewPopup = layoutInflater.inflate(R.layout.popup_option, null);

        TextView mTxtDelete = (TextView) viewPopup.findViewById(R.id.tv_txtDelete);
        TextView mTxtDwnld = (TextView) viewPopup.findViewById(R.id.tv_txtDwnld);
        TextView mTxtShare = (TextView) viewPopup.findViewById(R.id.tv_txtShare);

        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");
        mTxtDelete.setTypeface(font1);
        mTxtDwnld.setTypeface(font1);
        mTxtShare.setTypeface(font1);

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        popupWindow.setContentView(viewPopup);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int height = v.getHeight();
        Log.v("Filer Icon", "Height: " + height);
        popupWindow.showAsDropDown(v, 0, -(height / 2));

        mTxtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                String image = "" + listSharedPhotos.get(Viewposition).get("photo");
                if (!TextUtils.isEmpty(image)) {
                    new Share(image).execute();
                }
            }
        });
        mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.app_name);
                builder.setMessage("Delete this photo?");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Util.isOnline(getActivity())) {
                            new deletePhoto(listSharedPhotos.get(Viewposition).get("album_id"), listSharedPhotos.get(Viewposition).get("photo_id"), ViewPosition).execute();
                        } else
                            Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        mTxtDwnld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                String image = "" + listSharedPhotos.get(Viewposition).get("photo");
                if (!TextUtils.isEmpty(image)) {
                    downloadImage(image, false);
                }
            }
        });
    }

    /*likeUnlike:  0 means will call unlike services
     * 1 means will call like services */
    private void addLiketoPhoto(int likedStatus, String photoId, int likeUnlike) {
        addLike task = new addLike(likedStatus, photoId, likeUnlike);
        task.execute();
    }

    private void setLikeViews(int LikeStatus, String userId) {
        for (int i = 0; i < listSharedPhotos.size(); i++) {
            if (listSharedPhotos.get(i).get("photo_id") == listSharedPhotos.get(ViewPosition).get("photo_id")) {
                for (int l = 0; l < 6; l++) {
                    String param = "";
                    String userIds = "";
                    String[] tempIds;
                    switch (l) {
                        case 0:

                            param = "isliked";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");


                            if (!userIds.contains(userId)) {
                                /* Add id from String */
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if (totalLikes != null && totalLikes.length() > 0) {
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp + 1;
                                        listSharedPhotos.get(i).put("total_likes", temp + "");
                                    } else {
                                        listSharedPhotos.get(i).put("total_likes", 1 + "");
                                    }

                                }
                            } else {
                                /* Remove id from String */
                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if (totalLikes != null && totalLikes.length() > 0) {
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp - 1;
                                    listSharedPhotos.get(i).put("total_likes", (temp == 0 ? "" : temp) + "");
                                } else {
                                    listSharedPhotos.get(i).put("total_likes", "");
                                }
                            }

                            break;
                        case 1:
                            param = "isloved";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if (totalLikes != null && totalLikes.length() > 0) {
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp + 1;
                                        listSharedPhotos.get(i).put("total_likes", temp + "");
                                    } else {
                                        listSharedPhotos.get(i).put("total_likes", 1 + "");
                                    }

                                }
                            } else {

                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if (totalLikes != null && totalLikes.length() > 0) {
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp - 1;
                                    listSharedPhotos.get(i).put("total_likes", (temp == 0 ? "" : temp) + "");
                                } else {
                                    listSharedPhotos.get(i).put("total_likes", "");
                                }
                            }
                            break;
                        case 2:
                            param = "ishaha";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if (totalLikes != null && totalLikes.length() > 0) {
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp + 1;
                                        listSharedPhotos.get(i).put("total_likes", temp + "");
                                    } else {
                                        listSharedPhotos.get(i).put("total_likes", 1 + "");
                                    }

                                }
                            } else {

                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if (totalLikes != null && totalLikes.length() > 0) {
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp - 1;
                                    listSharedPhotos.get(i).put("total_likes", (temp == 0 ? "" : temp) + "");
                                } else {
                                    listSharedPhotos.get(i).put("total_likes", "");
                                }

                            }
                            break;
                        case 3:
                            param = "iswow";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if (totalLikes != null && totalLikes.length() > 0) {
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp + 1;
                                        listSharedPhotos.get(i).put("total_likes", temp + "");
                                    } else {
                                        listSharedPhotos.get(i).put("total_likes", 1 + "");
                                    }

                                }
                            } else {

                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if (totalLikes != null && totalLikes.length() > 0) {
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp - 1;
                                    listSharedPhotos.get(i).put("total_likes", (temp == 0 ? "" : temp) + "");
                                } else {
                                    listSharedPhotos.get(i).put("total_likes", "");
                                }
                            }
                            break;
                        case 4:
                            param = "isangry";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if (totalLikes != null && totalLikes.length() > 0) {
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp + 1;
                                        listSharedPhotos.get(i).put("total_likes", temp + "");
                                    } else {
                                        listSharedPhotos.get(i).put("total_likes", 1 + "");
                                    }

                                }
                            } else {
                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if (totalLikes != null && totalLikes.length() > 0) {
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp - 1;
                                    listSharedPhotos.get(i).put("total_likes", (temp == 0 ? "" : temp) + "");
                                } else {
                                    listSharedPhotos.get(i).put("total_likes", "");
                                }
                            }
                            break;
                        case 5:
                            param = "issad";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if (totalLikes != null && totalLikes.length() > 0) {
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp + 1;
                                        listSharedPhotos.get(i).put("total_likes", temp + "");
                                    } else {
                                        listSharedPhotos.get(i).put("total_likes", 1 + "");
                                    }

                                }
                            } else {

                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if (totalLikes != null && totalLikes.length() > 0) {
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp - 1;
                                    listSharedPhotos.get(i).put("total_likes", (temp == 0 ? "" : temp) + "");
                                } else {
                                    listSharedPhotos.get(i).put("total_likes", "");
                                }
                            }
                            break;
                    }
                }
                AdpSyncedPhotoNew recyclerAdp = (AdpSyncedPhotoNew) mRecyclerView.getAdapter();
                recyclerAdp.setData(listSharedPhotos, page, pageLimit);
            }
        }
    }

    class addLike extends AsyncTask<Void, String, String> {

        int LikeStatus, likeUnlike;
        String photoId, response;
        String userId;

        /*likeUnlike:  0 means will call unlike services
         * 1 means will call like services */
        private addLike(int LikeStatus, String photoId, int likeUnlike) {
            this.LikeStatus = LikeStatus;
            this.photoId = photoId;
            this.likeUnlike = likeUnlike;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "likephoto");
                jData.put("user_id", "" + userId);
                jData.put("photo_id", "" + photoId);
                jData.put("like_status", "" + LikeStatus);

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
            if (Util.isOnline(getActivity())) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    int status = jObj.optInt("success");
                    String msg = jObj.optString("msg");
//                    Toast.makeText(getActivity(), msg + "", Toast.LENGTH_LONG).show();

                    String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                    /*new getPhotos(userId, true).execute();*/
//                    refreshList();
                   /*for (int i = 0; i < listSharedPhotos.size(); i++) {
                        if (listSharedPhotos.get(i).get("photo_id") == photoId) {
                            for (int l = 0; l < 6; l++) {
                                String param = "";
                                String userIds = "";
                                String[] tempIds;
                                switch (l) {
                                    case 0:

                                        param = "isliked";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");


                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }

                                        break;
                                    case 1:
                                        param = "isloved";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                    case 2:
                                        param = "ishaha";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                    case 3:
                                        param = "iswow";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                    case 4:
                                        param = "isangry";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {
                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                    case 5:
                                        param = "issad";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                }
                            }

                            recyclerAdp.setData(listSharedPhotos, page, pageLimit);
                        }
                    }*/
//                    setLikeViews(userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class addFavs extends AsyncTask<Void, String, String> {

        String photoId, response;
        String userId;

        /*likeUnlike:  0 means will call unlike services
         * 1 means will call like services */
        private addFavs(String photoId) {
            this.photoId = photoId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "favouritephoto");
                jData.put("user_id", "" + userId);
                jData.put("photo_id", "" + photoId);

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
            if (Util.isOnline(getActivity())) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    int status = jObj.optInt("success");
                    String msg = jObj.optString("msg");
                    Toast.makeText(getActivity(), msg + "", Toast.LENGTH_LONG).show();
                   /* if (status == 1) {
                        *//* Liked *//*
                    } else {
                        *//* Liked *//*
                    }
                    *//*new getPhotos(userId, true).execute();*//*
                    refreshList();*/
                    int likedUnliked = 0;
                    if (status == 1) {
                        /* Liked */
                        likedUnliked = 1;
                    } else {
                        /* Unliked */
                        likedUnliked = 0;
                    }
                    for (int i = 0; i < listSharedPhotos.size(); i++) {
                        if (listSharedPhotos.get(i).get("photo_id") == photoId) {
                            listSharedPhotos.get(i).put("favourite_photo", "" + likedUnliked);
                            AdpSyncedPhotoNew recyclerAdp = (AdpSyncedPhotoNew) mRecyclerView.getAdapter();
                            recyclerAdp.setData(listSharedPhotos, page, pageLimit);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class deletePhoto extends AsyncTask<Void, String, String> {

        String photoid, album_id;
        int pos;
        String response;

        private deletePhoto(String album_id, String photoid, int pos) {
            this.album_id = album_id;
            this.photoid = photoid;
            this.pos = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            CustomDialog.getInstance().show(getActivity(), "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "deletephoto");
                jData.put("userid", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
                jData.put("album_id", "" + album_id);
                jData.put("photoid", "" + photoid);


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
            try {
                JSONObject jObj = new JSONObject(result);
                int status = jObj.optInt("success");

                if (status == 1) {
                    delete(pos);
//                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTOS, "");
                    Intent intent1 = new Intent();
                    intent1.setAction(getActivity().ACTIVITY_SERVICE);
                    intent1.putExtra("type", 0);
                    getActivity().sendBroadcast(intent1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    class Share extends AsyncTask<String, String, Uri> {

        String img;

        public Share(String img) {
            this.img = img;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            CustomDialog.getInstance().show(getActivity(), "");
        }

        @Override
        protected Uri doInBackground(String... params) {
            Log.d("Tests", "Testing graph API wall post");
            String response = null;
            Uri uri = null;
            try {
                Bitmap bmp = Util.getBitmapFromURL("" + img);
                uri = Util.getImageUri(getActivity(), bmp);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return uri;
        }

        @Override
        protected void onPostExecute(Uri response) {
            super.onPostExecute(response);
//            CustomDialog.getInstance().hide();
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("image/*");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Constant.AppName);
            emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, response);
            startActivity(Intent.createChooser(emailIntent, "Share"));
        }

    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    public void delete(int position) {
        listSharedPhotos.remove(position);
        AdpSyncedPhotoNew recyclerAdp = (AdpSyncedPhotoNew) mRecyclerView.getAdapter();
        if (recyclerAdp != null && recyclerAdp.getItemCount() > 0) {
            recyclerAdp.setData(listSharedPhotos, page, pageLimit);
        } else {

            recyclerAdp = new AdpSyncedPhotoNew(getActivity(), listSharedPhotos, page, pageLimit, instance);
            mRecyclerView.setAdapter(recyclerAdp);
        }
        setNoData();
    }

    class getAlbums extends AsyncTask<Void, String, String> {

        String response;
        String userId;

        private getAlbums(String userId) {
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            CustomDialog.getInstance().show(getActivity(), "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "getalbum");
                jData.put("userid", "" + userId);
                jData.put("page", "" + "");

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

            if (Util.isOnline(getActivity())) {
                try {

                    if (result != null) {
                        JSONObject jObj = new JSONObject(result);
                        int status = jObj.optInt("success");

                        if (status == 1) {
                            Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Albums, "" + result);
                            final JSONArray jsonArray = jObj.getJSONArray("data");
                            final JSONObject jsonObject = jsonArray.optJSONObject(0);

                            if (jsonObject.optString("album_id").equalsIgnoreCase(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID))) {

                            } else {

                                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID, jsonObject.optString("album_id"));
                                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName, jsonObject.optString("album_name"));
                                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_AdminId, jsonObject.optString("album_name"));
                                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_CREATEDBYME, jsonObject.optInt("created_by_me") + "");
                                write(Constant.SHRED_PR.KEY_RELOAD_Home, "1");
                                write(Constant.SHRED_PR.KEY_RELOAD_Albums, "1");

                                com.flashtr.HomeActivity.selectedPosition = 0;
                                String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
                                String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);

                                tvAlbubName.setText(albumName);
                                refreshList();
                                // Todo: set name and album at top on camera
                                CaptureActivity.getInstance().setName();
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
            /* Todo change fragment after successfully applied for fragment */
//            finish();

        }
    }

    public void reloadOfflinePhoto() {
        try {
            listRecentPhotos.clear();
            String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);

            File[] files = new File(getActivity().getExternalCacheDir().toString() + "/Album/" + albumId + "/").listFiles();
            File[] files1 = new File(getActivity().getExternalCacheDir().toString() + "/Albums/" + albumId + "/").listFiles();

            files = SortFiles(files);
            files1 = SortFiles(files1);

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    String[] sep = files[i].toString().split("/");
                    String filename = sep[sep.length - 1];
                    for (int j = 0; j < files1.length; j++) {
                        String[] sep1 = files1[j].toString().split("/");
                        String filename1 = sep1[sep1.length - 1];
                        if (filename.equals(filename1)) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("user_id", "");
                            hashMap.put("date", "");
                            hashMap.put("album_id", "" + albumId);
                            hashMap.put("photo", "" + files[i].toString());
                            hashMap.put("photo1", "" + files1[j].toString());
                            hashMap.put("shared", "0");
                            hashMap.put("isShowinList", "1");

                            /* Check description of MyApp.uploadingImage variable in Myapp file*/
                            if (!MyApp.uploadingImage.contains(files[i].toString()))
                                listRecentPhotos.add(hashMap);

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerAdpRecent = (AdpRecentPic) lvRecentPhotos1.getAdapter();
        if (recyclerAdpRecent != null && recyclerAdpRecent.getItemCount() > 0) {
            recyclerAdpRecent.setListItem(listRecentPhotos);
        } else {

            recyclerAdpRecent = new AdpRecentPic(this, listRecentPhotos, tvCount, instance);
            lvRecentPhotos1.setAdapter(recyclerAdpRecent);
        }

        photoCnt = listRecentPhotos.size();
        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
        tvCount.setText("" + photoCnt + " " + getActivity().getResources().getString(R.string.photos_awaiting));

       /* if(lvRecentPhotos1.getVisibility() == View.VISIBLE || gridRecentPhotos.getVisibility() == View.VISIBLE){
            tvCount.setVisibility(View.VISIBLE);
        }else{
            tvCount.setVisibility(View.GONE);
        }*/

        setNoData();

    }

    public File[] SortFiles(File[] files) {

        List<File> f = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            f.add(files[i]);
        }

        Collections.sort(f, new Comparator<File>() {
            public int compare(File o1, File o2) {
                if (new Date(o1.lastModified()) == null || new Date(o2.lastModified()) == null)
                    return 0;
                return new Date(o2.lastModified()).compareTo(new Date(o1.lastModified()));
            }
        });

        File[] f1 = new File[f.size()];
        for (int i = 0; i < f.size(); i++) {
            f1[i] = f.get(i);
        }

        return f1;
    }


    public void setNoData() {
        if (!isOnline) {
            tvCount.setVisibility(View.GONE);
            if (listSharedPhotos == null || listSharedPhotos.isEmpty()) {
                swipeRefreshLayout1.setVisibility(View.GONE);
                gridSharedPhotos.setVisibility(View.GONE);
                if (Util.isOnline(getActivity())) {
                    tvError.setVisibility(View.VISIBLE);
                    tvErrorTxt.setVisibility(View.GONE);
                } else {
                    tvError.setVisibility(View.GONE);
                    tvErrorTxt.setVisibility(View.VISIBLE);
                    tvErrorTxt.setText("No Internet Connection...");
                }

            } else {
                tvError.setVisibility(View.GONE);
                tvErrorTxt.setVisibility(View.GONE);
                if (MyApp.gridSelected == 0) {
                    swipeRefreshLayout1.setVisibility(View.VISIBLE);
                    gridSharedPhotos.setVisibility(View.GONE);
                } else {
                    swipeRefreshLayout1.setVisibility(View.GONE);
                    gridSharedPhotos.setVisibility(View.VISIBLE);
                }

            }
        } else {
            if (listRecentPhotos == null || listRecentPhotos.isEmpty()) {
                lvRecentPhotos1.setVisibility(View.GONE);
                gridRecentPhotos.setVisibility(View.GONE);
                tvCount.setVisibility(View.GONE);
                if (Util.isOnline(getActivity())) {
                    tvError.setVisibility(View.VISIBLE);
                    tvErrorTxt.setVisibility(View.GONE);
                } else {

                    tvError.setVisibility(View.GONE);
                    tvErrorTxt.setVisibility(View.VISIBLE);
                    tvErrorTxt.setText("No Internet Connection...");
                }
            } else {
                tvCount.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);
                tvErrorTxt.setVisibility(View.GONE);
                if (MyApp.gridSelected == 0) {
                    lvRecentPhotos1.setVisibility(View.VISIBLE);
                    gridRecentPhotos.setVisibility(View.GONE);
                } else {
                    lvRecentPhotos1.setVisibility(View.GONE);
                    gridRecentPhotos.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void LodingShow() {
        tvError.setVisibility(View.GONE);
        tvErrorTxt.setVisibility(View.VISIBLE);
        tvErrorTxt.setText("Loading...");
        lvRecentPhotos1.setVisibility(View.GONE);
        gridRecentPhotos.setVisibility(View.GONE);
        swipeRefreshLayout1.setVisibility(View.GONE);
        gridSharedPhotos.setVisibility(View.GONE);

    }

    private Bitmap rotatephoto(String image_absolute_path){
        Bitmap bitmap=BitmapFactory.decodeFile(image_absolute_path);
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(image_absolute_path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotate(bitmap, 90);

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotate(bitmap, 180);

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotate(bitmap, 270);

                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    return flip(bitmap, true, false);

                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    return flip(bitmap, false, true);

                default:
                    return bitmap;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String saveBitmap(Bitmap bt) {

        Random r = new Random();
        int Low = 10;
        int High = 1000000;
        int Result = r.nextInt(High - Low) + Low;

        File pictureFile;


        /** Create a File for saving an image or video */
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getActivity().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());

        String mImageName = "MI_" + timeStamp + "_" + Result + ".jpg";
        pictureFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);


        if (pictureFile == null) {
            Log.d("FLASHTR",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return "";
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bt.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("FLASHTR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("FLASHTR", "Error accessing file: " + e.getMessage());
        }


        return pictureFile.getAbsolutePath();
    }
    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}