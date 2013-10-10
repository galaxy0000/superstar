package com.galaxy.superstar;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import com.galaxy.protobuf.TopList.PersonInfo;
import com.galaxy.protobuf.TopList.RespDataPackage;
import com.galaxy.protobuf.TopList.RespDataType;
import com.galaxy.protobuf.TopList.RespGetList;
import com.galaxy.superstar.HttpManager.HttpQueryCallback;
import com.galaxy.superstar.PullRefreshListView.OnRefreshListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
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
					listview.onRefreshComplete(true);
					
					if(state == HttpQueryCallback.STATE_OK) {
						stateText.setVisibility(View.GONE);
						findViewById(R.id.starlist).setVisibility(View.VISIBLE);
						
						RespGetList respGetList = null;
						try {
							RespDataPackage	respDataPackage = RespDataPackage.parseFrom((InputStream)result);
							if (respDataPackage.getType() == RespDataType.DATA_TYPE_RespGetList)
							{
								respGetList = RespGetList.parseFrom(respDataPackage.getData());
							}
						} catch (IOException e) {
							e.printStackTrace();
							Util.showToast(MonthTopListActivity.this, "数据解析失败", Toast.LENGTH_SHORT);
							return;
						}
						adapter = new TopListAdapter(MonthTopListActivity.this);
						List<PersonInfo> infos = respGetList.getInfosList();
						for(int i = 0; i < infos.size(); i++) {
							adapter.data.add(infos.get(i));
						}
						listview.setAdapter(adapter);
					} else {
						stateText.setText(getText(R.string.failed_click_reload));
						stateText.setClickable(true);
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
		listview.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				Util.getWeekList(getlistCallback);
			}
		});
		
		Util.getMonthList(getlistCallback);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
}
