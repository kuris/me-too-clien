package com.zinho.clien.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.zinho.clien.R;
import com.zinho.clien.util.AppData;
import com.zinho.clien.util.TitleActivity;
import com.zinho.clien.util.Util;

public class SettingActivity extends TitleActivity {
	private String tempThemeData;
	
	public SettingActivity() {
		super();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_main);
		setTitle("", "이전");
//		initScreen();
	}
	
	public void initScreen() {
		String id = Util.getSharedData(this, AppData.LOGIN_ID, "정보없음");
		if (id==null || id.equals("")) id = "정보없음";
		((TextView)findViewById(R.id.setting_main_logininfo_textview)).setText(id);
		
		final SettingActivity thisClass = this;
		
		// 타이틀바 숨김
		String titlebar = Util.getSharedData(this, AppData.TITLE_BAR_HIDE, "false");
		CheckBox titlebarCheckbox = ((CheckBox)findViewById(R.id.setting_main_titlebar_checkbox));
		titlebarCheckbox.setChecked(titlebar.equals("true"));
		titlebarCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				Util.setSharedData(thisClass, AppData.TITLE_BAR_HIDE, Boolean.toString(arg1));
			}
		});
		
		// 버전 정보 설정
		try {
		   PackageInfo i = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
		   String version = i.versionName;
		   ((TextView)findViewById(R.id.setting_main_version_textview)).setText("v "+version);
		} catch(NameNotFoundException e) {
			
		}
		
		// 테마 설정
		if (tempThemeData==null) { 
			tempThemeData = Util.getSharedData(this, AppData.THEME_STYLE, "0");
			CheckBox isBlackThemeCheckbox = ((CheckBox)findViewById(R.id.setting_main_theme_checkbox));
			isBlackThemeCheckbox.setChecked(tempThemeData.equals("1"));
			isBlackThemeCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					String value = arg1?"1":"0";
					Util.setSharedData(thisClass, AppData.THEME_STYLE, value);
				}
			});
		}
		
		// 제스쳐 설정
		String gestureData = Util.getSharedData(this, AppData.GEUSTURE_ENABLE, "0");
		CheckBox isGestureCheckbox = ((CheckBox)findViewById(R.id.setting_main_gesture_checkbox));
		isGestureCheckbox.setChecked(gestureData.equals("1"));
		isGestureCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				String value = arg1?"1":"0";
				Util.setSharedData(thisClass, AppData.GEUSTURE_ENABLE, value);
			}
		});
		
		// MultiLine 설정
		String multiLineData = Util.getSharedData(this, AppData.MULTI_LINE_ENABLE, "1");
		CheckBox isMultiLineCheckbox = ((CheckBox)findViewById(R.id.setting_main_2line_checkbox));
		isMultiLineCheckbox.setChecked(multiLineData.equals("1"));
		isMultiLineCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				String value = arg1?"1":"0";
				Util.setSharedData(thisClass, AppData.MULTI_LINE_ENABLE, value);
			}
		});
		
		// 자동 이미지 보기 설정
		String autoPictureData = Util.getSharedData(this, AppData.AUTO_PICTURE_ENABLE, "0");
		CheckBox autoPictureCheckbox = ((CheckBox)findViewById(R.id.setting_main_contents_imageview_checkbox));
		autoPictureCheckbox.setChecked(autoPictureData.equals("1"));
		autoPictureCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				String value = arg1?"1":"0";
				Util.setSharedData(thisClass, AppData.AUTO_PICTURE_ENABLE, value);
			}
		});
		
		// 자동 동영상 보기 설정
		String autoVideoData = Util.getSharedData(this, AppData.AUTO_VIDEO_ENABLE, "0");
		CheckBox autoVideoCheckbox = ((CheckBox)findViewById(R.id.setting_main_contents_flashview_checkbox));
		autoVideoCheckbox.setChecked(autoVideoData.equals("1"));
		autoVideoCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				String value = arg1?"1":"0";
				Util.setSharedData(thisClass, AppData.AUTO_VIDEO_ENABLE, value);
			}
		});
	}
	
	public void onResume() {
		super.onResume();
		initScreen();
	}
	
	/**
     * Title 왼쪽 버튼
     * @param v
     */
    public void onLeftTitleClickButton(View v) {
    	finish();
    }
	
	public void onClickButtonLoginSetting(View v) {
		Util.startActivity(this, LoginActivity.class);
	}
	
	public void onClickButtonFontSetting(View v) {
		Util.startActivity(this, FontSettingActivity.class);
	}
	
	private boolean askme = true;
	
	public void finish() {
		if (askme == false) super.finish();
		CheckBox titlebarCheckbox = ((CheckBox)findViewById(R.id.setting_main_titlebar_checkbox));
		AppData.setHideTitleBar(titlebarCheckbox.isChecked());

		if (!tempThemeData.equals(Util.getSharedData(this, AppData.THEME_STYLE, "0"))) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("알림");
			alert.setMessage("테마를 변경하기 위해 종료합니다. 다시 시작하십시요.");
			alert.setPositiveButton( "닫기", new DialogInterface.OnClickListener() {
			    public void onClick( DialogInterface dialog, int which) {
			    	askme = false;
			        Util.ApplicationExit();
			    }
			});
			alert.show();
		} else {
			super.finish();	
		}

	}
}
