package com.flashtr.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.R;
import com.flashtr.app.MyApp;
import com.flashtr.asyncTask.uploadPhotoTask;
import com.flashtr.fragment.BaseFragment;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.util.Constant;
import com.flashtr.util.SimpleOrientationListener;
import com.flashtr.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Custom class to capture image using camera object. Also has facility to use
 * front and back camera and allow user to on/off flash.
 */
@SuppressWarnings("deprecation")
public class CaptureActivity extends BaseFragment {

    private static final String TAG = "CaptureActivity";
    // XML Components
    private ImageView imgCapture, img_thumb, imgPreview;
    private LinearLayout lin_swape_cam, linHeader;
    LinearLayout lin_mainHeader;
    //   Button btnUsePhoto, btnCancelRetake;
    TextView tvAlbubName;

    // Camera related variables
    private Camera camera = null;
    private SurfaceHolder previewHolder = null;
    private int deviceWidth;
    private SurfaceView preview = null;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;

    private String pathImage = "";

    /**
     * This varible is used in on resume to initialize camera preview again
     */
    private int cameraWidth, cameraHeight;
    private int deviceWidth1, deviceHeight1;

    /**
     * This variable is indicate wheather flash is on or off Mainly this
     * variable is used on mode change.
     */
    private boolean isFlashOn = false;
    private boolean isFlashAvail = false;

    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private DisplayMetrics metrics = new DisplayMetrics();
    int displayRotation;
    Uri finalPath;

    private int MAX_WIDTH, MAX_HEIGHT;

    public boolean isCaptured = false;
    public View mainView;
    private float mDist;
    private LinearLayout rlShare, rlOption;

    ImageView tbShare;
    boolean isChecked = false;
    TextView txtShare;
    TextView txtOpt3;
    ImageView mImgFlash;

    private RotateAnimation rotateAnim;
    public static int photoCnt = 0;
    Animation anticlock;
    ImageView option;
    TextView mTxtOption;

    LinearLayout rlTimer;
    LinearLayout rlFrontCamera;
    LinearLayout rlFlash;

    ImageView optionIC1;
    private boolean isTimerStart = false;
    TextView txtOptIc1;

    private boolean isCounteDown = false;
    int countTime = 10;
    TimerTask timerTask;
    TextView txtCounter;
    private Bitmap image_to_camera_folder;
    private int orientation = 0;  // 0=portrait and 1=landscape

    public String model_name="";

    private SimpleOrientationListener orientationListener;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (Settings.System.getInt(getActivity().getContentResolver(),
//                Settings.System.ACCELEROMETER_ROTATION, 0) == 0) {
//            Settings.System.putInt(getActivity().getContentResolver(),
//                    Settings.System.ACCELEROMETER_ROTATION, 1);
//        }
//    }

    private static CaptureActivity instance;

    public static CaptureActivity getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        if (Settings.System.getInt(getActivity().getContentResolver(),
//                Settings.System.ACCELEROMETER_ROTATION, 0) == 0) {
//            Settings.System.putInt(getActivity().getContentResolver(),
//                    Settings.System.ACCELEROMETER_ROTATION, 1);
//        }
        mainView = inflater.inflate(R.layout.ac_full_screen_capture, container, false);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mainView.setPadding(0, Util.getStatusBarHeight(getActivity()), 0, 0);

        setView();

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
        instance = this;
        return mainView;
    }

    private void setView() {

//        setContentView(R.layout.ac_full_screen_capture);
        /* This solution is aplied only when moto e device is there.
           If another device is there than apply another if condition */
		model_name = getDeviceName();
//        Toast.makeText(getActivity(),model_name+"",Toast.LENGTH_LONG).show();
        setScreenRatio();
        /*set full screen*/
        /*getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        MAX_WIDTH = metrics.widthPixels;
        MAX_HEIGHT = metrics.heightPixels;

        Log.d(TAG, "setView --> layout(w,h)  MAX_WIDTH = "+MAX_WIDTH+" MAX_HEIGHT = "+MAX_HEIGHT);
        /**
         * If any Exception(App Crash) fire then automatically mail send that
         * Exception
         */
        // Thread.setDefaultUncaughtExceptionHandler(new
        // ExceptionHandler(this));
        pathImage = getActivity().getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
        // Initialize variables
        init();
        onMenuPressed();

        /** Check availability of flash light */
        isFlashAvail = getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH);

        setViewInViewPager();
        if (finalPath != null) {
            img_thumb.setImageURI(finalPath);
            imgPreview.setImageURI(finalPath);

        }
    }

    /**
     * Initialize all xml file components and variables
     */
    private void init() {

		/*  Initalize cameraWidth and cameraHeight Here instanceof surfaceChanged because
            surfaceChanged method device device height not get exact, So surfaceview is streched */
        int screenWH[] = Util.getScreenWidthHeight(getActivity());
        cameraWidth = screenWH[0];
        cameraHeight = screenWH[1] + getNavigationBarHeight(Configuration.ORIENTATION_PORTRAIT);

        Log.d(TAG, "init: Camera(w,h) cameraWidth = "+cameraWidth+" cameraHeight = "+cameraHeight);

        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        deviceWidth1 = size.x;
        deviceHeight1 = size.y;

        Log.d("", "init: device(w,h) deviceWidth1 = "+deviceWidth1+ " deviceHeight1 = "+deviceHeight1);
        /** Create Database if not exists */
        try {
//            btnUsePhoto = (Button) mainView.findViewById(R.id.btnUsePhoto);
//            btnCancelRetake = (Button) mainView.findViewById(R.id.btnCancelRetake);
            img_thumb = (ImageView) mainView.findViewById(R.id.img_thumb);
            imgPreview = (ImageView) mainView.findViewById(R.id.imgPreview);
            imgCapture = (ImageView) mainView.findViewById(R.id.imgCapture);
            tvAlbubName = (TextView) mainView.findViewById(R.id.tvAlbubName);

            lin_swape_cam = (LinearLayout) mainView.findViewById(R.id.lin_swape_cam);
            linHeader = (LinearLayout) mainView.findViewById(R.id.linHeader);
            lin_mainHeader = (LinearLayout) mainView.findViewById(R.id.lin_mainHeader);
            rlShare = (LinearLayout) mainView.findViewById(R.id.rlShare);
            rlOption = (LinearLayout) mainView.findViewById(R.id.rlOption);
            tbShare = (ImageView) mainView.findViewById(R.id.tbShare);
            txtShare = (TextView) mainView.findViewById(R.id.txtShare);
            option = (ImageView) mainView.findViewById(R.id.option);
            mTxtOption = (TextView) mainView.findViewById(R.id.txtOption);
            rlTimer = (LinearLayout) mainView.findViewById(R.id.rlTimer);
            rlFrontCamera = (LinearLayout) mainView.findViewById(R.id.rlFrontCamera);
            rlFlash = (LinearLayout) mainView.findViewById(R.id.rlFlash);
            optionIC1 = (ImageView) mainView.findViewById(R.id.optionIC1);
            txtOptIc1 = (TextView) mainView.findViewById(R.id.txtOptIc1);
            txtCounter = (TextView) mainView.findViewById(R.id.txtCounter);
            txtOpt3 = (TextView) mainView.findViewById(R.id.txtOpt3);
            mImgFlash = (ImageView) mainView.findViewById(R.id.optionIC3);

            String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
            tvAlbubName.setText(albumName);

            //LinearLayout lin_done = (LinearLayout) findViewById(R.id.lin_done);
//            btnUsePhoto.setEnabled(false);
//            btnUsePhoto.setVisibility(View.INVISIBLE);

            /** if phone has only one camera, hide "switch camera" button */
            if (Camera.getNumberOfCameras() == 1) {
                lin_swape_cam.setVisibility(View.INVISIBLE);
            }
            try {
                photoCnt = Util.getOfflinePic(getActivity());
                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
//            tvPhotoCount.setText("" + photoCnt);
                //go on as normal
            } catch (NumberFormatException e) {
                //handle error
            }

            /** Initialize surfasce view for custom camera */
            preview = (SurfaceView) mainView.findViewById(R.id.preview);

            preview.setZOrderOnTop(false);
            previewHolder = preview.getHolder();
            previewHolder.addCallback(surfaceCallback);
            previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            if (isChecked) {
                tbShare.setBackgroundResource(R.drawable.rounded_orange);
                txtShare.setTextColor(getResources().getColor(R.color.nBtnCameraOption));
            } else {
                tbShare.setBackgroundResource(R.drawable.rounded_solid_gray);
                txtShare.setTextColor(getResources().getColor(R.color.white));
            }

            preview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return onTouchEvent(event);
                }
            });

            lin_mainHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickEvent(v);
                }
            });

            rlShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickEvent(v);
                }
            });
            rlOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickEvent(v);
                }
            });

            rlTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickEvent(v);
                }
            });

            rlFrontCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickEvent(v);
                }
            });

            rlFlash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickEvent(v);
                }
            });

            imgCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    captureImage();
                }
            });

            orientationListener = new SimpleOrientationListener(getActivity()) {
                @Override
                public void onSimpleOrientationChanged(int orientation1, int rotation1) {
                    try {
                        if (rotation1 >= 330 || rotation1 < 30) {
                            orientation = 0;
//                    Toast.makeText(getActivity(), "portrait down", Toast.LENGTH_SHORT).show();
                        } else if (rotation1 >= 60 && rotation1 < 120) {
                            orientation = 1;
//                    Toast.makeText(getActivity(), "landscape right", Toast.LENGTH_SHORT).show();
                        } else if (rotation1 >= 150 && rotation1 < 210) {
                            orientation = 3;
//                    Toast.makeText(getActivity(), "portrait up", Toast.LENGTH_SHORT).show();
                        } else if (rotation1 >= 240 && rotation1 < 300) {
                            orientation = 2;
//                    Toast.makeText(getActivity(), "landscape left", Toast.LENGTH_SHORT).show();
                        }

                        Log.d("TAG", "onSimpleOrientationChanged : " + orientation1 + " ori: " + orientation + " rot: " + rotation1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onDestroy() {
        super.onDestroy();
        unRegisterSensor();
    }

    @Override
    public void onResume() {
        super.onResume();

        registerSensor();

        /* Enable this button as we disable before take picture */
        ButtonHandler(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final ViewTreeObserver vto = preview.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        deviceWidth = preview.getMeasuredWidth();
                        vto.removeOnGlobalLayoutListener(this);
                    }
                });


            }
        }, 500);

        if (camera == null) {
            setView();
            camera = getCameraInstance(currentCameraId);
        }
        /**
         * cameraWidth and cameraHeight is initialize in surfaceChanged() This
         * condition is specially for when user lock screen using power button
         * App is goes to onPause(),onStop(). On unlock screen of phone App is
         * onResume() at that time surfaceChanged() not called so start camera
         * preview
         */
        if (camera != null && cameraWidth > 0 && cameraHeight > 0) {
            /** inititalize camera */
            initCamera(cameraWidth, cameraHeight, currentCameraId);

        }

        Log.e(getClass().getSimpleName(), "camera fragment resume");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

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

                try {
                    photoCnt = Util.getOfflinePic(getActivity());
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
//                    tvPhotoCount.setText("" + photoCnt);
                    //go on as normal
                } catch (NumberFormatException e) {
                    //handle error
                }

                if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Albums) == "15") {
                    String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
                    tvAlbubName.setText(albumName);

                }
                Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Albums, "0");

            }
        }, 500);

        if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Albums) == "15") {
            String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
            tvAlbubName.setText(albumName);

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
                        /*animateShutter();
                        Animation blinkAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
                        ibCameraClickl.startAnimation(blinkAnim);*/
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

    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        if (camera == null) {
            Camera.open(currentCameraId);
        }
        if (camera == null) {
            camera = getCameraInstance(currentCameraId);
        }
        Camera.Parameters params = camera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                camera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }
        }
        return true;
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /**
     * Determine the space between the first two fingers
     */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        camera.setParameters(params);
    }

    @Override
    public void onPause() {
        stopPreview();
        unRegisterSensor();
        super.onPause();
    }

    /**
     * public method to handle click event of widget(view) defined in xml
     *
     * @param v
     */
    public void clickEvent(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.lin_mainHeader:
                Intent i = new Intent(getActivity(), SelectAlbum.class);
                startActivity(i);
                break;
            case R.id.rlShare:
                if (!isChecked) {
                    isChecked = true;
                    tbShare.setBackgroundResource(R.drawable.rounded_orange);
                    txtShare.setTextColor(getResources().getColor(R.color.nBtnCameraOption));
                } else {
                    isChecked = false;
                    tbShare.setBackgroundResource(R.drawable.rounded_solid_gray);
                    txtShare.setTextColor(getResources().getColor(R.color.white));
                }
                break;
            case R.id.rlOption:
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

                if (isTimerStart) {
                    optionIC1.setBackgroundResource(R.drawable.rounded_orange);
                    txtOptIc1.setTextColor(getResources().getColor(R.color.nBtnCameraOption));
                } else {
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


                    if (isFlashAvail && currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        rlFlash.setVisibility(View.VISIBLE);
                    } else {
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
                break;
            case R.id.rlTimer:
                if (isTimerStart) {
                    if (isCounteDown) {
                        stopTask();
                    }
                    isTimerStart = false;
                    optionIC1.setBackgroundResource(R.drawable.rounded_solid_gray);
                    txtOptIc1.setTextColor(getResources().getColor(R.color.white));
                } else {
                    isTimerStart = true;

                    optionIC1.setBackgroundResource(R.drawable.rounded_orange);
                    txtOptIc1.setTextColor(getResources().getColor(R.color.nBtnCameraOption));
                }
                break;
            case R.id.rlFrontCamera:
                ButtonHandler(false);
                if (inPreview) {
                    camera.stopPreview();
                }
                // NB: if you don't release the current camera before switching, you
                // app will crash
                if (camera != null) {
                    camera.release();
                }

                // swap the id of the camera to be used
                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

                    anticlock = AnimationUtils.loadAnimation(getActivity(),
                            R.anim.zoom_out);
                    rlFlash.startAnimation(anticlock);
                    rlFlash.setVisibility(View.INVISIBLE);
                } else {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    Animation clock1 = AnimationUtils.loadAnimation(getActivity(),
                            R.anim.zoom_in);
                    rlFlash.setVisibility(View.VISIBLE);
                    rlFlash.startAnimation(clock1);
                }

                camera = getCameraInstance(currentCameraId);

                //** Initialize surfasce view for custom camera *//*
                preview = (SurfaceView) getView().findViewById(R.id.preview);


                preview.setZOrderOnTop(false);
                previewHolder = preview.getHolder();
                previewHolder.addCallback(surfaceCallback);
                previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

                preview.setLayoutParams(new RelativeLayout.LayoutParams((int) (deviceWidth1 * 1.1f), (int) (deviceHeight1 * 1.1f)));

                Log.d("before init", "clickEvent: w = "+cameraWidth+ "  h = "+cameraHeight);

                initCamera(cameraWidth, cameraHeight, currentCameraId);
                setScreenRatio();
                break;
            case R.id.rlFlash:
                // *//**Check availability of flash light*//*
                // boolean
                // isFlashAvail=getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                if (isFlashAvail) {
                    //** Check flash light is on or off *//*
                    if (camera == null) {
                        setView();
                        camera = getCameraInstance(currentCameraId);
                    }
                    Parameters parameters = camera.getParameters();
                    parameters.setJpegQuality(90);
                    parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    if (parameters.getFlashMode().equalsIgnoreCase(Parameters.FLASH_MODE_OFF)) {
                        isFlashOn = true;
                        txtOpt3.setText("FLASH ON");
                        txtOpt3.setTextColor(getResources().getColor(R.color.nBtnCameraOption));
                        mImgFlash.setBackgroundResource(R.drawable.rounded_orange);
                        mImgFlash.setImageResource(R.drawable.flash);
//					parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        parameters.setFlashMode(Parameters.FLASH_MODE_ON);

                    } else {
                        isFlashOn = false;
                        txtOpt3.setText("FLASH OFF");
                        txtOpt3.setTextColor(getResources().getColor(R.color.white));
                        mImgFlash.setBackgroundResource(R.drawable.rounded_solid_gray);
                        mImgFlash.setImageResource(R.drawable.flash);
                        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                    }
                    camera.setParameters(parameters);

                } else {
                    txtOpt3.setText("FLASH OFF");
                    // Toast.makeText(this,
                    // "Flash light is not available in your device",
                    // Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.imgCapture:
                captureImage();
                break;

        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {

//			cameraWidth = width;
//			cameraHeight = height;
            Log.d("System out", "cameraWidth: " + cameraWidth
                    + ", cameraHeight: " + cameraHeight);
            /** inititalize camera */
            initCamera(cameraWidth, cameraHeight, currentCameraId);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance(int cameraId) {
        if (currentCameraId != Camera.CameraInfo.CAMERA_FACING_BACK
                && currentCameraId != Camera.CameraInfo.CAMERA_FACING_FRONT) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        Camera c = null;
        try {
            c = Camera.open(cameraId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * Initialize Camera with preview width and height
     */
    public void initCamera(int width, int height, int cameraid) {
        TextView txt_onoff = (TextView) mainView.findViewById(R.id.txt_onoff);

        if (camera == null) {
            camera = getCameraInstance(cameraid);
        }
        LinearLayout lin_flash = (LinearLayout) mainView.findViewById(R.id.lin_flash);

        if (camera == null) {
            camera = getCameraInstance(cameraid);
        }
        Camera.Parameters params = camera.getParameters();

        if (params.getSupportedFocusModes() != null && !params.getSupportedFocusModes().isEmpty() && params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        if (camera != null) {
            params = camera.getParameters();
            params.setJpegQuality(100);
            params.getSupportedPictureSizes();
            if (cameraid == Camera.CameraInfo.CAMERA_FACING_BACK) {
                lin_flash.setEnabled(true);

                List<String> FocusModes = params.getSupportedFocusModes();
                if (FocusModes != null
                        && FocusModes
                        .contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                    params
                            .setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                if (isFlashAvail) {
                    /**
                     * Change mode video to audio remember flash status(On or
                     * Off)
                     */
                    if (isFlashOn) {
                        txt_onoff.setText("On");
                        params.setFlashMode(Parameters.FLASH_MODE_ON);
                    } else {
                        txt_onoff.setText("Off");
                        params.setFlashMode(Parameters.FLASH_MODE_OFF);
                    }

                } else
                    txt_onoff.setText("Off");
            } else
                lin_flash.setEnabled(false);

            camera.setParameters(params);

            initPreview(width, height);
            params.setPictureSize(width, height);
            startPreview();


            Display display = ((WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE))
                    .getDefaultDisplay();
            displayRotation = display.getRotation();
            Log.d("System out", "initpreview: display.getRotation(): "
                    + displayRotation);

            if (display.getRotation() == Surface.ROTATION_0) {
                camera.setDisplayOrientation(90);
            } else if (display.getRotation() == Surface.ROTATION_90) {
                params.setPreviewSize(width, height);
            } else if (display.getRotation() == Surface.ROTATION_180) {
                params.setPreviewSize(height, width);
            } else if (display.getRotation() == Surface.ROTATION_270) {
                camera.setDisplayOrientation(180);
            }
        }

    }

    /**
     * Initalize camera preview
     */
    private void initPreview(int width, int height) {
        if (camera != null && previewHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(previewHolder);
            } catch (Throwable t) {
//				Log.e("PreviewDemo-surfaceCallback","Exception in setPreviewDisplay()");
            }
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> supportedSizes = parameters.getSupportedPictureSizes();
            Camera.Size size = getBestPreviewSize(width, height, parameters);
            parameters.setJpegQuality(90);
            if (size != null) {
                Log.d("", "initPreview: width  = "+size.width+ " height = "+size.height);
                parameters.setPreviewSize(size.width, size.height);
                parameters.setPictureSize(size.width, size.height);
                camera.setParameters(parameters);
                cameraConfigured = true;
            }
        }
    }

    /**
     * Start Preview of camera
     */
    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            inPreview = true;
        }
    }

    /**
     * Stop Preview of camera
     */
    private void stopPreview() {
        if (camera != null && inPreview) {
            camera.stopPreview();
            inPreview = false;
            try {
                camera.setPreviewCallback(null);
                preview.getHolder().removeCallback(surfaceCallback);
                camera.release();
                camera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Get best camera preview size for device
     */
    private Camera.Size getBestPreviewSize(int width, int height,
                                           Parameters parameters) {

        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.height <= width && size.width <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }

    PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            final BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
//                sizeOptions.inJustDecodeBounds = false;
                sizeOptions.inScaled = false;
//                sizeOptions.inDither = false;
            sizeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeByteArray(data, 0, data.length, sizeOptions);
            Log.d("onPictureTaken", "Bitmap is " + sizeOptions.outWidth + "x"
                    + sizeOptions.outHeight);


            int angleToRotate = getRoatationAngle(currentCameraId);
            Log.d("Tag", "Rotation angle: " + angleToRotate);
            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
                angleToRotate = angleToRotate + 180;

            final String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
            new SavePhotoTask(albumId, data, angleToRotate, isChecked, orientation).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    };

    /**
     * This method is called on PageChange to set font color and change image
     */
    private void setViewInViewPager() {


        img_thumb = (ImageView) mainView.findViewById(R.id.img_thumb);
        imgPreview = (ImageView) mainView.findViewById(R.id.imgPreview);

        LinearLayout lin_flash = (LinearLayout) mainView.findViewById(R.id.lin_flash);

        imgCapture.setVisibility(View.VISIBLE);

        /** Show flash Swap Camera on Image mode */
        lin_flash.setVisibility(View.VISIBLE);

        /** if phone has only one camera, hide "switch camera" button */
        if (Camera.getNumberOfCameras() == 1)
            lin_swape_cam.setVisibility(View.INVISIBLE);
        else
            lin_swape_cam.setVisibility(View.VISIBLE);

        if (!inPreview) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    /**
                     * Initialize camera on capture and video mode Start Preview
                     * of camera
                     */
                    initCamera(cameraWidth, cameraHeight, currentCameraId);
                }
            }, 10);
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(getActivity(), "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v("Tag file path", pathImage);
            if (pathImage != null && pathImage.length() > 0) {

				/*
                 * Intent intent = new Intent(); intent.putExtra("path",
				 * pathImage); setResult(100, intent); finish();
				 */

                Uri picUri = Uri.fromFile(new File(pathImage));
                img_thumb.setImageURI(picUri);
                imgPreview.setImageURI(picUri);

            }
        }
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putString("output", pathImage);
//		outState.putBoolean("isCapture", isCaptured);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        String finalPath1 = savedInstanceState.getString("output");
//		isCaptured=savedInstanceState.getBoolean("output");
        Log.v("System out", "noRestore path " + finalPath1);
        if (finalPath1 != null && finalPath1.length() > 0) {

            finalPath = Uri.fromFile(new File(finalPath1));

        }
    }*/

    private int getNavigationBarHeight(int orientation) {
        Resources resources = getResources();

        int id = resources.getIdentifier(
                orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }

        return 0;
    }

    @Override
    public void onBackPressed() {
        /*super.onBackPressed();*/
        getActivity().finish();
    }

    public String getDeviceName() {
        String manufacturer = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model;
    }

    public void ButtonHandler(boolean flag_disable) {
//        if (flag_disable) {
//            imgCapture.setClickable(false);
//            imgCapture.setEnabled(false);
//        } else {
//            imgCapture.setClickable(true);
//            imgCapture.setEnabled(true);
//        }
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
                        txtCounter.setText((countTime) + "");

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
                    /*animateShutter();
                    Animation blinkAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
                    ibCameraClickl.startAnimation(blinkAnim);*/
                    capture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    public void stopTask() {
        txtCounter.setText("");
        txtCounter.setVisibility(View.GONE);
        isCounteDown = false;
        if (timerTask != null) {
            timerTask.cancel();
            countTime = 10;
        }
    }

    private void captureImage() {
        if (isTimerStart) {
            if (isCounteDown) {
                stopTask();
            } else {
                startTimer();
            }
        } else {
            try {
                /*animateShutter();
                Animation blinkAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
                ibCameraClickl.startAnimation(blinkAnim);*/
                capture();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void capture() {
        // ***************** with default code*******************
        ButtonHandler(true);

        AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);

        ShutterCallback shutter = new ShutterCallback() {

            @Override
            public void onShutter() {
                // TODO Auto-generated method stub

                //** Play degfault capture image sound *//*
                AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
            }
        };
        // camera.setDisplayOrientation(180);
        camera.takePicture(null, null, mPicture);

        final Handler handler = new Handler();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                    }
                });
            }
        }, 1000);

        //** Flick view on capture image *//*
        final View view_transperant = getActivity().findViewById(R.id.view_transperant);
        view_transperant.setVisibility(View.VISIBLE);
        Animation animation = new AlphaAnimation(1, 0.5f); // Change alpha
        // from fully
        // visible to
        // invisible
        animation.setDuration(200); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        // animation
        // rate
        // animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        // infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at
        // the end so the button
        // will fade back in
        view_transperant.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                view_transperant.clearAnimation();
                view_transperant.setVisibility(View.GONE);
            }
        }, animation.getDuration() / 2);
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

    private class SavePhotoTask extends AsyncTask<Void, Void, String> {

        private String albumId;
        private String strDate, photo_cache1, photo_cache2;
        private File file, photo, file1, photo1;
        private byte[] data;
        private int angleToRotate;
        private boolean isShareEnable = false;
        private int orientation1;

        private SavePhotoTask(String albumId, byte[] data, int angleToRotate, boolean isShareEnable, int oritation) {
            this.albumId = albumId;
            this.data = data;
            this.angleToRotate = angleToRotate;
            this.isShareEnable = isShareEnable;
            this.orientation1 = oritation;
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

            photo_cache1 = photo.getAbsolutePath();
            photo_cache2 = photo1.getAbsolutePath();

            if (photo.exists()) {
                photo.delete();
            }
            if (photo1.exists()) {
                photo1.delete();
            }

            try {
                final String pictureFile = photo.getAbsolutePath();

                final BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
//                sizeOptions.inJustDecodeBounds = false;
//                sizeOptions.inScaled = false;
//                sizeOptions.inDither = false;
                sizeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                BitmapFactory.decodeByteArray(data, 0, data.length, sizeOptions);
                Log.d("TAG", "Bitmap is " + sizeOptions.outWidth + "x"
                        + sizeOptions.outHeight);

//                final float widthSampling = sizeOptions.outWidth / deviceWidth;
//                sizeOptions.inJustDecodeBounds = false;
//                sizeOptions.inSampleSize = (int) widthSampling;
                FileOutputStream fos;
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length, sizeOptions);
//                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.close();

//                realImage = getRotatedImage(photo.getAbsolutePath(),currentCameraId);

                Log.d("TAG", "Oriantaion :" + orientation1);

                int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                Log.d("TAG", "Oriantaion :" + rotation);

                fos = new FileOutputStream(pictureFile);
                if (orientation1 == 0) {
                    realImage = rotate(realImage, angleToRotate);
                    Log.e(getClass().getSimpleName(), "portrait");
                } else if (orientation1 == 1) {
                    realImage = rotate(realImage, 180);
                    Log.e(getClass().getSimpleName(), "landscape");
                } else if (orientation1 == 2) {
                    realImage = rotate(realImage, 0);
                    Log.e(getClass().getSimpleName(), "landscape");
                } else if (orientation1 == 3) {
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
                sizeOptions.inJustDecodeBounds = false;
                sizeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                /*BitmapFactory.decodeByteArray(data, 0, data.length, sizeOptions);
                Log.d("TAG", "Bitmap is " + sizeOptions.outWidth + "x"
                        + sizeOptions.outHeight);

                final float widthSampling = sizeOptions.outWidth / deviceWidth;
                sizeOptions.inJustDecodeBounds = false;
                sizeOptions.inSampleSize = (int) widthSampling;
*/
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length, sizeOptions);
//                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.close();

//                realImage = getRotatedImage(photo1.getAbsolutePath(),currentCameraId);
                fos = new FileOutputStream(pictureFile);
                if (orientation1 == 0) {
                    realImage = rotate(realImage, angleToRotate);
                    Log.e(getClass().getSimpleName(), "portrait");
                } else if (orientation1 == 1) {
                    realImage = rotate(realImage, 180);
                    Log.e(getClass().getSimpleName(), "landscape");
                } else if (orientation1 == 2) {
                    realImage = rotate(realImage, 0);
                    Log.e(getClass().getSimpleName(), "landscape");
                } else if (orientation1 == 3) {
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
                    /*Intent intent = new Intent(getActivity(), UploadPhotoIntentService.class);
                    intent.putExtra(Constant.SHRED_PR.KEY_USERID, userId);
                    intent.putExtra(Constant.SHRED_PR.KEY_Current_AlbumID, albumId);
                    intent.putExtra(Constant.SHRED_PR.KEY_Photo_Name, photo.getAbsolutePath());
                    intent.putExtra(Constant.SHRED_PR.KEY_Data, CaptureActivity.class.getSimpleName());
                    getActivity().startService(intent);*/
                    MyApp.uploadingImage.add(photo.getAbsolutePath());

                    uploadPhotoTask task = new uploadPhotoTask(getActivity(), userId, albumId, photo.getAbsolutePath(), CaptureActivity.class.getSimpleName(), photo_cache1, photo_cache2);
                    task.execute();
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
            HomeFragment.getInstance().reloadOfflinePhoto();
            camera.stopPreview();
            camera.startPreview();

        }
    }

    private Bitmap rotate(Bitmap bitmap, int degree) {
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();

        final Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
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
            MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(sourceFile));
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

    public void setName() {
        String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
        tvAlbubName.setText(albumName);
    }

    public void setScreenRatio(){
        if ((model_name != null && model_name.length() > 0 && model_name.contains("MotoE")) || (model_name != null && model_name.length() > 0 && model_name.contains("LGE Nexus 5")) ) {
            Log.d("LOG", "Mobile Name: " + model_name);

            int navBarHeight = getNavigationBarHeight(Configuration.ORIENTATION_PORTRAIT) + Util.getStatusBarHeight(getActivity());

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainView.findViewById(R.id.linHeader).getLayoutParams();
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) mainView.findViewById(R.id.preview).getLayoutParams();
            /*params.setMargins(0, 0, 0, navBarHeight);*/
            params2.setMargins(0, navBarHeight, 0, 0);
            /*mainView.findViewById(R.id.relBottom).setLayoutParams(params);*/
            mainView.findViewById(R.id.preview).setLayoutParams(params2);

            int height  = params.height;
            mainView.findViewById(R.id.linHeader).setBackgroundColor(Color.parseColor("#F0F0F0"));
            ((TextView)mainView.findViewById(R.id.tvChangeAlbum)).setTextColor(Color.BLACK);
            ((TextView)mainView.findViewById(R.id.tvAlbubName)).setTextColor(Color.BLACK);
            ((ImageView)mainView.findViewById(R.id.img_Arrow)).setImageResource(R.drawable.down_arrow);
            /*params.setMargins(0, -navBarHeight, 0, 0);*/
            if(navBarHeight>height){
                params.height = navBarHeight;
            }
            mainView.findViewById(R.id.linHeader).setLayoutParams(params);

        } else {
//			Log.d("LOG","Name: 2"+model_name);
            Log.d("LOG", "Mobile Name: " + model_name);

        /*Check if soft button for home and back is available*/
            if (Util.isNavigationBarAvailable()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int navBarHeight = getNavigationBarHeight(Configuration.ORIENTATION_PORTRAIT);
                    int height = Util.getStatusBarHeight(getActivity());
                    mainView.findViewById(R.id.rel_main_capture_camera).setPadding(0, height, 0, -navBarHeight);

//                mainView.findViewById(R.id.rel_remaining_sec).setPadding(0, 0, 0, navBarHeight);
//                mainView.findViewById(R.id.lin_retake).setPadding(0, 0, 0, navBarHeight);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainView.findViewById(R.id.relBottom).getLayoutParams();
                    params.setMargins(0, 0, 0, navBarHeight);
                    mainView.findViewById(R.id.relBottom).setLayoutParams(params);

                } else {
                    int height = Util.getStatusBarHeight(getActivity());
                    mainView.findViewById(R.id.rel_main_capture_camera).setPadding(0, height, 0, 0);
                }
            } else {
                int height = Util.getStatusBarHeight(getActivity());
                mainView.findViewById(R.id.rel_main_capture_camera).setPadding(0, height, 0, 0);
            }
        }
    }

}