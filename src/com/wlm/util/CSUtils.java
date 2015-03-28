package com.wlm.util;

import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.zvidia.pomelo.websocket.PomeloClient;

//import com.netease.pomelo.DataCallBack;
//import com.netease.pomelo.DataEvent;
//import com.netease.pomelo.DataListener;
//import com.netease.pomelo.PomeloClient;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

// client server util
public class CSUtils {

//	public final static String HOST_DEFAULT = "192.168.1.104";
//	public static String HOST_DEFAULT = "10.17.20.135";
	public static String HOST_DEFAULT = "182.92.185.51";
//	public final static String HOST_DEFAULT = "115.28.222.201";
	public final static int PORT_DEFAULT = 50000;
	public final static int PORT_RANK = 3030;
	public final static int PORT_REGIST = 3040;
	private PomeloClient mClient = null;
	public final static String ROUTE_REGIST = "regist.registHandler.regist";
	public final static String ROUTE_REGIST_GATE = "regist.registHandler.queryEntry";
	public final static String ROUTE_REGIST_ENTER = "regist.registHandler.enter";
	public final static String ROUTE_RANK = "rank.rankHandler.getPageRank";
	public final static String ROUTE_MYRANK = "rank.rankHandler.getMyRank";
	public final static String ROUTE_UPDATE_IN_RANK = "rank.rankHandler.update";
	public final static String ROUTE_GATE = "gate.gateHandler.queryEntry";
	public final static String ROUTE_SEND = "chat.chatHandler.send";

	public final static String EVENT_ONADD = "onAdd";
	public final static String EVENT_ONCHAT = "onChat";
	public final static String EVENT_ONLEAVE = "onLeave";
	public final static String EVENT_ONCOMINGINFO = "onCommingInfo";

	public static String ROOMID = "test_roomid";
	public static String USERID = "test_userid";

	public final static int HTTPCODE_OK = 200;
	public final static int HTTPCODE_NONE = 404;
	public final static int HTTPCODE_TIMEOUT = 408;
	public final static int HTTPCODE_NOSERVER = 503;

	public final static int HTTPCODE_SERVEREXCEPTION = 801;
	public final static int HTTPCODE_REGISTED = 802;
	
	public final static String ROUTE_TOPIC_REQUEST = "gate.gateHandler.requestTopics";
	public final static String ROUTE_INIT_USERINFO = "gate.gateHandler.initUserinfo";
	public final static String ROUTE_REGIST_USER = "gate.gateHandler.regist";
	public final static String ROUTE_ENTER = "connector.entryHandler.enter";
	public final static String  ROUTE_CHAT_SEND = "chat.chatHandler.send";
	
	public final static String  UPDATE_USERINFO = "gate.gateHandler.updateUserInfos";
	public final static String  ROUTE_FEEDBACK = "gate.gateHandler.feedback";
	
	
	// watchers
	public final static int MSG_WHAT_CS = 1000;
	public final static int MSG_ARG1_DEFAULT = 0;
	public final static int MSG_ARG1_REQUEST = 1;
	public final static int MSG_ARG1_ON = 2;
	public final static int MSG_ARG2_DEFAULT = 0;
	private ArrayList<Handler> mWatchers = new ArrayList<Handler>();

	public static String mRoomID = "";// user room
	public static String mName = "";// user name
	public static String mUID = "";// user id

	public final static String MATCHING_ROOM_INTENT = "tid";
	public CSUtils() {

	}

	public CSUtils(Context context) {

	}

	public static CSUtils csu = null;

	public static CSUtils getInstance(Context context) {
		if (null == csu) {
			csu = new CSUtils(context);
		}
		return csu;
	}

	/**
	 * @param createNewDefault 若为true且当前client为空，则创建新的默认客户�?	 */
	public PomeloClient getClient(boolean createNewDefault) {
		if(true == createNewDefault && null == mClient) {
			mClient = newDefaultClient();
		}
		return mClient;
	}
	
	public void setClient(PomeloClient po) {
		mClient = po;
	}

	public boolean isClientValid() {
		if (null != mClient) {
			return true;
		}
		return false;
	}

	public PomeloClient newDefaultClient() {
		return newClient(HOST_DEFAULT, PORT_DEFAULT);
	}

	public PomeloClient newClient(String host, int port) {
		try {
			if (null != mClient) {
				mClient.close();
				mClient = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			mClient = null;
		}

		try {
			mClient = new PomeloClient(host, port);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		mClient.init();

		return mClient;
	}

	public void addWatcher(Handler handler) {
		mWatchers.add(handler);
	}

	public void notifyWatchers(int what, int arg1, int arg2, Object obj) {
		for (Handler handler : mWatchers) {
			if (null != handler) {
				handler.sendMessage(handler
						.obtainMessage(what, arg1, arg2, obj));
			}
		}
	}

	public void notifyWatchers(int what) {
		notifyWatchers(what, 0, 0, null);
	}

//	public void inform(String route, JSONObject msg) {
//		if (null != mClient) {
//			mClient.inform(route, msg);
//		}
//	}
//
//	public void on(String route) {
//		if (null != mClient) {
//			mClient.on(route, new DataListener() {
//				public void receiveData(DataEvent event) {
//					JSONObject msg = event.getMessage();
//					notifyWatchers(MSG_WHAT_CS, MSG_ARG1_ON, MSG_ARG2_DEFAULT,
//							msg);
//				}
//			});
//		}
//	}

	public void disconnect() {
		try {
			if (isClientValid()) {
				mClient.close();
				Log.e("pomelo client", String.valueOf(mClient.getReadyState()));
				mClient = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendFeedback(final String content) {
		// test
		// ToastUtils.showText(content);
		new Thread(new Runnable() {
			@Override
			public void run() {

				// sendFeedBackBaseGet(content);
				sendFeedBackBaseHttp(content, true);

			}
		}).start();
	}

	public static void sendFeedBackBaseHttp(String content, boolean isBasePost) {
		try {
			String userId = "1140804223710223";
			String url = "http://192.168.1.103:3000/login.ejs" + "?"
					+ "userid=" + URLEncoder.encode(userId, "UTF-8") + "&"
					+ "feedback=" + URLEncoder.encode(content, "UTF-8");
			HttpUriRequest http;
			if (isBasePost) {
				http = new HttpPost(url);
			} else {
				http = new HttpGet(url);
			}
			HttpResponse httpResponse = new DefaultHttpClient().execute(http);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == statusCode) {
				Log.i("tag", "success");
			} else {
				Log.e("tag", "failed");
			}
		} catch (Exception e) {
			Log.e("tag", "failed e=" + e.toString());
		}
	}

}
