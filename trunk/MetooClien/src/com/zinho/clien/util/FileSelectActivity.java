package com.zinho.clien.util;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zinho.clien.R;

public class FileSelectActivity extends TitleActivity {
	
	private String filepath = "/sdcard";
	private ArrayList<File> filedata = new ArrayList<File>();
	private FileSelectAdapter adapter = null;
	private LayoutInflater mInflater;
	
		
	/**
	 * 화면 구성
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.file_select);
		setTitleName(filepath);
		
		mInflater = LayoutInflater.from(this);
		adapter = new FileSelectAdapter();
		
		ListView listview = (ListView)this.findViewById(R.id.file_select_listview);
		listview.setAdapter(adapter);
		
		initRadioList();
	}
	
	/**
	 * 이전 버튼
	 * 
	 * @param target
	 */
	public void onTitleClickButton_01(View target) {
		if (filepath.equals("/sdcard")) {
			finish();
			return;
		}
		filepath = filepath.substring(0, filepath.lastIndexOf("/"));
		
		initRadioList();
		setTitleName(filepath);
		adapter.notifyDataSetChanged();
	}

	
	/**
	 * Back 버튼이 눌린경우 이전 폴더로 이동.
	 */
	public void onBackPressed() {
		onTitleClickButton_01(null);
	}
	
	
	private void setTitleName(String name) {
		setTitle(name, "이전");
	}
	
	/**
	 * 파일의 리스트를 작성한다.
	 * 
	 */
	private void initRadioList() {
		String[] filelist = new File(filepath).list();
		filedata.clear();
		if (filelist==null) return;//외장 저장장치가 없거나 컴튜터와 연결된 상황
		
		//폴더먼저
		for (int i=0;i<filelist.length;i++) {
			File target = new File(filepath+"/"+filelist[i]);
			if (target.isDirectory() && !target.isHidden()) {
				filedata.add(target);
			}
		}
		
		//파일
		for (int i=0;i<filelist.length;i++) {
			File target = new File(filepath+"/"+filelist[i]);
			if (!target.isDirectory() && !target.isHidden()) {
				filedata.add(target);
			}
		}
	}
	
	/**
	 * 버튼이 눌린 경우
	 * @param v
	 */
	public void onClickButton_ListViewSelectedItem(View v) {
		Button btn = (Button) v;
		File target = (File)btn.getTag();
		if (target.isDirectory()) {
			try {
				this.filepath = target.getPath();
				initRadioList();
				setTitleName(filepath);
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				Log.e("fileselect", e.toString(), e);
			}
			return;
		}
		
		Intent intent = new Intent();
		intent.putExtra("filename", target.getAbsolutePath());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	/**
	 * 파일 선택 리스트 어덥터
	 * @author zinho79
	 *
	 */
	private class FileSelectAdapter extends BaseAdapter {

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
		
		public View getView(int p, View view, ViewGroup parent) {
			view = mInflater.inflate(R.layout.file_select_item, null);
			ImageView iconImage = (ImageView)view.findViewById(R.id.file_select_item_icon_image);
			TextView fileNameTextView = (TextView)view.findViewById(R.id.file_select_item_filename);
			TextView fileSizeTextView = (TextView)view.findViewById(R.id.file_select_item_filesize);
			Button fileButton = (Button)view.findViewById(R.id.file_select_item_backgroundButton);
			
			File target = filedata.get(p);
			
			fileButton.setTag(target);
			
			if (target.isDirectory() == false) {
				iconImage.setImageResource(R.drawable.icon_clip);
				fileSizeTextView.setText(FileUtil.getKBDisplaySizeString(target.length()));
			} else {
				fileSizeTextView.setText("");
			}
			fileNameTextView.setText(target.getName());
			
			return view;
		}
		
		public int getCount() {
			return filedata.size();
		}
	}
}
