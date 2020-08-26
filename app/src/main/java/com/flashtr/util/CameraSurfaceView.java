package com.flashtr.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flashtr.R;
import com.flashtr.fragment.CameraFragment;

import java.io.IOException;
import java.util.List;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {

    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final double ASPECT_RATIO = 9.0 / 4.0;

    public Camera.Parameters parameters;
    private Camera camera;
    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;
    private int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
    private onPictureTakenCallback onPictureTakenCallback;
    private float mDist;
    private ImageView mImgFlash;
    private TextView txtOpt3;
    private LinearLayout rlFlash;
    public boolean isFlashOn = false;
    public boolean hasFlash = false;
    private Context mContext;

    private int deviceWidth;
    private int deviceHeight;

    public CameraSurfaceView(Context context) {
        super(context);
        mContext = context;
        init(context);

    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        final Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        deviceWidth = size.x;
        deviceHeight = size.y;
        getHolder().addCallback(this);
        mContext = context;

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try{
            camera.setPreviewDisplay(surfaceHolder);
        }catch (IOException e){
            camera.release();
            camera = null;
            e.printStackTrace();
        }

        Log.e(getClass().getSimpleName(), "surface created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h) {
        Log.e(getClass().getSimpleName(), "surface changed");
        CameraFragment.getInstance().setLayoutParams(false);
        setCameraParams();
        CameraFragment.getInstance().setLayoutParams(false);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        camera.stopPreview();
        camera.release();
        camera = null;
        Log.e(getClass().getSimpleName(), "surface destroy");
    }

    private void setCameraParams() {
        if (camera != null) {
            parameters = camera.getParameters();

            mPreviewSize = determineBestPreviewSize(parameters);
            mPictureSize = determineBestPictureSize(parameters);

            if (mPreviewSize != null)
                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

            if (mPictureSize != null)
                parameters.setPictureSize(mPictureSize.width, mPictureSize.height);

            if (parameters.getSupportedFocusModes() != null && !parameters.getSupportedFocusModes().isEmpty() && parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            hasFlash = mContext.getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if (hasFlash) {
                if (rlFlash != null)
                    rlFlash.setVisibility(INVISIBLE);
                if (isFlashOn && CAMERA_FACING == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    if (mImgFlash != null) {
                        txtOpt3.setTextColor(getResources().getColor(R.color.nRippleHeaderBG));
                        txtOpt3.setText("FLASH ON");
                        mImgFlash.setBackgroundResource(R.drawable.rounded_orange);
                        mImgFlash.setImageResource(R.drawable.flash);
                    }
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

                    // changing button/switch image
//            toggleButtonImage();
                } else {
                /* If you want to start camera light like a tourch then use below line */
                /*parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);*/
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    if (mImgFlash != null) {
                        txtOpt3.setTextColor(getResources().getColor(R.color.white));
                        txtOpt3.setText("FLASH OFF");
                        mImgFlash.setBackgroundResource(R.drawable.rounded_solid_gray);
                        mImgFlash.setImageResource(R.drawable.flash);
                    }
                    // changing button/switch image
                    //toggleButtonImage();
                }

            } else {
                if (parameters.getSupportedFlashModes() != null && !parameters.getSupportedFlashModes().isEmpty() && parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                }
                if (rlFlash != null)
                    rlFlash.setVisibility(INVISIBLE);
            }

            camera.setParameters(parameters);
            CameraFragment.getInstance().setLayoutParams(false);
            setLayoutParams(new FrameLayout.LayoutParams(mPreviewSize.width, mPreviewSize.height));

        }
    }

    public void destroyCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void startCamera() {
        if (camera == null) {
            openCamera();
        }
        if (camera != null) {
            camera.setDisplayOrientation(90);
            CameraFragment.getInstance().setLayoutParams(false);
            setCameraParams();
            CameraFragment.getInstance().setLayoutParams(false);
        }

    }

    public void startPreview() {
        if (camera != null && mPreviewSize != null && mPictureSize != null) {
            try {
                Log.e(getClass().getSimpleName(), getMeasuredWidth() + "  surfaceview " + getMeasuredHeight());
                Log.e(getClass().getSimpleName(), mPreviewSize.width + "  preview " + mPreviewSize.height);
                Log.e(getClass().getSimpleName(), mPictureSize.width + "  picture " + mPictureSize.height);
                camera.setPreviewDisplay(getHolder());
                CameraFragment.getInstance().setLayoutParams(false);
                camera.startPreview();
                CameraFragment.getInstance().setLayoutParams(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPreview() {
        if (camera != null) {
            try {
                camera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (width > height * ASPECT_RATIO) {
            width = (int) (height * ASPECT_RATIO);
        } else {
            height = (int) (width / ASPECT_RATIO);
        }

        setMeasuredDimension(width, height);

        if (parameters != null) {
            mPreviewSize = determineBestPreviewSize(parameters);
            mPictureSize = determineBestPictureSize(parameters);
        }
    }


    private Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
        if (parameters != null) {
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            return determineBestSize(sizes);
        } else {
            return null;
        }

    }

    private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
        if (parameters != null) {
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            return determineBestSize(sizes);
        } else {
            return null;
        }
    }

    private Camera.Size determineBestSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = null;

        for (Camera.Size currentSize : sizes) {
            boolean isDesiredRatio = (currentSize.width / 4) == (currentSize.height / 3);
            boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
            boolean isInBounds = currentSize.width <= PICTURE_SIZE_MAX_WIDTH;

            if (isDesiredRatio && isInBounds && isBetterSize) {
                bestSize = currentSize;
            }
        }
        if (bestSize == null) {
            return sizes.get(0);
        }
        return bestSize;
    }

    private void openCamera() {
        try {
            int cameraCount = 0;

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);

                if (CAMERA_FACING == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        camera = Camera.open(camIdx);
                        break;
                    }
                } else {
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        camera = Camera.open(camIdx);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void changePreview(ImageView mImgFlash, TextView txtOpt3, LinearLayout rlFlash) {
        destroyCamera();

        if (CAMERA_FACING == Camera.CameraInfo.CAMERA_FACING_BACK) {
            CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        startCamera();
        startPreview();

    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        if (onPictureTakenCallback != null) {
            final BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
//                sizeOptions.inJustDecodeBounds = false;
//                sizeOptions.inScaled = false;
//                sizeOptions.inDither = false;
            sizeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, sizeOptions);
            Log.d("onPictureTakenCallback", "Bitmap is " + sizeOptions.outWidth + "x"
                    + sizeOptions.outHeight);
            onPictureTakenCallback.onPictureTaken(bytes);
        }
    }

    public void setOnPictureTakenCallback(CameraSurfaceView.onPictureTakenCallback onPictureTakenCallback) {
        this.onPictureTakenCallback = onPictureTakenCallback;
    }

    public void takePicture() {
        if (camera != null) {
            camera.takePicture(null, null, this);
        }
    }

    public int getCAMERA_FACING() {
        return CAMERA_FACING;
    }

    public boolean getHashFlash() {
        return hasFlash;
    }

    public interface onPictureTakenCallback {
        void onPictureTaken(byte[] bytes);
    }

    /* to turn on or off flashlight
   * hasFlash: if flash available - true
   * hasFlash: if flash not available - false
   * isFlashOn: if flash on - true
   * isFlashOn: if flash off - false    */
    public void turnOnOffFlash(ImageView mImgFlash, TextView txtOpt3, LinearLayout rlFlash) {

        hasFlash = mContext.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//        if (hasFlash) {
//            if (camera == null) {
//                openCamera();
//            }
//            if (isFlashOn) {
//
//                parameters = camera.getParameters();
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                camera.setParameters(parameters);
//                camera.stopPreview();
//                isFlashOn = false;
//
//                // changing button/switch image
////            toggleButtonImage();
//            } else {
//                parameters = camera.getParameters();
//                /* If you want to start camera light like a tourch then use below line */
//                /*parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);*/
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//                camera.setParameters(parameters);
//                camera.startPreview();
//                isFlashOn = true;
//                // changing button/switch image
//                //toggleButtonImage();
//            }
//        } else {
//            Toast.makeText(mContext, "Sorry!! Device doesn't support Flash light...", Toast.LENGTH_SHORT).show();
//        }
        this.mImgFlash = mImgFlash;
        this.txtOpt3 = txtOpt3;
        this.rlFlash = rlFlash;
        if (CAMERA_FACING == Camera.CameraInfo.CAMERA_FACING_BACK) {
            if (isFlashOn) {
                isFlashOn = false;
            } else {
                isFlashOn = true;
            }
            CameraFragment.getInstance().setLayoutParams(false);
            setCameraParams();
            CameraFragment.getInstance().setLayoutParams(false);
        }
    }

    /* Touch Event to handel zoomIn/zooOut features */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        if (camera == null) {
            openCamera();
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

}
