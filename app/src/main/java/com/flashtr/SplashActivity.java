package com.flashtr;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.crittercism.app.Crittercism;
import com.flashtr.activity.*;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;

import java.io.File;


public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        /*Crittercism.initialize(getApplicationContext(), "560e0199d224ac0a00ed3efa");*/
//        Crittercism.initialize(getApplicationContext(), "72b2b317b6684fb7b5135396b447e7a100555300");

        write(Constant.SHRED_PR.KEY_RELOAD_Albums, "1");

        File file = new File(getExternalCacheDir().getAbsolutePath() + File.separator + Constant.AppName + File.separator);
        if (!file.exists()) file.mkdir();

        Constant.storageDirectory = file.getAbsolutePath();

        if (TextUtils.isEmpty(Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_Is_FirstTime))) {
            Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_Is_FirstTime, "1");
        }
        if (read(Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("1")) {
            startActivity(com.flashtr.activity.TabActivity.class);
            finish();
        } else {


//            LinearLayout mainRel = (LinearLayout) findViewById(R.id.mainRel);
//            mainRel.setPadding(0, Util.getStatusBarHeight(this), 0, 0);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(LoginActivity.class);
                    finish();
                }
            }, 2000);
        }

    }

    @Override
    public void onBackPressed() {
    }

}
