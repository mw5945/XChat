package com.wlm.util;

import java.io.Serializable;

public class UserInfo implements Serializable {
	private static final long serialVersionUID = -4599409260985038578L;
	String UID;
	long CreateTime;
	String PhoneNum;
	String IMEI;
	String AndroidID;
	int Sex;
	String Avatar;
	String Slogan;

	int TID;
	int Location;
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public long getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(long createTime) {
		CreateTime = createTime;
	}
	public String getPhoneNum() {
		return PhoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		PhoneNum = phoneNum;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public String getAndroidID() {
		return AndroidID;
	}
	public void setAndroidID(String androidID) {
		AndroidID = androidID;
	}
	public int getSex() {
		return Sex;
	}
	public void setSex(int sex) {
		Sex = sex;
	}
	public String getAvatar() {
		return Avatar;
	}
	public void setAvatar(String avatar) {
		Avatar = avatar;
	}
	public String getSlogan() {
		return Slogan;
	}
	public void setSlogan(String slogan) {
		Slogan = slogan;
	}
	public int getTopic() {
		return TID;
	}
	public void setTopic(int topic) {
		TID = topic;
	}
	public int getLocation() {
		return Location;
	}
	public void setLocation(int location) {
		Location = location;
	}
	
	
}
