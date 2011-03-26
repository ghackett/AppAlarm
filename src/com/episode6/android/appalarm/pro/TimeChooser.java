package com.episode6.android.appalarm.pro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TimeChooser extends Activity {
	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_SHORT_MESSAGE = "extra_short_message";
	public static final String EXTRA_LONG_MESSAGE = "extra_long_message";
	public static final String EXTRA_VALUE = "extra_init_value";
	
	private static final int SECONDS = 1;
	private static final int MINUTES = 2;
	private static final int HOURS = 3;

	private EditText mEtNumber;
	private Button mBtnLength, mBtnOk, mBtnCancel;
	private TextView mTvShortMsg, mTvLongMsg;
	
	private int mCurrentLength;


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		mCurrentLength = item.getItemId();
		mBtnLength.setText(item.getTitle());
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_chooser);
		
		Bundle extras = getIntent().getExtras();
		setTitle(extras.getInt(EXTRA_TITLE));
		
		
		initViews();
		
		if (extras.containsKey(EXTRA_SHORT_MESSAGE)) {
			mTvShortMsg.setText(extras.getInt(EXTRA_SHORT_MESSAGE));
		}
		if (extras.containsKey(EXTRA_LONG_MESSAGE)) {
			mTvLongMsg.setText(extras.getInt(EXTRA_LONG_MESSAGE));
			mTvLongMsg.setVisibility(View.VISIBLE);
		}
		
		assignListeners();
		
		int initialValue = extras.getInt(EXTRA_VALUE, 60);
		if (initialValue%3600 == 0) {
			initialValue /= 3600;
			mEtNumber.setText(initialValue + "");
			mBtnLength.setText("Hour(s)");
			mCurrentLength = HOURS;
		} else if (initialValue%60 == 0) {
			initialValue /= 60;
			mEtNumber.setText(initialValue + "");
			mBtnLength.setText("Minute(s)");
			mCurrentLength = MINUTES;
		} else {
			mEtNumber.setText(initialValue + "");
			mBtnLength.setText("Second(s)");
			mCurrentLength = SECONDS;
		}
	}
	
	private void initViews() {
		mEtNumber = (EditText)findViewById(R.id.wdc_et_number);
		mBtnLength = (Button)findViewById(R.id.wdc_btn_length);
		mBtnOk = (Button)findViewById(R.id.wdc_btn_ok);
		mBtnCancel = (Button)findViewById(R.id.wdc_btn_cancel);
		mTvShortMsg = (TextView)findViewById(R.id.wdc_tv_short_message);
		mTvLongMsg = (TextView)findViewById(R.id.wdc_tv_long_message);
	}
	
	private void assignListeners() {
		mBtnLength.setOnClickListener(mBtnLengthOnClick);
		mBtnLength.setOnCreateContextMenuListener(mBtnLengthOnCtxListener);
		mBtnOk.setOnClickListener(mBtnOkOnClick);
		mBtnCancel.setOnClickListener(mBtnCancelOnClick);
	}

	private View.OnClickListener mBtnLengthOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			v.showContextMenu();
			
		}
		
	};
	
	private View.OnCreateContextMenuListener mBtnLengthOnCtxListener  = new View.OnCreateContextMenuListener() {

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			menu.add(0, SECONDS, 0, "Second(s)");
			menu.add(0, MINUTES, 1, "Minute(s)");
			menu.add(0, HOURS, 2, "Hour(s)");
		}
		
	};
	
	private View.OnClickListener mBtnOkOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				int nt = Integer.parseInt(mEtNumber.getText().toString());
				switch (mCurrentLength) {
				case HOURS:
					nt = nt*60;
				case MINUTES:
					nt = nt*60;
				}
				Intent i = new Intent();
				i.putExtra(EXTRA_VALUE, nt);
				setResult(RESULT_OK, i);
				finish();
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "Please enter valid number.", Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	private View.OnClickListener mBtnCancelOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			setResult(RESULT_CANCELED);
			finish();
		}
		
	};
}
