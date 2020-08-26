package com.flashtr.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.flashtr.myInterface.OnBackApiResponse;
import com.flashtr.myInterface.OnClickEvent;
import android.support.v7.app.AppCompatActivity;

public class GlobalActivity extends AppCompatActivity implements OnBackApiResponse, OnClickEvent {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void setBackApiResponse(String result, int ApiId) {

    }

    @Override
    public void setBackApiResponse(String result, int APIID, int pos) {

    }

    @Override
    public void setBackApiResponse(String result, int APIID, String userType) {

    }

    @Override
    public void setBackApiResponse(String result, int APIID, int pos, String userType) {

    }

    @Override
    public void clickEvent(View v) {

    }
}
