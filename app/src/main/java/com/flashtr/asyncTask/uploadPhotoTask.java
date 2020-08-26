package com.flashtr.asyncTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.flashtr.activity.CaptureActivity;
import com.flashtr.app.MyApp;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by hirenkanani on 31/03/16.
 */
public class uploadPhotoTask extends AsyncTask<Object, Void, Object> {

    private static final String TAG = "uploadPhotoTask";
    private String photo, photo1, photo2, className, user_id, albumid;
    private String uploadResponse = "";
    private Context mContext;
    private Bitmap bitmap;

    public uploadPhotoTask(Context mContext, String user_id, String albumid, String photo, String className) {

        this.mContext = mContext;
        this.user_id = user_id;
        this.albumid = albumid;
        this.photo = photo;
        this.className = className;

    }

    public uploadPhotoTask(Context mContext, String user_id, String albumid, String photo, String className, String photo1, String photo2) {

        this.mContext = mContext;
        this.user_id = user_id;
        this.albumid = albumid;
        this.photo = photo;
        this.photo1 = photo1;
        this.photo2 = photo2;
        this.className = className;

    }

    public Bitmap ScaleImage(String img_path) {
        // Get the source image's dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(img_path, options);

        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

// Only scale if the source is big enough. This code is just trying to fit a image into a certain width.
        int desiredWidth = 1500;

        if (desiredWidth > srcWidth)
            desiredWidth = srcWidth;

// Calculate the correct inSampleSize/scale value. This helps reduce memory use. It should be a power of 2
// from: http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966
        int inSampleSize = 1;
        while (srcWidth / 2 > desiredWidth) {
            srcWidth /= 2;
            srcHeight /= 2;
            inSampleSize *= 2;
        }

        float desiredScale = (float) desiredWidth / srcWidth;

// Decode with inSampleSize
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(img_path, options);

// Resize
        Matrix matrix = new Matrix();
        matrix.postScale(desiredScale, desiredScale);
        Log.d(TAG, "ScaleImage: image path "+img_path);
//        if (img_path.contains("Camera"))
//        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createBitmap(sampledSrcBitmap, 0, 0, sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(), matrix, true);
        sampledSrcBitmap = null;

        return scaledBitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try {

            bitmap = ScaleImage(photo);
            bitmap = modifyOrientation(bitmap,photo);
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            options.inSampleSize = 2;
//            options.inScaled = false;
//            bitmap = BitmapFactory.decodeFile(photo,options);
//            bitmap = modifyOrientation(bitmap, photo);
//            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        photo = saveBitmap(bitmap);

    }


    @Override
    protected Object doInBackground(Object[] params) {

        try {

           /* try {
                Bitmap myBitmap = BitmapFactory.decodeFile(photo);
                ExifInterface exif = new ExifInterface(photo);
                int rotation = 0;
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        break;
                }


                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                Bitmap rotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);

                ContextWrapper cw = new ContextWrapper(mContext);
                // path to /data/data/yourapp/app_data/imageDir
//                                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                // Create imageDir
                File mypath=Util.getOutputMediaFile(mContext);*//*new File(directory,"profile.jpg");*//*

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    fos.close();
                }

                photo = mypath.getAbsolutePath();
                myBitmap.recycle();
                rotatedBitmap.recycle();

            }catch (Exception e){
                e.printStackTrace();
            }*/

            Log.e(getClass().getSimpleName(), "in condition :: " + photo);
            final JSONObject jData = new JSONObject();
            jData.put("method", "addphoto");
            jData.put("userid", user_id);
            jData.put("album_id", albumid);

            final HttpClient client = new DefaultHttpClient();
            HttpResponse response = null;
            final HttpPost poster = new HttpPost(
                    Constant.URL);

            FileBody fbody = null;
            final MultipartEntity entity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);

            if (!TextUtils.isEmpty(photo)) {
                File image = new File(photo);
                fbody = new FileBody(image);
                entity.addPart("file", fbody);
            }

            Log.e("camera_pathname", ">>" + jData);
            entity.addPart("data", new StringBody(jData.toString()));
            poster.setEntity(entity);

            response = client.execute(poster);
            final BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity()
                            .getContent()));
            String line = null;
            while ((line = rd.readLine()) != null) {
                uploadResponse += line;
            }

            Log.e(getClass().getSimpleName(), ">>" + uploadResponse);

            if (!TextUtils.isEmpty(uploadResponse)) {
                final JSONObject jObj = new JSONObject(uploadResponse);

                final int status = jObj.optInt("success");
                if (status == 1) {

                    if (className.equalsIgnoreCase(CaptureActivity.class.getSimpleName())) {

                        /*String albumId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumID);
                        File[] dir = new File(mContext.getExternalCacheDir().toString() + "/Album/" + albumId + "/").listFiles();
                        File[] dir1 = new File(mContext.getExternalCacheDir().toString() + "/Albums/" + albumId + "/").listFiles();

                        String photoName = photo.substring(photo.lastIndexOf("/"));
                        for (File f : dir) {
                            if(f.getName().contains(photoName)&&f.exists()){
                                f.delete();
                            }
                        }

                        for (File f : dir1) {
                            if(f.getName().contains(photoName)&&f.exists()){
                                f.delete();
                            }
                        }*/

                        final File file = new File(photo);
                        if (file.exists()) {
                            file.delete();
                        }

                        final File file1 = new File(photo1);
                        if (file1.exists()) {
                            file1.delete();
                        }

                        if (photo2 != null && photo2.length() > 0) {
                            final File file2 = new File(photo2);
                            if (file2.exists()) {
                                file2.delete();
                            }
                        }

                        /*if (dir.isDirectory())
                        {
                            String[] children = dir.list();
                            for (int i = 0; i < children.length; i++)
                            {
                                new File(dir, children[i]).delete();
                            }
                        }
                        if (dir1.isDirectory())
                        {
                            String[] children = dir1.list();
                            for (int i = 0; i < children.length; i++)
                            {
                                new File(dir1, children[i]).delete();
                            }
                        }*/
                    }
                    MyApp.uploadingImage.remove(photo);
                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_RELOAD_SHARED_PHOTOS, "1");
                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_RELOAD_Home, "5");
                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_GET_PHOTOS, "");

                    final Intent sendBroadCastIntent = new Intent(Constant.SHRED_PR.KEY_Intent_Filter);
                    sendBroadCastIntent.putExtra(Constant.SHRED_PR.KEY_Data, className);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(sendBroadCastIntent);
                } else {
                    MyApp.uploadingImage.remove(photo);
                }
            } else {
                MyApp.uploadingImage.remove(photo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MyApp.uploadingImage.remove(photo);
        }
        return null;

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(uploadResponse);
        if (bitmap != null) {
            bitmap.recycle();
        }

        Log.d(TAG, "onPostExecute:  upload success");
    }


    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(image_absolute_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exif != null;
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

//        ExifInterface ei = new ExifInterface(image_absolute_path);
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                bitmap = rotate(bitmap, 90);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                bitmap = rotate(bitmap, 180);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                bitmap = rotate(bitmap, 270);
//                break;
//            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//                bitmap = flip(bitmap, true, false);
//                break;
//            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//                bitmap = flip(bitmap, false, true);
//                break;
//        }

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
        }


        return bitmap;
    }


    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        /*Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);*/
        Matrix matrix = new Matrix();

        matrix.postRotate(degrees);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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
                + mContext.getPackageName()
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
//            bt  = scaleDown(bt, 800, true);
            bt.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("FLASHTR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("FLASHTR", "Error accessing file: " + e.getMessage());
        }

        return pictureFile.getAbsolutePath();
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}

