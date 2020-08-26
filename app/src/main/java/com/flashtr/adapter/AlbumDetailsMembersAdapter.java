package com.flashtr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by hirenkanani on 03/12/15.
 */
public class AlbumDetailsMembersAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater = null;
    ArrayList<HashMap<String, String>> locallist;
    String admin_id;

    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public AlbumDetailsMembersAdapter(Context mContext, ArrayList<HashMap<String, String>> locallist){
        this.mContext = mContext;
        this.locallist = locallist;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
//                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.user_img).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.user_img).showImageOnFail(R.drawable.user_img).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
        admin_id = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_AdminId);
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
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listdetailraw_member, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final HashMap<String, String> hashMap = locallist.get(position);

        holder.tvName.setText("" + hashMap.get("member_name"));
        holder.tvMobile.setText("Mobile: " + hashMap.get("member_number"));
        imageLoader.displayImage("" + hashMap.get("member_image"), holder.img, options);

        if(hashMap.get("member_id").equals(admin_id)){
            holder.admin.setVisibility(View.VISIBLE);
        }else{
            holder.admin.setVisibility(View.GONE);
        }

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.tvMobile)
        TextView tvMobile;
        @InjectView(R.id.img)
        ImageView img;
        @InjectView(R.id.tvAdmin)
        ImageView admin;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
