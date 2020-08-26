package com.flashtr.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.flashtr.R;
import com.flashtr.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sadiwala on 10/15/2015.
 */

public class ImagePagerAdapter extends PagerAdapter {

    int FROM;
    private Context mContext;
    private LayoutInflater inflater = null;
    ArrayList<HashMap<String, String>> locallist;

    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public ImagePagerAdapter(Context mContext, ArrayList<HashMap<String, String>> locallist, int FROM) {

        this.FROM = FROM;
        this.mContext = mContext;
        this.locallist = locallist;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
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
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void finishUpdate(View container) {
    }


    @SuppressLint("NewApi")
    @Override
    public Object instantiateItem(View view, int position) {

        final View imageLayout = inflater.inflate(R.layout.item_pager_image, null);

        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        final ProgressBar progressBar9 = (ProgressBar) imageLayout.findViewById(R.id.progressBar9);

        final HashMap<String, String> hashMap = locallist.get(position);

        imageView.setVisibility(View.GONE);

        if (FROM == Constant.SharedPics) {
//            imageLoader.displayImage("" + hashMap.get("photo"), imageView, options);
            Picasso.with(mContext).load(hashMap.get("photo")).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar9.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {

                    progressBar9.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }
            });
        } else {
//            imageLoader.displayImage(Uri.fromFile(new File("" + hashMap.get("photo"))).toString(), imageView, options);
            Picasso.with(mContext).load(Uri.fromFile(new File("" + hashMap.get("photo"))).toString()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar9.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {

                    progressBar9.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }
            });
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            }
        });


        ((ViewPager) view).addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View container) {
    }

}
