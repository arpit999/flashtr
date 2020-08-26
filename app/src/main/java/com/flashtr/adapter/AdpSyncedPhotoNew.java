package com.flashtr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flashtr.R;
import com.flashtr.customView.RoundedTransformation;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by 24B on 4/28/2016.
 */
public class AdpSyncedPhotoNew extends RecyclerView.Adapter<AdpSyncedPhotoNew.ViewHolder> {

    private Context mContext;
    private int[] screenWH;
    private ArrayList<HashMap<String, String>> localList;
    private int pageLimit,page;
    private String user_id;
    private HomeFragment instFragment;
    private Typeface typeBD,type;

    public  AdpSyncedPhotoNew(Context context, ArrayList<HashMap<String, String>> localList, int page,int pageLimit,HomeFragment instFragment) {
        this.mContext = context;
        this.localList = localList;
        screenWH = Util.getScreenWidthHeight(context);
        this.page = page;
        this.pageLimit = pageLimit;
        user_id = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID);
        this.instFragment = instFragment;
        type = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");
        typeBD = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUIB.TTF");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_new_list_home, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    public void setData(ArrayList<HashMap<String, String>> localList, int page, int pageLimit) {
        this.localList = localList;
        this.page = page;
        this.pageLimit = pageLimit;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HashMap<String, String> hashMap = localList.get(position);

        holder.mTxtUserName.setTypeface(typeBD);
        holder.txtLliked1.setTypeface(typeBD);
        holder.txtLliked2.setTypeface(type);
        holder.mTxtDateTime.setTypeface(type);
        holder.mTxtLoadMore.setTypeface(type);
        holder.mTxtDipText1.setTypeface(type);

        if (hashMap.get("thumbImage") != null && hashMap.get("thumbImage").length() > 0) {
            Picasso.with(mContext).load("" + hashMap.get("thumbImage")).into(holder.mIvAlbumPhoto);
            holder.ivError.setVisibility(View.GONE);
            holder.iv_albumNoPhoto.setVisibility(View.VISIBLE);
        } else {
            holder.iv_albumNoPhoto.setVisibility(View.GONE);
            holder.ivError.setVisibility(View.VISIBLE);
//            Picasso.with(mContext).load(R.drawable.default_img).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(holder.mIvAlbumPhoto);
        }

        if (hashMap.get("user_image") != null && hashMap.get("user_image").length() > 0) {
            holder.mImgUserProfie.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load("" + hashMap.get("user_image")).resize((int) (screenWH[0] * 0.6), (int) (screenWH[0] * 0.6)).centerCrop().transform(new RoundedTransformation(0, Color.TRANSPARENT)).into(holder.mImgUserProfie);
        } else {
//            Picasso.with(mContext).load(R.drawable.default_img).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(holder.mImgUserProfie);
            Picasso.with(mContext).load(R.drawable.default_profile).resize((int) (screenWH[0] * 0.6), (int) (screenWH[0] * 0.6)).centerCrop().transform(new RoundedTransformation(0, Color.TRANSPARENT)).into(holder.mImgUserProfie);
            holder.mImgUserProfie.setVisibility(View.VISIBLE);
        }

        if (hashMap.get("user_name") != null && hashMap.get("user_name").length() > 0) {
            holder.mTxtUserName.setText("" + hashMap.get("user_name"));
        }

        /*Date format: 2016-04-29 20:25:31*/
        if (hashMap.get("date") != null && hashMap.get("date").length() > 0) {
            String date = hashMap.get("date");
            SimpleDateFormat parseSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formateDate = "";
            SimpleDateFormat FormateSDF = new SimpleDateFormat("hh:mm a  MMMM dd yyyy");
            try {
                parseSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date parseDate = parseSDF.parse(date);

                formateDate = FormateSDF.format(parseDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.mTxtDateTime.setText(/*time + " " + timex*/formateDate);
        }

        if (position == (getItemCount() - 1) && !(page >= pageLimit)) {
            if (page > 2) {
                holder.mProgressBar2.setVisibility(View.GONE);
                holder.mTxtLoadMore.setVisibility(View.VISIBLE);
            } else {
                holder.mTxtLoadMore.setVisibility(View.GONE);
                holder.mProgressBar2.setVisibility(View.VISIBLE);
            }
        } else {
            holder.mTxtLoadMore.setVisibility(View.GONE);
            holder.mProgressBar2.setVisibility(View.GONE);
        }

        if(hashMap.get("isliked").contains(user_id)){
            holder.mIvEmo1.setImageResource(R.drawable.like1_selected);
        }else{
            holder.mIvEmo1.setImageResource(R.drawable.like1);
        }

        if(hashMap.get("isloved").contains(user_id)){
            holder.mIvEmo2.setImageResource(R.drawable.like2_selected);
        }else{
            holder.mIvEmo2.setImageResource(R.drawable.like2);
        }

        if(hashMap.get("ishaha").contains(user_id)){
            holder.mIvEmo3.setImageResource(R.drawable.like3_selected);
        }else{
            holder.mIvEmo3.setImageResource(R.drawable.like3);
        }

        if(hashMap.get("iswow").contains(user_id)){
            holder.mIvEmo4.setImageResource(R.drawable.like4_selected);
        }else{
            holder.mIvEmo4.setImageResource(R.drawable.like4);
        }

        if(hashMap.get("isangry").contains(user_id)){
            holder.mIvEmo5.setImageResource(R.drawable.like5_selected);
        }else{
            holder.mIvEmo5.setImageResource(R.drawable.like5);
        }

        if(hashMap.get("issad").contains(user_id)){
            holder.mIvEmo6.setImageResource(R.drawable.like6_selected);
        }else{
            holder.mIvEmo6.setImageResource(R.drawable.like6);
        }

        if(hashMap.get("total_likes")!=null&&hashMap.get("total_likes").length()>0){
            holder.txtLliked1.setVisibility(View.VISIBLE);
            holder.txtLliked2.setVisibility(View.VISIBLE);
            holder.relRec.setVisibility(View.VISIBLE);
            holder.txtLliked1.setText("" + hashMap.get("total_likes"));
            holder.txtLliked2.setText("REACTIONS");
        }else{
            holder.relRec.setVisibility(View.GONE);
            holder.txtLliked2.setVisibility(View.GONE);
            holder.txtLliked1.setVisibility(View.GONE);
        }

        if(hashMap.get("favourite_photo").equalsIgnoreCase("1")){
            holder.mImgFav.setImageResource(R.drawable.favorite_selected);
        }else{
            holder.mImgFav.setImageResource(R.drawable.favorite);
        }

        holder.mIvAlbumPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });

        holder.mImgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
        holder.mImgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
        holder.mIvEmo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
        holder.mIvEmo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
        holder.mIvEmo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
        holder.mIvEmo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
        holder.mIvEmo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
        holder.mIvEmo6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
        holder.mTxtLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
        holder.relRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });

        holder.mIvAlbumPhoto.setTag(position);
        holder.ivError.setTag(position);
        holder.mImgMenu.setTag(position);
        holder.mIvEmo1.setTag(position);
        holder.mIvEmo2.setTag(position);
        holder.mIvEmo3.setTag(position);
        holder.mIvEmo4.setTag(position);
        holder.mIvEmo5.setTag(position);
        holder.mIvEmo6.setTag(position);
        holder.mImgFav.setTag(position);
        holder.mTxtLoadMore.setTag(position);
        holder.relRec.setTag(position);

    }


    @Override
    public int getItemCount() {
        return localList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImgUserProfie, mImgFav, mImgMenu, mIvAlbumPhoto,ivError;
        TextView mTxtUserName, mTxtDateTime, txtLliked2, txtLliked1;
        ImageView mIvEmo1, mIvEmo2, mIvEmo3, mIvEmo4, mIvEmo5, mIvEmo6;
        LinearLayout mLinDipImo1, mLinDipImo2, mLinDipImo3, mLinDipImo4, mLinDipImo5, mLinDipImo6;
        ImageView mIvDipImo1, iv_albumNoPhoto, mIvDipImo2, mIvDipImo3, mIvDipImo4, mIvDipImo5, mIvDipImo6;
        TextView mTxtDipText1, mTxtDipText2, mTxtDipText3, mTxtDipText4, mTxtDipText5, mTxtDipText6;
        ProgressBar mProgressBar2, mProgressBar3;
        TextView mTxtLoadMore;
        RelativeLayout relRec;


        public ViewHolder(View itemView) {
            super(itemView);
            relRec = (RelativeLayout) itemView.findViewById(R.id.relRec);
            mImgUserProfie = (ImageView) itemView.findViewById(R.id.iv_imgUserProfie);
            mTxtUserName = (TextView) itemView.findViewById(R.id.tv_txtUserName);
            mTxtDateTime = (TextView) itemView.findViewById(R.id.tv_txtDateTime);
            mImgFav = (ImageView) itemView.findViewById(R.id.iv_imgFav);
            mImgMenu = (ImageView) itemView.findViewById(R.id.iv_imgMenu);
            mIvAlbumPhoto = (ImageView) itemView.findViewById(R.id.iv_albumPhoto);
            ivError = (ImageView) itemView.findViewById(R.id.ivError);
            txtLliked1 = (TextView) itemView.findViewById(R.id.txtLliked1);
            txtLliked2 = (TextView) itemView.findViewById(R.id.txtLliked2);
            iv_albumNoPhoto = (ImageView) itemView.findViewById(R.id.iv_albumNoPhoto);

            mIvEmo1 = (ImageView) itemView.findViewById(R.id.iv_imo1);
            mIvEmo2 = (ImageView) itemView.findViewById(R.id.iv_imo2);
            mIvEmo3 = (ImageView) itemView.findViewById(R.id.iv_imo3);
            mIvEmo4 = (ImageView) itemView.findViewById(R.id.iv_imo4);
            mIvEmo5 = (ImageView) itemView.findViewById(R.id.iv_imo5);
            mIvEmo6 = (ImageView) itemView.findViewById(R.id.iv_imo6);

            mLinDipImo1 = (LinearLayout) itemView.findViewById(R.id.lin_dipImo1);
            /*mLinDipImo2 = (LinearLayout) itemView.findViewById(R.id.lin_dipImo2);
            mLinDipImo3 = (LinearLayout) itemView.findViewById(R.id.lin_dipImo3);
            mLinDipImo4 = (LinearLayout) itemView.findViewById(R.id.lin_dipImo4);
            mLinDipImo5 = (LinearLayout) itemView.findViewById(R.id.lin_dipImo5);
            mLinDipImo6 = (LinearLayout) itemView.findViewById(R.id.lin_dipImo6);
*/
            mIvDipImo1 = (ImageView) itemView.findViewById(R.id.iv_dipImo1);
  /*          mIvDipImo2 = (ImageView) itemView.findViewById(R.id.iv_dipImo2);
            mIvDipImo3 = (ImageView) itemView.findViewById(R.id.iv_dipImo3);
            mIvDipImo4 = (ImageView) itemView.findViewById(R.id.iv_dipImo4);
            mIvDipImo5 = (ImageView) itemView.findViewById(R.id.iv_dipImo5);
            mIvDipImo6 = (ImageView) itemView.findViewById(R.id.iv_dipImo6);*/

            mTxtDipText1 = (TextView) itemView.findViewById(R.id.tv_txtDipText1);
            /*mTxtDipText2 = (TextView) itemView.findViewById(R.id.tv_txtDipText2);
            mTxtDipText3 = (TextView) itemView.findViewById(R.id.tv_txtDipText3);
            mTxtDipText4 = (TextView) itemView.findViewById(R.id.tv_txtDipText4);
            mTxtDipText5 = (TextView) itemView.findViewById(R.id.tv_txtDipText5);
            mTxtDipText6 = (TextView) itemView.findViewById(R.id.tv_txtDipText6);*/

            mProgressBar3 = (ProgressBar) itemView.findViewById(R.id.progressBar2);
            mProgressBar2 = (ProgressBar) itemView.findViewById(R.id.progressBar2);
            mTxtLoadMore = (TextView) itemView.findViewById(R.id.tv_txtLoadMore);



        }
    }

}
