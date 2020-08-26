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
import com.flashtr.UploadPhotoIntentService;
import com.flashtr.activity.CaptureActivity;
import com.flashtr.fragment.CameraFragment;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by MAHADEV on 10-10-2015.
 * No Use of this adapter
 * as this is used in simple list view now old listview replace with recyclerview so use another adapter AdpRecentPic for that
 */
public class RecentPicsAdapter extends BaseAdapter {

    private final Fragment fragment;
    ArrayList<HashMap<String, String>> locallist;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private Context mContext;
    private LayoutInflater inflater = null;

    public RecentPicsAdapter(Fragment fragment, ArrayList<HashMap<String, String>> locallist) {

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
            view = inflater.inflate(R.layout.listraw_recentpics, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final HashMap<String, String> hashMap = locallist.get(position);

        holder.rlImage.setVisibility(View.GONE);
        holder.tvDate.setText(Util.utcToLocalTime("" + hashMap.get("date")));
        imageLoader.displayImage(Uri.fromFile(new File("" + hashMap.get("photo"))).toString(), holder.img, options);

//        ViewGroup root = (ViewGroup) view.findViewById(R.id.rlDateInner);
//        Util.setFont(root, Util.getFont(1, mContext.getApplicationContext()));
        Typeface type = Typeface.createFromAsset(mContext.getAssets(), "SEGOEUI.TTF");
        holder.tvName.setTypeface(type);
        holder.tvTime.setTypeface(type);
        holder.tvDate.setTypeface(type);

//        holder.rlDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String image = "" + hashMap.get("photo");
//                if (!TextUtils.isEmpty(image)) {
//                    ((HomeFragment) fragment).downloadImage(image, true);
//                }
//            }
//        });

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
                            delete(position);
                            File file = new File(hashMap.get("photo"));
                            File file1 = new File(hashMap.get("photo1"));
                            if (file.exists()) file.delete();
                            if (file1.exists()) file1.delete();
                            int photoCnt = Integer.parseInt(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_GET_PHOTO_COUNT));
                            photoCnt--;
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_GET_PHOTO_COUNT, "" + photoCnt);
                            Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_RELOAD_Home, "5");
                            Intent intent1 = new Intent();
                            intent1.setAction(mContext.ACTIVITY_SERVICE);
                            intent1.putExtra("type", 1);
                            mContext.sendBroadcast(intent1);
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
                if (Util.isOnline(mContext)) {

                    /*Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_SELECTED_TAB, "0");
                    String userId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID);
                    String albumId = locallist.get(position).get("album_id");
                    String photo = locallist.get(position).get("photo");
                    String photo1 = locallist.get(position).get("photo1");
                    new addPhoto(userId, albumId, photo1).execute();

                    delete(position);
                    File file = new File(photo);
                    if (file.exists()) file.delete();
                    Util.WriteSharePrefrence(mContext, Constant.SHRED_PR.KEY_RELOAD_Home, "5");
                    Intent intent1 = new Intent();
                    intent1.setAction(mContext.ACTIVITY_SERVICE);
                    intent1.putExtra("type", 1);
                    mContext.sendBroadcast(intent1);*/

                    String userId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID);
                    String albumId = locallist.get(position).get("album_id");
                    String photo = locallist.get(position).get("photo");

                    delete(position);

                    Intent intent = new Intent(mContext, UploadPhotoIntentService.class);
                    intent.putExtra(Constant.SHRED_PR.KEY_USERID, userId);
                    intent.putExtra(Constant.SHRED_PR.KEY_Current_AlbumID, albumId);
                    intent.putExtra(Constant.SHRED_PR.KEY_Photo_Name, photo);
                    intent.putExtra(Constant.SHRED_PR.KEY_Data, CaptureActivity.class.getSimpleName());
                    mContext.startService(intent);


                } else
                    Toast.makeText(mContext, Constant.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImagePagerActivity.class);
                intent.putExtra("from", Constant.RecentPics);
                intent.putExtra("pos", position);
                intent.putExtra("list", locallist);
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.tvTime)
        TextView tvTime;
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.img)
        ImageView img;
        @InjectView(R.id.rlDelete)
        RelativeLayout rlDelete;
        @InjectView(R.id.rlShare)
        RelativeLayout rlShare;
        @InjectView(R.id.image_ll)
        RelativeLayout rlImage;
//        @InjectView(R.id.rlDownload)
//        RelativeLayout rlDownload;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    class addPhoto extends AsyncTask<Void, String, String> {

        String albumId, userId, photo;

        private addPhoto(String userId, String albumId, String photo) {
            this.userId = userId;
            this.albumId = albumId;
            this.photo = photo;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String resp = "";
            try {

                JSONObject jData = new JSONObject();
                jData.put("method", "addphoto");
                jData.put("userid", "" + userId);
                jData.put("album_id", "" + albumId);

                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = new HttpPost(
                        Constant.URL);

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                Log.e("camera_pathname", ">>" + photo);
                if (photo.length() > 0) {
                    File image = new File(photo);
                    fbody = new FileBody(image);
                    entity.addPart("file", fbody);
                }


                entity.addPart("data", new StringBody(jData.toString()));
                poster.setEntity(entity);

                response = client.execute(poster);
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity()
                                .getContent()));
                String line = null;
                while ((line = rd.readLine()) != null) {
                    resp += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Resp Upload", "" + resp);
            return resp;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            Log.e("result", ">>" + result);

            try {
                JSONObject jObj = new JSONObject(result);

                int status = jObj.optInt("success");
                if (status == 1) {

                    //delete(pos);
                    File file = new File(photo);
                    if (file.exists()) file.delete();

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

}
