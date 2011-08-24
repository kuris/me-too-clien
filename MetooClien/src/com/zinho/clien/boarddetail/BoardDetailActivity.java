package com.zinho.clien.boarddetail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import net.daum.mobilead.AdConfig;
import net.daum.mobilead.AdHttpListener;
import net.daum.mobilead.MobileAdView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zinho.clien.R;
import com.zinho.clien.boarditem.BoardItemListActivity;
import com.zinho.clien.boarditem.BoardItemType;
import com.zinho.clien.boardwrite.BoardWriteActivity;
import com.zinho.clien.network.NetworkBase;
import com.zinho.clien.network.NetworkBoardDetail;
import com.zinho.clien.setting.SettingActivity;
import com.zinho.clien.util.AppData;
import com.zinho.clien.util.GetWriterPicData;
import com.zinho.clien.util.TitleActivity;
import com.zinho.clien.util.Util;
import com.zinho.clien.util.ZStyle;

public class BoardDetailActivity extends TitleActivity implements AdHttpListener {
	
	private static BoardDetailActivity thisClass = null;
	public static boolean showImage = true;
	public static boolean showVideo = true;
	
	private BoardItemType boardItem;
	private String baseURL = NetworkBase.CLIEN_URL+"cs2/bbs/";
	private LayoutInflater vi;
	
	private LinearLayout mainLayout;
	private ScrollView scrollView;
	
	private MobileAdView adView = null;
	private WebView webview = null;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_detail);
        thisClass = this;
        setTitle("","이전");
        getLoginButton();	//로그인 버튼 활성화
           
        Button originalLink = (Button)findViewById(R.id.board_detail_original_link_button);
        originalLink.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				onClickButton_originalLink();
			}
        });
        
        vi = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainLayout = (LinearLayout)findViewById(R.id.board_detail_main_layout);
        mainLayout.setVisibility(View.INVISIBLE);
        
        scrollView = (ScrollView) findViewById(R.id.board_detail_main_scrollview);
        View titlebar = findViewById(R.id.title_relativeLayout1);
        if (titlebar != null) {
	        titlebar.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					scrollView.fullScroll(View.FOCUS_DOWN);
				}
			});
        }
        
        View line = findViewById(R.id.board_detail_center_line1);
        line.setBackgroundColor(ZStyle.ListBackgroundSelectedColor);
        
    	String autoPictureData = Util.getSharedData(this, AppData.AUTO_PICTURE_ENABLE, "0");
        if (autoPictureData.equals("0")) showImage = false;
        String autoVideoData = Util.getSharedData(this, AppData.AUTO_VIDEO_ENABLE, "0");
        if (autoVideoData.equals("0")) showVideo = false;  
        
		// 할당 받은 clientId 설정
		AdConfig.setClientId("dccZ0zT131e0476483");

		// Ad@m sdk 초기화 시작
		adView = (MobileAdView) findViewById(R.id.adview);
		// adView.setRequestInterval(30);
		adView.setAdListener(this);
		adView.setVisibility(View.VISIBLE);
		
		webview = (WebView)findViewById(R.id.WebView);

    }
    
    public void loadedData(final ArticleType article) {
        
        TextView txtView = (TextView)findViewById(R.id.TitleView);
        txtView.setText(article.getTitle());
        txtView.setTextSize(18 * AppData.getBodyFontSize());
        
        TextView cntView = (TextView)findViewById(R.id.TitleCount);
        cntView.setText(article.getHit());
                
        String w = article.getWriter();
        TextView writerTextView = (TextView)findViewById(R.id.board_detail_writer_textview);
        ImageView writerImageView = (ImageView)findViewById(R.id.board_detail_writer_imageview);
        writerTextView.setText(article.getWriter());
        if (w.indexOf("img src") > -1) {
        	writerTextView.setVisibility(View.GONE);
			writerImageView.setVisibility(View.VISIBLE);
			
			String tmp = "<img src='";
			int start = w.indexOf(tmp)+tmp.length();
			String url=w.substring(start, w.indexOf("'", start));
			
			new GetWriterPicData(url, writerImageView).execute();
		} else {
			writerImageView.setVisibility(View.GONE);
			writerTextView.setVisibility(View.VISIBLE);
			writerTextView.setText(w);
		}
        
        
        webview.loadDataWithBaseURL(baseURL, article.insertCss()+article.getContent(), "text/html", "utf-8", null);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setPluginsEnabled(true);//플러그인작동시킨다.
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	Log.d("debug", "click url = "+ url);
            	if (url.indexOf("showImg:")>-1) {
	            	showImage = true;
	            	webview.loadDataWithBaseURL(baseURL, article.insertCss()+article.getContent(), "text/html", "utf-8", null);
	            	return true;
            	} else if (url.indexOf("showVideo:")>-1) {
            		showVideo = true;
	            	webview.loadDataWithBaseURL(baseURL, article.insertCss()+article.getContent(), "text/html", "utf-8", null);
	            	return true;
            	}  else if (url.indexOf("download:")>-1) {
	            	Log.d("debug", "download img = "+ url);
	            	downloadImage(url);
	            	return true;
            	} else {
            		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            		startActivity(i);
            		return true;
            	}
            }
        });

        addComment(article);
        
        TextView dateText = ((TextView)findViewById(R.id.board_detail_date_text));
        dateText.setText(article.getDate());
        dateText.setTextSize(14 * AppData.getBodyFontSize());
        
        if (NetworkBase.isLogin()) {
        	setTitle(article.getTitle(),"이전");
        	
        	boolean insertLayer = true;
        	LinearLayout layout = ((LinearLayout)findViewById(R.id.board_detail_reply_layout));
        	for (int i=layout.getChildCount();i>0;i--) {
	       		 View replyAddView = layout.getChildAt(i);
	       		 if (replyAddView==null || replyAddView.getTag()==null) continue;
	       		 if (replyAddView.getTag().equals("reply_new")){
	       			 insertLayer = false;
	       		 }
	       	 }
        	
        	if (insertLayer) {
	        	View replyAddView = vi.inflate(R.layout.board_detail_reply_add, null);
	        	replyAddView.setTag("reply_new");
	        	layout.addView(replyAddView);
        	}
        	
        } else {
        	setTitle(article.getTitle(),"이전");
        }
        
        
        Button btn = (Button)findViewById(R.id.board_detail_original_link_button);
		btn.setTextSize(14 * AppData.getBodyFontSize());
		
        if (article.updateLink()!=null) {
        	findViewById(R.id.board_detail_contents_edit_layout).setVisibility(View.VISIBLE);
        	final Button updateBtn = (Button)findViewById(R.id.board_detail_update_link_button);
        	final Button deleteBtn = (Button)findViewById(R.id.board_detail_delete_link_button);
        	
        	updateBtn.setTag(article.updateLink());
        	deleteBtn.setTag(article.deleteLink());
        	
        	updateBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					HashMap<String, String> intent = new HashMap<String, String>();
					intent.put("type", "u");
					intent.put("data", (String)updateBtn.getTag());
					Util.startActivity(thisClass, BoardWriteActivity.class, intent);
				}
        	});
        	
        	deleteBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					new DeleteData(deleteBtn.getTag().toString()).execute();
				}
        	});
        }
        mainLayout.setVisibility(View.VISIBLE);
    }
    
    public void downloadImage(final String url) {
    	
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("이미지 저장");
		dialog.setMessage("해당 이미지를 저장하시겠습니까?");
		dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				InputStream in = null;
				OutputStream mout = null;
				try {
					String url2 = url.replaceAll("download:", "");
					url2 = Util.converClianURL(url2);
					Log.d("debug", "download url="+url2);
					in = OpenHttpConnection(url2);
					String downloadfile = "/sdcard/download/"+url2.substring(url2.lastIndexOf("/"));
					mout = new FileOutputStream(downloadfile);
					byte[] buffer = new byte[1024];
					int length=0;
					while((length=in.read(buffer))>0) {
						mout.write(buffer,0,length);
					}
					Toast.makeText(thisClass, "다운로드 완료 : "+downloadfile, Toast.LENGTH_SHORT).show();
				} catch(Exception e) {
					Toast.makeText(thisClass, "error : "+e.toString(), Toast.LENGTH_SHORT).show();
				}
				
				try {mout.flush();}catch(Exception e) {}
				try {mout.close();}catch(Exception e) {}
				try {in.close();}catch(Exception e) {}
			}
		});
		dialog.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

        dialog.show();
    }
    
    private InputStream OpenHttpConnection(String $imagePath) {
		InputStream stream = null ;
		try {
			URL url = new URL( $imagePath ) ;
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection() ;
			urlConnection.setRequestMethod( "GET" ) ;
			urlConnection.connect() ;
			if( urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK ) {
				stream = urlConnection.getInputStream() ;
			}
		} catch (MalformedURLException e) {
			Log.e("debug", "", e);
		} catch (IOException e) {
			Log.e("debug", "", e);
		}
		return stream ;
	}
    
    public void addComment(ArticleType aComment) {
    	
    	 Vector<ArticleType> comments = aComment.getComment();
    	 LinearLayout layout = ((LinearLayout)findViewById(R.id.board_detail_reply_layout));
    	 
    	 if (comments.size()<1) {
    		 return;
    	 }
    	 
    	 removeReplyEditText();
		 
		for (int i = 0; i < comments.size(); i++) {
			View addedLayout = null;
			final ArticleType vComment = comments.get(i);
			View replyLayout = null;

			w:for (int j = 0; j < layout.getChildCount(); j++) {
				View tmp = layout.getChildAt(j);
				if (tmp.getTag() != null && vComment != null && tmp.getTag().equals(vComment.getWrId())) {
					addedLayout = tmp;
					break w;
				}
			}

			if (addedLayout == null) {
				replyLayout = vi.inflate(R.layout.board_detail_reply, null);
			} else {
				replyLayout = addedLayout;
			}

			if (vComment.isReplyed()) {
				LinearLayout mainLayout = (LinearLayout) replyLayout.findViewById(R.id.board_detail_reply_main_layout);
				LinearLayout.LayoutParams layoutParam = ((LinearLayout.LayoutParams) mainLayout.getLayoutParams());
				layoutParam.leftMargin = Util.parseDPItoPx(15);
				mainLayout.setLayoutParams(layoutParam);
				replyLayout.findViewById(R.id.board_detail_reply_img).setVisibility(View.VISIBLE);
			} else {
				replyLayout.findViewById(R.id.board_detail_reply_img).setVisibility(View.GONE);
			}
			
			TextView writerText = (TextView) replyLayout.findViewById(R.id.board_detail_reply_writer_text);
			replyLayout.findViewById(R.id.board_detail_reply_writer_layout).setBackgroundColor(Color.parseColor(ZStyle.getListBackfroundSelectedColor()));
			writerText.setTextColor(Color.parseColor(ZStyle.getTextColor()));
			final ImageView writerImageView = (ImageView)replyLayout.findViewById(R.id.board_detail_reply_writer_image);
			if (vComment.getWriter().indexOf("../data/member") > -1) {
				
				String writer = vComment.getWriter().replaceAll("<img src=\"../skin/board/cheditor/img/blet_re2.gif\">", "");
				writerImageView.setVisibility(View.VISIBLE);
				int index1 = writer.indexOf("'")+1;
				int index2 = writer.indexOf("'",index1);
				new GetWriterPicData(NetworkBase.CLIEN_URL+"cs2"+writer.substring(index1,index2).replaceAll("\\.\\.", ""), writerImageView).execute();
				
				String writerStr =  "님 <font size=1>(" + vComment.getDate() + ")</font>";
				writerText.setText(Html.fromHtml(writerStr));
			} else {
				
				writerImageView.setVisibility(View.GONE);
				String writerStr = Util.removeTag(vComment.getWriter())	+ " <font size=1>(" + vComment.getDate() + ")</font>";
				writerText.setText(Html.fromHtml(writerStr));
			}

			TextView replyContents = (TextView) replyLayout.findViewById(R.id.board_detail_reply_contents);
			replyContents.setText(Html.fromHtml(vComment.getContent()));
			replyContents.setTextSize(16 * AppData.getBodyFontSize());
			if (vComment.getContent().toLowerCase().indexOf("<a")>-1) {
				Linkify.addLinks(replyContents, Linkify.WEB_URLS);
			}

			final Button fButton = (Button) replyLayout.findViewById(R.id.board_detail_reply_f_button);
			if (NetworkBase.isLogin() && (vComment.isReplyed()==false || vComment.getModify() || vComment.getDelete())) {
				fButton.setVisibility(View.VISIBLE);
				fButton.setTag(vComment.getWrId());
				if (vComment.getModify()) {
					fButton.setText("F");
					fButton.setTextColor(Color.RED);
				} else {
					fButton.setText("R");
					fButton.setTextColor(Color.parseColor("#d0d0d0"));
				}
				fButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (fButton.getText().equals("F")) functionDialog(v, vComment);
						else onClickReplyReply(v,"R");
					}
				});

			} else {
				fButton.setVisibility(View.GONE);
			}
			

			if (addedLayout == null) { // 신규 추가요~!
				replyLayout.setTag(vComment.getWrId());
				layout.addView(replyLayout, i);
			}
         }         
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.board_detail_menu, menu);
		
    	return true;
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (NetworkBase.isLogin()) {
			menu.findItem(R.id.board_detail_menu_login).setTitle(R.string.board_list_menu_logout);
		} else {
			menu.findItem(R.id.board_detail_menu_login).setTitle(R.string.board_list_menu_login);
		}
		
		if (AppData.isHideTitleBar()) {
			menu.findItem(R.id.board_list_menu_hide_titlebar).setTitle(R.string.board_list_menu_showtitlebar);
		} else {
			menu.findItem(R.id.board_list_menu_hide_titlebar).setTitle(R.string.board_list_menu_hidetitlebar);
		}
		return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.board_detail_menu_reload:
        	update();
        	break;
        	
        case R.id.board_detail_menu_login:
        	onClickLoginButton(null);
        	break;
        	
        case R.id.board_detail_menu_setting:
        	Util.startActivity(this, SettingActivity.class);
        	break;
        	
        case R.id.board_list_menu_hide_titlebar:
        	hideTitleBar(!AppData.isHideTitleBar());
        	break;
        }
        return false;
    }
    
    public void onResume() {
    	super.onResume();
    	update();
    	callHiddenWebViewMethod("onResume");
    }
    

    public void onPause() {
    	callHiddenWebViewMethod("onPause");
        super.onPause();
    }

    
    /**
     * 이전 화면으로
     * 
     * @param v
     */
    public void onLeftTitleClickButton(View v) {
    	finish();
    }
    
    /**
     * 쓰기 화면으로 이동
     * 
     * @param v
     */
    public void onRightTitleClickButton(View v) {
    	
    }
    
    public void onClickButton_simpleReply(View v) {
    	String tag = (String)v.getTag(), selectedKey, type;
    	if (tag==null) {
    		selectedKey="";
    		type="";
    	}
    	else {
    		selectedKey = tag.split("&")[0];
    		type = tag.split("&")[1];
    	}
    	
    	EditText editText = (EditText)findViewById(R.id.board_detail_simpleReply_txt);
    	String message = editText.getText().toString();
    	if (message==null || message.length()<1) {
    		Toast.makeText(this, "내용이 없습니다.", 1000).show();
    		return;
    	}
    	
    	HashMap<String, String> data = new HashMap<String, String>();
    	data.put("wr_content", message+" ⓣ");	//내용
    	if (selectedKey.equals("") || type.equals("R")) {
    		data.put("w", "c");	//
    	} else if (type.equals("U")){
    		data.put("w", "cu");
    	}
    	data.put("bo_table", boardItem.getBo_table());
    	data.put("wr_id", boardItem.getWrId());
    	data.put("comment_id", selectedKey); // 코멘트 아이디가 넘어오면 답변, 수정
    	data.put("sca", "");
    	data.put("sfl", "");
    	data.put("stx", "");
    	data.put("spt", "");
    	data.put("page", "");
    	data.put("cwin", "");
    	data.put("is_good", "");
    	showLoading(true);
    	
    	//키보드 숨기기
    	InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    	new ReplyData(editText, data).execute();
    }
    
    public void onClickButton_originalLink() {
		try {
			BoardItemType item = BoardItemListActivity.getInstance().getSelectedItem();
			String url = NetworkBase.CLIEN_URL+item.getUrl().replaceAll("\\.\\.","");
			Uri uri = Uri.parse(url);
			Intent it  = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(it);
		}catch (Exception e) {
			Toast.makeText(this, "열수 없는 형식입니다.", 1000).show();
		}
    }
    
    private boolean removeReplyEditText() {
    	LinearLayout layout = ((LinearLayout)findViewById(R.id.board_detail_reply_layout));
    	
    	for (int i=0;i<layout.getChildCount();i++) {
    		View tmp = layout.getChildAt(i);
			if (tmp.getTag()!=null && (tmp.getTag().equals("R") || tmp.getTag().equals("U"))) {
				layout.removeViewAt(i);
				return true;
			}
    	}
    	return false;
    }
    
    private void removeReplyTextView(String key) {
    	LinearLayout layout = ((LinearLayout)findViewById(R.id.board_detail_reply_layout));
    	
    	for (int i=0;i<layout.getChildCount();i++) {
    		View tmp = layout.getChildAt(i);
			if (tmp.getTag()!=null && tmp.getTag().equals(key)) {
				layout.removeViewAt(i);
				return;
			}
    	}
    }
    
    /**
     * 답글에 답글 달기 버튼 클릭시.
     * 
     * @param v
     */
    public void onClickReplyReply(View v, String type) {
    	String key = (String)v.getTag();
    	LinearLayout layout = ((LinearLayout)findViewById(R.id.board_detail_reply_layout));
    	if (removeReplyEditText()) return;
    	
    	//없을 경우
    	for (int i=0;i<layout.getChildCount();i++) {
    		String value = (String)layout.getChildAt(i).getTag();
    		if (key.equals(value)) {
    			LinearLayout replyAddView =(LinearLayout)vi.inflate(R.layout.board_detail_reply_add, null);
    			View btn = replyAddView.findViewById(R.id.board_detail_simpleReply_sendbutton);
    			btn.setTag(key+"&"+type);
    			replyAddView.setTag(type);
    			if (type.equals("U")) {
    				EditText edit = (EditText)replyAddView.findViewById(R.id.board_detail_simpleReply_txt);
    				LinearLayout target = (LinearLayout)layout.getChildAt(i);
    				TextView txt = (TextView)target.findViewById(R.id.board_detail_reply_contents);
    				edit.setText(txt.getText().toString().replaceAll("ⓣ", ""));
    			}
    			layout.addView(replyAddView, i+1);
    			
    	 		LinearLayout.LayoutParams layoutParam = ((LinearLayout.LayoutParams)replyAddView.getLayoutParams());
    	 		layoutParam.leftMargin = Util.parseDPItoPx(15);
    	 		replyAddView.setLayoutParams(layoutParam);
    			break;
    		}
    	}
    }
    
	private void choiceDialog(View v, String type) {
		if (type.equals("수정")) {
			onClickReplyReply(v, "U");
		} else if (type.equals("삭제")) {
			String tag = (String)v.getTag(), selectedKey;
	    	if (tag==null) selectedKey="";
	    	else selectedKey = tag.split("&")[0];
	    	
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("w","D");
			data.put("bo_table",boardItem.getBo_table());
			data.put("comment_id", selectedKey);
			data.put("cwin","");
			data.put("page","0");
			new ReplyData(null, data).execute();
		}
	}
	
	private void functionDialog(final View v, ArticleType vComment) {
		final String items[];
		
		if (vComment.isReplyed()) items = new String[]{"수정","삭제"};
		else items = new String[]{"답글","수정","삭제"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("기능 선택");
		builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				choiceDialog(v, items[whichButton]);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
    public static BoardDetailActivity getInstance() {
    	return BoardDetailActivity.thisClass;
    }
    
    public void update() {
    	showLoading(true);
    	new GetData().execute();
    }
    
	public void onDestroy() {
		
		if (adView != null) {
			adView.destroy();
			adView = null;
		}
		
		super.onDestroy();
	}
	
	public void failedDownloadAd_AdListener(int errorno, String errMsg) {
		// fail to receive Ad
	}

	public void didDownloadAd_AdListener() {
		// success to receive Ad
	}
    
    private class GetData extends AsyncTask<String, Integer, ArticleType> {
    	
		protected ArticleType doInBackground(String... arg0) {
			ArticleType article = null;
			try {
				boardItem = BoardItemListActivity.getInstance().getSelectedItem();
		        NetworkBoardDetail net = new NetworkBoardDetail();
	        
	        	article = net.getData(boardItem);
	        } catch (Exception e) {
	        	Log.e("debug", e.toString(), e);
	        }
			return article;
		}

		protected void onPostExecute(ArticleType article) {
			if (article==null) {
				showLoading(false);
				Toast.makeText(thisClass, "네트워크 오류입니다.", 1000).show();
				return;
			}
			if (boardItem.getBo_table().equals("sold") && NetworkBase.isLogin()==false) {
				Toast.makeText(thisClass, "회원중고장터의 글은 로그인 상태에서만 열람이 가능합니다.", 1000).show();
			}
			loadedData(article);
			showLoading(false);
		}
	}
    
    private class ReplyData extends AsyncTask<String, Integer, String> {
    	EditText target;
    	HashMap<String, String> data;
    	
    	public ReplyData(EditText target, HashMap<String, String> data) {
    		this.target = target;
    		this.data = data;
    	}
    	
		protected String doInBackground(String... arg0) {
			String result = null;
			String url = null;
			if (data.get("w").equals("D")) {
				url = NetworkBase.CLIEN_URL+"cs2/bbs/delete_comment.php";
			} else {
				url = NetworkBase.CLIEN_URL+"cs2/bbs/write_comment_update.php";
			}
			try {
				result = NetworkBase.getData(url, data);
			}catch (Exception e) {}
			
			return result;
		}

		protected void onPostExecute(String temp) {
			if (temp==null) {
				Toast.makeText(thisClass, "네트워크 오류입니다.", 1000).show();
				return;
			}
			if (data.get("w").equals("D")) {
				Toast.makeText(thisClass, "댓글 삭제 완료", 1000).show();
				removeReplyTextView(data.get("comment_id"));
			} else {
				Toast.makeText(thisClass, "댓글 쓰기 완료", 1000).show();
			}
			removeReplyEditText();
			update();
			if (target!=null) target.setText("");
			
		}
	}
    
    private class DeleteData extends AsyncTask<String, Integer, String> {
    	String params = null;
    	
    	public DeleteData(String params) {
    		this.params = params;
    	}
    	
		protected String doInBackground(String... arg0) {
			String url = NetworkBase.CLIEN_URL+"cs2/bbs/delete.php?"+params;
			Log.d("debug", url);
			String result = null;
			try {
				result = NetworkBase.getData(url);
			}catch (Exception e) {
				Log.w("Network Error : ", e.toString(), e);
			}
			return result;
		}

		protected void onPostExecute(String result) {
			if (result==null) {
				showLoading(false);
				Toast.makeText(thisClass, "네트워크 오류입니다.", 1000).show();
				return;
			}
			Log.d("debug", result);
			showLoading(false);
			Toast.makeText(thisClass, "삭제되었습니다.", 1000).show();
			finish();
		}
	}
    
    private void callHiddenWebViewMethod(String name){
    	Log.d("debug", ".. "+ name);
    	if( webview != null ){
            try {
            	Log.d("debug", "run "+ name);
                Method method = WebView.class.getMethod(name);
                method.invoke(webview);
            } catch (Exception e) {
                Log.e("debug",":"+name, e);
            }
        }
    }
}
