package com.galaxy.superstar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import com.galaxy.superstar.HttpManager.HttpQueryCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class BaoliaoDetailActivity extends CommonTitleBarActivity {
	private Button starName;
	private List<Integer> starNameList = new ArrayList<Integer>();
	
	private Handler handler = new Handler();

	private HttpQueryCallback saveCallback = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object queryId, final Object result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(state == HttpQueryCallback.STATE_OK) {
						findViewById(R.id.progress).setVisibility(View.GONE);
						
						JsonObject ret = null;
						try {
							ret = new Gson().fromJson((String)result, JsonObject.class);
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(BaoliaoDetailActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
							return;
						}
						switch(ret.get("status").getAsInt()) {
						case 101:
							Util.jump2Login(BaoliaoDetailActivity.this);
							break;
						case 1:
							Toast.makeText(BaoliaoDetailActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {									
									finish();
									overridePendingTransition(R.anim.in_from_left,	R.anim.out_to_right);
								}
							}, 1200);
							break;
						default:
							findViewById(R.id.progress).setVisibility(View.GONE);
							Toast.makeText(BaoliaoDetailActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
							break;
						}
					} else {
						Toast.makeText(BaoliaoDetailActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baoliao_detail);
		
		setCustomTitle("爆料");
		setLeftButton(getResources().getDrawable(R.drawable.back), this.onBackListeger);
		
		starName = (Button)findViewById(R.id.choose_star);
		
		findViewById(R.id.done).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		findViewById(R.id.done).setClickable(false);

		starName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String[] names = new String[]{"王力宏", "林志玲", "郭德刚", "美女"};
				new AlertDialog.Builder(BaoliaoDetailActivity.this)
					.setTitle("选择明星")
					.setItems(names, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							starName.setText(names[which]);
							dialog.dismiss();
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();				
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			leftView.performClick();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
