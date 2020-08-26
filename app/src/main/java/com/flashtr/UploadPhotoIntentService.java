package com.flashtr;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.flashtr.activity.CaptureActivity;
import com.flashtr.app.MyApp;
import com.flashtr.fragment.CameraFragment;
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
import java.io.InputStreamReader;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class UploadPhotoIntentService extends IntentService {

    public UploadPhotoIntentService() {
        super("UploadPhotoIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final String userId = intent.getStringExtra(Constant.SHRED_PR.KEY_USERID);
            final String albumId = intent.getStringExtra(Constant.SHRED_PR.KEY_Current_AlbumID);
            final String photo = intent.getStringExtra(Constant.SHRED_PR.KEY_Photo_Name);
            final String from = intent.getStringExtra(Constant.SHRED_PR.KEY_Data);
            String resp = "";
            try {
                Log.e(getClass().getSimpleName(), "in condition :: " + photo);
                final JSONObject jData = new JSONObject();
                jData.put("method", "addphoto");
                jData.put("userid", userId);
                jData.put("album_id", albumId);

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
                    resp += line;
                }

                Log.e(getClass().getSimpleName(), ">>" + resp);

                if (!TextUtils.isEmpty(resp)) {
                    final JSONObject jObj = new JSONObject(resp);

                    final int status = jObj.optInt("success");
                    if (status == 1) {

                        if (from.equalsIgnoreCase(CaptureActivity.class.getSimpleName())) {
                            final File file = new File(photo);
                            if (file.exists()) file.delete();

                        }
                        MyApp.uploadingImage.remove(photo);
                        Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_RELOAD_SHARED_PHOTOS, "1");
                        Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_GET_PHOTOS, "");

                        final Intent sendBroadCastIntent = new Intent(Constant.SHRED_PR.KEY_Intent_Filter);
                        sendBroadCastIntent.putExtra(Constant.SHRED_PR.KEY_Data, from);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(sendBroadCastIntent);
                    }else{
                        MyApp.uploadingImage.remove(photo);
                    }
                }else{
                    MyApp.uploadingImage.remove(photo);
                }

            } catch (Exception e) {
                e.printStackTrace();
                MyApp.uploadingImage.remove(photo);
            }
        }

    }
}
