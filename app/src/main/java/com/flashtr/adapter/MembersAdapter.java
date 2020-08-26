package com.flashtr.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.R;
import com.flashtr.customView.RoundedTransformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by MAHADEV on 03-10-2015.
 */
public class MembersAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater = null;
    ArrayList<HashMap<String, String>> locallist;
    private  Typeface type,typeBD;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public MembersAdapter(Context mContext, ArrayList<HashMap<String, String>> locallist) {

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
        type = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");
        typeBD = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUIB.TTF");

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
            view = inflater.inflate(R.layout.listraw_members, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final HashMap<String, String> hashMap = locallist.get(position);

        if(position%2==0){
            holder.tvTxtName.setBackgroundResource(R.drawable.rounded_colored_odd);
        }else{
            holder.tvTxtName.setBackgroundResource(R.drawable.rounded_colored_even);
        }

        holder.tvName.setTypeface(type);
        holder.tvTxtName.setTypeface(typeBD);
        holder.tvMobile.setTypeface(type);

        holder.tvName.setText("" + hashMap.get("member_name"));
        holder.tvMobile.setText("Mobile: " + hashMap.get("member_number"));
        if(hashMap.get("member_image") != null && hashMap.get("member_image").length()>0){
            Log.e("IMG", "" + hashMap.get("member_image"));
//            imageLoader.displayImage("" + hashMap.get("member_image"), holder.img, options);
            holder.img.setVisibility(View.VISIBLE);
            holder.tvTxtName.setVisibility(View.GONE);
            Picasso.with(mContext).load(hashMap.get("member_image")).resize(100,100).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(holder.img);
        }else if(hashMap.get("member_name") != null && hashMap.get("member_name").length()>0){
            holder.img.setVisibility(View.GONE);
            holder.tvTxtName.setVisibility(View.VISIBLE);
            String[] txt = hashMap.get("member_name").split(" ");
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
            holder.tvTxtName.setVisibility(View.GONE);
            Picasso.with(mContext).load(R.drawable.default_profile).resize(100,100).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(holder.img);
        }



        if (hashMap.get("selected").equals("0")) {
            holder.imgTick.setImageResource(R.drawable.plus);
        } else if (hashMap.get("selected").equals("1")) {
            holder.imgTick.setImageResource(R.drawable.right);
        } else if (hashMap.get("selected").equals("2")) {
            holder.imgTick.setImageResource(R.drawable.invite);
        }

        holder.rlTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hashMap.get("selected").equals("0")) {
                    holder.imgTick.setImageResource(R.drawable.right);
                    locallist.get(position).put("selected", "1");
                } else if (hashMap.get("selected").equals("1")) {
                    holder.imgTick.setImageResource(R.drawable.plus);
                    locallist.get(position).put("selected", "0");
                } else if (hashMap.get("selected").equals("2")) {
                    try {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("sms_body", "Check out Flashtr, Download it today from "+ Uri.parse("https://goo.gl/kKtEUd"));
                        sendIntent.putExtra("address", "" + hashMap.get("member_number"));
                        sendIntent.setType("vnd.android-dir/mms-sms");
                        mContext.startActivity(sendIntent);
                    } catch (Exception e) {
                        Toast.makeText(mContext,
                                "SMS failed, please try again later!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.tvMobile)
        TextView tvMobile;
        @InjectView(R.id.imgTick)
        ImageButton imgTick;
        @InjectView(R.id.tvTxtName)
        TextView tvTxtName;
        @InjectView(R.id.rlTick)
        RelativeLayout rlTick;
        @InjectView(R.id.img)
        ImageView img;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);



        }
    }
}


