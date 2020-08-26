package com.flashtr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flashtr.R;
import com.flashtr.fragment.FavFragmentNew;
import com.flashtr.model.Favorite;
import com.flashtr.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 24B on 5/7/2016.
 */
public class AdpAlbumsList extends RecyclerView.Adapter<AdpAlbumsList.ViewHolder> {

    private Context mContext;
    private List<Favorite> favist;
    private int[] screenWH;
    private FavFragmentNew instFragment;

    public AdpAlbumsList(Context context, List<Favorite> favist, FavFragmentNew instFragment) {
        this.mContext = context;
        this.favist = favist;
        this.instFragment = instFragment;
        screenWH = Util.getScreenWidthHeight(context);
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

        if (favData.getCover_image() != null && favData.getCover_image().length() > 0) {
            Picasso.with(mContext).load(favData.getCover_image()).resize(200,200).centerCrop().into(holder.iv_imgUserProfie);
        } else {
//            Picasso.with(mContext).load(R.drawable.default_img).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(holder.iv_imgUserProfie);
        }

        if (favData.getAlbum_name() != null && favData.getAlbum_name().length() > 0) {
            holder.tv_txtUserName.setText("" + favData.getAlbum_name());
        }

        if(favData.getThumb_image()!=null&&favData.getThumb_image().length()>0){
            Picasso.with(mContext).load(favData.getThumb_image()).resize((int) (screenWH[0] * 0.6), (int) (screenWH[0] * 0.6)).centerCrop().into(holder.iv_albumPhoto);
        } else {
//            Picasso.with(mContext).load(R.drawable.default_img).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(holder.iv_albumPhoto);
        }

        holder.iv_imgFav.setTag(position);
        holder.iv_albumPhoto.setTag(position);

        holder.iv_imgFav.setOnClickListener(new View.OnClickListener() {
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
        TextView tv_txtUserName;
        ImageView iv_imgFav;
        ProgressBar progressBar3;
        ImageView iv_albumPhoto;


        public ViewHolder(View itemView) {
            super(itemView);
            iv_imgUserProfie = (ImageView) itemView.findViewById(R.id.iv_imgUserProfie);
            tv_txtUserName = (TextView) itemView.findViewById(R.id.tv_txtUserName);
            iv_imgFav = (ImageView) itemView.findViewById(R.id.iv_imgFav);
            progressBar3 = (ProgressBar) itemView.findViewById(R.id.progressBar3);
            iv_albumPhoto = (ImageView) itemView.findViewById(R.id.iv_albumPhoto);


        }
    }

}
