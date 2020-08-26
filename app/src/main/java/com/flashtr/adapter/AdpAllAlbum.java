package com.flashtr.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flashtr.R;
import com.flashtr.fragment.DemoFragment;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.model.AllAlbum;
import com.flashtr.model.Favorite;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 24B on 5/9/2016.
 */
public class AdpAllAlbum extends BaseAdapter {

    private List<AllAlbum> listAllAlbum;
    private Context mContext;
    private Typeface font;

    public AdpAllAlbum(Context context, List<AllAlbum> listAllAlbum) {
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

    public void setData(List<AllAlbum> listAllAlbum){
        this.listAllAlbum = listAllAlbum;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_all_album, viewGroup, false);

            holder.tv_txtName = (TextView) convertView.findViewById(R.id.tv_txtName);
            holder.tv_txtMemberCount = (TextView) convertView.findViewById(R.id.tv_txtMemberCount);
            holder.tv_txtPhotoCount = (TextView) convertView.findViewById(R.id.tv_txtPhotoCount);
            holder.tv_txtDate = (TextView) convertView.findViewById(R.id.tv_txtDate);

            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final AllAlbum favData = listAllAlbum.get(position);
        holder.tv_txtMemberCount.setTypeface(font);
        holder.tv_txtName.setTypeface(font);
        holder.tv_txtPhotoCount.setTypeface(font);
        holder.tv_txtDate.setTypeface(font);

        if (favData.getAlbum_name() != null && favData.getAlbum_name().length() > 0) {
            holder.tv_txtName.setText(favData.getAlbum_name());
        }

        holder.tv_txtMemberCount.setText("Members: " + favData.getMember_count());

        holder.tv_txtPhotoCount.setText("Photos: " + favData.getPhoto_count());

        if (favData.getCreate_date() != null && favData.getCreate_date().length() > 0) {
            String date = favData.getCreate_date();
            SimpleDateFormat parseSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formateDate = "";
            SimpleDateFormat FormateSDF = new SimpleDateFormat("MMMM dd yyyy");
            try {
                Date parseDate = parseSDF.parse(date);
                formateDate = FormateSDF.format(parseDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tv_txtDate.setText(/*time + " " + timex*/formateDate);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumID, favData.getAlbum_id());
                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumName, favData.getAlbum_name());
//                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Albums, "" + listAllAlbum);
                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_CREATEDBYME, favData.getCreated_by_me()+"");

                    write(Constant.SHRED_PR.KEY_RELOAD_Home, "1");
                    write(Constant.SHRED_PR.KEY_RELOAD_Albums, "15");

                    com.flashtr.HomeActivity.selectedPosition = 0;
                    String albumName = "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumName);
                    String albumId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumID);

                    ((Activity)mContext).finish();
                }
            });

        }
        return convertView;
    }

    static class ViewHolder {
        TextView tv_txtName;
        TextView tv_txtMemberCount;
        TextView tv_txtPhotoCount;
        TextView tv_txtDate;

    }
    protected void write(String key, String val) {
        Util.WriteSharePrefrence(mContext, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(mContext, key);
    }

}
