package com.galaxy.superstar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.galaxy.protobuf.TopList.ReqDataPackage;
import com.galaxy.protobuf.TopList.ReqDataType;
import com.galaxy.protobuf.TopList.ReqGetList;
import com.galaxy.protobuf.TopList.RespDataPackage;
import com.galaxy.protobuf.TopList.RespDataType;
import com.galaxy.protobuf.TopList.RespGetList;
import com.galaxy.superstar.MD5;
import com.galaxy.superstar.HttpManager.HttpQueryCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

public class Util{
	public static void login(String userName, String password, HttpQueryCallback callback) {
		String url = AppConstants.service_url + "move/login?platform=2&device_token=" + SuperApp.instance.deviceToken;
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair("email", userName));
		postData.add(new BasicNameValuePair("password", password));
		HttpManager.asyncPost(url, postData, null, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	public static void logout(HttpQueryCallback callback) {
		String url = AppConstants.service_url + "move/log_out?login_code=" + SuperApp.instance.loginStr;
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}

	public static void sinaLogin(String sinaCode, HttpQueryCallback callback) {
		String url = AppConstants.service_url + "move/sina_login";
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair("id", sinaCode));
		HttpManager.asyncPost(url, postData, null, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}

	public static void qqLogin(String qqCode, HttpQueryCallback callback) {
		String url = AppConstants.service_url + "move/qq_login";
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair("code", qqCode));
		HttpManager.asyncPost(url, postData, null, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}

	public static void register(String userName, String password, String nick, String stateId, String cityId, HttpQueryCallback callback) {
		String url = AppConstants.service_url + "move/register";
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add(new BasicNameValuePair("email", userName));
		postData.add(new BasicNameValuePair("password", password));
		postData.add(new BasicNameValuePair("nickname", nick));
		postData.add(new BasicNameValuePair("state_id", stateId));
		postData.add(new BasicNameValuePair("city_id", cityId));
		HttpManager.asyncPost(url, postData, null, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	
	public static boolean checkMail(String mail) {
		String mailPattern = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$";
		Pattern pattern = Pattern.compile(mailPattern);
		Matcher matcher = pattern.matcher(mail);
		return matcher.find();
	}
	public static boolean checkPassword(String password) {
		if(password.length() < 6 || password.length() > 20) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void getRandomBuddy(HttpQueryCallback callback) {
		String url = appendParams("move/day_rand_user?");
//		String url = AppConstants.service_url + "move/day_rand_user?login_code=" + YcpApp.instance.loginStr + "&device_token=" + YcpApp.instance.deviceToken;
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}

	public static void getNearbyBuddy(Double longitude, Double latitude, HttpQueryCallback callback) {
		String url = appendParams("move/get_neighbours/" + longitude.toString() + "/" + latitude.toString() + "?");
//		String url = AppConstants.service_url + "move/get_neighbours/" + longitude.toString() + "/" + latitude.toString() + "?login_code=" + YcpApp.instance.loginStr;
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}

	public static void getSessionList(int page, HttpQueryCallback callback) {
		String url = appendParams("move/msg_list/" + page + "/?");
//		String url = AppConstants.service_url + "move/msg_list/" + page + "/?login_code=" + YcpApp.instance.loginStr;
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}

	public static void showToast(final Activity activity, final String text, final int time) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, text, time).show();
			}
		});
	}
	public static void hideProgress(final Activity activity, final int id) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				activity.findViewById(id).setVisibility(View.GONE);
			}
		});

	}
	
	public static Drawable loadRoundHead(Context c, String url, int width, int height) {
		if(url == null){
			return null;
		}
		Drawable result = null;
		String filePath = c.getFilesDir() +"/head/" + MD5.getDigest(url);
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		if(bitmap != null) { 
			bitmap = roundImage(bitmap, AppConstants.image_round, width, height);
			result = new BitmapDrawable(bitmap);
		}
		return result;
	}
	
	public static boolean saveHead(Context c, String url, byte[] data) {
		if(c == null || url == null || url.equals("") || data == null) {
			return false;
		}
		
		File destDir = new File(c.getFilesDir() + "/head");
		if(!destDir.exists()) {
			destDir.mkdir();
		}
		
		String imageName = MD5.getDigest(url);
		File destFile = new File(destDir + "/" + imageName);

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(destFile);
			fOut.write(data);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			destFile.delete();
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static void deleteHeadFile(Context c, String url) {
		File dest = new File(c.getFilesDir() + "/head/" + MD5.getDigest(url));
		if(dest.exists()) {
			dest.delete();
		}
	}
	
    public static final Bitmap roundImage(Bitmap src, float corner, int width, int height) {
    	if(null == src) {
    		return null;
    	}
    	
    	Bitmap output = null;
        try {
        	output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        }
        catch(OutOfMemoryError oome) {
        	oome.printStackTrace();
        	return null;
        }
        
        if(output != null)
        {
	        Canvas canvas = new Canvas(output);
	        Paint paint = new Paint();
	        final Rect srcRect = new Rect(0, 0, src.getWidth(), src.getHeight());
	        final Rect destRect = new Rect(0, 0, width, height);
	        final RectF rectF = new RectF(destRect);
	        final float roundPx = corner;
	        paint.setAntiAlias(true);
	        
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(src, srcRect, destRect, paint);
        }
        return output;
    }
    
    public static void getUserInfo(String userId, HttpQueryCallback callback) {
    	String url = appendParams("move/get_userinfo/" + userId + "?");
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
    }
    
    public static void contactUser(String userId, HttpQueryCallback callback) {
    	String url = appendParams("move/filter_rand_user/" + userId + "/1?");
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
    }
    
    public static void ignoreUser(String userId) {
    	String url = appendParams("move/filter_rand_user/" + userId + "/0?");
    	HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(null));
    }
    
    public static String loadStringFromAsset(Context context, String fileName){  
        String result="";  
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			result = new String(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
    
	public static void reportLocation(Double longitude, Double latitude) {
		if(SuperApp.instance.loginStr == null || SuperApp.instance.loginStr.equals("")) {
			return;
		}
		String url = appendParams("move/save_lbs/" + longitude.toString() + "/" + latitude.toString() + "?");
//		String url = AppConstants.service_url + login_code=" + YcpApp.instance.loginStr;
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(null));
	}

	public static void getSingleSession(String userId, int page, String id, HttpQueryCallback callback) {
		String url = appendParams("move/see_msg/" + userId + "/" + page + "?");
//		String url = AppConstants.service_url + "move/see_msg/" + userId + "/" + page + "?login_code=" + YcpApp.instance.loginStr;
		HttpManager.asyncGetString(url, HTTP.UTF_8, id, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	public static void getSingleSession(String userId, int page, HttpQueryCallback callback) {
		String url = appendParams("move/see_msg/" + userId + "/" + page + "?");
//		String url = AppConstants.service_url + "move/see_msg/" + userId + "/" + page + "?login_code=" + YcpApp.instance.loginStr;
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	
	public static void sendMessage(String userId, String msg, HttpQueryCallback callback) {
		String url = appendParams("move/send_msg/" + userId + "?");
//		String url = AppConstants.service_url + "move/send_msg/" + userId + "?login_code=" + YcpApp.instance.loginStr;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("info[message]", msg));
		HttpManager.asyncPost(url, params, null, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	
	public static void getActiveNum(HttpQueryCallback callback) {
		String url = AppConstants.service_url + "common/get_active_num";
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}

	public static void saveInfo(List<NameValuePair> params, HttpQueryCallback callback) {
		String url = appendParams("move/save_info?");
		HttpManager.asyncPost(url, params, null, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	
	public static void getMyRecommand(HttpQueryCallback callback) {
		String url = appendParams("move/android_my_tuijian?");
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	
	public static void saveMyRecommand(List<NameValuePair> params, HttpQueryCallback callback) {
		String url = appendParams("move/tuijian_update?");
		HttpManager.asyncPost(url, params, null, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	
	public static void getHead(String url, HttpQueryCallback callback) {
		HttpManager.asyncGetBytes(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	
	public static void jump2Login(Context c) {
		Intent intent = new Intent(c, LoginActivity.class);
		intent.putExtra("source", AppConstants.login_source);
		c.startActivity(intent);
	}
	
	public static void queryMyInfo(HttpQueryCallback callback) {
		String url = appendParams("move/android_my_info?");
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));

	}
	public static void getAboutInfo(HttpQueryCallback callback) {
		String url = AppConstants.service_url + "move/about";
		HttpManager.asyncGetString(url, new WeakReference<HttpManager.HttpQueryCallback>(callback));
	}
	///////////////////////////////////////////////////
	private static String appendParams(String str) {
		return AppConstants.service_url + str + "login_code=" + SuperApp.instance.loginStr + "&device_token=" + SuperApp.instance.deviceToken;
	}
	
	public static void getMonthList(HttpQueryCallback callback) {
		ReqGetList.Builder reqBuilder = ReqGetList.newBuilder();
		reqBuilder.setListId(0);
		reqBuilder.setDesc(true);
		reqBuilder.setStartIndex(0);
		reqBuilder.setNum(10);
		
		ReqGetList req = reqBuilder.build();
		
		ReqDataPackage.Builder builder = ReqDataPackage.newBuilder();
		builder.setType(ReqDataType.DATA_TYPE_ReqGetList);
		builder.setVersion(0);
		builder.setData(req.toByteString());
		
		ReqDataPackage data = builder.build();
		
		InputStream result = HttpManager.syncPost(HttpManager.url, data.toByteArray());
		
		RespDataPackage	respDataPackage;
		try {
			respDataPackage = RespDataPackage.parseFrom(result);
			if (respDataPackage.getType() == RespDataType.DATA_TYPE_RespGetList)
			{
				RespGetList respGetList = RespGetList.parseFrom(respDataPackage.getData());
				callback.onQueryComplete(0, null, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		callback.onQueryComplete(0, null, null);
	}
    public static void getStarInfo(String starId, HttpQueryCallback callback) {
    }

}
