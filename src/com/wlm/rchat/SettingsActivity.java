package com.wlm.rchat;

import java.lang.reflect.Field;

import com.wlm.util.SPUtil;
import com.wlm.util.UserInfo;
import com.wlm.util.UserInfoUtil;
import com.wlm.widget.CircleImageView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {
	
	TextView sloganTv;
	ImageView sexIv;
//	CircleImageView headView;
	ImageView headView;
	RelativeLayout fbLayout;
	RelativeLayout aboutLayout;
	
	Dialog dialog;
	UserInfo userInfo;
	
	static final int UPDATE_UI = 10;
	static final int SERVER_ERROR = 9;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userInfoUtil = new UserInfoUtil(getApplicationContext());
		setContentView(R.layout.activity_settings);
		initView();
		initHandler();
	}

	void initView() {
//		headView = (CircleImageView) findViewById(R.id.setting_iv_head);
		headView = (ImageView) findViewById(R.id.setting_iv_head);
		headView.setOnClickListener(this);
		sexIv = (ImageView) findViewById(R.id.sex_iv);
		sexIv.setOnClickListener(this);
		sloganTv = (TextView) findViewById(R.id.sign_tv);
		sloganTv.setOnClickListener(this);
		fbLayout = (RelativeLayout)findViewById(R.id.setting_feedback_rl);
		fbLayout.setOnClickListener(this);
		aboutLayout = (RelativeLayout)findViewById(R.id.setting_about_rl);
		aboutLayout.setOnClickListener(this);
		
		dialog = new SexSettingDialog(this);
		dialog.setTitle("性别");
		Window dialogWindow = getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
	}
	
	protected void initHandler() {
	mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_UI:
				renewView();
				break;
			case SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "网络连接错误，请重试", Toast.LENGTH_LONG).show();
			default:
				break;
			}
		}
	};
	}
	
	public void renewView() {
		userInfo = userInfoUtil.getuUserInfo();
		sloganTv.setText(userInfo.getSlogan());
		if (userInfo.getSex() == 0) {
			sexIv.setImageResource(R.drawable.female_selected);
		} else {
			sexIv.setImageResource(R.drawable.male_selected);
		}
		String avatar = "c_"+userInfo.getAvatar();
		Field field;
		int resId = 0;
		try {
			field = R.drawable.class.getField(avatar);
			resId = field.getInt(new R.drawable());
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		headView.setImageResource(resId);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		renewView();
	}
	@Override
	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.sex_iv:
			dialog.show();
			break;
		case R.id.setting_iv_head:
			intent = new Intent();
			intent.setClass(this, AvatarActivity.class);
			startActivity(intent);
			break;
		case R.id.sign_tv:
			intent = new Intent();
			intent.setClass(this, SloganActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_feedback_rl:
			intent = new Intent();
			intent.setClass(this, FeedbackActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_about_rl:
			intent = new Intent();
			intent.setClass(this, AboutActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
