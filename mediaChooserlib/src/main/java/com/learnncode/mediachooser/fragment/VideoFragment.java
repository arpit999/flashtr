/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */



package com.learnncode.mediachooser.fragment;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.learnncode.mediachooser.MediaChooserConstants;
import com.learnncode.mediachooser.MediaModel;
import com.learnncode.mediachooser.R;
import com.learnncode.mediachooser.adapter.GridViewAdapter;

public class VideoFragment extends Fragment implements OnScrollListener {

	private final static Uri MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	private final static String MEDIA_DATA = MediaStore.Video.Media.DATA;

	private GridViewAdapter mVideoAdapter;
	private GridView mVideoGridView;
	private Cursor mCursor;
	private int mDataColumnIndex;
	private ArrayList<String> mSelectedItems = new ArrayList<String>();
	private ArrayList<MediaModel> mGalleryModelList;
	private View mView;
	private OnVideoSelectedListener mCallback;


	// Container Activity must implement this myInterface
	public interface OnVideoSelectedListener {
		public void onVideoSelected(int count);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback myInterface. If not, it throws an exception
		try {
			mCallback = (OnVideoSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnVideoSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public VideoFragment(){
		setRetainInstance(true);
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if(mView == null){
			mView = inflater.inflate(R.layout.view_grid_layout_media_chooser, container, false);

			mVideoGridView = (GridView)mView.findViewById(R.id.gridViewFromMediaChooser);

			if (getArguments() != null) {
				initVideos(getArguments().getString("name"));
			}else{
				initVideos();
			}

		}else{
			((ViewGroup) mView.getParent()).removeView(mView);
			if(mVideoAdapter == null || mVideoAdapter.getCount() == 0){
				Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
			}
		}

		return mView;
	};


	private void initVideos(String bucketName) {

		try {
			final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
			String searchParams = null;
			searchParams = "bucket_display_name = \"" + bucketName + "\"";

			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Video.Media._ID};
			mCursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, searchParams, null, orderBy + " DESC");
			setAdapter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initVideos() {

		try {
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			//Here we set up a string array of the thumbnail ID column we want to get back

			String [] proj = {MediaStore.Video.Media.DATA,MediaStore.Video.Media._ID};

			mCursor =  getActivity().getContentResolver().query(MEDIA_EXTERNAL_CONTENT_URI, proj, null,null, orderBy + " DESC");
			setAdapter();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setAdapter() {
		int count = mCursor.getCount();

		if(count > 0){
			mDataColumnIndex = mCursor.getColumnIndex(MEDIA_DATA);

			//move position to first element
			mCursor.moveToFirst();

			mGalleryModelList = new ArrayList<MediaModel>();
			for(int i= 0; i < count; i++) {
				mCursor.moveToPosition(i);
				String url = mCursor.getString(mDataColumnIndex);
				mGalleryModelList.add(new MediaModel(url, false));
			}


			mVideoAdapter =  new GridViewAdapter(getActivity(), 0, mGalleryModelList, true);
			mVideoAdapter.videoFragment = this;
			mVideoGridView.setAdapter(mVideoAdapter);
			mVideoGridView.setOnScrollListener(this);
		}else{
			Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();

		}


		mVideoGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
				MediaModel galleryModel = (MediaModel) adapter.getItem(position);
				File file = new File(galleryModel.url);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "video/*");
				startActivity(intent);
				return false;
			}
		});

		mVideoGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// update the mStatus of each category in the adapter
				GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
				MediaModel galleryModel = (MediaModel) adapter.getItem(position);

				if(! galleryModel.status){
					long size = MediaChooserConstants.ChekcMediaFileSize(new File(galleryModel.url.toString()), true);
					if(size != 0){
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.file_size_exeeded) + "  " + MediaChooserConstants.SELECTED_VIDEO_SIZE_IN_MB + " " +  getActivity().getResources().getString(R.string.mb), Toast.LENGTH_SHORT).show();
						return;
					}

					if((MediaChooserConstants.MAX_MEDIA_LIMIT == MediaChooserConstants.SELECTED_MEDIA_COUNT)){
						if (MediaChooserConstants.SELECTED_MEDIA_COUNT < 2) {
							Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.max_limit_file) + "  " + MediaChooserConstants.SELECTED_MEDIA_COUNT + " " +  getActivity().getResources().getString(R.string.file), Toast.LENGTH_SHORT).show();
							return;
						} else {
							Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.max_limit_file) + "  " + MediaChooserConstants.SELECTED_MEDIA_COUNT + " " +  getActivity().getResources().getString(R.string.files), Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}

				// inverse the status
				galleryModel.status = ! galleryModel.status;
				adapter.notifyDataSetChanged();

				if (galleryModel.status) {
					mSelectedItems.add(galleryModel.url.toString());
					MediaChooserConstants.SELECTED_MEDIA_COUNT ++;

				}else{
					mSelectedItems.remove(galleryModel.url.toString().trim());
					MediaChooserConstants.SELECTED_MEDIA_COUNT --;
				}

				if (mCallback != null) {
					mCallback.onVideoSelected(mSelectedItems.size());
					Intent intent = new Intent();
					intent.putStringArrayListExtra("list", mSelectedItems);
					getActivity().setResult(Activity.RESULT_OK, intent);
				}

			}
		});

	}

	public void addItem(String item) {
		if(mVideoAdapter != null){
			MediaModel model = new MediaModel(item, false);
			mGalleryModelList.add(0, model);
			mVideoAdapter.notifyDataSetChanged();
		}else{
			initVideos();
		}
	}


	public GridViewAdapter getAdapter() {
		if (mVideoAdapter != null) {
			return mVideoAdapter;
		}
		return null;
	}

	public ArrayList<String> getSelectedVideoList() {
		return mSelectedItems;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//		if (view.getId() == android.R.id.list) {
		if (view == mVideoGridView) {
			// Set scrolling to true only if the user has flinged the
			// ListView away, hence we skip downloading a series
			// of unnecessary bitmaps that the user probably
			// just want to skip anyways. If we scroll slowly it
			// will still download bitmaps - that means
			// that the application won't wait for the user
			// to lift its finger off the screen in order to
			// download.
			if (scrollState == SCROLL_STATE_FLING) {
				//chk
			} else {
				mVideoAdapter.notifyDataSetChanged();
			}
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}


}

