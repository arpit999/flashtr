package com.flashtr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flashtr.R;
import com.flashtr.customView.RoundedTransformation;
import com.flashtr.model.MemberDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by MAHADEV on 03-10-2015.
 */
public class ReactionAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater = null;
    List<MemberDetail> locallist;

    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Typeface fonTypeface,fonTypefaceBD;


    public ReactionAdapter(Context mContext, List<MemberDetail> locallist) {

        this.mContext = mContext;
        this.locallist = locallist;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.user_img).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.user_img).showImageOnFail(R.drawable.user_img).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
        fonTypeface = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");
        fonTypefaceBD = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUIB.TTF");

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
    public View getView(final int position, View view, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_reaction, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final MemberDetail hashMap = locallist.get(position);

        holder.tvName.setTypeface(fonTypeface);
        holder.tvMobile.setTypeface(fonTypeface);
        holder.tvTxtName.setTypeface(fonTypeface);

        holder.img.setVisibility(View.VISIBLE);
        holder.tvTxtName.setVisibility(View.GONE);

        holder.tvName.setText("" + hashMap.getMember_name());
        holder.tvMobile.setText("Mobile: " + hashMap.getMember_number());
        if(hashMap.getMember_image()!=null&&hashMap.getMember_image().length()>0){
            /*imageLoader.displayImage("" + hashMap.getMember_image(), holder.img, options);*/
            holder.img.setVisibility(View.VISIBLE);
            holder.tvTxtName.setVisibility(View.INVISIBLE);
            Picasso.with(mContext).load(hashMap.getMember_image()).resize(500,500).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(holder.img);
        }else if(hashMap.getMember_name()!=null && hashMap.getMember_name().length()>0){
            holder.img.setVisibility(View.INVISIBLE);
            holder.tvTxtName.setVisibility(View.VISIBLE);
            String[] txt = hashMap.getMember_name().split(" ");
            switch (txt.length){
                case 1:
                    holder.tvTxtName.setText(txt[0].charAt(0)+"");
                    break;
                case 2:
                    holder.tvTxtName.setText(txt[0].charAt(0)+""+txt[1].charAt(0));
                    break;
                default:
                    holder.tvTxtName.setText(txt[0].charAt(0)+""+txt[1].charAt(0));
                    break;
            }
        }else{
            holder.img.setVisibility(View.VISIBLE);
            holder.tvTxtName.setVisibility(View.INVISIBLE);
            Picasso.with(mContext).load(R.drawable.default_profile).resize(500,500).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(holder.img);
        }

        holder.rlTick.setVisibility(View.GONE);

        return view;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvMobile;
        ImageView img;
        RelativeLayout rlTick;
        TextView tvTxtName;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvMobile = (TextView) view.findViewById(R.id.tvMobile);
            img = (ImageView) view.findViewById(R.id.img);
            rlTick = (RelativeLayout) view.findViewById(R.id.rlTick);
            tvTxtName = (TextView) view.findViewById(R.id.tvTxtName);
        }
    }

    public void setData(List<MemberDetail> list){
        locallist = list;
        notifyDataSetChanged();
    }

}



