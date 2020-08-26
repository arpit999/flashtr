package com.flashtr.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flashtr.HomeActivity;
import com.flashtr.R;
import com.flashtr.SelectAlbumActivity;
import com.flashtr.UploadPhotoIntentService;
import com.flashtr.activity.TabActivity;
import com.flashtr.myInterface.OnBackApiResponse;
import com.flashtr.util.CameraSurfaceView;
import com.flashtr.util.Constant;
import com.flashtr.util.SimpleOrientationListener;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CameraFragment extends BaseFragment implements CameraSurfaceView.onPictureTakenCallback {
    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    public static int photoCnt = 0;
    private static CameraFragment instance;
    /*@InjectView(R.id.tvPhotoCount)
    TextView tvPhotoCount;*/
    @InjectView(R.id.tvAlbumName)
    TextView tvAlbumName;
    @InjectView(R.id.tbShare)
    ImageView tbShare;
    boolean isChecked = false;
    @InjectView(R.id.txtShare)
    TextView txtShare;
    @InjectView(R.id.rlMenu)
    RelativeLayout rlMenu;
    @InjectView(R.id.rlLogo)
    RelativeLayout rlLogo;
    @InjectView(R.id.rlCapture)
    RelativeLayout rlCapture;
    /*@InjectView(R.id.flPhotoCount)
    FrameLayout flPhotoCount;*/
    @InjectView(R.id.preview_view)
    CameraSurfaceView mCameraPreview;
    @InjectView(R.id.ibCameraClick)
    ImageButton ibCameraClickl;
    @InjectView(R.id.vShutter)
    View vShutter;
    @InjectView(R.id.optionIC3)
    ImageView mImgFlash;
    @InjectView(R.id.txtOpt3)
    TextView txtOpt3;

    @InjectView(R.id.optionIC2)
    ImageView mImgFront;
    @InjectView(R.id.txtOpt2)
    TextView txtOpt2;

    private View mainView;
    TabActivity parent;

    @InjectView(R.id.txtCounter)
    TextView txtCounter;

//    @InjectView(R.id.tvPhotoCntStatus)
//    TextView tvPhotoCntStatus;

    private RotateAnimation rotateAnim;
    @InjectView(R.id.option)
    ImageView option;
    @InjectView(R.id.txtOption)
    TextView mTxtOption;

    String TAG = "CaptureActivity";
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private int deviceWidth;
    private int deviceHeight;
    private int orientation = 0;  // 0=portrait and 1=landscape
    private SimpleOrientationListener orientationListener;
    private Bitmap image_to_camera_folder;
    private boolean isPreviewing = false;

    int countTime = 10;
    TimerTask timerTask;
    private boolean isTimerStart = false;
    private boolean isCounteDown = false;

    public static CameraFragment newInstance() {
        CameraFragment cameraFragment = new CameraFragment();
        return cameraFragment;
    }

    public static CameraFragment getInstance() {
        return instance;
    }

    private int getRoatationAngle(int cameraId) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();

        Camera.getCameraInfo(cameraId, info);

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (cameraId == 1) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private Bitmap rotate(Bitmap bitmap, int degree) {
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();

        Log.d(TAG, "rotate: w = "+w+" h = "+h);

        final Matrix matrix = new Matrix();
        matrix.postRotate(degree, w / 2, h / 2);
        matrix.postScale(0.5f, 0.5f);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        mainView = inflater.inflate(R.layout.fragment_camera, container, false);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getView().setPadding(0, Util.getStatusBarHeight(getActivity()), 0, 0);

        ButterKnife.inject(this, mainView);
        instance = this;
//        CameraFragment.getInstance().setFlash();
        parent = (TabActivity) getActivity();
//        parent.showTabLayout(false);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        onMenuPressed();

        CameraFragment.getInstance().setLayoutParams(false);
        CameraFragment.getInstance().getmCameraPreview().startCamera();
        CameraFragment.getInstance().setLayoutParams(false);
        mCameraPreview.setOnPictureTakenCallback(this);


    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);

        if (visible && isResumed()) {   // only at fragment screen is resumed
            registerSensor();

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (mCameraPreview != null) {
//                        setLayoutParams(false);
//                        mCameraPreview.startPreview();
//                        setLayoutParams(false);
//                    }
//                }
//            }, 500);

            if (tvAlbumName != null) {
                String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
                if (albumName.length() > 0)
                    tvAlbumName.setText("" + albumName);
                else tvAlbumName.setText("No Album");
            }
        } else if (!visible) {
            unRegisterSensor();
            if (mCameraPreview != null) {
                mCameraPreview.stopPreview();
                setLayoutParams(false);
            }
        }
    }

    @Override
    public void onResume() {
        Log.e(getClass().getSimpleName(), "camera fragment resume");

        registerSensor();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final ViewTreeObserver vto = mCameraPreview.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        deviceWidth = mCameraPreview.getMeasuredWidth();
                        deviceHeight = mCameraPreview.getMeasuredHeight();
                        vto.removeOnGlobalLayoutListener(this);
                    }
                });

                /*if (mCameraPreview != null) {
                    setLayoutParams(true);
                    mCameraPreview.startPreview();

                }*/


                rlTimer.setVisibility(View.INVISIBLE);
                rlFrontCamera.setVisibility(View.INVISIBLE);
                rlFlash.setVisibility(View.INVISIBLE);

                option.setImageResource(R.drawable.option);
                mTxtOption.setTextColor(getResources().getColor(R.color.white));

                rotateAnim = new RotateAnimation(45, 0, option.getHeight() / 2, option.getWidth() / 2);
                rotateAnim.setDuration(200);
                rotateAnim.setInterpolator(new OvershootInterpolator());
                rotateAnim.setFillAfter(true);
                option.startAnimation(rotateAnim);

                setLayoutParams(false);
                mCameraPreview.destroyCamera();
                mCameraPreview.startCamera();
                mCameraPreview.startPreview();
                setLayoutParams(false);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCameraPreview != null) {
                            mCameraPreview.stopPreview();
                            setLayoutParams(false);
                            mCameraPreview.destroyCamera();
                            mCameraPreview.startCamera();
                            mCameraPreview.startPreview();
                            setLayoutParams(false);
                        }
                    }
                },200);

                try {
                    photoCnt = Util.getOfflinePic(getActivity());
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
//                    tvPhotoCount.setText("" + photoCnt);
                    //go on as normal
                } catch (NumberFormatException e) {
                    //handle error
                }

            }
        }, 500);


        super.onResume();
    }


    private void registerSensor() {

        if (orientationListener != null) {
            orientationListener.enable();
        }
    }

    private void unRegisterSensor() {
        if (orientationListener != null) {
            orientationListener.disable();
        }
    }

    @Override
    public void onPause() {
        unRegisterSensor();
        if (mCameraPreview != null) {
            mCameraPreview.destroyCamera();
        }
        super.onPause();
    }

    @InjectView(R.id.rlTimer)
    LinearLayout rlTimer;
    @InjectView(R.id.optionIC1)
    ImageView optionIC1;
    @InjectView(R.id.txtOptIc1)
    TextView txtOptIc1;
    @InjectView(R.id.rlFrontCamera)
    LinearLayout rlFrontCamera;
    @InjectView(R.id.rlFlash)
    LinearLayout rlFlash;

    Animation anticlock;

    @OnClick(R.id.rlTimer)
    public void timerclick(View v) {
        rotateAnim = null;
//        if (rlTimer.getVisibility() == View.VISIBLE) {
//            rotateAnim = new RotateAnimation(45, 0, option.getHeight() / 2, option.getWidth() / 2);
//            option.setImageResource(R.drawable.option);
//            mTxtOption.setTextColor(getResources().getColor(R.color.white));
//        } else {
//            rotateAnim = new RotateAnimation(0, 45, option.getHeight() / 2, option.getWidth() / 2);
//            option.setImageResource(R.drawable.close_button);
//            mTxtOption.setTextColor(getResources().getColor(R.color.white));
//        }
//        rotateAnim.setDuration(200);
//        rotateAnim.setInterpolator(new OvershootInterpolator());
//        rotateAnim.setFillAfter(true);
//        option.startAnimation(rotateAnim);

        if(isTimerStart){
            if(isCounteDown){
                stopTask();
            }
            isTimerStart = false;
            optionIC1.setBackgroundResource(R.drawable.rounded_solid_gray);
            txtOptIc1.setTextColor(getResources().getColor(R.color.white));
        }else{
            isTimerStart = true;

            optionIC1.setBackgroundResource(R.drawable.rounded_orange);
            txtOptIc1.setTextColor(getResources().getColor(R.color.nRippleHeaderBG));
        }
       /* anticlock = AnimationUtils.loadAnimation(getActivity(),
                R.anim.zoom_out);
        rlTimer.startAnimation(anticlock);
//            flSelectOption.startAnimation(animHide);
        rlTimer.setVisibility(View.INVISIBLE);
        rlTimer.setVisibility(View.INVISIBLE);

        rlFrontCamera.startAnimation(anticlock);
        rlFrontCamera.setVisibility(View.INVISIBLE);

        rlFlash.startAnimation(anticlock);
        rlFlash.setVisibility(View.INVISIBLE);*/

    }

    @OnClick(R.id.option)
    public void showOptions(View v) {
        rotateAnim = null;
        if (rlTimer.getVisibility() == View.VISIBLE) {
            rotateAnim = new RotateAnimation(45, 0, option.getHeight() / 2, option.getWidth() / 2);
            option.setImageResource(R.drawable.option);
            mTxtOption.setTextColor(getResources().getColor(R.color.white));
        } else {
            rotateAnim = new RotateAnimation(0, 45, option.getHeight() / 2, option.getWidth() / 2);
            option.setImageResource(R.drawable.close_button);
            mTxtOption.setTextColor(getResources().getColor(R.color.white));
        }
//        rotateAnim.setDuration(200);
//        rotateAnim.setInterpolator(new OvershootInterpolator());
//        rotateAnim.setFillAfter(true);
//        option.startAnimation(rotateAnim);

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

        if(isTimerStart){
            optionIC1.setBackgroundResource(R.drawable.rounded_orange);
            txtOptIc1.setTextColor(getResources().getColor(R.color.nRippleHeaderBG));
        }else{
            optionIC1.setBackgroundResource(R.drawable.rounded_solid_gray);
            txtOptIc1.setTextColor(getResources().getColor(R.color.white));
        }

        if (rlFrontCamera.getVisibility() == View.VISIBLE) {
//            animHide.setFillAfter(false);

            rlFlash.startAnimation(anticlock);
            rlFlash.setVisibility(View.INVISIBLE);

            rlFrontCamera.startAnimation(anticlock);
            rlFrontCamera.setVisibility(View.INVISIBLE);


            rlTimer.startAnimation(anticlock);
//            flSelectOption.startAnimation(animHide);
            rlTimer.setVisibility(View.INVISIBLE);


            option.setImageResource(R.drawable.option);
            mTxtOption.setTextColor(getResources().getColor(R.color.white));

        } else {
            // Start animation
//            animShow.setFillAfter(true);

            /*flTopLayer.setVisibility(View.VISIBLE);*/

            boolean hasFlash = getActivity().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            if(hasFlash && mCameraPreview.getCAMERA_FACING() == Camera.CameraInfo.CAMERA_FACING_BACK){
                rlFlash.setVisibility(View.VISIBLE);
            }else{
                rlFlash.setVisibility(View.INVISIBLE);
            }
//            rlTimer.setVisibility(View.VISIBLE);
//            flSelectOption.startAnimation(animShow);
            rlTimer.startAnimation(clock);

            rlFrontCamera.setVisibility(View.VISIBLE);
//            flSelectOption.startAnimation(animShow);
            rlFrontCamera.startAnimation(clock);

//            rlFlash.setVisibility(View.VISIBLE);
//            flSelectOption.startAnimation(animShow);

            option.setImageResource(R.drawable.close_button);
            mTxtOption.setTextColor(getResources().getColor(R.color.white));
            rlFlash.startAnimation(clock);

        }
    }

    /*@OnClick(R.id.flPhotoCount)
    public void flPhotoCount(View view) {
//        view.animate().x(200).setDuration(500).start();
        Toast.makeText(getActivity(), "" + photoCnt + " offline photos", Toast.LENGTH_SHORT).show();
    }*/


    @OnClick(R.id.rlMenu)
    @SuppressWarnings("unused")
    public void Menu(View view) {
        // animation on click
        if (HomeActivity.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            HomeActivity.mDrawerLayout.closeDrawers();
        } else {
            HomeActivity.mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    @OnClick(R.id.rlLogo)
    @SuppressWarnings("unused")
    public void Camera(View view) {
        Intent intent = new Intent(getActivity(), SelectAlbumActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rlFrontCamera)
    public void FrontCamera(View view) {
        setLayoutParams(false);
        mCameraPreview.changePreview(mImgFlash, txtOpt3, rlFlash);
        setLayoutParams(false);

        boolean hasFlash = getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if(mCameraPreview.getHashFlash() && mCameraPreview.getCAMERA_FACING() == Camera.CameraInfo.CAMERA_FACING_BACK){
            rlFlash.setVisibility(View.VISIBLE);
        }else{
            rlFlash.setVisibility(View.GONE);
        }
       /* if(mCameraPreview.getCAMERA_FACING() == Camera.CameraInfo.CAMERA_FACING_BACK){
            txtOpt2.setTextColor(getResources().getColor(R.color.white));
            mImgFront.setBackgroundResource(R.drawable.rounded_solid_gray);
        }else{
            txtOpt2.setTextColor(getResources().getColor(R.color.nRippleHeaderBG));
            mImgFront.setBackgroundResource(R.drawable.rounded_orange);
        }*/

    }

    @OnClick(R.id.rlCapture)
    public void Capture(View view) {
        if (isTimerStart) {
            if (isCounteDown) {
                stopTask();
            } else {
                startTimer();
            }
        } else {
            try {
                animateShutter();
                Animation blinkAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
                ibCameraClickl.startAnimation(blinkAnim);
                captureImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.rlFlash)
    public void OnOffFlash(View view) {

        rotateAnim = null;
        if (rlTimer.getVisibility() == View.VISIBLE) {
//            rotateAnim = new RotateAnimation(45, 0, option.getHeight() / 2, option.getWidth() / 2);
//            option.setImageResource(R.drawable.option);
//            mTxtOption.setTextColor(getResources().getColor(R.color.white));
        } else {
//            rotateAnim = new RotateAnimation(0, 45, option.getHeight() / 2, option.getWidth() / 2);
//            option.setImageResource(R.drawable.close_button);
//            mTxtOption.setTextColor(getResources().getColor(R.color.white));
        }
//        rotateAnim.setDuration(200);
//        rotateAnim.setInterpolator(new OvershootInterpolator());
//        rotateAnim.setFillAfter(true);
//        option.startAnimation(rotateAnim);

       /* anticlock = AnimationUtils.loadAnimation(getActivity(),
                R.anim.zoom_out);
        rlTimer.startAnimation(anticlock);
//            flSelectOption.startAnimation(animHide);
        rlTimer.setVisibility(View.INVISIBLE);
        rlTimer.setVisibility(View.INVISIBLE);

        rlFrontCamera.startAnimation(anticlock);
        rlFrontCamera.setVisibility(View.INVISIBLE);

        rlFlash.startAnimation(anticlock);
        rlFlash.setVisibility(View.INVISIBLE);*/
        mCameraPreview.turnOnOffFlash(mImgFlash, txtOpt3, rlFlash);
    }

    private void animateShutter() {
        vShutter.setVisibility(View.VISIBLE);
        vShutter.setAlpha(0.f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0f, 0.8f);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vShutter.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }


    public void setLayoutParams(boolean isReset) {
        if (!isReset) {
            mCameraPreview.setLayoutParams(new RelativeLayout.LayoutParams((int) (deviceWidth * 1.1f), (int) (deviceHeight * 1.1f)));
//            mCameraPreview.setLayoutParams(new FrameLayout.LayoutParams((int) (deviceWidth * 1.0f), (int) (deviceHeight * 1.0f)));
        } else {
            mCameraPreview.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }


    private void init() {

        if (isChecked) {
            tbShare.setBackgroundResource(R.drawable.rounded_orange);
            txtShare.setTextColor(getResources().getColor(R.color.nRippleHeaderBG));
        } else {
            tbShare.setBackgroundResource(R.drawable.rounded_solid_gray);
            txtShare.setTextColor(getResources().getColor(R.color.white));
        }

        mCameraPreview.hasFlash = getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//        if(CameraFragment.getInstance().getmCameraPreview().hasFlash){
//            mImgFlash.setVisibility(View.VISIBLE);
//            if (CameraFragment.getInstance().getmCameraPreview().isFlashOn) {
//                if (mImgFlash != null)
//                    mImgFlash.setImageResource(R.drawable.flash_off);
//                CameraFragment.getInstance().getmCameraPreview().isFlashOn = false;
//
//                // changing button/switch image
////            toggleButtonImage();
//            } else {
//                /* If you want to start camera light like a tourch then use below line */
//                /*parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);*/
//                CameraFragment.getInstance().getmCameraPreview().isFlashOn = true;
//                if (mImgFlash != null)
//                    mImgFlash.setImageResource(R.drawable.flash_off);
//                // changing button/switch image
//                //toggleButtonImage();
//            }
//        }else{
//            mImgFlash.setVisibility(View.GONE);
//        }

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");
        tvAlbumName.setTypeface(typeface);

        try {
            photoCnt = Util.getOfflinePic(getActivity());
            Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
//            tvPhotoCount.setText("" + photoCnt);
            //go on as normal
        } catch (NumberFormatException e) {
            //handle error
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        float newProportion = (float) width / (float) height;

        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        deviceWidth = size.x;
        deviceHeight = size.y;
        float screenProportion = (float) deviceWidth / (float) deviceHeight;

//        // Get the SurfaceView layout parameters
//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getActivity().findViewById(R.id.preview_view).getLayoutParams();
//        float scaleFactor = 1;
//
//        if (newProportion > screenProportion) {
//            lp.width = deviceWidth;
//            lp.height = (int) ((float) deviceWidth * (1 / newProportion));
//            scaleFactor = (deviceWidth / lp.height); // calculate the factor to make it full screen
//        } else {
//            lp.width = (int) (newProportion * (float) deviceHeight);
//            lp.height = deviceHeight;
//            scaleFactor = deviceHeight / lp.width; // calculate the factor to make it full screen.
//
//        }
//        lp.width = (int) (lp.width * scaleFactor);
//        lp.height = (int) (lp.height * scaleFactor);
//
//        mCameraPreview.setLayoutParams(lp);


        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getActivity()));

        tbShare.setOnClickListener(/*new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tbShare.setBackgroundResource(R.drawable.rounded_orange);
                    txtShare.setTextColor(getResources().getColor(R.color.white));
                } else {
                    tbShare.setBackgroundResource(R.drawable.rounded_gray);
                    txtShare.setTextColor(getResources().getColor(R.color.nRippleHeaderBG));
                }
            }
        }*/ new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked) {
                    isChecked = true;
                    tbShare.setBackgroundResource(R.drawable.rounded_orange);
                    txtShare.setTextColor(getResources().getColor(R.color.nRippleHeaderBG));
                } else {
                    isChecked = false;
                    tbShare.setBackgroundResource(R.drawable.rounded_solid_gray);
                    txtShare.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        orientationListener = new SimpleOrientationListener(getActivity()) {
            @Override
            public void onSimpleOrientationChanged(int orientation, int rotation) {
                try {
                    if (rotation >= 330 || rotation < 30) {
                        CameraFragment.this.orientation = 0;
//                    Toast.makeText(getActivity(), "portrait down", Toast.LENGTH_SHORT).show();
                    } else if (rotation >= 60 && rotation < 120) {
                        CameraFragment.this.orientation = 1;
//                    Toast.makeText(getActivity(), "landscape right", Toast.LENGTH_SHORT).show();
                    } else if (rotation >= 150 && rotation < 210) {
                        CameraFragment.this.orientation = 3;
//                    Toast.makeText(getActivity(), "portrait up", Toast.LENGTH_SHORT).show();
                    } else if (rotation >= 240 && rotation < 300) {
                        CameraFragment.this.orientation = 2;
//                    Toast.makeText(getActivity(), "landscape left", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

//        setFlash();

    }

    private void captureImage() {
        if (mCameraPreview != null) {
            mCameraPreview.takePicture();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterSensor();
        if (mCameraPreview != null) {
            mCameraPreview.destroyCamera();
        }

    }


    public void setTitle() {
        if (tvAlbumName != null) {
            String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
            if (albumName.length() > 0)
                tvAlbumName.setText("" + albumName);
            else tvAlbumName.setText("No Album");
        }
    }

    @Override
    public void onPictureTaken(byte[] bytes) {
        int angleToRotate = getRoatationAngle(mCameraPreview.getCAMERA_FACING());
        if (mCameraPreview.getCAMERA_FACING() == Camera.CameraInfo.CAMERA_FACING_FRONT)
            angleToRotate = angleToRotate + 180;

        final String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
        new SavePhotoTask(albumId, bytes, angleToRotate, isChecked).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mCameraPreview.startPreview();
    }

    public CameraSurfaceView getmCameraPreview() {
        photoCnt = Util.getOfflinePic(getActivity());
        String cnt = photoCnt + "";
        try {
            if (cnt == null) {
                cnt = "0";
//                tvPhotoCount.setText(cnt);
                Log.e("CNT", "" + cnt);
            } else {
                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
//                tvPhotoCount.setText("" + photoCnt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCameraPreview;
    }

    public void saveImageToCameraFolder(Bitmap img, String path) throws IOException {

        final String[] title = path.split("/");
        final String imageName = title[title.length - 1];

        Boolean isSDPresent = isExternalStorageAvailable();
        Log.e("isSDPresent", "" + isSDPresent);
        if (isSDPresent) {
            // yes SD-card is present
            final File root = new File(Environment.getExternalStorageDirectory()
                    + "/Pictures/" + getString(R.string.app_name));
//            File dir = new File(sdCard.getAbsolutePath() + "/Pictures");
            if (!root.exists()) root.mkdir();
            final File sourceFile = new File(path);
            final File destinationFile = new File(Environment.getExternalStorageDirectory()
                    + "/Pictures/" + getString(R.string.app_name), imageName);
            copyFile(sourceFile, destinationFile);
        } else {
            final File root = new File(Environment.getDataDirectory()
                    + "/Pictures/" + getString(R.string.app_name));
//            File dir = new File(sdCard.getAbsolutePath() + "/Pictures");
            if (!root.exists()) root.mkdir();
            final File sourceFile = new File(path);
            final File destinationFile = new File(Environment.getDataDirectory() + "/Pictures/" + getString(R.string.app_name), imageName);
            copyFile(sourceFile, destinationFile);
        }

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

            String[] paths = {destFile.getPath()};
            String[] mediaType = {"image/jpeg"};
            MediaScannerConnection.scanFile(getActivity(), paths, mediaType, null);
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }

    }

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


    private class SavePhotoTask extends AsyncTask<Void, Void, String> {

        private String albumId;
        private String strDate;
        private File file, photo, file1, photo1;
        private byte[] data;
        private int angleToRotate;
        private boolean isShareEnable = false;

        private SavePhotoTask(String albumId, byte[] data, int angleToRotate, boolean isShareEnable) {
            this.albumId = albumId;
            this.data = data;
            this.angleToRotate = angleToRotate;
            this.isShareEnable = isShareEnable;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final Date date = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            strDate = sdf.format(date);

            final File cacheDir = new File(getActivity().getExternalCacheDir().toString() + "/Album/");
            final File cacheDir1 = new File(getActivity().getExternalCacheDir().toString() + "/Albums/");
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            if (!cacheDir1.exists()) {
                cacheDir1.mkdir();
            }

//            file = new File(Environment.getExternalStorageDirectory()
//                    + "/Pictures/" + getString(R.string.app_name));
            file = new File(cacheDir.getAbsolutePath() + "/" + albumId);
            if (!file.exists()) file.mkdir();
            file1 = new File(cacheDir1.getAbsolutePath() + "/" + albumId);
            if (!file1.exists()) file1.mkdir();

        }


        @Override
        protected String doInBackground(Void... str) {

            photo = new File(file.toString(), strDate + ".jpg");
            photo1 = new File(file1.toString(), strDate + ".jpg");

            if (photo.exists()) {
                photo.delete();
            }
            if (photo1.exists()) {
                photo1.delete();
            }

            try {
                final String pictureFile = photo.getAbsolutePath();

                final BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
                sizeOptions.inJustDecodeBounds = true;
                sizeOptions.inSampleSize=1;
                sizeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                BitmapFactory.decodeByteArray(data, 0, data.length, sizeOptions);
                Log.d(TAG, "Bitmap is " + sizeOptions.outWidth + "x"
                        + sizeOptions.outHeight);

                final float widthSampling = sizeOptions.outWidth / deviceWidth;
                sizeOptions.inJustDecodeBounds = false;
//                sizeOptions.inSampleSize = (int) widthSampling;

                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length, sizeOptions);
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                fos = new FileOutputStream(pictureFile);
                if (orientation == 0) {
                    realImage = rotate(realImage, angleToRotate);
                    Log.e(getClass().getSimpleName(), "portrait");
                } else if (orientation == 1) {
                    realImage = rotate(realImage, 180);
                    Log.e(getClass().getSimpleName(), "landscape");
                } else if (orientation == 2) {
                    realImage = rotate(realImage, 0);
                    Log.e(getClass().getSimpleName(), "landscape");
                } else if (orientation == 3) {
                    realImage = rotate(realImage, 270);
                    Log.e(getClass().getSimpleName(), "portrait");
                }
                image_to_camera_folder = realImage;
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();


                if (!realImage.isRecycled())
                    realImage.recycle();

            } catch (FileNotFoundException e) {
                Log.d("Info", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("TAG", "Error accessing file: " + e.getMessage());
            }

            try {
                final String pictureFile = photo1.getPath();

                final BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
                sizeOptions.inJustDecodeBounds = true;
                sizeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                BitmapFactory.decodeByteArray(data, 0, data.length, sizeOptions);
                Log.d(TAG, "Bitmap is " + sizeOptions.outWidth + "x"
                        + sizeOptions.outHeight);

                final float widthSampling = sizeOptions.outWidth / deviceWidth;
                sizeOptions.inJustDecodeBounds = false;
                sizeOptions.inSampleSize = (int) widthSampling;

                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length, sizeOptions);
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                fos = new FileOutputStream(pictureFile);
                if (orientation == 0) {
                    realImage = rotate(realImage, angleToRotate);
                    Log.e(getClass().getSimpleName(), "portrait");
                } else if (orientation == 1) {
                    realImage = rotate(realImage, 180);
                    Log.e(getClass().getSimpleName(), "landscape");
                } else if (orientation == 2) {
                    realImage = rotate(realImage, 0);
                    Log.e(getClass().getSimpleName(), "landscape");
                } else if (orientation == 3) {
                    realImage = rotate(realImage, 270);
                    Log.e(getClass().getSimpleName(), "portrait");
                }
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                if (!realImage.isRecycled())
                    realImage.recycle();

            } catch (FileNotFoundException e) {
                Log.d("Info", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("TAG", "Error accessing file: " + e.getMessage());
            }

            return (null);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (isShareEnable) {
                if (getActivity() != null && Util.isOnline(getActivity())) {
                    String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                    Intent intent = new Intent(getActivity(), UploadPhotoIntentService.class);
                    intent.putExtra(Constant.SHRED_PR.KEY_USERID, userId);
                    intent.putExtra(Constant.SHRED_PR.KEY_Current_AlbumID, albumId);
                    intent.putExtra(Constant.SHRED_PR.KEY_Photo_Name, photo.getAbsolutePath());
                    intent.putExtra(Constant.SHRED_PR.KEY_Data, CameraFragment.class.getSimpleName());
                    getActivity().startService(intent);
                }
            }
            // save image to camera folder sdcard
            try {
                saveImageToCameraFolder(image_to_camera_folder, photo.getAbsolutePath());


                Log.e("SAVE", "Image Save To Camera Folder");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (getActivity() != null) {
                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home, "5");
                Toast.makeText(getActivity(), "photo captured", Toast.LENGTH_SHORT).show();
                if (!isChecked) {
                    photoCnt++;
//                    tvPhotoCount.setText("" + photoCnt);
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
                }

            }
        }
    }

    private void onMenuPressed() {
        mainView.setFocusableInTouchMode(true);
        mainView.requestFocus();

        mainView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    try {
                        animateShutter();
                        Animation blinkAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
                        ibCameraClickl.startAnimation(blinkAnim);
                        captureImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    Toast.makeText(getActivity(),"Pressed Volume",Toast.LENGTH_LONG).show();
                    return true;
                }
//                if(event.getAction() == KeyEvent.KEYCODE_BACK){
//                    Toast.makeText(getActivity(),"Pressed Back",Toast.LENGTH_LONG).show();
//                    return true;
//                }
                return false;
            }
        });
    }

    public void setFlash() {
        mCameraPreview.hasFlash = getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (mCameraPreview.hasFlash && mCameraPreview.getCAMERA_FACING() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            rlFlash.setVisibility(View.VISIBLE);
            if (mCameraPreview.isFlashOn) {
                txtOpt3.setTextColor(getResources().getColor(R.color.nRippleHeaderBG));
                txtOpt3.setText("FLASH ON");
                mImgFlash.setBackgroundResource(R.drawable.rounded_orange);
                mImgFlash.setImageResource(R.drawable.flash);
            } else {
                txtOpt3.setTextColor(getResources().getColor(R.color.white));
                txtOpt3.setText("FLASH OFF");
                mImgFlash.setImageResource(R.drawable.rounded_solid_gray);
                mImgFlash.setImageResource(R.drawable.flash);
            }
        } else {
            rlFlash.setVisibility(View.INVISIBLE);
        }
    }

    public void startTimer() {
        txtCounter.setVisibility(View.VISIBLE);
        isCounteDown = true;
//        switch (mediaType) {
//            case Constants.MEDIA_AUDIO:
//                txt_counter_audio.setText("00:00");
//                txt_counter_audio.setVisibility(View.VISIBLE);
//                break;
//            case Constants.MEDIA_VIDEO:
//                txt_remian_time.setText(Html.fromHtml(getActivity().getResources().getString(R.string.sec1)));
//                setTimerText(10);
//                txt_counter_video.setVisibility(View.VISIBLE);
//                break;
//        }

        final Handler handler = new Handler();
        Timer ourtimer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
//
                        if (countTime < 1) {
                            stopTimer();
                        } else {
                            countTime--;
                        }
//                        Toast.makeText(getActivity(), countTime + "", Toast.LENGTH_SHORT).show();
                        txtCounter.setText((countTime)+"");

                    }
                });
            }
        };

        ourtimer.schedule(timerTask, 0, 1000);
    }

    /**
     * Stop Recording timer
     */
    public void stopTimer() {
        stopTask();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
//                txt_counter_audio.setVisibility(View.INVISIBLE);
//                txt_counter_video.setVisibility(View.INVISIBLE);
                try {
                    animateShutter();
                    Animation blinkAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
                    ibCameraClickl.startAnimation(blinkAnim);
                    captureImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    public void stopTask(){
        txtCounter.setText("");
        txtCounter.setVisibility(View.GONE);
        isCounteDown = false;
        if (timerTask != null) {
            timerTask.cancel();
            countTime = 10;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getActivity().finish();
    }
}