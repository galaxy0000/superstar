package com.galaxy.superstar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.galaxy.superstar.HttpManager.HttpQueryCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.os.Bundle;
import android.os.Handler;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyDetailActivity extends CommonTitleBarActivity{

	private Handler handler = new Handler();
	private EditText nick;
	private EditText email;
	private Button workMode;
	private Button roleType;
	private Button location;
	private EditText preAchieve;

	private String[] workModeOption;
	private String[] roleTypeOption;
	
	private CityInfo[] cityInfos;
	
	private Item[] items;
	private class Item {
		String label;
		String name;
		String type;
		String[] option; 
		String value;
		String is_must;
		String is_pattern;
		String is_unique;
		String unique_ajax_url;
	}
	private class GetResult {
		public int status;
		public String msg;
		public Item[] data;
	}
	private class saveResult {
		public int status;
		public String msg;
	}
	
	private HttpQueryCallback getDetailCallback = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object queryId, final Object result) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					findViewById(R.id.progress).setVisibility(View.GONE);
					if(state == HttpQueryCallback.STATE_OK) {
						JsonObject obj = null;

						GetResult getRet = null;
						try {
							String resultStr = (String)result;
							resultStr = resultStr.replace("\"option\":\"\",", "");
							getRet = new Gson().fromJson(resultStr, GetResult.class);
						} catch (Exception e) {
							Toast.makeText(MyDetailActivity.this, getResources().getString(R.string.data_format_error), Toast.LENGTH_SHORT).show();
							return;
						}
						switch (getRet.status) {
						case 101:
							finish();
							Util.jump2Login(MyDetailActivity.this);
							break;
						case 1:
							items = getRet.data;
							break;
						default:
							Toast.makeText(MyDetailActivity.this, getRet.msg, Toast.LENGTH_LONG).show();
							break;
						}
						parseItems();
					} else {
						Toast.makeText(MyDetailActivity.this, getString(R.string.query_failed), Toast.LENGTH_LONG).show();						
					}
				}
			}, 350);
			
		}
	};
	private HttpQueryCallback saveDetailCallback = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object queryId, final Object result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(state == HttpQueryCallback.STATE_OK) {
						findViewById(R.id.progress).setVisibility(View.GONE);
						saveResult ret = null;
						try {
							ret = new Gson().fromJson((String)result, saveResult.class);
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(MyDetailActivity.this, getResources().getString(R.string.data_format_error), Toast.LENGTH_SHORT).show();
							return;
						}
						switch (ret.status) {
						case 101:
							Util.jump2Login(MyDetailActivity.this);
							break;
						case 1:
							Toast.makeText(MyDetailActivity.this, getString(R.string.save_info_success), Toast.LENGTH_SHORT).show();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									finish();
									Intent i = new Intent(MyDetailActivity.this, MainActivity.class);
									startActivity(i);
									overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
								}
							}, 1200);
							break;
						default:
							Toast.makeText(MyDetailActivity.this, getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
							break;
						}
					} else {
						findViewById(R.id.progress).setVisibility(View.GONE);
						Toast.makeText(MyDetailActivity.this, getString(R.string.query_failed), Toast.LENGTH_LONG).show();
					}
				}
			});
			
		}
	};
	private void parseItems() {
		if(items == null) {
			return;
		}
		
		String stateId = "";
		String cityId = "";
		for(Item item : items) {
			String name = item.name.substring(5, item.name.length() - 1);
			if(name != null) {
				if(name.equals("name")) {
					nick.setText(item.value);
				} else if(name.equals("email")) {
					email.setText(item.value);
				} else if(name.equals("work_mode")) {
					workModeOption = item.option;
					int i = Integer.parseInt(item.value);
					workMode.setText(workModeOption[i]);
				} else if(name.equals("role_type")) {
					roleTypeOption = item.option;
					int i = Integer.parseInt(item.value);
					roleType.setText(roleTypeOption[i]);
				} else if(name.equals("state_id")) {
					stateId = item.value;
				} else if(name.equals("city_id")) {
					cityId = item.value;
				} else if(name.equals("pre_achieve")) {
					preAchieve.setText(item.value);
				}
			}
		}
		
		String ret = findStateCityById(stateId, cityId);
		if(!"".equals(ret)) {
			location.setText(ret);
		} else {
			ret = findStateById(stateId);
			if(!"".equals(ret)) {
				location.setText(ret);
			}
		}
		
		workMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MyDetailActivity.this)
					.setTitle("选择心态")
					.setItems(workModeOption, new DialogInterface.OnClickListener() { // content
						@Override
						public void onClick(DialogInterface dialog, int which) {
							for(Item item : items) {
								if(item.name.equals("info[work_mode]")) {
									item.value = ((Integer)which).toString();
									break;
								}
							}
							workMode.setText(workModeOption[which]);
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.show();
			}
		});

		roleType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MyDetailActivity.this)
					.setTitle("选择定位")
					.setItems(roleTypeOption, new DialogInterface.OnClickListener() { // content
						@Override
						public void onClick(DialogInterface dialog, int which) {
							for(Item item : items) {
								if(item.name.equals("info[role_type]")) {
									item.value = ((Integer)which).toString();
									break;
								}
							}
							roleType.setText(roleTypeOption[which]);
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.show();
			}

		});
	}
	private void queryMyInfo() {
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		Util.queryMyInfo(getDetailCallback);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_detail);

		this.setCustomTitle("详细资料");
		
		cityInfos = new Gson().fromJson(Util.loadStringFromAsset(this, "city.json.txt"), CityInfo[].class);
		
		nick = (EditText)findViewById(R.id.nick);
		email = (EditText)findViewById(R.id.email);
		workMode = (Button)findViewById(R.id.chooseWorkMode);
		roleType = (Button)findViewById(R.id.chooseRoleType);
		location = (Button)findViewById(R.id.chooseLocation);
		preAchieve = (EditText)findViewById(R.id.preAchieve);
		
		String src = getIntent().getStringExtra(AppConstants.ikey_src);
		if(src != null && (src.equals(SettingActivity.class.getSimpleName()) || src.equals(SessionMessageAdapter.class.getSimpleName()))) {			
			setLeftButton(getResources().getDrawable(R.drawable.back), this.onBackListeger);
		}
		queryMyInfo();
		
		nick.addTextChangedListener(new TextWatcher() {
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
					findViewById(R.id.nick_ok).setVisibility(View.GONE);
				} else {
					findViewById(R.id.nick_ok).setVisibility(View.VISIBLE);
					findViewById(R.id.nick_ok).setBackgroundResource(R.drawable.valid);						
				}
				for(Item item : items) {
					if(item.name.equals("info[name]")) {
						item.value = s.toString();
						break;
					}
				}
			}
		});
		
		email.addTextChangedListener(new TextWatcher() {
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
					findViewById(R.id.email_ok).setVisibility(View.GONE);
				} else {
					if(Util.checkMail(s.toString())) {
						findViewById(R.id.email_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.email_ok).setBackgroundResource(R.drawable.valid);						
					} else {
						findViewById(R.id.email_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.email_ok).setBackgroundResource(R.drawable.invalid);						
					}
				}
				for(Item item : items) {
					if(item.name.equals("info[email]")) {
						item.value = s.toString();
						break;
					}
				}
			}
		});

		preAchieve.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString() == null || s.toString().length() == 0){
					findViewById(R.id.preAchieve_ok).setVisibility(View.GONE);
				} else {
					if(s.toString().length() >= 6) {
						findViewById(R.id.preAchieve_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.preAchieve_ok).setBackgroundResource(R.drawable.valid);						
					} else {
						findViewById(R.id.preAchieve_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.preAchieve_ok).setBackgroundResource(R.drawable.invalid);						
					}
				}
				for(Item item : items) {
					if(item.name.equals("info[pre_achieve]")) {
						item.value = s.toString();
						break;
					}
				}
			}
		});
		
		location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String[] stateArray = getStateList();
				new AlertDialog.Builder(MyDetailActivity.this)
					.setTitle("选择位置")
					.setItems(stateArray, new DialogInterface.OnClickListener() { // content
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final String stateStr = stateArray[which];
							final String[] cityArray = getCityList(stateStr);
////////////////////////////////////////////////////////
							new AlertDialog.Builder(MyDetailActivity.this)
							.setTitle("选择位置")
							.setItems(cityArray, new DialogInterface.OnClickListener() { // content
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String cityStr = cityArray[which];
									location.setText(stateStr + " " + cityStr);
									
									String stateIdStr = "";
									String cityIdStr = "";
									for(CityInfo info : cityInfos) {
										if(info.state_name.equals(stateStr) && info.name.equals(cityStr)) {
											stateIdStr = ((Integer)info.state_code).toString();
											cityIdStr = ((Integer)info.city_code).toString();
											break;
										}
									}
									
									for (Item item : items) {
										if (item.name.equals("info[state_id]")) {
											item.value = stateIdStr;
										}
										if (item.name.equals("info[city_id]")) {
											item.value = cityIdStr;
										}
									}
								}
							})
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).show();				
//////////////////////////////////////////////////////
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();				
			}
		});
		
		findViewById(R.id.done).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkParams()) {
					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), MODE_PRIVATE);

					findViewById(R.id.progress).setVisibility(View.VISIBLE);

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					for(Item item : items) {
						params.add(new BasicNameValuePair(item.name, item.value));
					}
					Util.saveInfo(params, saveDetailCallback);
				} else {
					Toast.makeText(MyDetailActivity.this, getString(R.string.detail_input_error), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private boolean checkParams() {
		String str = email.getEditableText().toString();
		if(str == null || !Util.checkMail(str)) {
			return false;
		}
		str = nick.getEditableText().toString();
		if(str == null) {
			return false;
		}
		str = preAchieve.getEditableText().toString();
		if(str == null || str.length() < 6) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			String src = getIntent().getStringExtra(AppConstants.ikey_src);
			if(src != null && (src.equals(SettingActivity.class.getSimpleName()) || src.equals(SessionMessageAdapter.class.getSimpleName()))) {							
				leftView.performClick();
				return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private String findStateById(String stateId) {
		try {
			Integer id = Integer.parseInt(stateId);
			for(CityInfo info : cityInfos) {
				if(info.state_code == id) {
					return info.state_name;
				}
			}
		} catch (Exception e) {
		}
		
		return "";
	}
	private String findStateCityById(String stateId, String cityId) {
		try {
			Integer state = Integer.parseInt(stateId);
			Integer city = Integer.parseInt(cityId);
			for(CityInfo info : cityInfos) {
				if(info.state_code == state && info.city_code == city) {
					return info.state_name + " " + info.name;
				}
			}
		} catch (Exception e) {
		}
		
		return "";
	}
	
	private String[] getStateList() {
		List<String> states = new ArrayList<String>();
		int lastState = 0;
		for(CityInfo info : cityInfos) {
			if(info.state_code != lastState) {
				states.add(info.state_name);
				lastState = info.state_code;
			}
		}
		
		String[] statesArray = new String[states.size()];
		return states.toArray(statesArray);
	}
	
	private String[] getCityList(String state) {
		List<String> citys = new ArrayList<String>();
		for(CityInfo info : cityInfos) {
			if(info.state_name.equals(state)) {
				citys.add(info.name);
			}
		}
		
		String[] citysArray = new String[citys.size()];
		return citys.toArray(citysArray);
	}
}
