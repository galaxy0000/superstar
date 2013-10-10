package com.galaxy.superstar;


import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

public class SuperApp extends Application {
	static SuperApp instance;

	public String loginStr;
	public String userId;
	public String deviceToken;
	
	public String recordFile;
	
	public boolean canPush = false;
	
	public boolean isLogin() {
		if(userId != null && !"".equals(userId) && loginStr != null && !"".equals(loginStr)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void saveLogoutState() {
		this.userId = null;
		this.loginStr = null;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		sp.edit().remove(AppConstants.user_id).remove(AppConstants.login_str).commit();
	}
	
	public void saveLoginState(String userId, String loginStr) {
		this.userId = userId;
		this.loginStr = loginStr;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		sp.edit().putString(AppConstants.user_id, userId).putString(AppConstants.login_str, loginStr).commit();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		Resources res = getResources();  
		Configuration config = new Configuration();  
		config.setToDefaults();  
		res.updateConfiguration(config,res.getDisplayMetrics() );

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		this.userId = sp.getString(AppConstants.user_id, "");
		this.loginStr = sp.getString(AppConstants.login_str, "");
	}
}
