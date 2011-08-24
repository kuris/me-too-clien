package com.zinho.clien.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zinho.clien.R;
import com.zinho.clien.network.NetworkBase;
import com.zinho.clien.setting.LoginActivity;

public class TitleActivity extends Activity {
	private TextView title;
	private Button btnLeft;
	private Button loginButton;
	private Button searchButton;
	private Button etcButton;
	private TitleActivity activity;
	
	private boolean isHideTitlebar;
	private GestureDetector gesture;
	private boolean isGesture = false;

	public void onCreate(Bundle savedInstanceState) {
		activity = this; // 항상 위에.
		isGesture = false;
		isHideTitlebar = AppData.isHideTitleBar();
		if (ZStyle.getThemeType(this)==ZStyle.THEME_TYPE_WHITE) {
			this.setTheme(R.style.WindowTitleBar_White);
		} else {
			this.setTheme(R.style.WindowTitleBar_Black);
		}
		
		super.onCreate(savedInstanceState);
		Util.addActivity(this);
		
		if (isHideTitlebar) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		} else {
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.titlebar);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

			title = (TextView) findViewById(R.id.title_textView1);
			btnLeft = (Button) findViewById(R.id.title_button1);
			
			loginButton = (Button) findViewById(R.id.title_login_button);
			loginButton.setVisibility(View.GONE);
			searchButton = (Button) findViewById(R.id.title_search_button);
			searchButton.setVisibility(View.GONE);
			etcButton = (Button) findViewById(R.id.title_etc_button);
			etcButton.setVisibility(View.GONE);
		}
	}
	
	public void hideTitleBar(boolean hide) {
		AppData.setHideTitleBar(hide);
		this.finish();
		Util.startActivity(this,this.getClass(), this.getIntent());
	}
	
	public void onResume() {
		if (isHideTitlebar != AppData.isHideTitleBar()) {
			hideTitleBar(AppData.isHideTitleBar());
		}
		super.onResume();
		initScreen();
	}
	
	public void initScreen() {
		if (NetworkBase.isLogin()) {
			if (loginButton!=null) loginButton.setVisibility(View.GONE);
		}
		else {
			if (loginButton!=null) {
				loginButton.setVisibility(View.VISIBLE);
				loginButton.setText("Login");
			}
		}

		String gestureData = Util.getSharedData(this, AppData.GEUSTURE_ENABLE, "0");
		if (gestureData.equals("1")) createGesture();
		else {
			gesture = null;
			isGesture = false;
		}
		
		//Font Size
		if (title!=null) title.setTextSize(AppData.getTitleFontSize()*20);
	}
	
	public void setTitle(String _title, String _leftButtonName, int _leftButtonType) {
		if (isHideTitlebar) return;
		title.setText(_title);

		if (_leftButtonName.equals("")) {
			btnLeft.setVisibility(View.GONE);
		} else {
			btnLeft.setVisibility(View.VISIBLE);
			btnLeft.setText(_leftButtonName);
			btnLeft.setBackgroundResource(_leftButtonType);
		}
	}
	
	public void setTitle(String _title, String _leftButtonName) {
		setTitle(_title, _leftButtonName, R.drawable.btn_title_normal);
	}
	
	public Button getLoginButton() {
		if (isHideTitlebar) return null;
		loginButton.setVisibility(View.VISIBLE);
		if (NetworkBase.isLogin()) loginButton.setText("Logout");
		else loginButton.setText("로긴");
		return loginButton;
	}
	
	public Button getEtcButton() {
		if (isHideTitlebar) return null;
		if (NetworkBase.isLogin()) {
			etcButton.setVisibility(View.VISIBLE);
		} else {
			etcButton.setVisibility(View.GONE);
		}
		
		return etcButton;
	}
	
	public Button getSearchButton() {
		if (isHideTitlebar) return null;
		searchButton.setVisibility(View.VISIBLE);
		return searchButton;
	}
	
	public void showLoading(boolean isShow) {
		if (isHideTitlebar) return;
		View v = findViewById(R.id.title_progressbar);
		if (v != null) {
			if (isShow) v.setVisibility(View.VISIBLE);
			else v.setVisibility(View.GONE);
		}
	}
	
	public void onClickLoginButton(View v) {
		if (NetworkBase.isLogin()) {
			NetworkBase.Logout();
			Toast.makeText(activity, "로그아웃 되었습니다.", 1000).show();
			onResume();
		} else {
			final String id = Util.getSharedData(this, AppData.LOGIN_ID, "");
			final String pwd = Util.getSharedData(this, AppData.LOGIN_PWD, "");
			if (!id.equals("")) {
				new AsyncTask<String, Integer, Boolean>() {
					protected Boolean doInBackground(String... arg0) {
						return NetworkBase.getLogin(id, pwd);
					}
					protected void onPostExecute(Boolean temp) {
						if (temp) {
							Toast.makeText(activity, "로그인 되었습니다.", 1000).show();
							onResume();
						} else {
							Toast.makeText(activity, "로그인에 실패했습니다.", 1000).show();
							Util.startActivity(activity, LoginActivity.class);
							onResume();
						}
					}
				}.execute();
			} else {
				Util.startActivity(this, LoginActivity.class);
				onResume();
			}
		}
	}
	
	public void onClickSearchButton(View v) {
		
	}
	
	public boolean gestureSuccess() {
		return isGesture;
	}
	
	private void createGesture() {
		gesture = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
		    private static final int SWIPE_MIN_DISTANCE_X = 120;
		    private static final int SWIPE_MAX_DISTANCE_Y = 140;
		    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
			
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				try {
					if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE_X &&
					        Math.abs(e2.getY() - e1.getY()) < SWIPE_MAX_DISTANCE_Y &&
					        Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						isGesture=true;
						finish();
					}
				}catch (Exception e) {}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean ret = super.dispatchTouchEvent(ev);
		if (gesture != null) {
			return gesture.onTouchEvent(ev);
		}
		else {
			return ret;
		}
	}

	public void onClickButton_BackPressed(View target) {
		onBackPressed();
	}

	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void finish() {
		Util.removeActivity(this);
		super.finish();
	}
}