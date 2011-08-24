package com.zinho.clien.setting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zinho.clien.R;
import com.zinho.clien.boarddetail.ArticleType;
import com.zinho.clien.util.AppData;
import com.zinho.clien.util.TitleActivity;
import com.zinho.clien.util.Util;
import com.zinho.clien.util.ZStyle;

public class FontSettingActivity extends TitleActivity {
	private LayoutInflater vi;
	private TextView titleTextView;
	private View listAddedView, bodyAddedView;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_font);
        setTitle("폰트 설정","이전");
        vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
        initScreen();
    }
	
	public void initScreen() {
		
		//타이틀 화면
		if (titleTextView==null) {
			View addedView = vi.inflate(R.layout.titlebar, null);
			LinearLayout targetView = (LinearLayout)findViewById(R.id.setting_font_title_preview_layout);
			addedView.findViewById(R.id.title_search_button).setVisibility(View.GONE);
			addedView.findViewById(R.id.title_etc_button).setVisibility(View.GONE);
			addedView.findViewById(R.id.title_login_button).setVisibility(View.GONE);
			addedView.findViewById(R.id.title_button1).setVisibility(View.GONE);
			
			titleTextView = (TextView)addedView.findViewById(R.id.title_textView1);
			targetView.addView(addedView);
		}
		TextView fontTitleSize = (TextView)findViewById(R.id.setting_font_title_textView);
		fontTitleSize.setText("폰트크기 : "+Math.round(AppData.getTitleFontSize()*100)+"%");
		titleTextView.setTextSize(20 * AppData.getTitleFontSize());
		
		//List 화면
		if (listAddedView==null) {
			listAddedView = vi.inflate(R.layout.board_item_row, null);
			LinearLayout targetView = (LinearLayout)findViewById(R.id.setting_font_list_preview_layout);
			targetView.setBackgroundColor(ZStyle.ListBackgroundColor);
			targetView.addView(listAddedView);
		}
		
		int height = Util.parseDPItoPx((int)(AppData.getListFontSize()*55)+2);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height);
		listAddedView.setLayoutParams(params);
		
		TextView listTextView = (TextView)listAddedView.findViewById(R.id.BoardTitle);
		listTextView.setTextSize(20 * AppData.getListFontSize());
		listTextView = (TextView)listAddedView.findViewById(R.id.BoardCnt);
		listTextView.setTextSize(14 * AppData.getListFontSize());
		listTextView = (TextView)listAddedView.findViewById(R.id.BoardDate);
		listTextView.setTextSize(14 * AppData.getListFontSize());
		listTextView = (TextView)listAddedView.findViewById(R.id.board_item_row_boardwriter);
		listTextView.setTextSize(14 * AppData.getListFontSize());
		
		TextView fontListSize = (TextView)findViewById(R.id.setting_font_list_textView);
		fontListSize.setText("폰트크기 : "+Math.round(AppData.getListFontSize()*100)+"%");
		
		//Body 화면
		if (bodyAddedView==null) {
			bodyAddedView = vi.inflate(R.layout.board_detail, null);
			LinearLayout targetView = (LinearLayout)findViewById(R.id.setting_font_body_preview_layout);
			targetView.setBackgroundColor(ZStyle.ListBackgroundColor);
			targetView.addView(bodyAddedView);
		}
		
		TextView bodyTextView = (TextView)bodyAddedView.findViewById(R.id.TitleView);
		bodyTextView.setTextSize(18 * AppData.getBodyFontSize());
		bodyTextView = (TextView)bodyAddedView.findViewById(R.id.board_detail_date_text);
		bodyTextView.setTextSize(14 * AppData.getBodyFontSize());
		ArticleType article = new ArticleType();
		TextView textView = (TextView)bodyAddedView.findViewById(R.id.board_detail_writer_textview);
		textView.setText("글쓴이님");
		WebView webView = (WebView)bodyAddedView.findViewById(R.id.WebView);
		webView.loadDataWithBaseURL(null, article.insertCss()+"본문내용", "text/html", "UTF-8", null);
		Button btn = (Button)bodyAddedView.findViewById(R.id.board_detail_original_link_button);
		btn.setTextSize(14 * AppData.getBodyFontSize());
		
		TextView fontBodySize = (TextView)findViewById(R.id.setting_font_body_textView);
		fontBodySize.setText("폰트크기 : "+Math.round(AppData.getBodyFontSize()*100)+"%");
	}
	
	/**
     * Title 왼쪽 버튼
     * @param v
     */
    public void onLeftTitleClickButton(View v) {
    	finish();
    }
    
    public void onClickTitleFontSmall(View v) {
    	AppData.setTitleFontSize(AppData.getTitleFontSize()-0.01);
    	initScreen();
    }
    
    public void onClickTitleFontBig(View v) {
    	AppData.setTitleFontSize(AppData.getTitleFontSize()+0.01);
    	initScreen();
    }
    
    public void onClickListFontSmall(View v) {
    	AppData.setListFontSize(AppData.getListFontSize()-0.01);
    	initScreen();
    }
    
    public void onClickListFontBig(View v) {
    	AppData.setListFontSize(AppData.getListFontSize()+0.01);
    	initScreen();
    }
    
    public void onClickBodyFontSmall(View v) {
    	AppData.setBodyFontSize(AppData.getBodyFontSize()-0.01);
    	initScreen();
    }
    
    public void onClickBodyFontBig(View v) {
    	AppData.setBodyFontSize(AppData.getBodyFontSize()+0.01);
    	initScreen();
    }
    
    public void finish() {
		super.finish();
    }
}
