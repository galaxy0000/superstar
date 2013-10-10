package com.galaxy.superstar;


import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SessionMessageAdapter extends BaseAdapter {
	private Activity activity;
	private List<SingleMessage> data;
	private String myHeadUrl;
	private String hisHeadUrl;
	private Drawable myHead;
	private Drawable hisHead;
	
	public void setMyHeadUrl(String url) {
		myHeadUrl = url;
		int size = (int)(42 * activity.getResources().getDisplayMetrics().density);
		if(url != null && !url.equals("")) {
			myHead = Util.loadRoundHead(activity, myHeadUrl, size, size);
		}
	}
	public void setHisHeadUrl(String url) {
		hisHeadUrl = url;
		int size = (int)(42 * activity.getResources().getDisplayMetrics().density);
		if(url != null && !url.equals("")) {
			hisHead = Util.loadRoundHead(activity, hisHeadUrl, size, size);
		}
	}
	
	public List<SingleMessage> getData() {
		return data;
	}
	public SessionMessageAdapter(Activity c) {
		activity = c;
	}

	public void setData(List<SingleMessage> info) {
		data = info;
	}
	public void appendDataFront(List<SingleMessage> info) {
		data.addAll(0, info);
	}
	public void appendData(SingleMessage msg) {
		data.add(msg);
	}	
	@Override
	public int getCount() {
		if(data != null){
			return data.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(position >= 0 && position < data.size()) {
			return data.get(position);
		}
		return null;
	}
	@Override
    public int getViewTypeCount() {
        return 2;
    }
	
	@Override
    public int getItemViewType(int position) {
		String sender = data.get(position).sender_id;
		if(SuperApp.instance.userId.equals(sender)) {
			return 0;
		} else {
			return 1;
		}
    }


	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String sender = data.get(position).sender_id;
		if(convertView == null) {
			if(SuperApp.instance.userId.equals(sender)) {
				convertView = View.inflate(activity, R.layout.session_msg_item_r, null);
				convertView.findViewById(R.id.head).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(activity, MyDetailActivity.class);
						i.putExtra(AppConstants.ikey_src, SessionMessageAdapter.class.getSimpleName());
						activity.startActivity(i);
						activity.overridePendingTransition(R.anim.in_from_right,	R.anim.out_to_left);
					}
				});
			} else {
				convertView = View.inflate(activity, R.layout.session_msg_item_l, null);
				convertView.findViewById(R.id.head).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(activity, StarDetailActivity.class);
						i.putExtra(AppConstants.ikey_src, SessionMessageAdapter.class.getSimpleName());
						i.putExtra(AppConstants.ikey_buddy_id, sender);
						i.putExtra(AppConstants.ikey_buddy_head, hisHeadUrl);

						activity.startActivity(i);
						activity.overridePendingTransition(R.anim.in_from_right,	R.anim.out_to_left);
					}
				});
			}
		}
		if(data.get(position).update_time != null) {
			((TextView)convertView.findViewById(R.id.time)).setText(data.get(position).update_time);
		}
		if(data.get(position).message != null) {
			((TextView)convertView.findViewById(R.id.msg)).setText(data.get(position).message);
		}
		if(SuperApp.instance.userId.equals(sender)) {
			if(myHead != null) {
				convertView.findViewById(R.id.head).setBackgroundDrawable(myHead);
			}
		} else {
			if(hisHead != null) {
				convertView.findViewById(R.id.head).setBackgroundDrawable(hisHead);
			}
		}

		return convertView;
	}
}
