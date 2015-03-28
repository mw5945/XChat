package com.wlm.rchat;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.wlm.db.DBManager;
import com.wlm.db.TopicInfo;
import com.wlm.util.CSUtils;
import com.wlm.util.SPUtil;
import com.wlm.util.UserInfo;
import com.wlm.util.UserInfoUtil;
import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import com.zvidia.pomelo.websocket.onDataCallBack;

public class SplashActivity extends BaseActivity {

	private final static int MSG_WHAT_CHECKDATA_TIMEOUT = 1;
	private final static int MSG_WHAT_GOTOMAIN = 2;
	private final static int MSG_WHAT_GOTOGUIDE = 3;
	DBManager dbManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		dbManager = new DBManager(this);
		spUtil = new SPUtil(this);
		userInfoUtil = new UserInfoUtil(this);
		if (isFirstStart()) {
			initTopicDB();
			initProfile();
			updateUser();
			mHandler.sendEmptyMessageDelayed(MSG_WHAT_GOTOGUIDE, 1000);
		} else {
			if (!spUtil.GetUpdateState()) {
				updateUser();
			}
			mHandler.sendEmptyMessageDelayed(MSG_WHAT_GOTOMAIN, 1000);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void initTopicDB() {
		List<TopicInfo> topicInfos = new ArrayList<TopicInfo>();

		topicInfos.add(new TopicInfo("M0000001", "随机聊天", "topic_bg_random.png",
				"随机", 0, 1));
		topicInfos.add(new TopicInfo("M0000002", "真心话", "topic_bg_heart.png",
				"游戏", 0, 1));
		topicInfos.add(new TopicInfo("M0000003", "谈谈情", "topic_bg_wordlove.png",
				"爱爱", 0, 1));
		topicInfos.add(new TopicInfo("M0000004", "失眠", "topic_bg_insomnia.png",
				"睡觉", 0, 1));
		topicInfos.add(new TopicInfo("M0000007", "发泄", "topic_bg_abreact.png",
				"对骂", 0, 1));
		topicInfos.add(new TopicInfo("M0000006", "失恋疗伤",
				"topic_bg_lovelorn.png", "勾兑", 0, 1));

		dbManager.addTopic(topicInfos);
	}

	private void initProfile() {
		UserInfo userInfo = new UserInfo();
		userInfo.setSex(0);
		userInfo.setAvatar("m_0");
		userInfo.setSlogan("很高兴认识你！");
		userInfoUtil.InitUserInfo(userInfo);
	}

	private void updateUser() {
		OnHandshakeSuccessHandler onHandshakeSuccessHandler = new OnHandshakeSuccessHandler() {
			@Override
			public void onSuccess(PomeloClient _client, JSONObject resp) {
				registUser();
			}
		};
		// OnErrorHandler onErrorHandler = new OnErrorHandler() {
		//
		// @Override
		// public void onError(Exception e) {
		// // TODO Auto-generated method stub
		// Log.e("pomelo client", e.toString());
		// // CSUtils.getInstance(getApplicationContext()).disconnect();
		// }
		// };
		CSUtils.getInstance(getApplicationContext()).disconnect();
		mClient = CSUtils.getInstance(getApplicationContext()).getClient(true);
		mClient.setOnHandshakeSuccessHandler(onHandshakeSuccessHandler);
		// mClient.setOnErrorHandler(onErrorHandler);
		mClient.connect();
	}

	private void registUser() {
		JSONObject msg = new JSONObject();
		JSONObject registinfo = new JSONObject();
		UserInfo userInfo = userInfoUtil.getuUserInfo();
		try {
			registinfo.put("uid", userInfo.getUID());
			registinfo.put("sex", userInfo.getSex());
			registinfo.put("slogan", userInfo.getSlogan());
			registinfo.put("avatar", userInfo.getAvatar());
			if (userInfo.getPhoneNum() != null) {
				registinfo.put("phonenums", userInfo.getPhoneNum());
			}
			msg.put("registinfo", registinfo);
			msg.put("uid", userInfoUtil.getUID());

			mClient.request(CSUtils.ROUTE_REGIST_USER, msg,
					new onDataCallBack() {

						@Override
						public void responseData(JSONObject message) {
							// TODO Auto-generated method stub
//							Log.e("regist", message.toString());
							CSUtils.getInstance(getApplicationContext())
									.disconnect();
							spUtil.SaveUpdateState(true);
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PomeloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean isFirstStart() {
		int startTime = spUtil.StartTimeCount();
//		Log.e("Topic", String.valueOf(startTime));
		if (startTime == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void initHandler() {
		super.initHandler();

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_WHAT_CHECKDATA_TIMEOUT:
					break;
				case MSG_WHAT_GOTOMAIN:
					gotoActivity(new Intent(SplashActivity.this,
							TopicListActivity.class), R.anim.fadein,
							R.anim.fadeout);
//					 final String SER_KEY = "com.wlm.rchat.match.parinfo";
//					 final String TID_KEY = "com.wlm.rchat.match.tid";
//					 final String RID_KEY = "com.wlm.rchat.match.rid";
//					 final String BUNDLE_KEY = "com.wlm.rchat.match.bundle";
//					 UserInfo parInfo = new UserInfo();
//					 parInfo.setAndroidID("111");
//					 parInfo.setAvatar("m_0");
//					 parInfo.setSex(1);
//					 parInfo.setSlogan("ok");
//					 String tid = "random";
//					 Intent intent = new Intent();
//					 Bundle bundle = new Bundle();
//					 bundle.putSerializable(SER_KEY, parInfo);
//					 bundle.putString(TID_KEY, tid);
//					 intent.putExtra(BUNDLE_KEY, bundle);
//					 intent.setClass(SplashActivity.this, ChatActivity.class);
//					 gotoActivity(intent, R.anim.fadein,
//					 R.anim.fadeout);
					finish();
					break;
				case MSG_WHAT_GOTOGUIDE:
					gotoActivity(new Intent(SplashActivity.this,
							GuideActivity.class), R.anim.fadein, R.anim.fadeout);
					finish();
				default:
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
			return false;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		CSUtils.getInstance(getApplicationContext()).disconnect();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
