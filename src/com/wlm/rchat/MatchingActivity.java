package com.wlm.rchat;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.wlm.util.CSUtils;
import com.wlm.util.UserInfo;
import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import com.zvidia.pomelo.websocket.onDataCallBack;

public class MatchingActivity extends BaseActivity {
	/* View */
	// View mCancleBtn;

	/* Chat */
	UserInfo parInfo;
	String tid;
	String rid = null;

	/* Handler Message */
	final static int MSG_WHAT_MATCHING_REQUEST = 1;
	final static int MSG_WHAT_SEND_REQUEST = 2;
	final static int MSG_WHAT_CHAT_VIEW = 3;
	final static int MSG_WHAT_MATCHING_VIEW = 4;
	final static int MSG_WHAT_MATCHING_TIMEOUT = 10;
	final static int MSG_WHAT_GET_ROOMID = 11;
	final static int MSG_WHAT_XIA_REMOVE = 9;
	final static int SECOND = 1000;

	/* Serial */
	public final static String SER_KEY = "com.wlm.rchat.match.parinfo";
	public final static String TID_KEY = "com.wlm.rchat.match.tid";
	public final static String RID_KEY = "com.wlm.rchat.match.rid";
	public final static String BUNDLE_KEY = "com.wlm.rchat.match.bundle";
	/* Network */
	String host;
	int port;

	/* LOG */
	final static String LISTEN_ADDON_TAG = "xl_add_on";
	final static String LISTEN_LEAVEON_TAG = "xl_leave_on";
	final static String LISTEN_ONCOMMING_TAG = "xl_comming";
	final static String LISTEN_ONQUIT_TAG = "xl_quit";
	final static String LISTEN_ONCHAT_TAG = "xl_onchat";
	final static String LISTEN_ENTER_TAG = "xl_enter";
	final static String LISTEN_CHATSEND_TAG = "xl_chatsend";

	ImageView xia;
	int width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent != null) {
			tid = intent.getStringExtra(CSUtils.MATCHING_ROOM_INTENT);
			if (tid != null) {
				initViews();
				sendMatchRequest();
//				mHandler.sendEmptyMessageDelayed(MSG_WHAT_MATCHING_TIMEOUT,
//						15 * SECOND);
			} else {
				onBackPressed();
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		Intent intent = getIntent();
		setIntent(intent);
		if (intent != null) {
			tid = intent.getStringExtra(CSUtils.MATCHING_ROOM_INTENT);
//			Log.e("matching tid", tid);
			if (tid != null) {
				initViews();
				sendMatchRequest();
//				mHandler.sendEmptyMessageDelayed(MSG_WHAT_MATCHING_TIMEOUT,
//						15 * SECOND);
			} else {
				onBackPressed();
			}
		}
		super.onNewIntent(intent);
	}

	void sendMatchRequest() {
		OnHandshakeSuccessHandler onHandshakeSuccessHandler = new OnHandshakeSuccessHandler() {
			@Override
			public void onSuccess(PomeloClient _client, JSONObject resp) {
				initRoomRequest();
			}
		};
		mClient = CSUtils.getInstance(getApplicationContext()).getClient(true);
		mClient.setOnHandshakeSuccessHandler(onHandshakeSuccessHandler);
		mClient.connect();
	}

	void initViews() {
		setContentView(R.layout.activity_matching);
		WindowManager wm = this.getWindowManager();
		width = wm.getDefaultDisplay().getWidth();
		// xia
		xia = (ImageView) findViewById(R.id.match_xia);
		Animation translateAnimation = new TranslateAnimation(width / 2 + 150,
				0, 0, 0);
		translateAnimation.setDuration(1500);
		Animation updownAnimation = AnimationUtils.loadAnimation(
				MatchingActivity.this, R.anim.translate_xia_updown_anim);
		updownAnimation.setStartTime(2000);
		AnimationSet set = new AnimationSet(true);
		set.addAnimation(translateAnimation);
		set.addAnimation(updownAnimation);
		xia.startAnimation(set);
		// wave
		ImageView waveImageViewl = (ImageView) findViewById(R.id.match_wave_l);
		Animation waveAnimation = AnimationUtils.loadAnimation(
				MatchingActivity.this, R.anim.translate_wave_l_anim);
		waveImageViewl.startAnimation(waveAnimation);
		ImageView waveImageViewr = (ImageView) findViewById(R.id.match_wave_r);
		Animation waveAnimationr = AnimationUtils.loadAnimation(
				MatchingActivity.this, R.anim.translate_wave_r_anim);
		waveImageViewr.startAnimation(waveAnimationr);
		// buble
		ImageView buble = (ImageView) findViewById(R.id.match_buble);
		int offset = buble.getWidth();
		Animation bubleTranAnimation = new TranslateAnimation(
				-(width + offset), (width + offset), 0, 0);
		bubleTranAnimation.setDuration(3000);
		bubleTranAnimation.setRepeatCount(Animation.INFINITE);
		bubleTranAnimation.setRepeatMode(Animation.RESTART);
		Animation bublescaleAnimation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.translate_scale_buble_anim);
		AnimationSet bubleset = new AnimationSet(true);
		bubleset.addAnimation(bublescaleAnimation);
		bubleset.addAnimation(bubleTranAnimation);
		buble.startAnimation(bubleset);
	}

	protected void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_WHAT_MATCHING_TIMEOUT:
					CSUtils.getInstance(getApplicationContext()).disconnect();
					Toast.makeText(getApplicationContext(), "匹配失败，换个话题试试~",
							Toast.LENGTH_LONG).show();
					onBackPressed();
					break;
				case MSG_WHAT_CHAT_VIEW:
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable(SER_KEY, parInfo);
					bundle.putString(TID_KEY, tid);
					intent.putExtra(BUNDLE_KEY, bundle);
					intent.setClass(MatchingActivity.this, ChatActivity.class);
					startActivity(intent);
					SoundPool soundPool;
					soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
					soundPool.load(getApplicationContext(), R.raw.matched,1);
//					SoundPool.setVolume(port, port, port);
					soundPool.play(1,(float)0.3, (float)0.3, 0, 0, 1);
//					Toast.makeText(getApplicationContext(),
//							"onComming" + parInfo.getUID(), Toast.LENGTH_SHORT)
//							.show();
					finish();
					break;
				case MSG_WHAT_GET_ROOMID:
//					Toast.makeText(getApplicationContext(), rid,
//							Toast.LENGTH_LONG).show();
					break;
				case MSG_WHAT_XIA_REMOVE:
					Animation translateAnimation = new TranslateAnimation(0,
							-(width / 2 + 150), 0, 0);
					translateAnimation.setDuration(1000);
					xia.setAnimation(translateAnimation);
					break;
				default:
					break;
				}
			}
		};
	}

	void initRoomRequest() {
		try {
			JSONObject msg = new JSONObject();
			msg.put("uid", userInfoUtil.getUID());
			mClient.request(CSUtils.ROUTE_GATE, msg, new onDataCallBack() {
				@Override
				public void responseData(JSONObject msg) {
					CSUtils.getInstance(getApplicationContext()).disconnect();
					try {
						host = msg
								.getString(PomeloClient.HANDSHAKE_RES_HOST_KEY);
						port = msg.getInt(PomeloClient.HANDSHAKE_RES_PORT_KEY);
						mClient = CSUtils.getInstance(getApplicationContext())
								.newClient(host, port);
						mClient.setOnHandshakeSuccessHandler(onHandshakeSuccessHandlerConnect);
						mClient.connect();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (PomeloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	OnHandshakeSuccessHandler onHandshakeSuccessHandlerConnect = new OnHandshakeSuccessHandler() {
		@Override
		public void onSuccess(PomeloClient _client, JSONObject resp) {
			initListener();
			sendConnectRequest();
		}
	};

	void sendConnectRequest() {
		JSONObject msg = new JSONObject();
		try {
			msg.put("uid", userInfoUtil.getUID());
			msg.put("mark", tid);
			// msg.put("tid", tid);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mClient.request(CSUtils.ROUTE_ENTER, msg, new onDataCallBack() {
				@Override
				public void responseData(JSONObject message) {
					try {
						rid = message.getString("rid");
						mHandler.sendEmptyMessage(MSG_WHAT_GET_ROOMID);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (PomeloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void initListener() {
		mClient.on(CSUtils.EVENT_ONCOMINGINFO, new onDataCallBack() {

			@Override
			public void responseData(JSONObject msg) {
				// TODO Auto-generated method stub
				try {
					JSONObject message = msg.getJSONObject("msg");
					if (message.getInt("code") == 200) {
//						Log.e(CSUtils.EVENT_ONCOMINGINFO, message.toString());
						JSONObject result = message.getJSONObject("result");
						if (result != null) {
							parInfo = new UserInfo();
							parInfo.setUID(result.getString("uid"));
							parInfo.setSex(result.getInt("sex"));
							parInfo.setAvatar(result.getString("avatar"));
							parInfo.setSlogan(result.getString("slogan"));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (parInfo.getUID() != null) {
						// set new ui
//						mHandler.removeMessages(MSG_WHAT_MATCHING_TIMEOUT);
						mHandler.sendEmptyMessage(MSG_WHAT_XIA_REMOVE);
						mHandler.sendEmptyMessageDelayed(MSG_WHAT_CHAT_VIEW,
								SECOND);
					}
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		mHandler.removeMessages(MSG_WHAT_MATCHING_TIMEOUT);
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
	}

	@Override
	public void onClick(View view) {
		// switch (view.getId()) {
		// case R.id.cancel_btn: {
		// onBackPressed();
		// break;
		// }
		// default:
		// break;
		// }
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		CSUtils.getInstance(getApplicationContext()).disconnect();
		super.onBackPressed();
	}
}
