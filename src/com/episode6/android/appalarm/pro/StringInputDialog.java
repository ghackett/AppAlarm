package com.episode6.android.appalarm.pro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StringInputDialog extends Activity {
	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_SHORT_MESSAGE = "extra_short_message";
	public static final String EXTRA_LONG_MESSAGE = "extra_long_message";
	public static final String EXTRA_VALUE = "extra_init_value";
	public static final String EXTRA_HIDE_HTTP_TEST = "extra_hide_http_test";


	private EditText mEtInput;
	private Button mBtnOk, mBtnCancel, mBtnTest;
	private TextView mTvShortMsg, mTvLongMsg;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.string_input);
		
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
		if (extras.getBoolean(EXTRA_HIDE_HTTP_TEST, false)) {
			mBtnTest.setVisibility(View.GONE);
		} else {
			mBtnTest.setVisibility(View.VISIBLE);
		}
		
		assignListeners();
		
		mEtInput.setText(extras.getString(EXTRA_VALUE));
	}
	
	private void initViews() {
		mEtInput = (EditText)findViewById(R.id.si_et_input);
		mBtnOk = (Button)findViewById(R.id.si_btn_ok);
		mBtnCancel = (Button)findViewById(R.id.si_btn_cancel);
		mTvShortMsg = (TextView)findViewById(R.id.si_tv_short_message);
		mTvLongMsg = (TextView)findViewById(R.id.si_tv_long_message);
		mBtnTest = (Button)findViewById(R.id.si_btn_test);
	}
	
	private void assignListeners() {
		mBtnOk.setOnClickListener(mBtnOkOnClick);
		mBtnCancel.setOnClickListener(mBtnCancelOnClick);
		mBtnTest.setOnClickListener(mBtnTestOnClick);
	}

	
	private View.OnClickListener mBtnOkOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent();
			i.putExtra(EXTRA_VALUE, mEtInput.getText().toString());
			setResult(RESULT_OK, i);
			finish();			
		}
		
	};
	private View.OnClickListener mBtnCancelOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			setResult(RESULT_CANCELED);
			finish();
		}
		
	};
	private View.OnClickListener mBtnTestOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (HTTPHelper.isNetworkActive(mEtInput.getText().toString())) {
				Toast.makeText(getBaseContext(),"Connection made succesfully.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getBaseContext(), "Could not establish connection.", Toast.LENGTH_SHORT).show();
			}
		}
		
	};
}
