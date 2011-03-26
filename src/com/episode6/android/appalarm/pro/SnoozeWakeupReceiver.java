package com.episode6.android.appalarm.pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class SnoozeWakeupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "AppAlarmReceiver");
		wl.acquire(120000);
		Intent i = new Intent(ctx, AalService.class);
		i.setAction(AalService.ACTION_RECOVER_SNOOZE_ALARM);
		ctx.startService(i);
	}

}
