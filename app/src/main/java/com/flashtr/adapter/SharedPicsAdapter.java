package com.flashtr.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.ImagePagerActivity;
import com.flashtr.R;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by BHAVIK ZAVERI on 20-03-2016
 * No Use of this adapter
 * as this is used in simple list view now old listview replace with recyclerview so use another adapter AdpRecentPic for that
 */

public class SharedPicsAdapter extends BaseAdapter {

    private final Fragment fragment;
    ArrayList<HashMap<String, String>> locallist;
    DisplayImageOptions options, options1;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private Activity mContext;
    private LayoutInflater inflater = null;
    Typeface font;

    public SharedPicsAdapter(Fragment fragment, ArrayList<HashMap<String, String>> locallist) {

        this.mContext = fragment.getActivity();
        this.fragment = fragment;
        this.locallist = locallist;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
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

    public void delete(int position) {
        locallist.remove(position);
        notifyDataSetChanged();
    }

    @Override
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

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listraw_syncedpics, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final HashMap<String, String> hashMap = locallist.get(position);

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
        imageLoader.displayImage("" + hashMap.get("user_image"), holder.image, options1);
        imageLoader.displayImage("" + hashMap.get("photo"), holder.img, options);

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
                    ((HomeFragment) fragment).downloadImage(image,false);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImagePagerActivity.class);
                intent.putExtra("from", Constant.SharedPics);
                intent.putExtra("pos", position);
                intent.putExtra("list", locallist);
                mContext.startActivity(intent);

            }
        });

        return view;
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





    static class ViewHolder {
        @InjectView(R.id.tvTime)
        TextView tvTime;
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.image)
        ImageView image;
        @InjectView(R.id.img)
        ImageView img;
        @InjectView(R.id.rlDelete)
        RelativeLayout rlDelete;
        @InjectView(R.id.rlShare)
        RelativeLayout rlShare;
        @InjectView(R.id.rlDownload)
        RelativeLayout rlDownload;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    class deletePhoto extends AsyncTask<Void, String, String> {

        String photoid, album_id;
        int pos;
        ProgressDialog progressDialog;
        String response;

        private deletePhoto(String album_id, String photoid, int pos) {
            this.album_id = album_id;
            this.photoid = photoid;
            this.pos = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext, R.style.MyTheme);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.show();

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

            if (progressDialog != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

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

        ProgressDialog progressDialog;
        String img;

        public Share(String img) {
            this.img = img;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext, R.style.MyTheme);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.show();

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
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("image/*");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Constant.AppName);
            emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, response);
            mContext.startActivity(Intent.createChooser(emailIntent, "Share"));
        }

    }


}
