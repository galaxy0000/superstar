package com.galaxy.superstar;

import java.lang.ref.WeakReference;
import java.util.List;

import com.galaxy.superstar.HttpManager.HttpQueryCallback;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NearbyBuddyListActivity extends CommonTitleBarActivity {
	private List<NearbyBuddyInfo> data;
	
	private class NearbyBuddyListAdapter extends BaseAdapter {
		private Context context;
		private List<NearbyBuddyInfo> nearbyBuddys;

		private HttpQueryCallback headCallback = new HttpQueryCallback() {
			@Override
			public void onQueryComplete(int state, Object requestId, Object result) {
//				ImageCache.putIntoCache((String)requestId, (byte[])result, false);
				Util.saveHead(context, (String)requestId, (byte[])result);
//				Bitmap bitmap = BitmapFactory.decodeByteArray((byte[])result, 0, ((byte[])result).length);
//				if(bitmap != null) {
//					Util.headMap.put((String)requestId, new BitmapDrawable(bitmap));
//				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						notifyDataSetChanged();
					}
				});
			}
		};

		public NearbyBuddyListAdapter(Context c) {
			context = c;
		}
		public void setData(List<NearbyBuddyInfo> info) {
			nearbyBuddys = info;
		}
		@Override
		public int getCount() {
			if(nearbyBuddys != null){
				return nearbyBuddys.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(position >= 0 && position < nearbyBuddys.size()) {
				return nearbyBuddys.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = View.inflate(context, R.layout.buddy_list_item, null);
			}
			if(nearbyBuddys.get(position).id != null) {
				((TextView)convertView.findViewById(R.id.nick)).setText(nearbyBuddys.get(position).name);			
			}
				
			((TextView)convertView.findViewById(R.id.distance)).setText(nearbyBuddys.get(position).distance / 1000.0f + "公里以内");
			
			if(nearbyBuddys.get(position).pre_achieve != null) {
				((TextView)convertView.findViewById(R.id.description)).setText(nearbyBuddys.get(position).pre_achieve );
			}
			if(nearbyBuddys.get(position).small_imgpath != null && !nearbyBuddys.get(position).small_imgpath.equals("")) {
//				Bitmap head = ImageCache.getFromCache(nearbyBuddys.get(position).small_imgpath);
				int size = (int)(SuperApp.instance.getResources().getDisplayMetrics().density * 60);
				Drawable head = Util.loadRoundHead(context, nearbyBuddys.get(position).small_imgpath, size, size);
				if(head == null) {
					convertView.findViewById(R.id.head).setBackgroundResource(R.drawable.head);
					HttpManager.asyncGetBytes(nearbyBuddys.get(position).small_imgpath,
							nearbyBuddys.get(position).small_imgpath,
							new WeakReference<HttpManager.HttpQueryCallback>(headCallback));
				} else {
					convertView.findViewById(R.id.head).setBackgroundDrawable(head);
				}
			} else {
				convertView.findViewById(R.id.head).setBackgroundResource(R.drawable.head);
			}
			
			return convertView;
		}
	}
	private ListView listview;
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
			NearbyBuddyInfo info = (NearbyBuddyInfo)parent.getAdapter().getItem(position);
			if(info != null && info.id != null) {
				Intent intent = new Intent(NearbyBuddyListActivity.this, StarDetailActivity.class);
				intent.putExtra(AppConstants.ikey_buddy_id, info.id);
				intent.putExtra(AppConstants.ikey_buddy_head, info.small_imgpath);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby_buddylist);
		
		setCustomTitle("附近的创业合伙人");
		setLeftButton(getResources().getDrawable(R.drawable.back), this.onBackListeger);
		
		listview = (ListView)findViewById(R.id.buddylist);
		
		listview.setOnItemClickListener(itemClickListener);
		
		data = (List<NearbyBuddyInfo>)getIntent().getSerializableExtra("data");
		NearbyBuddyListAdapter adapter = new NearbyBuddyListAdapter(NearbyBuddyListActivity.this); 
		adapter.setData(data);
		listview.setAdapter(adapter);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			leftView.performClick();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		for(NearbyBuddyInfo info : data) {			
			Util.deleteHeadFile(this, info.small_imgpath);
		}
		
		super.onDestroy();
	}
}
