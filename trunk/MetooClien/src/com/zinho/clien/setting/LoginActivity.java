package com.zinho.clien.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.zinho.clien.R;
import com.zinho.clien.network.NetworkBase;
import com.zinho.clien.util.AppData;
import com.zinho.clien.util.TitleActivity;
import com.zinho.clien.util.UserInfo;
import com.zinho.clien.util.Util;

public class LoginActivity extends TitleActivity {
	
	private static UserInfo loginUser = null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        setTitle("로그인","이전");
        findViewById(R.id.login_main_fail_textview).setVisibility(View.INVISIBLE);
        
        Button logoutButton = (Button)findViewById(R.id.login_main_logout_button);
        if (NetworkBase.isLogin()) {
        	String id = Util.getSharedData(this, AppData.LOGIN_ID, "");
        	String pass = Util.getSharedData(this, AppData.LOGIN_PWD, "");
        	((EditText)findViewById(R.id.login_main_id_edittext)).setText(id);
        	((EditText)findViewById(R.id.login_main_passwod_edittext)).setText(pass);
        } else {
        	String id = Util.getSharedData(this, AppData.LOGIN_ID, "");
        	String pass = Util.getSharedData(this, AppData.LOGIN_PWD, "");
        	((EditText)findViewById(R.id.login_main_id_edittext)).setText(id);
        	((EditText)findViewById(R.id.login_main_passwod_edittext)).setText(pass);
        	logoutButton.setText("로그인");
        }
        
        String autoLogin = Util.getSharedData(this, AppData.LOGIN_AUTO_LOGIN, "false");
        if (autoLogin.equals("true")) {
        	CheckBox check = (CheckBox)findViewById(R.id.login_main_autologin_checkbox);
        	check.setChecked(true);
        }
        
    }
	
	public void initScreen() {
		
	}
	
	/**
     * Title 왼쪽 버튼
     * @param v
     */
    public void onLeftTitleClickButton(View v) {
    	finish();
    }
    
    /**
     * 타이틀 오른쪽 버튼 클릭
     * @param v
     */
    public void onClickButtonLogin(View v) {
    	if (NetworkBase.isLogin()) {
    		NetworkBase.Logout();
    		finish();
    		return;
    	}
    	EditText idEditText = (EditText)findViewById(R.id.login_main_id_edittext);
    	EditText pwEditText = (EditText)findViewById(R.id.login_main_passwod_edittext);
    	
    	String id = idEditText.getText().toString();
    	String pass = pwEditText.getText().toString();
    	
    	if (id.length()<1 || pass.length()<1) {
    		Toast.makeText(this, "입력값이 없습니다.", 1000).show();
    	}
    	
    	boolean isSuccess = NetworkBase.getLogin(id, pass);
    	if (isSuccess) {
    		LoginActivity.loginUser = new UserInfo();
    		LoginActivity.loginUser.setId(id);
    		finish();
    		return;
    	} else {
    		findViewById(R.id.login_main_fail_textview).setVisibility(View.VISIBLE);
    	}
    }
    
    public void onClickAutoLogin(View v) {
    }
    
    public static UserInfo getLoginUser() {
    	if (LoginActivity.loginUser==null) return new UserInfo();
    	return LoginActivity.loginUser;
    }
    
    public void finish() {
    	CheckBox check = (CheckBox)findViewById(R.id.login_main_autologin_checkbox);
    	EditText idEditText = (EditText)findViewById(R.id.login_main_id_edittext);
    	EditText pwEditText = (EditText)findViewById(R.id.login_main_passwod_edittext);
    	
    	String id = idEditText.getText().toString();
    	String pass = pwEditText.getText().toString();
    	
		if (check.isChecked()) {
			Util.setSharedData(this, AppData.LOGIN_AUTO_LOGIN, "true");
    		Util.setSharedData(this, AppData.LOGIN_ID, id);
    		Util.setSharedData(this, AppData.LOGIN_PWD, pass);
		} else {
			Util.setSharedData(this, AppData.LOGIN_AUTO_LOGIN, "false");
			Util.setSharedData(this, AppData.LOGIN_ID, "");
			Util.setSharedData(this, AppData.LOGIN_PWD, "");
		}
		super.finish();
    }
}
