package com.flashtr.adapter;

import android.app.Activity;
import android.content.Context;
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
import com.flashtr.model.AllAlbum;
import com.flashtr.model.MemberDetail;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 24B on 5/9/2016.
 */
public class AdpAlbumDetail extends BaseAdapter {

    private List<MemberDetail> listAllAlbum;
    private Context mContext;
    private Typeface font;

    public AdpAlbumDetail(Context context, List<MemberDetail> listAllAlbum) {
        this.mContext = context;
        this.listAllAlbum = listAllAlbum;
        font = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listAllAlbum.size();
//		return 5;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listAllAlbum.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public void setData(List<MemberDetail> listAllAlbum){
        this.listAllAlbum = listAllAlbum;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listraw_members, viewGroup, false);

            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvTxtName = (TextView) convertView.findViewById(R.id.tvTxtName);
            holder.tvMobile = (TextView) convertView.findViewById(R.id.tvMobile);
            holder.rlTick = (RelativeLayout) convertView.findViewById(R.id.rlTick);
            holder.rlAdmin = (RelativeLayout) convertView.findViewById(R.id.rlAdmin);
            holder.img = (ImageView) convertView.findViewById(R.id.img);

            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MemberDetail detail = listAllAlbum.get(position);

        if(position%2==0){
            holder.tvTxtName.setBackgroundResource(R.drawable.rounded_colored_odd);
        }else{
            holder.tvTxtName.setBackgroundResource(R.drawable.rounded_colored_even);
        }

        holder.tvName.setTypeface(font);
        holder.tvTxtName.setTypeface(font);
        holder.tvMobile.setTypeface(font);
        holder.rlTick.setVisibility(View.GONE);
        if(detail.getIsadmin()==1) {
            holder.rlAdmin.setVisibility(View.VISIBLE);
        }else{
            holder.rlAdmin.setVisibility(View.GONE);
        }

        holder.tvName.setText("" + detail.getMember_name());
        holder.tvMobile.setText("Mobile: " + detail.getMember_number());
        if(detail.getMember_image()!=null && detail.getMember_image().length()>0 /*hashMap.get("member_image") != null && hashMap.get("member_image").length()>0*/){
            /*Log.e("IMG", "" + hashMap.get("member_image"));*/
//            imageLoader.displayImage("" + hashMap.get("member_image"), holder.img, options);
            holder.img.setVisibility(View.VISIBLE);
            holder.tvTxtName.setVisibility(View.GONE);
            Picasso.with(mContext).load(detail.getMember_image()).resize(100,100).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(holder.img);
        }else if(detail.getMember_name()!=null&&detail.getMember_name().length()>0){
            holder.img.setVisibility(View.GONE);
            holder.tvTxtName.setVisibility(View.VISIBLE);
            String[] txt = detail.getMember_name().split(" ");
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



        return convertView;
    }

    static class ViewHolder {
        TextView tvTxtName;
        TextView tvMobile;
        TextView tvName;
        ImageView img;
        RelativeLayout rlTick,rlAdmin;

    }
    protected void write(String key, String val) {
        Util.WriteSharePrefrence(mContext, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(mContext, key);
    }

}
