package com.flashtr.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flashtr.R;
import com.flashtr.util.Util;
import com.squareup.picasso.Picasso;

/**
 * Created by hirenkanani on 02/01/16.
 */
public class TipsPagerAdapter extends PagerAdapter {
    private Context mContext;
//    private LayoutInflater inflater = null;
    int[] locallist;
    int[] screenWH;

    public TipsPagerAdapter(Context mContext, int[] locallist) {

        this.mContext = mContext;
        this.locallist = locallist;
//        inflater = (LayoutInflater) mContext.
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        screenWH = Util.getScreenWidthHeight(mContext);
    }


    @Override
    public int getCount() {
        return locallist.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = LayoutInflater.from(mContext).inflate(R.layout.tips_pager_image,view,false);
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        imageView.setEnabled(false);
//        Picasso.with(mContext).load(locallist[position]).resize(screenWH[0],screenWH[1]).into(imageView);
        imageView.setImageResource(locallist[position]);
//        imageLoader.displayImage((new File("" + locallist[position])).toString(), imageView, options);
        /*Picasso.with(mContext).load(locallist[position]).resize(screenWH[0],screenWH[1]).centerCrop().into(imageView);*/
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        ((ViewPager) container).removeView((View) object);
                  container.removeView((LinearLayout) object);
    }
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

//    @Override
//    public void startUpdate(View container) { @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((LinearLayout) object);
//        }
//    }
}
