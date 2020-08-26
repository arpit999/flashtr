package com.flashtr.util;


/**
 * Created by BVK on 09-07-2015.
 */

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flashtr.app.MyApp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class Util {


    private static final String TAG = Util.class.getSimpleName();

    public static boolean isOnline(Context context) {
        if (context != null) {
            final ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mgr != null) {

                final NetworkInfo mobileInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                final NetworkInfo wifiInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (wifiInfo != null && wifiInfo.isAvailable() && wifiInfo.isAvailable() && wifiInfo.isConnected()) {

                    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo wifiInfoStatus = wifiManager.getConnectionInfo();
                    final SupplicantState supState = wifiInfoStatus.getSupplicantState();

                    if (String.valueOf(supState).equalsIgnoreCase("COMPLETED") || String.valueOf(supState).equalsIgnoreCase("ASSOCIATED")) {
                        // WiFi is connected
                        return true;
                    }
                }

                if (mobileInfo != null && mobileInfo.isAvailable() && mobileInfo.isConnected()) {
                    // Mobile Network is connected
                    return true;
                }
            }
        }
        return false;
    }

    public static String postData(List<NameValuePair> nameValuePairs, String url) {
        // TODO Auto-generated method stub
        String responseStr = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        Log.e("reqURL", "" + url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                responseStr = EntityUtils.toString(resEntity).trim();
                // you can add an if statement here and do other actions based
                // on the response
            }

            Log.e("Response-->", responseStr);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseStr;
    }

    public static String makeServiceCall(String url, int method,
                                         List<NameValuePair> params) {

        String response = null;
        int GET = 1;
        int POST = 2;
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }

                Log.i("Url", url);
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            final InputStream is = httpEntity.getContent();
            response = convertStreamToString(is);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }

    public static  String getAppVersion(Context context)
    {
        String version = "";
        try
        {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // Log.v("getAppVersion", "App Version: "+ version);
        return version;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void writeToFile(String data, String file_name,
                                   Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(file_name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(String file_name, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file_name);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(
                        inputStream);
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void WriteSharePrefrence(Context context, String key,
                                           String value) {
        @SuppressWarnings("static-access")
//        SharedPreferences write_Data = context.getSharedPreferences(
//                Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);
//        Editor editor = write_Data.edit();
//        editor.putString(key, values);
//        editor.apply();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String ReadSharePrefrence(Context context, String key) {
//        SharedPreferences read_data = context.getSharedPreferences(
//                Constant.SHRED_PR.SHARE_PREF, context.MODE_PRIVATE);
//
//        return read_data.getString(key, "");

        String data;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        data = preferences.getString(key, "");
        editor.commit();
        return data;
    }

    public static void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof Button /*etc.*/)
                ((TextView) v).setTypeface(font);
            else if (v instanceof ViewGroup)
                setFont((ViewGroup) v, font);
        }
    }


    public static Typeface getFont(int n, Context ctx){
        Typeface mFont = null;
        switch (n){
            case 1:
                mFont = Typeface.createFromAsset(ctx.getAssets(), "SEGOEUI.TTF");
                break;
            case 2:
                mFont = Typeface.createFromAsset(ctx.getAssets(), "SEGOEUIB.TTF");
                break;
        }
    return mFont;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String utcToLocalTime(String dtStart) {
        if (dtStart != null && dtStart.length() > 5) {
            String newDate = "";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date localTime = format.parse(dtStart);
                newDate = sdf.format(localTime);
                return newDate;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newDate;
        }
        return "";
    }


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String lastN(String x, int n) {
        return x.substring(x.length() - n, x.length());
    }

//    public static Animation loadAnimation(Context ctx){
//        // animation on click
//        return AnimationUtils.loadAnimation(ctx, R.anim.blink);
//    }

    public static int getOfflinePic(Context inContext){
//        if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home).equals("5")) {
        ArrayList<HashMap<String, String>> listRecentPhotos = new ArrayList<HashMap<String, String>>();
        //For Recent Photos:
        try {
            listRecentPhotos.clear();
            String albumId = Util.ReadSharePrefrence(inContext, Constant.SHRED_PR.KEY_Current_AlbumID);

            File[] files = new File(inContext.getExternalCacheDir().toString() + "/Album/" + albumId + "/").listFiles();
            File[] files1 = new File(inContext.getExternalCacheDir().toString() + "/Albums/" + albumId + "/").listFiles();
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
                            if(!MyApp.uploadingImage.contains(files[i].toString()))
                                listRecentPhotos.add(hashMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listRecentPhotos.size();
    }

    public static int getOfflinePic(Context inContext,String albumIdPad){
//        if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Home).equals("5")) {
        ArrayList<HashMap<String, String>> listRecentPhotos = new ArrayList<HashMap<String, String>>();
        //For Recent Photos:
        try {
            listRecentPhotos.clear();
            String albumId = albumIdPad;/*Util.ReadSharePrefrence(inContext, Constant.SHRED_PR.KEY_Current_AlbumID);*/

            File[] files = new File(inContext.getExternalCacheDir().toString() + "/Album/" + albumId + "/").listFiles();
            File[] files1 = new File(inContext.getExternalCacheDir().toString() + "/Albums/" + albumId + "/").listFiles();
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
                            if(!MyApp.uploadingImage.contains(files[i].toString()))
                                listRecentPhotos.add(hashMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listRecentPhotos.size();
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static int[] getScreenWidthHeight(Context mContext){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        int[] wh = {width,height};
        return wh;

    }

    /* To hidekeyboard on particular view:
    @param  v : View on which you want to hide keyboard
     mContext : Context of that class*/

    public static void hideKeyboard(View view,Context mContext) {
        InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @SuppressLint("ClickableViewAccessibility")
    public static void setupOutSideTouchHideKeyboard(final Context mContext,final View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(view,mContext);
                    return false;
                }
            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupOutSideTouchHideKeyboard(mContext,innerView);
            }
        }
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(Context mContext){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + File.separator
                + ".flashtr"
                + File.separator
                + "/Sent");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName=Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumID)+"_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    // A method to find height of the status bar
    public static int getStatusBarHeight(Context mContext) {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //  Check for soft navigation bar availablity
    public static boolean isNavigationBarAvailable() {

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        return (!(hasBackKey && hasHomeKey));
    }

    public static boolean checkPermission(String Permissionname, Context ctx) {

//        boolean temp = false;


//        int permissionCheck = ContextCompat.checkSelfPermission(ctx,Permissionname);

//        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
//            temp = true;
//        }else{
//            temp = false;
//        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            try {
                AppOpsManager appOps = (AppOpsManager) ctx
                        .getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps.checkOpNoThrow(Permissionname,
                        android.os.Process.myUid(), ctx.getPackageName());
                boolean granted = mode == AppOpsManager.MODE_ALLOWED;
                return granted;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        } else {
            return true;
        }
    }
}