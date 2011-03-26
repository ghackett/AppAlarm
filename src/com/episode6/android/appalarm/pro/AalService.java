package com.episode6.android.appalarm.pro;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class AalService extends Service {
	public static final String ACTION_SET_ALARM = "set_alarm";
	public static final String ACTION_LAUNCH_ALARM = "launch_alarm";
	public static final String ACTION_FORCE_LAUNCH_ALARM = "force_launch";
	public static final String ACTION_STOP_ALARM = "stop_alarm";
	public static final String ACTION_STOP_ALARM_AND_KILL = "stop_alarm_and_kill";
	public static final String ACTION_SET_SILENT_ALARM = "set_silent_alarm";
	public static final String ACTION_SNOOZE_ALARM = "snooze_alarm";
	public static final String ACTION_RECOVER_SNOOZE_ALARM = "recover_snooze_alarm";
	public static final String ACTION_SET_SHOW_NOTIF = "set_show_notif";
	public static final String ACTION_DISMISS_SNOOZE = "dismiss_snooze";
	public static final String ACTION_DISMISS_SNOOZE_AND_KILL = "dismiss_snooze_and_kill";
	
	public static final String EXTRA_SHOW_NOTIF = "show_notification";
	public static final String EXTRA_DONT_DISABLE = "dont_disable";
	
	public static final boolean DEFAULT_SHOW_NOTIF = false;
	public static final String EXTRA_DONT_SHOW_SNOOZE = "dont_show_snooze";
	public static final boolean DEFAULT_DONT_SHOW_SNOOZE = false;

	
	public static final String PREF_FILE_NAME = "AutoAppLauncherPrefs";
	private static final String PREF_KEY_NEXT_ALARM_ID = "next_alarm_id";
	private static final String PREF_KEY_SNOOZE_RESTART_BACKUP_ALARM = "snooze_restart_backup_alarm";
	private static final String PREF_KEY_SNOOZE_ALARM = "snooze_alarm";
	private static final String PREF_KEY_SNOOZE_VOLUME = "snooze_volume";
	private static final String PREF_KEY_SHOW_NOTIF = "show_notif";
	
	private static final int NOTIFY_ID = R.layout.main;
	private static final int NOTIFY_ID_ALARM_SET = R.layout.alarm_edit;
	private static final int NOTIFY_ID_ALARM_SNOOZE = R.layout.app_list;
	
	private PackageManager mPackageManager;
	private NotificationManager mNotificationManager;
	private PowerManager.WakeLock mFullWakeLock;
	private PowerManager.WakeLock mPartialWakeLock;
	private final Handler mHandler = new Handler();
	
	private AalDbAdapter mDbAdapter;
	
	private AlarmItem mCurrentAlarmItem;
	
	private boolean mShowAlarmToast, mIsPlayingBackup, mIsCounting, mIsWaitingForWifi, mIsGoingSnooze, mIsShowingSnooze;
	
	private WifiNetworkStateChangeReceiver mWifiReceiver;
	

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mPartialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AppAlarmTag");
		mPartialWakeLock.acquire();
		
		mPackageManager = getPackageManager();
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		mDbAdapter = new AalDbAdapter(this);
		mDbAdapter.open();
		
		mShowAlarmToast = true;
		mIsPlayingBackup = false;
		mIsGoingSnooze = false;
		mIsCounting = false;
		mIsWaitingForWifi = false;
		mIsShowingSnooze = false;
	}

	@Override
	public void onDestroy() {
		setNextAlarm();
		unregisterWifiReciever();
		if (mCurrentAlarmItem != null) {
			if (!mIsGoingSnooze) {
				try {
					if (mCurrentAlarmItem.getBool(AlarmItem.KEY_WIFI) && mCurrentAlarmItem.getBool(AlarmItem.KEY_TURN_OFF_WIFI)) {
						WifiManager wm = (WifiManager)getSystemService(WIFI_SERVICE);
						wm.setWifiEnabled(false);
					}
				} catch (Exception e) {}
			}
		}
		if (mIsShowingSnooze) {
			Intent i = new Intent(getBaseContext(), SnoozeActivity.class);
			i.putExtra(SnoozeActivity.EXTRA_CLOSE_ACTIVITY, true);	
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		}
		mDbAdapter.close();
		try {
			mFullWakeLock.release();
		} catch (Exception e) {}
		try {
			mNotificationManager.cancel(NOTIFY_ID);
		} catch (Exception e) {}
//		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
		
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		doAction(intent);
		
		try {
			mPartialWakeLock.release();
		} catch (Exception e) {}
	}
	
	private void unregisterWifiReciever() {
		if (mIsWaitingForWifi) {
			try {
				unregisterReceiver(mWifiReceiver);
			} catch (Exception e) {}				
		}
		mIsWaitingForWifi = false;
	}
	
//	private void stopApp() {
//		if (mCurrentAlarmItem != null) {
//			if (mCurrentAlarmItem.hasPackageName()) {
//				ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//				am.restartPackage(mCurrentAlarmItem.getString(AlarmItem.KEY_PACKAGE_NAME));
//			}
//		}
//	}
	
	private void doAction(Intent intent) {
		String action = intent.getAction();
		if (action.equals(ACTION_SET_ALARM)) {
			stopOrSet();
		} else if (action.equals(ACTION_SET_SILENT_ALARM)) {
			mShowAlarmToast = false;
			stopOrSet();
		} else if (action.equals(ACTION_LAUNCH_ALARM)) {
			actionLaunchAlarm(intent.getBooleanExtra(EXTRA_DONT_DISABLE, false));
		} else if (action.equals(ACTION_FORCE_LAUNCH_ALARM)) {
			actionForceLaunchAlarm();
		} else if (action.equals(ACTION_STOP_ALARM)) {
			actionStopAlarm(false);
		} else if (action.equals(ACTION_STOP_ALARM_AND_KILL)) {
			actionStopAlarm(true);
		} else if (action.equals(ACTION_SNOOZE_ALARM)) {
			actionSnoozeAlarm();
		} else if (action.equals(ACTION_RECOVER_SNOOZE_ALARM)) {
			actionRecoverSnoozeAlarm(intent.getBooleanExtra(EXTRA_DONT_SHOW_SNOOZE, DEFAULT_DONT_SHOW_SNOOZE));
		} else if (action.equals(ACTION_SET_SHOW_NOTIF)) {
			saveShowNotifPref(intent.getBooleanExtra(EXTRA_SHOW_NOTIF, DEFAULT_SHOW_NOTIF));
			mShowAlarmToast = false;
			stopOrSet();
		} else if (action.equals(ACTION_DISMISS_SNOOZE)) {
			actionDismissSnooze(false);
		} else if (action.equals(ACTION_DISMISS_SNOOZE_AND_KILL)) {
			actionDismissSnooze(true);
		}
	}
	
	private void actionLaunchAlarm(boolean dontDisable) {
		turnOnForeground(getNotification(R.string.as_nm_launched, R.string.as_nt_launched));
		mCurrentAlarmItem = mDbAdapter.getAlarmById(loadNextAlarmPref());
		
		if (!mCurrentAlarmItem.hasRepeat() && !dontDisable) {
			mDbAdapter.setAlarmEnabled(mCurrentAlarmItem.getInt(AlarmItem.KEY_ROWID), false);
		}
		
		if (mCurrentAlarmItem.getBool(AlarmItem.KEY_DONT_LAUNCH_ON_CALL) && isPhoneNotIdle()) {
			try {
				Thread.sleep(2000);
				mShowAlarmToast = false;
				stopOrSet();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			launchAlarm(false);
		}
	}
	
	private void actionForceLaunchAlarm() {
		unregisterWifiReciever();
		
		if (mCurrentAlarmItem.getBool(AlarmItem.KEY_DONT_LAUNCH_ON_CALL) && isPhoneNotIdle()) {
			mShowAlarmToast = false;
			stopOrSet();
		} else {
			launchAlarm(true);
		}
	}
	
	private void actionStopAlarm(boolean killApp) {
		cancelSnoozeAlarm();
		mIsPlayingBackup = false;
		try {
			mRingtone.stop();
			mRingtone = null;
		} catch (Exception e) {}
		mShowAlarmToast = false;
		if (killApp) {
			Intent i = new Intent(this, KillAndLaunchActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(KillAndLaunchActivity.EXTRA_PACKAGE_TO_RESTART, mCurrentAlarmItem.getString(AlarmItem.KEY_PACKAGE_NAME));
			startActivity(i);
		}
		stopSelf();
	}
	
	private void actionSnoozeAlarm() {
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		saveSnoozeVolume(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
		mIsGoingSnooze = true;
		if (mCurrentAlarmItem == null) {
			saveSnoozeAlarmId(0);
		} else {
			saveSnoozeAlarmId(mCurrentAlarmItem.getInt(AlarmItem.KEY_ROWID));
		}
		
		saveSnoozeRestartBackup(mIsPlayingBackup);
		setSnoozeAlarm();
		
		mIsPlayingBackup = false;
		try {
			mRingtone.stop();
			mRingtone = null;
		} catch (Exception e) {}
		mShowAlarmToast = false;
		stopSelf();
	}
	
	private void actionRecoverSnoozeAlarm(boolean dontShowSleep) {
		turnOnForeground(getNotification(R.string.as_nm_launched, R.string.as_nt_launched));
		cancelAlarmSnoozeNotification();
		
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, getSnoozeVolume(), AudioManager.FLAG_SHOW_UI);
		
		
		long alarmId = getSnoozeAlarm();
		if (alarmId != 0) {
			mCurrentAlarmItem = mDbAdapter.getAlarmById(alarmId);
			if (loadSnoozeRestartBackup()) {
				playBackupAlarm();
			}
		}
//		setNextAlarm();
		
		if (!dontShowSleep) {
			checkAndShowSnooze(false);
		}
		mIsCounting = true;
		mHandler.postDelayed(mSetTask, 2000);
	}
	
	private void actionDismissSnooze(boolean killApp) {
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, getSnoozeVolume(), AudioManager.FLAG_SHOW_UI);
		cancelSnoozeAlarm();
		long alarmId = getSnoozeAlarm();
		if (alarmId != 0) {
			mCurrentAlarmItem = mDbAdapter.getAlarmById(alarmId);
		}
		mIsPlayingBackup = false;
		try {
			mRingtone.stop();
			mRingtone = null;
		} catch (Exception e) {}
		mShowAlarmToast = false;
		if (killApp) {
			Intent i = new Intent(this, KillAndLaunchActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(KillAndLaunchActivity.EXTRA_PACKAGE_TO_RESTART, mCurrentAlarmItem.getString(AlarmItem.KEY_PACKAGE_NAME));
			startActivity(i);
		}
		stopSelf();	
	}
	
	private boolean isPhoneNotIdle() {
		TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		return tm.getCallState() != TelephonyManager.CALL_STATE_IDLE;
	}
	
	private void launchAlarm(boolean force) {		
		if (mCurrentAlarmItem.getBool(AlarmItem.KEY_WIFI) && (!force)) {
			mWifiReceiver = new WifiNetworkStateChangeReceiver();
			WifiManager wm = (WifiManager)getSystemService(WIFI_SERVICE);
			
			if (wm.isWifiEnabled()) {
				wm.setWifiEnabled(false);
				while (wm.getWifiState() != WifiManager.WIFI_STATE_DISABLED) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
			mIsWaitingForWifi = true;
			boolean didItWork = wm.setWifiEnabled(true);
			wm.startScan();
			if (didItWork) {
				mNotificationManager.notify(NOTIFY_ID, getNotification(R.string.as_nm_waiting_for_wifi));
				Thread t = new Thread(mWifiWaitTask);
				t.setDaemon(true);
				t.start();
			} else {
				doWifiFailedAction();
			}
		} else {
		
			if (mCurrentAlarmItem.getBool(AlarmItem.KEY_SET_MEDIA_VOLUME)) {
				AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
				int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				int newVol = mCurrentAlarmItem.getInt(AlarmItem.KEY_MEDIA_VOLUME);
				if (newVol > maxVol) {
					newVol = maxVol;
				}
				am.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, AudioManager.FLAG_SHOW_UI);
				am = null;
			}
			
			if (mCurrentAlarmItem.getBool(AlarmItem.KEY_NET_TEST)) {
				if (HTTPHelper.isNetworkActive(mCurrentAlarmItem.getString(AlarmItem.KEY_NET_TEST_URL))) {
					mNotificationManager.notify(NOTIFY_ID, getNotification(R.string.as_nm_np_la));
					launchApp();
				} else if (mCurrentAlarmItem.getInt(AlarmItem.KEY_BACKUP_OPTION) == 2) {
					mNotificationManager.notify(NOTIFY_ID, getNotification(R.string.as_nm_nf_plba));
					playBackupAlarm();
					checkAndShowSnooze(false);
				}
			} else {
				launchApp();
			}
			
			if (mCurrentAlarmItem.getInt(AlarmItem.KEY_BACKUP_OPTION) == 1) {
				playBackupAlarm();
			}
			
			
			
			mIsCounting = true;
			mHandler.postDelayed(mSetTask, 2000);
			}
	}
	
	private void checkAndShowSnooze(boolean doPause) {
		if (doPause) {
			mHandler.postDelayed(mShowSnoozeDialogTask, 20000);
		} else {
			mHandler.post(mShowSnoozeDialogTask);
		}

	}
	
	
	private final Runnable mShowSnoozeDialogTask = new Runnable() {

		@Override
		public void run() {
			if (mCurrentAlarmItem != null) {
				if (mCurrentAlarmItem.getBool(AlarmItem.KEY_MUTE_SNOOZE)) {
					Intent i = new Intent(getBaseContext(), SnoozeActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND);
					startActivity(i);
					mIsShowingSnooze = true;
				}
			}
		}
		
	};
	

	private Notification getNotification(int message) {
		return getNotification(message, message);
	}
	
	private Notification getNotification(int message, int ticker) {
		Notification notif = new Notification(R.drawable.stat_notify_alarm, getString(ticker), System.currentTimeMillis());
		Intent delIntent = new Intent(this, AalService.class);
		delIntent.setAction(ACTION_STOP_ALARM);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, SnoozeActivity.class), 0);
		notif.setLatestEventInfo(this, getString(R.string.as_notif_constant), getString(message), contentIntent);
		notif.deleteIntent = PendingIntent.getService(this, 0, delIntent, 0);
		notif.ledARGB = 0xffffff00;
		notif.ledOnMS = 300;
		notif.ledOffMS = 1000;
		notif.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_FOREGROUND_SERVICE;
		return notif;
	}
	
	private void showAlarmSetNotification(String timeString) {
		if (loadShowNotifPref()) {
			String message = "Next alarm: " + timeString; 
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, AlarmList.class), 0);
			Notification notif = new Notification(R.drawable.aal_stat_icon, message, System.currentTimeMillis());
			notif.setLatestEventInfo(this, "AppAlarm Scheduled", timeString, contentIntent);
			notif.flags = Notification.FLAG_ONGOING_EVENT;
			mNotificationManager.notify(NOTIFY_ID_ALARM_SET, notif);	
		} else {
			mNotificationManager.cancel(NOTIFY_ID_ALARM_SET);
		}
		
	}
	
	private void showAlarmSnoozeNotification(String timeString) {
		String message = "Snoozing for " + timeString; 
		Intent i = new Intent(this, SnoozeActivity.class);
		i.setAction(SnoozeActivity.ACTION_NO_SNOOZE);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
		Notification notif = new Notification(R.drawable.stat_notify_snooze, message, System.currentTimeMillis());
		notif.setLatestEventInfo(this, "AppAlarm Snoozing", timeString, contentIntent);
		notif.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(NOTIFY_ID_ALARM_SNOOZE, notif);	
	}
	
	private void cancelAlarmSnoozeNotification() {
		mNotificationManager.cancel(NOTIFY_ID_ALARM_SNOOZE);	
	}
	
	private void turnOnForeground(Notification notif) {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mFullWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "AppAlarmTag");
		mFullWakeLock.acquire();

		try {
			Method m = Service.class.getMethod("startForeground", new Class[] {int.class, Notification.class});
			m.invoke(this, NOTIFY_ID, notif);
		} catch (Exception e) {
			setForeground(true);
			mNotificationManager.notify(NOTIFY_ID, notif);
		}
	}
	
	public static String getIntentUri(Intent i) {
		String rtr = "";
		try {
			Method m = Intent.class.getMethod("toUri", new Class[] {int.class});
			rtr = (String) m.invoke(i, Intent.class.getField("URI_INTENT_SCHEME").getInt(null));
		} catch (Exception e) {
			rtr = i.toURI();
		}
		return rtr;
	}
	
	private Intent getAlarmIntent() {
		Intent i;
		String pname = mCurrentAlarmItem.getString(AlarmItem.KEY_PACKAGE_NAME);
		String cAction = mCurrentAlarmItem.getString(AlarmItem.KEY_CUSTOM_ACTION);
		String cData = mCurrentAlarmItem.getString(AlarmItem.KEY_CUSTOM_DATA);
		String cType = mCurrentAlarmItem.getString(AlarmItem.KEY_CUSTOM_TYPE);
		
		if (pname == null || pname.equals("")) {
			return null;
		} else if (mCurrentAlarmItem.isShortcutIntent()) {
			try {
				i = Intent.getIntent(cData);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return null;			
			}
		} else if (!cAction.equals("")) {
			i = new Intent();
			i.setAction(cAction);
			if (!cData.equals("")) {
				i.setData(Uri.parse(cData));
			}
			if (!cType.equals("")) {
				i.setType(cType);
			}
		}  else {
			try {
				i =  mPackageManager.getLaunchIntentForPackage(pname);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return i;
	}
	
	private void stopOrSet() {
		if (isSafeToStopSelf()) {
			stopSelf();
		} else {
			setNextAlarm();
		}
		
	}
	
	   /** 
     * Is the battery currently discharging? 
     * 
     * @return True if our battery is discharging.  False otherwise. 
     */ 
    public static boolean isBatteryDischarging(Context c) { 
    	try {
	        Intent batteryIntent = c.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); 
	        if (batteryIntent == null) {  
	            return true; 
	        } 
 
	        int batteryStatus = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0); 
	        if (batteryStatus == 0) {  
	            return true; 
	        } 
	        return batteryStatus == 0; 
    	} catch (Exception e) {
    		return true;
    	}
    } 
    
    private boolean isBatteryDischarging() {
    	return isBatteryDischarging(this);
    }
	
	
	private Runnable mSetTask = new Runnable() {

		@Override
		public void run() {
			mIsCounting = false;
			try { 
				mShowAlarmToast = false;
				setNextAlarm();
				long timeout = 1000;
				if (isBatteryDischarging()) {
					timeout *= mCurrentAlarmItem.getInt(AlarmItem.KEY_WL_TIMEOUT_BATT);
				} else {
					timeout *= mCurrentAlarmItem.getInt(AlarmItem.KEY_WL_TIMEOUT_PLUG);
				}
				mHandler.postDelayed(mStopTask, timeout);
			} catch (Exception e) {
				Intent i = new Intent(getBaseContext(), AalService.class);
				i.setAction(ACTION_SET_SILENT_ALARM);
				startService(i);
			}
		}
		
	};
	
	private Ringtone mRingtone;
	private Ringtone getAlarmRingtone() {
		Ringtone r = null;
		try {
			r = RingtoneManager.getRingtone(getBaseContext(), Uri.parse(mCurrentAlarmItem.getString(AlarmItem.KEY_BACKUP)));
			r.setStreamType(AudioManager.STREAM_ALARM);
		} catch (Exception e) {
			try {
				r = RingtoneManager.getRingtone(getBaseContext(), RingtoneManager.getValidRingtoneUri(getBaseContext()));
				r.setStreamType(AudioManager.STREAM_ALARM);
			} catch (Exception e2) {}
		}
		return r;
	}


	private Runnable mPlayRintoneTask = new Runnable() {

		@Override
		public void run() {
			try {
				if (mRingtone == null) {
					mRingtone = getAlarmRingtone();
				}
				while (mIsPlayingBackup) {
					if (!mRingtone.isPlaying()) {
						mRingtone.play();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}	
			} catch (Exception e) {
				
			}
			mRingtone = null;
		}
	};
	
	private Runnable mWifiWaitTask = new Runnable() {

		@Override
		public void run() {
			int timeout = 1000;
			timeout *= mCurrentAlarmItem.getInt(AlarmItem.KEY_WIFI_WAIT_TIME);
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {}
			if (mIsWaitingForWifi) {
				doWifiFailedAction();
			}
		}
		
	};
	
	private void doWifiFailedAction() {
		unregisterWifiReciever();
		
		switch(mCurrentAlarmItem.getInt(AlarmItem.KEY_WIFI_FAILED_ACTION)) {
		case AlarmItem.WIFI_FAILED_DO_NOTHING:
			stopOrSet();
			break;
		case AlarmItem.WIFI_FAILED_PLAY_BACKUP:
			mNotificationManager.notify(NOTIFY_ID, getNotification(R.string.as_nm_wf_plba));
			playBackupAlarm();
			mHandler.post(mSetTask);
			checkAndShowSnooze(false);
			break;
		case AlarmItem.WIFI_FAILED_LAUNCH_ALARM:
			mNotificationManager.notify(NOTIFY_ID, getNotification(R.string.as_nm_wf_la));
			launchAlarm(true);
			break;
		}
	}
	
	private Runnable mStopTask = new Runnable() {

		@Override
		public void run() {
			mIsPlayingBackup = false;
			try {
				mRingtone.stop();
				mRingtone = null;
			} catch (Exception e) {}
			
			if (mCurrentAlarmItem.getBool(AlarmItem.KEY_STOP_APP_ON_TIMEOUT)) {
				Intent i = new Intent(getBaseContext(), KillAndLaunchActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(KillAndLaunchActivity.EXTRA_PACKAGE_TO_RESTART, mCurrentAlarmItem.getString(AlarmItem.KEY_PACKAGE_NAME));
				startActivity(i);
			}
			stopSelf();
		}
		
	};
	
	private void playBackupAlarm() {
		mIsPlayingBackup = true;
		Thread t = new Thread(mPlayRintoneTask);
		t.setDaemon(true);
		t.start();
	}
	
	private void launchApp() {
		Intent targetIntent = getAlarmIntent();
		if (targetIntent != null) {
			
			
			
			if (Intent.ACTION_CALL.equals(targetIntent.getAction())) {
				AudioManager am = (AudioManager)getBaseContext().getSystemService(AUDIO_SERVICE);
				am.setMode(AudioManager.MODE_IN_CALL);
				am.setSpeakerphoneOn(true);
				am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FLAG_SHOW_UI);
			
				Thread t = new Thread(mSetSpeakerphoneTask);
				t.setDaemon(true);
				t.start();
			} 
			
			Intent i = new Intent(this, KillAndLaunchActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(KillAndLaunchActivity.EXTRA_INTENT_TO_LAUNCH, targetIntent);
			if (mCurrentAlarmItem.getBool(AlarmItem.KEY_FORCE_RESTART)) {
				i.putExtra(KillAndLaunchActivity.EXTRA_PACKAGE_TO_RESTART, mCurrentAlarmItem.getString(AlarmItem.KEY_PACKAGE_NAME));
			}
			startActivity(i);
			checkAndShowSnooze(true);
		} else {
			checkAndShowSnooze(false);
		}
	}
	
	private final Runnable mSetSpeakerphoneTask = new Runnable() {

		@Override
		public void run() {
			
			int i = 0;
			do
			{
				i+=1;
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (!isPhoneNotIdle() || (i < 5));
			AudioManager am = (AudioManager)getBaseContext().getSystemService(AUDIO_SERVICE);
			am.setMode(AudioManager.MODE_IN_CALL);
			if (!am.isSpeakerphoneOn()) {
				am.setSpeakerphoneOn(true);
				am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FLAG_SHOW_UI);
			}			
		}
		
	};
	

	
	private boolean isSafeToStopSelf() {
		return !(mIsPlayingBackup||mIsCounting);
	}
	
	
	private void setNextAlarm() {
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReciever.class), 0);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.cancel(sender);
		
    	Cursor c = mDbAdapter.fetchEnabledAlarms();
    	if (c != null) {
    		c.moveToFirst();
    		if (!c.isAfterLast()) {
    			long nextAlarmTimeInMilis = 0;
    			long nextAlarmId = 0;
    			
    			AlarmItem ai = null;
    			while(!c.isAfterLast()) {
    				ai = new AlarmItem(AlarmItem.ALARM_DEFAULTS_LIST, c);
    				if (nextAlarmTimeInMilis == 0) {
    					nextAlarmTimeInMilis = ai.getNextAbsoluteTimeInMillis();
    					nextAlarmId = ai.getInt(AlarmItem.KEY_ROWID);
    				} else {
    					long atim = ai.getNextAbsoluteTimeInMillis();
    					if (atim >0 && atim < nextAlarmTimeInMilis) {
    						nextAlarmTimeInMilis = atim;
    						nextAlarmId = ai.getInt(AlarmItem.KEY_ROWID);
    					}
    				}
    				c.moveToNext();
    			}
    			
    			saveNextAlarmPref(nextAlarmId);
    			

    			if (nextAlarmId != 0) {
    				alarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarmTimeInMilis, sender);
    				Calendar aC = Calendar.getInstance();
    				aC.setTimeInMillis(nextAlarmTimeInMilis);
    				String timeString = aC.getTime().toLocaleString();
    				if (mShowAlarmToast) {
    					Toast.makeText(this, "Next alarm set for:\n\n"+ timeString , Toast.LENGTH_LONG).show();
    					mShowAlarmToast = false;
    				}
    				showAlarmSetNotification(timeString);
    			} else {
    				mNotificationManager.cancel(NOTIFY_ID_ALARM_SET);
    			}
    		} else {
				mNotificationManager.cancel(NOTIFY_ID_ALARM_SET);
			}
    		c.close();
    	} else {
			mNotificationManager.cancel(NOTIFY_ID_ALARM_SET);
		}
	}
	private void setSnoozeAlarm() {
		Intent i = new Intent(this, SnoozeWakeupReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, i, 0);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.cancel(sender);
		if (mCurrentAlarmItem == null) {
			mCurrentAlarmItem = mDbAdapter.getAlarmById(getSnoozeAlarm());
		}
		long snoozeTime = mCurrentAlarmItem.getInt(AlarmItem.KEY_MUTE_SNOOZE_TIME) * 1000;
		snoozeTime += System.currentTimeMillis();
		alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTime, sender);
		showAlarmSnoozeNotification(AlarmItem.getTimeoutText(mCurrentAlarmItem.getInt(AlarmItem.KEY_MUTE_SNOOZE_TIME)));
	}
	private void cancelSnoozeAlarm() {
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, new Intent(this, SnoozeWakeupReceiver.class), 0);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.cancel(sender);
		cancelAlarmSnoozeNotification();
	}
	
	private void saveNextAlarmPref(long alarmId) {
		saveNextAlarmPref(getBaseContext(), alarmId);
	}
	public static void saveNextAlarmPref(Context c, long alarmId) {
		SharedPreferences.Editor spe = c.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).edit();
		spe.putLong(PREF_KEY_NEXT_ALARM_ID, alarmId);
		spe.commit();
	}
	private long loadNextAlarmPref() {
		SharedPreferences sp = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
		return sp.getLong(PREF_KEY_NEXT_ALARM_ID, 0);
	}
	private void saveShowNotifPref(boolean showNotif) {
		SharedPreferences.Editor spe = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).edit();
		spe.putBoolean(PREF_KEY_SHOW_NOTIF, showNotif);
		spe.commit();
		int tMsgRes = 0;
		if (showNotif) {
			tMsgRes = R.string.as_stat_on_msg;
		} else 
			tMsgRes = R.string.as_stat_off_msg;
		Toast.makeText(getBaseContext(), tMsgRes, Toast.LENGTH_LONG).show();
	}
	public static boolean loadShowNotifPref(Context c) {
		SharedPreferences sp = c.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
		return sp.getBoolean(PREF_KEY_SHOW_NOTIF, DEFAULT_SHOW_NOTIF);
	}
	private boolean loadShowNotifPref() {
		return loadShowNotifPref(this);
	}
	private void saveSnoozeRestartBackup(boolean doBackupRestart) {
		SharedPreferences.Editor spe = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).edit();
		spe.putBoolean(PREF_KEY_SNOOZE_RESTART_BACKUP_ALARM, doBackupRestart);
		spe.commit();
	}
	private boolean loadSnoozeRestartBackup() {
		SharedPreferences sp = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
		return sp.getBoolean(PREF_KEY_SNOOZE_RESTART_BACKUP_ALARM, false);
	}
	private void saveSnoozeAlarmId(long alarmId) {
		SharedPreferences.Editor spe = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).edit();
		spe.putLong(PREF_KEY_SNOOZE_ALARM, alarmId);
		spe.commit();
	}
	private long getSnoozeAlarm() {
		SharedPreferences sp = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
		return sp.getLong(PREF_KEY_SNOOZE_ALARM, 0);
	}
	private void saveSnoozeVolume(int vol) {
		SharedPreferences.Editor spe = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).edit();
		spe.putInt(PREF_KEY_SNOOZE_VOLUME, vol);
		spe.commit();
	}
	private int getSnoozeVolume() {
		SharedPreferences sp = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
		return sp.getInt(PREF_KEY_SNOOZE_VOLUME, 15);
	}
	
	
	public class WifiNetworkStateChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context c, Intent i) {
			NetworkInfo ni = (NetworkInfo)i.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (ni.isConnected()) {
				Intent fl = new Intent(c, AalService.class);
				fl.setAction(ACTION_FORCE_LAUNCH_ALARM);
				startService(fl);
			}
		}
		
	}

}
