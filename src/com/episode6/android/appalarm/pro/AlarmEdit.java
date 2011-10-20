package com.episode6.android.appalarm.pro;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmEdit extends Activity {
	
	private static final int MENU_OTHER_APPS = Menu.FIRST+1;
	private static final int MENU_SUPPORT = Menu.FIRST+2;
	private static final int MENU_SHARE_INTENT = Menu.FIRST+3;
	
	private static final int DIALOG_PICK_TIME = 1;
	private static final int DIALOG_PICK_BACKUP_OPTION = 2;
	private static final int DIALOG_PICK_APP_TYPE = 3;
	private static final int DIALOG_PICK_WIFI_FAILED_ACTION = 4;
	private static final int DIALOG_WARN_STOP_APP = 5;
	private static final int DIALOG_BITLY = 6;
	private static final String[] APP_TYPE_OPTIONS = {"Choose App", "Create Shortcut", "Home Screen Shortcut", "Pandora Radio Station", "Tunewiki Shoutcast Station", "Custom Intent", "Clear App Selection"};
	
	private static final int ACTION_CHOOSE_APP = 2;
	private static final int ACTION_CHOOSE_REPEAT = 3;
	private static final int ACTION_CHOOSE_RINGTONE = 4;
	private static final int ACTION_CHOOSE_MEDIA_VOLUME = 5;
	private static final int ACTION_CUSTOM_INTENT = 6;
	private static final int ACTION_CHOOSE_TIMEOUT_BATT = 7;
	private static final int ACTION_CHOOSE_TIMEOUT_PLUG = 8;
	private static final int ACTION_INPUT_NET_TEST_URL = 9;
	private static final int ACTION_CHOOSE_WIFI_WAIT_TIME = 10;
	private static final int ACTION_CHOOSE_FROM_PROVIDER = 11;
	private static final int ACTION_CHOOSE_MUTE_SNOOZE_TIME = 12;
	private static final int ACTION_CREATE_HOME_SCREEN_SHORTCUT = 14;
	private static final int FETCH_HOME_SCREEN_SHORTCUT = 15;
	private static final int ACTION_CHOOSE_APP_CUSTOM = 16;
	private static final int ACTION_INPUT_LABEL = 17;
	
	private static final int CTX_EDIT_INTENT = 18;
	private static final int CTX_SHARE_INTENT = 19;
	
	private View mRootView;
	private ScrollView mSvScroller;
	private LinearLayout mLlEnabled, mLlTime, mLlAppSelect, mLlRepeat, mLlDontLaunchOnCall, mLlNetTest, mLlNetTestUrl, mLlBackup, mLlBackupOption, mLlWifi, mLlWlBattTimeout, mLlWlPlugTimeout, mLlSetMediaVolume, mLlMediaVolume, mLlWifiOptions, mLlWifiWaitTime, mLlWifiFailedAction, mLlTurnOffWifi, mLlStopApp, mLlForceRestart, mLlMuteSnooze, mLlMuteSnoozeTime, mLlLabel;
	private CheckBox mChkEnabled, mChkDontLaunchOnCall, mChkNetTest, mChkWifi, mChkSetMediaVolume, mChkTurnOffWifi, mChkStopApp, mChkForceRestart, mChkMuteSnooze;
	private TextView mTvTime, mTvApp, mTvRepeat, mTvBackupOption, mTvBackup, mTvNetTestUrl, mTvWlBattTimeout, mTvWlPlugTimeout, mTvMediaVolume, mTvWifiWaitTime, mTvWifiFailedAction, mTvMuteSnoozeTime, mTvLabel;
	private ImageView mIvAppIcon;
	
	private AalDbAdapter mDbAdapter;
	private AlarmItem mAlarmItem;
	
	private String mUrl;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_edit);
		mRootView = findViewById(R.id.ea_ll_root);
		
		mDbAdapter = new AalDbAdapter(this);
		mDbAdapter.open();
		
		findViews();
		loadAlarmFromIntent();
		assignListeners();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(mRootView);
		
//		findViews();
//		loadAlarmFromGlobal();
//		assignListeners();
	}
	
	private void findViews() {
		mSvScroller = (ScrollView)findViewById(R.id.ea_sv_full_scroller);
		
		mLlEnabled = (LinearLayout)findViewById(R.id.ea_ll_alarm_enabled);
		mLlTime = (LinearLayout)findViewById(R.id.ea_ll_alarm_time);
		mLlAppSelect = (LinearLayout)findViewById(R.id.ea_ll_app);
		mLlNetTest = (LinearLayout)findViewById(R.id.ea_ll_net_test);
		mLlNetTestUrl = (LinearLayout)findViewById(R.id.ea_ll_net_test_url);
		mLlRepeat = (LinearLayout)findViewById(R.id.ea_ll_alarm_repeat);
		mLlBackup = (LinearLayout)findViewById(R.id.ea_ll_backup_alarm);
		mLlBackupOption = (LinearLayout)findViewById(R.id.ea_ll_backup_option);
		mLlWifi = (LinearLayout)findViewById(R.id.ea_ll_turn_on_wifi);
		mLlWlBattTimeout = (LinearLayout)findViewById(R.id.ea_ll_wl_timeout_batt);
		mLlWlPlugTimeout = (LinearLayout)findViewById(R.id.ea_ll_wl_timeout_plug);
		mLlSetMediaVolume = (LinearLayout)findViewById(R.id.ea_ll_set_media_volume);
		mLlMediaVolume = (LinearLayout)findViewById(R.id.ea_ll_media_volume);
		mLlDontLaunchOnCall = (LinearLayout)findViewById(R.id.ea_ll_dont_launch_on_call);
		mLlWifiOptions = (LinearLayout)findViewById(R.id.ea_ll_wifi_options);
		mLlWifiWaitTime = (LinearLayout)findViewById(R.id.ea_ll_wifi_wait_time);
		mLlWifiFailedAction = (LinearLayout)findViewById(R.id.ea_ll_wifi_failed_action);
		mLlTurnOffWifi = (LinearLayout)findViewById(R.id.ea_ll_turn_off_wifi);
		mLlStopApp = (LinearLayout)findViewById(R.id.ea_ll_stop_app);
		mLlForceRestart = (LinearLayout)findViewById(R.id.ea_ll_force_restart);
		mLlMuteSnooze = (LinearLayout)findViewById(R.id.ea_ll_mute_snooze);
		mLlMuteSnoozeTime = (LinearLayout)findViewById(R.id.ea_ll_mute_snooze_time);
		mLlLabel = (LinearLayout)findViewById(R.id.ea_ll_label);
		
		mChkEnabled = (CheckBox)findViewById(R.id.ea_chk_alarm_enabled);
		mChkNetTest = (CheckBox)findViewById(R.id.ea_chk_net_test);
		mChkDontLaunchOnCall = (CheckBox)findViewById(R.id.ea_chk_dont_launch_on_call);
		mChkWifi = (CheckBox)findViewById(R.id.ea_chk_turn_on_wifi);
		mChkSetMediaVolume = (CheckBox)findViewById(R.id.ea_chk_set_media_volume);
		mChkTurnOffWifi = (CheckBox)findViewById(R.id.ea_chk_turn_off_wifi);
		mChkStopApp = (CheckBox)findViewById(R.id.ea_chk_stop_app);
		mChkForceRestart = (CheckBox)findViewById(R.id.ea_chk_force_restart);
		mChkMuteSnooze = (CheckBox)findViewById(R.id.ea_chk_mute_snooze);
		
		mTvTime = (TextView)findViewById(R.id.ea_tv_time);
		mTvApp = (TextView)findViewById(R.id.ea_tv_app_name);
		mTvRepeat = (TextView)findViewById(R.id.ea_tv_alarm_repeat);
		mTvBackupOption = (TextView)findViewById(R.id.ea_tv_backup_option);
		mTvBackup = (TextView)findViewById(R.id.ea_tv_backup_alarm);
		mTvNetTestUrl = (TextView)findViewById(R.id.ea_tv_net_test_url);
		mTvWlBattTimeout = (TextView)findViewById(R.id.ea_tv_wl_timeout_batt);
		mTvWlPlugTimeout = (TextView)findViewById(R.id.ea_tv_wl_timeout_plug);
		mTvMediaVolume = (TextView)findViewById(R.id.ea_tv_media_volume);
		mTvWifiWaitTime = (TextView)findViewById(R.id.ea_tv_wifi_wait_time);
		mTvWifiFailedAction = (TextView)findViewById(R.id.ea_tv_wifi_failed_action);
		mTvMuteSnoozeTime = (TextView)findViewById(R.id.ea_tv_mute_snooze_time);
		mTvLabel = (TextView)findViewById(R.id.ea_tv_label);
		
		mIvAppIcon = (ImageView)findViewById(R.id.ea_iv_app_icon);
		
	}
	
	private void loadAlarmFromIntent() {
		Intent i = getIntent();
		if (i.hasExtra(AlarmItem.KEY_ROWID)) {
			mAlarmItem = mDbAdapter.getAlarmById(i.getLongExtra(AlarmItem.KEY_ROWID, 0));
		} else {
			mAlarmItem = mDbAdapter.getNewAlarm();
		}
		
		loadAlarmFromGlobal();
	}
	
	private void loadAlarmFromGlobal() {
		
		
		mChkEnabled.setChecked(mAlarmItem.getBool(AlarmItem.KEY_ENABLED));
		mChkDontLaunchOnCall.setChecked(mAlarmItem.getBool(AlarmItem.KEY_DONT_LAUNCH_ON_CALL));
		mChkNetTest.setChecked(mAlarmItem.getBool(AlarmItem.KEY_NET_TEST));
		mChkWifi.setChecked(mAlarmItem.getBool(AlarmItem.KEY_WIFI));
		mChkSetMediaVolume.setChecked(mAlarmItem.getBool(AlarmItem.KEY_SET_MEDIA_VOLUME));
		mChkTurnOffWifi.setChecked(mAlarmItem.getBool(AlarmItem.KEY_TURN_OFF_WIFI));
		mChkStopApp.setChecked(mAlarmItem.getBool(AlarmItem.KEY_STOP_APP_ON_TIMEOUT));
		mChkForceRestart.setChecked(mAlarmItem.getBool(AlarmItem.KEY_FORCE_RESTART));
		mChkMuteSnooze.setChecked(mAlarmItem.getBool(AlarmItem.KEY_MUTE_SNOOZE));
		
		vUpdateTime();
		vUpdateApp();
		vUpdateRepeat();
		vUpdateMuteSnooze();
		vUpdateBackup(true);
		vUpdateNetTest();
		vUpdateBattTimeout();
		vUpdatePlugTimeout();
		vUpdateMediaVolume(false);
		vUpdateWifi();
		vUpdateLabel();
		
	}
	
	
	private void vUpdateTime() {
		mTvTime.setText(mAlarmItem.getAlarmText());
	}
	private void vUpdateApp() {
		PackageManager pm = getPackageManager();
		mTvApp.setText(mAlarmItem.getAppName(pm));
		mAlarmItem.setAppIconInImageView(mIvAppIcon, pm);
		checkCustomAppPackage();
		pm = null;
	}
	private void vUpdateLabel() {
		String label = mAlarmItem.getString(AlarmItem.KEY_LABEL);
		if (label == null || label.trim().equals("")) {
			label = getString(R.string.ea_label_msg);
		}
		mTvLabel.setText(label);
	}
	private void vUpdateRepeat() {
		mTvRepeat.setText(mAlarmItem.getRepeatText());
	}
	private void vUpdateBackup(boolean reloadBackupName) {
		mTvBackupOption.setText(AlarmItem.BACKUP_OPTIONS[mAlarmItem.getInt(AlarmItem.KEY_BACKUP_OPTION)]);
		if (mAlarmItem.getInt(AlarmItem.KEY_BACKUP_OPTION) != 0 || (mAlarmItem.getBool(AlarmItem.KEY_WIFI) && mAlarmItem.getInt(AlarmItem.KEY_WIFI_FAILED_ACTION) == AlarmItem.WIFI_FAILED_PLAY_BACKUP)) {
			mLlBackup.setVisibility(View.VISIBLE);
		} else {
			mLlBackup.setVisibility(View.GONE);
		}
		if (reloadBackupName) {
			try {
				Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(mAlarmItem.getString(AlarmItem.KEY_BACKUP)));
				mTvBackup.setText(r.getTitle(this));
			}
			catch (Exception e) {
				try {
					mAlarmItem.set(AlarmItem.KEY_BACKUP, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString());
					Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(mAlarmItem.getString(AlarmItem.KEY_BACKUP)));
					mTvBackup.setText(r.getTitle(this));
				} catch (Exception e2) {
					try {
						mAlarmItem.set(AlarmItem.KEY_BACKUP, RingtoneManager.getValidRingtoneUri(getBaseContext()).toString());
						Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(mAlarmItem.getString(AlarmItem.KEY_BACKUP)));
						mTvBackup.setText(r.getTitle(this));
					} catch (Exception e3) {
						mTvBackup.setText("Error reading ringtone, please select one.");
					}
				}
				
			}
		}
	}
	private void vUpdateNetTest() {
		if (mAlarmItem.getBool(AlarmItem.KEY_NET_TEST)) {
			mLlNetTestUrl.setVisibility(View.VISIBLE);
			mTvNetTestUrl.setText(mAlarmItem.getString(AlarmItem.KEY_NET_TEST_URL));
		} else {
			mLlNetTestUrl.setVisibility(View.GONE);
		}
	}
	private void vUpdateBattTimeout() {
		mTvWlBattTimeout.setText(mAlarmItem.getBattTimeoutText());
	}
	private void vUpdatePlugTimeout() {
		mTvWlPlugTimeout.setText(mAlarmItem.getPlugTimeoutText());
	}

	private void vUpdateMediaVolume(boolean doScroll) {
		if (mAlarmItem.getBool(AlarmItem.KEY_SET_MEDIA_VOLUME)) {
			mLlMediaVolume.setVisibility(View.VISIBLE);
			mTvMediaVolume.setText(mAlarmItem.getMediaVolumeText());
			if (doScroll) {
				mSvScroller.smoothScrollBy(0, 100);
			}
		} else {
			mLlMediaVolume.setVisibility(View.GONE);
		}
	}
	private void vUpdateWifi() {
		if (mAlarmItem.getBool(AlarmItem.KEY_WIFI)) {
			mLlWifiOptions.setVisibility(View.VISIBLE);
			mTvWifiWaitTime.setText(mAlarmItem.getWifiWaitTimeText());
			mTvWifiFailedAction.setText(mAlarmItem.getWifiFailedActionText());
		} else {
			mLlWifiOptions.setVisibility(View.GONE);
		}
		vUpdateBackup(false);
	}
	private void vUpdateMuteSnooze() {
		mTvMuteSnoozeTime.setText(mAlarmItem.getMuteSnoozeTimeText());
			
	}
	
	private void assignListeners() {
		mLlEnabled.setOnClickListener(mLlEnabledOnClick);
		mLlTime.setOnClickListener(mLlTimeOnClick);
		mLlAppSelect.setOnClickListener(mLlAppSelectOnClick);
		mLlAppSelect.setOnCreateContextMenuListener(mLlAppOnCreateContext);
		mLlRepeat.setOnClickListener(mLlRepeatOnClick);
		mLlDontLaunchOnCall.setOnClickListener(mLlDontLaunchOnCallOnClick);
		mLlNetTest.setOnClickListener(mLlNetTestOnClick);
		mLlBackup.setOnClickListener(mLlBackupOnClick);
		mLlBackupOption.setOnClickListener(mLlBackupOptionOnClick);
		mLlNetTestUrl.setOnClickListener(mLlNetTestUrlOnClick);
		mLlWifi.setOnClickListener(mLlWifiOnClick);
		mLlWlBattTimeout.setOnClickListener(mLlWlBattTimeoutOnClick);
		mLlWlPlugTimeout.setOnClickListener(mLlWlPlugTimeoutOnClick);
		mLlSetMediaVolume.setOnClickListener(mLlSetMediaVolumeOnClick);
		mLlMediaVolume.setOnClickListener(mLlMediaVolumeOnClick);
		mLlWifiWaitTime.setOnClickListener(mLlWifiWaitTimeOnClick);
		mLlWifiFailedAction.setOnClickListener(mLlWifiFailedActionOnClick);
		mLlTurnOffWifi.setOnClickListener(mLlTurnOffWifiOnClick);
		mLlStopApp.setOnClickListener(mLlStopAppOnClick);
		mLlForceRestart.setOnClickListener(mLlForceRestartOnClick);
		mLlMuteSnooze.setOnClickListener(mLlMuteSnoozeOnClick);
		mLlMuteSnoozeTime.setOnClickListener(mLlMuteSnoozeTimeOnClick);
		mLlLabel.setOnClickListener(mLlLabelOnClick);
		
		mChkEnabled.setOnCheckedChangeListener(mChkEnabledOnChange);
		mChkDontLaunchOnCall.setOnCheckedChangeListener(mChkDontLaunchOnCallOnChange);
		mChkNetTest.setOnCheckedChangeListener(mChkNetTestOnChange);
		mChkWifi.setOnCheckedChangeListener(mChkWifiOnChange);
		mChkSetMediaVolume.setOnCheckedChangeListener(mChkSetMediaVolumeOnChange);
		mChkTurnOffWifi.setOnCheckedChangeListener(mChkTurnOffWifiOnChange);
		mChkStopApp.setOnCheckedChangeListener(mChkStopAppOnChange);
		mChkForceRestart.setOnCheckedChangeListener(mChkForceRestartOnChange);
		mChkMuteSnooze.setOnCheckedChangeListener(mChkMuteSnooeOnChange);
		
//		((Button)findViewById(R.id.m_btn_pro)).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				LiteHelper.getProVersion(getBaseContext());
//			}
//		});
	}
	
	private void doAlarmSet() {
		mDbAdapter.saveAlarm(mAlarmItem);
		Intent i = new Intent(this, AalService.class);
    	i.setAction(AalService.ACTION_SET_SILENT_ALARM);
    	startService(i);
	}

	@Override
	protected void onDestroy() {
		mDbAdapter.close();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mDbAdapter.saveAlarm(mAlarmItem);
		super.onPause();
	}
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ACTION_CHOOSE_APP:
				mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, data.getStringExtra(AppChooser.EXTRA_PACKAGE_NAME));
				mAlarmItem.set(AlarmItem.KEY_CUSTOM_ACTION, "");
				mAlarmItem.set(AlarmItem.KEY_CUSTOM_DATA, "");
				mAlarmItem.set(AlarmItem.KEY_CUSTOM_TYPE, "");
				vUpdateApp();
				break;
			case ACTION_INPUT_LABEL:
				mAlarmItem.set(AlarmItem.KEY_LABEL, data.getStringExtra(StringInputDialog.EXTRA_VALUE));
				vUpdateLabel();
				break;
			case ACTION_CHOOSE_APP_CUSTOM:
				mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, data.getStringExtra(AppChooser.EXTRA_PACKAGE_NAME));
				vUpdateApp();
				break;
			case ACTION_CHOOSE_FROM_PROVIDER:
				processShortcut(data);
				break;
			case ACTION_CUSTOM_INTENT:
				String dataString = data.getStringExtra(AlarmItem.KEY_CUSTOM_DATA);
				mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, "");
				mAlarmItem.set(AlarmItem.KEY_CUSTOM_ACTION, data.getStringExtra(AlarmItem.KEY_CUSTOM_ACTION));
				mAlarmItem.set(AlarmItem.KEY_CUSTOM_DATA, dataString);
				mAlarmItem.set(AlarmItem.KEY_CUSTOM_TYPE, data.getStringExtra(AlarmItem.KEY_CUSTOM_TYPE));
				if (mAlarmItem.isShortcutIntent()) {
					try {
						mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, Intent.getIntent(mAlarmItem.getString(AlarmItem.KEY_CUSTOM_DATA)).getComponent().getPackageName());
					} catch (URISyntaxException e) {
						mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, "custom");
						e.printStackTrace();
					}
				}
				if (mAlarmItem.getString(AlarmItem.KEY_PACKAGE_NAME).equals("")) {
					mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, "custom");
				}
				vUpdateApp();
				break;
			case ACTION_CHOOSE_REPEAT:
				mAlarmItem.set(AlarmItem.KEY_RPT_MON, data.getBooleanExtra(AlarmItem.KEY_RPT_MON, false));
				mAlarmItem.set(AlarmItem.KEY_RPT_TUES, data.getBooleanExtra(AlarmItem.KEY_RPT_TUES, false));
				mAlarmItem.set(AlarmItem.KEY_RPT_WED, data.getBooleanExtra(AlarmItem.KEY_RPT_WED, false));
				mAlarmItem.set(AlarmItem.KEY_RPT_THURS, data.getBooleanExtra(AlarmItem.KEY_RPT_THURS, false));
				mAlarmItem.set(AlarmItem.KEY_RPT_FRI, data.getBooleanExtra(AlarmItem.KEY_RPT_FRI, false));
				mAlarmItem.set(AlarmItem.KEY_RPT_SAT, data.getBooleanExtra(AlarmItem.KEY_RPT_SAT, false));
				mAlarmItem.set(AlarmItem.KEY_RPT_SUN, data.getBooleanExtra(AlarmItem.KEY_RPT_SUN, false));
				vUpdateRepeat();
				if (mAlarmItem.getBool(AlarmItem.KEY_ENABLED)) {
					doAlarmSet();
				} else {
					mChkEnabled.setChecked(true);
				}
				break;
			case ACTION_CHOOSE_RINGTONE:
				if (data.hasExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)) {
					Bundle b = data.getExtras();
					mAlarmItem.set(AlarmItem.KEY_BACKUP, ((Uri)b.get(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)).toString());
					vUpdateBackup(true);
				}
				break;
			case ACTION_CHOOSE_MEDIA_VOLUME:
				mAlarmItem.set(AlarmItem.KEY_MEDIA_VOLUME, data.getIntExtra(AlarmItem.KEY_MEDIA_VOLUME, 50));
				vUpdateMediaVolume(false);
				break;
			case ACTION_CHOOSE_TIMEOUT_BATT:
				mAlarmItem.set(AlarmItem.KEY_WL_TIMEOUT_BATT, data.getIntExtra(TimeChooser.EXTRA_VALUE, 60));
				vUpdateBattTimeout();
				break;
			case ACTION_CHOOSE_TIMEOUT_PLUG:
				mAlarmItem.set(AlarmItem.KEY_WL_TIMEOUT_PLUG, data.getIntExtra(TimeChooser.EXTRA_VALUE, 60));
				vUpdatePlugTimeout();
				break;
			case ACTION_INPUT_NET_TEST_URL:
				mAlarmItem.set(AlarmItem.KEY_NET_TEST_URL, data.getStringExtra(StringInputDialog.EXTRA_VALUE));
				vUpdateNetTest();
				break;
			case ACTION_CHOOSE_WIFI_WAIT_TIME:
				mAlarmItem.set(AlarmItem.KEY_WIFI_WAIT_TIME, data.getIntExtra(TimeChooser.EXTRA_VALUE, 300));
				vUpdateWifi();
				break;
			case ACTION_CHOOSE_MUTE_SNOOZE_TIME:
				mAlarmItem.set(AlarmItem.KEY_MUTE_SNOOZE_TIME, data.getIntExtra(TimeChooser.EXTRA_VALUE, 300));
				vUpdateMuteSnooze();
				break;
			case ACTION_CREATE_HOME_SCREEN_SHORTCUT:
				startActivityForResult(data, FETCH_HOME_SCREEN_SHORTCUT);
				break;
			case FETCH_HOME_SCREEN_SHORTCUT:
				processShortcut(data);
				break;
			}
			
		
		} else {
			switch (requestCode) {
			case ACTION_CHOOSE_APP_CUSTOM:
				mChkStopApp.setChecked(false);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void processShortcut(Intent data) {
		Intent i = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		mAlarmItem.set(AlarmItem.KEY_CUSTOM_DATA, AalService.getIntentUri(i));
		if (data.hasExtra(ProviderList.EXTRA_PACKAGE_NAME)) {
			mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, data.getStringExtra(ProviderList.EXTRA_PACKAGE_NAME));
		} else {
			try {
				mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, i.getComponent().getPackageName());
			} catch (Exception e) {
				mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, "");
				e.printStackTrace();
			}			

		}

		if (!mAlarmItem.hasPackageName()) {
			mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, "custom");
		}
		mAlarmItem.set(AlarmItem.KEY_CUSTOM_ACTION, data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME));
		mAlarmItem.set(AlarmItem.KEY_CUSTOM_TYPE, "");
		vUpdateApp();
	}
	
	private void checkCustomAppPackage() {
		if ((mAlarmItem.getBool(AlarmItem.KEY_STOP_APP_ON_TIMEOUT) || mAlarmItem.getBool(AlarmItem.KEY_FORCE_RESTART)) && mAlarmItem.isCustomIntent()) {
			showDialog(DIALOG_WARN_STOP_APP);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_PICK_TIME:
			return new TimePickerDialog(this, mOnTimeSetListener, mAlarmItem.getInt(AlarmItem.KEY_HOUR), mAlarmItem.getInt(AlarmItem.KEY_MINUTE), false);
		case DIALOG_PICK_BACKUP_OPTION:
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.ea_ti_backup_option);
			adb.setItems(AlarmItem.BACKUP_OPTIONS, mOnBackupDialogOnClick);
			return adb.create();
		case DIALOG_PICK_APP_TYPE:
			AlertDialog.Builder adb2 = new AlertDialog.Builder(this);
			adb2.setTitle(R.string.ea_ti_app);
			adb2.setItems(APP_TYPE_OPTIONS, mAppTypeDialogOnClick);
			return adb2.create();
		case DIALOG_PICK_WIFI_FAILED_ACTION:
			AlertDialog.Builder adb3 = new AlertDialog.Builder(this);
			adb3.setTitle(R.string.ea_ti_wifi_failed);
			adb3.setItems(AlarmItem.WIFI_FAILED_OPTIONS, mWifiFailedDialogOnClick);
			return adb3.create();
		case DIALOG_BITLY:
			ProgressDialog pd = new ProgressDialog(this);
			pd.setIndeterminate(true);
			pd.setMessage("Shortenting Url with Bit.ly...");
			pd.setCancelable(false);
			return pd;
		case DIALOG_WARN_STOP_APP:
			AlertDialog.Builder adb4 = new AlertDialog.Builder(this);
			adb4.setTitle(R.string.ae_stop_app_warning_title);
			adb4.setMessage(R.string.ae_stop_app_warning_message);
			adb4.setCancelable(false);
			adb4.setPositiveButton("Select App", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(getBaseContext(), AppChooser.class);
					startActivityForResult(i, ACTION_CHOOSE_APP_CUSTOM);
				}
				
			});
			adb4.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mChkStopApp.setChecked(false);
					mChkForceRestart.setChecked(false);
				}
				
			});
			return adb4.create();
			
		}
		
		return super.onCreateDialog(id);
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, MENU_SHARE_INTENT, 0, "Share Intent");
		mi.setIcon(android.R.drawable.ic_menu_share);
		mi = menu.add(0, MENU_OTHER_APPS, 1, R.string.al_menu_other_apps);
		mi.setIcon(R.drawable.ic_menu_e6_logo);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case MENU_OTHER_APPS:
			i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(getString(R.string.e6_market_uri))); 
			startActivity(i);
			break;
		case MENU_SHARE_INTENT:
			showDialog(DIALOG_BITLY);
			Thread t = new Thread(mMakeUrlTask);
			t.start();
			break;
		}

		return super.onOptionsItemSelected(item);
	}



	private TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mAlarmItem.set(AlarmItem.KEY_HOUR, hourOfDay);
			mAlarmItem.set(AlarmItem.KEY_MINUTE, minute);
			vUpdateTime();
			if (mAlarmItem.getBool(AlarmItem.KEY_ENABLED)) {
				doAlarmSet();
			} else {
				mChkEnabled.setChecked(true);
			}
		}
		
	};

	private View.OnClickListener mLlEnabledOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mChkEnabled.setChecked(!mChkEnabled.isChecked());
		}
	};
	private View.OnClickListener mLlTimeOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(DIALOG_PICK_TIME);
		}
	};
	private View.OnClickListener mLlAppSelectOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(DIALOG_PICK_APP_TYPE);
		}
	};
	private View.OnClickListener mLlDontLaunchOnCallOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mChkDontLaunchOnCall.setChecked(!mChkDontLaunchOnCall.isChecked());
		}
	};
	private View.OnClickListener mLlNetTestOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mChkNetTest.setChecked(!mChkNetTest.isChecked());
		}
	};
	private View.OnClickListener mLlBackupOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			i.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
			i.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
			if (!Uri.parse(mAlarmItem.getString(AlarmItem.KEY_BACKUP)).equals("")) {
				i.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(mAlarmItem.getString(AlarmItem.KEY_BACKUP)));
			}
			startActivityForResult(i, ACTION_CHOOSE_RINGTONE);
		}
	};
	private View.OnClickListener mLlBackupOptionOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(DIALOG_PICK_BACKUP_OPTION);
		}
	};
	private View.OnClickListener mLlWifiOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mChkWifi.setChecked(!mChkWifi.isChecked());
		}
	};
	
	private View.OnClickListener mLlSetMediaVolumeOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mChkSetMediaVolume.setChecked(!mChkSetMediaVolume.isChecked());
		}
	}; 
	private View.OnClickListener mLlMediaVolumeOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), VolumeChooser.class);
			i.putExtra(AlarmItem.KEY_MEDIA_VOLUME, mAlarmItem.getInt(AlarmItem.KEY_MEDIA_VOLUME));
			startActivityForResult(i, ACTION_CHOOSE_MEDIA_VOLUME);
		}
	};
	private View.OnClickListener mLlMuteSnoozeTimeOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), TimeChooser.class);
			i.putExtra(TimeChooser.EXTRA_TITLE, R.string.ea_ti_mute_snooze_time);
			i.putExtra(TimeChooser.EXTRA_LONG_MESSAGE, R.string.tch_lm_mute_snooze);
			i.putExtra(TimeChooser.EXTRA_VALUE, mAlarmItem.getInt(AlarmItem.KEY_MUTE_SNOOZE_TIME));
			startActivityForResult(i, ACTION_CHOOSE_MUTE_SNOOZE_TIME);
		}
	};
	private View.OnClickListener mLlWifiWaitTimeOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), TimeChooser.class);
			i.putExtra(TimeChooser.EXTRA_TITLE, R.string.tch_ti_wifi_wait);
			i.putExtra(TimeChooser.EXTRA_LONG_MESSAGE, R.string.tch_lm_wifi_wait);
			i.putExtra(TimeChooser.EXTRA_VALUE, mAlarmItem.getInt(AlarmItem.KEY_WIFI_WAIT_TIME));
			startActivityForResult(i, ACTION_CHOOSE_WIFI_WAIT_TIME);
		}
	};
	private View.OnClickListener mLlWifiFailedActionOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDialog(DIALOG_PICK_WIFI_FAILED_ACTION);
		}
	};
	private View.OnClickListener mLlTurnOffWifiOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mChkTurnOffWifi.setChecked(!mAlarmItem.getBool(AlarmItem.KEY_TURN_OFF_WIFI));
		}
	};
	private View.OnClickListener mLlStopAppOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mChkStopApp.setChecked(!mAlarmItem.getBool(AlarmItem.KEY_STOP_APP_ON_TIMEOUT));
		}
	};	
	private View.OnClickListener mLlForceRestartOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mChkForceRestart.setChecked(!mAlarmItem.getBool(AlarmItem.KEY_FORCE_RESTART));
		}
	};
	private View.OnClickListener mLlMuteSnoozeOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mChkMuteSnooze.setChecked(!mAlarmItem.getBool(AlarmItem.KEY_MUTE_SNOOZE));
		}
	};
	private View.OnClickListener mLlLabelOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(AlarmEdit.this, StringInputDialog.class);
			i.putExtra(StringInputDialog.EXTRA_HIDE_HTTP_TEST, true);
			i.putExtra(StringInputDialog.EXTRA_TITLE, R.string.ea_ti_label);
			i.putExtra(StringInputDialog.EXTRA_SHORT_MESSAGE, R.string.ea_ti_label);
			i.putExtra(StringInputDialog.EXTRA_LONG_MESSAGE, R.string.ea_label_msg);
			i.putExtra(StringInputDialog.EXTRA_VALUE, mAlarmItem.getString(AlarmItem.KEY_LABEL));
			startActivityForResult(i, ACTION_INPUT_LABEL);
		}
		
	};
	
	private CheckBox.OnCheckedChangeListener mChkEnabledOnChange = new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mAlarmItem.set(AlarmItem.KEY_ENABLED, isChecked);
			doAlarmSet();
		}
	};

	private CheckBox.OnCheckedChangeListener mChkNetTestOnChange = new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mAlarmItem.set(AlarmItem.KEY_NET_TEST, isChecked);
			vUpdateNetTest();
//			vUpdateBackup(false);
		}
	};

	private CheckBox.OnCheckedChangeListener mChkMuteSnooeOnChange = new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mAlarmItem.set(AlarmItem.KEY_MUTE_SNOOZE, isChecked);
			if (isChecked) {
				Toast.makeText(getBaseContext(), R.string.snooze_warning, Toast.LENGTH_LONG).show();
			}
		}
	};
	private CheckBox.OnCheckedChangeListener mChkTurnOffWifiOnChange = new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mAlarmItem.set(AlarmItem.KEY_TURN_OFF_WIFI, isChecked);
		}
	};
	private CheckBox.OnCheckedChangeListener mChkStopAppOnChange = new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mAlarmItem.set(AlarmItem.KEY_STOP_APP_ON_TIMEOUT, isChecked);
			checkCustomAppPackage();
		}
	};
	private CheckBox.OnCheckedChangeListener mChkForceRestartOnChange = new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mAlarmItem.set(AlarmItem.KEY_FORCE_RESTART, isChecked);
			checkCustomAppPackage();
		}
	};
	private DialogInterface.OnClickListener mOnBackupDialogOnClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			mAlarmItem.set(AlarmItem.KEY_BACKUP_OPTION, which);
			vUpdateBackup(false);
			if (which == 2) {
				mChkNetTest.setChecked(true);
				Toast.makeText(getBaseContext(), "Make sure to enable the \"Test Network\" option, if you want the backup alarm to work.", Toast.LENGTH_LONG).show();
			}
//			Toast.makeText(getBaseContext(), AalDbAdapter.BACKUP_OPTIONS[which], Toast.LENGTH_SHORT).show();
		}
	};
	private DialogInterface.OnClickListener mAppTypeDialogOnClick = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent i;
			switch (which) {
			case 0:
				//Select App
				i = new Intent(getBaseContext(), AppChooser.class);
				startActivityForResult(i, ACTION_CHOOSE_APP);
				break;
			case 1:
				//Create Shortcut
				i = new Intent(Intent.ACTION_PICK_ACTIVITY);
				i.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
				i.putExtra(Intent.EXTRA_TITLE, "Create a Shortcut");
				startActivityForResult(i, ACTION_CREATE_HOME_SCREEN_SHORTCUT);
				break;
			case 2:
				//Home Screen Shortcut
				i = new Intent(getBaseContext(), ProviderList.class);
				i.putExtra(ProviderList.EXTRA_PROVIDER, ProviderList.PROVIDER_HOMESCREEN);
				startActivityForResult(i, ACTION_CHOOSE_FROM_PROVIDER);
				break;
			case 3:
				//Pandora Station
				i = new Intent(getBaseContext(), ProviderList.class);
				i.putExtra(ProviderList.EXTRA_PROVIDER, ProviderList.PROVIDER_PANDORA);
				startActivityForResult(i, ACTION_CHOOSE_FROM_PROVIDER);
				break;
//			case 4:
//				//Google Listen Feed
//				//TODO: Need to check out what queue looks like in db
//				i = new Intent(getBaseContext(), CustomActionActivity.class);
//				i.putExtra(CustomActionActivity.EXTRA_ACTION_TYPE, CustomActionActivity.ACTION_TYPE_LATEST_UNHEARD_LISTEN_PODCAST);
//				mAlarmItem.packageName = CustomActionActivity.GOOGLE_LISTEN_PACKAGE_NAME);
//				mAlarmItem.customAction = "Latest Podcast on Google Listen";
//				mAlarmItem.customData = AalService.getIntentUri(i);
//				mAlarmItem.customType = "";
//				vUpdateApp();
//				break;
			case 4:
				//Tunewiki Station
				i = new Intent(getBaseContext(), ProviderList.class);
				i.putExtra(ProviderList.EXTRA_PROVIDER, ProviderList.PROVIDER_TUNEWIKI);
				startActivityForResult(i, ACTION_CHOOSE_FROM_PROVIDER);
				break;
			case 5:
				//Custom Intent
				i = new Intent(getBaseContext(), CustomIntentMaker.class);
				i.putExtra(AlarmItem.KEY_CUSTOM_ACTION, mAlarmItem.getString(AlarmItem.KEY_CUSTOM_ACTION));
				i.putExtra(AlarmItem.KEY_CUSTOM_DATA, mAlarmItem.getString(AlarmItem.KEY_CUSTOM_DATA));
				i.putExtra(AlarmItem.KEY_CUSTOM_TYPE, mAlarmItem.getString(AlarmItem.KEY_CUSTOM_TYPE));
//				i.putExtra(AlarmItem.KEY_PACKAGE_NAME, mAlarmItem.packageName);
				startActivityForResult(i, ACTION_CUSTOM_INTENT);
				break;

			case 6:
				//Clear App
				mAlarmItem.set(AlarmItem.KEY_PACKAGE_NAME, "");
				mAlarmItem.set(AlarmItem.KEY_CUSTOM_ACTION, "");
				mAlarmItem.set(AlarmItem.KEY_CUSTOM_DATA, "");
				mAlarmItem.set(AlarmItem.KEY_CUSTOM_TYPE, "");
				vUpdateApp();
				break;
			}
		}
		
	};
	private DialogInterface.OnClickListener mWifiFailedDialogOnClick = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			mAlarmItem.set(AlarmItem.KEY_WIFI_FAILED_ACTION, which);
			vUpdateWifi();
		}
		
	};
	
	private void makeIntentUrl() {
		String rtr = "";
		rtr += "/" + escapeIntentItem(mAlarmItem.getString(AlarmItem.KEY_PACKAGE_NAME));
		rtr += "/" + escapeIntentItem(mAlarmItem.getString(AlarmItem.KEY_CUSTOM_ACTION));
		rtr += "/" + escapeIntentItem(mAlarmItem.getString(AlarmItem.KEY_CUSTOM_TYPE));
		rtr += "/" + escapeIntentItem(mAlarmItem.getString(AlarmItem.KEY_CUSTOM_DATA));
		try {
			mUrl = "http://episode6.com/ibuilder/m" + java.net.URLEncoder.encode(rtr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			mUrl = "http://episode6.com/ibuilder/m" + rtr;
			e.printStackTrace();
		}
	}
	
	private String escapeIntentItem(String item) {
		if (item == null) {
			return "null";
		} else if (item.trim().equals("")) {
			return "null";
		} else {
			return item.trim().replaceAll("/", "@@").replaceAll("%2F", "@@");
		}
	}
	
    private void makeBitlyUrl(String url) throws Exception{
    	String bUrl = "";
    	String rtrUrl = "";

		bUrl = "http://api.bit.ly/shorten?version=2.0.1&longUrl=" + java.net.URLEncoder.encode(url, "UTF-8");
    	bUrl += ApiKeys.BITLY_API_LOGIN_AND_KEY;
    	String jsonStr = "";
    	

   		jsonStr = HTTPHelper.DownloadText(bUrl);

    	if (jsonStr.length() != 0) {
			JSONObject jo = new JSONObject(jsonStr);
			JSONObject results = jo.getJSONObject("results");
			results = results.getJSONObject(url);
			rtrUrl = results.getString("shortUrl");
    	}
    	mUrl = rtrUrl;
    }
    
    private void copyToClipboard(String url) {
    	ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
    	cm.setText(url);
    }
    
    private void sendMessage(String url) {
    	Intent i = new Intent(Intent.ACTION_SEND);
    	i.setType("text/plain");
    	i.putExtra(Intent.EXTRA_TEXT, url);
    	startActivity(Intent.createChooser(i, "Share Link With..."));
    }
    
    private View.OnCreateContextMenuListener mLlAppOnCreateContext = new View.OnCreateContextMenuListener() {

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			menu.add(0, CTX_EDIT_INTENT, 0, "Edit Custom Intent");
			menu.add(0, CTX_SHARE_INTENT, 1, "Share This Intent");
		}
    	
    };


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case CTX_EDIT_INTENT:
			Intent i = new Intent(getBaseContext(), CustomIntentMaker.class);
			i.putExtra(AlarmItem.KEY_CUSTOM_ACTION, mAlarmItem.getString(AlarmItem.KEY_CUSTOM_ACTION));
			i.putExtra(AlarmItem.KEY_CUSTOM_DATA, mAlarmItem.getString(AlarmItem.KEY_CUSTOM_DATA));
			i.putExtra(AlarmItem.KEY_CUSTOM_TYPE, mAlarmItem.getString(AlarmItem.KEY_CUSTOM_TYPE));
//			i.putExtra(AlarmItem.KEY_PACKAGE_NAME, mAlarmItem.packageName);
			startActivityForResult(i, ACTION_CUSTOM_INTENT);
			break;
		case CTX_SHARE_INTENT:
			showDialog(DIALOG_BITLY);
			Thread t = new Thread(mMakeUrlTask);
			t.start();
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	
	private final Handler mHandler = new Handler();
	
	private Runnable mMakeUrlTask = new Runnable() {

		@Override
		public void run() {
			makeIntentUrl();
			try {
				makeBitlyUrl(mUrl);
			} catch (Exception e) {
				
			}
			copyToClipboard(mUrl);
			mHandler.post(mFinishUrlTask);
		}
		
	};
	private Runnable mFinishUrlTask = new Runnable() {

		@Override
		public void run() {
			dismissDialog(DIALOG_BITLY);
			if (!mUrl.startsWith("http://bit.ly")) {
				Toast.makeText(getBaseContext(), "Error shortening link with Bit.ly, using long link instead.", Toast.LENGTH_LONG).show();
			}
			Toast.makeText(getBaseContext(), mUrl + " copied to clipboard.", Toast.LENGTH_LONG).show();
			sendMessage(mUrl);
		}
		
	};
    

	
	
	//************************LITE EDITS******************************************
	
	//LinearLayouts
//	LiteHelper.showProFeatureDialog(AlarmEdit.this);
	private View.OnClickListener mLlRepeatOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), RepeatChooser.class);
			i.putExtra(AlarmItem.KEY_RPT_MON, mAlarmItem.getBool(AlarmItem.KEY_RPT_MON));
			i.putExtra(AlarmItem.KEY_RPT_TUES, mAlarmItem.getBool(AlarmItem.KEY_RPT_TUES));
			i.putExtra(AlarmItem.KEY_RPT_WED, mAlarmItem.getBool(AlarmItem.KEY_RPT_WED));
			i.putExtra(AlarmItem.KEY_RPT_THURS, mAlarmItem.getBool(AlarmItem.KEY_RPT_THURS));
			i.putExtra(AlarmItem.KEY_RPT_FRI, mAlarmItem.getBool(AlarmItem.KEY_RPT_FRI));
			i.putExtra(AlarmItem.KEY_RPT_SAT, mAlarmItem.getBool(AlarmItem.KEY_RPT_SAT));
			i.putExtra(AlarmItem.KEY_RPT_SUN, mAlarmItem.getBool(AlarmItem.KEY_RPT_SUN));
			startActivityForResult(i, ACTION_CHOOSE_REPEAT);
		}
	};
	private View.OnClickListener mLlNetTestUrlOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), StringInputDialog.class);
			i.putExtra(StringInputDialog.EXTRA_TITLE, R.string.sid_ti_net_test_url);
			i.putExtra(StringInputDialog.EXTRA_LONG_MESSAGE, R.string.sid_lm_net_test_url);
			i.putExtra(StringInputDialog.EXTRA_SHORT_MESSAGE, R.string.sid_sm_net_test_url);
			i.putExtra(StringInputDialog.EXTRA_VALUE, mAlarmItem.getString(AlarmItem.KEY_NET_TEST_URL));
			startActivityForResult(i, ACTION_INPUT_NET_TEST_URL);
		}
	};
	private View.OnClickListener mLlWlBattTimeoutOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), TimeChooser.class);
			i.putExtra(TimeChooser.EXTRA_TITLE, R.string.tch_ti_wl_batt);
			i.putExtra(TimeChooser.EXTRA_LONG_MESSAGE, R.string.tch_lm_wl_batt);
			i.putExtra(TimeChooser.EXTRA_VALUE, mAlarmItem.getInt(AlarmItem.KEY_WL_TIMEOUT_BATT));
			startActivityForResult(i, ACTION_CHOOSE_TIMEOUT_BATT);
		}
	};
	private View.OnClickListener mLlWlPlugTimeoutOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), TimeChooser.class);
			i.putExtra(TimeChooser.EXTRA_TITLE, R.string.tch_ti_wl_plug);
			i.putExtra(TimeChooser.EXTRA_LONG_MESSAGE, R.string.tch_lm_wl_plug);
			i.putExtra(TimeChooser.EXTRA_VALUE, mAlarmItem.getInt(AlarmItem.KEY_WL_TIMEOUT_PLUG));
			startActivityForResult(i, ACTION_CHOOSE_TIMEOUT_PLUG);
		}
	};
	
	//Checkboxes
//	LiteHelper.showProFeatureDialog(AlarmEdit.this);
//	buttonView.setChecked(!isChecked);
	private CheckBox.OnCheckedChangeListener mChkWifiOnChange = new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mAlarmItem.set(AlarmItem.KEY_WIFI, isChecked);
			vUpdateWifi();
		}
	};
	private CheckBox.OnCheckedChangeListener mChkSetMediaVolumeOnChange = new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mAlarmItem.set(AlarmItem.KEY_SET_MEDIA_VOLUME, isChecked);
			vUpdateMediaVolume(true);
		}
	};
	private CheckBox.OnCheckedChangeListener mChkDontLaunchOnCallOnChange = new CheckBox.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			mAlarmItem.set(AlarmItem.KEY_DONT_LAUNCH_ON_CALL, isChecked);
		}
	};
}
