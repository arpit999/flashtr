package com.flashtr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.activity.*;
import com.flashtr.adapter.CropingOptionAdapter;
import com.flashtr.util.CircleImageView;
import com.flashtr.util.Constant;
import com.flashtr.util.CropingOption;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {
    public static final String ERROR_SERVICE_NOT_AVAILABLE =
            "SERVICE_NOT_AVAILABLE";
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;
    @InjectView(R.id.mlEditProfile)
    MaterialRippleLayout mlEditProfile;
    @InjectView(R.id.ivEditProfileImg)
    ImageView ivEditProfileImg;
    private static String gcmRegId;
    @InjectView(R.id.imgStaticProfile)
    CircleImageView staticImg;
    @InjectView(R.id.rlSubmit)
    MaterialRippleLayout rlSubmit;
    @InjectView(R.id.imgProfile)
    CircleImageView imgProfile;
    @InjectView(R.id.etName)
    EditText etName;
    @InjectView(R.id.etEmail)
    EditText etEmail;
    /*@InjectView(R.id.rlLogo)
    MaterialRippleLayout rlLogo;*/
    /*@InjectView(R.id.rlTopLogo)
    RelativeLayout rlTopLogo;*/
    /*@InjectView(R.id.back)
    ImageView backButton;*/
    String camera_pathname = "";
    Uri globalUri = null;
    RegisterActivity registerActivity = null;
    Cursor cursor = null;
    DisplayImageOptions options;
    // GCM
    GoogleCloudMessaging gcm;
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    private ImageLoader imageLoader;
    private int orientation = 0;  // 0=portrait and 1=landscape
    private int angle;
    Typeface type;
    private Drawable backArrow;
    private String activity;
    @InjectView(R.id.main)
    RelativeLayout mainParent;

    @InjectView(R.id.tvLogin)
    TextView mTvLogin;
    @InjectView(R.id.tvSelectAlbum)
    TextView mTvSelectAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_register);
        mainParent.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        init();


    }

    /*@OnClick(R.id.rlLogo)
    public void logoBack(View view){
        onBackPressed();
    }*/


    @OnClick(R.id.mlEditProfile)
    public void editProfileImg(View view) {
        selectImageOption();
    }


    @OnClick(R.id.rlSubmit)
    @SuppressWarnings("unused")
    public void Submit(View view) {
        if (isEmpty(getText(etName))) {
            Toast.makeText(getApplicationContext(), "please enter name", Toast.LENGTH_SHORT).show();
        } else {
            register();
        }
    }

    private void register() {
        if (Util.isOnline(getApplicationContext())) {
            gcmRegId = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_GCM_REGID);
            if (gcmRegId.equals("")) {
                // Registration is not present, register now with GCM
                gcmRegId = registerGCM();
            } else {
                //Already Registr with GCM
                gcmRegId = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_GCM_REGID);
//                toast("You are already register on GCM ==>> " + gcmRegId);
                new Registration(getText(etEmail), getText(etName), gcmRegId).execute();
            }
        }
    }

    public String registerGCM() {

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
//        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(gcmRegId)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + gcmRegId);
        } else {
//            Toast.makeText(getApplicationContext(),
//                    "RegId already available. RegId: " + gcmRegId,
//                    Toast.LENGTH_LONG).show();
        }
        return gcmRegId;
    }


    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    gcmRegId = gcm.register(Constant.SENDER_ID);
                    Log.e("DEVICE", "" + gcmRegId);
                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_GCM_REGID, gcmRegId);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + gcmRegId);
                    msg = "" + gcmRegId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                    register();
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//                Toast.makeText(getApplicationContext(),
//                        "BEFOR REGISTRATION" + msg, Toast.LENGTH_LONG)
//                        .show();
                Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_GCM_REGID, msg);
                new Registration(getText(etEmail), getText(etName), msg).execute();

            }
        }.execute(null, null, null);
    }


    private void init() {
        type = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
        etName.setTypeface(type);
        etEmail.setTypeface(type);
        mTvSelectAlbum.setTypeface(type);
        mTvLogin.setTypeface(type);

        Util.setupOutSideTouchHideKeyboard(this, mainParent);

        registerActivity = this;
        final File cacheDir = new File(getExternalCacheDir().toString() + "/Profile/");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        final File photo = new File(cacheDir.getAbsolutePath(), "Profile.jpg");
        if (photo.exists()) {
            photo.delete();
        }

        outPutFile = photo;
        Log.e("outPutFile", "1" + outPutFile);
//        createURI();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));
        options = new DisplayImageOptions.Builder()
//                .displayer(new RoundedBitmapDisplayer(300))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        if (getIntent() != null) {
            if (getIntent().getStringExtra("activity").equalsIgnoreCase("HomeActivity")) {
                activity = "HomeActivity";
                etName.setText(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERNAME));
                etEmail.setText(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_EMAIL));
                /*rlTopLogo.setBackgroundColor(getResources().getColor(R.color.background_color));*/
                /*rlLogo.setBackgroundColor(getResources().getColor(R.color.transparent));*/
                backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                backArrow.setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.SRC_ATOP);
                /*backButton.setBackground(backArrow);*/
                mTvLogin.setText("UPDATE");
                final String url = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERIMAGE);
                if (!TextUtils.isEmpty(url)) {
                    imageLoader.displayImage(url, imgProfile, options);
//                        boolean hasDrawable = (imgProfile.getDrawable() != null);
//                        if(hasDrawable) {
                    // imageView has image in it
//                            Log.e("hasDrawable",""+hasDrawable);
                    staticImg.setVisibility(View.GONE);
                    imgProfile.setVisibility(View.VISIBLE);
                } else {
                    staticImg.setVisibility(View.VISIBLE);
                    imgProfile.setVisibility(View.GONE);
                }
            }

            if (getIntent().getStringExtra("activity").equalsIgnoreCase("RegisterActivity")) {
                activity = "RegisterActivity";
                final String userData = getIntent().getStringExtra(Constant.SHRED_PR.KEY_Data);
                if (!TextUtils.isEmpty(userData)) {
                    try {
                        final JSONObject userJsonObject = new JSONObject(userData);
                        etName.setText(userJsonObject.optString("user_name"));
                        etEmail.setText(userJsonObject.optString("email"));
                        final String url = userJsonObject.optString("user_image");
                        if (!TextUtils.isEmpty(url)) {
                            imageLoader.displayImage(url, imgProfile, options);
//                        boolean hasDrawable = (imgProfile.getDrawable() != null);
//                        if(hasDrawable) {
                            // imageView has image in it
//                            Log.e("hasDrawable",""+hasDrawable);
                            staticImg.setVisibility(View.GONE);
                            imgProfile.setVisibility(View.VISIBLE);
                        } else {
                            staticImg.setVisibility(View.VISIBLE);
                            imgProfile.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {

                    outPutFile = new File(Constant.storageDirectory, System.currentTimeMillis() + ".jpg");
                    globalUri = Uri.fromFile(outPutFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, globalUri);
                    startActivityForResult(intent, CAMERA_CODE);

                } else if (items[item].equals("Choose from Gallery")) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //    protected void startCameras() {
//        if (globalUri != null) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, globalUri);
//            startActivityForResult(intent, CAMERA_CODE);
//        } else {
//            createURI();
//        }
//    }

    private void createURI() {

        final File cacheDir = new File(getExternalCacheDir().toString() + "/Profile/");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        final File photo = new File(cacheDir.getAbsolutePath(), "Profile.jpg");
        try {
            photo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        globalUri = Uri.fromFile(photo);
        Log.e(getClass().getSimpleName(), globalUri.toString());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:

                    try {
                        mImageCaptureUri = data.getData();
                        System.out.println("Gallery Image URI : " + mImageCaptureUri);
                        CropingIMG(GALLERY_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case CAMERA_CODE:
                    new LoadImagesFromSDCard().execute(globalUri.getPath());
//                    imageLoader.displayImage(Uri.fromFile(new File(globalUri.getPath())).toString(), imgProfile, options);
                    break;

                case CROPING_CODE:
                    try {
                        Log.e("outPutFile", "3" + outPutFile);
                        if (outPutFile.exists()) {
                            Bitmap photo = decodeFile(outPutFile);
                            //imgProfile.setImageBitmap(photo);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();

                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                            final File cacheDir = new File(getExternalCacheDir().toString() + "/Profile/");
                            if (!cacheDir.exists()) {
                                cacheDir.mkdir();
                            }
                            final File photoFile = new File(cacheDir.getAbsolutePath(), System.currentTimeMillis() + ".jpg");
                            if (photoFile.exists()) {
                                photoFile.delete();
                            }
                            try {
                                photoFile.createNewFile();
                                FileOutputStream fos = new FileOutputStream(photoFile);
                                photo.compress(Bitmap.CompressFormat.PNG, 95, fos);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            camera_pathname = photoFile.getAbsolutePath();
                            Log.e(">>>>>>>", "" + camera_pathname);
                            Log.e(">>>>>>>", "" + Uri.fromFile(new File(camera_pathname)));
                            imageLoader.displayImage(Uri.fromFile(new File(camera_pathname)).toString(), imgProfile, options);
                            staticImg.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(this, "Error while save image", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

            }
        } else {
            Log.e("Result:", "data null " + resultCode + ":" + data);
        }
    }

    private void CropingIMG(int type) {

        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {

            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            //Create output file here
            if (type == GALLERY_CODE) {
                intent.setData(mImageCaptureUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
            } else {

                intent.setData(globalUri);
            }

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(this, cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (activity.equalsIgnoreCase("HomeActivity")) {
            /*Intent intent= new Intent(RegisterActivity.this,HomeActivity.class);
            startActivity(intent);*/
            finish();
        }
    }

    class Registration extends AsyncTask<Void, String, String> {

        String Email, Name, device_id;

        private Registration(String Email, String Name, String gcmRegId) {
            this.Email = Email;
            this.Name = Name;
            this.device_id = gcmRegId;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            CustomDialog.getInstance().show(RegisterActivity.this, "");
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String resp = "";
            try {

                JSONObject jData = new JSONObject();
                jData.put("method", "registration");
                jData.put("userid", "" + read(Constant.SHRED_PR.KEY_USERID));
                //jData.put("userid", "493474331");
                jData.put("device_id", device_id);
                jData.put("name", "" + Name);
                jData.put("email", "" + Email);

                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = new HttpPost(
                        Constant.URL);

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                if (!TextUtils.isEmpty(camera_pathname)) {
                    Log.e("camera_pathname", ">>" + camera_pathname);
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
            Log.d("Resp Upload", "" + resp);
            return resp;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            CustomDialog.getInstance().hide();
            if (Util.isOnline(RegisterActivity.this)) {
                Log.e("result>>", ">>" + result);

                try {
                    JSONObject jObj = new JSONObject(result);

                    toast("" + jObj.getString("msg"));

                    int status = jObj.optInt("success");
                    if (status == 1) {

                        JSONObject jsonObject = jObj.optJSONObject("data");
                        JSONObject albumObject = jObj.optJSONObject("albumdata");

                        write(Constant.SHRED_PR.KEY_Current_AlbumID, "" + albumObject.optString("album_id"));
                        write(Constant.SHRED_PR.KEY_Current_AlbumName, "" + albumObject.optString("album_name"));

//                    write(Constant.SHRED_PR.KEY_USERID, "" + jsonObject.optString("userid"));
                        write(Constant.SHRED_PR.KEY_USERNAME, "" + jsonObject.optString("username"));
                        write(Constant.SHRED_PR.KEY_USERIMAGE, "" + jsonObject.optString("user_image"));
                        write(Constant.SHRED_PR.KEY_MOBILE, "" + jsonObject.optString("mobileno"));
                        write(Constant.SHRED_PR.KEY_EMAIL, "" + jsonObject.optString("email"));
                        Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_SELECTED_TAB, "0");
                        write(Constant.SHRED_PR.KEY_IS_LOGGEDIN, "1");
                        if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Is_FirstTime).equalsIgnoreCase("1")) {
                            Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_PAGER_POSITION, "0");
                            Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Is_FirstTime, "2");
                            startActivity(TipsActivity.class);
                            finish();
                        } else {
                        /*startActivity(com.flashtr.activity.TabActivity.class);*/
                            Intent in = new Intent(RegisterActivity.this, TabActivity.class);
                            startActivity(in);
                            finish();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        }
    }


    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }


    /**
     * Async task for loading the images from the SD card.
     *
     * @author Android Example
     */
// Class with extends AsyncTask class
    public class LoadImagesFromSDCard extends AsyncTask<String, Void, Bitmap> {
        protected void onPreExecute() {
            /****** NOTE: You can call UI Element here. *****/
        }

        // Call after onPreExecute method
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;
            try {
                bitmap = BitmapFactory.decodeFile(urls[0]);
                if (bitmap != null) {
                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
                    newBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                    bitmap.recycle();
                }
            } catch (Exception e) {
                // Error fetching image, try to recover
                /********* Cancel execution of this task. **********/
                cancel(true);
            }
            return newBitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            // NOTE: You can call UI Element here.
            // Close progress dialog
            if (bitmap != null) {
                //imgProfile.setImageBitmap(bitmap);
                CropingIMG(CAMERA_CODE);
            }
        }
    }

}