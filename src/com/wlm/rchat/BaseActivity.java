package com.wlm.rchat;

import com.wlm.db.DBManager;
import com.wlm.util.SPUtil;
import com.wlm.util.UserInfoUtil;
import com.zvidia.pomelo.websocket.PomeloClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseActivity extends Activity implements OnClickListener{
	protected PomeloClient mClient = null;
	protected Handler mHandler = null;
	DBManager dbManager;
	SPUtil spUtil;
	UserInfoUtil userInfoUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userInfoUtil = new UserInfoUtil(this);
		initHandler();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	protected void initHandler() {
		// TODO Auto-generated method stub
		
	}
	
	protected void gotoActivity(Intent intent, int animIn, int animOut) {
		this.startActivity(intent);
		overridePendingTransition(animIn, animOut);
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}
