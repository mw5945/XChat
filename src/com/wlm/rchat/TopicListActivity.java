package com.wlm.rchat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toast;

import com.wlm.db.DBManager;
import com.wlm.db.TopicInfo;
import com.wlm.util.CSUtils;
import com.wlm.util.SPUtil;
import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.websocket.OnErrorHandler;
import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import com.zvidia.pomelo.websocket.onDataCallBack;

public class TopicListActivity extends BaseActivity {

	static final int UPDATE_UI = 0;
	static final int UPDATE_TOPIC = 1;
	int[] resId_tv = { R.id.topic_tv_random, R.id.topic_tv_0, R.id.topic_tv_1,
			R.id.topic_tv_2, R.id.topic_tv_3, R.id.topic_tv_4, };
	int[] resId_iv = { R.id.topic_iv_random, R.id.topic_iv_0, R.id.topic_iv_1,
			R.id.topic_iv_2, R.id.topic_iv_3, R.id.topic_iv_4, };
	TextView[] textViews;
	ImageView[] imageViews;
	SparseArray<String> tidhMap;
	List<TopicInfo> topicsInuse;
	ImageView setting_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		dbManager = new DBManager(this);
		spUtil = new SPUtil(this);
		updateTopic();
	}

	void updateTopic() {

		OnHandshakeSuccessHandler onHandshakeSuccessHandler = new OnHandshakeSuccessHandler() {
			@Override
			public void onSuccess(PomeloClient _client, JSONObject resp) {
				renewTopic();
			}
		};
		OnErrorHandler onErrorHandler = new OnErrorHandler() {

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
//				Log.e("pomelo client", e.toString());
//				 CSUtils.getInstance(getApplicationContext()).disconnect();
			}
		};
		CSUtils.getInstance(getApplicationContext()).disconnect();
		mClient = CSUtils.getInstance(getApplicationContext()).getClient(true);
		mClient.setOnHandshakeSuccessHandler(onHandshakeSuccessHandler);
		mClient.setOnErrorHandler(onErrorHandler);
		mClient.connect();
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
				default:
					break;
				}
			}
		};
	}

	void initViews() {
		setContentView(R.layout.activity_topiclist_bk);
		textViews = new TextView[resId_tv.length];
		for (int i = 0; i < textViews.length; i++) {
			textViews[i] = (TextView) findViewById(resId_tv[i]);
		}
		imageViews = new ImageView[resId_iv.length];
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i] = (ImageView) findViewById(resId_iv[i]);
			imageViews[i].setOnClickListener(this);
		}
		tidhMap = new SparseArray<String>();
		mHandler.sendEmptyMessage(UPDATE_UI);
		setting_btn = (ImageView) findViewById(R.id.topic_setting_btn);
		setting_btn.setOnClickListener(this);
	}

	private void renewView() {
		topicsInuse = dbManager.query();
//		Log.e("Topic", String.valueOf(topicsInuse.size()));
		tidhMap.clear();
		int i = 0;
		for (TopicInfo topicInfo : topicsInuse) {
//			Log.e("Topic", topicInfo.getName());
			textViews[i].setText(topicInfo.getName());
			// imageViews[i].
			try {
				String[] pics = topicInfo.getPic().split("\\.");
				Field field = R.drawable.class.getField(pics[0]);
				int resId = field.getInt(new R.drawable());
				imageViews[i].setImageResource(resId);
				tidhMap.put(imageViews[i].getId(), topicInfo.getTID());
				i++;
				// imageViews[i]
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

		}
	}

	private void renewTopic() {
		JSONObject msg = new JSONObject();
		try {
			msg.put("uid", userInfoUtil.getUID());
			msg.put("platform", "android");
			msg.put("version", 100);
			mClient.request(CSUtils.ROUTE_TOPIC_REQUEST, msg,
					new onDataCallBack() {

						@Override
						public void responseData(JSONObject message) {
							// TODO Auto-generated method stub
							CSUtils.getInstance(getApplicationContext())
									.disconnect();
//							Log.e("pomelo topic", message.toString());
							List<TopicInfo> topicInfos = null;
							try {
								topicInfos = ParseTopicMsg(message);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (topicInfos != null && !topicInfos.isEmpty()) {
								dbManager.clearTopics();
								dbManager.addTopic(topicInfos);
								// renew ui
								mHandler.sendEmptyMessage(UPDATE_UI);
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

	private List<TopicInfo> ParseTopicMsg(JSONObject msg) throws JSONException {
		ArrayList<TopicInfo> topicinfos = new ArrayList<TopicInfo>();
		// JSONObject bodyMsg= msg.getJSONObject("body");
		int code = msg.getInt("code");
		if (code == 200) {
			JSONArray topicArray = msg.getJSONArray("topics");
			for (int i = 0; i < topicArray.length(); ++i) {
				TopicInfo topicInfo = new TopicInfo();
				JSONObject topic = topicArray.getJSONObject(i);
				topicInfo.setTID(topic.getString("tid"));
				topicInfo.setName(topic.getString("name"));
				topicInfo.setPic(topic.getString("pic"));
				topicInfo.setDescription(topic.getString("description"));
				topicInfo.setPopulation(topic.getInt("population"));
				topicInfo.setStatus(topic.getInt("status"));
				topicinfos.add(topicInfo);
			}
		}
		return topicinfos;
	}

	private void sendTopicRequest(int topicId) {
		// send topic tid to server
		// mClient
		// turn to chat activity
		String mTid = tidhMap.get(topicId);
		// String mTid = "M0000001";
//		Toast.makeText(getApplicationContext(), mTid, Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		intent.putExtra(CSUtils.MATCHING_ROOM_INTENT, mTid);
		intent.setClass(this, MatchingActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.topic_iv_random:
		case R.id.topic_iv_0:
		case R.id.topic_iv_1:
		case R.id.topic_iv_2:
		case R.id.topic_iv_3:
		case R.id.topic_iv_4:
			sendTopicRequest(view.getId());
			break;
		case R.id.topic_setting_btn:
			Intent intent = new Intent();
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onPause() {
		CSUtils.getInstance(getApplicationContext()).disconnect();
		super.onPause();
	}
}
