package com.flashtr.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.flashtr.HomeActivity;
import com.flashtr.R;
import com.flashtr.activity.TabActivity;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;

/**
 * Created by hirenkanani on 02/12/15.
 */
public class GCMNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "GCMNotificationIntentService";
    NotificationCompat.Builder builder;
    String title, message, albumId, albumName;
    private NotificationManager mNotificationManager;
    private int isAddedUser;

    public GCMNotificationIntentService() {
        super(Constant.SENDER_ID);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Log.e("ON HANDLE", "----" + extras);
        title = extras.getString(Constant.TITLE_KEY);
        albumName = title;
        albumId = extras.getString(Constant.SHRED_PR.KEY_Current_AlbumID);
     try{
        isAddedUser = Integer.parseInt(extras.getString(Constant.SHRED_PR.KEY_IS_USER_ADDED));
        } catch (NumberFormatException e) {
        //handle error
        }
        Log.e("isAddedUser",""+isAddedUser);
        /*Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_Current_AlbumName, albumName);
        Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_Current_AlbumID, albumId);
        Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_RELOAD_Home, "1");*/
        HomeActivity.selectedPosition = 0;

        Log.e("albumName", "" + albumName);
        Log.e("albumId", "" + albumId);

        if (!TextUtils.isEmpty(extras.getString(Constant.MESSAGE_KEY))) {
            sendNotification(extras.getString(Constant.MESSAGE_KEY));
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        Log.d("TAG", "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent in = new Intent(this, TabActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        in.putExtra("activity", "HomeActivity");
        in.putExtra("pos", 1);
        in.putExtra(Constant.SHRED_PR.KEY_IS_USER_ADDED, isAddedUser);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                in, PendingIntent.FLAG_UPDATE_CURRENT);

        int numMessages = 0;
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setContentText(msg)
                .setNumber(++numMessages);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d("TAG", "Notification sent successfully.");
    }
}