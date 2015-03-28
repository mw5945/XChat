package com.wlm.rchat;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
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

public class AvatarActivity extends BaseActivity implements OnItemClickListener {
	GridView gridView;
	int female_resid[] = { R.drawable.s_f_0, R.drawable.s_f_1,
			R.drawable.s_f_2, R.drawable.s_f_3, R.drawable.s_f_4 };
	int male_resid[] = { R.drawable.s_m_0, R.drawable.s_m_1, R.drawable.s_m_2,
			R.drawable.s_m_3, R.drawable.s_m_4 };
	int head_id;
	String avatarInfo;
	UserInfo userInfo;
	SimpleAdapter sa;
	ImageView preimageView;
	ImageView back_btn;
	String avatar;
	
	static final int SERVER_ERROR = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userInfoUtil = new UserInfoUtil(this);
		userInfo = userInfoUtil.getuUserInfo();
		spUtil = new SPUtil(this);
		initViews();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_head_select);
		gridView = (GridView) findViewById(R.id.head_gridview);
		avatarInfo = userInfo.getAvatar();
		head_id = Integer.parseInt(avatarInfo.substring(
				avatarInfo.length() - 1, avatarInfo.length()));
		ArrayList<HashMap<String, Object>> al_m = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> al_f = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < female_resid.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("head", female_resid[i]);
			if (head_id == i) {
				map.put("select", R.drawable.select);
			} else {
				map.put("select", R.drawable.unselect);
			}
			al_f.add(map);
		}
		for (int i = 0; i < male_resid.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("head", male_resid[i]);
			if (head_id == i) {
				map.put("select", R.drawable.select);
			} else {
				map.put("select", R.drawable.unselect);
			}
			al_m.add(map);
		}
		if (userInfo.getSex() == 0) {
			sa = new SimpleAdapter(this, al_f, R.layout.head_gridview,
					new String[] { "head", "select" }, new int[] {
							R.id.ItemImage, R.id.SelectImage }) {
				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					// TODO Auto-generated method stub
					final int p = position;
					final View view = super.getView(position, convertView,
							parent);
					return setClick(view, p);
				}
			};
		} else if (userInfo.getSex() == 1) {
			sa = new SimpleAdapter(this, al_m, R.layout.head_gridview,
					new String[] { "head", "select" }, new int[] {
							R.id.ItemImage, R.id.SelectImage }) {

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					// TODO Auto-generated method stub
					final int p = position;
					final View view = super.getView(position, convertView,
							parent);
					return setClick(view, p);
				}

			};
		}
		// sa.setViewBinder(new ViewBinder() {
		//
		// @Override
		// public boolean setViewValue(View view, Object data, String string) {
		// // TODO Auto-generated method stub
		// if (view instanceof ImageView && data instanceof Drawable) {
		// ImageView iv = (ImageView) view;
		// iv.setImageDrawable((Drawable) data);
		// return true;
		// } else
		// return false;
		// }
		// });
		gridView.setAdapter(sa);
		gridView.setOnItemClickListener(this);

		back_btn = (ImageView) findViewById(R.id.head_backbt);
		back_btn.setOnClickListener(this);

	}

	View setClick(View view, final int pos) {
		final View relavite = view.findViewById(R.id.ItemRL);
		final ImageView imageView = (ImageView) view
				.findViewById(R.id.SelectImage);
		if (pos == head_id && imageView != null && preimageView == null) {
			preimageView = imageView;
		}
		relavite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (head_id != pos) {
					imageView.setImageResource(R.drawable.select);
					preimageView.setImageResource(R.drawable.unselect);
					head_id = pos;
					preimageView = imageView;
					if (userInfo.getSex() == 0) {
						avatar = "f_" + head_id;
					} else {
						avatar = "m_" + head_id;
					}
					updateAvatar();
				}
			}
		});
		return view;
	}

	void updateAvatar() {
		OnHandshakeSuccessHandler onHandshakeSuccessHandler = new OnHandshakeSuccessHandler() {
			@Override
			public void onSuccess(PomeloClient _client, JSONObject resp) {
				requestAvatar();
			}
		};

		OnErrorHandler onErrorHandler = new OnErrorHandler() {
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(SERVER_ERROR);
			}
		};

		CSUtils.getInstance(getApplicationContext()).disconnect();
		mClient = CSUtils.getInstance(getApplicationContext()).getClient(true);
		mClient.setOnHandshakeSuccessHandler(onHandshakeSuccessHandler);
		mClient.setOnErrorHandler(onErrorHandler);
		mClient.connect();
	}

	void requestAvatar() {
		try {
			JSONObject updatemsg = new JSONObject();
			updatemsg.put("uid", userInfo.getUID());
			JSONObject updateinfos = new JSONObject();
			updateinfos.put("avatar", avatar);
			updatemsg.put("updateinfos", updateinfos);
			mClient.request(CSUtils.UPDATE_USERINFO, updatemsg,
					new onDataCallBack() {

						@Override
						public void responseData(JSONObject message) {
							// TODO Auto-generated method stub
							try {
								if (message.getInt("code") == 200) {
									CSUtils.getInstance(getApplicationContext())
											.disconnect();
									spUtil.SaveUserInfoAvatar(avatar);
									finish();
								} else {
									mHandler.sendEmptyMessage(SERVER_ERROR);
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
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.head_backbt) {
			onBackPressed();
		}
	}

	@Override
	protected void initHandler() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case SERVER_ERROR:
					Toast.makeText(getApplicationContext(),
							"服务器保存失败,请重试", Toast.LENGTH_LONG)
							.show();
					break;
				default:
					break;
				}
			}
		};
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int pos,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		CSUtils.getInstance(getApplicationContext()).disconnect();
		super.onPause();
	}

}