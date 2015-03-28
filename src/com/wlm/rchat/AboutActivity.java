package com.wlm.rchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class AboutActivity extends BaseActivity {
	RelativeLayout weiboLayout;
	RelativeLayout wechatLayout;
	RelativeLayout websiteLayout;
	RelativeLayout qqgroupLayout;
	ImageView backbtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initViews();
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		websiteLayout = (RelativeLayout) findViewById(R.id.about_website);
		weiboLayout = (RelativeLayout)findViewById(R.id.about_weibo);
		backbtn = (ImageView)findViewById(R.id.about_backbt);
		websiteLayout.setOnClickListener(this);
		weiboLayout.setOnClickListener(this);
		backbtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Uri uri;
		Intent it;
		switch (view.getId()) {
		case R.id.about_website:
			uri = Uri.parse("http://www.xialiao.me");    
			it = new Intent(Intent.ACTION_VIEW, uri);    
			startActivity(it);
			break;
		case R.id.about_weibo:
			uri = Uri.parse("http://weibo.com/u/5287294428");    
			it = new Intent(Intent.ACTION_VIEW, uri);    
			startActivity(it);
			break;
		case R.id.about_backbt:
			onBackPressed();
			break;
		default:
			break;
		}
	}
}
