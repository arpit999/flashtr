package com.flashtr;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.adapter.ImagePagerAdapter;
import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class ImagePagerActivity extends BaseActivity {

    @InjectView(R.id.rlMain)
    RelativeLayout rlMain;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.rlDelete)
    RelativeLayout rlDelete;
    @InjectView(R.id.rlShare)
    RelativeLayout rlShare;
    @InjectView(R.id.rlSave)
    RelativeLayout rlSave;
    @InjectView(R.id.rlMenu)
    RelativeLayout rlMenu;
    @InjectView(R.id.rlOptionMenu)
    RelativeLayout rlOptionMenu;
    @InjectView(R.id.tvDelete)
    TextView tvDelete;
    @InjectView(R.id.tvShare)
    TextView tvShare;
    @InjectView(R.id.flTopLayer)
    FrameLayout flTopLayer;
    @InjectView(R.id.progressBar10)
    ProgressBar prog10;

    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    int FROM = Constant.SharedPics;
    int pagerposition = 0;
    ArrayList<HashMap<String, String>> listPhotos = new ArrayList<>();
    private DownloadManager downloadManager;
    private long enqueue;
    private String imageName;
    Animation animShow,animHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_image_pager);
        rlMain.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        init();
        animShow = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);
        //Load animation
        animHide = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FROM = extras.getInt("from");

            pagerposition = extras.getInt("pos");
            listPhotos = (ArrayList<HashMap<String, String>>) extras.getSerializable("list");
            pager.setAdapter(new ImagePagerAdapter(this, listPhotos, FROM));
            pager.setCurrentItem(pagerposition);

            setData();
        }

            prog10.setVisibility(View.VISIBLE);
        prog10.setIndeterminate(true);
        prog10.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        File file= new File(android.os.Environment.getExternalStorageDirectory()+ "/temp/"+imageName);
        if(file.exists())
        {
            file.delete();
        }
            finish();
//        startActivity(HomeActivity.class);
    }

    @OnClick(R.id.flTopLayer)
    public void layerClick(View view){
        rlOptionMenu.startAnimation(animHide);
        rlOptionMenu.setVisibility(View.GONE);
        flTopLayer.setVisibility(View.GONE);
    }




    @OnClick(R.id.rlMenu)
    @SuppressWarnings("unused")
    public void Menu(View view) {

        if (rlOptionMenu.getVisibility() == View.VISIBLE) {
            rlOptionMenu.startAnimation(animHide);
            rlOptionMenu.setVisibility(View.GONE);
            flTopLayer.setVisibility(View.GONE);
        } else {
            flTopLayer.setVisibility(View.VISIBLE);
            rlOptionMenu.setVisibility(View.VISIBLE);
            rlOptionMenu.startAnimation(animShow);

        }
    }



    @OnClick(R.id.rlDelete)
    @SuppressWarnings("unused")
    public void Delete(View view) {
        // animation on click
        rlOptionMenu.startAnimation(animHide);
        flTopLayer.setVisibility(View.GONE);
        rlOptionMenu.setVisibility(View.GONE);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Delete this photo?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (FROM == Constant.SharedPics) {
                    if (Util.isOnline(getApplicationContext())) {
                        new deletePhoto(listPhotos.get(pagerposition).get("album_id"), listPhotos.get(pagerposition).get("photo_id"), pagerposition).execute();
                    } else
                        Toast.makeText(getApplicationContext(), Constant.network_error, Toast.LENGTH_SHORT).show();
                } else {
                    File file = new File(listPhotos.get(pagerposition).get("photo"));
                    File file1 = new File(listPhotos.get(pagerposition).get("photo1"));
                    if (file.exists()) file.delete();
                    if (file1.exists()) file1.delete();

                    write(Constant.SHRED_PR.KEY_RELOAD_Home, "2");
                    listPhotos.remove(pagerposition);

                    if (pagerposition > (listPhotos.size() - 1)) pagerposition--;
                    if (pagerposition >= 0) {
                        pager.setAdapter(new ImagePagerAdapter(getApplicationContext(), listPhotos, FROM));
                        pager.setCurrentItem(pagerposition);
                        setData();
                    } else finish();

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

    @OnClick(R.id.rlShare)
    @SuppressWarnings("unused")
    public void Share(View view) {
        Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_SELECTED_TAB, "0");
        rlOptionMenu.startAnimation(animHide);
        flTopLayer.setVisibility(View.GONE);
        rlOptionMenu.setVisibility(View.GONE);

        if (FROM == Constant.RecentPics) {
            if (Util.isOnline(getApplicationContext())) {
                String userId = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID);
                new addPhoto(userId, pagerposition).execute();
            } else
                Toast.makeText(getApplicationContext(), Constant.network_error, Toast.LENGTH_SHORT).show();
        } else {
            String image = "" + listPhotos.get(pagerposition).get("photo");
            if (image.length() > 0) {
                new Share(image).execute();
            }
        }

    }

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Save(View view) {

        flTopLayer.setVisibility(View.GONE);
        rlOptionMenu.setVisibility(View.GONE);

        final String image = "" + listPhotos.get(pagerposition).get("photo");
        if (image.length() > 0) {

            downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

            final Uri downloadUri = Uri.parse(image);
            final DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            final String[] title = image.split("/");
            final String imageName = title[title.length - 1];
            final File root = new File(Environment.getExternalStorageDirectory()
                    + "/Pictures/" + getString(R.string.app_name));
            if (!root.exists()) root.mkdir();

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(true).setTitle(getString(R.string.app_name)).setDescription(imageName)
                    .setDestinationInExternalPublicDir("/Pictures/" + getString(R.string.app_name), imageName);
            enqueue = downloadManager.enqueue(request);

//            new Save(image).execute();
        }

    }

    private BroadcastReceiver downloadImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                final DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(enqueue);
                final Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)) {
                        Toast.makeText(getApplicationContext(), "Photo saved", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private void init() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));


        rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlOptionMenu.setVisibility(View.GONE);
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {
                // TODO Auto-generated method stub
                pagerposition = pos;
                setData();

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        registerReceiver(downloadImageReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadImageReceiver);
    }

    private void setData() {

        rlOptionMenu.setVisibility(View.GONE);
        tvTitle.setText((pagerposition + 1) + " of " + listPhotos.size());

        if (FROM == Constant.SharedPics) {

            rlSave.setVisibility(View.VISIBLE);
            tvShare.setText("Share");
            tvDelete.setText("Delete");

            String loggedUser = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID);
            String user_id = "" + listPhotos.get(pagerposition).get("user_id");
            if (loggedUser.equals(user_id)) {
                rlDelete.setVisibility(View.VISIBLE);
            } else {
                rlDelete.setVisibility(View.GONE);
            }
        } else {

            tvShare.setText("Upload to Album");
            tvDelete.setText("Delete");

            rlShare.setVisibility(View.VISIBLE);
            rlDelete.setVisibility(View.VISIBLE);
            rlSave.setVisibility(View.GONE);
        }

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
            CustomDialog.getInstance().show(ImagePagerActivity.this,"");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "deletephoto");
                jData.put("userid", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
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
                    write(Constant.SHRED_PR.KEY_RELOAD_Home, "2");
                    listPhotos.remove(pagerposition);

                    if (pagerposition > (listPhotos.size() - 1)) pagerposition--;
                    if (pagerposition >= 0) {
                        pager.setAdapter(new ImagePagerAdapter(getApplicationContext(), listPhotos, FROM));
                        pager.setCurrentItem(pagerposition);
                        setData();
                    } else finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class addPhoto extends AsyncTask<Void, String, String> {

        String albumId, userId, photo;
        int pos;

        private addPhoto(String userId, int pos) {
            albumId = listPhotos.get(pos).get("album_id");
            this.userId = userId;
            photo = listPhotos.get(pos).get("photo");

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            CustomDialog.getInstance().show(ImagePagerActivity.this,"");
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
            CustomDialog.getInstance().hide();
            try {
                JSONObject jObj = new JSONObject(result);

                int status = jObj.optInt("success");
                if (status == 1) {

                    File file = new File(listPhotos.get(pagerposition).get("photo"));
                    if (file.exists()) file.delete();

                    write(Constant.SHRED_PR.KEY_RELOAD_Home, "2");
                    listPhotos.remove(pagerposition);

                    if (pagerposition > (listPhotos.size() - 1)) pagerposition--;
                    if (pagerposition >= 0) {
                        pager.setAdapter(new ImagePagerAdapter(getApplicationContext(), listPhotos, FROM));
                        pager.setCurrentItem(pagerposition);
                        setData();
                    } else finish();

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
            CustomDialog.getInstance().show(ImagePagerActivity.this,"");
        }

        @Override
        protected Uri doInBackground(String... params) {
            Log.d("Tests", "Testing graph API wall post");
            String response = null;
            Uri uri = null;
            try {
                Bitmap bmp = Util.getBitmapFromURL("" + img);
                uri = Util.getImageUri(getApplicationContext(), bmp);

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
            startActivity(Intent.createChooser(emailIntent, "Share"));
        }
    }

    class Save extends AsyncTask<Void, Void, Void> {

        String img;

        public Save(String img) {
            this.img = img;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(ImagePagerActivity.this,"");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Tests", "Testing graph API wall post");
            Bitmap bmp = null;
            try {
                bmp = Util.getBitmapFromURL("" + img);
                if (bmp != null) {
                    String[] title = img.split("/");
                    File root = new File(Environment.getExternalStorageDirectory()
                            + "/Pictures/" + getString(R.string.app_name));
                    if (!root.exists()) root.mkdir();

                    File file = new File(root, title[title.length - 1]);
                    FileOutputStream ostream = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.close();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void bitmap) {
            super.onPostExecute(bitmap);
            CustomDialog.getInstance().hide();
            Toast.makeText(getApplicationContext(), "Photo saved", Toast.LENGTH_SHORT).show();

        }

    }
}
