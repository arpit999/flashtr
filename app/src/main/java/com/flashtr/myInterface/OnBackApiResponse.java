package com.flashtr.myInterface;
public interface OnBackApiResponse {
	public void setBackApiResponse(String result, int ApiId);
	public void setBackApiResponse(String result, int APIID, int pos);
	public void setBackApiResponse(String result, int APIID, String userType);
	public void setBackApiResponse(String result, int APIID, int pos, String userType);
}
