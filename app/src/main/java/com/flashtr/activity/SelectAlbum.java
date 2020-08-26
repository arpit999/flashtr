package com.flashtr.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.BaseActivity;
import com.flashtr.R;
import com.flashtr.adapter.AdpAllAlbum;
import com.flashtr.model.AllAlbum;
import com.flashtr.model.Favorite;
import com.flashtr.model.MemberDetail;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 24B on 5/9/2016.
 */
public class SelectAlbum extends BaseActivity {

    TextView tv_txtHeader, txt_Cancel;
    ListView albumListView;
    LinearLayout rlCancel;
    TextView tv_txtNoData;
    private Typeface font;
    RelativeLayout main;
    ProgressBar progressBar7;

    private List<AllAlbum> albumList = new ArrayList<AllAlbum>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.fragment_select);

        main = (RelativeLayout) findViewById(R.id.main);

        main.setPadding(0, Util.getStatusBarHeight(this), 0, 0);

        progressBar7 = (ProgressBar) findViewById(R.id.progressBar7);
        tv_txtHeader = (TextView) findViewById(R.id.tv_txtHeader);
        albumListView = (ListView) findViewById(R.id.albumList);
        tv_txtNoData = (TextView) findViewById(R.id.tv_txtNoData);
        txt_Cancel = (TextView) findViewById(R.id.txt_Cancel);

        font = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
        tv_txtHeader.setTypeface(font);
        tv_txtNoData.setTypeface(font);

        txt_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        String userId = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID);
        if (albumList.isEmpty()) {
            setDataAdapter();
            new getAlbums(userId, false).execute();
        } else {

            new getAlbums(userId, true).execute();
        }
    }

    class getAlbums extends AsyncTask<Void, String, String> {

        String response;
        String userId;
        boolean isDialogShow;

        private getAlbums(String userId, boolean isDialogShow) {
            this.userId = userId;
            this.isDialogShow = isDialogShow;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isDialogShow)
//                CustomDialog.getInstance().show(SelectAlbum.this, "");
                progressBar7.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "getalbum");
                jData.put("userid", "" + userId);
                jData.put("page", "" + "");

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
//            if (isDialogShow)
//                CustomDialog.getInstance().hide();
            progressBar7.setVisibility(View.GONE);
            if(Util.isOnline(SelectAlbum.this)) {
                if (result != null && result.length() > 0) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        int status = jObj.optInt("success");

                        if (status == 1) {

                            Util.WriteSharePrefrence(SelectAlbum.this, Constant.SHRED_PR.KEY_Albums, "" + result);

                            albumListView.setVisibility(View.VISIBLE);
                            tv_txtNoData.setVisibility(View.GONE);

                            final JSONArray jsonArray = jObj.getJSONArray("data");
                            albumList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);

                                AllAlbum fav = new AllAlbum();
                                fav.setAlbum_id(jsonObject.optString("album_id"));
                                fav.setAlbum_name(jsonObject.optString("album_name"));
                                fav.setCreate_date(jsonObject.optString("create_date"));
                                fav.setModify_date(jsonObject.optString("modify_date"));
                                fav.setAlbum_cover_thumb_image(jsonObject.optString("album_cover_thumb_image"));
                                fav.setAlbum_cover_image(jsonObject.optString("album_cover_image"));
                                fav.setShared_with_me(jsonObject.optInt("shared_with_me"));
                                fav.setCreated_by_me(jsonObject.optInt("created_by_me"));
                                fav.setAdmin_id(jsonObject.optString("admin_id"));
                                fav.setAdmin_dp(jsonObject.optString("admin_dp"));
                                fav.setAdmin_name(jsonObject.optString("admin_name"));
                                fav.setAdmin_mobileno(jsonObject.optString("admin_mobileno"));
                                fav.setPhoto_count(jsonObject.optString("photo_count"));
                                fav.setMember_count(jsonObject.optInt("member_count"));

//                        JSONObject jObj1 = jsonObject.getJSONObject("total_likes");
                                fav.setCount(jsonObject.optString("total_likes"));

                                JSONArray jArray = jsonObject.getJSONArray("member_details");
                                List<MemberDetail> list = new ArrayList<MemberDetail>();
                                for (int j = 0; j < jArray.length(); j++) {
                                    JSONObject jObj2 = jArray.getJSONObject(j);
                                    MemberDetail mamDetail = new MemberDetail();
                                    mamDetail.setMember_id(jObj2.optString("member_id"));
                                    mamDetail.setMember_name(jObj2.optString("member_name"));
                                    mamDetail.setMember_image(jObj2.optString("member_image"));
                                    mamDetail.setMember_number(jObj2.optString("member_number"));
                                    mamDetail.setIsadmin(jObj2.optInt("isadmin"));
                                    list.add(mamDetail);
                                }


                                fav.setMember_details(list);
                                albumList.add(fav);
                            }
                            setDataAdapter();
                        } else {
                            albumListView.setVisibility(View.GONE);
                            tv_txtNoData.setVisibility(View.VISIBLE);
                            tv_txtNoData.setText("No Album(s) found");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        albumListView.setVisibility(View.GONE);
                        tv_txtNoData.setVisibility(View.VISIBLE);
                        tv_txtNoData.setText("No Album(s) found");
                    }
                } else {

                    try {
                        String result1 = Util.ReadSharePrefrence(SelectAlbum.this, Constant.SHRED_PR.KEY_Albums);

                        if(result1!=null && result1.length()>0) {
                            JSONObject jObj = new JSONObject(result1);
                            Toast.makeText(SelectAlbum.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

                            final JSONArray jsonArray = jObj.getJSONArray("data");
                            albumList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);

                                AllAlbum fav = new AllAlbum();
                                fav.setAlbum_id(jsonObject.optString("album_id"));
                                fav.setAlbum_name(jsonObject.optString("album_name"));
                                fav.setCreate_date(jsonObject.optString("create_date"));
                                fav.setModify_date(jsonObject.optString("modify_date"));
                                fav.setAlbum_cover_thumb_image(jsonObject.optString("album_cover_thumb_image"));
                                fav.setAlbum_cover_image(jsonObject.optString("album_cover_image"));
                                fav.setShared_with_me(jsonObject.optInt("shared_with_me"));
                                fav.setCreated_by_me(jsonObject.optInt("created_by_me"));
                                fav.setAdmin_id(jsonObject.optString("admin_id"));
                                fav.setAdmin_dp(jsonObject.optString("admin_dp"));
                                fav.setAdmin_name(jsonObject.optString("admin_name"));
                                fav.setAdmin_mobileno(jsonObject.optString("admin_mobileno"));
                                fav.setPhoto_count(jsonObject.optString("photo_count"));
                                fav.setMember_count(jsonObject.optInt("member_count"));

//                        JSONObject jObj1 = jsonObject.getJSONObject("total_likes");
                                fav.setCount(jsonObject.optString("total_likes"));

                                JSONArray jArray = jsonObject.getJSONArray("member_details");
                                List<MemberDetail> list = new ArrayList<MemberDetail>();
                                for (int j = 0; j < jArray.length(); j++) {
                                    JSONObject jObj2 = jArray.getJSONObject(j);
                                    MemberDetail mamDetail = new MemberDetail();
                                    mamDetail.setMember_id(jObj2.optString("member_id"));
                                    mamDetail.setMember_name(jObj2.optString("member_name"));
                                    mamDetail.setMember_image(jObj2.optString("member_image"));
                                    mamDetail.setMember_number(jObj2.optString("member_number"));
                                    mamDetail.setIsadmin(jObj2.optInt("isadmin"));
                                    list.add(mamDetail);
                                }

                                fav.setMember_details(list);
                                albumList.add(fav);
                            }
                            setDataAdapter();
                        }else{
                            albumListView.setVisibility(View.GONE);
                            tv_txtNoData.setVisibility(View.VISIBLE);
                            tv_txtNoData.setText("No Album(s) found");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        albumListView.setVisibility(View.GONE);
                        tv_txtNoData.setVisibility(View.VISIBLE);
                        tv_txtNoData.setText("No Album(s) found");
                    }

                }
            }else{

                try {
                    String result1 = Util.ReadSharePrefrence(SelectAlbum.this, Constant.SHRED_PR.KEY_Albums);

                    if(result1!=null && result1.length()>0) {
                        JSONObject jObj = new JSONObject(result1);
                        Toast.makeText(SelectAlbum.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

                        final JSONArray jsonArray = jObj.getJSONArray("data");
                        albumList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            AllAlbum fav = new AllAlbum();
                            fav.setAlbum_id(jsonObject.optString("album_id"));
                            fav.setAlbum_name(jsonObject.optString("album_name"));
                            fav.setCreate_date(jsonObject.optString("create_date"));
                            fav.setModify_date(jsonObject.optString("modify_date"));
                            fav.setAlbum_cover_thumb_image(jsonObject.optString("album_cover_thumb_image"));
                            fav.setAlbum_cover_image(jsonObject.optString("album_cover_image"));
                            fav.setShared_with_me(jsonObject.optInt("shared_with_me"));
                            fav.setCreated_by_me(jsonObject.optInt("created_by_me"));
                            fav.setAdmin_id(jsonObject.optString("admin_id"));
                            fav.setAdmin_dp(jsonObject.optString("admin_dp"));
                            fav.setAdmin_name(jsonObject.optString("admin_name"));
                            fav.setAdmin_mobileno(jsonObject.optString("admin_mobileno"));
                            fav.setPhoto_count(jsonObject.optString("photo_count"));
                            fav.setMember_count(jsonObject.optInt("member_count"));

//                        JSONObject jObj1 = jsonObject.getJSONObject("total_likes");
                            fav.setCount(jsonObject.optString("total_likes"));

                            JSONArray jArray = jsonObject.getJSONArray("member_details");
                            List<MemberDetail> list = new ArrayList<MemberDetail>();
                            for (int j = 0; j < jArray.length(); j++) {
                                JSONObject jObj2 = jArray.getJSONObject(j);
                                MemberDetail mamDetail = new MemberDetail();
                                mamDetail.setMember_id(jObj2.optString("member_id"));
                                mamDetail.setMember_name(jObj2.optString("member_name"));
                                mamDetail.setMember_image(jObj2.optString("member_image"));
                                mamDetail.setMember_number(jObj2.optString("member_number"));
                                mamDetail.setIsadmin(jObj2.optInt("isadmin"));
                                list.add(mamDetail);
                            }

                            fav.setMember_details(list);
                            albumList.add(fav);
                        }
                        setDataAdapter();
                    }else{
                        albumListView.setVisibility(View.GONE);
                        tv_txtNoData.setVisibility(View.VISIBLE);
                        tv_txtNoData.setText(getResources().getString(R.string.no_internet));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    albumListView.setVisibility(View.GONE);
                    tv_txtNoData.setVisibility(View.VISIBLE);
                    tv_txtNoData.setText(getResources().getString(R.string.no_internet));
                }


            }
            /* Todo change fragment after successfully applied for fragment */
//            finish();

        }
    }

    private void setDataAdapter() {
//        if (adpAllAlbum != null) {
//            adpAllAlbum.setData(albumList);
//        } else {
        AdpAllAlbum adpAllAlbum = new AdpAllAlbum(SelectAlbum.this, albumList);
        albumListView.setAdapter(adpAllAlbum);
//        }
    }

}
