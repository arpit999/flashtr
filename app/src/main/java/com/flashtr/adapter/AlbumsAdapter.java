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

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.HomeActivity;
import com.flashtr.R;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sadiwala on 9/19/2015.
 */
public class AlbumsAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> locallist;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private Activity mContext;
    private LayoutInflater inflater = null;
    Typeface font,fontBold;

    public AlbumsAdapter(Activity mContext, ArrayList<HashMap<String, String>> locallist) {

        this.mContext = mContext;
        this.locallist = locallist;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
        font = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");
        fontBold = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUIB.TTF");

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

        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_album, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
//        ViewGroup album = (ViewGroup) view.findViewById(R.id.rlAlbumName);
//        Util.setFont(album, Util.getFont(2, mContext.getApplicationContext()));
//
//        ViewGroup rlDate = (ViewGroup) view.findViewById(R.id.rlDate);
//        Util.setFont(rlDate, Util.getFont(1, mContext.getApplicationContext()));
//
//        ViewGroup root = (ViewGroup) view.findViewById(R.id.llAlbumDetails);
//        Util.setFont(root, Util.getFont(1, mContext.getApplicationContext()));
        final HashMap<String, String> hashMap = locallist.get(position);
        holder.img.setTag(hashMap.get("album_cover_image"));


        holder.tvAlbumName.setTypeface(fontBold);
        holder.tvMemberCount.setTypeface(font);
        holder.tvPhotoCount.setTypeface(font);
        holder.tvDate.setTypeface(font);



        holder.tvAlbumName.setText("" + hashMap.get("album_name"));
        holder.tvMemberCount.setText("Members : " + hashMap.get("member_count"));
        holder.tvPhotoCount.setText("Photos : " + hashMap.get("photo_count"));
        imageLoader.displayImage("" + hashMap.get("album_cover_image"), holder.img, options);

        String dt = hashMap.get("create_date");
        List<String> elephantList = Arrays.asList(dt.split("-"));
        int month = Integer.parseInt(elephantList.get(1));
        List<String> data = Arrays.asList(elephantList.get(2).split(" "));
        int day = Integer.parseInt(data.get(0));
        holder.tvDate.setText("" + getMonthSuffix(Integer.parseInt(elephantList.get(1))) + " " + day +", " + elephantList.get(0));
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUIB.TTF");
        holder.tvAlbumName.setTypeface(typeface);

        Typeface type = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");
        holder.tvDate.setTypeface(type);
        holder.tvMemberCount.setTypeface(type);
        holder.tvPhotoCount.setTypeface(type);
        holder.mlAlbumRow.setOnClickListener(new View.OnClickListener() {
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

    public String getMonthSuffix(final int n) {
        String month = "";
        switch (n) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
        }
        return month;
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
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.mlAlbumRow)
        MaterialRippleLayout mlAlbumRow;
//        @InjectView(R.id.rlDate)
//        RelativeLayout rlDate;
//        @InjectView(R.id.rlAlbumName)
//        RelativeLayout rlAlbumName;
//        @InjectView(R.id.llAlbumDetails)
//        LinearLayout llAlbumDetails;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
