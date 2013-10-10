package com.galaxy.superstar;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class WelcomeActivity extends Activity {

	private Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		final Intent intent = new Intent();
//		if(((SuperApp)getApplication()).isLogin()) {
//			intent.setClass(this, MainActivity.class);
//		} else {
//			intent.setClass(this, LoginActivity.class);
//		}
		intent.setClass(this, MainActivity.class);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		}, 1000);
	}
}
