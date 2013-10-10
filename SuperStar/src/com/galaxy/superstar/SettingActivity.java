package com.galaxy.superstar;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.galaxy.superstar.HttpManager.HttpQueryCallback;
import com.galaxy.superstar.LoginActivity.AccessToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.content.Intent;

public class SettingActivity extends CommonTitleBarActivity {
	private Tencent mTencent;
    
	private void shareToQQ() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("comment", URLEncoder.encode("分享：粉丝们，快来给***加人气啊。")));
				params.add(new BasicNameValuePair("title", URLEncoder.encode("超级明星榜")));
				params.add(new BasicNameValuePair("url", URLEncoder.encode("http://www.ycpai.com?" + System.currentTimeMillis())));
				params.add(new BasicNameValuePair("site", URLEncoder.encode("超级明星榜 Android App")));
				params.add(new BasicNameValuePair("fromurl", URLEncoder.encode("http://www.ycpai.com")));
				params.add(new BasicNameValuePair("oauth_consumer_key", mTencent.getAppId()));
				params.add(new BasicNameValuePair("access_token", mTencent.getAccessToken()));
				params.add(new BasicNameValuePair("openid", mTencent.getOpenId()));
				String result = HttpManager.syncHttpsPost("https://graph.qq.com/share/add_share", params);
				
        		JsonObject postRet = new Gson().fromJson(result, JsonObject.class);
        		if(postRet.get("ret") != null && postRet.get("ret").getAsInt() == 0) {
        			Util.hideProgress(SettingActivity.this, R.id.progress);
        			Util.showToast(SettingActivity.this, "分享成功", Toast.LENGTH_SHORT);
        		} else {
        			Util.hideProgress(SettingActivity.this, R.id.progress);
        			Util.showToast(SettingActivity.this, "分享失败", Toast.LENGTH_SHORT);
        		}
			}
		}).run();
	}
	private IUiListener qqLoginListener = new IUiListener() {
		@Override
		public void onError(UiError arg0) {
			Util.showToast(SettingActivity.this, "QQ授权失败", Toast.LENGTH_SHORT);
		}
		@Override
		public void onComplete(JSONObject arg0) {
			shareToQQ();
		}
		@Override
		public void onCancel() {
			Util.showToast(SettingActivity.this, "QQ授权取消", Toast.LENGTH_SHORT);
		}
	};

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data) ;
	}

	private HttpQueryCallback logoutCallback = new HttpQueryCallback() {
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
							Toast.makeText(SettingActivity.this, getResources().getString(R.string.logout_failed), Toast.LENGTH_SHORT).show();
							return;
						}
						if(ret.get("status").getAsInt() == 101) {
							Util.jump2Login(SettingActivity.this);							
						}else if(ret.get("status").getAsInt() == 1) {
							((SuperApp)getApplication()).saveLogoutState();
							
							finish();
							startActivity(new Intent(SettingActivity.this, LoginActivity.class));
							getParent().overridePendingTransition(R.anim.in_from_right,	R.anim.out_to_left);
						} else {
							Toast.makeText(SettingActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
						}
					} else {
						findViewById(R.id.progress).setVisibility(View.GONE);
						Toast.makeText(SettingActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();						
					}
				}
			});
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		setCustomTitle("设置");
		
		findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.progress).setVisibility(View.VISIBLE);
				Util.logout(logoutCallback);
			}
		});
		
		findViewById(R.id.item1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SettingActivity.this, MyDetailActivity.class);
				i.putExtra(AppConstants.ikey_src, SettingActivity.class.getSimpleName());
				startActivity(i);
				getParent().overridePendingTransition(R.anim.in_from_right,	R.anim.out_to_left);
			}
		});
		findViewById(R.id.item2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(SettingActivity.this, BaoliaoDetailActivity.class);
//				startActivity(i);
//				getParent().overridePendingTransition(R.anim.in_from_right,	R.anim.out_to_left);
			}
		});
		findViewById(R.id.item3).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("http://www.ycpai.com");
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
			}
		});
		findViewById(R.id.item4).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Weibo weibo = Weibo.getInstance(AppConstants.weiboApiKey, AppConstants.weiboRedirectURL, AppConstants.SINA_SCOPE);
				weibo.anthorize(SettingActivity.this, new WeiboAuthDialogListener());

			}
		});
		findViewById(R.id.item5).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTencent = Tencent.createInstance(AppConstants.TENCENT_APP_ID, SettingActivity.this);

		        if (!mTencent.isSessionValid()) {
		            mTencent.login(SettingActivity.this, AppConstants.QQ_SCOPE, qqLoginListener);
		        } else {
		        	shareToQQ();
		        }
//		        boolean ready = mTencent.isSessionValid() && mTencent.getOpenId() != null;
//		        if (!ready)
//		            Toast.makeText(this, "login and get openId first, please!",
//		                    Toast.LENGTH_SHORT).show();
			}
		});
		findViewById(R.id.item6).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri smsToUri = Uri.parse("smsto:");// 联系人地址 
	            Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri); 
	            mIntent.putExtra("sms_body", "粉丝们，快来给***加人气啊。");// 短信内容 
	            startActivity(mIntent); 
			}
		});
		findViewById(R.id.item7).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {"admin@ycpai.com"});  // 收件人           
				intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "意见反馈"); // 主题           
				intent.putExtra(android.content.Intent.EXTRA_TEXT, "我要反馈些意见："); // 正文
				intent.setType("plain/text");
				startActivity(Intent.createChooser(intent, "Mail Chooser"));
			}
		});

		findViewById(R.id.item8).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.progress).setVisibility(View.VISIBLE);
				Util.getAboutInfo(aboutCallback);
			}
		});
	}
	
	HttpQueryCallback aboutCallback = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object requestId, final Object result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {					
					findViewById(R.id.progress).setVisibility(View.GONE);
					if(state == STATE_OK) {
						JsonObject json = null;
						String about = "";
						try{
							json = new Gson().fromJson((String)result, JsonObject.class);
							about = json.get("text").getAsString();
						} catch (Exception e) {
							Util.showToast(SettingActivity.this, "获取失败，请重试", Toast.LENGTH_SHORT);
							return;
						}
						if(about != null && !"".equals(about)) {
							Intent i = new Intent(SettingActivity.this, AboutActivity.class);
							i.putExtra("about", about);
							startActivity(i);
							getParent().overridePendingTransition(R.anim.in_from_right,	R.anim.out_to_left);
						} else {
							Util.showToast(SettingActivity.this, "获取失败，请重试", Toast.LENGTH_SHORT);
						}
					} else {
						Util.showToast(SettingActivity.this, "获取失败，请重试", Toast.LENGTH_SHORT);
					}
				}
			});
		}
	};
	private void startSinaLogin(final String authCode) {
    	if(authCode != null && !authCode.equals("")) {
    		findViewById(R.id.progress).setVisibility(View.VISIBLE);
    		new Thread(new Runnable() {
				@Override
				public void run() {
		    		try{
		        		String urlString = "https://api.weibo.com/oauth2/access_token";
		        		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		        		postData.add(new BasicNameValuePair("client_id", "3675955695"));
		        		postData.add(new BasicNameValuePair("client_secret", "514a242498ae10213fb91b76bebc791f"));
		        		postData.add(new BasicNameValuePair("grant_type", "authorization_code"));
		        		postData.add(new BasicNameValuePair("code", authCode));
		        		postData.add(new BasicNameValuePair("redirect_uri", "http://www.ycpai.com"));

		        		String result = HttpManager.syncHttpsPost(urlString, postData);
		        		AccessToken token = new Gson().fromJson(result, AccessToken.class);
		        		
		        		urlString = "https://api.weibo.com/2/statuses/update.json";
		        		postData = new ArrayList<NameValuePair>();
		        		postData.add(new BasicNameValuePair("access_token", token.access_token));
		        		postData.add(new BasicNameValuePair("status", URLEncoder.encode("粉丝们，快来给***加人气啊。")));
		        		postData.add(new BasicNameValuePair("visible", "0"));
		        		result = HttpManager.syncHttpsPost(urlString, postData);

		        		JsonObject postRet = new Gson().fromJson(result, JsonObject.class);
		        		if(postRet.get("id") != null) {
		        			Util.hideProgress(SettingActivity.this, R.id.progress);
		        			Util.showToast(SettingActivity.this, "分享成功", Toast.LENGTH_SHORT);
		        		} else {
		        			Util.hideProgress(SettingActivity.this, R.id.progress);
		        			Util.showToast(SettingActivity.this, "分享失败", Toast.LENGTH_SHORT);
		        		}
		    		} catch(Exception e) {
		    			Util.hideProgress(SettingActivity.this, R.id.progress);
		    			Util.showToast(SettingActivity.this, "分享失败", Toast.LENGTH_SHORT);
		    		}
				}
			}).start();
    	}
	}
	private void sinaLoginError() {
		Util.showToast(this, "新浪微博认证失败 ", Toast.LENGTH_SHORT);
	}
    class WeiboAuthDialogListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
        	String code = values.getString("code");
        	startSinaLogin(code);
        }

        @Override
        public void onError(WeiboDialogError e) {
        	sinaLoginError();
        }

        @Override
        public void onCancel() {
        	sinaLoginError();
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	sinaLoginError();
        }
    }

}
