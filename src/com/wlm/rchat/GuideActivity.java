package com.wlm.rchat;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class GuideActivity extends BaseActivity {

	private ViewPager mViewPager;
	private ViewGroup mViewPointsLayout;

	private ArrayList<View> mPageViewsArray;
	private ImageView[] mPointsArray;

	private View mJoinBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		mPageViewsArray = new ArrayList<View>();
		mPageViewsArray.add(getLayoutInflater().inflate(R.layout.guide_page0,
				null));
		mPageViewsArray.add(getLayoutInflater().inflate(R.layout.guide_page1,
				null));
		mPageViewsArray.add(getLayoutInflater().inflate(R.layout.guide_page2,
				null));
		mPageViewsArray.add(getLayoutInflater().inflate(R.layout.guide_page3,
				null));

		mPointsArray = new ImageView[mPageViewsArray.size()];

		// 添加小圆点的图片
		for (int i = 0; i < mPageViewsArray.size(); i++) {
			ImageView imageView = new ImageView(GuideActivity.this);
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(50, 50);
			imageView.setLayoutParams(lp);
			mPointsArray[i] = imageView;
			mPointsArray[i]
					.setBackgroundResource((i == 0) ? R.drawable.page_indicator_focused
							: R.drawable.page_indicator_normal);
			mViewPointsLayout.addView(mPointsArray[i]);
		}

		mViewPager.setAdapter(new GuidePageAdapter());
		mViewPager.setOnPageChangeListener(new GuidePageChangeListener());

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	void initViews() {
		setContentView(R.layout.activity_guide);

		mViewPager = (ViewPager) findViewById(R.id.guide_pages);
		mViewPointsLayout = (ViewGroup) findViewById(R.id.guide_points_layout);
		mJoinBtn = findViewById(R.id.join_btn);
		mJoinBtn.setOnClickListener(this);

	}

	@Override
	protected void initHandler() {

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {

				default:
					break;
				}
			}
		};
	}

	// @Override
	// public void onClick(View view) {
	// switch (view.getId()) {
	// case R.id.join_btn: {
	// newStartActivity(SexActivity.class);
	// break;
	// }
	// default:
	// break;
	// }
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
			// newStartActivity(SexActivity.class);
			return false;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	class GuidePageAdapter extends PagerAdapter {
		@Override
		public void destroyItem(View v, int position, Object arg2) {
			((ViewPager) v).removeView(mPageViewsArray.get(position));
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return mPageViewsArray.size();
		}

		@Override
		public Object instantiateItem(View v, int position) {
			((ViewPager) v).addView(mPageViewsArray.get(position));
			return mPageViewsArray.get(position);
		}

		@Override
		public boolean isViewFromObject(View v, Object arg1) {
			return v == arg1;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < mPageViewsArray.size(); i++) {
				mPointsArray[i]
						.setBackgroundResource((position == i) ? R.drawable.page_indicator_focused
								: R.drawable.page_indicator_normal);
			}
			// 到达�?���?��时显示�?立即体验”按�?
			if (position == mPageViewsArray.size() - 1) {
				// mViewPointsLayout.setVisibility(View.GONE);
				// mJoinBtn.setVisibility(View.VISIBLE);
				gotoActivity(new Intent(GuideActivity.this,
						SexSettingActivity.class), R.anim.fadein,
						R.anim.fadeout);
				finish();
			}

		}
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
