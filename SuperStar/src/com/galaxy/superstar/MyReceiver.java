package com.galaxy.superstar;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(AppConstants.push_intent_action.equals(intent.getAction()) && !isForgroundRunning(context)) { 
			Log.v("rz", "pushdata : " + intent.getExtras().getString("com.avos.avoscloud.Data"));
			String alert = "您有新的站内信，请点击查看。";
			JsonObject obj = new Gson().fromJson(intent.getExtras().getString("com.avos.avoscloud.Data"), JsonObject.class);
			if(obj.get("alert") != null) {
				alert = obj.get("alert").getAsString();
			}

			Notification notification = new Notification(R.drawable.ic_launcher, "您有新的站内信", System.currentTimeMillis());
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			PendingIntent pt= PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);    
			notification.setLatestEventInfo(context, "站内信", alert, pt);  
			NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE); 
			nm.notify(AppConstants.notification_id, notification);  
		}
	}
	
    private boolean isForgroundRunning(Context context) {
    	try {
	        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
	        if (tasksInfo.size() > 0) {
	            if (context.getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
	                return true;
	            }
	        }
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
        return false;
    }
}
