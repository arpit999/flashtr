package com.flashtr;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by hirenkanani on 05/12/15.
 */
public class UpdateMobileNoActivity extends BaseActivity {
    TextView txt_oldMobNo;
    EditText etMobileOld;

    TextView txt_newMobNo;
    EditText etMobileNew;

    TextView tvLogin;


    LinearLayout lin_phase1;
    LinearLayout lin_phase2;

    TextView tvVerificationCode;

    EditText etVerificationCode;

    TextView tvVerification;

    LinearLayout mainParent;

    LinearLayout rel_header2;
    ImageView ivLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.act_update_mobno);



        txt_oldMobNo = (TextView) findViewById(R.id.txt_oldMobNo);
        etMobileOld = (EditText) findViewById(R.id.etMobileOld);
        txt_newMobNo = (TextView) findViewById(R.id.txt_newMobNo);
        etMobileNew = (EditText) findViewById(R.id.etMobileNew);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        lin_phase1 = (LinearLayout) findViewById(R.id.lin_phase1);
                lin_phase2 = (LinearLayout) findViewById(R.id.lin_phase2);
        tvVerificationCode = (TextView) findViewById(R.id.tvVerificationCode);
        tvVerification = (TextView) findViewById(R.id.tvVerification);
                etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        mainParent = (LinearLayout) findViewById(R.id.mainParent);
        rel_header2 = (LinearLayout) findViewById(R.id.rel_header2);
        ivLogout = (ImageView) findViewById(R.id.ivLogout);

        mainParent.setPadding(0, Util.getStatusBarHeight(this), 0, 0);

        Typeface type = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");

        txt_oldMobNo.setTypeface(type);
        etMobileOld.setTypeface(type);
        txt_newMobNo.setTypeface(type);
        etMobileNew.setTypeface(type);
        tvLogin.setTypeface(type);
        tvVerificationCode.setTypeface(type);
        etVerificationCode.setTypeface(type);
        tvVerification.setTypeface(type);



        Util.setupOutSideTouchHideKeyboard(this, mainParent);

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Next(v);
            }
        });

        tvVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Done(v);
            }
        });

    }



    public void Next(View view) {
        String current_mobile = Util.ReadSharePrefrence(getApplication(), Constant.SHRED_PR.KEY_MOBILE);
        if (isEmpty(getText(etMobileOld))) {
            Toast.makeText(getApplicationContext(), "please Enter Old Mobile Number", Toast.LENGTH_SHORT).show();
        } else {
            if (isEmpty(getText(etMobileNew))) {
                Toast.makeText(getApplicationContext(), "please Enter New Mobile Number", Toast.LENGTH_SHORT).show();
            } else if (current_mobile.equals(etMobileOld)) {
                Toast.makeText(getApplicationContext(), "Entered number can't be same as old mobile number", Toast.LENGTH_SHORT).show();
            } else {
                if (Util.isOnline(getApplicationContext())) {
                    new UpdateMobileNumber(getText(etMobileOld), getText(etMobileNew)).execute();
                    lin_phase1.setVisibility(View.GONE);
                    lin_phase2.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(getApplicationContext(), Constant.network_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
//    private void showDialog(String message) {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.app_name);
//        builder.setMessage(message);
//        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
//    }

    public void Done(View view) {
        if (isEmpty(getText(etVerificationCode))) {
            Toast.makeText(getApplicationContext(), "Please Enter Verification Code", Toast.LENGTH_SHORT).show();
        } else {
            if (Util.isOnline(getApplicationContext())) {
                new ChangeMobileNoVerifyCode(getText(etVerificationCode)).execute();
            } else
                Toast.makeText(getApplicationContext(), Constant.network_error, Toast.LENGTH_SHORT).show();
        }

    }

    class UpdateMobileNumber extends AsyncTask<Void, String, String> {

        String mobileOld, mobileNew;
        String response;

        public UpdateMobileNumber(String mobileOld, String mobileNew) {

            this.mobileOld = mobileOld;
            this.mobileNew = mobileNew;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(UpdateMobileNoActivity.this, "");
        }


        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "changemobileno");
                jData.put("old_mobile_no", "" + mobileOld);
                jData.put("new_mobile_no", "" + mobileNew);
                jData.put("userid", Util.ReadSharePrefrence(getApplication(), Constant.SHRED_PR.KEY_USERID));
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
                    toast(jObj.optString("msg"));
                    if (!TextUtils.isEmpty(jObj.optString("verification_code"))) {
                        write(Constant.SHRED_PR.KEY_VERIFICATION_CODE, "" + jObj.optString("verification_code"));
//                            showDialog( "" + jObj.optString("verification_code"));
                    }
                    write(Constant.SHRED_PR.KEY_MOBILE_NEW, "" + mobileNew);
                } else toast(jObj.optString("msg"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class ChangeMobileNoVerifyCode extends AsyncTask<Void, String, String> {

        String code;
        String response;

        public ChangeMobileNoVerifyCode(String code) {
            this.code = code;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomDialog.getInstance().show(UpdateMobileNoActivity.this, "");
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "changemobile_verify_code");
                jData.put("mobile_no", "" + read(Constant.SHRED_PR.KEY_MOBILE));
                jData.put("verification_code", "" + code);

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
                    toast(jObj.optString("msg"));
                    write(Constant.SHRED_PR.KEY_MOBILE, Constant.SHRED_PR.KEY_MOBILE_NEW);
                    write(Constant.SHRED_PR.KEY_MOBILE_NEW, "");

                } else toast(jObj.optString("msg"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
