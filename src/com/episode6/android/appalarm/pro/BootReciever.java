package com.episode6.android.appalarm.pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, AalService.class);
		i.setAction(AalService.ACTION_SET_SILENT_ALARM);
		context.startService(i);

	}

}
