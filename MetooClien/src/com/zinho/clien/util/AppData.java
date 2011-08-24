package com.zinho.clien.util;

import com.zinho.clien.boardlist.BoardListActivity;

public class AppData {
	public static final String LOGIN_ID = "login_id";
	public static final String LOGIN_PWD = "login_pass";
	public static final String LOGIN_AUTO_LOGIN = "login_auto_login";
	public static final String TITLE_BAR_HIDE = "title_bar_hide_setting";
	public static final String THEME_STYLE = "theme_selected_style";
	public static final String GEUSTURE_ENABLE = "gesture_enable_setting";
	public static final String MULTI_LINE_ENABLE = "multi_line_enable_setting";
	public static final String AUTO_PICTURE_ENABLE = "auto_picture_enable_setting";
	public static final String AUTO_VIDEO_ENABLE = "auto_video_enable_setting";
	public static final String FONT_SIZE_TITLE = "font_size_title";
	public static final String FONT_SIZE_LIST = "font_size_list";
	public static final String FONT_SIZE_BODY = "font_size_body";
	
	
	private static String HIDE_TITLE_BAR = null;
	private static double titleFontSize=0;
	private static double listFontSize=0;
	private static double bodyFontSize=0;
	
	public static boolean isHideTitleBar() {
		if (HIDE_TITLE_BAR==null) {
			HIDE_TITLE_BAR = Util.getSharedData(BoardListActivity.getInstance(), AppData.TITLE_BAR_HIDE, "");
			if (HIDE_TITLE_BAR==null) return false;
		}
		
		return HIDE_TITLE_BAR.equals("true");
	}
	
	public static void setHideTitleBar(boolean hide) {
		HIDE_TITLE_BAR = Boolean.toString(hide);
	}

	public static float getTitleFontSize() {
		if (titleFontSize==0) {
			try {
				titleFontSize = Double.parseDouble(Util.getSharedData(BoardListActivity.getInstance(), AppData.FONT_SIZE_TITLE, "0.9"));
			}catch (Exception e){
				titleFontSize = 0.9;
			}
		}
		if (titleFontSize>2) titleFontSize=2.0;
		return (float)titleFontSize;
	}
	
	public static void setTitleFontSize(double size) {
		if (size<0) return;
		Util.setSharedData(BoardListActivity.getInstance(), AppData.FONT_SIZE_TITLE, Double.toString(size));
		titleFontSize = size;
	}
	
	public static float getListFontSize() {
		if (listFontSize==0) {
			try {
				listFontSize = Double.parseDouble(Util.getSharedData(BoardListActivity.getInstance(), AppData.FONT_SIZE_LIST, "0.9"));
			}catch (Exception e){
				listFontSize = 0.9;
			}
		}
		if (listFontSize>2) listFontSize=2.0;
		return (float)listFontSize;
	}
	
	public static void setListFontSize(double size) {
		if (size<0) return;
		Util.setSharedData(BoardListActivity.getInstance(), AppData.FONT_SIZE_LIST, Double.toString(size));
		listFontSize = size;
	}
	
	public static float getBodyFontSize() {
		if (bodyFontSize==0) {
			try {
				bodyFontSize = Double.parseDouble(Util.getSharedData(BoardListActivity.getInstance(), AppData.FONT_SIZE_BODY, "0.9"));
			}catch (Exception e){
				bodyFontSize = 0.9;
			}
		}
		if (bodyFontSize>2) bodyFontSize=2.0;
		return (float)bodyFontSize;
	}
	
	public static void setBodyFontSize(double size) {
		if (size<0) return;
		Util.setSharedData(BoardListActivity.getInstance(), AppData.FONT_SIZE_BODY, Double.toString(size));
		bodyFontSize = size;
	}
}
