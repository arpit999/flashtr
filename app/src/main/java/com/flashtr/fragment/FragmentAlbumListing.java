package com.flashtr.fragment;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flashtr.R;
import com.flashtr.activity.SelectAlbum;
import com.flashtr.activity.TabActivity;
import com.flashtr.adapter.AdpAlbumList;
import com.flashtr.adapter.AdpAlbumsList;
import com.flashtr.adapter.AdpAllAlbum;
import com.flashtr.adapter.AdpFavorite;
import com.flashtr.app.MyApp;
import com.flashtr.model.AllAlbum;
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
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by 24B on 5/11/2016.
 */
public class FragmentAlbumListing extends BaseFragment {

    public static FragmentAlbumListing instance;
    private DemoFragment parent;
    ListView listAlbumList;
    TextView tv_txtHeader;
    TextView noDataFound;
    AdpAlbumsList albumsListAdp;
    private List<AllAlbum> albumList = new ArrayList<AllAlbum>();
    View mainView;
    int delay = 200;
    int page = 0;
    private boolean isRequestCallled = false;
    int noMoreData = 2;
    ProgressBar progressBar4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*View mainView = inflater.inflate(R.layout.fragment_new_album, null);
        ButterKnife.inject(getActivity());*/
        mainView = inflater.inflate(R.layout.frag_album_list, container, false);
        instance = this;
        parent = (DemoFragment) ((TabActivity) getActivity()).adapter.getItem(1);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bindControl();
            }
        }, delay);

    }

    private void bindControl() {
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");

        listAlbumList = (ListView) mainView.findViewById(R.id.listAlbumList);
        progressBar4 = (ProgressBar) mainView.findViewById(R.id.progressBar4);
        tv_txtHeader = (TextView) mainView.findViewById(R.id.tv_txtHeader);
        noDataFound = (TextView) mainView.findViewById(R.id.noDataFound);

        tv_txtHeader.setTypeface(type);
        noDataFound.setTypeface(type);

        if(albumList!=null&&!albumList.isEmpty()&&albumList.size()>0){
            listAlbumList.setVisibility(View.VISIBLE);
            noDataFound.setVisibility(View.GONE);
            progressBar4.setVisibility(View.GONE);
            setDataAdapter();
            if (Util.isOnline(getActivity())) {

                String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                new getAlbums(userId, false).execute();
            }
        }else{
            if (Util.isOnline(getActivity())) {

                String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                new getAlbums(userId, false).execute();
            }
        }

        listAlbumList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (page > 2) {

                } else {
                    int lastVisibleItem = listAlbumList.getLastVisiblePosition();


                    if ((lastVisibleItem + 1) == albumList.size() && !isRequestCallled && noMoreData == 2/*&&*/ /*!(lastMomentListDataCount >= 0 && lastMomentListDataCount < 10)*/ /*!(page >= pageLimit)*/) {
                        if (Util.isOnline(getActivity())) {

                            String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                            new getAlbums(userId, false).execute();
                        } else {
//                        loadOfflinePhotos();
                        }
                    } else {
                   /* visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    firstVisibleItem = manager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount && TotalRecord > listSharedPhotos.size()) {
                        page++;

                    }*/
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(MyApp.isDeleteAlbum != -1 && MyApp.deleteAlbumPos!=-1){
            albumList.remove(MyApp.deleteAlbumPos);
            MyApp.isDeleteAlbum = -1;
            MyApp.deleteAlbumPos =-1;
            setDataAdapter();
        }

       /* final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!albumList.isEmpty()) {
                    refreshList(false);
                    *//*new getAlbums(userId, false).execute();*//*
                } else {
                    refreshList(true);
                    *//*new getAlbums(userId, true).execute();*//*
                }
            }
        }, delay);*/


    }



    private void refreshList(boolean isDialogRequired) {
        page = 0;
        noMoreData = 2;
        isRequestCallled = false;
        albumList = null;
        albumList = new ArrayList<AllAlbum>();
        String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
        new getAlbums(userId, isDialogRequired).execute();
    }

    @Override
    public void clickEvent(View v) {
        super.clickEvent(v);
        switch (v.getId()) {
            case R.id.tv_txtLoadMore:
                int lastVisibleItem = listAlbumList.getLastVisiblePosition();
                if ((lastVisibleItem + 1) == albumList.size() && !isRequestCallled /*&&*/ /*!(lastMomentListDataCount >= 0 && lastMomentListDataCount < 10)*/ /*!(page >= pageLimit)*/) {
                    if (Util.isOnline(getActivity())) {

                        String userId1 = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                        new getAlbums(userId1, true).execute();
                    } else {
//                        loadOfflinePhotos();
                    }
                }
                break;
        }
    }

    class getAlbums extends AsyncTask<Void, String, String> {

        String response;
        String userId;
        boolean isDialogShow;

        private getAlbums(String userId, boolean isDialogShow) {
            this.userId = userId;
            this.isDialogShow = isDialogShow;
            isRequestCallled = true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isDialogShow)
                progressBar4.setVisibility(View.VISIBLE);
//                CustomDialog.getInstance().show(getActivity(), "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "getalbum");
                jData.put("userid", "" + userId);
                jData.put("page", "" + page);

                page++;

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

            isRequestCallled = false;

//            if (isDialogShow) {
//                CustomDialog.getInstance().hide();
                progressBar4.setVisibility(View.GONE);
//            }
            if(result!=null&&result.length()>0) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    int status = jObj.optInt("success");

                    if (status == 1) {

//                    albumList.clear();
                        listAlbumList.setVisibility(View.VISIBLE);
                        noDataFound.setVisibility(View.GONE);

                        final JSONArray jsonArray = jObj.getJSONArray("data");

                        if (jsonArray.length() > 0) {
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

                                MemberDetail mamD = new MemberDetail();
                                mamD.setIsadmin(1);
                                mamD.setMember_id(fav.getAdmin_id());
                                mamD.setMember_name(fav.getAdmin_name());
                                mamD.setMember_image(fav.getAdmin_dp());
                                mamD.setMember_number(fav.getAdmin_mobileno());

                                list.add(mamD);

                                for (int j = 0; j < jArray.length(); j++) {

                                    JSONObject jObj2 = jArray.getJSONObject(j);

                                    if (!mamD.getMember_id().equalsIgnoreCase(jObj2.optString("member_id"))) {
                                        MemberDetail mamDetail = new MemberDetail();
                                        mamDetail.setMember_id(jObj2.optString("member_id"));
                                        mamDetail.setMember_name(jObj2.optString("member_name"));
                                        mamDetail.setMember_image(jObj2.optString("member_image"));
                                        mamDetail.setMember_number(jObj2.optString("member_number"));
                                        mamDetail.setIsadmin(jObj2.optInt("isadmin"));
                                        list.add(mamDetail);
                                    }

                                }


                                fav.setMember_details(list);
                                albumList.add(fav);
                            }
                            setDataAdapter();
                            noMoreData = 2;
                        } else {
                            setDataAdapter();
                            noMoreData = 1;
                        }
                    } else {
                        if (albumList.size() > 0) {
                            noMoreData = 1;
                            setDataAdapter();
                        } else {
                            listAlbumList.setVisibility(View.GONE);
                            noDataFound.setVisibility(View.VISIBLE);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    listAlbumList.setVisibility(View.GONE);
                    noDataFound.setVisibility(View.VISIBLE);
                }
            }else{

            }
            /* Todo change fragment after successfully applied for fragment */
//            finish();

        }
    }

    private void setDataAdapter() {
        AdpAlbumList adpAllAlbum = (AdpAlbumList) listAlbumList.getAdapter();
        if (adpAllAlbum != null && albumList.size() > 0 /*&& page != 1*/) {
            adpAllAlbum.setData(albumList, noMoreData, page);
            adpAllAlbum.notifyDataSetChanged();
           /* adpAllAlbum = new AdpAlbumList(getActivity(), albumList, noMoreData, page,instance);
            listAlbumList.setAdapter(adpAllAlbum);*/
        } else {
            adpAllAlbum = new AdpAlbumList(getActivity(), albumList, noMoreData, page,instance);
            listAlbumList.setAdapter(adpAllAlbum);
        }
    }

}
