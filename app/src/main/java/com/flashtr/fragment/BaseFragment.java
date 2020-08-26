package com.flashtr.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.flashtr.app.MyApp;
import com.flashtr.myInterface.OnBackApiResponse;
import com.flashtr.myInterface.OnBackPressed;
import com.flashtr.myInterface.OnClickEvent;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;


public class BaseFragment extends Fragment implements OnBackApiResponse,OnClickEvent,OnBackPressed {

//	public TabActivity mActivityHome;
//	Initialize Global classes
	public CustomDialog customDialog;
	public Util util;

//	public TimeAgo timeAgo;
//	public AnimationUtil animUtil;
//	public DatabaseHelper dbHelper;
//	public GraphicsUtil gUtil;
	SharedPreferences commomPref;

	public Typeface fontRobotoReg;


	public int userId = 0;


	
//	Toast mToast;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		mActivityHome=(TabActivity) getActivity();

	}
	protected void toast(CharSequence text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	protected void toast(int resId) {
		toast(this.getResources().getText(resId));
	}

	protected void startActivity(Class klass) {
		startActivity(new Intent(getActivity(), klass));
	}

	protected String getText(EditText eTxt) {
		return eTxt == null ? "" : eTxt.getText().toString().trim();
	}

	protected boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	protected void write(String key, String val) {
		Util.WriteSharePrefrence(getActivity(), key, val);
	}

	protected String read(String key) {
		return Util.ReadSharePrefrence(getActivity(), key);
	}

	public void hideKeyboard() {
		// Check if no view has focus:
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/*@Override
	public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
		// TODO Auto-generated method stub
		
		Log.v("onCreateAnimator", "enter: " + enter + " transit: " + transit);
		if (MyApplication.disableFragmentAnimations) {
			if(!enter)
			{
				Animator animator = null;
				if(nextAnim!=0)
				{
					animator = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
					if(animator!=null)
						animator.setDuration(0);
					return animator;
				}
				
			}
		}
		return super.onCreateAnimator(transit, enter, nextAnim);
	}*/

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

		Log.v("onCreateAnimator", "enter: " + enter + " transit: " + transit);
		if (MyApp.disableFragmentAnimations) {
			if(!enter)
			{
				Animation anim = null;
				if(nextAnim!=0)
				{
					anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
					if(anim!=null)
						anim.setDuration(0);
					return anim;
				}

			}
		}

		return super.onCreateAnimation(transit, enter, nextAnim);
	}

	@Override
	public void setBackApiResponse(String result, int ApiId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setBackApiResponse(String result, int APIID, int pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBackApiResponse(String result, int APIID, int pos,String strUsertype) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackApiResponse(String result, int APIID,String strUsertype) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clickEvent(View v) {

	}


	@Override
	public void onBackPressed() {

	}
}
