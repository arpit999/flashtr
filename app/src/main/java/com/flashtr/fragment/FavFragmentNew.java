package com.flashtr.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.ImagePagerActivity;
import com.flashtr.R;
import com.flashtr.activity.ReactionScreen;
import com.flashtr.activity.TabActivity;
import com.flashtr.adapter.AdpFavorite;
import com.flashtr.adapter.GridFavs;
import com.flashtr.adapter.GridRecentPicsAdapter;
import com.flashtr.model.Favorite;
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
 * Created by 24B on 5/7/2016.
 */
public class FavFragmentNew extends BaseFragment {

    private DemoFragment parent;
    @InjectView(R.id.favListView)
    RecyclerView favListView;
    @InjectView(R.id.tv_txtHeader)
    TextView tv_txtHeader;
    @InjectView(R.id.no_data_found)
    ImageView noDataFound;
    @InjectView(R.id.gridFavs)
    GridView gridFavs;
    @InjectView(R.id.iv_imGrid)
    ImageView iv_imGrid;

    private List<Favorite> favList = new ArrayList<Favorite>();
    public static ArrayList<HashMap<String, String>> listSharedPhotos = new ArrayList<HashMap<String, String>>();
    private boolean isRequestCallled;
    private LinearLayoutManager manager;
    private int page = 0, pageLimit, lastMomentListDataCount;
    public static FavFragmentNew instance;
    @InjectView(R.id.progressBar5)
    ProgressBar progressBar5;
    int ViewPosition;


    private boolean isGridShow = false;

    @OnClick(R.id.iv_imGrid)
    public void ChangeView(View v){
        if(isGridShow){
            isGridShow = false;

        }else{
            isGridShow = true;
        }
        setView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*View mainView = inflater.inflate(R.layout.fragment_new_album, null);
        ButterKnife.inject(getActivity());*/
        View view = inflater.inflate(R.layout.fragment_fav, container, false);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.inject(this, view);
        instance = this;
        parent = (DemoFragment) ((TabActivity) getActivity()).adapter.getItem(1);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindControl();
    }

    private void bindControl() {
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        favListView.setLayoutManager(manager);

        setView();

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");

        tv_txtHeader.setTypeface(font);

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    return true;
                }
                return false;
            }
        });

       /* favListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int lastVisibleItem = manager.findLastVisibleItemPosition();


                if ((lastVisibleItem + 1) == favList.size() && !isRequestCallled && *//*!(lastMomentListDataCount >= 0 && lastMomentListDataCount < 10)*//* !(page >= pageLimit)) {
                    if (Util.isOnline(getActivity())) {

                        String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                        new getPhotos(userId, false).execute();
                    } else {
//                        loadOfflinePhotos();
                    }
                } else {
                   *//* visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    firstVisibleItem = manager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount && TotalRecord > listSharedPhotos.size()) {
                        page++;

                    }*//*
                }

            }
        });*/


       /* swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();

            }
        });*/
    }

    private void refreshList(boolean isDialogShown) {
        page = 0;
        lastMomentListDataCount = 0;
        isRequestCallled = false;
        favList.clear();
        favList = new ArrayList<Favorite>();
        listSharedPhotos.clear();
        listSharedPhotos = new ArrayList<HashMap<String, String>>();

        if (Util.isOnline(getActivity())) {

            String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            new getFavPhotos(userId, isDialogShown).execute();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    private void getFavList(boolean isDialogRequired) {
        String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
        new getFavPhotos(userId, isDialogRequired).execute();
    }

    class getFavPhotos extends AsyncTask<Void, String, String> {

        String response;
        String userId;
        boolean isDialogRequired;

        private getFavPhotos(String userId, boolean isDialogRequired) {
            this.userId = userId;
            this.isDialogRequired = isDialogRequired;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isDialogRequired) {
//                CustomDialog.getInstance().show(getActivity(), "");
                progressBar5.setVisibility(View.VISIBLE);
            } else {
                progressBar5.setVisibility(View.GONE);
            }

        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "favouritephotolist");
                jData.put("user_id", "" + userId);

//                page++;

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

//            if (CustomDialog.getInstance().isDialogShowing())
//                CustomDialog.getInstance().hide();
            progressBar5.setVisibility(View.GONE);
            isRequestCallled = false;
            if (!TextUtils.isEmpty(result)) {

                if (Util.isOnline(getActivity())) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        int status = jObj.optInt("success");
//                        pageLimit = jObj.optInt("totalpage");
                        if (status == 1) {
                            listSharedPhotos.clear();

                            /*favListView.setVisibility(View.VISIBLE);*/
                            setView();
                            noDataFound.setVisibility(View.GONE);
                            favList.clear();

                            JSONArray jsonArray = jObj.getJSONArray("data");
                            lastMomentListDataCount = jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);

                                Favorite fav = new Favorite();
                                fav.setUser_id(jsonObject.optString("user_id"));
                                fav.setAlbum_id(jsonObject.optString("album_id"));
                                fav.setAlbum_name(jsonObject.optString("album_name"));
                                fav.setCover_image(jsonObject.optString("cover_image"));
                                fav.setPhoto_id(jsonObject.optString("photo_id"));
                                fav.setPhoto(jsonObject.optString("photo"));
                                fav.setFavourite_photo_id(jsonObject.optString("favourite_photo_id"));
                                fav.setThumb_image(jsonObject.optString("thumb_image"));
                                fav.setDate(jsonObject.optString("date"));

                                fav.setModify_date(jsonObject.optString("modify_date"));
                                fav.setShared(jsonObject.optString("shared"));
                                fav.setIsliked(jsonObject.optString("isliked"));
                                fav.setIsloved(jsonObject.optString("isloved"));
                                fav.setIshaha(jsonObject.optString("ishaha"));
                                fav.setIswow(jsonObject.optString("iswow"));
                                fav.setIsangry(jsonObject.optString("isangry"));
                                fav.setIssad(jsonObject.optString("issad"));
                                fav.setUser_name(jsonObject.optString("user_name"));
                                fav.setUser_image(jsonObject.optString("user_image"));
                                fav.setPhoto_status(jsonObject.optInt("photo_status"));
                                fav.setFavourite_photo(jsonObject.optInt("favourite_photo"));
                                fav.setLike_performed(jsonObject.optInt("like_performed"));
                                fav.setTotal_likes(jsonObject.optInt("total_likes"));

                                HashMap<String, String> hashMap = new HashMap<String, String>();
                                hashMap.put("user_id", "" + jsonObject.optString("user_id"));
                                hashMap.put("album_id", "" + jsonObject.optString("album_id"));
                                hashMap.put("album_name", "" + jsonObject.optString("album_name"));
                                hashMap.put("cover_image", "" + jsonObject.optString("cover_image"));
                                hashMap.put("favourite_photo_id", "" + jsonObject.optString("favourite_photo_id"));
                                hashMap.put("photo_id", "" + jsonObject.optString("photo_id"));
                                hashMap.put("photo", "" + jsonObject.optString("photo"));

                                hashMap.put("user_name", "" + jsonObject.optString("user_name"));
                                hashMap.put("user_image", "" + jsonObject.optString("user_image"));
                                hashMap.put("shared", "" + jsonObject.optString("shared"));
                                hashMap.put("date", "" + jsonObject.optString("date"));
                                hashMap.put("modify_date", "" + jsonObject.optString("modify_date"));
                                hashMap.put("thumbImage", "" + jsonObject.optString("thumb_image"));

                                hashMap.put("isliked", "" + jsonObject.optString("isliked"));
                                hashMap.put("isloved", "" + jsonObject.optString("isloved"));
                                hashMap.put("ishaha", "" + jsonObject.optString("ishaha"));
                                hashMap.put("iswow", "" + jsonObject.optString("iswow"));
                                hashMap.put("isangry", "" + jsonObject.optString("isangry"));
                                hashMap.put("issad", "" + jsonObject.optString("issad"));
                                hashMap.put("shared", "" + jsonObject.optString("shared"));
                                hashMap.put("photo_status", "" + jsonObject.optString("photo_status"));
                                hashMap.put("favourite_photo", "" + jsonObject.optString("favourite_photo"));
                                hashMap.put("like_performed", "" + jsonObject.optString("like_performed"));
                                hashMap.put("total_likes", "" + jsonObject.optString("total_likes"));

                                listSharedPhotos.add(hashMap);

                                favList.add(fav);

                            }
                            if (!favList.isEmpty()) {
                                AdpFavorite favoriteAdapter = (AdpFavorite) favListView.getAdapter();
                                if (favoriteAdapter != null) {
                                    favoriteAdapter.setData(favList);
                                    favoriteAdapter.notifyDataSetChanged();
                                } else {
                                    favoriteAdapter = new AdpFavorite(getActivity(), favList, instance);
                                    favListView.setAdapter(favoriteAdapter);
                                }
                                gridFavs.setAdapter(new GridFavs(getActivity(), listSharedPhotos));
                            }
                        } else {
                            favListView.setVisibility(View.GONE);
                            gridFavs.setVisibility(View.GONE);
                            noDataFound.setVisibility(View.VISIBLE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "No data available", Toast.LENGTH_SHORT).show();
            }


        }
    }

    public void clickEvent(View v) {
        super.clickEvent(v);
        String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
        switch (v.getId()) {

            case R.id.iv_albumPhoto:

                int ViewPosition1 = (int) v.getTag();
                Log.d("POSITION: ", ViewPosition1 + "");
                Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
                intent.putExtra("from", Constant.SharedPics);
                intent.putExtra("pos", ViewPosition1);
                intent.putExtra("list", listSharedPhotos);
                startActivity(intent);
                break;
            case R.id.relRec:
                int ViewPos = (int) v.getTag();
                Intent i = new Intent(getActivity(), ReactionScreen.class);
                i.putExtra("num",listSharedPhotos.get(ViewPos).get("total_likes"));
                i.putExtra("PhotoId",listSharedPhotos.get(ViewPos).get("photo_id"));
                startActivity(i);
                break;
            case R.id.iv_imgFav:
                int ViewPosition2 = (int) v.getTag();

                if (favList.size() > 0) {

                    addFavs task = new addFavs(listSharedPhotos.get(ViewPosition2).get("photo_id"));
                    task.execute();
                    favList.remove(ViewPosition2);
                }
                if (favList != null && favList.size() > 0) {
                    AdpFavorite favoriteAdapter= (AdpFavorite) favListView.getAdapter();
                    if(favoriteAdapter!=null){
                        favoriteAdapter.setData(favList);
                    }else {
                        favoriteAdapter = new AdpFavorite(getActivity(), favList, instance);
                        favListView.setAdapter(favoriteAdapter);
                    }
                        gridFavs.setAdapter(new GridFavs(getActivity(), listSharedPhotos));

                } else {
                    favListView.setVisibility(View.GONE);
                    gridFavs.setVisibility(View.GONE);
                    noDataFound.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.iv_imo1:
                ViewPosition = (int) v.getTag();

                int LikeStatus1; /* 0 means will call unlike services
                       1 means will call like services */
                if (listSharedPhotos.get(ViewPosition).get("isliked").contains(userId)) {
                    LikeStatus1 = 0;
                } else {
                    LikeStatus1 = 1;
                }
                setLikeViews(1, userId);
                addLiketoPhoto(1, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus1);
                break;
            case R.id.iv_imo2:
                ViewPosition = (int) v.getTag();
                int LikeStatus2; /* 0 means will call unlike services
                       1 means will call like services */
                if (listSharedPhotos.get(ViewPosition).get("isloved").contains(userId)) {
                    LikeStatus2 = 0;
                } else {
                    LikeStatus2 = 1;
                }
                setLikeViews(2, userId);
                addLiketoPhoto(2, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus2);
                break;
            case R.id.iv_imo3:
                ViewPosition = (int) v.getTag();
                int LikeStatus3; /* 0 means will call unlike services
                       1 means will call like services */
                if (listSharedPhotos.get(ViewPosition).get("ishaha").contains(userId)) {
                    LikeStatus3 = 0;
                } else {
                    LikeStatus3 = 1;
                }
                setLikeViews(3, userId);
                addLiketoPhoto(3, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus3);
                break;
            case R.id.iv_imo4:
                ViewPosition = (int) v.getTag();
                int LikeStatus4; /* 0 means will call unlike services
                       1 means will call like services */
                if (listSharedPhotos.get(ViewPosition).get("iswow").contains(userId)) {
                    LikeStatus4 = 0;
                } else {
                    LikeStatus4 = 1;
                }
                setLikeViews(4, userId);
                addLiketoPhoto(4, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus4);
                break;
            case R.id.iv_imo5:
                ViewPosition = (int) v.getTag();

                int LikeStatus5; /* 0 means will call unlike services
                       1 means will call like services */
                if (listSharedPhotos.get(ViewPosition).get("isangry").contains(userId)) {
                    LikeStatus5 = 0;
                } else {
                    LikeStatus5 = 1;
                }
                setLikeViews(5, userId);
                addLiketoPhoto(5, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus5);
                break;
            case R.id.iv_imo6:
                ViewPosition = (int) v.getTag();
                int LikeStatus6; /* 0 means will call unlike services
                       1 means will call like services */
                if (listSharedPhotos.get(ViewPosition).get("issad").contains(userId)) {
                    LikeStatus6 = 0;
                } else {
                    LikeStatus6 = 1;
                }
                setLikeViews(6, userId);
                addLiketoPhoto(6, listSharedPhotos.get(ViewPosition).get("photo_id"), LikeStatus6);
                break;
        }
    }

    class addFavs extends AsyncTask<Void, String, String> {

        String photoId, response;
        String userId;

        /*likeUnlike:  0 means will call unlike services
         * 1 means will call like services */
        private addFavs(String photoId) {
            this.photoId = photoId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "favouritephoto");
                jData.put("user_id", "" + userId);
                jData.put("photo_id", "" + photoId);

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
            if (Util.isOnline(getActivity())) {
                refreshList(false);
            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*favListView.setVisibility(View.VISIBLE);*/
        setView();
        noDataFound.setVisibility(View.GONE);
        if (favList != null && favList.size() > 0) {

            AdpFavorite favoriteAdapter = (AdpFavorite) favListView.getAdapter();
            if(favoriteAdapter!=null){
                favoriteAdapter.setData(favList);
            }else {
                favoriteAdapter = new AdpFavorite(getActivity(), favList, instance);
                favListView.setAdapter(favoriteAdapter);
            }
                gridFavs.setAdapter(new GridFavs(getActivity(), listSharedPhotos));
            getFavList(false);
        } else {
            getFavList(true);
        }
    }

    private void setLikeViews(int LikeStatus, String userId) {
        for (int i = 0; i < listSharedPhotos.size(); i++) {
            if (listSharedPhotos.get(i).get("photo_id") == listSharedPhotos.get(ViewPosition).get("photo_id")) {
                for (int l = 0; l < 6; l++) {
                    String param = "";
                    String userIds = "";
                    String[] tempIds;
                    switch (l) {
                        case 0:

                            param = "isliked";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");


                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if(totalLikes!=null&&totalLikes.length()>0){
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp+1;
                                        listSharedPhotos.get(i).put("total_likes",temp+"");
                                    }else{
                                        listSharedPhotos.get(i).put("total_likes",1+"");
                                    }
                                }
                            } else {

                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if(totalLikes!=null&&totalLikes.length()>0){
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp-1;
                                    listSharedPhotos.get(i).put("total_likes",(temp==0?"":temp)+"");
                                }else{
                                    listSharedPhotos.get(i).put("total_likes","");
                                }
                            }

                            break;
                        case 1:
                            param = "isloved";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if(totalLikes!=null&&totalLikes.length()>0){
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp+1;
                                        listSharedPhotos.get(i).put("total_likes",temp+"");
                                    }else{
                                        listSharedPhotos.get(i).put("total_likes",1+"");
                                    }
                                }
                            } else {

                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if(totalLikes!=null&&totalLikes.length()>0){
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp-1;
                                    listSharedPhotos.get(i).put("total_likes",(temp==0?"":temp)+"");
                                }else{
                                    listSharedPhotos.get(i).put("total_likes","");
                                }
                            }
                            break;
                        case 2:
                            param = "ishaha";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if(totalLikes!=null&&totalLikes.length()>0){
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp+1;
                                        listSharedPhotos.get(i).put("total_likes",temp+"");
                                    }else{
                                        listSharedPhotos.get(i).put("total_likes",1+"");
                                    }
                                }
                            } else {

                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if(totalLikes!=null&&totalLikes.length()>0){
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp-1;
                                    listSharedPhotos.get(i).put("total_likes",(temp==0?"":temp)+"");
                                }else{
                                    listSharedPhotos.get(i).put("total_likes","");
                                }
                            }
                            break;
                        case 3:
                            param = "iswow";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if(totalLikes!=null&&totalLikes.length()>0){
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp+1;
                                        listSharedPhotos.get(i).put("total_likes",temp+"");
                                    }else{
                                        listSharedPhotos.get(i).put("total_likes",1+"");
                                    }
                                }
                            } else {

                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if(totalLikes!=null&&totalLikes.length()>0){
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp-1;
                                    listSharedPhotos.get(i).put("total_likes",(temp==0?"":temp)+"");
                                }else{
                                    listSharedPhotos.get(i).put("total_likes","");
                                }
                            }
                            break;
                        case 4:
                            param = "isangry";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if(totalLikes!=null&&totalLikes.length()>0){
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp+1;
                                        listSharedPhotos.get(i).put("total_likes",temp+"");
                                    }else{
                                        listSharedPhotos.get(i).put("total_likes",1+"");
                                    }
                                }
                            } else {
                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if(totalLikes!=null&&totalLikes.length()>0){
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp-1;
                                    listSharedPhotos.get(i).put("total_likes",(temp==0?"":temp)+"");
                                }else{
                                    listSharedPhotos.get(i).put("total_likes","");
                                }
                            }
                            break;
                        case 5:
                            param = "issad";
                            userIds = listSharedPhotos.get(i).get(param);
                            tempIds = userIds.split(",");

                            if (!userIds.contains(userId)) {
                                if (LikeStatus == l + 1) {
                                    if (userIds != null && userIds.length() > 0) {
                                        userIds = userIds + "," + userId;
                                    } else {
                                        userIds = userId;
                                    }
                                    listSharedPhotos.get(i).put(param, "" + userIds);
                                    String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                    if(totalLikes!=null&&totalLikes.length()>0){
                                        int temp = Integer.parseInt(totalLikes);
                                        temp = temp+1;
                                        listSharedPhotos.get(i).put("total_likes",temp+"");
                                    }else{
                                        listSharedPhotos.get(i).put("total_likes",1+"");
                                    }
                                }
                            } else {

                                String IDS = "";
                                for (int j = 0; j < tempIds.length; j++) {
                                    if (!tempIds[j].equalsIgnoreCase(userId)) {
                                        if (j == 0) {
                                            IDS = tempIds[j];
                                        } else {
                                            IDS = IDS + "," + tempIds[j];
                                        }
                                    }
                                }
                                listSharedPhotos.get(i).put(param, "" + IDS);
                                String totalLikes = listSharedPhotos.get(i).get("total_likes");
                                if(totalLikes!=null&&totalLikes.length()>0){
                                    int temp = Integer.parseInt(totalLikes);
                                    temp = temp-1;
                                    listSharedPhotos.get(i).put("total_likes",(temp==0?"":temp)+"");
                                }else{
                                    listSharedPhotos.get(i).put("total_likes","");
                                }
                            }
                            break;
                    }
                }
                favList.clear();
                for (int i1 = 0; i1 < listSharedPhotos.size(); i1++) {
                    HashMap<String, String> hashMap = listSharedPhotos.get(i1);

                    /*HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("user_id", "" + jsonObject.optString("user_id"));
                    hashMap.put("album_id", "" + jsonObject.optString("album_id"));
                    hashMap.put("album_name", "" + jsonObject.optString("album_name"));
                    hashMap.put("cover_image", "" + jsonObject.optString("cover_image"));
                    hashMap.put("favourite_photo_id", "" + jsonObject.optString("favourite_photo_id"));
                    hashMap.put("photo_id", "" + jsonObject.optString("photo_id"));
                    hashMap.put("photo", "" + jsonObject.optString("photo"));

                    hashMap.put("user_name", "" + jsonObject.optString("user_name"));
                    hashMap.put("user_image", "" + jsonObject.optString("user_image"));
                    hashMap.put("shared", "" + jsonObject.optString("shared"));
                    hashMap.put("date", "" + jsonObject.optString("date"));
                    hashMap.put("modify_date", "" + jsonObject.optString("modify_date"));
                    hashMap.put("thumbImage", "" + jsonObject.optString("thumb_image"));

                    hashMap.put("isliked", "" + jsonObject.optString("isliked"));
                    hashMap.put("isloved", "" + jsonObject.optString("isloved"));
                    hashMap.put("ishaha", "" + jsonObject.optString("ishaha"));
                    hashMap.put("iswow", "" + jsonObject.optString("iswow"));
                    hashMap.put("isangry", "" + jsonObject.optString("isangry"));
                    hashMap.put("issad", "" + jsonObject.optString("issad"));
                    hashMap.put("shared", "" + jsonObject.optString("shared"));
                    hashMap.put("photo_status", "" + jsonObject.optString("photo_status"));
                    hashMap.put("favourite_photo", "" + jsonObject.optString("favourite_photo"));
                    hashMap.put("like_performed", "" + jsonObject.optString("like_performed"));
                    hashMap.put("total_likes", "" + jsonObject.optString("total_likes"));*/

                    Favorite fav = new Favorite();
                    fav.setUser_id(hashMap.get("user_id"));
                    fav.setAlbum_id(hashMap.get("album_id"));
                    fav.setAlbum_name(hashMap.get("album_name"));
                    fav.setCover_image(hashMap.get("cover_image"));
                    fav.setPhoto_id(hashMap.get("photo_id"));
                    fav.setPhoto(hashMap.get("photo"));
                    fav.setFavourite_photo_id(hashMap.get("favourite_photo_id"));
                    fav.setThumb_image(hashMap.get("thumb_image"));
                    fav.setDate(hashMap.get("date"));

                    fav.setModify_date(hashMap.get("modify_date"));
                    fav.setShared(hashMap.get("shared"));
                    fav.setIsliked(hashMap.get("isliked"));
                    fav.setIsloved(hashMap.get("isloved"));
                    fav.setIshaha(hashMap.get("ishaha"));
                    fav.setIswow(hashMap.get("iswow"));
                    fav.setIsangry(hashMap.get("isangry"));
                    fav.setIssad(hashMap.get("issad"));
                    fav.setUser_name(hashMap.get("user_name"));
                    fav.setUser_image(hashMap.get("user_image"));
                    if (hashMap.get("photo_status") != null && hashMap.get("photo_status").length() > 0) {
                        fav.setPhoto_status(Integer.parseInt(hashMap.get("photo_status")));
                    }else{
                        fav.setPhoto_status(0);
                    }

                    if (hashMap.get("favourite_photo") != null && hashMap.get("favourite_photo").length() > 0) {
                        fav.setFavourite_photo(Integer.parseInt(hashMap.get("favourite_photo")));
                    }else{
                        fav.setFavourite_photo(0);
                    }

                    if (hashMap.get("like_performed") != null && hashMap.get("like_performed").length() > 0) {
                        fav.setLike_performed(Integer.parseInt(hashMap.get("like_performed")));
                    }else{
                        fav.setLike_performed(0);
                    }

                    if (hashMap.get("total_likes") != null && hashMap.get("total_likes").length() > 0) {
                        fav.setTotal_likes(Integer.parseInt(hashMap.get("total_likes")));
                    } else {
                        fav.setTotal_likes(0);
                    }

//                    listSharedPhotos.add(hashMap);

                    favList.add(fav);

                }

                AdpFavorite favoriteAdapter = (AdpFavorite) favListView.getAdapter();
                if(favoriteAdapter!=null) {
                    favoriteAdapter.setData(favList);
                }else{
                    favoriteAdapter = new AdpFavorite(getActivity(), favList, instance);
                    favListView.setAdapter(favoriteAdapter);
                }
                gridFavs.setAdapter(new GridFavs(getActivity(), listSharedPhotos));
            }
        }
    }

    /*likeUnlike:  0 means will call unlike services
     * 1 means will call like services */
    private void addLiketoPhoto(int likedStatus, String photoId, int likeUnlike) {
        addLike task = new addLike(likedStatus, photoId, likeUnlike);
        task.execute();
    }

    class addLike extends AsyncTask<Void, String, String> {

        int LikeStatus, likeUnlike;
        String photoId, response;
        String userId;

        /*likeUnlike:  0 means will call unlike services
         * 1 means will call like services */
        private addLike(int LikeStatus, String photoId, int likeUnlike) {
            this.LikeStatus = LikeStatus;
            this.photoId = photoId;
            this.likeUnlike = likeUnlike;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "likephoto");
                jData.put("user_id", "" + userId);
                jData.put("photo_id", "" + photoId);
                jData.put("like_status", "" + LikeStatus);

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
            if (Util.isOnline(getActivity())) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    int status = jObj.optInt("success");
                    String msg = jObj.optString("msg");
//                    Toast.makeText(getActivity(), msg + "", Toast.LENGTH_LONG).show();

                    String userId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
                    /*new getPhotos(userId, true).execute();*/
//                    refreshList();
                   /*for (int i = 0; i < listSharedPhotos.size(); i++) {
                        if (listSharedPhotos.get(i).get("photo_id") == photoId) {
                            for (int l = 0; l < 6; l++) {
                                String param = "";
                                String userIds = "";
                                String[] tempIds;
                                switch (l) {
                                    case 0:

                                        param = "isliked";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");


                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }

                                        break;
                                    case 1:
                                        param = "isloved";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                    case 2:
                                        param = "ishaha";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                    case 3:
                                        param = "iswow";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                    case 4:
                                        param = "isangry";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {
                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                    case 5:
                                        param = "issad";
                                        userIds = listSharedPhotos.get(i).get(param);
                                        tempIds = userIds.split(",");

                                        if (!userIds.contains(userId)) {
                                            if (LikeStatus == l + 1) {
                                                if (userIds != null && userIds.length() > 0) {
                                                    userIds = userIds + "," + userId;
                                                } else {
                                                    userIds = userId;
                                                }
                                                listSharedPhotos.get(i).put(param, "" + userIds);
                                            }
                                        } else {

                                            String IDS = "";
                                            for (int j = 0; j < tempIds.length; j++) {
                                                if (!tempIds[j].equalsIgnoreCase(userId)) {
                                                    if (j == 0) {
                                                        IDS = tempIds[j];
                                                    } else {
                                                        IDS = IDS + "," + tempIds[j];
                                                    }
                                                }
                                            }
                                            listSharedPhotos.get(i).put(param, "" + IDS);

                                        }
                                        break;
                                }
                            }

                            recyclerAdp.setData(listSharedPhotos, page, pageLimit);
                        }
                    }*/
//                    setLikeViews(userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setView(){
        if(isGridShow){
            gridFavs.setVisibility(View.VISIBLE);
            favListView.setVisibility(View.GONE);
            iv_imGrid.setImageResource(R.drawable.list_icon);
        }else{
            gridFavs.setVisibility(View.GONE);
            favListView.setVisibility(View.VISIBLE);
            iv_imGrid.setImageResource(R.drawable.grid_icon);
        }
    }
}
