package com.zinho.clien.network;

import java.net.URLEncoder;
import java.util.Vector;

import android.util.Log;

import com.zinho.clien.boarditem.BoardItemType;
import com.zinho.clien.boardlist.BoardListType;

public class NetworkBoardItemList {
	private static String nextUrl = null;
	private Vector<BoardItemType> boardVc = new Vector<BoardItemType>();
	
	private void getData(BoardListType item, int page, String searchMsg, String searchType) throws Exception {
		boardVc.clear();
		String url = NetworkBase.CLIEN_URL+item.getUrl()+"&page="+page;
		if (searchMsg!=null && searchMsg.length()>0) {
			if (page==1 || nextUrl==null || nextUrl.length()<1) {
				url = NetworkBase.CLIEN_URL+item.getUrl()+"&sfl="+URLEncoder.encode(searchType)+"&stx="+URLEncoder.encode(searchMsg);
			} else {
				url = NetworkBase.CLIEN_URL+"cs2/bbs/"+nextUrl;
				url = url.replaceAll("/./", "/");
			}
		}
		
		String result = null;
		Log.d("debug", url);
		result = NetworkBase.getData(url);
		String lines[] = result.split("\n");
		boolean capture = false;
		int i=0;
        w:for(;i<lines.length;i++) {
        	String line = lines[i];
        	// 기준점
        	if (line.indexOf("<form name=\"fboardlist\"")>-1) {;
            	capture= true;
            	continue w;
        	}
        	
        	if (capture && line.indexOf("</form>")>-1) {
        		capture=false;
        		break w;
        	}
        	
            if (capture) {
    			int hrefCnt = line.indexOf("post_subject");
    	    	
    	    	if (hrefCnt>-1) {
    	    		BoardItemType titleItem = new BoardItemType();
    	    		titleItem.setBo_table(item.getKey());
    	    		int line1 = line.indexOf("<a href='", hrefCnt);
    	    		int line2 = line.indexOf(">", line1+1);
    	    		titleItem.setUrl(line.substring(line1+9,line2-2)); //링크
    	    		
    	    		int line4 = line.indexOf("</a>",line2+1);
    	    		if (line4==-1) continue w;	//삭제된 글
    	    		titleItem.setTitle(line.substring(line2+1,line4));	//제목
    	    		if (titleItem.getTitle().indexOf("-차단하신 게시물 입니다")!=-1) continue;
    	    		
    	    		int line5 = line.lastIndexOf("<span>");
    	    		int line6 = line.lastIndexOf("</span>");
    	    		
    	    		if (line5>-1) {
    	    			titleItem.setReCnt(line.substring(line5+6,line6));	// 댓글수.
    	    		}
    	    		
    	    		line = lines[++i];
    	    		
    	    		int line7 = line.indexOf("<td>")+4;
    	    		int line8 = line.indexOf("</td>", line7);
    	    		titleItem.setWriter(line.substring(line7, line8));	//writer
    	    		
    	    		line = lines[++i];
    	    		
    	    		int line9 = line.indexOf("<td>")+4;
    	    		int line10 = line.indexOf("</td>", line9);
    	    		titleItem.setDate(line.substring(line9,line10));	//Date
    	    		
    	    		line = lines[++i];
    	    		line1 = line.indexOf("<td>")+4;
    	    		line2 = line.indexOf("</td>", line1);
    	    		titleItem.setCnt(line.substring(line1,line2));	//cnt
    	    		
    	    		boardVc.add(titleItem);
    	    	}
            }
        }
		for(;i<lines.length;i++) {
			String line = lines[i];
			int startIndex = line.indexOf("<a class='page_search'");
			if (startIndex==-1) continue;
//			Log.d("debug", line);
			String searchText = "href='";
			int line5 = line.lastIndexOf(searchText);
			int line6 = line.indexOf("'",line5+searchText.length());
			
			if (line5>-1) {
				nextUrl=line.substring(line5+6,line6);	// 다음검색 URL
			}
		}
	}
	
	public Vector<BoardItemType> getBoardItemList(BoardListType item, int page, String searchMsg, String searchType) throws Exception {
		getData(item, page, searchMsg, searchType);
		return boardVc;
	}

}
