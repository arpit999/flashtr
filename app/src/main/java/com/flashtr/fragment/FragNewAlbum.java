package com.flashtr.fragment;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flashtr.AlbumDetailsActivity;
import com.flashtr.R;
import com.flashtr.activity.CaptureActivity;
import com.flashtr.activity.TabActivity;
import com.flashtr.adapter.MembersAdapter;
import com.flashtr.util.Constant;
import com.flashtr.util.CustomDialog;
import com.flashtr.util.Util;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 24B on 4/22/2016.
 */
public class FragNewAlbum extends BaseFragment {

    @InjectView(R.id.tv_txtHeader)
    TextView tvTitle;
    @InjectView(R.id.tv_txtTagLine)
    TextView tvTagLine;
    @InjectView(R.id.etAlbumName)
    EditText etAlbumName;
    @InjectView(R.id.etSearchContact)
    EditText etSearchContact;
    @InjectView(R.id.lvMembers)
    ListView lvMembers;
    //    @InjectView(R.id.rlCross)
//    RelativeLayout rlCross;
    /*@InjectView(R.id.rlAlbumName)
    RelativeLayout rlAlbumname;*/
    /*@InjectView(R.id.rlSteps)
    RelativeLayout rlSteps;*/
    @InjectView(R.id.rlMemberList)
    RelativeLayout rlMemberList;
    /*@InjectView(R.id.rlDone)
    MaterialRippleLayout rlDone;*/
    /*@InjectView(R.id.rlNext)
    MaterialRippleLayout rlNext;*/
    /*@InjectView(R.id.tvDone)
    TextView tvDone;*/
    /*@InjectView(R.id.tvNext)
    TextView tvNext;*/
    /*@InjectView(R.id.ivBack)
    ImageView ivBack;*/
    /*@InjectView(R.id.rlAlbumNameNext)
    RelativeLayout rlAlbumNameNext;*/
    /*@InjectView(R.id.tvAddMemberTitle)
    TextView tvAddMemberTitle;*/
    @InjectView(R.id.main)
    LinearLayout mainParent;
    @InjectView(R.id.progressBar6)
    ProgressBar progressBar6;
    @InjectView(R.id.progressBar8)
    ProgressBar progressBar8;

    boolean flagCreate = true;
    HashMap<String, String> hashMap;
    ArrayList<HashMap<String, String>> listMembers = new ArrayList<>();
    ArrayList<HashMap<String, String>> listSearchMembers = new ArrayList<>();
    ArrayList<HashMap<String, String>> listContacts = new ArrayList<>();
    ArrayList<HashMap<String, String>> sourceList = new ArrayList<>();
    private String albumName;
    private Drawable backArrow;
    private String activity;

    private DemoFragment parent;
    private getMembers taskGetMem;


    public static FragNewAlbum newInstance() {
        FragNewAlbum FragNewAlbumInst = new FragNewAlbum();
        return FragNewAlbumInst;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*View mainView = inflater.inflate(R.layout.fragment_new_album, null);
        ButterKnife.inject(getActivity());*/
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View view = inflater.inflate(R.layout.fragment_new_album, container, false);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.inject(this, view);
        parent = (DemoFragment) ((TabActivity)getActivity()).adapter.getItem(1);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindControl();
    }

    private void bindControl() {
        init();

        Bundle extras = getArguments();
        Log.e("EXTRA", ">>>>>> " + extras);
        if (extras != null) {
            if (extras.getString("activity").equalsIgnoreCase("HomeActivity")) {
                activity = extras.getString("activity");
            }
            if (extras.getString("activity").equalsIgnoreCase("AlbumDetailsActivity")) {
                activity = extras.getString("activity");
                hashMap = (HashMap<String, String>) extras.getSerializable("map");
                flagCreate = false;
                tvTitle.setText(getResources().getString(R.string.str_new_header6));
                etAlbumName.setText("" + hashMap.get("album_name"));
            }
            if (extras.getString("activity").equalsIgnoreCase("AllAlbumsFragment")) {
                activity = extras.getString("activity");
            }
        }
        Log.e("flagCreate", ">>" + flagCreate);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Util.isOnline(getActivity().getApplicationContext())) {
                    if (listMembers != null && !(listMembers.isEmpty())) {

                        taskGetMem = new getMembers(false);
                        taskGetMem.execute();

                    } else {
                        taskGetMem = new getMembers(true);
                        taskGetMem.execute();
                    }
                } else toast(Constant.network_error);
            }
        }, 500);

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (Util.isOnline(getApplicationContext())) {
//                    new getMembers().execute();
//                } else toast(Constant.network_error);
//            }
//        }, 500);
    }
   /* @OnClick(R.id.rlBack)
    public void goBack(View view) {

        if (activity.equalsIgnoreCase("HomeActivity")) {
            *//*Intent intent = new Intent(NewAlbumActivity.this, HomeActivity.class);
//            Util.WriteSharePrefrence(this, Constant.SHRED_PR.KEY_RELOAD_Home, "1");
            startActivity(intent);*//*
            finish();
        }
        if (activity.equalsIgnoreCase("AlbumDetailsActivity")) {
           *//* Intent intent = new Intent(NewAlbumActivity.this, AlbumDetailsActivity.class);
            intent.putExtra("activity", "NewAlbumActivity");
            intent.putExtra("map", hashMap);
            startActivity(intent);*//*
            finish();

        }
        if (activity.equalsIgnoreCase("AllAlbumsFragment")) {
            Intent intent = new Intent(NewAlbumActivity.this, AllAlbumsFragment.class);
            startActivity(intent);
        }
    }*/

/* Todo click of next and done album */
//    @OnClick(R.id.rlNext)
//    public void Next() {
//        hideKeyboard();
//        if (isValidate()) {
//            albumName = etAlbumName.getText().toString();
////            Toast.makeText(NewAlbumActivity.this, ""+albumName, Toast.LENGTH_SHORT).show();
//            rlAlbumNameNext.setVisibility(View.GONE);
//            tvAddMemberTitle.setVisibility(View.VISIBLE);
//            rlNext.setVisibility(View.GONE);
//            rlDone.setVisibility(View.VISIBLE);
//            rlMemberList.setVisibility(View.VISIBLE);
//            if (Util.isOnline(getActivity().getApplicationContext())) {
//                new getMembers().execute();
//            } else toast(Constant.network_error);
//        }
//    }

    /* Todo create album form here */
    @OnClick(R.id.tvNext)
    public void Done() {

        if (Util.isOnline(getActivity().getApplicationContext())) {
            if(isValidate()) {
                new CreateAlbum(etAlbumName.getText().toString()).execute();
            }
        } else toast(Constant.network_error);
    }

    private void init() {
        backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        backArrow.setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.SRC_ATOP);
        /*ivBack.setBackground(backArrow);*/

        Util.setupOutSideTouchHideKeyboard(getActivity(), mainParent);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "SEGOEUI.TTF");
        tvTitle.setTypeface(type);
        tvTagLine.setTypeface(type);
        etAlbumName.setTypeface(type);
        etSearchContact.setTypeface(type);

        etSearchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchStr = getText(etSearchContact).toLowerCase().toString();
                if (searchStr.length() == 0) {
                    lvMembers.setDivider(null);
                    lvMembers.setAdapter(new MembersAdapter(getActivity(), listMembers));
                } else {
                    reloadContacts(searchStr);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchStr = getText(etSearchContact).toLowerCase().toString();
                if (searchStr.length() == 0) {
//                    rlCross.setVisibility(View.GONE);
                    lvMembers.setDivider(null);
                    lvMembers.setAdapter(new MembersAdapter(getActivity(), listMembers));
                } else {
//                    rlCross.setVisibility(View.VISIBLE);
                    reloadContacts(searchStr);
                }
            }
        });

    }

    private void reloadContacts(String searchStr) {

        for (int i = 0; i < listSearchMembers.size(); i++) {
            for (int j = 0; j < listMembers.size(); j++) {
                if (listSearchMembers.get(i).get("member_id").equals(listMembers.get(j).get("member_id"))) {
                    listMembers.get(j).put("selected", "" + listSearchMembers.get(i).get("selected"));
                }
            }
        }

        listSearchMembers.clear();
        for (int i = 0; i < listMembers.size(); i++) {
            String name = listMembers.get(i).get("member_name");
            if (name.length() >= searchStr.length()) {
                name = name.substring(0, searchStr.length());
                if (name.equalsIgnoreCase(searchStr)) listSearchMembers.add(listMembers.get(i));
            }
        }
        lvMembers.setDivider(null);
        lvMembers.setAdapter(new MembersAdapter(getActivity(), listSearchMembers));

    }

    private void reload(String result) {

        listMembers.clear();

        if (!flagCreate) {
            try {
                JSONArray jsonArray1 = new JSONArray("" + hashMap.get("member_details"));
                Log.e("jsonArray1", ">>" + jsonArray1);
                for (int j = 0; j < jsonArray1.length(); j++) {
                    HashMap<String, String> hashMap1 = new HashMap<String, String>();
                    hashMap1.put("member_id", "" + jsonArray1.optJSONObject(j).optString("member_id"));
                    hashMap1.put("member_name", "" + jsonArray1.optJSONObject(j).optString("member_name"));
                    hashMap1.put("member_image", "" + jsonArray1.optJSONObject(j).optString("member_image"));
                    hashMap1.put("member_number", "" + jsonArray1.optJSONObject(j).optString("member_number"));
                    hashMap1.put("selected", "1");
                    listMembers.add(hashMap1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            JSONObject jObj = new JSONObject(result);
            int status = jObj.optInt("success");

            if (status == 1) {
                JSONArray jsonArray = jObj.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);

                    HashMap<String, String> hashMap1 = new HashMap<String, String>();
                    hashMap1.put("member_id", "" + jsonObject.optString("member_id"));
                    hashMap1.put("member_name", "" + jsonObject.optString("member_name"));
                    hashMap1.put("member_image", "" + jsonObject.optString("member_image"));
                    hashMap1.put("member_number", "" + jsonObject.optString("member_number"));
                    hashMap1.put("selected", "0");

                    boolean flag = true;
                    if (!flagCreate) {
                        try {
                            JSONArray jsonArray1 = new JSONArray("" + hashMap.get("member_details"));
                            Log.e("jsonArray1", ">>" + jsonArray1);
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                if (hashMap1.get("member_id").equals(jsonArray1.optJSONObject(j).optString("member_id")))
                                    flag = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (flag) {
                        if (!jsonObject.optString("member_number").equalsIgnoreCase(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_MOBILE))) {
                            listMembers.add(hashMap1);
                        }
                    }


                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        int size = listMembers.size();
        for (int i = 0; i < listContacts.size(); i++) {
            boolean flag = true;
            for (int j = 0; j < size; j++) {
                if (listContacts.get(i).get("member_number").equals(listMembers.get(j).get("member_number")))
                    flag = false;
            }

            if (flag) listMembers.add(listContacts.get(i));
        }
        lvMembers.setDivider(null);
        listContacts.trimToSize();

        if (listMembers != null && !listMembers.isEmpty()) {
            if (listContacts != null && !listContacts.isEmpty()) {
                for (int i = 0; i < listContacts.size(); i++) {
                    final HashMap<String, String> contact = listContacts.get(i);
                    for (int j = 0; j < listMembers.size(); j++) {
                        final HashMap<String, String> member = listMembers.get(j);
                        if (contact.get("member_number").equalsIgnoreCase(member.get("member_number"))) {
                            member.put("member_name", contact.get("member_name"));
                            break;
                        }
                    }
                }
            }
        }

        lvMembers.setAdapter(new MembersAdapter(getActivity(), listMembers));

    }

    public String getContacts() {
        String contacts = "";
        listContacts.clear();
        if(getActivity()!=null) {
            Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//      Log.e("Total >>>>>> ",""+phones.getCount());

            if (phones != null && phones.getCount() > 0) {
//            Log.e("Phone Count = ", ">>" + phones.getCount());
                phones.moveToFirst();
                do {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                Log.e("Name "," "+name+" "+phoneNumber);
//                String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//                Uri photo = getPhotoUri(id);

                    phoneNumber = phoneNumber.replaceAll("\\D+", "");
                    if (phoneNumber.length() >= 10) {
                        phoneNumber = Util.lastN(phoneNumber, 10);
//                    if (contacts.length() == 0) contacts = phoneNumber;
//                    else contacts = contacts + "," + phoneNumber;

                        HashMap<String, String> hashMap1 = new HashMap<String, String>();
                        hashMap1.put("member_id", "");
                        hashMap1.put("member_name", "" + name);
                        hashMap1.put("member_image", "");
                        hashMap1.put("member_number", "" + phoneNumber);
                        hashMap1.put("selected", "2");
                        sourceList.add(hashMap1);
                    }
                } while (phones.moveToNext());
                phones.close();
            }

//        Log.e("sourceList >>>>> ","Total = "+sourceList.size());

            listContacts = new ArrayList(new HashSet(sourceList));
            for (int i = 0; i < listContacts.size(); i++) {
                String number = listContacts.get(i).get("member_number");
                if (number.length() >= 10) {
                    number = Util.lastN(number, 10);
                    if (contacts.length() == 0) contacts = number;
                    else contacts = contacts + "," + number;
//                Log.e("DATA",""+ i +">> " + number);
                }
            }

//        Log.e("Contact",""+contacts);
//        Log.e("sourceList >>>>> ","Total = "+listContacts.size());

            IgnoreCaseComparator icc = new IgnoreCaseComparator();
            Collections.sort(listContacts, icc);
        }
        return contacts;

    }

    public Uri getPhotoUri(String id) {
        try {
            Cursor cur = getActivity().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    private boolean isValidate() {

        if (isEmpty(getText(etAlbumName))) {
            toast("please enter album name");
            return false;
        }

        return true;
    }

    class getMembers extends AsyncTask<Void, String, String> {

        String response;
        boolean isDialogShown;

        public getMembers(boolean isDialogShown){
            this.isDialogShown = isDialogShown;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isDialogShown) {
//                CustomDialog.getInstance().show(getActivity(), "");
                progressBar6.setVisibility(View.VISIBLE);
            }
        }


        @Override
        protected String doInBackground(Void... params) {


            String contacts = getContacts();
            //contacts = "9033544558,1234567890,234333313,9879610351,8487847678";
            Log.e("CONTACTS ", "" + contacts);

            try {
                if(contacts!=null&&contacts.length()>0) {
                    JSONObject jData = new JSONObject();
                    jData.put("method", "memberlist");
                    jData.put("mobileno", "" + contacts);

                    List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                    params1.add(new BasicNameValuePair("data", jData.toString()));
                    response = Util.makeServiceCall(Constant.URL, 2, params1);
//                Log.e("params1", ">>" + params1);
                }
                Log.e("** response is:- ", ">>" + response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

//            if(CustomDialog.getInstance().isDialogShowing())
//                CustomDialog.getInstance().hide();
            progressBar6.setVisibility(View.GONE);
            if(getActivity()!=null) {
                if (result != null && result.length() > 0) {
                    reload(result);
                }
            }

        }
    }

    class IgnoreCaseComparator implements Comparator<HashMap<String, String>> {
        public int compare(HashMap<String, String> hashMap1, HashMap<String, String> hashMap2) {
            String strA = hashMap1.get("member_name").toLowerCase();
            String strB = hashMap2.get("member_name").toLowerCase();

            return strA.compareToIgnoreCase(strB);
        }
    }

    class CreateAlbum extends AsyncTask<Void, String, String> {

        String Name, device_id;

        private CreateAlbum(String Name) {
            this.Name = Name;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            CustomDialog.getInstance().show(getActivity(), "");
            progressBar8.setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String sharePersons = "";
            for (int i = 0; i < listMembers.size(); i++) {
                if (listMembers.get(i).get("selected").equals("1")) {
                    if (sharePersons.length() == 0)
                        sharePersons = listMembers.get(i).get("member_id");
                    else sharePersons = sharePersons + "," + listMembers.get(i).get("member_id");
                }
            }
            Log.e("sharePersons", ">>" + sharePersons);

            String resp = "";
            try {
                device_id = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_GCM_REGID);
                JSONObject jData = new JSONObject();
                jData.put("method", "createalbum");
                jData.put("userid", "" + read(Constant.SHRED_PR.KEY_USERID));
                jData.put("albumname", "" + Name);
                jData.put("shareperson", "" + sharePersons);
                if (flagCreate)
                    jData.put("album_id", "0");
                else jData.put("album_id", "" + hashMap.get("album_id"));

                HttpClient client = new DefaultHttpClient();
                HttpResponse response = null;
                HttpPost poster = new HttpPost(
                        Constant.URL);

                FileBody fbody = null;
                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);


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
//            CustomDialog.getInstance().hide();
            progressBar8.setVisibility(View.GONE);
            Log.e("result", ">>" + result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                toast("" + jsonObject.getString("msg"));

                int status = jsonObject.optInt("success");
                if (status == 1) {
                    etAlbumName.setText("");
                    String userId = read(Constant.SHRED_PR.KEY_USERID);
                    new getAlbums(userId).execute();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    class getAlbums extends AsyncTask<Void, String, String> {

        String response;
        String userId;

        private getAlbums(String userId) {
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            CustomDialog.getInstance().show(getActivity(), "");
            progressBar8.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject();
                jData.put("method", "getalbum");
                jData.put("userid", "" + userId);
                jData.put("page", "" + "");

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
                progressBar8.setVisibility(View.GONE);
                if (status == 1) {

                    final JSONArray jsonArray = jObj.getJSONArray("data");
                    final JSONObject jsonObject = jsonArray.optJSONObject(0);
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID, jsonObject.optString("album_id"));
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName, jsonObject.optString("album_name"));
                    Util.WriteSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Albums, "" + result);
                    write(Constant.SHRED_PR.KEY_RELOAD_Home, "1");
                    write(Constant.SHRED_PR.KEY_RELOAD_Albums, "1");

                    com.flashtr.HomeActivity.selectedPosition = 0;
                    String albumName = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumName);
                    String albumId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_Current_AlbumID);
                    (DemoFragment.getInstance()).pushFragments(Constant.TAB_FRAGMENT_HOME, HomeFragment.newInstance(albumId), false, false, true, true);
                    (DemoFragment.getInstance()).setTabColor(3);
                    // Todo: set name and album at top on camera
                    CaptureActivity.getInstance().setName();
                    HomeFragment.getInstance().refreshList();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (!flagCreate) {
                write(Constant.SHRED_PR.KEY_RELOAD_Home, "2");
                if (AlbumDetailsActivity.albumDetailsActivity != null)
                    AlbumDetailsActivity.albumDetailsActivity.finish();
            }

            /* Todo change fragment after successfully applied for fragment */
//            finish();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(taskGetMem.getStatus()== AsyncTask.Status.RUNNING||taskGetMem.getStatus()== AsyncTask.Status.PENDING){
            taskGetMem.cancel(true);
        }
    }
}
