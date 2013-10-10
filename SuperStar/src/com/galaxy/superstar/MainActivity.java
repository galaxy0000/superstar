package com.galaxy.superstar;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;

public class MainActivity extends TabActivity {

	private TabHost mTabHost;
	private TabWidget mTabWidget;
	private String[] mTabsId = { "0", "1", "2", "3" };
	private View[] mTabs = new View[4];
	private int[] mIcons = {R.drawable.session_icon, R.drawable.session_icon, R.drawable.buddy_icon, R.drawable.setting_icon};
	private int[] mFocusIcons = {R.drawable.session_icon_focus, R.drawable.session_icon_focus, R.drawable.buddy_icon_focus, R.drawable.setting_icon_focus};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTabs();
        
        mTabWidget.setCurrentTab(0);
		reportLocation(null);
		LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6000, 1000, locationListener);
    }
    
    private void setupTabs() {
    	mTabHost = getTabHost();
    	mTabWidget = mTabHost.getTabWidget();
    	mTabWidget.getLayoutParams().height = (int)(60 * getResources().getDisplayMetrics().density);
    	
    	mTabs[0] = getLayoutInflater().inflate(R.layout.tab_item_week, null);
    	mTabs[1] = getLayoutInflater().inflate(R.layout.tab_item_month, null);
    	mTabs[2] = getLayoutInflater().inflate(R.layout.tab_item_baoliao, null);
    	mTabs[3] = getLayoutInflater().inflate(R.layout.tab_item_setting, null);
    	
    	mTabHost.addTab(mTabHost.newTabSpec(mTabsId[0]).setIndicator(mTabs[0]).setContent(new Intent(this, WeekTopListActivity.class)));
    	mTabHost.addTab(mTabHost.newTabSpec(mTabsId[1]).setIndicator(mTabs[1]).setContent(new Intent(this, MonthTopListActivity.class)));
    	mTabHost.addTab(mTabHost.newTabSpec(mTabsId[2]).setIndicator(mTabs[2]).setContent(new Intent(this, BaoliaoActivity.class)));
    	mTabHost.addTab(mTabHost.newTabSpec(mTabsId[3]).setIndicator(mTabs[3]).setContent(new Intent(this, SettingActivity.class)));
    	mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
    		@Override
    		public void onTabChanged(String tabId) {
    			int index = Integer.parseInt(tabId);
    			for(int i = 0; i < mTabs.length; i++) {
    				mTabs[i].setBackgroundResource(R.drawable.tab_bg);
    				mTabs[i].findViewById(R.id.icon).setBackgroundResource(mIcons[i]);
    			}
    			mTabs[index].setBackgroundResource(R.drawable.tab_bg_focus);
    			mTabs[index].findViewById(R.id.icon).setBackgroundResource(mFocusIcons[index]);
    		}
    	});
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
	}
    
    @Override
    protected void onNewIntent(Intent intent) {
    	reportLocation(null);
    }
    
	private void reportLocation(Location location) {
		if(location == null) {
			LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
					&& locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
				location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			} else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
					&& locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			} 
		}
		if(location != null) {
			Util.reportLocation(location.getLongitude(), location.getLatitude());
		}
	}

	private LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.v("rz", "location changed");
			reportLocation(location);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}
	};
}
