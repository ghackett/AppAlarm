package com.episode6.android.appalarm.pro;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class VolumeChooser extends Activity {

	private TextView mTvVol;
	private SeekBar mSbVol;
	private Button mBtnOk, mBtnCancel;
	
	private int mCurrentVolume;
	private int mMaxVolume;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volume_chooser);
		setTitle("Set Media Volume...");
		
		mCurrentVolume = getIntent().getIntExtra(AlarmItem.KEY_MEDIA_VOLUME, 100);
		
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		mMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		am = null;
		if (mCurrentVolume > mMaxVolume) {
			mCurrentVolume = mMaxVolume;
		}
		
		initViews();
		assignListeners();
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.volume_chooser);
		
		initViews();
		assignListeners();
	}
	
	private void initViews() {
		mTvVol = (TextView)findViewById(R.id.vc_tv_volume);
		mSbVol = (SeekBar)findViewById(R.id.vc_sb_volume);
		mBtnOk = (Button)findViewById(R.id.vc_btn_ok);
		mBtnCancel = (Button)findViewById(R.id.vc_btn_cancel);
		
		
		mSbVol.setMax(mMaxVolume);
		vUpdateTextView();
		mSbVol.setProgress(mCurrentVolume);
		
	}
	private void assignListeners() {
		mSbVol.setOnSeekBarChangeListener(mSbVolOnChange);
		mBtnOk.setOnClickListener(mBtnOkOnClick);
		mBtnCancel.setOnClickListener(mBtnCancelOnClick);
	}
	
	private void vUpdateTextView() {
		mTvVol.setText(mCurrentVolume + "");
	}
	
	
	private SeekBar.OnSeekBarChangeListener mSbVolOnChange = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			mCurrentVolume = progress;
			vUpdateTextView();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	};
		
	private View.OnClickListener mBtnOkOnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			Intent i = new Intent();
			i.putExtra(AlarmItem.KEY_MEDIA_VOLUME, mCurrentVolume);
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
	
}
