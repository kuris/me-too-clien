package com.zinho.clien.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

import com.zinho.clien.boardlist.BoardListType;

public class NetworkBoardList {
	
	private static Vector<BoardListType> boardVc = new Vector<BoardListType>();
	
	public void getData() {
		try {
			InputStream is = new URL(NetworkBase.CLIEN_URL).openStream();
		    String line = null;
		    
		    try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                boolean capture = false;
                w:while ((line = reader.readLine()) != null) {
                	if (line.indexOf("<div id=\"snb_navi1\">")>-1) {;
                    	capture= true;
                    	continue w;
                	}
                	
                	if (capture && line.indexOf("</div>")>-1) {
                		capture=false;
                	}
                	
                    if (capture) {
                    	int hrefCnt = line.indexOf("href");
                    	if (hrefCnt>-1) {
                    		int line1 = line.indexOf("\"", hrefCnt);
                    		int line2 = line.indexOf("\"", line1+1);
                    		String url = line.substring(line1+1,line2);
                    		
                    		int line3 = line.indexOf("/>");
                    		int line4 = line.indexOf("</a>",line3+1);
                    		String boardName = line.substring(line3+2,line4);
//                    		Log.d("debug", url+"+"+boardName);
//                    		boardVc.add(new BoardListType(url,boardName));
                    	}
                    }
                }
            } finally {
                is.close();
            }
		} catch (Exception e) {
			
		}
	}
	
	public static BoardListType getBoard(String key) {
		BoardListType result = null;
		for (int i=0;i<boardVc.size();i++) {
			if (boardVc.get(i).getKey().equals(key)) return boardVc.get(i);
		}
		return result;
	}
	
	public Vector<BoardListType> getBoardList() {
//		getData();
		if (boardVc.size()>0) return boardVc;	
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=park","모두의공원"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=image","사진게시판"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=kin","아무거나질문"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=news","새로운소식"));		
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=lecture","팁과강좌"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=use","사용기게시판"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=useful","유용한사이트"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=jirum","알뜰구매"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=coupon","쿠폰/이벤트"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=hongbo","직접홍보"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=pds","자료실"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=sold","회원중고장터"));
		
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_main","임시 소모임"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_mac","MacLIEN"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_iphonien","아이포니앙"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_girl","소녀시대"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_dia","디아블로"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_nokien","노키앙"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_leather","가죽당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_bb","블랙베리"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_wow","WOW"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_baby","육아당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_book","활자중독당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_daegu","대구당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_havehome","내집마련당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_kara","카라당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_oversea","바다건너당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_sea","Sea마당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_mabi","Mabinogien"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_music","소리당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_star","스타당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_coffee","클다방"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_lang","어학당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_car","굴러간당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_bike","자전거당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_andro","안드로메당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_tour","여행을떠난당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_twit","트윗당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_golf","골프당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_bear","곰돌이당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_swim","퐁당퐁당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_app","앱개발자당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_movie","영화본당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_board","보드게임당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_mount","오른당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_snow","미끄러진당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_photo","찰칵찍당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_webos","webOS당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_food","맛있겠당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_stock","고배당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_70","X세대당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_fashion","패셔니앙"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_pic","그림그린당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_cook","요리한당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_ku","THE KU당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_werule","WeRule"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_gym","땀흘린당"));
		boardVc.add(new BoardListType("cs2/bbs/board.php?bo_table=cm_classic","클래시앙"));
		return boardVc;
	}

}
