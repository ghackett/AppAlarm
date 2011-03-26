package com.episode6.android.appalarm.pro;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

public class CustomActionActivity extends Activity {
	public static final String EXTRA_ACTION_TYPE = "action_type";
	public static final String EXTRA_PODCAST_URL = "podcast_url";
	
	public static final int ACTION_TYPE_LATEST_LISTEN_PODCAST = 1;
	public static final int ACTION_TYPE_LATEST_UNHEARD_LISTEN_PODCAST = 2;
	public static final int ACTION_TYPE_CUSTOM_LISTEN_PODCAST = 3;
	
	public static final String GOOGLE_LISTEN_PACKAGE_NAME = "com.google.android.apps.listen";
//	public static final String GOOGLE_LISTEN_LAUNCH_PREFIX = "http://listen.googlelabs.com/listen?id=";
	public static final String GOOGLE_LISTEN_LAUNCH_PREFIX = "http://lfe-alpo-go-next.appspot.com/listen?id=0";
	
	public static final Uri GOOGLE_LISTEN_ITEMS_URI = Uri.parse("content://com.google.android.apps.listen.PodcastProvider/items");
	public static final Uri GOOGLE_LISTEN_SUBSCRIPTIONS_URI = Uri.parse("content://com.google.android.apps.listen.PodcastProvider/subscriptions");
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent fromIntent = getIntent();
		
		int action_type = fromIntent.getIntExtra(EXTRA_ACTION_TYPE, ACTION_TYPE_LATEST_LISTEN_PODCAST);
		
		switch(action_type) {
		case ACTION_TYPE_LATEST_LISTEN_PODCAST:
			startLatestPodcast();
			break;
		case ACTION_TYPE_LATEST_UNHEARD_LISTEN_PODCAST:
//			startUnheardLatestPodcast();
			startLatestPodcast();
			break;
		case ACTION_TYPE_CUSTOM_LISTEN_PODCAST:
			startLatestPodcastCustom(fromIntent.getStringExtra(EXTRA_PODCAST_URL));
			break;
		}
		
		finish();
	}
	
	private void startLatestPodcast() {
//		Cursor c = getContentResolver().query(GOOGLE_LISTEN_ITEMS_URI, new String[] {"listenid"}, null, null, "pubDate DESC");
//		if (c.moveToFirst()) {
			try {
				startGoogleListenPodcast(""); 
			} catch (Exception e) {
				e.printStackTrace();
			}
//		}
//		c.close();
	}
	
	private void startUnheardLatestPodcast() {
		Cursor c = getContentResolver().query(GOOGLE_LISTEN_ITEMS_URI, new String[] {"listenid"}, "(listened is null) OR (listened=0)", null, "pubDate DESC");
		if (c.moveToFirst()) {
			try {
				startGoogleListenPodcast(c.getString(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			c.close();
		} else {
			c.close();
			startLatestPodcast();
		}
		
	}
	
	private void startLatestPodcastCustom(String channelGuid) {
		Cursor c = getContentResolver().query(GOOGLE_LISTEN_ITEMS_URI, new String[] {"guid"}, "channelGuid='"+channelGuid+"'", null, "pubDate DESC");
		if (c.moveToFirst()) {
			try {
				startGoogleListenPodcast(c.getString(0));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		c.close();
	}
	
	private void startGoogleListenPodcast(String guid) throws Exception {
		mIntentToLaunch = new Intent(Intent.ACTION_VIEW);
		mIntentToLaunch.setPackage(GOOGLE_LISTEN_PACKAGE_NAME);
		String podcastGuid = URLEncoder.encode(guid, "UTF-8");
		mIntentToLaunch.setData(Uri.parse(GOOGLE_LISTEN_LAUNCH_PREFIX + podcastGuid));
		mIntentToLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		try {
			Intent i =  getPackageManager().getLaunchIntentForPackage(GOOGLE_LISTEN_PACKAGE_NAME);
			mHandler.postDelayed(mStartIntentTask, 4000);
			startActivity(i);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	private final Handler mHandler = new Handler();
	private Intent mIntentToLaunch;
	
	private final Runnable mStartIntentTask = new Runnable() {

		@Override
		public void run() {
			startActivity(mIntentToLaunch);
			finish();
		}
		
	};

}
