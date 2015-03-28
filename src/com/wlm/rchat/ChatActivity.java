package com.wlm.rchat;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconTextView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Field;

import org.java_websocket.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wlm.db.DBManager;
import com.wlm.db.TopicInfo;
import com.wlm.util.CSUtils;
import com.wlm.util.UserInfo;
import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.websocket.onDataCallBack;

public class ChatActivity extends BaseActivity {
	// view
	private View mSendBtn;
	private EmojiconEditText mMsgInput;
	private LinearLayout mChatContentsView;
	private ScrollView mChatContentsScrollView;

	private TextView mParSignTextView;
	private ImageView mPheadImageView;

	private View mBackView;
	private View mChangeParView;

	private ImageButton mSmileBtn;
	PopupWindow popup;
	boolean isKeyboardShowing = false;
	// private RelativeLayout faceLayout;

	final static int MSG_WHAT_NEWMSG_FROM_PAR = 5;
	final static int MSG_WHAT_NEWMSG_FROM_SELF = 6;
	final static int MSG_WHAT_NEWMSG_FROM_SYS = 7;
	final static int MSG_WHAT_NEWMSG_FROM_INFO = 8;
	final static int MSG_WHAT_PAR_QUIT = 9;

	final static String LISTEN_CHATSEND_TAG = "xl_chatsend";
	// chat info
	UserInfo parInfo;
	UserInfo userInfo;
	TopicInfo topicInfo;
	String rid;
	String tid;
	boolean isExpanded = false;
	View rootview;
	int userAvatarResid;
	int parAavatarResid;

	public enum MsgType {
		SELF_MSG, PAR_MSG, INFORM_MSG, OFFICIAL_MSG,
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		dbManager = new DBManager(this);
		if (intent != null) {
			Bundle bundle = intent.getBundleExtra(MatchingActivity.BUNDLE_KEY);
			parInfo = (UserInfo) bundle
					.getSerializable(MatchingActivity.SER_KEY);
			tid = bundle.getString(MatchingActivity.TID_KEY);
			topicInfo = dbManager.queryTid(parInfo.getTopic());
			userInfo = userInfoUtil.getuUserInfo();
			mClient = CSUtils.getInstance(getApplicationContext()).getClient(false);
			initListener();
			initViews();
			if (topicInfo != null && topicInfo.getName() != null) {
				addMessageToScroll(MsgType.INFORM_MSG,
						"对方来自“" + topicInfo.getName() + "”话题");
			}
		} else {
			onBackPressed();
		}
	}

	@Override
	protected void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_WHAT_NEWMSG_FROM_PAR:
					if (null != msg.obj) {
						String content = (String) msg.obj;
						addMessageToScroll(MsgType.PAR_MSG, content);
					}
					break;
				case MSG_WHAT_NEWMSG_FROM_SELF:
					if (null != msg.obj) {
						String content = (String) msg.obj;
						addMessageToScroll(MsgType.SELF_MSG, content);
					}
					break;
				case MSG_WHAT_PAR_QUIT:
					String content = "对方已退出，重新匹配。";
					addMessageToScroll(MsgType.INFORM_MSG, content);
					reMatch();
					break;

				}
			}
		};
	}

	void initViews() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_chat);
		mChatContentsView = (LinearLayout) findViewById(R.id.chat_contents_layout);
		mChatContentsView.setOnClickListener(this);
		mChatContentsScrollView = (ScrollView) findViewById(R.id.chat_contents_scroll);
		mSendBtn = (View) findViewById(R.id.send_btn);
		mSendBtn.setOnClickListener(this);
		mMsgInput = (EmojiconEditText) findViewById(R.id.editEmojicon);
		mParSignTextView = (TextView) findViewById(R.id.par_sign_tv);
		mParSignTextView.setText(parInfo.getSlogan());

		mBackView = (View) findViewById(R.id.back_btn);
		mBackView.setOnClickListener(this);
		mChangeParView = (View) findViewById(R.id.change_btn);
		mChangeParView.setOnClickListener(this);

		mSmileBtn = (ImageButton) findViewById(R.id.smile_btn);
		mSmileBtn.setOnClickListener(this);
		// faceLayout=(RelativeLayout)findViewById(R.id.faceLayout);

		String parAvatar = "c_" + parInfo.getAvatar();
		String userAvatar = "c_" + userInfo.getAvatar();
		Field field;
		try {
			field = R.drawable.class.getField(parAvatar);
			parAavatarResid = field.getInt(new R.drawable());
			field = R.drawable.class.getField(userAvatar);
			userAvatarResid = field.getInt(new R.drawable());
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
		mPheadImageView = (ImageView) findViewById(R.id.par_icon);
		mPheadImageView.setImageResource(parAavatarResid);
		// Give the topmost view of your activity layout hierarchy. This will be
		// used to measure soft keyboard height
		rootview = findViewById(R.id.chat_layout);
		rootview.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						int diff = rootview.getRootView().getHeight()
								- rootview.getHeight();
						if (diff > 100) {
							isKeyboardShowing = true;
						} else {
							isKeyboardShowing = false;
						}
					}

				});
		popup = new EmojiconsPopup(rootview, this);
		// Will automatically set size according to the soft keyboard size
		((EmojiconsPopup) popup).setSizeForSoftKeyboard();

		// Set on emojicon click listener
		((EmojiconsPopup) popup)
				.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

					@Override
					public void onEmojiconClicked(
							github.ankushsachdeva.emojicon.emoji.Emojicon emojicon) {
						// TODO Auto-generated method stub
						mMsgInput.append(emojicon.getEmoji());
					}
				});

		// Set on backspace click listener
		((EmojiconsPopup) popup)
				.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

					@Override
					public void onEmojiconBackspaceClicked(View v) {
						KeyEvent event = new KeyEvent(0, 0, 0,
								KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
								KeyEvent.KEYCODE_ENDCALL);
						mMsgInput.dispatchKeyEvent(event);
					}
				});

		// Set listener for keyboard open/close
		((EmojiconsPopup) popup)
				.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

					@Override
					public void onKeyboardOpen(int keyBoardHeight) {
					}

					@Override
					public void onKeyboardClose() {
						if (popup.isShowing())
							popup.dismiss();
					}
				});
		// if (findViewById(R.id.emojicons) != null) {
		// EmojiconsFragment emojiconsFragment = new EmojiconsFragment();
		// getFragmentManager().beginTransaction().add(R.id.emojicons,
		// emojiconsFragment).commit();
		// }

		// tabWidget=(TabWidget)findViewById(android.R.id.tabs);
		// tabHost=(TabHost)findViewById(android.R.id.tabhost);
	}

	void initListener() {
		// when user get away from the matching activity, resend the request
		mClient.on(CSUtils.EVENT_ONCHAT, new onDataCallBack() {

			@Override
			public void responseData(JSONObject message) {
				// TODO Auto-generated method stub
				try {
					String content = message.getString("msg");
					String from = message.getString("from");
					if (from.trim().equals(parInfo.getUID())) {
						mHandler.sendMessage(mHandler.obtainMessage(
								MSG_WHAT_NEWMSG_FROM_PAR, content));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		mClient.on(CSUtils.EVENT_ONLEAVE, new onDataCallBack() {
			@Override
			public void responseData(JSONObject message) {
				// TODO Auto-generated method stub
				try {
					// JSONObject bodymsg = message.getJSONObject("body");
					String userString = message.getString("user");
//					Log.e("ONQUIT-" + userInfoUtil.getUID(), userString);
//					Log.e(userString, parInfo.getUID());
					if (parInfo.getUID() != null
							&& userString.equals(parInfo.getUID())) {
						mHandler.sendEmptyMessage(MSG_WHAT_PAR_QUIT);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		mClient.on(CSUtils.EVENT_ONADD, new onDataCallBack() {
			@Override
			public void responseData(JSONObject message) {
				// TODO Auto-generated method stub
				try {
					JSONObject bodymsg = message.getJSONObject("body");
					String userString = bodymsg.getString("user");
//					Log.e(userInfoUtil.getUID(), userString);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	void sendMessage(final String content) throws JSONException {
		JSONObject chat_msg = new JSONObject();
		chat_msg.put("from", userInfo.getUID());
		String cotent_encode = Base64.encodeBytes(content.getBytes());
		chat_msg.put("content", cotent_encode);
		chat_msg.put("target", parInfo.getUID());
		chat_msg.put("rid", rid);
		try {
			mClient.request(CSUtils.ROUTE_CHAT_SEND, chat_msg,
					new onDataCallBack() {
						@Override
						public void responseData(JSONObject message) {
							// TODO Auto-generated method stub
							// parse the message to make sure the server
							// response
//							Log.e(LISTEN_CHATSEND_TAG, message.toString());
							if (true) {
								StringBuilder failedMsg = new StringBuilder();
								if (content.length() > 8) {
									failedMsg.append(content.substring(0, 8));
									failedMsg.append("...");
								} else {
									failedMsg.append(content);
								}
								failedMsg.append("发送失败");
								mHandler.sendMessage(mHandler.obtainMessage(
										MSG_WHAT_NEWMSG_FROM_INFO,
										failedMsg.toString()));
							}
						}
					});
		} catch (PomeloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void addMessageToScroll(MsgType msgtype, String msg) {
		RelativeLayout ll = null;
		ImageView headTextView = null;
		switch (msgtype) {
		case SELF_MSG:
			ll = (RelativeLayout) LayoutInflater.from(this).inflate(
					R.layout.msg_view_right, null, true);
			headTextView = (ImageView) ll.findViewById(R.id.head_image);
			headTextView.setImageResource(userAvatarResid);
			break;
		case PAR_MSG:
			ll = (RelativeLayout) LayoutInflater.from(this).inflate(
					R.layout.msg_view_left, null, true);
			headTextView = (ImageView) ll.findViewById(R.id.head_image);
			headTextView.setImageResource(parAavatarResid);
			break;
		case INFORM_MSG:
			ll = (RelativeLayout) LayoutInflater.from(this).inflate(
					R.layout.msg_view_mid, null, true);
			break;
		case OFFICIAL_MSG:
			break;
		default:
			break;
		}
		if (null != ll) {
			EmojiconTextView msgTextView = (EmojiconTextView) ll.findViewById(R.id.msg_text);
			byte[] bytesEncoded;
			String msg_decode = null;
			if (msgtype  == MsgType.PAR_MSG) {
				try {
					bytesEncoded = Base64.decode(msg.getBytes());
					msg_decode = new String(bytesEncoded);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				msg_decode = msg;
			}
			msgTextView.setText(msg_decode);
			mChatContentsView.addView(ll);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mChatContentsScrollView.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
		}
	}

	public void reMatch() {
		CSUtils.getInstance(getApplicationContext()).disconnect();
		// runOnUiThread(new Runnable() {
		// public void run() {
		Intent intent = new Intent(ChatActivity.this, MatchingActivity.class);
		intent.putExtra(CSUtils.MATCHING_ROOM_INTENT, tid);
//		Log.e("chat tid", tid);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		// }
		// });
		finish();
	}

	private void setFaceLayout() {
//		Log.e("isKeyboardShowing", isKeyboardShowing + "");
		if (isExpanded) {
			if (popup.isShowing()) {
				popup.dismiss();
			}
			mSmileBtn.setBackgroundResource(R.drawable.smail_btn_normal);
			isExpanded = false;
		} else {
			if (!popup.isShowing()) {
				if (!isKeyboardShowing) {
//					Log.e("isKeyboardShowing", "showAtBottomPending");
					((EmojiconsPopup) popup).showAtBottomPending();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, 0);
				} else {
//					Log.e("isKeyboardShowing", "showAtBottom");
					((EmojiconsPopup) popup).showAtBottom();
				}
				mSmileBtn.setBackgroundResource(R.drawable.smail_btn_pressed);
				isExpanded = true;
			}
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.send_btn:
			String msg = mMsgInput.getText().toString();
			mMsgInput.setText("");
			if (null != msg && msg.length() > 0) {
				try {
					sendMessage(msg);
					mHandler.sendMessage(mHandler.obtainMessage(
							MSG_WHAT_NEWMSG_FROM_SELF, msg));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case R.id.back_btn:
			CSUtils.getInstance(getApplicationContext()).disconnect();
			finish();
			break;
		case R.id.change_btn:
			reMatch();
			break;
		case R.id.smile_btn:
			setFaceLayout();
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		CSUtils.getInstance(getApplicationContext()).disconnect();
		super.onBackPressed();
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
	}

}
