package com.flashtr.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flashtr.ImagePagerActivity;
import com.flashtr.R;
import com.flashtr.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by MAHADEV on 04-10-2015.
 */
public class GridSharedPicsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater = null;
    ArrayList<HashMap<String, String>> locallist;

    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public GridSharedPicsAdapter(Context mContext, ArrayList<HashMap<String, String>> locallist) {

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

    }


    @Override
    public int getCount() {
        return locallist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view,
                        ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_grid_image, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final HashMap<String, String> hashMap = locallist.get(position);

        /*imageLoader.displayImage("" + hashMap.get("photo"), holder.imageView, options);*/
        if(hashMap.get("thumbImage")!=null && hashMap.get("thumbImage").length()>0){
            Picasso.with(mContext).load(hashMap.get("thumbImage")).into(holder.imageView);
        }else{
//            Picasso.with(mContext).load(R.drawable.default_img).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(holder.imageView);
        }


        ViewTreeObserver vto = holder.rlMain.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                holder.rlMain.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = holder.rlMain.getMeasuredWidth();
                RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(width, width);
                holder.rlMain.setLayoutParams(rel_btn);

            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImagePagerActivity.class);
                intent.putExtra("from", Constant.SharedPics);
                intent.putExtra("pos", position);
                intent.putExtra("list", locallist);
                mContext.startActivity(intent);
            }
        });

        return view;

    }

    static class ViewHolder {
        @InjectView(R.id.image)
        ImageView imageView;
        @InjectView(R.id.rlMain)
        RelativeLayout rlMain;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
