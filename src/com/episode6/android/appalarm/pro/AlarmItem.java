package com.episode6.android.appalarm.pro;

import java.io.BufferedReader;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.widget.ImageView;

public class AlarmItem extends SimplePropertyCollection {
	public AlarmItem() {
		super(ALARM_DEFAULTS_ALL);
	}
	public AlarmItem(BufferedReader reader, boolean skipId) throws Exception {
		super(ALARM_DEFAULTS_ALL, reader, skipId, KEY_ROWID);
//		LiteHelper.setProFeatures(this);
	}
	public AlarmItem(SimpleProperty[] defaults, Cursor cur) {
		super(defaults, cur);
//		LiteHelper.setProFeatures(this);
	}
	public AlarmItem(Cursor cur) {
		super(ALARM_DEFAULTS_ALL, cur);
//		LiteHelper.setProFeatures(this);
	}


	public static final String KEY_ROWID = "_id";
	public static final String KEY_HOUR = "alarm_hour";
	public static final String KEY_MINUTE = "alarm_minute";
	public static final String KEY_RPT_MON = "alarm_rpt_mon";
	public static final String KEY_RPT_TUES = "alarm_rpt_tues";
	public static final String KEY_RPT_WED = "alarm_rpt_wed";
	public static final String KEY_RPT_THURS = "alarm_rpt_thurs";
	public static final String KEY_RPT_FRI = "alarm_rpt_fri";
	public static final String KEY_RPT_SAT = "alarm_rpt_sat";
	public static final String KEY_RPT_SUN = "alarm_rpt_sun";
	public static final String KEY_BACKUP = "backup_alarm";
	public static final String KEY_ENABLED = "alarm_enabled";
	public static final String KEY_NET_TEST = "alarm_net_test";
	public static final String KEY_NET_TEST_URL = "alarm_net_test_url";
	public static final String KEY_BACKUP_OPTION = "alarm_backup_option";
	public static final String KEY_PACKAGE_NAME = "alarm_package_name";
	public static final String KEY_CUSTOM_ACTION = "alarm_custom_action";
	public static final String KEY_CUSTOM_DATA = "alarm_custom_data";
	public static final String KEY_CUSTOM_TYPE = "alarm_custom_type";
	public static final String KEY_WIFI = "alarm_wifi";
	public static final String KEY_WL_TIMEOUT_BATT = "alarm_wl_timeout_batt";
	public static final String KEY_WL_TIMEOUT_PLUG = "alarm_wl_timeout_plug";
	public static final String KEY_SET_MEDIA_VOLUME = "alarm_set_media_volume";
	public static final String KEY_MEDIA_VOLUME = "alarm_media_volume";
	public static final String KEY_DONT_LAUNCH_ON_CALL = "alarm_dont_launch_call";
	public static final String KEY_WIFI_WAIT_TIME = "alarm_wifi_wait_time";
	public static final String KEY_WIFI_FAILED_ACTION = "alarm_wifi_failed_action";
	public static final String KEY_TURN_OFF_WIFI = "alarm_turn_off_wifi";
	public static final String KEY_STOP_APP_ON_TIMEOUT = "alarm_stop_app";
	public static final String KEY_MUTE_SNOOZE = "alarm_mute_snooze";
	public static final String KEY_MUTE_SNOOZE_TIME = "alarm_mute_snooze_time";
	public static final String KEY_FORCE_RESTART = "alarm_force_restart";
	public static final String KEY_LABEL = "alarm_label";

	public static final String[] WIFI_FAILED_OPTIONS = {"Cancel Alarm", "Play Backup Alarm", "Launch Alarm Normally"};
	public static final int WIFI_FAILED_DO_NOTHING = 0;
	public static final int WIFI_FAILED_PLAY_BACKUP = 1;
	public static final int WIFI_FAILED_LAUNCH_ALARM = 2;
	
	
	public static final SimpleProperty[] ALARM_DEFAULTS_ALL = new SimpleProperty[] {
		new SimpleProperty(KEY_ROWID, 0),
		new SimpleProperty(KEY_HOUR, 12),
		new SimpleProperty(KEY_MINUTE, 0),
		new SimpleProperty(KEY_RPT_MON, false),
		new SimpleProperty(KEY_RPT_TUES, false),
		new SimpleProperty(KEY_RPT_WED, false),
		new SimpleProperty(KEY_RPT_THURS, false),
		new SimpleProperty(KEY_RPT_FRI, false),
		new SimpleProperty(KEY_RPT_SAT, false),
		new SimpleProperty(KEY_RPT_SUN, false),
		new SimpleProperty(KEY_BACKUP, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString()),
		new SimpleProperty(KEY_ENABLED, false),
		new SimpleProperty(KEY_NET_TEST, false),
		new SimpleProperty(KEY_NET_TEST_URL, "http://google.com"),
		new SimpleProperty(KEY_BACKUP_OPTION, 0),
		new SimpleProperty(KEY_PACKAGE_NAME, ""),
		new SimpleProperty(KEY_CUSTOM_ACTION, ""),
		new SimpleProperty(KEY_CUSTOM_DATA, ""),
		new SimpleProperty(KEY_CUSTOM_TYPE, ""),
		new SimpleProperty(KEY_WIFI, false),
		new SimpleProperty(KEY_WL_TIMEOUT_BATT, 600),
		new SimpleProperty(KEY_WL_TIMEOUT_PLUG, 1200),
		new SimpleProperty(KEY_SET_MEDIA_VOLUME, false),
		new SimpleProperty(KEY_MEDIA_VOLUME, 15),
		new SimpleProperty(KEY_DONT_LAUNCH_ON_CALL, true),
		new SimpleProperty(KEY_WIFI_WAIT_TIME, 300),
		new SimpleProperty(KEY_WIFI_FAILED_ACTION, 1),
		new SimpleProperty(KEY_TURN_OFF_WIFI, false),
		new SimpleProperty(KEY_STOP_APP_ON_TIMEOUT, false),
		new SimpleProperty(KEY_MUTE_SNOOZE, false),
		new SimpleProperty(KEY_MUTE_SNOOZE_TIME, 300),
		new SimpleProperty(KEY_FORCE_RESTART, true),
		new SimpleProperty(KEY_LABEL, "")
	};
	
	public static final SimpleProperty[] ALARM_DEFAULTS_LIST = new SimpleProperty[] {
		new SimpleProperty(KEY_ROWID, SimpleProperty.TYPE_INT, 0),
		new SimpleProperty(KEY_HOUR, SimpleProperty.TYPE_INT, 0),
		new SimpleProperty(KEY_MINUTE, SimpleProperty.TYPE_INT, 0),
		new SimpleProperty(KEY_RPT_MON, SimpleProperty.TYPE_BOOL, 0),
		new SimpleProperty(KEY_RPT_TUES, SimpleProperty.TYPE_BOOL, 0),
		new SimpleProperty(KEY_RPT_WED, SimpleProperty.TYPE_BOOL, 0),
		new SimpleProperty(KEY_RPT_THURS, SimpleProperty.TYPE_BOOL, 0),
		new SimpleProperty(KEY_RPT_FRI, SimpleProperty.TYPE_BOOL, 0),
		new SimpleProperty(KEY_RPT_SAT, SimpleProperty.TYPE_BOOL, 0),
		new SimpleProperty(KEY_RPT_SUN, SimpleProperty.TYPE_BOOL, 0),
		new SimpleProperty(KEY_ENABLED, SimpleProperty.TYPE_BOOL, 0),
		new SimpleProperty(KEY_PACKAGE_NAME, SimpleProperty.TYPE_TEXT, 0),
		new SimpleProperty(KEY_CUSTOM_ACTION, SimpleProperty.TYPE_TEXT, 0),
		new SimpleProperty(KEY_CUSTOM_DATA, SimpleProperty.TYPE_TEXT, 0),
		new SimpleProperty(KEY_LABEL, SimpleProperty.TYPE_TEXT, 0)
	};
	
	
	public static final String[] BACKUP_OPTIONS = {"Disabled", "Always Play", "Only if Network Test Fails"};
	
	
	public String getAlarmText() {
		return getAlarmText(getInt(KEY_HOUR), getInt(KEY_MINUTE));
	}
	
	public static String getAlarmText(int hour, int minute) {
		String str = "";
		String hr = hour + "";
		String mn = minute + "";
		String ap = " PM";
		
		if (hour < 12) {
			ap = " AM";
		} else if (hour > 12) {
			hr = (hour-12) + "";
		}
		if (hour == 0) {
			hr = "12";
		}
		
		if (minute < 10) {
			mn = "0" + minute;
		}
		
		str = hr + ":" + mn + ap;
		return str;
	}
	
	public String getRepeatText() {
		
		return getRepeatText(getBool(KEY_RPT_MON), getBool(KEY_RPT_TUES), getBool(KEY_RPT_WED), getBool(KEY_RPT_THURS) ,getBool(KEY_RPT_FRI), getBool(KEY_RPT_SAT), getBool(KEY_RPT_SUN));
	}
	
	public static String getRepeatText(boolean mon, boolean tue, boolean wed, boolean thur, boolean fri, boolean sat, boolean sun) {
		String str = "";
		if (mon) {
			str = addDayToRepeatText(str, "Mon");
		}
		if (tue) {
			str = addDayToRepeatText(str, "Tues");
		}
		if (wed) {
			str = addDayToRepeatText(str, "Wed");
		}
		if (thur) {
			str = addDayToRepeatText(str, "Thur");
		}
		if (fri) {
			str = addDayToRepeatText(str, "Fri");
		}
		if (sat) {
			str = addDayToRepeatText(str, "Sat");
		}
		if (sun) {
			str = addDayToRepeatText(str, "Sun");
		}
		if (str.contentEquals(""))
			str = "Never repeat";
		return str;
	}
	
	private static String addDayToRepeatText(String str, String day) {
		if (!str.contentEquals(""))
			str += ", ";
		return str + day;
	}
	
	public boolean hasRepeat() {
		if (getBool(KEY_RPT_MON) || getBool(KEY_RPT_TUES) || getBool(KEY_RPT_WED) || getBool(KEY_RPT_THURS) || getBool(KEY_RPT_FRI) || getBool(KEY_RPT_SAT) || getBool(KEY_RPT_SUN)) {
			return true;
		} else {
			return false;
		}
	}
	
	private ContentValues getRptVals() {
		ContentValues cv = new ContentValues();
		cv.put(Calendar.SUNDAY+"", getBool(KEY_RPT_SUN));
		cv.put(Calendar.MONDAY+"", getBool(KEY_RPT_MON));
		cv.put(Calendar.TUESDAY+"", getBool(KEY_RPT_TUES));
		cv.put(Calendar.WEDNESDAY+"", getBool(KEY_RPT_WED));
		cv.put(Calendar.THURSDAY+"", getBool(KEY_RPT_THURS));
		cv.put(Calendar.FRIDAY+"", getBool(KEY_RPT_FRI));
		cv.put(Calendar.SATURDAY+"", getBool(KEY_RPT_SAT));
		return cv;
	}
	
	public Calendar getNextAlarmCalendar() {
		Calendar todayCal = Calendar.getInstance();
		todayCal.setTimeInMillis(System.currentTimeMillis());
		
		Calendar aCal = Calendar.getInstance();
		aCal.setTimeInMillis(todayCal.getTimeInMillis());
		aCal.set(Calendar.HOUR_OF_DAY, getInt(KEY_HOUR));
		aCal.set(Calendar.MINUTE, getInt(KEY_MINUTE));
		aCal.set(Calendar.SECOND, 0);
		
		if(todayCal.compareTo(aCal) == 1) {
			aCal.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		if (hasRepeat()) {
			int day = aCal.get(Calendar.DAY_OF_WEEK);
			ContentValues cv = getRptVals();
			while (!cv.getAsBoolean(day+"")) {
				aCal.add(Calendar.DAY_OF_YEAR, 1);
				day = aCal.get(Calendar.DAY_OF_WEEK);
			}
		}
		return aCal;
	}
	
	public long getNextAbsoluteTimeInMillis() {
		return getNextAlarmCalendar().getTimeInMillis();
	}
	
	public String getNextTimeString() {
//		String rStr = "";
		Calendar c = getNextAlarmCalendar();
		return c.getTime().toLocaleString();
	}
	
	public boolean isNew() {
		return (getInt(KEY_ROWID) == 0);
	}
	
	public boolean isShortcutIntent() {
		return isShortcutIntent(getString(KEY_CUSTOM_DATA));
	}
	public static boolean isShortcutIntent(String data) {
		String lcase = data.toLowerCase();
		return lcase.startsWith("intent:") || lcase.contains("#intent");
	}
	public boolean isCustomIntent() {
		return getString(KEY_PACKAGE_NAME).equals("custom");
	}
	public boolean hasPackageName() {
		if (getString(KEY_PACKAGE_NAME) == null || getString(KEY_PACKAGE_NAME).equals("")) {
			return false;
		} else {
			return !isCustomIntent();
		}
	}
	
	public String getAppName(PackageManager pm) {
		if (getString(KEY_PACKAGE_NAME) == null || getString(KEY_PACKAGE_NAME).equals("")) {
			return "No app selected.";
		}
		if (isShortcutIntent()) {
			return getString(KEY_CUSTOM_ACTION);
		}
		if (isCustomIntent()) {
			return "Custom";
		}
		
		String rtr = "";
		try {
			rtr = (String) pm.getApplicationInfo(getString(KEY_PACKAGE_NAME), 0).loadLabel(pm);
		} catch (Exception e) {
			e.printStackTrace();
			rtr = "ERROR LOADING APP NAME";
		}
		return rtr;
	} 
	
//	public boolean isAppPandoraStation() {
//		if (packageName != null) {
//			if (packageName.equals("custom") && customData != null) {
//				if (customData.startsWith(PandoraPicker.PANDORA_URI_STRING)) {
//					return true;
//				}
//			} else if (packageName.equals(PandoraPicker.PANDORA_PACKAGE_NAME)) {
//    			if (customData.startsWith("intent:")) {
//    				return true;
//    			}
//    		}
//		}
//		
//		return false;
//	}
	
	public void setAppIconInImageView(ImageView iv, PackageManager pm) {
		if (!hasPackageName()) {
			iv.setImageResource(R.drawable.icon);
		} else {
    		try {
				iv.setImageDrawable(pm.getApplicationIcon(getString(KEY_PACKAGE_NAME)));
			} catch (Exception e) {
				e.printStackTrace();
				iv.setImageResource(R.drawable.icon);
			}
		}
		
	}
	
	public String getMuteSnoozeTimeText() {
		return "Mute-Snooze time is set for " + getTimeoutText(getInt(KEY_MUTE_SNOOZE_TIME));
	}
	public String getBattTimeoutText() {
		return "The alarm service will stop running after " + getTimeoutText(getInt(KEY_WL_TIMEOUT_BATT)) + " while running on battery power.";
	}
	public String getPlugTimeoutText() {
		return "The alarm service will stop running after " + getTimeoutText(getInt(KEY_WL_TIMEOUT_PLUG)) + " while charging.";
	}
	public String getWifiWaitTimeText() {
		return "The alarm service will wait " + getTimeoutText(getInt(KEY_WIFI_WAIT_TIME)) + " for a wi-fi connection before declaring it a failure.";
	}
	public String getWifiFailedActionText() {
		switch (getInt(KEY_WIFI_FAILED_ACTION)) {
		case WIFI_FAILED_DO_NOTHING:
			return "Do nothing. Cancel the alarm if the wi-fi connection fails.";
		case WIFI_FAILED_PLAY_BACKUP:
			return "Skip the app, just play the backup alarm (this will also skip the standard network test).";
		case WIFI_FAILED_LAUNCH_ALARM:
			return "Launch the alarm normally (network test and backup alarm rules will apply).";
		}
		return "";
	}

	
	public static String getTimeoutText(int timeoutInSeconds) {
		int wD = timeoutInSeconds;
		String rtr = "";
		
		if (wD%3600 == 0) {
			wD /= 3600;
			rtr += wD + " hour";
		} else if (wD%60 == 0) {
			wD /= 60;
			rtr += wD + " minute";
		} else {
			rtr += wD + " second";
		}
		
		if (wD > 1) {
			rtr += "s";
		}
		
		return rtr;
	}
	
	public String getMediaVolumeText() {
		return "The media volume will be set to " + getInt(KEY_MEDIA_VOLUME) + " before the app is launched.";
	}
	
	public String getLabel(PackageManager pm) {
		String label = getString(KEY_LABEL);
		if (label == null || label.equals("")) {
			label = getAppName(pm);
		}
		return label.trim();
	}

}


