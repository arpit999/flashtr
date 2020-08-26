package com.flashtr.util;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
 * Created by BVK on 09-07-2015.
 */
public class Constant {

    /* Live URL */
        public static String URL = "http://52.25.157.214/flashtr/webservice/service.php";
    /* Demo URL */
//    public static String URL = "http://52.0.55.172/flashtr/webservice/service.php";

    //	Tab Name
    public final static String TAB_FRAGMENT_ALBUM = "FragmentAlbum";
    public final static String TAB_FRAGMENT_NEW_ALBUM = "FragmentNewAlbum";
    public final static String TAB_FRAGMENT_HOME = "FragmentHome";
    public final static String TAB_FRAGMENT_FAVS = "FragmentFavs";
    public final static String TAB_FRAGMENT_SETS = "FragmentSets";

    public final static String TAB_FRAGMENT_CAMERA = "FragmentCamera";


    // Google project id
//    public static String SENDER_ID = "347966281841";
    public static String SENDER_ID = "153421525786";

    public static String network_error = "please check your network connectivity.";
    public static String MESSAGE_KEY = "message";
    public static String TITLE_KEY = "title";
    public static String METHOD_KEY = "method";
    public static String AppName = "Flashtr";
    public static String storageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + AppName;

    public final static HashMap<Integer,Fragment> map = new HashMap<Integer,Fragment>();

    public static int SharedPics = 0;
    public static int RecentPics = 1;


    public static class SHRED_PR {
        public static final String SHARE_PREF = AppName + "_preferences";
        public static final String KEY_IS_LOGGEDIN = "is_loggedin";
        public static final String KEY_VERIFICATION_CODE = "verification_code";
        public static final String KEY_MOBILE = "mobile";
        public static final String KEY_MOBILE_NEW = "mobile";
        public static final String KEY_USERID = "userid";
        public static final String KEY_USERNAME = "username";
        public static final String KEY_USERIMAGE = "userimage";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_RELOAD_Albums = "reload_albums";
        public static final String KEY_RELOAD_Home = "reload_home";
        public static final String KEY_RELOAD_SHARED_PHOTOS = "reload_shared_photos";
        public static final String KEY_Albums = "albums";
        public static final String KEY_Current_AlbumID = "albumid";
        public static final String KEY_Current_AlbumName = "albumname";
        public static final String KEY_Data = "data";
        public static final String KEY_Photo_Name = "photo_name";
        public static final String KEY_Intent_Filter = "upload_photo_action";
        public static final String KEY_Angle = "angle";
        public static final String KEY_Share_Button = "sharebutton";
        public static final String KEY_Is_FirstTime = "isFirstTime";
        public static final String KEY_GCM_REGID = "gcmRegId";
        public static final String KEY_Last_Updated_Album = "lastUpdatedAlbumId";
        public static final String KEY_AdminId = "admin_id";
        public static final String KEY_GET_PHOTOS = "key_getphotos";
        public static final String KEY_GET_PHOTO_COUNT = "key_getphotoCount";
        public static final String KEY_IS_USER_ADDED = "is_user_added";
        public static final String KEY_PAGER_POSITION = "pager_position";
        public static final String KEY_SELECTED_TAB = "selected_tab";
        public static final String KEY_CREATEDBYME = "created_by_me";

    }


}
