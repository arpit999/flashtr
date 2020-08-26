package com.flashtr;

import android.graphics.Color;
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

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.etMobile)
    EditText etMobile;
    @InjectView(R.id.main)
    RelativeLayout mainParent;
    @InjectView(R.id.tv_txtHeader)
    TextView mTxtHeader;
    @InjectView(R.id.tv_txtTitle)
    TextView mTxtTitle;
    @InjectView(R.id.tvLogin)
    TextView mTvLogin;

    Typeface type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Util.setupOutSideTouchHideKeyboard(this, mainParent);

        mainParent.setPadding(0, Util.getStatusBarHeight(this), 0, 0);

        type = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
        mTxtHeader.setTypeface(type);
        mTxtTitle.setTypeface(type);
        mTvLogin.setTypeface(type);
        etMobile.setTypeface(type);
    }

    @OnClick(R.id.rlSubmit)
    public void Click(View view) {
        if (isEmpty(getText(etMobile))) {
            Toast.makeText(getApplicationContext(), "please enter valid mobile", Toast.LENGTH_SHORT).show();
        } else {
            if (Util.isOnline(getApplicationContext())) {
                new Login(getText(etMobile)).execute();
            } else toast(Constant.network_error);
        }
    }


//    @OnClick(R.id.rlSubmit)
//    public void Submit(View view) {
//        Toast.makeText(getApplicationContext(),"CLICK",Toast.LENGTH_LONG).show();
//        if (isEmpty(getText(etMobile))) {
//            Toast.makeText(getApplicationContext(), "please enter valid mobile", Toast.LENGTH_SHORT).show();
//        } else {
//            if (Util.isOnline(getApplicationContext())) {
//                new Login(getText(etMobile)).execute();
//            } else toast(Constant.network_error);
//        }
//    }

    class Login extends AsyncTask<Void, String, String> {

        String mobile;

        String response;

        public Login(String mobile) {
            this.mobile = mobile;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(LoginActivity.this, "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "login");
                jData.put("mobileno", "" + mobile);

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

            if(Util.isOnline(LoginActivity.this)) {
                try {
                    JSONObject jObj = new JSONObject(result);
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
                            startActivity(VerifyCodeActivity.class);
                            finish();
                        } else {
                            toast(jObj.optString("msg"));
//                        toast("Please use below given code for the registration. This Code sent to Phone on SMS when its live, This is Just for testing Purpose, " );

//                        if(!TextUtils.isEmpty(jObj.optString("verify code"))){
//                            write(Constant.SHRED_PR.KEY_VERIFICATION_CODE, "" + jObj.optString("verify code"));
//                        }
                            if (!TextUtils.isEmpty(jObj.optString("verification_code"))) {
                                write(Constant.SHRED_PR.KEY_VERIFICATION_CODE, "" + jObj.optString("verification_code"));
                            }
                            write(Constant.SHRED_PR.KEY_MOBILE, "" + mobile);
                            startActivity(VerifyCodeActivity.class);
                            finish();
                        }
                    } else toast(jObj.optString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(LoginActivity.this,getResources().getString(R.string.no_internet),Toast.LENGTH_LONG).show();
            }
        }
    }

}
