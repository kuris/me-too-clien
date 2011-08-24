package com.zinho.clien.boarddetail;

import java.util.Vector;

import android.graphics.Bitmap;
import android.util.Log;

import com.zinho.clien.network.NetworkBase;
import com.zinho.clien.util.AppData;
import com.zinho.clien.util.Util;
import com.zinho.clien.util.ZStyle;

public class ArticleType {
	
	private String title = null;
	private String writer = null;
	private String date = null;
	private String hit = null;
	private String content = null;
	private String signature = null;
	private String wrid = null;
	private boolean replyed = false;
	private Vector<ArticleType> comment = new Vector<ArticleType>();
	private Bitmap bitmap = null;
	private String editAndDeleteStr = null;
	private boolean isModify = false;
	private boolean isDelete = false;
	
	private static final String TITLE_TEG = "view_title";
	
	public void setHtml(String txt) {
		String[] txts = txt.split("\\n");
		
		for (int i=0;i<txts.length;i++) {
			
			//Writer 설정
			if (txts[i].indexOf("user_info")>-1) {
				int span = txts[i].indexOf("<span");
				// 일반 Txt
				if (span>-1) {
					int line1 = txts[i].indexOf(">", span+4)+1;
					int line2 = txts[i].indexOf("</span>",line1);
					setWriter(txts[i].substring(line1,line2));
				} else { // 반짝이
					String _writer = txts[i].trim();
					_writer = _writer.replaceAll("<p class=\"user_info\">", "");
					_writer = _writer.replaceAll("</p>", "");
					_writer = _writer.replaceAll("<img src='../", "<img src='"+NetworkBase.CLIEN_URL+"cs2/");
					setWriter(_writer);
				}
			}
			
			// Post Info
			if (txts[i].indexOf("post_info")>-1) {
				int firstLine = txts[i].indexOf("post_info");
				int line1 = txts[i].indexOf(">",firstLine)+1;
				int line2 = txts[i].indexOf("</",line1);
				
				String tmpTxt = txts[i].substring(line1,line2);
				String[] postInfos = tmpTxt.split(",");
				setDate(postInfos[0]);
				setHit(postInfos[1]);
			}
			
			
			// 제목 부분 설정	
			if (txts[i].indexOf(TITLE_TEG)>-1) {
				w1:for (;i<txts.length;i++) {
					if (txts[i].indexOf("<span>")>-1) {
						int line1 = txts[i].indexOf("<span>")+6;
						int line2 = txts[i].indexOf("</span>",line1+1);
						setTitle(txts[i].substring(line1,line2));
						break w1;
					}
				}
			}
			
			// Contents
			if (txts[i].indexOf("<div class=\"view_content\"")>-1) {
				StringBuffer bf = new StringBuffer();
				int startline = txts[i].indexOf(">")+1;
				bf.append(txts[i].substring(startline));
				
				i++;
				
				w2:for (;i<txts.length;i++) {
					if (txts[i].indexOf("<ul class=\"view_content_btn2\">")>-1) break w2;
					if (txts[i].indexOf("<!-- 광고 영역 -->")>-1) break w2;
					String converted = Util.getShortLink(txts[i],30); //Link
					bf.append(converted);
				}
				
				setContent(bf.toString());
			}
			
			//reply
			if (txts[i].indexOf("<div class=\"reply_head\"")>-1) {
				StringBuffer tmpBf = new StringBuffer();
				
				w3:for (;i<txts.length;i++) {
					if (txts[i].indexOf("</textarea>")>-1) break w3;
					tmpBf.append(txts[i]);
				}
				String tmpStr = tmpBf.toString();
				ArticleType reply = new ArticleType();
				
				String tmpTag = "<div class=\"reply_content\">";
				int line1 = tmpStr.indexOf(tmpTag);
				if (line1>-1) {
					int line2 = tmpStr.indexOf("<span",line1);
					if (line2==-1) continue;
					reply.setContent(tmpStr.substring(line1+tmpTag.length(), line2));
				}
				
				//사용자 정보
				tmpTag = "<li class=\"user_id\">";
				line1 = tmpStr.indexOf(tmpTag);
				if (line1>-1) {
					int line2 = tmpStr.indexOf("</li",line1);
					int line3 = tmpStr.indexOf("</span",line1);
					if (line2>line3) line2 = line3; 
					reply.setWriter(tmpStr.substring(line1+tmpTag.length(), line2));
				}
				
				//comment Date 값
				String searchStr = "<li> (";
				int indexStart = tmpStr.indexOf(searchStr);
				if (indexStart>0) {
					String indexStartStr = "(";
					indexStart = tmpStr.indexOf(indexStartStr, indexStart-1);
					int indexEnd = tmpStr.indexOf(")", indexStart+indexStartStr.length());
					reply.setDate(tmpStr.substring(indexStart+indexStartStr.length(), indexEnd));
				}
				
				//comment id값
				searchStr = "<span id='reply";
				indexStart = tmpStr.indexOf(searchStr);
				if (indexStart>-1) {
					String indexStartStr = "_";
					indexStart = tmpStr.indexOf(indexStartStr, indexStart);
					int indexEnd = tmpStr.indexOf("'", indexStart+indexStartStr.length());
					reply.setWrId(tmpStr.substring(indexStart+indexStartStr.length(), indexEnd));
				}
				
				searchStr = "icon_reply_modify.gif";
				indexStart = tmpStr.indexOf(searchStr);
				if (indexStart>-1) {
					reply.setModify(true);
				}
				
				searchStr = "icon_reply_del.gif";
				indexStart = tmpStr.indexOf(searchStr);
				if (indexStart>-1) {
					reply.setDelete(true);
				}
				
				addComment(reply);
			}
		}
	}
	
	public void setHtmlPic(String txt) {
		String[] txts = txt.split("\n");
		
		for (int i=0;i<txts.length;i++) {
			
			//Writer 설정
			if (txts[i].indexOf("user_info")>-1) {
				int span = txts[i].indexOf("<span");
				// 일반 Txt
				if (span>-1) {
					int line1 = txts[i].indexOf(">", span+4)+1;
					int line2 = txts[i].indexOf("</span>",line1);
					setWriter(txts[i].substring(line1,line2));
				} else { // 반짝이
					String _writer = txts[i].trim();
					_writer = _writer.replaceAll("<td class=\"post_name\">", "");
					_writer = _writer.replaceAll("<img src='../", "<img src='"+NetworkBase.CLIEN_URL+"cs2/");
					Log.d("debug", ""+_writer);
					setWriter("반짝이");
				}
			}
			
			// Post Info
			if (txts[i].indexOf("post_info")>-1) {
				int firstLine = txts[i].indexOf("post_info");
				int line1 = txts[i].indexOf(">",firstLine)+1;
				int line2 = txts[i].indexOf("</",line1);
				
				String tmpTxt = txts[i].substring(line1,line2);
				String[] postInfos = tmpTxt.split(",");
				setDate(postInfos[0]);
				setHit(postInfos[1]);
			}
			
			
			// 제목 부분 설정	
			if (txts[i].indexOf(TITLE_TEG)>-1) {
				w1:for (;i<txts.length;i++) {
					if (txts[i].indexOf("<span>")>-1) {
						int line1 = txts[i].indexOf("<span>")+6;
						int line2 = txts[i].indexOf("</span>",line1+1);
						setTitle(Util.removeTag(txts[i].substring(line1,line2)));
						break w1;
					}
				}
			}
			
			// Contents
			if (txts[i].indexOf("<div class=\"view_content\"")>-1) {
				StringBuffer bf = new StringBuffer();
				int startline = txts[i].indexOf(">")+1;
				bf.append(txts[i].substring(startline));
				
				i++;
				
				w2:for (;i<txts.length;i++) {
					if (txts[i].indexOf("<ul class=\"view_content_btn2\">")>-1) break w2;
					if (txts[i].indexOf("<!-- 광고 영역 -->")>-1) break w2;
					bf.append(txts[i]);
				}
				
				setContent(bf.toString());
			}
			
			if (i>=txts.length) return;
			//reply
			if (txts[i].indexOf("<div class=\"reply_head\"")>-1) {
				StringBuffer tmpBf = new StringBuffer();
				
				w3:for (;i<txts.length;i++) {
					if (txts[i].indexOf("</textarea>")>-1) break w3;
					tmpBf.append(txts[i]);
				}
				String tmpStr = tmpBf.toString();
				ArticleType reply = new ArticleType();
				
				String tmpTag = "<div class=\"reply_content\">";
				int line1 = tmpStr.indexOf(tmpTag);
				if (line1>-1) {
					int line2 = tmpStr.indexOf("<span",line1);
					reply.setContent(tmpStr.substring(line1+tmpTag.length(), line2));
				}
				
				//사용자 정보
				tmpTag = "<li class=\"user_id\">";
				line1 = tmpStr.indexOf(tmpTag);
				if (line1>-1) {
					int line2 = tmpStr.indexOf("</li",line1);
					int line3 = tmpStr.indexOf("</span",line1);
					if (line2>line3) line2 = line3; 
					reply.setWriter(tmpStr.substring(line1+tmpTag.length(), line2));
				}
				
				addComment(reply);
			}
		}
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getWriter() {
		if (writer==null) return "";
		return writer.trim();
	}

	public void setWriter(String writer) {
		this.writer = Util.removeATag(writer);
		if (this.writer.indexOf("blet_re2.gif")>-1) {
			replyed = true;
		}
	}

	public String getDate() {
		return Util.removeTag(date);
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getHit() {
		return hit;
	}

	public void setHit(String hit) {
		this.hit = hit;
	}
	
	public void setWrId(String wrid) {
		this.wrid = wrid;
	}
	
	public String getWrId() {
		return wrid;
	}
	
	public void setModify(boolean modify) {
		this.isModify = modify;
	}
	
	public boolean getModify() {
		return this.isModify;
	}
	
	public void setDelete(boolean delete) {
		this.isDelete = delete;
	}
	
	public boolean getDelete() {
		return isDelete;
	}
	
	public String updateLink() {
		if (editAndDeleteStr==null) return null;
		
		String indexStartStr = "write.php?";
		int indexStart = editAndDeleteStr.indexOf(indexStartStr);
		if (indexStart>-1) {
			indexStart = indexStart+indexStartStr.length();
			int indexEnd = editAndDeleteStr.indexOf(">",indexStart);
			return editAndDeleteStr.substring(indexStart,indexEnd-1);
		}
		
		return null;
	}
	
	public String deleteLink() {
		if (editAndDeleteStr==null) return null;
		
		String indexStartStr = "delete.php?";
		int indexStart = editAndDeleteStr.indexOf(indexStartStr);
		if (indexStart>-1) {
			indexStart = indexStart+indexStartStr.length();
			int indexEnd = editAndDeleteStr.indexOf(")",indexStart);
			return editAndDeleteStr.substring(indexStart,indexEnd-1);
		}
		
		return null;
	}

	public String getContent() {
		String indexStartStr = "<p class=\"view_content_btn\">";
		String indexEndStr = "</p>";
		if (content==null) return "";
		String wContent = null;
		if (BoardDetailActivity.showImage) {
			wContent = content;
		} else {
			wContent = content.replaceAll("<img", "<div style='border:2 solid #0000ff;padding:10;' onclick='window.location=\"showImg:123\"'>이미지 있음. 보기를 하려면 클릭 하세요.</div><div");
		}
		
		if (BoardDetailActivity.showVideo) {
		} else {
			if (wContent.indexOf("<object")>-1) {
				wContent = wContent.replaceAll("<object", "<div style='border:2 solid #0000ff;padding:10;' onclick='window.location=\"showVideo:123\"'>비디오 있음. 보기를 하려면 클릭 하세요.</div><div");
				wContent = wContent.replaceAll("</object>", "");
				wContent = wContent.replaceAll("<embed", "<div");
				wContent = wContent.replaceAll("</embed>", "");
			} else if (wContent.indexOf("<embed")>-1){
				wContent = wContent.replaceAll("<embed", "<div style='border:2 solid #0000ff;padding:10;' onclick='window.location=\"showVideo:123\"'>비디오 있음. 보기를 하려면 클릭 하세요.</div><div");
				wContent = wContent.replaceAll("</embed>", "");
			}
			
		}
		int indexStart = wContent.indexOf(indexStartStr);
		if (indexStart>-1) {
			int indexEnd = wContent.indexOf(indexEndStr, indexStart+indexStartStr.length());
			editAndDeleteStr = wContent.substring(indexStart+indexStartStr.length(), indexEnd).trim();

			if (editAndDeleteStr.length()<1) editAndDeleteStr = null;
			return wContent.substring(indexEnd+indexEndStr.length()).trim();
		}
		
		return wContent.trim();
	}

	public void setContent(String content) {

//		Log.d("debug", "conent="+content);
		int sig1 = content.indexOf("<div class=\"signature\">");
		int sig2 = content.indexOf("<div class=\"ccl\">");

		if (sig1==-1) {
			if (sig2==-1) {
				this.content = content;
			} else {
				this.content = content.substring(0,sig2);
			}
		} else {
			this.content = content.substring(0,sig1);	 // 본문 내용만.
			if (sig2 != -1) {
				this.signature = content.substring(sig1,sig2);	// 서명 부분.
			}
		}
	}
	
	/**
	 * 컨텐츠에 이미지가 있을 경우.
	 * @return
	 */
	public String getImage() {
		int img1 = content.indexOf("<img");
		if (img1==-1) return null;
		int img2 = content.indexOf("src='",img1);
		if (img2==-1) {
			img2 = content.indexOf("src=\"", img1);
			int img3 = content.indexOf("\"",img2+5);
			return content.substring(img2+5,img3);
		} else {
			int img3 = content.indexOf("'",img2+5);
			return content.substring(img2+5,img3);
		}
	}
	
	public String getSignature() {
		return this.signature;
	}
	
	public void setImageBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public Bitmap getImageBitmap() {
		return this.bitmap;
	}

	public Vector<ArticleType> getComment() {
		return comment;
	}

	public void addComment(ArticleType comment) {
		this.comment.add(comment);
	}
	
	public String getAllCommentHtml() {
		StringBuffer bf = new StringBuffer();
		for (int i=0;i<comment.size();i++) {
			bf.append(getCommentHtml(comment.get(i)));
		}
		
		return bf.toString();
	}
	
	public boolean isReplyed() {
		return this.replyed;
	}
	
	public String getCommentHtml(ArticleType comment) {
		StringBuffer bf = new StringBuffer();
		bf.append("<table>");
		bf.append("<tr>");
		if (this.replyed) {
			bf.append("<td width='30'>&nbsp;</td>");
		}
		bf.append("<td class='writer'>");
		bf.append(comment.getWriter());
		bf.append("</td><td></td></tr>");
		bf.append("<tr>");
		if (this.replyed) {
			bf.append("<td width='30'>&nbsp;</td>");
		}
		bf.append("<td conspan=2>");
		bf.append(comment.content);
		bf.append("</td></tr>");
		bf.append("</table>");
		return bf.toString();
	}
	
	public String insertCss() {
		boolean isWhite = ZStyle.getThemeType(null)==ZStyle.THEME_TYPE_WHITE;
		StringBuffer str = new StringBuffer();
		str.append("<html><head><style>\n");
		str.append("p { margin: 0; padding:0; line-height: 120%; }\n");
		str.append("img, div, table,tr,td {max-width:99.5%;}\n");
		str.append("embed {width:90%;height:auto;}\n");
		str.append("body {\n");
		str.append("margin: 5px 0px 5px 0px; padding: 0; line-height: 120%; \n");
		str.append("background-color:"+(isWhite?"white":"black")+";\n");
		str.append("font-size:"+(AppData.getBodyFontSize()*100)+"%;\n");
		str.append("color:"+(isWhite?"black":"#dadada")+";}\n");
		str.append(".writer{ padding:5px;\n");
		str.append("background-color:"+ZStyle.getListBackfroundSelectedColor()+";\n");
		str.append("color:"+ZStyle.getTextColor()+";}\n");
		str.append("</style>\n");
		str.append("<script>\n");
		str.append("document.write('-');");
		str.append("function image_window3(v,v2,v3) { window.location='download:'+v; }");
		str.append("</script></head><body>\n");
		return str.toString();
	}
	
	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("title="+title);
		bf.append(", writer="+writer);
		bf.append(", hit="+hit);
		bf.append(", content="+content);
		return bf.toString();
	}
}
