package com.flashtr.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.R;
import com.flashtr.activity.ActivityEditAlbum;
import com.flashtr.activity.ActivityUpdateAlbum;
import com.flashtr.activity.CaptureActivity;
import com.flashtr.app.MyApp;
import com.flashtr.customView.RoundedTransformation;
import com.flashtr.fragment.DemoFragment;
import com.flashtr.fragment.FragmentAlbumListing;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.model.AllAlbum;
import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 24B on 5/9/2016.
 */
public class AdpAlbumList extends BaseAdapter {

    private List<AllAlbum> listAllAlbum;
    private Context mContext;
    private Typeface font;
    private Typeface font_bold;
    private int noMoreData;
    private int page;
    private FragmentAlbumListing fragment;
    private int[] screenWH;

    public AdpAlbumList(Context context, List<AllAlbum> listAllAlbum, int noMoreData, int page, FragmentAlbumListing fragment) {
        this.mContext = context;
        this.listAllAlbum = listAllAlbum;
        this.noMoreData = noMoreData;
        this.page = page;
        this.fragment = fragment;
        font = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");
        font_bold = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUIB.TTF");
        screenWH = Util.getScreenWidthHeight(mContext);
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

    public void setData(List<AllAlbum> listAllAlbum, int noMoreData, int page) {
        this.listAllAlbum = listAllAlbum;
        this.noMoreData = noMoreData;
        this.page = page;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list_album, viewGroup, false);

            holder.backImage = (ImageView) convertView.findViewById(R.id.backImage);
            holder.rel_Image = (RelativeLayout) convertView.findViewById(R.id.rel_Image);
            holder.txt_albumName = (TextView) convertView.findViewById(R.id.txt_albumName);
            holder.txt_date = (TextView) convertView.findViewById(R.id.txt_date);
            holder.txt_memberCount = (TextView) convertView.findViewById(R.id.txt_memberCount);
            holder.txt_photoCount = (TextView) convertView.findViewById(R.id.txt_photoCount);
            holder.tv_txtLoadMore = (TextView) convertView.findViewById(R.id.tv_txtLoadMore);
            holder.progressBar2 = (ProgressBar) convertView.findViewById(R.id.progressBar2);
            holder.lin_addMemberPhoto = (LinearLayout) convertView.findViewById(R.id.lin_addMemberPhoto);

            holder.rel1 = (RelativeLayout) convertView.findViewById(R.id.rel1);
            holder.tvTxtName1 = (TextView) convertView.findViewById(R.id.tvTxtName1);
            holder.userImage1 = (ImageView) convertView.findViewById(R.id.userImage1);
            holder.rel2 = (RelativeLayout) convertView.findViewById(R.id.rel2);
            holder.tvTxtName2 = (TextView) convertView.findViewById(R.id.tvTxtName2);
            holder.userImage2 = (ImageView) convertView.findViewById(R.id.userImage2);
            holder.rel3 = (RelativeLayout) convertView.findViewById(R.id.rel3);
            holder.tvTxtName3 = (TextView) convertView.findViewById(R.id.tvTxtName3);
            holder.userImage3 = (ImageView) convertView.findViewById(R.id.userImage3);
            holder.rel4 = (RelativeLayout) convertView.findViewById(R.id.rel4);
            holder.tvTxtName4 = (TextView) convertView.findViewById(R.id.tvTxtName4);
            holder.userImage4 = (ImageView) convertView.findViewById(R.id.userImage4);

            holder.iv_editPhoto = (ImageView) convertView.findViewById(R.id.iv_editPhoto);
            holder.iv_option = (ImageView) convertView.findViewById(R.id.iv_option);

            holder.txtCount = (TextView) convertView.findViewById(R.id.txtCount);
            holder.noImage = (ImageView) convertView.findViewById(R.id.noImage);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final AllAlbum favData = listAllAlbum.get(position);
        holder.txt_photoCount.setTypeface(font);
        holder.txt_memberCount.setTypeface(font);
        holder.txt_albumName.setTypeface(font_bold);
        holder.txt_date.setTypeface(font);
        holder.tv_txtLoadMore.setTypeface(font);
        holder.txtCount.setTypeface(font);
        /*if(favData.getMember_details().size()>0){
            int photoWidth = screenWH[0]/5;
            int k=0;
            for(int i = 0;i<favData.getMember_details().size();i++){
                if(k>=5){
                    break;
                }else {
                    if(favData.getMember_details().get(i).getMember_image()!=null && favData.getMember_details().get(i).getMember_image().length()>0)
                    {
                        k++;
                        ImageView iv = new ImageView(mContext);
                        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(photoWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                        layoutParam.gravity = Gravity.CENTER_VERTICAL;
                        iv.setLayoutParams(layoutParam);
                        holder.lin_addMemberPhoto.addView(iv);
                        Picasso.with(mContext).load(favData.getMember_details().get(i).getMember_image()).resize(100,100).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(iv);
                    }else if(favData.getMember_details().get(i).getMember_name()!=null&&favData.getMember_details().get(i).getMember_name().length()>0){
                        k++;
                        TextView tv = new TextView(mContext);
                        tv.setTextColor(Color.WHITE);
                        tv.setTextSize(16);
                        tv.setGravity(View.TEXT_ALIGNMENT_CENTER);

                        if (i % 2 == 0) {
                            tv.setBackgroundResource(R.drawable.rounded_colored_even);
                        }else{
                            tv.setBackgroundResource(R.drawable.rounded_colored_odd);
                        }

                        String[] txt = favData.getMember_details().get(i).getMember_name().split(" ");
                        switch (txt.length){
                            case 1:
                                tv.setText(txt[0].charAt(0)+"");
                                break;
                            case 2:
                                tv.setText(txt[0].charAt(0)+""+txt[1].charAt(0));
                                break;
                            default:
                                tv.setText(txt[0].charAt(0)+""+txt[1].charAt(0));
                                break;
                        }

                        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(photoWidth, photoWidth);
                        layoutParam.gravity = Gravity.CENTER;
                        tv.setLayoutParams(layoutParam);
                        holder.lin_addMemberPhoto.addView(tv);
                    }

                }
            }
        }*/

        int Count = Util.getOfflinePic(mContext,favData.getAlbum_id());
        if(Count>0) {
            holder.txtCount.setVisibility(View.VISIBLE);
            if(Count==1){
                holder.txtCount.setText(Count+" Offline Photo");
            }else{
                holder.txtCount.setText(Count+" Offline Photos");
            }

        }else{
            holder.txtCount.setVisibility(View.GONE);
        }
        if (favData.getMember_details() != null && favData.getMember_details().size() > 0) {
            int looping = favData.getMember_details().size() >= 4 ? 4 : favData.getMember_details().size();
            for (int i = 0; i < looping; i++) {
//                switch (i) {
//                    case 0:
                if (i == 0) {
                    if (favData.getMember_details().get(i).getMember_id() != null && favData.getMember_details().get(i).getMember_id().length() > 0) {
                        holder.rel1.setVisibility(View.VISIBLE);
                        holder.rel4.setVisibility(View.GONE);
                        holder.rel3.setVisibility(View.GONE);
                        holder.rel2.setVisibility(View.GONE);
                        if (favData.getMember_details().get(i).getMember_image() != null && favData.getMember_details().get(i).getMember_image().length() > 0) {
                            holder.tvTxtName1.setVisibility(View.GONE);
                            holder.userImage1.setVisibility(View.VISIBLE);
                            Picasso.with(mContext).load(favData.getMember_details().get(i).getMember_image()).resize(100, 100).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(holder.userImage1);
                        } else if (favData.getMember_details().get(i).getMember_name() != null && favData.getMember_details().get(i).getMember_name().length() > 0) {
                            holder.tvTxtName1.setVisibility(View.VISIBLE);
                            holder.userImage1.setVisibility(View.GONE);
                            if (i % 2 == 0) {
                                holder.tvTxtName1.setBackgroundResource(R.drawable.rounded_colored_even);
                            } else {
                                holder.tvTxtName1.setBackgroundResource(R.drawable.rounded_colored_odd);
                            }

                            String[] txt = favData.getMember_details().get(i).getMember_name().split(" ");
                            switch (txt.length) {
                                case 1:
                                    holder.tvTxtName1.setText(txt[0].charAt(0) + "");
                                    break;
                                case 2:
                                    holder.tvTxtName1.setText(txt[0].charAt(0) + "" + txt[1].charAt(0));
                                    break;
                                default:
                                    holder.tvTxtName1.setText(txt[0].charAt(0) + "" + txt[1].charAt(0));
                                    break;
                            }
                        } else {
                            holder.rel1.setVisibility(View.GONE);
                            holder.rel4.setVisibility(View.GONE);
                            holder.rel3.setVisibility(View.GONE);
                            holder.rel2.setVisibility(View.GONE);
                        }
                    } else {
                        holder.rel1.setVisibility(View.GONE);
                        holder.rel4.setVisibility(View.GONE);
                        holder.rel3.setVisibility(View.GONE);
                        holder.rel2.setVisibility(View.GONE);
                    }
//                        break;
//                    case 1:
                } else if (i == 1) {
                    if (favData.getMember_details().get(i).getMember_id() != null && favData.getMember_details().get(i).getMember_id().length() > 0) {
                        holder.rel2.setVisibility(View.VISIBLE);
                        holder.rel4.setVisibility(View.GONE);
                        holder.rel3.setVisibility(View.GONE);
                        if (favData.getMember_details().get(i).getMember_image() != null && favData.getMember_details().get(i).getMember_image().length() > 0) {
                            holder.tvTxtName2.setVisibility(View.GONE);
                            holder.userImage2.setVisibility(View.VISIBLE);
                            Picasso.with(mContext).load(favData.getMember_details().get(i).getMember_image()).resize(100, 100).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(holder.userImage2);
                        } else if (favData.getMember_details().get(i).getMember_name() != null && favData.getMember_details().get(i).getMember_name().length() > 0) {
                            holder.userImage2.setVisibility(View.GONE);
                            holder.tvTxtName2.setVisibility(View.VISIBLE);
                            if (i % 2 == 0) {
                                holder.tvTxtName2.setBackgroundResource(R.drawable.rounded_colored_even);
                            } else {
                                holder.tvTxtName2.setBackgroundResource(R.drawable.rounded_colored_odd);
                            }

                            String[] txt = favData.getMember_details().get(i).getMember_name().split(" ");
                            switch (txt.length) {
                                case 1:
                                    holder.tvTxtName2.setText(txt[0].charAt(0) + "");
                                    break;
                                case 2:
                                    holder.tvTxtName2.setText(txt[0].charAt(0) + "" + txt[1].charAt(0));
                                    break;
                                default:
                                    holder.tvTxtName2.setText(txt[0].charAt(0) + "" + txt[1].charAt(0));
                                    break;
                            }
                        } else {
                            holder.rel2.setVisibility(View.GONE);
                            holder.rel4.setVisibility(View.GONE);
                            holder.rel3.setVisibility(View.GONE);
                        }
                    } else {
                        holder.rel2.setVisibility(View.GONE);
                        holder.rel4.setVisibility(View.GONE);
                        holder.rel3.setVisibility(View.GONE);
                    }
//                        break;
//                    case 2:
                } else if (i == 2) {
                    if (favData.getMember_details().get(i).getMember_id() != null && favData.getMember_details().get(i).getMember_id().length() > 0) {
                        holder.rel3.setVisibility(View.VISIBLE);
                        holder.rel4.setVisibility(View.GONE);
                        if (favData.getMember_details().get(i).getMember_image() != null && favData.getMember_details().get(i).getMember_image().length() > 0) {
                            holder.tvTxtName3.setVisibility(View.GONE);
                            holder.userImage3.setVisibility(View.VISIBLE);
                            Picasso.with(mContext).load(favData.getMember_details().get(i).getMember_image()).resize(100, 100).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(holder.userImage3);
                        } else if (favData.getMember_details().get(i).getMember_name() != null && favData.getMember_details().get(i).getMember_name().length() > 0) {
                            holder.userImage3.setVisibility(View.GONE);
                            holder.tvTxtName3.setVisibility(View.VISIBLE);
                            if (i % 2 == 0) {
                                holder.tvTxtName3.setBackgroundResource(R.drawable.rounded_colored_even);
                            } else {
                                holder.tvTxtName3.setBackgroundResource(R.drawable.rounded_colored_odd);
                            }

                            String[] txt = favData.getMember_details().get(i).getMember_name().split(" ");
                            switch (txt.length) {
                                case 1:
                                    holder.tvTxtName3.setText(txt[0].charAt(0) + "");
                                    break;
                                case 2:
                                    holder.tvTxtName3.setText(txt[0].charAt(0) + "" + txt[1].charAt(0));
                                    break;
                                default:
                                    holder.tvTxtName3.setText(txt[0].charAt(0) + "" + txt[1].charAt(0));
                                    break;
                            }
                        } else {
                            holder.rel3.setVisibility(View.GONE);
                            holder.rel4.setVisibility(View.GONE);
                        }
                    } else {
                        holder.rel3.setVisibility(View.GONE);
                        holder.rel4.setVisibility(View.GONE);
                    }
//                        break;
//                    case 3:
                } else {
                    if (favData.getMember_details().get(i).getMember_id() != null && favData.getMember_details().get(i).getMember_id().length() > 0) {
                        holder.rel4.setVisibility(View.VISIBLE);
                        if (favData.getMember_details().get(i).getMember_image() != null && favData.getMember_details().get(i).getMember_image().length() > 0) {
                            holder.tvTxtName4.setVisibility(View.GONE);
                            holder.userImage4.setVisibility(View.VISIBLE);
                            Picasso.with(mContext).load(favData.getMember_details().get(i).getMember_image()).resize(100, 100).transform(new RoundedTransformation(0, Color.TRANSPARENT)).centerCrop().into(holder.userImage4);
                        } else if (favData.getMember_details().get(i).getMember_name() != null && favData.getMember_details().get(i).getMember_name().length() > 0) {
                            holder.tvTxtName4.setVisibility(View.VISIBLE);
                            holder.userImage4.setVisibility(View.GONE);
                            if (i % 2 == 0) {
                                holder.tvTxtName4.setBackgroundResource(R.drawable.rounded_colored_even);
                            } else {
                                holder.tvTxtName4.setBackgroundResource(R.drawable.rounded_colored_odd);
                            }

                            String[] txt = favData.getMember_details().get(i).getMember_name().split(" ");
                            switch (txt.length) {
                                case 1:
                                    holder.tvTxtName4.setText(txt[0].charAt(0) + "");
                                    break;
                                case 2:
                                    holder.tvTxtName4.setText(txt[0].charAt(0) + "" + txt[1].charAt(0));
                                    break;
                                default:
                                    holder.tvTxtName4.setText(txt[0].charAt(0) + "" + txt[1].charAt(0));
                                    break;
                            }
                        } else {
                            holder.rel4.setVisibility(View.GONE);
                        }
                    } else {
                        holder.rel4.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        } else {
            holder.rel1.setVisibility(View.GONE);
            holder.rel4.setVisibility(View.GONE);
            holder.rel3.setVisibility(View.GONE);
            holder.rel2.setVisibility(View.GONE);

        }

        if (favData.getAlbum_name() != null && favData.getAlbum_name().length() > 0) {
            holder.txt_albumName.setText(favData.getAlbum_name());
        }

        holder.txt_memberCount.setText("Members: " + favData.getMember_count());

        if (favData.getAlbum_cover_thumb_image() != null && favData.getAlbum_cover_thumb_image().length() > 0) {

            Picasso.with(mContext).load(favData.getAlbum_cover_thumb_image()).into(holder.backImage);
            holder.backImage.setVisibility(View.VISIBLE);
            holder.noImage.setVisibility(View.GONE);
        } else {
            holder.noImage.setVisibility(View.VISIBLE);
            holder.noImage.setImageResource(R.drawable.no_image1);
            holder.backImage.setVisibility(View.GONE);
        }

        holder.txt_photoCount.setText("Photos: " + favData.getPhoto_count());

        holder.rel_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumID, favData.getAlbum_id());
                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumName, favData.getAlbum_name());
                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_Albums, "" + listAllAlbum);
                Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_CREATEDBYME, favData.getCreated_by_me()+"");
                write(Constant.SHRED_PR.KEY_RELOAD_Home, "1");
                write(Constant.SHRED_PR.KEY_RELOAD_Albums, "15");

                com.flashtr.HomeActivity.selectedPosition = 0;
                String albumName = "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumName);
                String albumId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_Current_AlbumID);
                (DemoFragment.getInstance()).pushFragments(Constant.TAB_FRAGMENT_HOME, HomeFragment.newInstance(albumId), false, false, true, true);
                (DemoFragment.getInstance()).setTabColor(3);
                // Todo: set name and album at top on camera
                CaptureActivity.getInstance().setName();
            }
        });

        final ViewHolder finalHolder = holder;
        holder.tv_txtLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.tv_txtLoadMore.setVisibility(View.GONE);
                finalHolder.progressBar2.setVisibility(View.VISIBLE);
                fragment.clickEvent(v);
            }
        });

        holder.iv_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.deleteAlbumPos=position;
                Intent i = new Intent(mContext, ActivityUpdateAlbum.class);
                /*Bundle b= new Bundle();
                b.putSerializable("Data",favData);
                i.putExtra("bundel",b);*/
                Bundle bundle = new Bundle();
                bundle.putSerializable("value", favData);
                i.putExtras(bundle);
                mContext.startActivity(i);
            }
        });

        holder.iv_editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog(v,true,position);
            }
        });

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

            holder.txt_date.setText(/*time + " " + timex*/formateDate);

        }

        if (position == (listAllAlbum.size() - 1) && noMoreData == 2) {
            if (page > 2) {
                holder.progressBar2.setVisibility(View.GONE);
                holder.tv_txtLoadMore.setVisibility(View.VISIBLE);
            } else {
                holder.tv_txtLoadMore.setVisibility(View.GONE);
                holder.progressBar2.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tv_txtLoadMore.setVisibility(View.GONE);
            holder.progressBar2.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView backImage;
        RelativeLayout rel_Image;
        TextView txt_albumName;
        TextView txt_date;
        TextView txt_memberCount;
        TextView txt_photoCount;
        ProgressBar progressBar2;
        TextView tv_txtLoadMore;
        LinearLayout lin_addMemberPhoto;
        RelativeLayout rel1;
        TextView tvTxtName1;
        ImageView userImage1;
        RelativeLayout rel2;
        TextView tvTxtName2;
        ImageView userImage2;
        RelativeLayout rel3;
        TextView tvTxtName3;
        ImageView userImage3;
        RelativeLayout rel4;
        TextView tvTxtName4;
        ImageView userImage4;
        ImageView iv_editPhoto;
        ImageView iv_option;
        TextView txtCount;
        ImageView noImage;
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(mContext, key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(mContext, key);
    }

    public void showImageSelectionDialog(View view1, final boolean isFromEdit, final int pos) {
        final Dialog dialogImageSelection = new Dialog(mContext, R.style.DialogTheme1);
        dialogImageSelection.setCancelable(true);
        dialogImageSelection.setCanceledOnTouchOutside(true);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.diag_image_selector, null);


        TextView mTxtCamera = (TextView) view.findViewById(R.id.tv_txtCamera);
        TextView mTxtGallery = (TextView) view.findViewById(R.id.tv_txtGallery);
        TextView mTxtClose = (TextView) view.findViewById(R.id.tv_txtClose);

        mTxtClose.setVisibility(View.GONE);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");

        if (isFromEdit) {
            mTxtCamera.setText("Delete Album");

            String ids = "";

            for(int i=0; i<listAllAlbum.get(pos).getMember_details().size();i++){
                if(listAllAlbum.get(pos).getMember_details().get(i).getIsadmin()==1)
                    ids = ids+","+listAllAlbum.get(pos).getMember_details().get(i).getMember_id();
            }

            if (Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID).equalsIgnoreCase(listAllAlbum.get(pos).getAdmin_id()) || ids.contains(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID))) {
                mTxtGallery.setText("Update Album");
                mTxtGallery.setVisibility(View.VISIBLE);
            } else {
                mTxtGallery.setVisibility(View.GONE);
            }
        } else {
            mTxtCamera.setText("Remove From Album");
            mTxtGallery.setText("Make Admin");
        }
        mTxtCamera.setTypeface(font);
        mTxtGallery.setTypeface(font);
        mTxtClose.setTypeface(font);

//            LinearLayout mLinearClose = (LinearLayout) view.findViewById(R.id.layClose);

        mTxtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImageSelection.dismiss();
                if (isFromEdit) {
                    AlertDialog.Builder alert1 = new AlertDialog.Builder(mContext);
                    alert1.setTitle("" + Constant.AppName);
                    alert1.setMessage("Delete this album?");
                    alert1.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @SuppressLint("InlinedApi")
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (Util.isOnline(mContext)) {
                                        new deleteAlbum(pos,listAllAlbum.get(pos).getAlbum_id()).execute();
                                    } else {
                                        Toast.makeText(mContext, Constant.network_error, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                    );
                    alert1.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    dialog.cancel();
                                }
                            }

                    );
                    alert1.create();
                    alert1.show();
                } else {
                    new MakeDeleteAdmin(false, pos).execute();
                }
            }
        });

        mTxtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImageSelection.dismiss();
                if (isFromEdit) {
                    Intent i = new Intent(mContext, ActivityEditAlbum .class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("value", listAllAlbum.get(pos));
                    i.putExtras(bundle);
                    mContext.startActivity(i);
                } else {
                    new MakeDeleteAdmin(true, pos).execute();
                }
            }
        });

//            mLinearClose.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialogImageSelection.dismiss();
//                }
//            });

        dialogImageSelection.setContentView(view);
        dialogImageSelection.show();
    }

    class MakeDeleteAdmin extends AsyncTask<Void, String, String> {

        boolean isMake;
        String response;
        int position;

        public MakeDeleteAdmin(boolean isMake,int position) {
            this.isMake = isMake;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(mContext, "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                if(isMake) {
                    jData.put("method", "makeadmin");
                    jData.put("album_id", "" + listAllAlbum.get(position).getAlbum_id());
                    jData.put("user_id", "" + Util.ReadSharePrefrence(mContext.getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
                    jData.put("new_user_id", "" + listAllAlbum.get(position).getMember_details().get(position).getMember_id());
                }else{
                    jData.put("method", "removeshareperson");
                    jData.put("album_id", "" + listAllAlbum.get(position).getAlbum_id());
                    jData.put("shareperson", "" + listAllAlbum.get(position).getMember_details().get(position).getMember_id());
                }
                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("data", jData.toString()));
                response = Util.makeServiceCall(Constant.URL, 1, params1);
                Log.e("params1", ">>" + params1);

                Log.e("** response is:- ", ">>" + response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            CustomDialog.getInstance().hide();
            try {
                JSONObject jObj = new JSONObject(result);
//                toast("" + jObj.getString("msg"));
                Toast.makeText(mContext, "" + jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                Intent intent;
                int status = jObj.optInt("success");
                if (status == 1) {

                    if(isMake){
                        listAllAlbum.get(position).getMember_details().get(position).setIsadmin(1);
                    }else{
                        listAllAlbum.get(position).getMember_details().remove(position);
                    }
                    /*adpMemberDetail.notifyDataSetChanged();*/
                }else{

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class deleteAlbum extends AsyncTask<Void, String, String> {

        String albumId;
        String response;
        int pos;

        private deleteAlbum(int pos,String albumId) {
            this.albumId = albumId;
            this.pos = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(mContext,"");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "deletealbum");
                jData.put("userid", "" + Util.ReadSharePrefrence(mContext.getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
                jData.put("album_id", "" + albumId);


                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("data", jData.toString()));
                response = Util.makeServiceCall(Constant.URL, 1, params1);
                Log.e("params1", ">>" + params1);

                Log.e("** response is:- ", ">>" + response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            CustomDialog.getInstance().hide();
            try {
                JSONObject jObj = new JSONObject(result);
                int status = jObj.optInt("success");

                if (status == 1) {
                    listAllAlbum.remove(pos);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
