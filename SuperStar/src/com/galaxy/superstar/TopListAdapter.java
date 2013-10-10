package com.galaxy.superstar;


import java.util.ArrayList;
import java.util.List;

import com.galaxy.protobuf.TopList.PersonInfo;
import com.galaxy.superstar.HttpManager.HttpQueryCallback;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TopListAdapter extends BaseAdapter {
	Activity activity;
	public List<PersonInfo> data;
	
	private HttpQueryCallback headCallback = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object requestId, final Object result) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(state == HttpQueryCallback.STATE_OK) {
						Util.saveHead(activity, (String)requestId, (byte[])result);
						try {
							notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	};
	
	public TopListAdapter(Activity c) {
		activity = c;
		
		data = new ArrayList<PersonInfo>();
	}
	public List<PersonInfo> getData() {
		return data;
	}
	public void setData(PersonInfo[] info) {
//		for test
	}
//	public void appendData(PersonInfo[] info) {
//		if(data != null && data.length > 0 && info != null && info.length > 0) {
//			PersonInfo[] temp = data;
//			data = new PersonInfo[data.length + info.length];
//			System.arraycopy(temp, 0, data, 0, temp.length);
//			System.arraycopy(info, 0, data, temp.length, info.length);
//		}
//	}
	
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
	public long getItemId(int position) {
		return 0;
	}

    public int getItemViewType(int position) {
        return position >= 3 ? 3 : position;
    }

    public int getViewTypeCount() {
        return 4;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			switch (position) {
			case 0:
				convertView = View.inflate(activity, R.layout.top_list_item_1, null);
				break;

			case 1:
				convertView = View.inflate(activity, R.layout.top_list_item_2, null);
				break;

			case 2:
				convertView = View.inflate(activity, R.layout.top_list_item_3, null);
				break;

			default:
				convertView = View.inflate(activity, R.layout.top_list_item, null);
				break;
			}
		}
		((TextView)convertView.findViewById(R.id.rank)).setText("" + (position + 1));

		if(data.get(position).getName() != null) {
			((TextView)convertView.findViewById(R.id.name)).setText(data.get(position).getName());			
		}

//		if(data.get(position).getDescription() != null) {
//			((TextView)convertView.findViewById(R.id.desc)).setText(data.get(position).getDescription());
//		}
//		((TextView)convertView.findViewById(R.id.rose_no)).setText("" + data.get(position).getRose());
//		((TextView)convertView.findViewById(R.id.car_no)).setText("" + data.get(position).getCar());
//		((TextView)convertView.findViewById(R.id.diamond_no)).setText("" + data.get(position).getDiamand());
		
		return convertView;
	}
}
