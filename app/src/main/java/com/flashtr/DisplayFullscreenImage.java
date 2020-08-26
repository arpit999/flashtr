package com.flashtr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.flashtr.util.TouchImageView;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.InjectView;

/**
 * Created by USER 3 on 15/05/2015.
 */
public class DisplayFullscreenImage extends BaseActivity {

    @InjectView(R.id.image)
    TouchImageView imageView;
@InjectView(R.id.main)
    RelativeLayout main;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    String URLURItoImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_display_fullimage);
        main.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        URLURItoImage = getIntent().getStringExtra("path");

        Log.e("URL/I received:", URLURItoImage);
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));

        imageLoader.displayImage(URLURItoImage, imageView, options);

    }
}
