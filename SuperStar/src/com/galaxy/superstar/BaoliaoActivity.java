package com.galaxy.superstar;

import java.util.ArrayList;

import com.galaxy.superstar.HttpManager.HttpQueryCallback;
import com.google.gson.Gson;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BaoliaoActivity extends CommonTitleBarActivity {

	private Handler handler = new Handler();
	
	private HttpManager.HttpQueryCallback activeNum = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object requestId, final Object result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(state == HttpQueryCallback.STATE_OK) {
						int num = 0;
						try {
							num = Integer.parseInt((String) result);
							SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaoliaoActivity.this);
							sp.edit().putInt(AppConstants.active_num, num).commit();
							setActiveNum();
						} catch (Exception e) {
						}
					}
				}
			});
		}
	};
	
	private void setActiveNum() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		int num = sp.getInt(AppConstants.active_num, 22757);
		((TextView)findViewById(R.id.activeNum)).setText("");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baoliao);
		
		setCustomTitle("爆料");
		//Util.getActiveNum(activeNum);
		//setActiveNum();
		
		findViewById(R.id.baoliao).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BaoliaoActivity.this, BaoliaoDetailActivity.class);
				startActivity(intent);
				getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
	}
}
