package com.zinho.clien.boarditem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.zinho.clien.R;

public class BoardSearchDialog extends Activity {

	private EditText searchEditText = null;
	private Button searchTypeButton = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board_search);
		searchEditText = (EditText)findViewById(R.id.board_search_editText);
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent event) {
				if ((paramInt == EditorInfo.IME_ACTION_DONE) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					if(searchEditText.getText().toString().length() >0){          
						onClickSearchButton(null);
					}else{
						Toast.makeText(getApplicationContext(), "검색어를 입력하세요.", 1000).show();
					}
				}

				return false;
			}
		});
		
		searchTypeButton = (Button)findViewById(R.id.board_search_type_button);
		searchTypeButton.setTag("wr_subject");
	}
	
	public void onClickSearchButton(View v) {
		
		String searchStr = searchEditText.getText().toString();
		Intent intent = new Intent();
		intent.putExtra("search_type", searchTypeButton.getTag().toString());
		intent.putExtra("search_message", searchStr);
		setResult(RESULT_OK, intent);
		finish();
		
	}
	
	public void onClickSearchTypeButton(View v) {
		final String[] searchTypes = new String[] {"제목", "내용", "제목+내용","회원아이디","회원아이디(코)","이름", "이름(코)"};
		final String[] searchTypesKey = new String[] {"wr_subject","wr_content","wr_subject||wr_content","mb_id,1","mb_id,0","wr_name,1","wr_name,0"};
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("검색 타입");
        dialog.setSingleChoiceItems(searchTypes, 0, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				searchTypeButton.setText(searchTypes[which]);
				searchTypeButton.setTag(searchTypesKey[which]);
			}
		});
        dialog.show();
	}
}
