package com.episode6.android.appalarm.pro;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SnoozeActivity extends Activity {
	public static String ACTION_NO_SNOOZE = "no_snooze";
	public static String EXTRA_CLOSE_ACTIVITY = "close_activity";
	
	private boolean mNoSnooze;
	
	private LinearLayout mLlOuterLayout;
	private KeyguardLock mKeyguardLock;
	private TextView mTvMessage;
	private Button mBtnSnooze;
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setContentView(mLlOuterLayout);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		if (i.getBooleanExtra(EXTRA_CLOSE_ACTIVITY, false)) {
			finish();
		} else {
		
//			setUpLayouts();
			setContentView(R.layout.snooze_layout);
			mLlOuterLayout = (LinearLayout)findViewById(R.id.sl_root_layout);
			mTvMessage = (TextView)findViewById(R.id.sl_tv_message);
			mBtnSnooze = (Button)findViewById(R.id.sl_btn_snooze);
			
			mBtnSnooze.setOnClickListener(mBtnSnoozeOnClick);
			((Button)findViewById(R.id.sl_btn_dismiss)).setOnClickListener(mBtnDismissOnClick);
			((Button)findViewById(R.id.sl_btn_dismiss_and_kill)).setOnClickListener(mBtnDismissAndKillOnClick);
			
			mNoSnooze = ACTION_NO_SNOOZE.equals(i.getAction());
			vUpdateText();
			
			KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
			mKeyguardLock = km.newKeyguardLock("AppAlarm");
		}
	}

	
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(EXTRA_CLOSE_ACTIVITY, false)) {
			finish();
		} else {
			mNoSnooze = ACTION_NO_SNOOZE.equals(getIntent().getAction());
			vUpdateText();
		}
	}

	@Override
	protected void onPause() {
		mKeyguardLock.reenableKeyguard();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mKeyguardLock.disableKeyguard();
//		Toast.makeText(this, "mNoSnooze = " + mNoSnooze, Toast.LENGTH_LONG).show();
		super.onResume();
	}

//	private void setUpLayouts() {
//		mLlOuterLayout = new LinearLayout(this);
//		mLlOuterLayout.setLayoutParams(getNewLayoutParams(true, true));
//		mLlOuterLayout.setOrientation(LinearLayout.VERTICAL);
//		
//		mTvMessage = new TextView(this);
//		mTvMessage.setLayoutParams(getNewLayoutParams(true, false));
//		mTvMessage.setTextColor(Color.WHITE);
//		mTvMessage.setText(R.string.sa_message);
//		mTvMessage.setPadding(10, 10, 10, 10);
//		mLlOuterLayout.addView(mTvMessage);
//		
//		mBtnSnooze = new Button(this);
//		LinearLayout.LayoutParams btnlp = getNewLayoutParams(true, false);
//		btnlp.leftMargin = 5;
//		btnlp.rightMargin = 5;
//		btnlp.topMargin = 5;
//		btnlp.bottomMargin = 5;
//		btnlp.weight = 1;
//		
//		mBtnSnooze.setLayoutParams(btnlp);
////		btn.setWidth(100);
//		mBtnSnooze.setText(R.string.sa_btn_snooze);
//		mBtnSnooze.setOnClickListener(mBtnSnoozeOnClick);
//		mLlOuterLayout.addView(mBtnSnooze);
//		
//		
//		LinearLayout ll = new LinearLayout(this);
//		ll.setLayoutParams(getNewLayoutParams(true, false));
//		ll.setOrientation(LinearLayout.HORIZONTAL);
//		ll.setGravity(Gravity.CENTER);
//		ll.setBackgroundColor(Color.GRAY);
//		mLlOuterLayout.addView(ll);
//
//		
//		Button btn = new Button(this);
//		LinearLayout.LayoutParams btnlp2 = getNewLayoutParams(true, false);
//		btnlp2.leftMargin = 5;
//		btnlp2.rightMargin = 5;
//		btnlp2.topMargin = 5;
//		btnlp2.bottomMargin = 5;
//		btnlp2.weight = 1;
//		btn.setLayoutParams(btnlp2);
////		btn.setWidth(100);
//		btn.setText(R.string.sa_btn_dismiss);
//		btn.setOnClickListener(mBtnDismissOnClick);
//		ll.addView(btn);
//		
//		btn = new Button(this);
//		btn.setLayoutParams(btnlp2);
////		btn.setWidth(100);
//		btn.setText(R.string.sa_btn_dismiss_and_kill);
//		btn.setOnClickListener(mBtnDismissAndKillOnClick);
//		ll.addView(btn);
//	}
	
	private void vUpdateText() {
		if (mNoSnooze) {
			mTvMessage.setText(R.string.sa_message_unsnooze);
			mBtnSnooze.setText(R.string.sa_btn_unsnooze);
		} else {
			mTvMessage.setText(R.string.sa_message);
			mBtnSnooze.setText(R.string.sa_btn_snooze);			
		}
	}
	
//	private LinearLayout.LayoutParams getNewLayoutParams(boolean fillWidth, boolean fillHeight) {
//		int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//		int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//		if (fillWidth) {
//			width = LinearLayout.LayoutParams.FILL_PARENT;
//		}
//		if (fillHeight) {
//			height = LinearLayout.LayoutParams.FILL_PARENT;
//		}
//		
//		return new LinearLayout.LayoutParams(width, height);
//	}

	
	private View.OnClickListener mBtnSnoozeOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), AalService.class);
			if (mNoSnooze) {
				i.setAction(AalService.ACTION_RECOVER_SNOOZE_ALARM);
				i.putExtra(AalService.EXTRA_DONT_SHOW_SNOOZE, true);
			} else {
				i.setAction(AalService.ACTION_SNOOZE_ALARM);
			}
			startService(i);
			finish();
		}
		
	};
	private View.OnClickListener mBtnDismissOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), AalService.class);
			if (mNoSnooze) {
				i.setAction(AalService.ACTION_DISMISS_SNOOZE);
			} else {
				i.setAction(AalService.ACTION_STOP_ALARM);
			}
			startService(i);
			finish();
		}
		
	};
	
	private View.OnClickListener mBtnDismissAndKillOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), AalService.class);
			if (mNoSnooze) {
				i.setAction(AalService.ACTION_DISMISS_SNOOZE_AND_KILL);
			} else {
				i.setAction(AalService.ACTION_STOP_ALARM_AND_KILL);
			}
			startService(i);
			finish();
		}
		
	};
}
