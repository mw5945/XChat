package com.wlm.rchat;

import org.json.JSONException;
import org.json.JSONObject;

import com.wlm.util.CSUtils;
import com.wlm.util.SPUtil;
import com.wlm.util.UserInfo;
import com.wlm.util.UserInfoUtil;
import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.websocket.OnErrorHandler;
import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import com.zvidia.pomelo.websocket.onDataCallBack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SexSettingActivity extends BaseActivity {
	Button startButton;
	ImageView female_select;
	ImageView male_select;
	ImageView femaleView;
	ImageView maleView;
	
	boolean isFemale;
	Context context;
	UserInfo userInfo;
	int sex;

	final static int MSG_WHAT_CHANGE_UI_SEX = 0;
	final static int MSG_WHAT_REQUEST_SEX_INFO = 1;
	final static int MSG_WHAT_SERVER_ERROR = 2;
	final static int MSG_WHAT_GOTO_MAIN = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		isFemale = true;
		sex = 0;
		userInfo = userInfoUtil.getuUserInfo();
		spUtil = new SPUtil(this);
		initView();
		initHandler();
	}

	private void initView() {
		setContentView(R.layout.activity_sex_setting);
		female_select = (ImageView) findViewById(R.id.female_select);
		male_select = (ImageView) findViewById(R.id.male_select);
		femaleView = (ImageView) findViewById(R.id.female);
		maleView = (ImageView) findViewById(R.id.male);
		startButton = (Button) findViewById(R.id.start_button);
		female_select.setOnClickListener(this);
		male_select.setOnClickListener(this);
		femaleView.setOnClickListener(this);
		maleView.setOnClickListener(this);
		startButton.setOnClickListener(this);
		
	}

	protected void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_WHAT_CHANGE_UI_SEX:
					if (isFemale) {
						female_select.setImageResource(R.drawable.duigou_down);
						male_select.setImageResource(R.drawable.duigou_nor);
					} else {
						male_select.setImageResource(R.drawable.duigou_down);
						female_select.setImageResource(R.drawable.duigou_nor);
					}
					break;
				case MSG_WHAT_REQUEST_SEX_INFO:
					updateSexSet();
					break;
				case MSG_WHAT_SERVER_ERROR:
					Toast.makeText(getApplicationContext(), "服务器连接错误",
							Toast.LENGTH_LONG).show();
					break;
				case MSG_WHAT_GOTO_MAIN:
					gotoActivity(new Intent(SexSettingActivity.this,
							TopicListActivity.class), R.anim.fadein,
							R.anim.fadeout);
					finish();
					break;
				default:
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.female:
		case R.id.female_select:
			isFemale = true;
			mHandler.sendEmptyMessage(MSG_WHAT_CHANGE_UI_SEX);
			break;
		case R.id.male:
		case R.id.male_select:
			isFemale = false;
			mHandler.sendEmptyMessage(MSG_WHAT_CHANGE_UI_SEX);
			break;
		case R.id.start_button:
			mHandler.sendEmptyMessage(MSG_WHAT_REQUEST_SEX_INFO);
			break;
		default:
			break;
		}
	}

	void updateSexSet() {
		CSUtils.getInstance(context).disconnect();
		OnHandshakeSuccessHandler onHandshakeSuccessHandler = new OnHandshakeSuccessHandler() {
			@Override
			public void onSuccess(PomeloClient client, JSONObject resp) {
				// TODO Auto-generated method stub
				requestSexSet();
			}
		};
		OnErrorHandler onErrorHandler = new OnErrorHandler() {
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				System.out.println(e.toString());
				mHandler.sendEmptyMessage(MSG_WHAT_SERVER_ERROR);
			}
		};
		mClient = CSUtils.getInstance(context).getClient(true);
		mClient.setOnHandshakeSuccessHandler(onHandshakeSuccessHandler);
		mClient.setOnErrorHandler(onErrorHandler);
		mClient.connect();
	}

	void requestSexSet() {
		try {
			String avatar = null;
			if (isFemale) {
				avatar = "f_0";
				sex = 0;
			} else if (!isFemale) {
				avatar = "m_0";
				sex = 1;
			}
			JSONObject updatemsg = new JSONObject();
			updatemsg.put("uid", userInfo.getUID());
			JSONObject updateinfos = new JSONObject();
			updateinfos.put("sex", sex);
			updateinfos.put("avatar", avatar);
			updatemsg.put("updateinfos", updateinfos);
			mClient.request(CSUtils.UPDATE_USERINFO, updatemsg,
					new onDataCallBack() {

						@Override
						public void responseData(JSONObject message) {
							// TODO Auto-generated method stub
							try {
//								Log.e("user info sex",message.toString());
//								Log.e("user info sex",message.getInt("code")+"");
								if (message.getInt("code") == 200) {
									spUtil.SaveUserInfoSex(sex);
									userInfo.setSex(sex);
									if (sex == 0) {
										spUtil.SaveUserInfoAvatar("f_0");
									} else if (sex == 1) {
										spUtil.SaveUserInfoAvatar("m_0");
									}
									CSUtils.getInstance(context).disconnect();
									mHandler.sendEmptyMessage(MSG_WHAT_GOTO_MAIN);
								} else {
									System.out.println("connect failed");
									CSUtils.getInstance(context).disconnect();
									mHandler.sendEmptyMessage(MSG_WHAT_SERVER_ERROR);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
