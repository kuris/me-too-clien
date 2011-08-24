package com.zinho.clien.boarditem;

import com.zinho.clien.network.NetworkBase;
import com.zinho.clien.util.ZStyle;


public class BoardItemType {
	private String w="";
	
	private String url = null;
	private String title = null;
	private String writer = null;
	private String reCnt = null;

	private String cnt = null;
	private String date = null;
	
	private String wr_id = null;
	private String bo_table = null;
	
	public BoardItemType() {
		
	}
	
	public BoardItemType(String title, String writer, String cnt, String date) {
		this.title = title;
		this.writer = writer;
		this.cnt = cnt;
		this.date = date;
	}
	
	public void setData(String str) {
		String[] strs = str.split("&");
		for (int i=0;i<strs.length;i++) {
			String[] str2 = strs[i].split("=");
			if (str2[0].equals("w")) this.w = str2[1];
			if (str2[0].equals("bo_table")) this.bo_table = str2[1];
			if (str2[0].equals("wr_id")) this.wr_id = str2[1];
		}
	}
	
	public void setBo_table(String bo_table) {
		this.bo_table = bo_table;
	}
	
	public String getBo_table() {
		return bo_table;
	}
	
	public String getReCnt() {
		return reCnt;
	}

	public void setReCnt(String reCnt) {
		this.reCnt = reCnt;
	}
	
	public String getWrId() {
		if (wr_id==null) return "";
		return wr_id;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		String[] urlItems = url.split("&");
		for (int i=0;i<urlItems.length;i++) {
			String[] urlItems2 = urlItems[i].split("=");
			if (urlItems2[0].equals("wr_id")) {
				wr_id =urlItems2[1];
				break;
			}
		}
		if (url.indexOf("..")>-1) this.url = url.replaceAll("\\.\\.","cs2");
		else this.url = url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		if (writer.indexOf("img src")>-1) {
			this.writer = writer.trim();
			this.writer = this.writer.replaceAll("<td class=\"post_name\">", "");
			this.writer = this.writer.replaceAll("<img src='../", "<img src='"+NetworkBase.CLIEN_URL+"cs2/");
			return;
		}
		
		int span1 = writer.indexOf("<span");
		if (span1>-1) {
			int span2 = writer.indexOf(">",span1+1);
			writer = writer.substring(span2+1, writer.indexOf("<", span2));
		}
		this.writer = writer;
	}
	public String getCnt() {
		return cnt;
	}
	public void setCnt(String cnt) {
		this.cnt = cnt;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		int span1 = date.indexOf("<span");
		if (span1>-1) {
			int span2 = date.indexOf(">",span1+1);
			date = date.substring(span2+1, date.indexOf("<", span2));
		}
		this.date = date;
	}
	
	public void setW(String w) {
		this.w = w;
	}
	
	public String getW() {
		return w;
	}
	
	public static String insertCss() {
		boolean isWhite = ZStyle.getThemeType(null)==ZStyle.THEME_TYPE_WHITE;
		StringBuffer str = new StringBuffer();
		str.append("<html><head><style>\n");
		str.append("body{margin: 3px; padding: 0;");
		str.append("background-color:"+(isWhite?"#dadada":"black")+";");
		str.append("color:"+(isWhite?"black":"#dadada")+";}\n");
		str.append("</style></head><body>");
		return str.toString();
	}
}
