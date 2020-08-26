package com.flashtr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class VerifyCodeActivity extends BaseActivity {

    @InjectView(R.id.tvNext)
    TextView tvNext;
    @InjectView(R.id.etCode)
    EditText etCode;
    String activity;
    @InjectView(R.id.main)
    RelativeLayout mainParent;

    @InjectView(R.id.tv_txtHeader)
    TextView mTxtHeader;
    @InjectView(R.id.tv_txtTitle)
    TextView mTxtTitle;
    @InjectView(R.id.tvBack)
    TextView mTvBack;
    @InjectView(R.id.tvResend)
    TextView mTvResend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_verify_code);



        Util.setupOutSideTouchHideKeyboard(this, mainParent);
        mainParent.setPadding(0, Util.getStatusBarHeight(this), 0, 0);

        Typeface type = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
        etCode.setTypeface(type);
        mTxtHeader.setTypeface(type);
        mTxtTitle.setTypeface(type);
        mTvBack.setTypeface(type);
        mTvResend.setTypeface(type);
        tvNext.setTypeface(type);
//        showDialog(String.format("Verification code : %s", read(Constant.SHRED_PR.KEY_VERIFICATION_CODE)));
    }

    @OnClick(R.id.rlBack)
    public void Back(View view) {
        write(Constant.SHRED_PR.KEY_USERID, "");
        write(Constant.SHRED_PR.KEY_USERNAME, "");
        write(Constant.SHRED_PR.KEY_USERIMAGE, "");
        write(Constant.SHRED_PR.KEY_MOBILE, "");
        write(Constant.SHRED_PR.KEY_EMAIL, "");
        write(Constant.SHRED_PR.KEY_IS_LOGGEDIN, "");
        startActivity(LoginActivity.class);
        finish();
    }

    @OnClick(R.id.rlResend)
    public void Resend(View view) {
        /*TODO call resend verification api */
        new ResendVerifyCode().execute();
    }

    @OnClick(R.id.rlSubmit)
    public void Submit(View view) {
        if (isEmpty(getText(etCode))) {
            Toast.makeText(getApplicationContext(), "please enter verification code", Toast.LENGTH_SHORT).show();
        } else {
            if (Util.isOnline(getApplicationContext())) {
                new VerifyCode(getText(etCode)).execute();
            } else toast(Constant.network_error);
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void showDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    class VerifyCode extends AsyncTask<Void, String, String> {

        String code;
        String response;

        public VerifyCode(String code) {
            this.code = code;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(VerifyCodeActivity.this, "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "verificationcode");
                jData.put("mobileno", "" + read(Constant.SHRED_PR.KEY_MOBILE));
                jData.put("verify_code", "" + code);

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
            if(Util.isOnline(VerifyCodeActivity.this)) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    toast("" + jObj.getString("msg"));
                    Intent intent;
                    int status = jObj.optInt("success");
                    if (status == 1) {

                        if (read(Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("2")) {
                            write(Constant.SHRED_PR.KEY_IS_LOGGEDIN, "1");
                            intent = new Intent(VerifyCodeActivity.this, RegisterActivity.class);
                            intent.putExtra("activity", "RegisterActivity");
                            startActivity(intent);
                            finish();
                        } else {
                            intent = new Intent(VerifyCodeActivity.this, RegisterActivity.class);

                            if (jObj.has("data") && jObj.optJSONArray("data").optJSONObject(0) != null) {
                                final JSONObject userObject = jObj.optJSONArray("data").optJSONObject(0);
                                Log.e(getClass().getSimpleName(), "");
                                write(Constant.SHRED_PR.KEY_USERID, "" + userObject.optString("userid"));
                                intent.putExtra(Constant.SHRED_PR.KEY_Data, userObject.toString());
                            } else {
                                write(Constant.SHRED_PR.KEY_USERID, "" + jObj.optString("userid"));
                            }
                            intent.putExtra("activity", "RegisterActivity");
                            startActivity(intent);
                            finish();

                        }
                    } else toast(jObj.optString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(VerifyCodeActivity.this,getResources().getString(R.string.no_internet),Toast.LENGTH_LONG).show();
            }
        }
    }

    class ResendVerifyCode extends AsyncTask<Void, String, String> {

        String response;

        public ResendVerifyCode() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(VerifyCodeActivity.this, "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "resendcode");
                jData.put("mobileno", "" + read(Constant.SHRED_PR.KEY_MOBILE));

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
//                    toast("CODE = "+jObj.optString("verification_code"));
                    if (jObj.has("data")) {
                        JSONObject jsonObject = jObj.optJSONObject("data");
                        write(Constant.SHRED_PR.KEY_USERID, "" + jsonObject.optString("userid"));
                        write(Constant.SHRED_PR.KEY_USERNAME, "" + jsonObject.optString("username"));
                        write(Constant.SHRED_PR.KEY_USERIMAGE, "" + jsonObject.optString("user_image"));
                        write(Constant.SHRED_PR.KEY_MOBILE, "" + jsonObject.optString("mobileno"));
                        write(Constant.SHRED_PR.KEY_EMAIL, "" + jsonObject.optString("email"));
                        write(Constant.SHRED_PR.KEY_IS_LOGGEDIN, "2");
                       /* startActivity(VerifyCodeActivity.class);
                        finish();*/
                    } else {
                        toast(jObj.optString("msg"));
//                        toast("Please use below given code for the registration. This Code sent to Phone on SMS when its live, This is Just for testing Purpose, " );

//                        if(!TextUtils.isEmpty(jObj.optString("verify code"))){
//                            write(Constant.SHRED_PR.KEY_VERIFICATION_CODE, "" + jObj.optString("verify code"));
//                        }
                        if (!TextUtils.isEmpty(jObj.optString("verification_code"))) {
                            write(Constant.SHRED_PR.KEY_VERIFICATION_CODE, "" + jObj.optString("verification_code"));
                        }
                        write(Constant.SHRED_PR.KEY_MOBILE, "" + read(Constant.SHRED_PR.KEY_MOBILE));
                        /*startActivity(VerifyCodeActivity.class);
                        finish();*/
                    }
                } else toast(jObj.optString("msg"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
