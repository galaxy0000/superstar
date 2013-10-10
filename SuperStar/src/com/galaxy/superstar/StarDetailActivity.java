package com.galaxy.superstar;

import com.galaxy.superstar.HttpManager.HttpQueryCallback;
import com.google.gson.Gson;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StarDetailActivity extends CommonTitleBarActivity {

	protected String starId;
	
	private HttpQueryCallback detailCallback = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object requestId, final Object result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					findViewById(R.id.progress).setVisibility(View.GONE);
					if(state == HttpQueryCallback.STATE_OK) {
					} else {
						Util.showToast(StarDetailActivity.this, getResources().getString(R.string.failed_reload), Toast.LENGTH_SHORT);
					}
				}
			});			
		}
	};
	
	private OnClickListener sendListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.send_rose:
				Util.showToast(StarDetailActivity.this, "送 花 成功", Toast.LENGTH_SHORT);
				break;
			case R.id.send_car:
				Util.showToast(StarDetailActivity.this, "送 车 成功", Toast.LENGTH_SHORT);
				break;
			case R.id.send_diamond:
				Util.showToast(StarDetailActivity.this, "送 钻石 成功", Toast.LENGTH_SHORT);
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buddy_detail);
		
		setCustomTitle("明星信息");
		setLeftButton(getResources().getDrawable(R.drawable.back), this.onBackListeger);

//		starId = getIntent().getExtras().getString(AppConstants.ikey_buddy_id);
		
		findViewById(R.id.send_rose).setOnClickListener(sendListener);
		findViewById(R.id.send_car).setOnClickListener(sendListener);
		findViewById(R.id.send_diamond).setOnClickListener(sendListener);
	}
	
//	protected void fillData(UserInfoDetail detailInfo) {
//		setCustomTitle(detailInfo.name);
//		((TextView)findViewById(R.id.nick)).setText(detailInfo.role_type);
//		((TextView)findViewById(R.id.city)).setText(detailInfo.city_str);
//		((TextView)findViewById(R.id.age)).setText(detailInfo.age_range + " " + detailInfo.work_mode);
//		((TextView)findViewById(R.id.lastLogin)).setText(detailInfo.last_login);
//		((TextView)findViewById(R.id.intro)).setText(detailInfo.pre_achieve);
//		if(detailInfo.skill_describe != null && !"".equals(detailInfo.skill_describe)) {
//			findViewById(R.id.skillLayout).setVisibility(View.VISIBLE);
//			((TextView)findViewById(R.id.skill)).setText(detailInfo.skill_describe);
//		} else {
//			findViewById(R.id.skillLayout).setVisibility(View.GONE);
//		}
//		((TextView)findViewById(R.id.eco)).setText(detailInfo.income_type);
//		((TextView)findViewById(R.id.motion)).setText(detailInfo.motivation);
//		((TextView)findViewById(R.id.time)).setText(detailInfo.time_support);
//		((TextView)findViewById(R.id.familySupport)).setText(detailInfo.family_support);
//		((TextView)findViewById(R.id.startupExperience)).setText(detailInfo.experience);
//		((TextView)findViewById(R.id.investablity)).setText(detailInfo.investablity);
//		
//		if("1".equals(detailInfo.mobile_vali_status)) {
//			findViewById(R.id.mobile).setVisibility(View.VISIBLE);
//		} else {
//			findViewById(R.id.mobile).setVisibility(View.GONE);
//		}
//		if("1".equals(detailInfo.email_vali_status)) {
//			findViewById(R.id.email).setVisibility(View.VISIBLE);
//		} else {
//			findViewById(R.id.email).setVisibility(View.GONE);
//		}
//		int kaopuStar = 0;
//		try {
//			kaopuStar = Integer.parseInt(detailInfo.kaopu_star);
//		} catch(Exception e) {
//		}
//		LinearLayout ll = (LinearLayout)findViewById(R.id.icons);
//
//		if(kaopuStar > 0 && kaopuStar <= 5) {
//			for(int i = 0; i < 5; i++) {
//				if(i < kaopuStar) {
//					ll.getChildAt(2 + i).setVisibility(View.VISIBLE);
//				} else {					
//					ll.getChildAt(2 + i).setVisibility(View.GONE);
//				}
//			}
//		}
//	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			leftView.performClick();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
//	private HttpQueryCallback contactCallback = new HttpQueryCallback() {
//		@Override
//		public void onQueryComplete(final int state, final Object requestId, final Object result) {
//			runOnUiThread(new Runnable() {
//				@Override
//				public void run() {					
//					findViewById(R.id.progress).setVisibility(View.GONE);
//					if(state == HttpQueryCallback.STATE_OK) {						
//						RandomBuddyListResult ret = null;
//						try {
//							ret = new Gson().fromJson((String)result, RandomBuddyListResult.class);
//						} catch (Exception e) {
//							e.printStackTrace();
//							return;
//						}
//						
//						switch (ret.status) {
//						case 101:
//							Util.jump2Login(StarDetailActivity.this);
//							break;
//						case 1:
//							Toast.makeText(StarDetailActivity.this, getResources().getString(R.string.contact_user_done), Toast.LENGTH_SHORT).show();
//							break;
//						default:
//							Toast.makeText(StarDetailActivity.this, ret.msg, Toast.LENGTH_SHORT).show();
//							break;
//						}
//					} else {
//						Toast.makeText(StarDetailActivity.this, R.string.contact_user_failed, Toast.LENGTH_SHORT).show();				
//					}
//				}
//			});
//		}
//	};

}