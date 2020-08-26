package com.flashtr.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.ImagePagerActivity;
import com.flashtr.R;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hirenkanani on 20/03/16.
 */

public class AdpSyncedPhoto extends RecyclerView.Adapter<AdpSyncedPhoto.ViewHolder> {
    Fragment fragment;
    Context mContext;
    private ArrayList<HashMap<String, String>> localList;
    DisplayImageOptions options, options1;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private Typeface font;

    public AdpSyncedPhoto(Fragment fragment, ArrayList<HashMap<String, String>> localList) {
        this.fragment = fragment;
        this.localList = localList;
        mContext = fragment.getActivity();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        options1 = new DisplayImageOptions.Builder()
//                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
    }

    public void setListItem(ArrayList<HashMap<String, String>> localList) {
        this.localList = localList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listraw_syncedpics, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        final HashMap<String, String> hashMap = localList.get(position);

        String loggedUser = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID);
        String user_id = "" + hashMap.get("user_id");

        if (loggedUser.equals(user_id)) {
            holder.rlDelete.setVisibility(View.VISIBLE);
        } else {
            holder.rlDelete.setVisibility(View.GONE);
        }
        font = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");
        holder.tvName.setTypeface(font);
        holder.tvName.setText("" + hashMap.get("user_name"));
        String date = Util.utcToLocalTime("" + hashMap.get("date"));
        List<String> elephantList = Arrays.asList(date.split("-"));
        int month = Integer.parseInt(elephantList.get(1));

        List<String> data = Arrays.asList(elephantList.get(2).split(" "));
        int day = Integer.parseInt(data.get(0));
        String time = data.get(1);
        String timex = data.get(2);
        holder.tvTime.setText(time + " " + timex);
        holder.tvTime.setTypeface(font);
        holder.tvDate.setText("" + getMonthSuffix(month) + " " + day + ", " + elephantList.get(0));
        holder.tvDate.setTypeface(font);

//        holder.tvDate.setText(Util.utcToLocalTime("" + hashMap.get("date")));
        /*imageLoader.displayImage("" + hashMap.get("user_image"), holder.image, options1);*/
        /*imageLoader.displayImage("" + hashMap.get("photo"), holder.img, options);*/
        if(hashMap.get("thumbImage")!=null && hashMap.get("thumbImage").length()>0){
            Picasso.with(mContext).load("" + hashMap.get("thumbImage")).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(holder.img);
        }else{
            Picasso.with(mContext).load(R.drawable.default_img).placeholder(R.drawable.default_img).error(R.drawable.default_img).into(holder.img);
        }

        holder.rlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Are you sure to delete this photo?");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Util.isOnline(mContext)) {
                            new deletePhoto(hashMap.get("album_id"), hashMap.get("photo_id"), position).execute();
                        } else
                            Toast.makeText(mContext, Constant.network_error, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();


            }
        });

        holder.rlShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String image = "" + hashMap.get("photo");
                if (!TextUtils.isEmpty(image)) {
                    new Share(image).execute();
                }
            }
        });
        holder.rlDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String image = "" + hashMap.get("photo");
                if (!TextUtils.isEmpty(image)) {
                    ((HomeFragment) fragment).downloadImage(image, false);
                }
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImagePagerActivity.class);
                intent.putExtra("from", Constant.SharedPics);
                intent.putExtra("pos", position);
                intent.putExtra("list", localList);
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return localList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvDate;
        TextView tvName;
        ImageView image;
        ImageView img;
        RelativeLayout rlDelete;
        RelativeLayout rlShare;
        RelativeLayout rlDownload;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
            tvDate = (TextView)itemView.findViewById(R.id.tvDate);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            image = (ImageView)itemView.findViewById(R.id.image);
            img = (ImageView)itemView.findViewById(R.id.img);
            rlDelete = (RelativeLayout)itemView.findViewById(R.id.rlDelete);
            rlShare = (RelativeLayout)itemView.findViewById(R.id.rlShare);
            rlDownload = (RelativeLayout)itemView.findViewById(R.id.rlDownload);

        }
    }
    public String getMonthSuffix(int n) {
        String month = "";
        switch (n) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
        }
        return month;
    }

    class deletePhoto extends AsyncTask<Void, String, String> {

        String photoid, album_id;
        int pos;
        String response;

        private deletePhoto(String album_id, String photoid, int pos) {
            this.album_id = album_id;
            this.photoid = photoid;
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
                jData.put("method", "deletephoto");
                jData.put("userid", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID));
                jData.put("album_id", "" + album_id);
                jData.put("photoid", "" + photoid);


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
                    delete(pos);
                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_GET_PHOTOS, "");
                    Intent intent1 = new Intent();
                    intent1.setAction(mContext.ACTIVITY_SERVICE);
                    intent1.putExtra("type", 0);
                    mContext.sendBroadcast(intent1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    class Share extends AsyncTask<String, String, Uri> {

        String img;

        public Share(String img) {
            this.img = img;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(mContext,"");
        }

        @Override
        protected Uri doInBackground(String... params) {
            Log.d("Tests", "Testing graph API wall post");
            String response = null;
            Uri uri = null;
            try {
                Bitmap bmp = Util.getBitmapFromURL("" + img);
                uri = Util.getImageUri(mContext, bmp);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return uri;
        }

        @Override
        protected void onPostExecute(Uri response) {
            super.onPostExecute(response);
            CustomDialog.getInstance().hide();
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("image/*");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Constant.AppName);
            emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, response);
            mContext.startActivity(Intent.createChooser(emailIntent, "Share"));
        }

    }
    public void delete(int position) {
        localList.remove(position);
        notifyDataSetChanged();
    }
}
