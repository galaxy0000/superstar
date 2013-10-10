package com.galaxy.superstar;

import java.util.ArrayList;
import java.util.List;

import com.galaxy.superstar.HttpManager.HttpQueryCallback;
import com.google.gson.Gson;

import android.os.Bundle;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends CommonTitleBarActivity{

	private Handler handler = new Handler();
	private EditText user;
	private EditText nick;
	private EditText pw1;
	private EditText pw2;
	private Spinner stateCode;
	private Spinner cityCode;

	private CityInfo[] cityInfos;
	private List<String> states;
	private ArrayAdapter<String> cityAdapter;
	
	private class RegisterResult {
		public int status;
		public String user_id;
		public String login_str;
	}
	private HttpQueryCallback registerCallback = new HttpQueryCallback() {
		@Override
		public void onQueryComplete(final int state, final Object queryId, final Object result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(state == HttpQueryCallback.STATE_OK) {
						findViewById(R.id.progress).setVisibility(View.GONE);
						RegisterResult ret = null;
						try {
							ret = new Gson().fromJson((String)result, RegisterResult.class);
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(RegisterActivity.this, getResources().getString(R.string.data_format_error), Toast.LENGTH_SHORT).show();
							return;
						}
						switch (ret.status) {
						case 1:
							((SuperApp)getApplication()).saveLoginState(ret.user_id, ret.login_str);
							
							Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									finish();
									Intent i = new Intent(RegisterActivity.this, MyDetailActivity.class);
//									i.putExtra(AppConstants.ikey_src, RegisterActivity.class.getSimpleName());
//									i.putExtra("nick", nick.getText().toString());
//									i.putExtra("email", user.getText().toString());
//									i.putExtra("location_state", stateCode.getSelectedItemPosition());
//									i.putExtra("location_city", cityCode.getSelectedItemPosition());
									startActivity(i);
									overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
								}
							}, 1500);
							break;
						case 3:
							Toast.makeText(RegisterActivity.this, getString(R.string.user_exist), Toast.LENGTH_SHORT).show();
							break;
						default:
							Toast.makeText(RegisterActivity.this, getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
							break;
						}
					} else {
						findViewById(R.id.progress).setVisibility(View.GONE);
						Toast.makeText(RegisterActivity.this, getString(R.string.query_failed), Toast.LENGTH_LONG).show();						
					}
				}
			});
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		setLeftButton(getResources().getDrawable(R.drawable.back), new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
				startActivity(i);
				finish();
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);				
			}
		});
		
		cityInfos = new Gson().fromJson(Util.loadStringFromAsset(this, "city.json.txt"), CityInfo[].class);
		states = getStateList();
		
		user = (EditText)findViewById(R.id.user_name);
		nick = (EditText)findViewById(R.id.nick);
		pw1 = (EditText)findViewById(R.id.password);
		pw2 = (EditText)findViewById(R.id.confirm);
		
		stateCode = (Spinner)findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.city_spinner_item);
		for(String str : states) {
			adapter.add(str);
		}
		stateCode.setAdapter(adapter);
		stateCode.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				List<String> citys = getCityList(states.get(position));
				cityAdapter.clear();
				for(String str : citys) {
					cityAdapter.add(str);
				}
				cityAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});

		cityAdapter = new ArrayAdapter<String>(this, R.layout.city_spinner_item);
		List<String> firstCitys = getCityList(cityInfos[0].state_name);
		cityCode = (Spinner)findViewById(R.id.spinner2);
		for(String str : firstCitys) {
			cityAdapter.add(str);
		}
		cityCode.setAdapter(cityAdapter);
		
		user.addTextChangedListener(new TextWatcher() {
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
		nick.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString() == null || s.toString().length() == 0){
					findViewById(R.id.nick_ok).setVisibility(View.GONE);
				} else {
					findViewById(R.id.nick_ok).setVisibility(View.VISIBLE);
					findViewById(R.id.nick_ok).setBackgroundResource(R.drawable.valid);						
				}
			}
		});
		pw1.addTextChangedListener(new TextWatcher() {
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
		pw2.addTextChangedListener(new TextWatcher() {
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
					findViewById(R.id.pw2_ok).setVisibility(View.GONE);
				} else {
					if(Util.checkPassword(s.toString()) && s.toString().equals(pw1.getEditableText().toString())) {
						findViewById(R.id.pw2_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.pw2_ok).setBackgroundResource(R.drawable.valid);						
					} else {
						findViewById(R.id.pw2_ok).setVisibility(View.VISIBLE);
						findViewById(R.id.pw2_ok).setBackgroundResource(R.drawable.invalid);						
					}
				}
			}
		});
		findViewById(R.id.register).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userStr = user.getEditableText().toString();
				String nickStr = nick.getEditableText().toString();
				String pwStr1 = pw1.getEditableText().toString();
				String pwStr2 = pw2.getEditableText().toString();
				String stateStr = (String)stateCode.getSelectedItem();
				String cityStr = (String)cityCode.getSelectedItem();
				String stateId = "11";
				String cityId = "1";
				for(CityInfo info : cityInfos) {
					if(info.state_name.equals(stateStr) && info.name.equals(cityStr)) {
						stateId = "" + info.state_code;
						cityId = "" + info.city_code;
						break;
					}
				}
				if(userStr != null && Util.checkMail(userStr)
						&& pwStr1 != null && Util.checkPassword(pwStr1)
						&& pwStr2 != null && Util.checkPassword(pwStr2) && pwStr1.equals(pwStr2)
						&& nickStr != null && nickStr.length() > 0) {
					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), MODE_PRIVATE);

					findViewById(R.id.progress).setVisibility(View.VISIBLE);

					Util.register(userStr, MD5.getDigest(pwStr1), nickStr, stateId, cityId, registerCallback);
				} else {
					Toast.makeText(RegisterActivity.this, getString(R.string.reg_input_error), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			leftView.performClick();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private List<String> getStateList() {
		List<String> states = new ArrayList<String>();
		int lastState = 0;
		for(CityInfo info : cityInfos) {
			if(info.state_code != lastState) {
				states.add(info.state_name);
				lastState = info.state_code;
			}
		}
		
		return states;
	}
	
	private List<String> getCityList(String state) {
		List<String> citys = new ArrayList<String>();
		for(CityInfo info : cityInfos) {
			if(info.state_name.equals(state)) {
				citys.add(info.name);
			}
		}
		
		return citys;
	}
}
