package com.flashtr;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.adapter.SpinnerAdapter;
import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class SelectAlbumActivity extends BaseActivity {

    @InjectView(R.id.tvError)
    TextView tvError;
    @InjectView(R.id.lvAlbums)
    ListView lvAlbums;
    @InjectView(R.id.tvSelectAlbum)
    TextView tvSelectAlbum;
    @InjectView(R.id.rlLogo)
    MaterialRippleLayout rlLogo;
    @InjectView(R.id.remMain)
    RelativeLayout remMain;
//    @InjectView(R.id.back)
//    ImageButton backButton;
    String albumId;
    ArrayList<HashMap<String, String>> listAlbums = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_select_album);
        remMain.setPadding(0, Util.getStatusBarHeight(this), 0, 0);

        albumId = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_Last_Updated_Album);

//        final Drawable backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        backArrow.setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP);
//        backButton.setBackground(backArrow);
        rlLogo.setBackgroundColor(getResources().getColor(R.color.transparent));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
        tvSelectAlbum.setTypeface(typeface);


        if (Util.isOnline(this)) {
            new getAlbums().execute();
        } else {
            loadOfflineAlbums();
        }
//    else
//            reload(result);

    }

    @OnClick(R.id.rlLogo)
    public void goBack(View view) {
        onBackPressed();
    }

    private void reload(String result) {

        listAlbums.clear();
        try {
            JSONObject jObj = new JSONObject(result);
            int status = jObj.optInt("success");

            if (status == 1) {
                Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_Albums, "" + result);
                JSONArray jsonArray = jObj.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("album_id", "" + jsonObject.optString("album_id"));
                    hashMap.put("album_name", "" + jsonObject.optString("album_name"));
                    hashMap.put("album_cover_image", "" + jsonObject.optString("album_cover_image"));
                    hashMap.put("shared_with_me", "" + jsonObject.optString("shared_with_me"));
                    hashMap.put("created_by_me", "" + jsonObject.optString("created_by_me"));
                    hashMap.put("admin_id", "" + jsonObject.optString("admin_id"));
                    hashMap.put("admin_dp", "" + jsonObject.optString("admin_dp"));
                    hashMap.put("admin_name", "" + jsonObject.optString("admin_name"));
                    hashMap.put("member_details", "" + jsonObject.optString("member_details"));
                    hashMap.put("photo_count", "" + jsonObject.optString("photo_count"));
                    hashMap.put("member_count", "" + jsonObject.optString("member_count"));
                    hashMap.put("create_date", "" + jsonObject.optString("create_date"));
                    hashMap.put("modify_date", "" + jsonObject.optString("modify_date"));

                    listAlbums.add(hashMap);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        lvAlbums.setDivider(null);
        lvAlbums.setAdapter(new SpinnerAdapter(SelectAlbumActivity.this, listAlbums));

        if (listAlbums.size() > 0) tvError.setVisibility(View.GONE);
        else tvError.setVisibility(View.VISIBLE);

    }

    class getAlbums extends AsyncTask<Void, String, String> {

        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(SelectAlbumActivity.this,"");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "getalbum");
                jData.put("userid", "" + Util.ReadSharePrefrence(SelectAlbumActivity.this, Constant.SHRED_PR.KEY_USERID));

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
            if (!TextUtils.isEmpty(result)) {
                reload(result);
            }

        }
    }

    private void loadOfflineAlbums() {
        final String result = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_Albums);
        if (!TextUtils.isEmpty(result)) {
            reload(result);
        }
    }

}
