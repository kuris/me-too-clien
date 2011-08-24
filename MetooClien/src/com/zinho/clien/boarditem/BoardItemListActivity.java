package com.zinho.clien.boarditem;

import java.util.HashMap;
import java.util.Vector;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zinho.clien.R;
import com.zinho.clien.boarddetail.ArticleType;
import com.zinho.clien.boarddetail.BoardDetailActivity;
import com.zinho.clien.boardlist.BoardListActivity;
import com.zinho.clien.boardlist.BoardListType;
import com.zinho.clien.boardwrite.BoardWriteActivity;
import com.zinho.clien.network.NetworkBase;
import com.zinho.clien.network.NetworkBoardDetail;
import com.zinho.clien.network.NetworkBoardItemList;
import com.zinho.clien.setting.SettingActivity;
import com.zinho.clien.util.AppData;
import com.zinho.clien.util.PullToRefreshListView;
import com.zinho.clien.util.PullToRefreshListView.OnRefreshListener;
import com.zinho.clien.util.TitleActivity;
import com.zinho.clien.util.Util;

public class BoardItemListActivity extends TitleActivity {
    
	private PullToRefreshListView listView = null;
	private BoardItemListAdapter mAdapter = null;
	
	public int selectedItemNum = -1;
	private static BoardItemType selectedTitleItem ;
	private static BoardItemListActivity thisClass;
	
	private static int PAGE_NUM = 1;
	private static String boardName = null;
	private String searchMsg = null;
	private String searchType = null;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_item_main);
        BoardItemListActivity.thisClass = this;
        
        Intent intent = this.getIntent();
        String boardName = intent.getStringExtra("board_name");
        if (boardName!=null) BoardItemListActivity.boardName = boardName;
        setTitle(BoardItemListActivity.boardName,"이전");
        
        getLoginButton();	//로그인 버튼 활성화
        getSearchButton();	//Search 버튼 활성화
        
		mAdapter = new BoardItemListAdapter(this);
		listView = (PullToRefreshListView)findViewById(R.id.baard_item_main_listview);
		listView.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
            	Log.d("debug", "refresh...");
            	PAGE_NUM = 1;
            	update();
            }
        });
		listView.setAdapter(mAdapter);
		PAGE_NUM = 1;
		update();
    }
    
	public static BoardItemListActivity getInstance() {
		return thisClass;
	}
	
	public void onResume() {
		super.onResume();
		Button writeButton = getEtcButton();
		if (writeButton != null) {
			writeButton.setBackgroundResource(R.drawable.btn_write_normal);
			writeButton.setText("");
		}
		mAdapter.notifyDataSetChanged();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_OK){
			String searchMsg = data.getStringExtra("search_message");
			String searchType = data.getStringExtra("search_type");
			this.searchMsg = searchMsg;
			this.searchType = searchType;
			searching();
		}
	}
    
    /**
     * Title 왼쪽 버튼
     * @param v
     */
    public void onLeftTitleClickButton(View v) {
    	finish();
    }
    
    public boolean isAutoReload() {
    	if (searchMsg != null && searchMsg.length()>1) return false;
    	return true;
    }
    
    /**
     * 쓰기 버튼 클릭.
     * @param v
     */
    public void onClickEtcButton(View v) {
    	BoardListType selectedItem = BoardListActivity.getInstance().getSelectedItem();
    	if (selectedItem.getKey().equals("news")) {
    		Toast.makeText(this, "해당 개시판에는 글쓰기가 임시 보류입니다.", 1000).show();
    		return;
    	} else {
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	data.put("type", "n");
	    	data.put("bo_table", selectedItem.getKey());
	    	Util.startActivity(this, BoardWriteActivity.class, data);
    	}
    }
    
    public void onClickSearchButton(View v) {
    	onSearchRequested();
	}
    
    public void searching() {
    	setTitle("검색:"+searchMsg,"이전");
		PAGE_NUM=1;
		showLoading(true);
		update();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.board_item_menu, menu);
		
    	return true;
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (NetworkBase.isLogin()) {
			menu.findItem(R.id.board_item_menu_login).setTitle(R.string.board_list_menu_logout);
		} else {
			menu.findItem(R.id.board_item_menu_login).setTitle(R.string.board_list_menu_login);
		}
		
		if (AppData.isHideTitleBar()) {
			menu.findItem(R.id.board_list_menu_hide_titlebar).setTitle(R.string.board_list_menu_showtitlebar);
		} else {
			menu.findItem(R.id.board_list_menu_hide_titlebar).setTitle(R.string.board_list_menu_hidetitlebar);
		}
		
		return true;
    }
    
    public boolean onSearchRequested() {
    	boolean result = super.onSearchRequested();
    	Util.startActivity(this, BoardSearchDialog.class, 1, null);
    	return result;
    }
    
    public void onBackPressed() {
    	if (searchMsg!=null && searchMsg.length()>0) {
    		searchMsg=null;
    		PAGE_NUM=1;
    		showLoading(true);
    		setTitle(BoardItemListActivity.boardName, "이전");
    		update();
    		return;
    	}
    	super.onBackPressed();
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        switch (item.getItemId()) {
        case R.id.board_item_menu_reload:
        	showLoading(true);
        	PAGE_NUM=1;
        	update();
        	break;
        case R.id.board_item_menu_search:
        	onSearchRequested();
        	break;
        	
        case R.id.board_item_menu_login:
        	onClickLoginButton(null);
        	break;
        	
        case R.id.board_item_menu_setting:
        	Util.startActivity(this, SettingActivity.class);
        	break;
        	
        case R.id.board_list_menu_hide_titlebar:
        	hideTitleBar(!AppData.isHideTitleBar());
        	break;
        }
        return false;
    }
    
    public void changeBoard(BoardItemType item) {
    	this.selectedTitleItem = item;
    	Intent intent = new Intent(BoardItemListActivity.this, BoardDetailActivity.class); 
        startActivity(intent); 
    }
    
    public BoardItemType getSelectedItem() {
    	return selectedTitleItem;
    }
    
    private boolean moreLock = false;
    /**
     * 더 보기 클릭시 액션.
     * 
     * @param v
     */
    public void onClickMoreButton(View v) {
    	if (moreLock==true) return;
    	moreLock=true;
    	View moreProgress = null;
    	TextView txtMessage = null;
    	if (v instanceof LinearLayout) {
    		moreProgress = v.findViewById(R.id.board_item_more_progressBar);
    		txtMessage = (TextView)v.findViewById(R.id.board_item_more_updateTextView);
    	} else {
    		moreProgress = findViewById(R.id.board_item_more_progressBar);
    		txtMessage = (TextView)findViewById(R.id.board_item_more_updateTextView);
    	}
    	if (moreProgress != null) moreProgress.setVisibility(View.VISIBLE);
    	if (txtMessage != null) txtMessage.setText("업데이트 하는 중");
    	PAGE_NUM ++;
    	update();
    }
    
    public boolean dispatchKeyEvent(KeyEvent e) {
    	if(e.getAction() == KeyEvent.ACTION_DOWN) {
			if (e.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP) {
	    		selectedItemNum--;
	    		if (selectedItemNum<0) selectedItemNum=0;
	    		return true;
	    	} else if (e.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN) {
	    		selectedItemNum++;
	    		return true;
	    	} else if (e.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
				changeBoard(mAdapter.getItem(selectedItemNum));

		    	return true;
		    }
		}
    	
    	if (selectedItemNum<0) selectedItemNum=0;
    	if (selectedItemNum>mAdapter.getCount()-1) selectedItemNum=mAdapter.getCount()-1;
    	listView.setSelection(selectedItemNum+1);
    	mAdapter.notifyDataSetChanged();
    	
    	return super.dispatchKeyEvent(e);
    }
    
    public void update() {
    	if (findViewById(R.id.board_item_main_layout).getVisibility()!=View.VISIBLE) {
    		showLoading(true);
    	}
    	if (BoardListActivity.getInstance()==null) return;
		BoardListType selectedItem = BoardListActivity.getInstance().getSelectedItem();
		if (selectedItem ==null) return;

		if (PAGE_NUM==1) mAdapter.clear();
		mAdapter.notifyDataSetChanged();
		
		if (selectedItem.getKey().equals("image")) {
			new GetPicData().execute();
		} else {
			new GetData().execute();
		}
    }
    
    public void updateEnd() {
    	showLoading(false);
		findViewById(R.id.board_item_main_layout).setVisibility(View.VISIBLE);
		TextView txtMessage = (TextView)findViewById(R.id.board_item_more_updateTextView);
		if (txtMessage != null) txtMessage.setText("더이상 게시물이 없습니다.");
		if (PAGE_NUM==1) mAdapter.clear();
		else {
			View progress = findViewById(R.id.board_item_more_progressBar);
			if (progress!=null) progress.setVisibility(View.INVISIBLE);
		}
		moreLock=false;
    }
    
    public void updateError() {
    	showLoading(false);
    	if (PAGE_NUM>2) PAGE_NUM--;
		findViewById(R.id.board_item_main_layout).setVisibility(View.VISIBLE);
		TextView txtMessage = (TextView)findViewById(R.id.board_item_more_updateTextView);
		if (txtMessage != null) txtMessage.setText("Network오류 : 터치하면 Reload합니다.");
		if (PAGE_NUM==1) mAdapter.clear();
		else {
			View progress = findViewById(R.id.board_item_more_progressBar);
			if (progress!=null) progress.setVisibility(View.INVISIBLE);
		}
		moreLock=false;
    }
    
    private class GetData extends AsyncTask<String, Integer, Vector<BoardItemType>> {
		@Override
		protected Vector<BoardItemType> doInBackground(String... arg0) {			
			BoardListType selectedItem = BoardListActivity.getInstance().getSelectedItem();
			NetworkBoardItemList nboard= new NetworkBoardItemList();
			Vector<BoardItemType> vc = null;
			try {
				vc = nboard.getBoardItemList(selectedItem, PAGE_NUM, searchMsg, searchType);
			} catch (Exception e) {
				Log.w("debug", "",e);
			}
			return vc;
		}

		@Override
		protected void onPostExecute(Vector<BoardItemType> tempData) {
			if (tempData == null) {
				updateError();
				if (PAGE_NUM==1) listView.onRefreshComplete();
				return;
			}
			updateEnd();
			
			for (int i=0;i<tempData.size();i++) {
	    		mAdapter.add(tempData.get(i));
	    	}
	    	mAdapter.notifyDataSetChanged();
			if (PAGE_NUM==1) listView.onRefreshComplete();
		}
	}
    
    private class GetPicData extends AsyncTask<String, Integer, Vector<ArticleType>> {
		@Override
		protected Vector<ArticleType> doInBackground(String... arg0) {
			BoardListType selectedItem = BoardListActivity.getInstance().getSelectedItem();
			NetworkBoardDetail detaildata = new NetworkBoardDetail();
			Vector<ArticleType> vc = null;
			try {
				vc = detaildata.getPicData(selectedItem, PAGE_NUM);
			} catch (Exception e) {
				Log.e("debug","",e);
			}
			return vc;
		}

		@Override
		protected void onPostExecute(Vector<ArticleType> tempData) {
			if (tempData == null) {
				updateError();
				if (PAGE_NUM==1) listView.onRefreshComplete();
				return;				
			}
			updateEnd();
	    	
	    	for (int i=0;i<tempData.size();i++) {
	    		mAdapter.add(tempData.get(i));
	    	}
	    	mAdapter.notifyDataSetChanged();
			if (PAGE_NUM==1) listView.onRefreshComplete();
		}
	}
}
