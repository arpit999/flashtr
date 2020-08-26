package com.flashtr.app;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hirenkanani on 29/12/15.
 */
public class MyApp extends Application /*implements IAviaryClientCredentials*/ {

    /* While uploading recent photo manage uploading photo in this stack
    *  when uploading start at click of button in adapter photo path is added in this variable and after getting result file deleted and
    *  name remove from this variable, in all cases while uploading like fire exception/net erro etc.. file path remove from this variable
    *  in Fragment while creating list of unsynced photo check that this variable contain that file path or not is contain then that file
    *  not added in list other added in list */
    public static List<String> uploadingImage;

    public static boolean disableFragmentAnimations = false;

    public static int deleteAlbumPos = -1;
    public static int isDeleteAlbum = -1;


    /* Be sure to fill in the two strings below. */
    private static final String CREATIVE_SDK_CLIENT_ID = /*Keys.CSDK_CLIENT_ID*/"0ff15a977f434d5aacdc1f4114c7a8d8";
    private static final String CREATIVE_SDK_CLIENT_SECRET = /*Keys.CSDK_CLIENT_SECRET*/"21149967-14d3-47ff-8fbb-ac8dc305e7bd";

    public static int gridSelected = 0;

    @Override
    public void onCreate() {
        super.onCreate();
       /* AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());*/
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "assets/SEGOEUI.TTF"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        uploadingImage = new ArrayList<String>();
    }

    /*@Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }


    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String getBillingKey() {
        return ""; // Leave this blank
    }*/

}
