package com.zinho.clien.network;

import java.util.Vector;

import android.util.Log;

import com.zinho.clien.boarddetail.ArticleType;
import com.zinho.clien.boarditem.BoardItemType;
import com.zinho.clien.boardlist.BoardListType;

public class NetworkBoardDetail {
	
	StringBuffer bf = new StringBuffer();
	
	public ArticleType getData(BoardItemType item) throws Exception {
		
		String url = NetworkBase.CLIEN_URL+item.getUrl().replaceAll("\\.\\.","");
//        Log.d("debug", "NetworkBoardDetail : "+url);
		String value = NetworkBase.getData(url);
        
		return createArticle(value, item.getWrId());
	}
	
	public ArticleType createArticle(String value, String wrid) {
		String[] lines = value.split("\n");
		String line = null;
		 boolean capture = false;
        w:for (int i=0;i<lines.length;i++) {
        	line = lines[i];
        	if (line.indexOf("<div class=\"board_main\">")>-1) {;
            	capture= true;
            	continue w;
        	}
        	
        	if (capture && line.indexOf("<!-- 리플을 작성하는 영역 입니다. -->")>-1) {
        		capture=false;
        		break w;
        	}
        	
            if (capture) {
            	bf.append(line+"\n");
            }
        }
        
        ArticleType article = new ArticleType();
		article.setWrId(wrid);
		article.setHtml(bf.toString());
		return article;
	}
	
	public Vector<ArticleType> getPicData(BoardListType item, int page) throws Exception {
		Vector<ArticleType> vc = new Vector<ArticleType>();
		
		String url = NetworkBase.CLIEN_URL+item.getUrl()+"&page="+page;
		Log.d("debug", "url="+url);
        String value = NetworkBase.getData(url);
        String lines[] = value.split("\n");
        boolean capture = false;
        StringBuffer bf = null;
        
        for (int i=0;i<lines.length;i++) {
        	
	        if (lines[i].indexOf("<!-- 정상일때으 처리 -->")>-1) {
	        	bf = new StringBuffer();
	        	capture = true;
	        }
	        if (capture) {
	        	bf.append(lines[i]+"\n");
	        }
	        
	        if (capture && lines[i].indexOf("<!-- 요기까지-->")>-1) {
	        	capture = false;
	        	
	        	String wrstr = "../bbs/board.php?bo_table=image&wr_id=";
	           	if (bf.indexOf(wrstr)==-1) continue;
	           	
	        	String wr = bf.toString().substring(bf.indexOf(wrstr)+wrstr.length(), bf.indexOf(wrstr)+wrstr.length()+8);
	        	wr = wr.replaceAll("&", "").replaceAll("'", "");
	        	
	        	ArticleType article = new ArticleType();
	    		article.setWrId(wr);
	    		article.setHtmlPic(bf.toString());
	        	vc.add(article);
	        }
        }
		return vc;
	}

}
