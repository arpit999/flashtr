package com.flashtr.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.R;
import com.flashtr.customView.RoundedTransformation;
import com.flashtr.fragment.FavFragmentNew;
import com.flashtr.model.Favorite;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by 24B on 5/7/2016.
 */
public class AdpFavorite extends RecyclerView.Adapter<AdpFavorite.ViewHolder> {

    private Context mContext;
    private List<Favorite> favist;
    private int[] screenWH;
    private FavFragmentNew instFragment;
    private Typeface typeBD, type;
    private String user_id;

    public AdpFavorite(Context context, List<Favorite> favist, FavFragmentNew instFragment) {
        this.mContext = context;
        this.favist = favist;
        this.instFragment = instFragment;
        screenWH = Util.getScreenWidthHeight(context);
        user_id = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID);
        type = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");
        typeBD = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUIB.TTF");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_fav, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    public void setData(List<Favorite> favist) {
        this.favist = favist;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Favorite favData = favist.get(position);

        holder.tv_txtAlbumDate.setTypeface(type);
        holder.tv_txtAlbumUserName.setTypeface(typeBD);
        holder.tv_txtAlbumDateTime.setTypeface(type);
        holder.txtLliked1.setTypeface(typeBD);
        holder.txtLliked2.setTypeface(type);
        holder.tv_txtPhotoDateTime.setTypeface(type);


        if (favData.getUser_image() != null && favData.getUser_image().length() > 0) {
            Log.d("PATH",favData.getCover_image()+"");
            Picasso.with(mContext).load(favData.getUser_image()).resize(200, 200).centerCrop().transform(new RoundedTransformation(0, Color.TRANSPARENT)).into(holder.iv_imgUserProfie);
        } else {
//            Picasso.with(mContext).load(R.drawable.default_img).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(holder.iv_imgUserProfie);
        }

        if (favData.getUser_name() != null && favData.getUser_name().length() > 0) {
            holder.tv_txtAlbumUserName.setText("" + favData.getUser_name());
        }

        if(favData.getTotal_likes()>0){
            holder.relRec.setVisibility(View.VISIBLE);
            holder.txtLliked1.setVisibility(View.VISIBLE);
            holder.txtLliked2.setVisibility(View.VISIBLE);
            holder.txtLliked1.setText(favData.getTotal_likes() + "");
            holder.txtLliked2.setText("REACTIONS");
        }else{
            holder.relRec.setVisibility(View.GONE);
            holder.txtLliked1.setVisibility(View.GONE);
            holder.txtLliked2.setVisibility(View.GONE);
        }

        if (favData.getAlbum_name() != null && favData.getAlbum_name().length() > 0) {
            holder.tv_txtAlbumDate.setText("Album: " + favData.getAlbum_name());
        }

        if (favData.getThumb_image() != null && favData.getThumb_image().length() > 0) {
            holder.iv_albumNoPhoto.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(favData.getThumb_image()).resize((int) (screenWH[0] * 0.6), (int) (screenWH[0] * 0.6)).centerCrop().into(holder.iv_albumPhoto);
        } else {
            holder.iv_albumNoPhoto.setVisibility(View.VISIBLE);
//            Picasso.with(mContext).load(R.drawable.default_img).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(holder.iv_albumPhoto);
        }

        /*Date format: 2016-04-29 20:25:31*/
        if (favData.getModify_date() != null && favData.getModify_date().length() > 0) {
            String date = favData.getModify_date();
            SimpleDateFormat parseSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formateDate = "";
            SimpleDateFormat FormateSDF = new SimpleDateFormat("hh:mm a  MMMM dd yyyy");
            try {
                parseSDF .setTimeZone(TimeZone.getTimeZone("UTC"));
                Date parseDate = parseSDF.parse(date);
                formateDate = FormateSDF.format(parseDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tv_txtPhotoDateTime.setText(/*time + " " + timex*/formateDate);
        }else{
            holder.tv_txtPhotoDateTime.setText("");
        }

        /*Date format: 2016-04-29 20:25:31*/
        if (favData.getDate() != null && favData.getDate().length() > 0) {
            String date = favData.getDate();
            SimpleDateFormat parseSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formateDate = "";
            SimpleDateFormat FormateSDF = new SimpleDateFormat("hh:mm a  MMMM dd yyyy");
            try {
                Date parseDate = parseSDF.parse(date);
                formateDate = FormateSDF.format(parseDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tv_txtAlbumDateTime.setText(/*time + " " + timex*/formateDate);
        }else{
            holder.tv_txtAlbumDateTime.setText("");
        }


        holder.iv_imgFav.setTag(position);
        holder.iv_albumPhoto.setTag(position);
        holder.mIvEmo1.setTag(position);
        holder.mIvEmo2.setTag(position);
        holder.mIvEmo3.setTag(position);
        holder.mIvEmo4.setTag(position);
        holder.mIvEmo5.setTag(position);
        holder.mIvEmo6.setTag(position);
        holder.relRec.setTag(position);

        if(favData.getIsliked().contains(user_id)){
            holder.mIvEmo1.setImageResource(R.drawable.like1_selected);
        }else{
            holder.mIvEmo1.setImageResource(R.drawable.like1);
        }

        if(favData.getIsloved().contains(user_id)){
            holder.mIvEmo2.setImageResource(R.drawable.like2_selected);
        }else{
            holder.mIvEmo2.setImageResource(R.drawable.like2);
        }

        if(favData.getIshaha().contains(user_id)){
            holder.mIvEmo3.setImageResource(R.drawable.like3_selected);
        }else{
            holder.mIvEmo3.setImageResource(R.drawable.like3);
        }

        if(favData.getIswow().contains(user_id)){
            holder.mIvEmo4.setImageResource(R.drawable.like4_selected);
        }else{
            holder.mIvEmo4.setImageResource(R.drawable.like4);
        }

        if(favData.getIsangry().contains(user_id)){
            holder.mIvEmo5.setImageResource(R.drawable.like5_selected);
        }else{
            holder.mIvEmo5.setImageResource(R.drawable.like5);
        }

        if(favData.getIssad().contains(user_id)){
            holder.mIvEmo6.setImageResource(R.drawable.like6_selected);
        }else{
            holder.mIvEmo6.setImageResource(R.drawable.like6);
        }

        holder.iv_imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Remove from favorites?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Util.isOnline(mContext)) {
                            instFragment.clickEvent(v);
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();


            }
        });

        holder.relRec.setOnClickListener(new View.OnClickListener() {
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

        holder.iv_albumPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instFragment.clickEvent(v);
            }
        });
    }


    @Override
    public int getItemCount() {
        return favist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_imgUserProfie;
        TextView tv_txtAlbumDate, txtLliked1, txtLliked2, tv_txtAlbumDateTime, tv_txtPhotoDateTime, tv_txtAlbumUserName;
        ImageView iv_imgFav;
        ProgressBar progressBar3;
        ImageView mIvEmo1, mIvEmo2, mIvEmo3, mIvEmo4, mIvEmo5, mIvEmo6,iv_albumNoPhoto;
        ImageView iv_albumPhoto;
        RelativeLayout relRec;


        public ViewHolder(View itemView) {
            super(itemView);
            iv_imgUserProfie = (ImageView) itemView.findViewById(R.id.iv_imgUserProfie);
            tv_txtAlbumDate = (TextView) itemView.findViewById(R.id.tv_txtAlbumDate);
            iv_imgFav = (ImageView) itemView.findViewById(R.id.iv_imgFav);
            progressBar3 = (ProgressBar) itemView.findViewById(R.id.progressBar3);
            iv_albumPhoto = (ImageView) itemView.findViewById(R.id.iv_albumPhoto);
            tv_txtAlbumUserName = (TextView) itemView.findViewById(R.id.tv_txtAlbumUserName);
            tv_txtPhotoDateTime = (TextView) itemView.findViewById(R.id.tv_txtPhotoDateTime);
            tv_txtAlbumDateTime = (TextView) itemView.findViewById(R.id.tv_txtAlbumDateTime);
            txtLliked1 = (TextView) itemView.findViewById(R.id.txtLliked1);
            txtLliked2 = (TextView) itemView.findViewById(R.id.txtLliked2);
            relRec = (RelativeLayout) itemView.findViewById(R.id.relRec);
            iv_albumNoPhoto = (ImageView) itemView.findViewById(R.id.iv_albumNoPhoto);

            mIvEmo1 = (ImageView) itemView.findViewById(R.id.iv_imo1);
            mIvEmo2 = (ImageView) itemView.findViewById(R.id.iv_imo2);
            mIvEmo3 = (ImageView) itemView.findViewById(R.id.iv_imo3);
            mIvEmo4 = (ImageView) itemView.findViewById(R.id.iv_imo4);
            mIvEmo5 = (ImageView) itemView.findViewById(R.id.iv_imo5);
            mIvEmo6 = (ImageView) itemView.findViewById(R.id.iv_imo6);

        }
    }

}
