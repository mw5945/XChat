package com.wlm.rchat;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wlm.util.CSUtils;
import com.wlm.util.SPUtil;
import com.wlm.util.UserInfo;
import com.wlm.util.UserInfoUtil;
import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.websocket.OnErrorHandler;
import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import com.zvidia.pomelo.websocket.onDataCallBack;

import android.view.View.OnClickListener;

public class SexSettingDialog extends Dialog implements OnClickListener {

	Context context;
	SPUtil spUtil;
	UserInfo userInfo;
	UserInfoUtil userInfoUtil;
	Handler mHandler;
	ImageView male_selectIV;
	ImageView female_selectIV;
	RelativeLayout male_layout;
	RelativeLayout female_layout;
	protected PomeloClient mClient = null;

	final static int MSG_WHAT_SET_MALE = 1;
	final static int MSG_WHAT_SET_FEMALE = 2;
	final static int  UPDATE_SEX = 3;
	final static int UPDATE_UI = 10;
	final static int SERVER_ERROR = 9;
	int sex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sex_setting);
		userInfoUtil = new UserInfoUtil(context);
		userInfo = userInfoUtil.getuUserInfo();
		spUtil = new SPUtil(context);
//		Log.e("sex info", String.valueOf(userInfo.getSex()));
		initViews();
		initHandler();
	}

	public SexSettingDialog(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	protected SexSettingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.settings_male_rl:
			if (userInfo.getSex() == 0) {
				mHandler.sendEmptyMessage(MSG_WHAT_SET_MALE);
			}
			break;
		case R.id.settings_female_rl:
			if (userInfo.getSex() == 1) {
				mHandler.sendEmptyMessage(MSG_WHAT_SET_FEMALE);
			}
			break;
		default:
			break;
		}
	}

	void initViews() {
		// TODO Auto-generated method stub
		male_selectIV = (ImageView) findViewById(R.id.male_yes);
		female_selectIV = (ImageView) findViewById(R.id.female_yes);
		setSexSelect();
		male_layout = (RelativeLayout) findViewById(R.id.settings_male_rl);
		female_layout = (RelativeLayout) findViewById(R.id.settings_female_rl);
		male_layout.setOnClickListener(this);
		female_layout.setOnClickListener(this);
	}

	private void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_WHAT_SET_MALE:
					sex = 1;
					// setSexSelect();
					updateSexSet();
					break;
				case MSG_WHAT_SET_FEMALE:
					sex = 0;
					// setSexSelect();
					updateSexSet();
					break;
				case UPDATE_SEX:
					setSexSelect();
					break;
				default:
					break;
				}
			}
		};
	}

	void setSexSelect() {
		if (userInfo.getSex() == 0) {
			female_selectIV.setImageResource(R.drawable.select);
			male_selectIV.setImageResource(R.drawable.unselect);
		} else if (userInfo.getSex() == 1) {
			male_selectIV.setImageResource(R.drawable.select);
			female_selectIV.setImageResource(R.drawable.unselect);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
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
				((SettingsActivity) context).mHandler.sendEmptyMessage(SERVER_ERROR);
				dismiss();
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
			if (sex == 0) {
				avatar = "f_0";
			} else if (sex == 1) {
				avatar = "m_0";
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
								if (message.getInt("code") == 200) {
									spUtil.SaveUserInfoSex(sex);
									userInfo.setSex(sex);
									if (sex == 0) {
										spUtil.SaveUserInfoAvatar("f_0");
									} else if (sex == 1) {
										spUtil.SaveUserInfoAvatar("m_0");
									}
									mHandler.sendEmptyMessage(UPDATE_SEX);
									((SettingsActivity) context).mHandler.sendEmptyMessage(UPDATE_UI);
									CSUtils.getInstance(context).disconnect();
									dismiss();
								} else {
									((SettingsActivity) context).mHandler.sendEmptyMessage(SERVER_ERROR);
									dismiss();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});
//			dismiss();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PomeloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
