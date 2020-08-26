package com.flashtr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.adapter.AlbumDetailsMembersAdapter;
import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

public class AlbumDetailsActivity extends BaseActivity {

    public static AlbumDetailsActivity albumDetailsActivity;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.lvMembers)
    ListView lvMembers;
    @InjectView(R.id.img)
    ImageView imgAlbum;
    @InjectView(R.id.rlExitAlbum)
    MaterialRippleLayout rlExitAlbum;
    @InjectView(R.id.rlDeleteAlbum)
    MaterialRippleLayout rlDeleteAlbum;
    @InjectView(R.id.rlUpdateAlbum)
    MaterialRippleLayout rlUpdateAlbum;
    @InjectView(R.id.rlExit)
    RelativeLayout rlExit;
    @InjectView(R.id.llBottom)
    LinearLayout llBottom;
    @InjectView(R.id.rlBack)
    MaterialRippleLayout rlBack;
    @InjectView(R.id.ivBack)
    ImageView ivBack;
    @InjectView(R.id.main)
            RelativeLayout main;
    int FROM = 0;
    DisplayImageOptions options;
//    ImageLoader imageLoader = ImageLoader.getInstance();
    ArrayList<HashMap<String, String>> listMembers = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    private String activity;
    private Drawable backArrow;
    private HashMap<String, String> listContacts = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_album_details);
        main.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        init();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("activity").equalsIgnoreCase("HomeActivity")) {
                activity = extras.getString("activity");
                FROM = extras.getInt("from");
                hashMap = (HashMap<String, String>) extras.getSerializable("map");
            } else {
                FROM = extras.getInt("from");
                hashMap = (HashMap<String, String>) extras.getSerializable("map");
            }


            new ContactsAsyncTask().execute();
        }

    }

    @OnClick(R.id.rlBack)
    public void onBack(View view) {
        /*Intent intent = new Intent(AlbumDetailsActivity.this, HomeActivity.class);
        startActivity(intent);*/
        finish();
    }


    @OnClick(R.id.rlExitAlbum)
    public void ExitAlbum(View view) {
        AlertDialog.Builder alert1 = new AlertDialog.Builder(AlbumDetailsActivity.this);
        alert1.setTitle("" + Constant.AppName);
        alert1.setMessage("Are you sure do you want to exit from this Album?");
        alert1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @SuppressLint("InlinedApi")
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (Util.isOnline(getApplicationContext())) {
                            String albumId = "" + hashMap.get("album_id");
                            new exitAlbum(albumId).execute();
                        } else {
                            toast(Constant.network_error);
                        }
                    }
                }
        );
        alert1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }

        );
        alert1.create();
        alert1.show();

    }


    @OnClick(R.id.rlUpdateAlbum)
    @SuppressWarnings("unused")
    public void UpdateAlbum(View view) {

        Intent intent = new Intent(AlbumDetailsActivity.this, NewAlbumActivity.class);
        intent.putExtra("map", hashMap);
        intent.putExtra("activity", "AlbumDetailsActivity");
        startActivity(intent);
    }

    @OnClick(R.id.rlDeleteAlbum)
    @SuppressWarnings("unused")
    public void DeleteAlbum(View view) {

        AlertDialog.Builder alert1 = new AlertDialog.Builder(AlbumDetailsActivity.this);
        alert1.setTitle("" + Constant.AppName);
        alert1.setMessage("Are you sure do you want to delete this Album?");
        alert1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @SuppressLint("InlinedApi")
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (Util.isOnline(getApplicationContext())) {
                            String albumId = "" + hashMap.get("album_id");
                            new deleteAlbum(albumId).execute();
                        } else {
                            toast(Constant.network_error);
                        }

                    }
                }
        );
        alert1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }

        );
        alert1.create();
        alert1.show();
    }

    @OnClick(R.id.img)
    @SuppressWarnings("unused")
    public void Display(View view) {

        if (hashMap != null) {
            String path = "" + hashMap.get("album_cover_image");
            if (path.length() > 0) {
                Intent intent = new Intent(AlbumDetailsActivity.this, DisplayFullscreenImage.class);
                intent.putExtra("path", "" + path);
                startActivity(intent);
            }
        }
    }


    private void init() {
        albumDetailsActivity = this;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
//        imageLoader.init(ImageLoaderConfiguration
//                .createDefault(getApplicationContext()));
        backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        backArrow.setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.SRC_ATOP);
        ivBack.setBackground(backArrow);

//        lvMembers.setOnTouchListener(new View.OnTouchListener() {
//            // Setting on Touch Listener for handling the touch inside ScrollView
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // Disallow the touch request for parent scroll on touch of child view
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });

    }

    private void setData() {
        Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_AdminId, hashMap.get("admin_id"));
        Log.e("ADMIN", " " + hashMap.get("admin_id"));
        if (read(Constant.SHRED_PR.KEY_USERID).equals("" + hashMap.get("admin_id"))) {
            llBottom.setVisibility(View.VISIBLE);
            rlExit.setVisibility(View.GONE);
        } else {
            llBottom.setVisibility(View.GONE);
            rlExit.setVisibility(View.VISIBLE);
        }

        tvTitle.setText("" + hashMap.get("album_name"));
//        imageLoader.displayImage("" + hashMap.get("album_cover_image"), imgAlbum, options);

        //Add admin
        HashMap<String, String> hashMap2 = new HashMap<>();
        hashMap2.put("member_id", "" + hashMap.get("admin_id"));
        hashMap2.put("member_name", "" + hashMap.get("admin_name"));
        hashMap2.put("member_image", "" + hashMap.get("admin_dp"));
        hashMap2.put("member_number", "" + hashMap.get("admin_mobileno"));
        listMembers.add(hashMap2);

        try {
            JSONArray jsonArray = new JSONArray("" + hashMap.get("member_details"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> hashMap1 = new HashMap<>();
                hashMap1.put("member_id", "" + jsonObject.optString("member_id"));
                hashMap1.put("member_name", "" + jsonObject.optString("member_name"));
                hashMap1.put("member_image", "" + jsonObject.optString("member_image"));
                hashMap1.put("member_number", "" + jsonObject.optString("member_number"));

                if (!hashMap1.get("member_id").equals(hashMap.get("admin_id")))
                    listMembers.add(hashMap1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        lvMembers.setDivider(null);
        listMembers.trimToSize();

        if (listMembers != null && !listMembers.isEmpty()) {
            if (listContacts != null && !listContacts.isEmpty()) {

                for (Map.Entry<String, String> map : listContacts.entrySet()) {
                    for (int i = 0; i < listMembers.size(); i++) {
                        final HashMap<String, String> hashMap = listMembers.get(i);
                        if (hashMap.get("member_number").equalsIgnoreCase(map.getKey())) {
                            hashMap.put("member_name", map.getValue());
                            break;
                        }
                    }
                }

            }
        }


        lvMembers.setAdapter(new AlbumDetailsMembersAdapter(getApplicationContext(), listMembers));


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent = new Intent(AlbumDetailsActivity.this, HomeActivity.class);
        startActivity(intent);*/
        finish();
    }

    private void getContacts() {
        listContacts.clear();
        final Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (phones != null) {
            phones.moveToFirst();
            do {
                final String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                phoneNumber = phoneNumber.replaceAll("\\D+", "");
                if (phoneNumber.length() >= 10) {
                    phoneNumber = Util.lastN(phoneNumber, 10);
                    listContacts.put(phoneNumber, name);
                }
            } while (phones.moveToNext());
            phones.close();
        }
    }

    class deleteAlbum extends AsyncTask<Void, String, String> {

        String albumId;
        String response;

        private deleteAlbum(String albumId) {
            this.albumId = albumId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(AlbumDetailsActivity.this,"");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "deletealbum");
                jData.put("userid", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
                jData.put("album_id", "" + albumId);


                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("data", jData.toString()));
                response = Util.makeServiceCall(Constant.URL, 1, params1);
                Log.e("params1", ">>" + params1);

                Log.e("** response is:- ", ">>" + response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            CustomDialog.getInstance().hide();
            try {
                JSONObject jObj = new JSONObject(result);
                int status = jObj.optInt("success");

                if (status == 1) {
                    if (FROM == 0) {
                        write(Constant.SHRED_PR.KEY_RELOAD_Albums, "1");
                        if (read(Constant.SHRED_PR.KEY_Current_AlbumID).equals(albumId))
                            write(Constant.SHRED_PR.KEY_RELOAD_Home, "2");
                    } else {
                        write(Constant.SHRED_PR.KEY_RELOAD_Home, "2");
                    }
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class exitAlbum extends AsyncTask<Void, String, String> {

        String albumId;
        String response;

        private exitAlbum(String albumId) {
            this.albumId = albumId;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(AlbumDetailsActivity.this,"");
        }


        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "exitalbum");
                jData.put("userid", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
                jData.put("album_id", "" + albumId);


                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("data", jData.toString()));
                response = Util.makeServiceCall(Constant.URL, 1, params1);
                Log.e("params1", ">>" + params1);

                Log.e("** ExitAlbum:- ", ">>" + response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            CustomDialog.getInstance().hide();
            try {
                JSONObject jObj = new JSONObject(result);
                int status = jObj.optInt("success");

                if (status == 1) {
                    if (FROM == 0) {
                        write(Constant.SHRED_PR.KEY_RELOAD_Albums, "1");
                        if (read(Constant.SHRED_PR.KEY_Current_AlbumID).equals(albumId))
                            write(Constant.SHRED_PR.KEY_RELOAD_Home, "2");
                    } else {
                        write(Constant.SHRED_PR.KEY_RELOAD_Home, "2");
                    }
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class ContactsAsyncTask extends AsyncTask<Void, Void, Void> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(AlbumDetailsActivity.this,"");
        }

        @Override
        protected Void doInBackground(Void... params) {
            getContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CustomDialog.getInstance().hide();
            setData();
        }
    }
}
