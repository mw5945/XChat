package com.wlm.rchat;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wlm.util.CSUtils;
import com.wlm.util.SPUtil;
import com.wlm.util.UserInfo;
import com.wlm.util.UserInfoUtil;
import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import com.zvidia.pomelo.websocket.onDataCallBack;

public class FeedbackActivity extends BaseActivity {
	private EditText edit_text;
	private TextView text_view;
	private TextView save_btn;
	private int BigIndex = 50;
	private UserInfo userinfo;
	private ImageView backBtn;

	final static int MSG_WHAT_REQUEST_FEEDBACK = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		userInfoUtil = new UserInfoUtil(getApplicationContext());
		userinfo = userInfoUtil.getuUserInfo();
		spUtil = new SPUtil(getApplicationContext());
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		save_btn = (TextView) findViewById(R.id.feedback_save);
		save_btn.setOnClickListener(this);
		edit_text = (EditText) findViewById(R.id.feedback_edit);
		text_view = (TextView) findViewById(R.id.feedback_charcount);
		text_view.setText("0");
		edit_text.addTextChangedListener(new EditTextWatcher());
		backBtn = (ImageView) findViewById(R.id.feedback_backbt);
		backBtn.setOnClickListener(this);
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
				case MSG_WHAT_REQUEST_FEEDBACK:
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
		case R.id.feedback_save:
			Message msg = new Message();
			msg.what = MSG_WHAT_REQUEST_FEEDBACK;
			msg.obj = edit_text.getText().toString();
			mHandler.sendMessage(msg);
			break;
		case R.id.feedback_backbt:
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
				requestFeedback(content);
			}
		};
		CSUtils.getInstance(getApplicationContext()).disconnect();
		mClient.setOnHandshakeSuccessHandler(onHandshakeSuccessHandler);
		mClient.connect();
	}

	void requestFeedback(final String conent) {
		JSONObject updatemsg = new JSONObject();
		try {
			updatemsg.put("uid", userinfo.getUID());
			JSONObject updateinfos = new JSONObject();
			updateinfos.put("feedback", conent);
			updatemsg.put("updateinfos", updateinfos);
			mClient.request(CSUtils.ROUTE_FEEDBACK, updatemsg,
					new onDataCallBack() {

						@Override
						public void responseData(JSONObject message) {
							// TODO Auto-generated method stub
							try {
								if (message.getInt("code") == 200) {
									// Toast.makeText(getApplicationContext(),
									// "谢谢您的反馈，我们将尽快联系您！",
									// Toast.LENGTH_LONG).show();
									finish();
								} else {
									Toast.makeText(getApplicationContext(),
											"服务器错误，请重试", Toast.LENGTH_LONG)
											.show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								CSUtils.getInstance(getApplicationContext())
										.disconnect();
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
