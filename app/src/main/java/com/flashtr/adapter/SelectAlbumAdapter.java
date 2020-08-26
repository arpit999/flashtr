package com.flashtr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flashtr.HomeActivity;
import com.flashtr.R;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ntech on 19/11/15.
 */
public class SelectAlbumAdapter extends BaseAdapter {

    private Activity mContext;
    private LayoutInflater inflater = null;
    ArrayList<HashMap<String, String>> locallist;

    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public SelectAlbumAdapter(Activity mContext, ArrayList<HashMap<String, String>> locallist) {

        this.mContext = mContext;
        this.locallist = locallist;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));

    }

    @Override
    public int getCount() {
        return locallist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_select_album, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final HashMap<String, String> hashMap = locallist.get(position);

        holder.tvAlbumName.setText("" + hashMap.get("album_name"));
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"SEGOEUIB.TTF");
        holder.tvAlbumName.setTypeface(typeface);

        holder.tvMemberCount.setText("" + hashMap.get("member_count")+" Members");
        holder.tvPhotoCount.setText("" + hashMap.get("photo_count") + " Photos");
        imageLoader.displayImage("" + hashMap.get("album_cover_image"), holder.img, options);

        Typeface type = Typeface.createFromAsset(mContext.getAssets(),"SEGOEUI.TTF");
        holder.tvAlbumName.setTypeface(type);
        holder.tvMemberCount.setTypeface(type);
        holder.tvPhotoCount.setTypeface(type);
//        String members = "";
//        try {
//            JSONArray jsonArray = new JSONArray("" + hashMap.get("member_details"));
//            for (int i = 0; i < jsonArray.length(); i++) {
//                if (members.length() == 0)
//                    members = jsonArray.optJSONObject(i).optString("member_name");
//
//
//                else members = members + ", " + jsonArray.optJSONObject(i).optString("member_name");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        holder.tvMemberName.setText("" + members);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumID, "" + hashMap.get("album_id"));
                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumName, "" + hashMap.get("album_name"));
                Intent intent = new Intent(mContext, HomeActivity.class);
                mContext.startActivity(intent);
                mContext.finish();
            }
        });

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.tvAlbumName)
        TextView tvAlbumName;
        @InjectView(R.id.tvMemberCount)
        TextView tvMemberCount;
        @InjectView(R.id.tvPhotoCount)
        TextView tvPhotoCount;
        @InjectView(R.id.img)
        ImageView img;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
