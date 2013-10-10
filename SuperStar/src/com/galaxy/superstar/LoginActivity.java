package com.galaxy.superstar;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.galaxy.superstar.HttpManager.HttpQueryCallback;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{
	class AccessToken {
		String access_token;
		String remind_in;
		int expires_in;
		String uid;
		String scope;
	}
	class SinaInfo {
		String idstr;
		String name;
		String province;
		String city;
		String profile_image_url;
		String profile_url;
		String gender;
		String avatar_large;
	}

	private Tencent mTencent;
	private EditText user;
	private EditText password;
	private String source;
	private Handler handler = new Handler();
	
	private class LoginResult {
		public int status;
		public String msg;
		public String user_id;
		public String login_str;
	}
	private void onLoginSuccess(LoginResult ret) {
		((SuperApp)getApplication()).saveLoginState(ret.user_id, ret.login_str);
		
		Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
		findViewById(R.id.progress).setVisibility(View.GONE);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
				if(!"relogin".equals(source)) {
					Intent i = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(i);
				}
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		}, 1200);
	}
	private HttpQueryCallback loginCallback = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object queryId, final Object result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(state == HttpQueryCallback.STATE_OK) {
						findViewById(R.id.progress).setVisibility(View.GONE);
						
						LoginResult ret = null;
						try {
							ret = new Gson().fromJson((String)result, LoginResult.class);
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(LoginActivity.this, getResources().getString(R.string.data_format_error), Toast.LENGTH_SHORT).show();
							return;
						}
						Log.v("rz", "login ret : " + (String)result);
						switch (ret.status) {
						case 0:
						case 2:
							Toast.makeText(LoginActivity.this, ret.msg, Toast.LENGTH_SHORT).show();
							break;
						case 1:
							onLoginSuccess(ret);
							break;
						default:
							Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
							break;
						}
					} else {
						findViewById(R.id.progress).setVisibility(View.GONE);
						Toast.makeText(LoginActivity.this, getString(R.string.query_failed), Toast.LENGTH_SHORT).show();						
					}
				}
			});
		}
	};
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data) ;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		user = (EditText)findViewById(R.id.user_name);
		password = (EditText)findViewById(R.id.password);
		source = getIntent().getStringExtra("source");
		if("relogin".equals(source)) {
			Toast.makeText(LoginActivity.this, AppConstants.need_relogin, Toast.LENGTH_SHORT).show();			
		}
		user.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString() == null || s.toString().length() == 0){
					findViewById(R.id.user_ok).setVisibility(View.GONE);
				} else {
					if(Util.checkMail(s.toString())) {
						findViewById(R.id.user_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.user_ok).setBackgroundResource(R.drawable.valid);						
					} else {
						findViewById(R.id.user_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.user_ok).setBackgroundResource(R.drawable.invalid);						
					}
				}
			}
		});
		
		password.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString() == null || s.toString().length() == 0){
					findViewById(R.id.pw_ok).setVisibility(View.GONE);
				} else {
					if(Util.checkPassword(s.toString())) {
						findViewById(R.id.pw_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.pw_ok).setBackgroundResource(R.drawable.valid);						
					} else {
						findViewById(R.id.pw_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.pw_ok).setBackgroundResource(R.drawable.invalid);						
					}
				}
			}
		});
		
		findViewById(R.id.login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userStr = user.getEditableText().toString();
				String pwStr = password.getEditableText().toString();
				if(userStr != null && pwStr != null && Util.checkPassword(pwStr) && Util.checkMail(userStr)) {
					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), MODE_PRIVATE);

					findViewById(R.id.progress).setVisibility(View.VISIBLE);
					
					Util.login(userStr, MD5.getDigest(pwStr), loginCallback);
				} else {
					Toast.makeText(LoginActivity.this, getString(R.string.login_input_error), Toast.LENGTH_SHORT).show();
				}

			}
		});

		findViewById(R.id.register).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		
		findViewById(R.id.weibo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startWeiboOAuth();
			}
		});
		
		findViewById(R.id.qq).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTencent = Tencent.createInstance(AppConstants.TENCENT_APP_ID, LoginActivity.this);
		        if (!mTencent.isSessionValid()) {
		            IUiListener listener = new BaseUiListener();
		            mTencent.login(LoginActivity.this, AppConstants.QQ_SCOPE, listener);
		        } else {
		            mTencent.logout(LoginActivity.this);
		        }
			}
		});
	}
	
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
		        		
		        		urlString = "https://api.weibo.com/2/users/show.json?access_token=" + token.access_token + "&uid=" + token.uid;
		        		result = HttpManager.syncHttpsGet(urlString);
		        		SinaInfo sina = new Gson().fromJson(result, SinaInfo.class);

		        		String url = AppConstants.service_url + "move/sina_login?platform=2&device_token=" + SuperApp.instance.deviceToken;
		        		postData = new ArrayList<NameValuePair>();
		        		postData.add(new BasicNameValuePair("id", sina.idstr));
		        		postData.add(new BasicNameValuePair("name", sina.name));
		        		postData.add(new BasicNameValuePair("profile_url", sina.profile_url));
		        		postData.add(new BasicNameValuePair("avatar_large", sina.avatar_large));
		        		postData.add(new BasicNameValuePair("profile_image_url", sina.profile_image_url));
		        		postData.add(new BasicNameValuePair("province", sina.province));
		        		postData.add(new BasicNameValuePair("city", sina.city));
		        		postData.add(new BasicNameValuePair("gender", sina.gender));
		        		result = HttpManager.syncPost(url, postData);

		        		final LoginResult loginRet = new Gson().fromJson(result, LoginResult.class);
		        		if(loginRet.status == 1) {
		        			handler.post(new Runnable() {
								@Override
								public void run() {
									onLoginSuccess(loginRet);
								}
							});
		        		} else {
		        			Util.hideProgress(LoginActivity.this, R.id.progress);
		        			Util.showToast(LoginActivity.this, loginRet.msg, Toast.LENGTH_SHORT);
		        		}
		    		} catch(Exception e) {
		    			Util.hideProgress(LoginActivity.this, R.id.progress);
		    			Util.showToast(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT);
		    		}
				}
			}).start();
    	}
	}
	
	private void startWeiboOAuth() {
		Weibo weibo = Weibo.getInstance(AppConstants.weiboApiKey, AppConstants.weiboRedirectURL, AppConstants.SINA_SCOPE);
		weibo.anthorize(this, new WeiboAuthDialogListener());
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

	private void sinaLoginError() {
		Util.showToast(this, "新浪微博认证失败 ", Toast.LENGTH_SHORT);
	}
	private void tencentLoginError() {
		Util.showToast(this, "腾讯认证失败 ", Toast.LENGTH_SHORT);
	}
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(final JSONObject response) {
        	findViewById(R.id.progress).setVisibility(View.VISIBLE);
    		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String access = null;
						String openid = null;
						access = response.getString("access_token");
						openid = response.getString("openid");
						String url = "https://graph.qq.com/user/get_user_info?access_token="
								+ access
								+ "&oauth_consumer_key="
								+ AppConstants.TENCENT_APP_ID
								+ "&openid=" + openid + "&format=json";
						String retString = HttpManager.syncHttpsGet(url);
						JsonObject retObj = new Gson().fromJson(retString, JsonObject.class);
						JsonElement element = retObj.get("ret");
						int ret = element.getAsInt();
						if(ret == 0) {
							element = retObj.get("nickname");
							String nick = element.getAsString();
							element = retObj.get("gender");
							String gender = element.getAsString();
							element = retObj.get("figureurl_qq_2");
							String figure = element.getAsString();
							
							url = AppConstants.service_url + "move/qq_login?platform=2&device_token=" + SuperApp.instance.deviceToken;
							
			        		List<NameValuePair> postData = new ArrayList<NameValuePair>();
			        		postData.add(new BasicNameValuePair("id", openid));
			        		postData.add(new BasicNameValuePair("nickname", nick));
			        		postData.add(new BasicNameValuePair("figureurl_qq_2", figure));
			        		postData.add(new BasicNameValuePair("gender", gender));
			        		String result = HttpManager.syncPost(url, postData);
		
			        		final LoginResult loginRet = new Gson().fromJson(result, LoginResult.class);
			        		if(loginRet.status == 1) {
			        			handler.post(new Runnable() {
									@Override
									public void run() {
										onLoginSuccess(loginRet);
									}
								});
			        		} else {
			        			Util.hideProgress(LoginActivity.this, R.id.progress);
			        			Util.showToast(LoginActivity.this, loginRet.msg, Toast.LENGTH_SHORT);
			        		}
						} else {
							Util.hideProgress(LoginActivity.this, R.id.progress);
							tencentLoginError();
						}
					} catch (Exception e) {
						Util.hideProgress(LoginActivity.this, R.id.progress);
						tencentLoginError();
						return;
					}
				}
    		}).start();
        }

        @Override
        public void onError(UiError e) {
        	tencentLoginError();
        }

        @Override
        public void onCancel() {
        	tencentLoginError();
        }
    }
}
