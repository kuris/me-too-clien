package com.zinho.clien.boardlist;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;

import com.zinho.clien.R;
import com.zinho.clien.boarditem.BoardItemListActivity;
import com.zinho.clien.network.NetworkBase;
import com.zinho.clien.network.NetworkBoardList;
import com.zinho.clien.setting.SettingActivity;
import com.zinho.clien.util.AppData;
import com.zinho.clien.util.TitleActivity;
import com.zinho.clien.util.Util;

/**
 * Board 리스트
 * @author zinho79
 *
 */
public class BoardListActivity extends TitleActivity {
    
	private ArrayList<BoardListType> data = new ArrayList<BoardListType>();
	private ListView listView = null;
	private BoardListAdapter mAdapter;
	private static BoardListActivity myClass = null;
	private BoardListType selectedItem = null;
	public int selectedItemNum = -1;
	
    public void onCreate(Bundle savedInstanceState) {
        BoardListActivity.myClass= this;
        super.onCreate(savedInstanceState);
        
        initData();
        setContentView(R.layout.board_list_main);
        setTitle("게시판 선택","");
        mAdapter = new BoardListAdapter(this, data);
        listView = (ListView)findViewById(R.id.main_listview);
        listView.setAdapter(mAdapter);
        getLoginButton();
    }
        
	public static BoardListActivity getInstance() {
		return BoardListActivity.myClass;
	}
    
    public void initData() {

		NetworkBoardList nboard= new NetworkBoardList();
		Vector<BoardListType> vc = nboard.getBoardList();
		data.add(new BoardListType("","게시판 리스트"));
		for (int i=0;i<vc.size();i++) {
			BoardListType item = vc.get(i);
			if (item.isExtraBoard() == false) data.add(item);
		}
		
		data.add(new BoardListType("","소모임 리스트"));
		for (int i=0;i<vc.size();i++) {
			BoardListType item = vc.get(i);
			if (item.isExtraBoard() == true) data.add(item);
		}
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.board_list_menu, menu);
		
    	return true;
    }
    
    public void onResume() {
    	super.onResume();
    	mAdapter.notifyDataSetChanged();
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (NetworkBase.isLogin()) {
			menu.findItem(R.id.board_list_menu_login).setTitle(R.string.board_list_menu_logout);
		} else {
			menu.findItem(R.id.board_list_menu_login).setTitle(R.string.board_list_menu_login);
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
        case R.id.board_list_menu_reload:
        	break;

        
        case R.id.board_list_menu_setting:
        	Util.startActivity(this, SettingActivity.class);
        	break;
        
        case R.id.board_list_menu_login:
        	onClickLoginButton(null);
        	break;
        case R.id.board_list_menu_hide_titlebar:
        	hideTitleBar(!AppData.isHideTitleBar());
        	break;
        }
        return false;
    }
    
    
    
    public void changeBoard(BoardListType item) {
    	Intent intent = new Intent(BoardListActivity.this, BoardItemListActivity.class); 
    	intent.putExtra("board_name", item.getName());
    	intent.putExtra("board_url", item.getUrl());
    	selectedItem = item;
        startActivity(intent); 
    }
    
    public BoardListType getSelectedItem() {
    	return selectedItem;
    }
    
    public void onClickSidebarShow(View v) {
    	
    	Button btn = (Button)v;
    	final View layout = findViewById(R.id.board_list_main_sidebar_detail_layout);
    	if (btn.getText().equals("<")) {
    		btn.setText(">");
            Animation sizeup = AnimationUtils.loadAnimation(this, R.anim.size_up);
            layout.setAnimation(sizeup);
    		layout.setVisibility(View.VISIBLE);
    	} else {
    		btn.setText("<");
    		Animation sizedown = AnimationUtils.loadAnimation(this, R.anim.size_down);
            layout.setAnimation(sizedown);
    		layout.setVisibility(View.GONE);
    	}
    }
    
    public void onClickSettingButton(View v) {
    	Util.startActivity(this, SettingActivity.class);
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
	    		BoardListType item = mAdapter.getItem(selectedItemNum);
	    		if (item.getUrl()!=null && !item.getUrl().equals("")) changeBoard(item);
		    	return true;
		    }
		}
    	
    	if (selectedItemNum<0) selectedItemNum=0;
    	if (selectedItemNum>mAdapter.getCount()-1) selectedItemNum=mAdapter.getCount()-1;
    	listView.setSelection(selectedItemNum);
    	mAdapter.notifyDataSetChanged();
    	
    	return super.dispatchKeyEvent(e);
    }
}
