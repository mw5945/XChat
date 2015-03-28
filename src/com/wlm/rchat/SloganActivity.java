package com.wlm.rchat;

import org.json.JSONException;
import org.json.JSONObject;
import com.wlm.util.CSUtils;
import com.wlm.util.SPUtil;
import com.wlm.util.UserInfo;
import com.wlm.util.UserInfoUtil;
import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import com.zvidia.pomelo.websocket.onDataCallBack;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SloganActivity extends BaseActivity {
	private EditText edit_text;
	private TextView text_view;
	private TextView save_btn;
	private int BigIndex = 30;
	private UserInfo userinfo;
	private ImageView back_btn;
	final static int MSG_WHAT_REQUEST_SIGN = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_setting);
		userInfoUtil = new UserInfoUtil(getApplicationContext());
		userinfo = userInfoUtil.getuUserInfo();
		spUtil = new SPUtil(getApplicationContext());
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		save_btn = (TextView) findViewById(R.id.sign_save);
		save_btn.setOnClickListener(this);
		edit_text = (EditText) findViewById(R.id.sign_edit);
		text_view = (TextView) findViewById(R.id.sign_charcount);
		String content = userinfo.getSlogan();
		if (null == content) {
			text_view.setText("0");
		} else {
			text_view.setText(content.length() + "");
			edit_text.setText(content);
		}
		edit_text.addTextChangedListener(new EditTextWatcher());
		back_btn = (ImageView) findViewById(R.id.sign_backbt);
		back_btn.setOnClickListener(this);
	}

	@Override
	protected void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_WHAT_REQUEST_SIGN:
					String content = (String) msg.obj;
					updateSign(content);
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
		case R.id.sign_save:
			Message msg = new Message();
			msg.what = MSG_WHAT_REQUEST_SIGN;
			msg.obj = edit_text.getText().toString();
			mHandler.sendMessage(msg);
			break;
		case R.id.sign_backbt:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	void updateSign(final String content) {
		mClient = CSUtils.getInstance(getApplicationContext()).getClient(true);
		OnHandshakeSuccessHandler onHandshakeSuccessHandler = new OnHandshakeSuccessHandler() {
			@Override
			public void onSuccess(PomeloClient _client, JSONObject resp) {
				requestSign(content);
			}
		};
		CSUtils.getInstance(getApplicationContext()).disconnect();
		mClient.setOnHandshakeSuccessHandler(onHandshakeSuccessHandler);
		mClient.connect();
	}
	
	void requestSign(final String conent) {
		JSONObject updatemsg = new JSONObject();
		try {
			updatemsg.put("uid", userinfo.getUID());
			JSONObject updateinfos = new JSONObject();
			updateinfos.put("slogan", conent);
			updatemsg.put("updateinfos", updateinfos);
			mClient.request(CSUtils.UPDATE_USERINFO, updatemsg,
					new onDataCallBack() {

						@Override
						public void responseData(JSONObject message) {
							// TODO Auto-generated method stub
							try {
								if (message.getInt("code") == 200) {
									spUtil.SaveUserInfoSlogan(conent);
									CSUtils.getInstance(getApplicationContext())
											.disconnect();
									finish();
								} else {
									Toast.makeText(getApplicationContext(),
											"服务器保存失败,请重试", Toast.LENGTH_LONG)
											.show();
								}
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

	public class EditTextWatcher implements TextWatcher {

		public void afterTextChanged(Editable arg0) {
			String edit = edit_text.getText().toString();

			edit_text.setVisibility(View.VISIBLE);
			if (edit.length() <= BigIndex) {
				text_view.setText("" + (BigIndex - edit.length()));
			} else {
				edit_text.setText(edit.substring(0, BigIndex));
				edit_text.setSelection(edit.substring(0, BigIndex).length());
			}
		}

		public void beforeTextChanged(CharSequence cs, int arg1, int arg2,
				int arg3) {
		}

		public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		}

	}

}
