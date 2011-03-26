package com.episode6.android.appalarm.pro;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class AlarmList extends ListActivity {
	private static final String PREF_LAST_VERSION = "last_version";
	private AalDbAdapter mDbAdapter;
	private CursorAdapter mCurAdapter;
	private PackageManager mPackageManager;
	
	private static final int MENU_OTHER_APPS = Menu.FIRST+1;
	private static final int MENU_SUPPORT = Menu.FIRST+2;
	private static final int MENU_BACKUP = Menu.FIRST+3;
	private static final int MENU_RESTORE = Menu.FIRST+4;
	private static final int MENU_WELCOME = Menu.FIRST+9;
//	private static final int MENU_IBUILDER = Menu.FIRST+5;
	
	private static final int CTX_EDIT_ALARM = Menu.FIRST+5;
	private static final int CTX_DELETE_ALARM = Menu.FIRST+6;
	private static final int CTX_COPY_ALARM = Menu.FIRST+7;
	private static final int CTX_TEST_ALARM = Menu.FIRST+8;
	
	private static final int DIALOG_BACKUP = 1;
	private static final int DIALOG_RESTORE = 2;
	private static final int DIALOG_OVERWRITE = 3;
	private static final int DIALOG_CLEAR_ALARMS = 4;
	private static final int DIALOG_SD_ERROR = 5;
	private static final int DIALOG_WELCOME = 6;
	
	private boolean addedCustom = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mDbAdapter = new AalDbAdapter(this);
        mDbAdapter.open();
        
        mPackageManager = getPackageManager();
        
        String dataString = getIntent().getDataString();
        if (dataString != null && !dataString.equals("") && !addedCustom) {
        	addAlarmFromIBuilder(dataString);
        	getIntent().setData(null);
//        	finish();
        }
        
        Cursor c = mDbAdapter.fetchAllAlarms();
        startManagingCursor(c);
        
        mCurAdapter = new AlarmListAdapter(this, c);
        
        assignListeners();
        
        
        
	    Intent i = new Intent(this, AalService.class);
	    i.setAction(AalService.ACTION_SET_ALARM);
	    startService(i);
	    
	    if (isApplicationNewUpgraded()) {
//	    	LiteHelper.showFirstRunDialog(this);
	    	showDialog(DIALOG_WELCOME);
	    }	    	
	    	
//	    if (AalService.isBatteryDischarging(this)) {
//	    	Toast.makeText(this, "Phone is Unplugged", Toast.LENGTH_SHORT).show();
//	    } else {
//	    	Toast.makeText(this, "Phone is Charging", Toast.LENGTH_SHORT).show();
//	    }
    }
    
	public boolean isApplicationNewUpgraded(){
		try {
			SharedPreferences sp = getSharedPreferences(AalService.PREF_FILE_NAME, Context.MODE_PRIVATE);
			int lastVersion = sp.getInt(PREF_LAST_VERSION, 0);
			int currentVersion = getVersionId();
			if (lastVersion != currentVersion) {
				SharedPreferences.Editor spe = sp.edit();
				spe.putInt(PREF_LAST_VERSION, currentVersion);
//				spe.putBoolean(LiteHelper.PREF_NO_NAG, false);
				spe.commit();
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	private void assignListeners() {
		ListView lv = getListView();
		lv.setAdapter(mCurAdapter);
		lv.setOnCreateContextMenuListener(onCreateItemContext);
		
		((Button)findViewById(R.id.m_btn_add_new)).setOnClickListener(onAddNewAlarmClick);
		((Button)findViewById(R.id.m_btn_toggle_notif)).setOnClickListener(onToggleNotifClick);
		
//		((Button)findViewById(R.id.m_btn_pro)).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				LiteHelper.getProVersion(getBaseContext());
//			}
//		});
		
	}
    
    private String unescapeIBuilder(String val) {
    	if (val.equals("null")) {
    		return "";
    	}
    	return val.replaceAll("@@", "/");
    }
    private void addAlarmFromIBuilder(String data) {
    	try {
			data = URLDecoder.decode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	data = data.substring(31);
    	String[] splitStr = data.split("/");
    	if (splitStr.length==4) {
    		AlarmItem ai = new AlarmItem();
    		ai.set(AlarmItem.KEY_PACKAGE_NAME, unescapeIBuilder(splitStr[0]));
    		if (ai.getString(AlarmItem.KEY_PACKAGE_NAME).equals("")) {
    			ai.set(AlarmItem.KEY_PACKAGE_NAME, "custom");
    		}
    		ai.set(AlarmItem.KEY_CUSTOM_ACTION, unescapeIBuilder(splitStr[1]));
    		ai.set(AlarmItem.KEY_CUSTOM_TYPE, unescapeIBuilder(splitStr[2]));
    		ai.set(AlarmItem.KEY_CUSTOM_DATA, unescapeIBuilder(splitStr[3]));
    		mDbAdapter.saveAlarm(ai);
    		Toast.makeText(this, "Added new alarm with custom intent.", Toast.LENGTH_LONG).show();
    	} else {
    		Toast.makeText(this, "Error parsing link", Toast.LENGTH_LONG).show();    		
    	}
    	
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.main);
		
		assignListeners();
	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		launchAlarmEdit(id);
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu bMenu = menu.addSubMenu(R.string.al_menu_backup_restore);
		bMenu.setIcon(android.R.drawable.ic_menu_save);
		bMenu.add(0, MENU_BACKUP, 0, R.string.al_menu_backup);
		bMenu.add(0, MENU_RESTORE, 1, R.string.al_menu_restore);
		
		menu.add(0, MENU_WELCOME, 2, R.string.al_menu_welcome)
			.setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, MENU_OTHER_APPS, 3, R.string.al_menu_other_apps)
			.setIcon(R.drawable.ic_menu_e6_logo);
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

		case MENU_BACKUP:
			File myFile = new File(Environment.getExternalStorageDirectory() + "/AppAlarm.backup.txt");
			if (myFile.exists()) {
				showDialog(DIALOG_OVERWRITE);
			} else {
				doAlarmBackup();
			}
			break;
		case MENU_RESTORE:
			if (getListView().getCount() > 0) {
				showDialog(DIALOG_CLEAR_ALARMS);
			}else {
				doAlarmRestore();
			}
			break;
		case MENU_WELCOME:
			showDialog(DIALOG_WELCOME);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_BACKUP:
			pd.setMessage("Backing up your alarms to your SD card...");
			return pd;
		case DIALOG_RESTORE:
			pd.setMessage("Restoring your alarms from your SD card...");
			return pd;
		case DIALOG_OVERWRITE:
			adb.setTitle("Overwrite?");
			adb.setMessage("Do you want to overwrite your existing backup file?");
			adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					doAlarmBackup();
				}
			});
			adb.setNegativeButton("Nevermind", null);
			return adb.create();
		case DIALOG_CLEAR_ALARMS:
			adb.setTitle("Clear Alarms?");
			adb.setMessage("Do you want to clear your existing alarms before restoring from your backup file?");
			adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					mDbAdapter.deleteAllAlarms();
					doAlarmRestore();
				}
			});
			adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					doAlarmRestore();
				}
			});
			return adb.create();
		case DIALOG_SD_ERROR:
			adb.setTitle("Error");
			adb.setMessage("There was an error accessing your SD Card. Please try removing and re-instering it before trying again.");
			adb.setPositiveButton("Okay", null);
			return adb.create();
		case DIALOG_WELCOME: {
			try {
					adb.setTitle("AppAlarm v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
				} catch (NameNotFoundException e) {
					adb.setTitle("Welcome To AppAlarm");
					e.printStackTrace();
				}
				
			TextView tv = new TextView(this);
			tv.setTextColor(Color.WHITE);
			
			int padd = convertToPx(8);
			tv.setPadding(padd, padd, padd, padd);
			tv.setAutoLinkMask(Linkify.WEB_URLS);
			tv.setMovementMethod(LinkMovementMethod.getInstance());
			tv.setText(R.string.al_welcome_msg);
			
			adb.setView(tv);
			adb.setPositiveButton("Close", null);
			return adb.create();
		}
		default:
			return pd;	
		}
	}
	
	private int convertToPx(int dp) {
		float density = getResources().getDisplayMetrics().density;
		return (int) ((float)dp*density + 0.5f);
	}

	@Override
	protected void onDestroy() {
		mDbAdapter.close();
		super.onDestroy();
	}
	
	private void doAlarmBackup() {
		showDialog(DIALOG_BACKUP);
		Thread t = new Thread(mAlarmBackupTask);
		t.start();
	}
	private void doAlarmRestore() {
		showDialog(DIALOG_RESTORE);
		Thread t = new Thread(mAlarmRestoreTask);
		t.start();
	}
	
	private final Runnable mAlarmBackupTask = new Runnable() {

		@Override
		public void run() {
			File myFile = new File(Environment.getExternalStorageDirectory() + "/AppAlarm.backup.txt");
			if (myFile.exists()) {
				myFile.delete();
			}
			
			AlarmItem ai = null;
			
			Cursor c = mDbAdapter.fetchAllAlarmsForBackup();
			
			try {
				myFile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(myFile));

				c.moveToFirst();
				
				while (!c.isAfterLast()) {
					ai = new AlarmItem(c);
					ai.writeToFileWriter(bw);
					c.moveToNext();
				}
				
				c.close();
				bw.flush();
				bw.close();
				dismissDialog(DIALOG_BACKUP);
			} catch (Exception e) {
				e.printStackTrace();
				dismissDialog(DIALOG_BACKUP);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						showDialog(DIALOG_SD_ERROR);
					}
					
				});
				
			}
		}
		
	};
	
	private final Runnable mAlarmRestoreTask = new Runnable() {

		@Override
		public void run() {
			File myFile = new File(Environment.getExternalStorageDirectory() + "/AppAlarm.backup.txt");
			if (myFile.exists()) {
				int counter = 0;
				try {
					BufferedReader br = new BufferedReader(new FileReader(myFile));
					while(true) {
						AlarmItem ai = new AlarmItem(br, true);
						mDbAdapter.saveAlarm(ai);
						counter++;
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("AppAlarm", "Added " + counter + " alarms to database");
				}
			}
			mHandler.post(mRefreshListTask);
			dismissDialog(DIALOG_RESTORE);
		}
		
	};
	
	private final Handler mHandler = new Handler();
	private final Runnable mRefreshListTask = new Runnable() {

		@Override
		public void run() {
			mCurAdapter.getCursor().requery();
			doAlarmSet();
		}
		
	};


    private void doAlarmSet() {
    	Intent i = new Intent(this, AalService.class);
    	i.setAction(AalService.ACTION_SET_SILENT_ALARM);
    	startService(i);
    }
    private void launchAlarmEdit(long id) {
    	Intent i = new Intent(this, AlarmEdit.class);
		i.putExtra(AlarmItem.KEY_ROWID, id);
		startActivity(i);
    }
    
	public int getVersionId() {
		int i = 0;
		 try {
			i = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {}
		return i;
	}
	

	
	private final View.OnClickListener onAddNewAlarmClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			startActivity(new Intent(getBaseContext(), AlarmEdit.class));
		}
		
	};
	
	private final View.OnClickListener onToggleNotifClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), AalService.class);
			i.setAction(AalService.ACTION_SET_SHOW_NOTIF);
			boolean newVal = !AalService.loadShowNotifPref(getBaseContext());
			i.putExtra(AalService.EXTRA_SHOW_NOTIF, newVal);
			startService(i);
		}
		
	};
    
	
	private final OnCreateContextMenuListener onCreateItemContext = new OnCreateContextMenuListener() {

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			menu.add(0, CTX_EDIT_ALARM, 0, "Edit Alarm");
			menu.add(0, CTX_DELETE_ALARM, 1, "Delete Alarm");
			menu.add(0, CTX_COPY_ALARM, 2, "Copy Alarm");
			menu.add(0, CTX_TEST_ALARM, 3, "Test Alarm");
		}
		
	};
	
	
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
		long clickedId = acmi.id;
		
		switch(item.getItemId()) {
		case CTX_EDIT_ALARM:
			launchAlarmEdit(clickedId);
			break;
		case CTX_DELETE_ALARM:
			mDbAdapter.deleteAlarm(clickedId);
			mCurAdapter.getCursor().requery();
			doAlarmSet();
			break;
		case CTX_COPY_ALARM:
			AlarmItem ai = mDbAdapter.getAlarmById(clickedId);
			ai.set(AlarmItem.KEY_ENABLED, false);
			ai.set(AlarmItem.KEY_ROWID, 0);
			mDbAdapter.saveAlarm(ai);
			mCurAdapter.getCursor().requery();
			break;
		case CTX_TEST_ALARM:
			AalService.saveNextAlarmPref(this, clickedId);
			Intent i = new Intent(this, AalService.class);
			i.setAction(AalService.ACTION_LAUNCH_ALARM);
			i.putExtra(AalService.EXTRA_DONT_DISABLE, true);
			startService(i);
			break;
		}
		return super.onContextItemSelected(item);
	}




	private class AlarmListAdapter extends CursorAdapter {

		public AlarmListAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			fillView(cursor, view);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.alarm_row, parent, false);
			return v;
		}
		
		private CheckBox.OnCheckedChangeListener checkList = new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0,
					boolean arg1) {
				long alarmId = Long.parseLong(arg0.getTag().toString());
				mDbAdapter.setAlarmEnabled(alarmId, arg1);
				mCurAdapter.getCursor().requery();
				
				doAlarmSet();
			}
			
		};
		
		private void fillView(Cursor cur, View row) {
			AlarmItem ai = new AlarmItem(AlarmItem.ALARM_DEFAULTS_LIST, cur);
			((TextView)row.findViewById(R.id.ar_tv_alarm_time)).setText(ai.getAlarmText());
			((TextView)row.findViewById(R.id.ar_tv_alarm_repeat)).setText(ai.getRepeatText());
			
			((TextView)row.findViewById(R.id.ar_tv_app_name)).setText(ai.getLabel(mPackageManager));
			ai.setAppIconInImageView(((ImageView)row.findViewById(R.id.ar_iv_app_icon)), mPackageManager);
			
			
	    	
			CheckBox chkAlarmEnabled = (CheckBox)row.findViewById(R.id.ar_chk_alarm_enabled);
			//disable change listener so you can setChecked without re-saving.
			chkAlarmEnabled.setOnCheckedChangeListener(null);
			chkAlarmEnabled.setTag(ai.getInt(AlarmItem.KEY_ROWID));
			chkAlarmEnabled.setChecked(ai.getBool(AlarmItem.KEY_ENABLED));			
			chkAlarmEnabled.setOnCheckedChangeListener(checkList);
			
			
		}
	}


}
