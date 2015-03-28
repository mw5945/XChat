package com.wlm.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
	}

	/**
	 * add topics
	 * 
	 * @param topics
	 */
	public void addTopic(List<TopicInfo> topicInfos) {
		db.beginTransaction(); // 开始事务
		try {
			for (TopicInfo topicInfo : topicInfos) {
				//if (topicInfo.Status == 3) {
					db.execSQL(
							"INSERT INTO topics VALUES(null, ?, ?, ?, ?, ?, ?)",
							new Object[] { topicInfo.TID, topicInfo.Name,
									topicInfo.Pic, topicInfo.Description,
									topicInfo.Population, topicInfo.Status });
				//}
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void updateTopicStatus(TopicInfo topicInfo) {
		ContentValues cv = new ContentValues();
		cv.put("status", topicInfo.Status);
		db.update("topics", cv, "tid = ?", new String[]{String.valueOf(topicInfo.TID)});
	}

	public void deleteTopic(String TID) {
		db.delete("topics", "tid = ?",new String[]{TID});
	}
	
	public void clearTopics() {
		List<TopicInfo> topicInfos = query();
		for (TopicInfo topicInfo : topicInfos) {
			deleteTopic(topicInfo.TID);
		}
	}

	/**
	 * query all topicInfo, return list
	 * 
	 * @return List<Person>
	 */
	public List<TopicInfo> query() {
		ArrayList<TopicInfo> topicinfos = new ArrayList<TopicInfo>();
		Cursor c = queryTheCursor();
		while (c.moveToNext()) {
			TopicInfo topicInfo = new TopicInfo();
			topicInfo.TID = c.getString(c.getColumnIndex("tid"));
			topicInfo.Name = c.getString(c.getColumnIndex("t_name"));
			topicInfo.Pic = c.getString(c.getColumnIndex("t_pic"));
			topicInfo.Description = c
					.getString(c.getColumnIndex("description"));
			topicInfo.Status = c.getInt(c.getColumnIndex("status"));
			topicInfo.Population = c.getInt(c.getColumnIndex("population"));
			topicinfos.add(topicInfo);
		}
		c.close();
		return topicinfos;
	}

	/**
	 * query all topic, return cursor
	 * 
	 * @return Cursor
	 */
	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM topics", null);
		return c;
	}

	public TopicInfo queryTid(Integer tid) {
		Cursor c = db.rawQuery("SELECT * FROM topics where tid = ?", new String[]{String.valueOf(tid)});
		TopicInfo topicInfo = null;
		if (c.moveToNext()) {
			topicInfo = new TopicInfo();
			topicInfo.TID = c.getString(c.getColumnIndex("tid"));
			topicInfo.Name = c.getString(c.getColumnIndex("t_name"));
			topicInfo.Pic = c.getString(c.getColumnIndex("t_pic"));
			topicInfo.Description = c
					.getString(c.getColumnIndex("description"));
			topicInfo.Status = c.getInt(c.getColumnIndex("status"));
			topicInfo.Population = c.getInt(c.getColumnIndex("population"));
		}
		return topicInfo;
	}
	/**
	 * close database
	 */
	public void closeDB() {
		db.close();
	}
}
