package com.episode6.android.appalarm.pro;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class RepeatChooser extends Activity {
	private CheckBox mChkRptMon, mChkRptTues, mChkRptWed, mChkRptThur, mChkRptFri, mChkRptSat, mChkRptSun;

	private Button.OnClickListener mOkOnClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent();
			i.putExtra(AlarmItem.KEY_RPT_MON, mChkRptMon.isChecked());
			i.putExtra(AlarmItem.KEY_RPT_TUES, mChkRptTues.isChecked());
			i.putExtra(AlarmItem.KEY_RPT_WED, mChkRptWed.isChecked());
			i.putExtra(AlarmItem.KEY_RPT_THURS, mChkRptThur.isChecked());
			i.putExtra(AlarmItem.KEY_RPT_FRI, mChkRptFri.isChecked());
			i.putExtra(AlarmItem.KEY_RPT_SAT, mChkRptSat.isChecked());
			i.putExtra(AlarmItem.KEY_RPT_SUN, mChkRptSun.isChecked());
			setResult(Activity.RESULT_OK, i);
			finish();
		}
		
	};
	private Button.OnClickListener mCancelOnClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		
	};

	
	
	private View.OnClickListener mRptMonOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mChkRptMon.setChecked(!mChkRptMon.isChecked());
		}
	};
	private View.OnClickListener mRptTueOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mChkRptTues.setChecked(!mChkRptTues.isChecked());
		}
	};
	private View.OnClickListener mRptWedOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mChkRptWed.setChecked(!mChkRptWed.isChecked());
		}
	};
	private View.OnClickListener mRptThurOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mChkRptThur.setChecked(!mChkRptThur.isChecked());
		}
	};
	private View.OnClickListener mRptFriOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mChkRptFri.setChecked(!mChkRptFri.isChecked());
		}
	};
	private View.OnClickListener mRptSatOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mChkRptSat.setChecked(!mChkRptSat.isChecked());
		}
	};
	private View.OnClickListener mRptSunOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mChkRptSun.setChecked(!mChkRptSun.isChecked());
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repeat_chooser);
		setTitle("Alarm Repeats...");
		
		mChkRptMon = (CheckBox)findViewById(R.id.sr_chk_rpt_mon);
		mChkRptTues = (CheckBox)findViewById(R.id.sr_chk_rpt_tues);
		mChkRptWed = (CheckBox)findViewById(R.id.sr_chk_rpt_wed);
		mChkRptThur = (CheckBox)findViewById(R.id.sr_chk_rpt_thur);
		mChkRptFri = (CheckBox)findViewById(R.id.sr_chk_rpt_fri);
		mChkRptSat = (CheckBox)findViewById(R.id.sr_chk_rpt_sat);
		mChkRptSun = (CheckBox)findViewById(R.id.sr_chk_rpt_sun);
		
		findViewById(R.id.sr_ll_rtp_mon).setOnClickListener(mRptMonOnClick);
		findViewById(R.id.sr_ll_rtp_tues).setOnClickListener(mRptTueOnClick);
		findViewById(R.id.sr_ll_rtp_wed).setOnClickListener(mRptWedOnClick);
		findViewById(R.id.sr_ll_rtp_thur).setOnClickListener(mRptThurOnClick);
		findViewById(R.id.sr_ll_rtp_fri).setOnClickListener(mRptFriOnClick);
		findViewById(R.id.sr_ll_rtp_sat).setOnClickListener(mRptSatOnClick);
		findViewById(R.id.sr_ll_rtp_sun).setOnClickListener(mRptSunOnClick);
		
		findViewById(R.id.sr_btn_ok).setOnClickListener(mOkOnClick);
		findViewById(R.id.sr_btn_cancel).setOnClickListener(mCancelOnClick);
		
		Bundle b = getIntent().getExtras();
		mChkRptMon.setChecked(b.getBoolean(AlarmItem.KEY_RPT_MON, false));
		mChkRptTues.setChecked(b.getBoolean(AlarmItem.KEY_RPT_TUES, false));
		mChkRptWed.setChecked(b.getBoolean(AlarmItem.KEY_RPT_WED, false));
		mChkRptThur.setChecked(b.getBoolean(AlarmItem.KEY_RPT_THURS, false));
		mChkRptFri.setChecked(b.getBoolean(AlarmItem.KEY_RPT_FRI, false));
		mChkRptSat.setChecked(b.getBoolean(AlarmItem.KEY_RPT_SAT, false));
		mChkRptSun.setChecked(b.getBoolean(AlarmItem.KEY_RPT_SUN, false));
	}


}
