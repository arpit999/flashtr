package com.flashtr.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.flashtr.LoginActivity;
import com.flashtr.R;
import com.flashtr.RegisterActivity;
import com.flashtr.TipsActivity;
import com.flashtr.UpdateMobileNoActivity;
import com.flashtr.activity.TabActivity;
import com.flashtr.myInterface.OnBackPressed;
import com.flashtr.myInterface.OnClickEvent;
import com.flashtr.util.Constant;
import com.flashtr.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by hirenkanani on 05/01/16.
 */
public class SettingsFragmnet extends BaseFragment {

    /*@InjectView(R.id.rlUpdateNumber)
    MaterialRippleLayout rlUpdateNumber;*/
    @InjectView(R.id.rlShare)
    MaterialRippleLayout rlShare;
    @InjectView(R.id.rlReview)
    MaterialRippleLayout rlReview;
    /*@InjectView(R.id.rlLogout)
    MaterialRippleLayout rlLogout;*/
    @InjectView(R.id.rlFeedback)
    MaterialRippleLayout rlFeedback;
    @InjectView(R.id.rlEditProfile)
    MaterialRippleLayout mRlEditProfile;
    @InjectView(R.id.rlUpdateNo)
    MaterialRippleLayout mRlUpdateNo;
    @InjectView(R.id.iv_imgProfile)
    ImageView mImgProfile;
    @InjectView(R.id.tv_txtName)
    TextView mTxtName;
    @InjectView(R.id.tv_txtEditProfile)
    TextView mTxtEditProfile;
    @InjectView(R.id.tv_txtTitle)
    TextView mTxtTitle;
    @InjectView(R.id.tv_txtHeader)
    TextView mTxtHeader;
    @InjectView(R.id.tvFeedback)
    TextView mTvFeedback;
    @InjectView(R.id.tvShare)
    TextView mTvShare;
    @InjectView(R.id.tvReview)
    TextView mTvReview;
    @InjectView(R.id.tvTips)
    TextView tvTips;
    @InjectView(R.id.tvContactUs)
    TextView tvContactUs;
    @InjectView(R.id.tv_txtTitle4)
    TextView tv_txtTitle4;
    @InjectView(R.id.tv_txtTitle3)
    TextView tv_txtTitle3;
    @InjectView(R.id.tv_txtTitle2)
    TextView tv_txtTitle2;
    @InjectView(R.id.tvFlashtrVersion)
    TextView tvFlashtrVersion;
    @InjectView(R.id.tvFlashtr)
    TextView tvFlashtr;
    @InjectView(R.id.tvTerms)
    TextView tvTerms;


    public static SettingsFragmnet newInstance() {
        SettingsFragmnet settingsFragmnet = new SettingsFragmnet();
        return settingsFragmnet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");
        Typeface typeBD = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUIB.TTF");
        mTxtHeader.setTypeface(type);
        mTxtTitle.setTypeface(type);
        mTxtName.setTypeface(type);
        mTxtEditProfile.setTypeface(type);
        mTvFeedback.setTypeface(type);
        mTvShare.setTypeface(type);
        mTvReview.setTypeface(type);
        tvTips.setTypeface(type);
        tvContactUs.setTypeface(type);
        tv_txtTitle4.setTypeface(type);
        tv_txtTitle3.setTypeface(type);
        tv_txtTitle2.setTypeface(type);
        tvFlashtr.setTypeface(type);
        tvFlashtrVersion.setTypeface(typeBD);
        tvTerms.setTypeface(type);
        tvFlashtrVersion.setText(Util.getAppVersion(getActivity()));

        String ProfileImage = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERIMAGE);
        if (ProfileImage != null && ProfileImage.length() > 0) {
            Picasso.with(getActivity()).load(ProfileImage).error(R.drawable.ic_profile).placeholder(R.drawable.ic_profile).resize(100, 100).centerCrop().into(mImgProfile);
        } else {
            mImgProfile.setImageResource(R.drawable.default_profile);
        }
        mTxtName.setText(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERNAME) + " " + "[Edit Profile]");
    }

    /*@OnClick(R.id.rlUpdateNumber)
    public void updateNumber(View view){
        Intent intent = new Intent(getActivity(), UpdateMobileNoActivity.class);
        startActivity(intent);
    }*/

    @OnClick(R.id.rlUpdateNo)
    public void updateNumber(View view) {
        Intent intent = new Intent(getActivity(), UpdateMobileNoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rlFeedback)
    public void giveFeedback(View view) {
        Intent email = new Intent(Intent.ACTION_VIEW);
        email.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
//        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"appflashtr@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Android Feedback");
        email.putExtra(Intent.EXTRA_TEXT, "Dear Sir/Madam,");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @OnClick(R.id.rlTips)
    public void startTips(View view)
    {
        Intent i=new Intent(getActivity(),TipsActivity.class);
        i.putExtra("isFromSettings",true);
        startActivity(i);
    }

    @OnClick(R.id.rlContactUs)
    public void startContactUs(View view) {
        Intent email = new Intent(Intent.ACTION_VIEW);
        email.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
//        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"appflashtr@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Android Contact Us");
        email.putExtra(Intent.EXTRA_TEXT, "Dear Sir/Madam,");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @OnClick(R.id.rlEditProfile)
    public void editProfile(View view) {
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        intent.putExtra("activity", "HomeActivity");

        startActivity(intent);
    }

    @OnClick(R.id.rlShare)
    public void share(View view) {
        // SHARE APPLICATION
        ((TabActivity)getActivity()).shareApp();

    }

    @OnClick(R.id.rlReview)
    public void review(View view) {
        //RATE APPLICATION CODE
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            if (goToMarket.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(goToMarket);
            }
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    @Override
    public void clickEvent(View v) {

    }

    @OnClick(R.id.rlLogout)
    public void logout(View view){
        AlertDialog.Builder alert1 = new AlertDialog.Builder(getActivity());
        alert1.setTitle("" + Constant.AppName);
        alert1.setMessage("Logout from Flashtr?");
        alert1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_IS_LOGGEDIN, "0");
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Albums, "");
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID, "");
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName, "");
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GET_PHOTOS, "");

                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID, "");
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERNAME, "");
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERIMAGE, "");
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_MOBILE, "");
                        Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_EMAIL, "");

                        Intent i = new Intent(getActivity(),
                                LoginActivity.class);
                        if (android.os.Build.VERSION.SDK_INT >= 11) {
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        } else {
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        startActivity(i);
                    }
                });
        alert1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub

                        dialog.cancel();
                    }
                });
        alert1.create();
        alert1.show();
    }

}
