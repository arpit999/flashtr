package com.flashtr.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flashtr.BaseActivity;
import com.flashtr.R;
import com.flashtr.adapter.AdpAlbumDetail;
import com.flashtr.app.MyApp;
import com.flashtr.model.AllAlbum;
import com.flashtr.model.MemberDetail;
import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24B on 5/12/2016.
 */
public class ActivityUpdateAlbum extends BaseActivity {

    LinearLayout rel_header1, rel_header2;
    TextView tv_txtHeader, noDataFound;
    ImageView albumPhoto, ivBack, ivLogout, noImage;
    ListView memberList;
    Bundle bundel;
    AllAlbum Data;
    LinearLayout main;
    AdpAlbumDetail adpMemberDetail;
    Dialog dialogImageSelection;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.act_update_album);


        rel_header1 = (LinearLayout) findViewById(R.id.rel_header1);
        tv_txtHeader = (TextView) findViewById(R.id.tv_txtHeader);
        rel_header2 = (LinearLayout) findViewById(R.id.rel_header2);
        albumPhoto = (ImageView) findViewById(R.id.albumPhoto);
        noImage = (ImageView) findViewById(R.id.noImage);
        memberList = (ListView) findViewById(R.id.memberList);
        noDataFound = (TextView) findViewById(R.id.noDataFound);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivLogout = (ImageView) findViewById(R.id.ivLogout);
        main = (LinearLayout) findViewById(R.id.main);
        main.setPadding(0, Util.getStatusBarHeight(this), 0, 0);

        Typeface type = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
        Typeface typeBD = Typeface.createFromAsset(getAssets(), "SEGOEUIB.TTF");
        tv_txtHeader.setTypeface(type);
        noDataFound.setTypeface(type);

        bundel = this.getIntent().getExtras();
//        bundel= getIntent().getBundleExtra("bundel");
        if (bundel != null) {
            Data = (AllAlbum) bundel.getSerializable("value");

            if (Data.getMember_details() != null && Data.getMember_details().size() > 0) {
                List<MemberDetail> listAllAlbum = Data.getMember_details();
                /*MemberDetail mm = new MemberDetail();
                mm.setIsadmin(1);
                mm.setMember_id(Data.getAdmin_id());
                mm.setMember_name(Data.getAdmin_name());
                mm.setMember_image(Data.getAdmin_dp());
                mm.setMember_number(Data.getAdmin_mobileno());*/

                /*listAllAlbum.add(mm);*/
                adpMemberDetail = new AdpAlbumDetail(ActivityUpdateAlbum.this, listAllAlbum);
                memberList.setAdapter(adpMemberDetail);

                memberList.setVisibility(View.VISIBLE);
                noDataFound.setVisibility(View.GONE);
            } else {
                memberList.setVisibility(View.GONE);
                noDataFound.setVisibility(View.VISIBLE);
            }

            tv_txtHeader.setText(Data.getAlbum_name());

            if (Data.getAlbum_cover_image() != null && Data.getAlbum_cover_thumb_image().length() > 0) {
                Picasso.with(this).load(Data.getAlbum_cover_image()).into(albumPhoto);
                noImage.setVisibility(View.GONE);
            } else {
                noImage.setVisibility(View.VISIBLE);
                noImage.setImageResource(R.drawable.no_image1);
                albumPhoto.setImageResource(0);
            }

        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog(v, true, 0, false);

            }
        });


        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Data.getAdmin_id().equalsIgnoreCase(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID))) {
                    if (Data.getMember_details().get(position).getIsadmin() != 1) {
                        showImageSelectionDialog(view, false, position, false);
                    } else {
                        showImageSelectionDialog(view, false, position, true);
                    }
                } else {
                    for (int i = 0; i < Data.getMember_details().size(); i++) {
                        if (Data.getMember_details().get(i).getMember_id().equalsIgnoreCase(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)) && Data.getMember_details().get(i).getIsadmin() == 1) {
                            if (Data.getMember_details().get(position).getIsadmin() == 1) {
                                showImageSelectionDialog(view, false, position, true);
                            } else {
                                showImageSelectionDialog(view, false, position, false);
                            }
                        }
                    }
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void showImageSelectionDialog(View view1, final boolean isFromEdit, final int pos, final boolean isMakeAdminGone) {
        if (dialogImageSelection != null && dialogImageSelection.isShowing()) {

        } else {
            dialogImageSelection = new Dialog(this, R.style.DialogTheme1);
            dialogImageSelection.setCancelable(true);
            dialogImageSelection.setCanceledOnTouchOutside(true);
            final View view = LayoutInflater.from(this).inflate(R.layout.diag_image_selector, null);


            TextView mTxtCamera = (TextView) view.findViewById(R.id.tv_txtCamera);
            TextView mTxtGallery = (TextView) view.findViewById(R.id.tv_txtGallery);
            TextView mTxtClose = (TextView) view.findViewById(R.id.tv_txtClose);

            mTxtClose.setVisibility(View.GONE);

            Typeface font = Typeface.createFromAsset(this.getAssets(), "SEGOEUI.TTF");

            if (isFromEdit) {
                mTxtCamera.setText("Delete Album");

                String ids = "";

                for (int i = 0; i < Data.getMember_details().size(); i++) {
                    if (Data.getMember_details().get(i).getIsadmin() == 1)
                        ids = ids + "," + Data.getMember_details().get(i).getMember_id();
                }

                if (Util.ReadSharePrefrence(ActivityUpdateAlbum.this, Constant.SHRED_PR.KEY_USERID).equalsIgnoreCase(Data.getAdmin_id()) || ids.contains(Util.ReadSharePrefrence(ActivityUpdateAlbum.this, Constant.SHRED_PR.KEY_USERID))) {
                    mTxtGallery.setText("Update Album");
                    mTxtGallery.setVisibility(View.VISIBLE);
                } else {
                    mTxtGallery.setVisibility(View.GONE);
                }
            } else {
                mTxtCamera.setText("Remove From Album");
                if (isMakeAdminGone) {
                    mTxtGallery.setVisibility(View.GONE);
                } else {
                    mTxtGallery.setVisibility(View.VISIBLE);
                    mTxtGallery.setText("Make Admin");
                }
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
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(ActivityUpdateAlbum.this);
                        alert1.setTitle("" + Constant.AppName);
                        alert1.setMessage("Delete this album?");
                        alert1.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @SuppressLint("InlinedApi")
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (Util.isOnline(getApplicationContext())) {
                                            new deleteAlbum(Data.getAlbum_id()).execute();
                                        } else {
                                            toast(Constant.network_error);
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
                        Intent i = new Intent(ActivityUpdateAlbum.this, ActivityEditAlbum.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("value", Data);
                        i.putExtras(bundle);
                        startActivity(i);
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
    }

    class deleteAlbum extends AsyncTask<Void, String, String> {

        String albumId;
        String response;

        private deleteAlbum(String albumId) {
            this.albumId = albumId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(ActivityUpdateAlbum.this, "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "deletealbum");
                jData.put("userid", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
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
                    MyApp.isDeleteAlbum=1;
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class MakeDeleteAdmin extends AsyncTask<Void, String, String> {

        boolean isMake;
        String response;
        int position;

        public MakeDeleteAdmin(boolean isMake, int position) {
            this.isMake = isMake;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(ActivityUpdateAlbum.this, "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                if (isMake) {
                    jData.put("method", "makeadmin");
                    jData.put("album_id", "" + Data.getAlbum_id());
                    jData.put("user_id", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID));
                    jData.put("new_user_id", "" + Data.getMember_details().get(position).getMember_id());
                } else {
                    jData.put("method", "removeshareperson");
                    jData.put("album_id", "" + Data.getAlbum_id());
                    jData.put("shareperson", "" + Data.getMember_details().get(position).getMember_id());
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
                toast("" + jObj.getString("msg"));
                Intent intent;
                int status = jObj.optInt("success");
                if (status == 1) {

                    if (isMake) {
                        Data.getMember_details().get(position).setIsadmin(1);
                    } else {
                        Data.getMember_details().remove(position);
                    }
                    adpMemberDetail.notifyDataSetChanged();
                } else {

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}