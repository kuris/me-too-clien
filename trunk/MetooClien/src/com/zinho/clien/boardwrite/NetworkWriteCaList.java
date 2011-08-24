package com.zinho.clien.boardwrite;

import java.util.ArrayList;

public class NetworkWriteCaList {
	private String values;
	
	public NetworkWriteCaList(String values) {
		this.values = values;
	}
	
	public ArrayList<String> getCaData() {
		ArrayList<String> resultArray = new ArrayList<String>();
		int indexStart=values.indexOf("<select name=ca_name");
		if (indexStart==-1) return null;
		int indexEnd = values.indexOf("</select>",indexStart+1);
		String searchStr = "<option value=";
		while(true) {
			int x1 = values.indexOf(searchStr, indexStart);
			if (x1==-1 || x1 > indexEnd) break;
			int x2 = values.indexOf(">", x1+searchStr.length());
			if (x2==-1) break;
			String val = values.substring(x1+searchStr.length()+1,x2-1);
			if (val.length()>0) resultArray.add(val);
			indexStart = x2;
		}
		return resultArray;
	}
	
	public String getSubject() {
		String searchStr = "<input class=\"field_pub_01\" style=\"width:100%;\" name=wr_subject";
		int indexStart = values.indexOf(searchStr);
		if (indexStart>-1) {
			String indexStartStr = "value=\"";
			indexStart = values.indexOf(indexStartStr, indexStart);
			int indexEnd = values.indexOf("\"", indexStart+indexStartStr.length());
			return values.substring(indexStart+indexStartStr.length(), indexEnd);
			
		}
		return null;
	}
	
	public String getContents() {
		String searchStr = "<textarea class=\"jquery_ckeditor\" id=\"wr_content\"";
		int indexStart = values.indexOf(searchStr);
		if (indexStart>-1) {
			String indexStartStr = ">";
			indexStart = values.indexOf(indexStartStr, indexStart);
			int indexEnd = values.indexOf("</textarea>", indexStart+indexStartStr.length());
			return values.substring(indexStart+indexStartStr.length(), indexEnd);
			
		}
		return null;
	}
	
	public String getSelectedCaName() {
		String searchStr = "ca_name.value";
		int indexStart = values.indexOf(searchStr);
		if (indexStart>-1) {
			String indexStartStr = "\"";
			indexStart = values.indexOf(indexStartStr, indexStart);
			int indexEnd = values.indexOf("\"", indexStart+indexStartStr.length());
			return values.substring(indexStart+indexStartStr.length(), indexEnd);
			
		}
		return null;
	}
}
