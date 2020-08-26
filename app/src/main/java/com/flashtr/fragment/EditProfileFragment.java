package com.flashtr.fragment;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.HomeActivity;
import com.flashtr.R;
import com.flashtr.adapter.CropingOptionAdapter;
import com.flashtr.util.Constant;
import com.flashtr.util.CropingOption;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EditProfileFragment extends Fragment {

    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;
    @InjectView(R.id.ivEditProfileImg)
    ImageView ivEditProfileImg;
    @InjectView(R.id.imgProfileStatic)
    ImageView staticImg;
    @InjectView(R.id.imgProfile)
    ImageView imgProfile;
    @InjectView(R.id.etName)
    EditText etName;
    @InjectView(R.id.etEmail)
    EditText etEmail;
    @InjectView(R.id.rlProfile)
    RelativeLayout rlProfile;
    @InjectView(R.id.rlSubmit)
    MaterialRippleLayout rlSubmit;
    @InjectView(R.id.mlEditProfile)
    MaterialRippleLayout mlEditProfile;
    String camera_pathname = "";
    String strImage = "";
    Uri globalUri = null;
    Cursor cursor = null;
    DisplayImageOptions options;
    ImageLoader imageLoader;
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    public Activity activity;
    private Typeface font;
    private Drawable editBtn;
    private Boolean isFirst = true;
    @InjectView(R.id.main)
    RelativeLayout mainParent;

    public static EditProfileFragment newInstance() {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        return editProfileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.inject(this, view);
        imageLoader = ImageLoader.getInstance();
        if(imageLoader.isInited()){
            imageLoader.destroy();
            imageLoader.init(ImageLoaderConfiguration
                    .createDefault(getActivity().getApplicationContext()));
        }

        options = new DisplayImageOptions.Builder()
//                .displayer(new RoundedBitmapDisplayer(300))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        outPutFile = new File(Constant.storageDirectory, System.currentTimeMillis() + ".jpg");
        init();
        font = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");
        etName.setTypeface(font);
        etEmail.setTypeface(font);

        editBtn = getResources().getDrawable(R.drawable.ic_edit);
        editBtn.setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP);
        ivEditProfileImg.setBackground(editBtn);

        etName.setText("" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERNAME));
        etEmail.setText("" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_EMAIL));
        final String url = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERIMAGE);
        if(isFirst) {
            isFirst = false;
            imgProfile.setBackgroundColor(getResources().getColor(R.color.blue));
            imageLoader.displayImage("" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERIMAGE), imgProfile, options);
            if (!TextUtils.isEmpty(url)) {
//            imageLoader.displayImage(url, imgProfile, options);
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

    }



    private void init(){
        final File cacheDir = new File(getActivity().getExternalCacheDir().toString() + "/Profile/");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        final File photo = new File(cacheDir.getAbsolutePath(), "Profile.jpg");
        if (photo.exists()) {
            photo.delete();
        }

        Util.setupOutSideTouchHideKeyboard(getActivity(), mainParent);

        outPutFile = photo;
         Log.e("outPutFile>>>>>>","1"+outPutFile);
    }


    @OnClick(R.id.mlEditProfile)
    public void editProfileImg(View view){
        selectImageOption();
    }

    @OnClick(R.id.rlSubmit)
    public void Submit(View view) {
        if (isEmpty(getText(etName))) {
            Toast.makeText(getActivity(), "please enter name", Toast.LENGTH_SHORT).show();
        } else {
            if (Util.isOnline(getActivity())) {
                String gcmRegId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GCM_REGID);
//                toast("You are already register on GCM ==>> " + gcmRegId);
                new Registration(getText(etEmail), getText(etName),gcmRegId).execute();
            } else Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
        }
    }


    private void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Constant.storageDirectory, System.currentTimeMillis()+".jpg");
//                    outPutFile = new File(Constant.storageDirectory, System.currentTimeMillis() + ".jpg");
//                    globalUri = Uri.fromFile(outPutFile);
                    mImageCaptureUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, CAMERA_CODE);
//                    startCameras();
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


//    protected void startCameras() {
//        if (globalUri != null) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, globalUri);
//            startActivityForResult(intent, CAMERA_CODE);
//        } else {
////            createURI();
//            globalUri = Uri.fromFile(outPutFile);
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:

                    try {
                        mImageCaptureUri = data.getData();
                        System.out.println("Gallery Image URI : " + mImageCaptureUri);
                        CropingIMG();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case CAMERA_CODE:
                // get the Uri for the captured image
//                    new LoadImagesFromSDCard().execute(mImageCaptureUri.getPath());
                    CropingIMG();

                    break;

                case CROPING_CODE:
                    try {
                            Log.e(">>>>>>>>>>>>>>>",""+outPutFile);
                        if (outPutFile.exists()) {
                            Bitmap photo = decodeFile(outPutFile);
                            Log.e("outputfile22",""+outPutFile);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();

                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                            final File cacheDir = new File(getActivity().getExternalCacheDir().toString() + "/Profile/");
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
                            Log.e("outputfile33", "" + camera_pathname.toString());
                            Log.e("outputfile44", "" + Uri.fromFile(new File(camera_pathname)).toString());


                            imgProfile.setBackgroundColor(getResources().getColor(R.color.yellow));
                         //   imageLoader.displayImage(Uri.fromFile(new File(camera_pathname)).toString(), imgProfile, options);
                            staticImg.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(getActivity(), "Error while save image", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

            }
        } else {
            Log.e("TAG", "data null");
        }
    }


    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();

//        final ArrayList cropOptions = new ArrayList();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            toast("Cann't find image croping app");
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

//            intent.putExtra("return-data", true);
            //Create output file here

//            if (type == GALLERY_CODE) {
//                intent.setData(mImageCaptureUri);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
//            } else {
//
//
//
//                intent.setData(globalUri);
//            }

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title = getActivity().getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getActivity().getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getActivity(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            getActivity().getContentResolver().delete(mImageCaptureUri, null, null);
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

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(getActivity(), key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(getActivity(), key);
    }

    protected void toast(CharSequence text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    protected String getText(EditText eTxt) {
        return eTxt == null ? "" : eTxt.getText().toString().trim();
    }

    protected boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    class Registration extends AsyncTask<Void, String, String> {

        String Email, Name, device_id;

        private Registration(String Email, String Name,String gcmRegId) {
            this.Email = Email;
            this.Name = Name;
            this.device_id = gcmRegId;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            CustomDialog.getInstance().show(getActivity(),"");
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String resp = "";
            try {

                JSONObject jData = new JSONObject();
                jData.put("method", "registration");
                jData.put("userid", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
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

                Log.e("camera_pathname", ">>" + camera_pathname);
                if (camera_pathname.length() > 0) {
                    File image = new File(camera_pathname);
                    fbody = new FileBody(image);
                    entity.addPart("file", fbody);
                } else {
                    jData.put("user_image", "" + read(Constant.SHRED_PR.KEY_USERIMAGE));
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
            Log.e("result", ">>" + result);
            if(isAdded()){
                getResources().getString(R.string.app_name);
            }

            try {
                JSONObject jObj = new JSONObject(result);

                toast("" + jObj.getString("msg"));

                int status = jObj.optInt("success");
                if (status == 1) {

                    JSONObject jsonObject = jObj.optJSONObject("data");
                    write(Constant.SHRED_PR.KEY_USERID, "" + jsonObject.optString("userid"));
                    write(Constant.SHRED_PR.KEY_USERNAME, "" + jsonObject.optString("username"));
                    write(Constant.SHRED_PR.KEY_USERIMAGE, "" + jsonObject.optString("user_image"));
                    write(Constant.SHRED_PR.KEY_MOBILE, "" + jsonObject.optString("mobileno"));
                    write(Constant.SHRED_PR.KEY_EMAIL, "" + jsonObject.optString("email"));


                    ((HomeActivity) getActivity()).setUserData();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Async task for loading the images from the SD card.
     *
     * @author Android Example
     */
    // Class with extends AsyncTask class
//    public class LoadImagesFromSDCard extends AsyncTask<String, Void, Bitmap> {
//        protected void onPreExecute() {
//            /****** NOTE: You can call UI Element here. *****/
//        }
//
//        // Call after onPreExecute method
//        protected Bitmap doInBackground(String... urls) {
//            Bitmap bitmap = null;
//            Bitmap newBitmap = null;
//            Uri uri = null;
//            try {
//                bitmap = BitmapFactory.decodeFile(urls[0]);
//                if (bitmap != null) {
//                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
//                    newBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
//                    bitmap.recycle();
//                }
//            } catch (Exception e) {
//                // Error fetching image, try to recover
//                /********* Cancel execution of this task. **********/
//                cancel(true);
//            }
//            return newBitmap;
//        }
//
//        protected void onPostExecute(Bitmap bitmap) {
//            // NOTE: You can call UI Element here.
//            // Close progress dialog
//            if (bitmap != null) {
//                //imgProfile.setImageBitmap(bitmap);
//                CropingIMG(CAMERA_CODE);
//            }
//        }
//    }


}
