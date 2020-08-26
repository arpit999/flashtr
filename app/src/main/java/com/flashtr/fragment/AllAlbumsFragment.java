package com.flashtr.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.NewAlbumActivity;
import com.flashtr.R;
import com.flashtr.adapter.AlbumsAdapter;
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
 * Created by Sadiwala on 9/19/2015.
 */
public class AllAlbumsFragment extends Fragment {

    @InjectView(R.id.tvError)
    TextView tvError;
    @InjectView(R.id.lvAlbums)
    ListView lvAlbums;

    ArrayList<HashMap<String, String>> listAlbums = new ArrayList<HashMap<String, String>>();

    public static AllAlbumsFragment newInstance() {
        AllAlbumsFragment homeFragment = new AllAlbumsFragment();
        return homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_allalbums, container, false);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lvAlbums.setDivider(null);
        lvAlbums.setDividerHeight(0);

        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Albums, "0");
        if (Util.isOnline(getActivity())) {
            String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            new getAlbums(userId).execute();
        } else Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Albums).equals("1")) {
            if (Util.isOnline(getActivity())) {
                String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                new getAlbums(userId).execute();
            } else Toast.makeText(getActivity(), Constant.network_error, Toast.LENGTH_SHORT).show();
        }
        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_RELOAD_Albums, "0");

    }

    @OnClick(R.id.rlNewAlbum)
    public void NewAlbum(View view) {

        Intent intent = new Intent(getActivity(),NewAlbumActivity.class);
        intent.putExtra("activity","AllAlbumsFragment");
        startActivity(intent);
//        startActivity(new Intent(getActivity(), NewAlbumActivity.class));
    }

    class getAlbums extends AsyncTask<Void, String, String> {

        String response;
        String userId;

        private getAlbums(String userId) {
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(getActivity(),"");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "getalbum");
                jData.put("userid", "" + userId);

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("data", jData.toString()));
                response = Util.makeServiceCall(Constant.URL, 1, params1);
                Log.e("params1", ">>" + params1);
                Log.e("** response is:- ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
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
            reload(result);

        }
    }

    private void reload(String result) {

        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Albums, "" + result);
        listAlbums.clear();
        try {
            JSONObject jObj = new JSONObject(result);
            int status = jObj.optInt("success");

            if (status == 1) {
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

        lvAlbums.setAdapter(new AlbumsAdapter(getActivity(), listAlbums));

        if (listAlbums.size() > 0) tvError.setVisibility(View.GONE);
        else tvError.setVisibility(View.VISIBLE);

    }



}
