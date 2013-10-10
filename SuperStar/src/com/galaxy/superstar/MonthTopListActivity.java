package com.galaxy.superstar;

import java.util.Random;

import com.galaxy.protobuf.TopList.PersonInfo;
import com.galaxy.superstar.HttpManager.HttpQueryCallback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class MonthTopListActivity extends CommonTitleBarActivity {

	private TextView stateText;
	private PullRefreshListView listview;
	private TopListAdapter adapter;

	HttpQueryCallback getlistCallback = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object queryId, final Object result) {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					findViewById(R.id.state).setVisibility(View.GONE);
					findViewById(R.id.starlist).setVisibility(View.VISIBLE);
					if(state == HttpQueryCallback.STATE_OK) {
						adapter = new TopListAdapter(MonthTopListActivity.this);
						Random random = new Random(System.currentTimeMillis());
						for(int i = 0; i < 24; i++) {
							PersonInfo.Builder builder = PersonInfo.newBuilder();
							builder.setId(i);
							builder.setMale(i % 2 == 0 ? true : false);
							builder.setName("我 就 是 : " + i);
							builder.setMark(i % 2 == 0 ? "粉丝们，你们好。。。。" : "我是屌丝女神，快来给我加人气");
//							builder.setRose(random.nextInt(4999));
//							builder.setCar(random.nextInt(299));
//							builder.setDiamand(random.nextInt(99));

							adapter.data.add(builder.build());
						}
						listview.setAdapter(adapter);
					} else {
						stateText.setText(getText(R.string.failed_click_reload));
					}
				}
			});
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_month_top_list);
		
		setCustomTitle("月排行榜");

		stateText = (TextView)findViewById(R.id.state);
		stateText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stateText.setText(getResources().getString(R.string.loading));
				Util.getMonthList(getlistCallback);
			}
		});
		stateText.setClickable(false);
		
		listview = (PullRefreshListView)findViewById(R.id.starlist);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MonthTopListActivity.this, StarDetailActivity.class);
				startActivity(intent);
				getParent().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		
		Util.getMonthList(getlistCallback);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
}
