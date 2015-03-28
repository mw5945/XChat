package com.wlm.util;

import java.sql.Date;
import java.text.SimpleDateFormat;



import android.R.bool;
import android.content.ContentProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;

public class UserInfoUtil {
	private Context mContext;
	private TelephonyManager mTelephonyManager;
	private SPUtil spUtil;
	public UserInfoUtil(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mTelephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		spUtil = new SPUtil(mContext);
	}

	public void InitUserInfo(UserInfo userInfo) {
		userInfo.CreateTime = System.currentTimeMillis();
		userInfo.PhoneNum = mTelephonyManager.getLine1Number();
		userInfo.AndroidID = android.provider.Settings.Secure.getString(
				mContext.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		userInfo.IMEI = mTelephonyManager.getDeviceId();
		userInfo.UID = genUID(userInfo);
		
		spUtil.SaveUserInfo(userInfo);
	}
	
	private String genUID(UserInfo userInfo) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(userInfo.CreateTime);
		String curTime = formatter.format(curDate);
		String UID = null;
		if(userInfo.PhoneNum != null) {
			UID = userInfo.PhoneNum + "_" + curTime;
		} else if (userInfo.AndroidID != null){
			UID = userInfo.AndroidID + "_" + curTime;
		} else if (userInfo.IMEI != null) {
			UID = userInfo.IMEI + "_" + curTime;
		}
		return UID;
	}
	
	public String getUID() {
		return spUtil.GetUID();
	}
	
	public UserInfo getuUserInfo() {
		return spUtil.GetUserInfo();
	}
}
