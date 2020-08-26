package com.flashtr.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.R;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by MAHADEV on 30-09-2015.
 */

public class SpinnerAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<HashMap<String, String>> locallist;
    private static LayoutInflater inflater = null;

    public int getCount() {
        return locallist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public SpinnerAdapter(Activity context, ArrayList<HashMap<String, String>> locallist) {

        this.context = context;
        this.locallist = locallist;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_spinner, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
//        ViewGroup album = (ViewGroup) view.findViewById(R.id.rlAlbumName);
//        Util.setFont(album, Util.getFont(2, context.getApplicationContext()));
//        ViewGroup root = (ViewGroup) view.findViewById(R.id.rlDateInner);
//        Util.setFont(root, Util.getFont(1, context.getApplicationContext()));
        final HashMap<String, String> hashMap = locallist.get(position);
        holder.tvTitle.setText(hashMap.get("album_name"));
        holder.tvMember.setText("Members : " + hashMap.get("member_count"));
        holder.tvPhoto.setText("Photos : " + hashMap.get("photo_count"));

        String dt = hashMap.get("create_date");
        List<String> elephantList = Arrays.asList(dt.split("-"));
        int month = Integer.parseInt(elephantList.get(1));
        List<String> data = Arrays.asList(elephantList.get(2).split(" "));
        int day = Integer.parseInt(data.get(0));
        String dx = getDayOfMonthSuffix(day);
        holder.tvDate.setText("" +getMonthSuffix(month)+ " " +day+", "+elephantList.get(0));

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "SEGOEUIB.TTF");
        holder.tvTitle.setTypeface(typeface);

        Typeface type = Typeface.createFromAsset(context.getAssets(), "SEGOEUI.TTF");
        holder.tvDate.setTypeface(type);
        holder.tvMember.setTypeface(type);
        holder.tvPhoto.setTypeface(type);

        holder.mlSelectAlbumRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_Current_AlbumID, "" + hashMap.get("album_id"));
                Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_Current_AlbumName, "" + hashMap.get("album_name"));
                Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_RELOAD_Home, "1");
                Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_RELOAD_Home, "1");
                Util.WriteSharePrefrence(context,Constant.SHRED_PR.KEY_SELECTED_TAB,""+0);
                context.finish();
            }
        });
        return view;
    }


    public class ViewHolder {
        @InjectView(R.id.tvTitle)
        TextView tvTitle;
        @InjectView(R.id.rlSelectAlbum)
        RelativeLayout rlSelectAlbum;
        @InjectView(R.id.tvMember)
        TextView tvMember;
        @InjectView(R.id.tvPhoto)
        TextView tvPhoto;
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.mlSelectAlbumRow)
        MaterialRippleLayout mlSelectAlbumRow;
//        @InjectView(R.id.rlDateInner)
//        RelativeLayout rlDateInner;
//        @InjectView(R.id.rlAlbumName)
//        RelativeLayout rlAlbumName;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


   public String getDayOfMonthSuffix(final int n) {
        switch (n) {
            case 1:
            case 21:
            case 31:
                return "st";
            case 2:
            case 22:
                return "nd";
            case 3:
            case 23:
                return "rd";
            default: return "th";
        }
    }

    public String getMonthSuffix(final int n) {
        String month = "";
        switch (n) {
            case 1:  month = "Jan";
                break;
            case 2:  month = "Feb";
                break;
            case 3:  month = "Mar";
                break;
            case 4: month = "Apr";
                break;
            case 5: month = "May";
                break;
            case 6: month = "Jun";
                break;
            case 7:  month = "Jul";
                break;
            case 8:  month = "Aug";
                break;
            case 9: month = "Sep";
                break;
            case 10: month = "Oct";
                break;
            case 11:  month = "Nov";
                break;
            case 12:  month = "Dec";
                break;
        }
        return month;
    }
}

