package com.zinho.clien.boardlist;

public class BoardListType {
	private String url = null;
	private String name = null;
	private String key = null;
	
	public BoardListType(String url, String name) {
		this.url = url;
		this.name = name;
		this.key = url.substring(url.indexOf("=")+1);
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public boolean isExtraBoard() {
		return url.indexOf("cm_")>-1;
	}
	
	public boolean isEmpty() {
		return url==null || url.length()<1;
	}
	
	public String toString() {
		return "["+url+"]"+name;
	}
}
