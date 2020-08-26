package com.flashtr.activity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flashtr.BaseActivity;
import com.flashtr.R;
import com.flashtr.adapter.ReactionAdapter;
import com.flashtr.model.MemberDetail;
import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24B on 5/31/2016.
 */
public class ReactionScreen extends BaseActivity {

    private ImageView ivBack;
    private ListView listReaction;
    private TextView tv_txtHeader;

    private TextView txt1, txt2, txt3, txt4, txt5, txt6;
    private View view1, view2, view3, view4, view5, view6;

    private LinearLayout lin1,lin2,lin3,lin4,lin5,lin6;

    private String PhotoId;
    private String numberOfLikes;
    private List<List<MemberDetail>> mainList = new ArrayList<List<MemberDetail>>();

    Typeface fonTypefaceBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_reaction);

        fonTypefaceBD = Typeface.createFromAsset(getAssets(), "SEGOEUIB.TTF");

        ivBack = (ImageView) findViewById(R.id.ivBack);
        listReaction = (ListView) findViewById(R.id.listReaction);
        tv_txtHeader = (TextView) findViewById(R.id.tv_txtHeader);

        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);
        txt3 = (TextView) findViewById(R.id.txt3);
        txt4 = (TextView) findViewById(R.id.txt4);
        txt5 = (TextView) findViewById(R.id.txt5);
        txt6 = (TextView) findViewById(R.id.txt6);

        txt1.setTypeface(fonTypefaceBD);
        txt2.setTypeface(fonTypefaceBD);
        txt3.setTypeface(fonTypefaceBD);
        txt4.setTypeface(fonTypefaceBD);
        txt5.setTypeface(fonTypefaceBD);
        txt6.setTypeface(fonTypefaceBD);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
        view6 = findViewById(R.id.view6);

        lin1 = (LinearLayout) findViewById(R.id.lin1);
        lin2 = (LinearLayout) findViewById(R.id.lin2);
        lin3 = (LinearLayout) findViewById(R.id.lin3);
        lin4 = (LinearLayout) findViewById(R.id.lin4);
        lin5 = (LinearLayout) findViewById(R.id.lin5);
        lin6 = (LinearLayout) findViewById(R.id.lin6);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent().hasExtra("PhotoId")){
            PhotoId = getIntent().getStringExtra("PhotoId");
        }

        if(getIntent().hasExtra("num")){
            numberOfLikes = getIntent().getStringExtra("num");
        }

        if(numberOfLikes!=null && numberOfLikes.length()>0){
            tv_txtHeader.setText(numberOfLikes+" REACTED");
        }else{
            tv_txtHeader.setText("NO ONE REACTED");
        }

        new CallReactionData(PhotoId).execute();

        lin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(0);
            }
        });

        lin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(1);
            }
        });

        lin3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(2);
            }
        });

        lin4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(3);
            }
        });

        lin5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(4);
            }
        });

        lin6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(5);
            }
        });
    }

    public void setView(int pos){

        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        view3.setVisibility(View.GONE);
        view4.setVisibility(View.GONE);
        view5.setVisibility(View.GONE);
        view6.setVisibility(View.GONE);

        switch (pos){
            case 0:
                view1.setVisibility(View.VISIBLE);
                setReactionScreenAdp(0);
                break;
            case 1:
                view2.setVisibility(View.VISIBLE);
                setReactionScreenAdp(1);
                break;
            case 2:
                view3.setVisibility(View.VISIBLE);
                setReactionScreenAdp(2);
                break;
            case 3:
                view4.setVisibility(View.VISIBLE);
                setReactionScreenAdp(3);
                break;
            case 4:
                view5.setVisibility(View.VISIBLE);
                setReactionScreenAdp(4);
                break;
            case 5:
                view6.setVisibility(View.VISIBLE);
                setReactionScreenAdp(5);
                break;

        }

    }

    class CallReactionData extends AsyncTask<Void, String, String> {

        String albumId;
        String response;

        private CallReactionData(String albumId) {
            this.albumId = albumId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(ReactionScreen.this, "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "getlikesbyphotoid");
                jData.put("photo_id", "" + PhotoId);


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

                    JSONArray jsonArray = jObj.getJSONArray("data");
                    JSONObject jPos = jsonArray.getJSONObject(0);
                    for(int i=0;i<jPos.length();i++){
                        JSONArray jsonArray1 = jPos.getJSONArray("like_"+(i+1));
                        List<MemberDetail> memberDetails = new ArrayList<MemberDetail>();
                        for(int j=0;j<jsonArray1.length();j++){
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);

                            MemberDetail mDe = new MemberDetail();
                            mDe.setMember_id(jsonObject1.getString("member_id"));
                            mDe.setMember_name(jsonObject1.getString("member_name"));
                            mDe.setMember_image(jsonObject1.getString("member_image"));
                            mDe.setMember_email(jsonObject1.getString("member_email"));
                            mDe.setMember_number(jsonObject1.getString("member_number"));

                            memberDetails.add(mDe);
                        }

                        mainList.add(memberDetails);

                    }

                    setReactionScreenAdp(0);
                }else{

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setReactionScreenAdp(int pos){
        if(mainList.get(pos)!=null&& mainList.get(pos).size()>0) {
            listReaction.setVisibility(View.VISIBLE);
            ReactionAdapter adp = (ReactionAdapter) listReaction.getAdapter();
            if (adp != null) {
                adp.setData(mainList.get(pos));
                adp.notifyDataSetChanged();
            } else {
                adp = new ReactionAdapter(ReactionScreen.this, mainList.get(pos));
                listReaction.setAdapter(adp);
            }
        }else{
            listReaction.setVisibility(View.GONE);
        }
        txt1.setText(mainList.get(0).size()+"");
        txt2.setText(mainList.get(1).size()+"");
        txt3.setText(mainList.get(2).size()+"");
        txt4.setText(mainList.get(3).size()+"");
        txt5.setText(mainList.get(4).size()+"");
        txt6.setText(mainList.get(5).size()+"");
    }

}
