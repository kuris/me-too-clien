package com.zinho.clien.util;

import android.app.Activity;
import android.graphics.Color;

public class ZStyle {
	private static int SELECTED_THEME_TYPE = 0;
	
	public static final int THEME_TYPE_WHITE = 0;
	public static final int THEME_TYPE_BLACK = 1;
	
	// 기본 흰색 색상표 리스트
	public static int ListBackgroundColor=Color.WHITE;
	public static int ListBackgroundSelectedColor=Color.parseColor("#AAAAAA");
	public static int TextColor = Color.BLACK;
	
	public static int getThemeType(Activity activity) {		
		String type = "0";
		try {
			type = Util.getSharedData(activity, AppData.THEME_STYLE, "0");
			SELECTED_THEME_TYPE = Integer.parseInt(type);
		}catch (Exception e) {}
		
		init();
		return SELECTED_THEME_TYPE;
	}
	
	public static void init() {
		ZStyle.ListBackgroundColor=Color.parseColor(getListBackgroundColor());
		ZStyle.ListBackgroundSelectedColor=Color.parseColor(getListBackfroundSelectedColor());
		ZStyle.TextColor=Color.parseColor(getTextColor());
	}
	
	public static String getListBackfroundSelectedColor() {
		if (SELECTED_THEME_TYPE==THEME_TYPE_BLACK) {
			return "#333333";
		} else {
			return "#AAAAAA";
		}
	}
	
	public static String getListBackgroundColor() {
		if (SELECTED_THEME_TYPE==THEME_TYPE_BLACK) {
			return "#000000";
		} else {
			return "#FFFFFF";
		}
	}
	
	public static String getTextColor() {
		if (SELECTED_THEME_TYPE==THEME_TYPE_BLACK) {
			return "#FFFFFF";
		} else {
			return "#000000";
		}
	}
}
