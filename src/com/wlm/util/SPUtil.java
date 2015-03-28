package com.wlm.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SPUtil {
	private Context mContext;
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;

	public SPUtil(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mSharedPreferences = mContext.getSharedPreferences("userprofile",
				Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}

	public UserInfo GetUserInfo() {
		UserInfo userInfo = new UserInfo();
		// User Info
		userInfo.UID = mSharedPreferences.getString("com.wlm.UID", null);
		userInfo.CreateTime = mSharedPreferences.getLong("com.wlm.CreateTime",
				0);
		userInfo.PhoneNum = mSharedPreferences.getString("com.wlm.PhoneNum",
				null);
		userInfo.IMEI = mSharedPreferences.getString("com.wlm.IMEI", null);
		userInfo.AndroidID = mSharedPreferences.getString("com.wlm.AndroidID",
				null);

		userInfo.Sex = mSharedPreferences.getInt("com.wlm.Sex", 0);
		userInfo.Avatar = mSharedPreferences.getString("com.wlm.Avatar", null);
		userInfo.Slogan = mSharedPreferences.getString("com.wlm.Slogan", null);

		return userInfo;
	}

	String GetUID() {
		return mSharedPreferences.getString("com.wlm.UID", null);
	}
	
	public int StartTimeCount() {
		int startTime = mSharedPreferences.getInt("com.wlm.starttime", 0);
		mEditor.putInt("com.wlm.starttime", ++startTime);
		mEditor.commit();
		return startTime;
	}

	public void SaveUserInfo(UserInfo userInfo) {
		// User Info
		mEditor.putString("com.wlm.UID", userInfo.UID);
		mEditor.putLong("com.wlm.CreateTime", userInfo.CreateTime);
		mEditor.putString("com.wlm.PhoneNum", userInfo.PhoneNum);
		mEditor.putString("com.wlm.IMEI", userInfo.IMEI);
		mEditor.putString("com.wlm.AndroidID", userInfo.AndroidID);

		mEditor.putInt("com.wlm.Sex", userInfo.Sex);
		mEditor.putString("com.wlm.Avatar", userInfo.Avatar);
		mEditor.putString("com.wlm.Slogan", userInfo.Slogan);

		mEditor.commit();
	}

	public void SaveUserInfoSex(int sex) {
		mEditor.putInt("com.wlm.Sex", sex);
		mEditor.commit();
	}

	public void SaveUserInfoAvatar(String avatar) {
		mEditor.putString("com.wlm.Avatar", avatar);
		mEditor.commit();
	}

	public void SaveUserInfoSlogan(String slogan) {
		mEditor.putString("com.wlm.Slogan", slogan);
		mEditor.commit();
	}
	
	public void  SaveUpdateState(boolean isUpdated) {
		mEditor.putBoolean("com.wlm.Update", isUpdated);
		mEditor.commit();
	}
	
	public boolean GetUpdateState() {
		return mSharedPreferences.getBoolean("com.wlm.Update", false);
	}
}
