package com.episode6.android.appalarm.pro;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class KillAndLaunchActivity extends Activity {
	public static final String EXTRA_PACKAGE_TO_RESTART = "package_to_restart";
	public static final String EXTRA_INTENT_TO_LAUNCH = "intent_to_launch";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String packRestart = getIntent().getStringExtra(EXTRA_PACKAGE_TO_RESTART);
        try {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(packRestart);
        } catch (Exception e) {
        	Toast.makeText(this, "There was a problem killing package: " + packRestart, Toast.LENGTH_LONG).show();
        }
        
        
        Intent i = getIntent().getParcelableExtra(EXTRA_INTENT_TO_LAUNCH);
        try {
        	startActivity(i);
        	finish();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        try {
			Method m = ActivityManager.class.getMethod("killBackgroundProcesses", new Class[] {String.class});
			setContentView(R.layout.froyo_kill_screen);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.i("AppAlarm", "old version finishing activity");
			finish();
		}
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setContentView(R.layout.froyo_kill_screen);
		super.onConfigurationChanged(newConfig);
	}
    
    
    
}
