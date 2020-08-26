package com.flashtr.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.ImagePagerActivity;
import com.flashtr.R;
import com.flashtr.activity.CaptureActivity;
import com.flashtr.app.MyApp;
import com.flashtr.asyncTask.uploadPhotoTask;
import com.flashtr.fragment.CameraFragment;
import com.flashtr.fragment.HomeFragment;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hirenkanani on 20/03/16.
 */

public class AdpRecentPic extends RecyclerView.Adapter<AdpRecentPic.ViewHolder> {
    private final Fragment fragment;
    ArrayList<HashMap<String, String>> localList;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private Context mContext;
    private LayoutInflater inflater = null;
    private TextView tvCount;
    private HomeFragment frag;

    public AdpRecentPic(Fragment fragment, ArrayList<HashMap<String, String>> localList, TextView tvCount,HomeFragment frag) {
        this.fragment = fragment;
        this.localList = localList;
        mContext = fragment.getActivity();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        this.tvCount = tvCount;
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
        this.frag = frag;
    }

    public void setListItem(ArrayList<HashMap<String, String>> localList) {
        this.localList = localList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listraw_recentpics, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final HashMap<String, String> hashMap = localList.get(position);

        holder.rlImage.setVisibility(View.GONE);
        holder.tvDate.setText(Util.utcToLocalTime("" + hashMap.get("date")));
        /*imageLoader.displayImage(Uri.fromFile(new File("" + hashMap.get("photo"))).toString(), holder.img, options);*/
        Picasso.with(mContext).load(Uri.fromFile(new File("" + hashMap.get("photo"))).toString()).resize(400, 400).centerCrop().into(holder.img);

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
                builder.setMessage("Delete this photo?");
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
                    String albumId = localList.get(position).get("album_id");
                    String photo = localList.get(position).get("photo");

                    delete(position);

                    /*Intent intent = new Intent(mContext, UploadPhotoIntentService.class);
                    intent.putExtra(Constant.SHRED_PR.KEY_USERID, userId);
                    intent.putExtra(Constant.SHRED_PR.KEY_Current_AlbumID, albumId);
                    intent.putExtra(Constant.SHRED_PR.KEY_Photo_Name, photo);
                    intent.putExtra(Constant.SHRED_PR.KEY_Data, CameraFragment.class.getSimpleName());
                    mContext.startService(intent);*/
                    File file = new File(hashMap.get("photo"));
                    File file1 = new File(hashMap.get("photo1"));
                    uploadPhotoTask task = new uploadPhotoTask(mContext, userId, albumId, photo, CaptureActivity.class.getSimpleName(), file1.getAbsolutePath(), file.getAbsolutePath());
                    task.execute();

                    MyApp.uploadingImage.add(photo);
                    int count = Util.getOfflinePic(mContext);
                    if (count > 0) {
                        tvCount.setText(Util.getOfflinePic(mContext) + " " + mContext.getResources().getString(R.string.photos_awaiting));
                    } else {
                        tvCount.setVisibility(View.GONE);
                    }

                } else
                    Toast.makeText(mContext, Constant.network_error, Toast.LENGTH_SHORT).show();
            }
        });

        holder.rlEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   /* 2) Create a new Intent */
                /*Intent imageEditorIntent = new AdobeImageIntent.Builder(mContext)
                        .setData(Uri.parse("file://"+hashMap.get("photo")+""))
                        .build();
                         *//* 3) Start the Image Editor with request code 1 *//*
                frag.startActivityForResult(imageEditorIntent, frag.EDIT_IMAGE_WITH_AVIERY);
*/
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImagePagerActivity.class);
                intent.putExtra("from", Constant.RecentPics);
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
        TextView tvName;
        TextView tvTime;
        TextView tvDate;
        ImageView img;
        RelativeLayout rlDelete;
        RelativeLayout rlEdit;
        RelativeLayout rlShare;
        RelativeLayout rlImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            rlImage = (RelativeLayout) itemView.findViewById(R.id.image_ll);
            img = (ImageView) itemView.findViewById(R.id.img);
            rlDelete = (RelativeLayout) itemView.findViewById(R.id.rlDelete);
            rlShare = (RelativeLayout) itemView.findViewById(R.id.rlShare);
            rlEdit = (RelativeLayout) itemView.findViewById(R.id.rlEdit);

        }
    }

    public void delete(int position) {
        localList.remove(position);
        notifyDataSetChanged();
    }
}
