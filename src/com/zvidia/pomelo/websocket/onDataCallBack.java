package com.zvidia.pomelo.websocket;

import org.json.JSONObject;

import com.zvidia.pomelo.protocol.PomeloMessage;

public interface onDataCallBack {
	public void responseData(JSONObject message);
}
