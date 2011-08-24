package com.zinho.clien.boardwrite;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zinho.clien.R;
import com.zinho.clien.boarditem.BoardItemType;
import com.zinho.clien.network.NetworkBase;
import com.zinho.clien.util.FileSelectActivity;
import com.zinho.clien.util.FileUtil;
import com.zinho.clien.util.TitleActivity;
import com.zinho.clien.util.Util;

public class BoardWriteActivity extends TitleActivity {
	
	private static BoardWriteActivity thisClass = null;
	private String updateData = null;
	
	private BoardItemType boardItem;
	private final static int PICK_FROM_GELLARY=1;
	private final static int PICK_FROM_FILE=2;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_write);
        thisClass = this;
        setTitle("글쓰기","이전");
        Intent intent = this.getIntent();
        boardItem = new BoardItemType();
        if (intent.getStringExtra("type").equals("u")) {
        	updateData = intent.getStringExtra("data");
        	boardItem.setData(updateData);
        } else {
        	boardItem.setBo_table(intent.getStringExtra("bo_table"));
        }
        
        getLoginButton();	//로그인 버튼 활성화
        findViewById(R.id.board_write_ca_select).setEnabled(false);
        new GetData(boardItem.getBo_table()).execute();
    }
    
    public void finish() {
    	// TODO 화면을 종료하기전에 글쓰기 중이었다면 한번더 물어볼것.
    	super.finish();
    }
    
    public void onLeftTitleClickButton(View v) {
    	finish();
    }
    
    public void onClickCancelButton(View v) {
    	finish();
    } 
    
    public void onClickAttachFileButton(View v) {
		LinearLayout layout = (LinearLayout)this.findViewById(R.id.board_write_fileattach_layout);
		if (layout.getChildCount()>2) {
			Toast.makeText(this, "최대 업로드 파일 갯수 : 2개", 1000).show();
			return;
		}
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("타입선택");
        dialog.setSingleChoiceItems(new String[] {"갤러리", "파일"}, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (which==0) {
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
					startActivityForResult(intent, PICK_FROM_GELLARY);
				} else {
					Util.startActivity(BoardWriteActivity.this, FileSelectActivity.class, PICK_FROM_FILE, null);
				}
			}
		});
        dialog.show();
    	
    }
    
    public void onClickSendButton(View v) {
    	EditText titleText = (EditText)findViewById(R.id.board_write_title_edittext);
    	EditText contentsText = (EditText)findViewById(R.id.board_write_content_edittext);
    	
    	// 입력값 검증
    	if (isValidation(titleText) == false) return;
    	if (isValidation(contentsText) == false) return;
    	
    	final Button btn = (Button)findViewById(R.id.board_write_ca_select);
    	if (btn.isEnabled() && btn.getText().toString().equals("미선택")) {
    		Toast.makeText(this, "분류를 선택해주세요.", 1000).show();
    		return;
    	}
    	String ca_name = btn.getText().toString();
    	if (ca_name.equals("미선택")) ca_name="";
    	
    	HashMap<String, String> data = new HashMap<String, String>();
    	data.put("wr_subject", titleText.getText().toString());
    	String sign = "\n\n:: 나도끌량ⓣ ::";
    	if (contentsText.getText().toString().indexOf(sign)>-1) {
    		sign="";
    	}
    	
		data.put("wr_content", contentsText.getText().toString()+sign);	//내용
    	data.put("w", boardItem.getW());	//
    	data.put("bo_table", boardItem.getBo_table());
    	data.put("html","html2");
    	data.put("ca_name", ca_name); //분류 확인하기
    	data.put("wr_id" , boardItem.getWrId());
    	data.put("comment_id", ""); // 코멘트 아이디가 넘어오면 답변, 수정
    	data.put("sca", "");
    	data.put("sfl", "");
    	data.put("stx", "");
    	data.put("spt", "");
    	data.put("sst", "");
    	data.put("sod", "");
    	data.put("page", "");
    	data.put("webedit", "1"); //0 사용 , 1 사용안함
    	data.put("wr_ccl_by", ""); //by 사용, 무 사용안함
    	data.put("wr_ccl_nc", ""); //nc 사용불가, 무 사용가능
    	data.put("wr_ccl_nd",""); //nd 변경불가, sa 동일조건 변경가능, 무 변경가능
    	data.put("wr_link1", ""); //링크1
    	data.put("wr_link2", ""); //링크2
    	data.put("bf_file", ""); // 파일
    	showLoading(true);
    	
    	//키보드 숨기기
    	InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	mgr.hideSoftInputFromWindow(contentsText.getWindowToken(), 0);
    	
    	// 전송하기
    	showLoading(true);
    	
    	ArrayList<File> attachFile = new ArrayList<File>();
    	int filesize = 0;
    	LinearLayout layout = (LinearLayout)this.findViewById(R.id.board_write_fileattach_layout);
    	for (int i=0;i<layout.getChildCount();i++) {
    		File file = new File(layout.getChildAt(i).getTag().toString());
    		attachFile.add(file);
    		filesize += file.length();
    	}
    	if (filesize>1*1024*1024) {
    		Toast.makeText(this, "최대 업로드는 1M입니다.", 1000).show();
    		return;
    	}
    	if (attachFile.size()>0) {
    		new SendData(data, attachFile.toArray(new File[]{})).execute();
    	} else {
    		new SendData(data, null).execute();
    	}
    }
    
    /**
     * 입력값을 확인한다. 잘못된 값이나 금지어를 체크한다.
     * 
     * @param v
     * @return
     */
    public boolean isValidation(EditText v) {
    	if (v.getText().toString().length()<1) return false;
    	return true;
    }
    
    public void initSelectBox(String result) {
    	NetworkWriteCaList network = new NetworkWriteCaList(result);
    	final ArrayList<String> arrayList = network.getCaData();
    	final Button btn = (Button)findViewById(R.id.board_write_ca_select);
    	if (arrayList==null) {
    		btn.setEnabled(false);
    	} else {
    		btn.setEnabled(true);
    	}
    	
    	btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		    	BoardWriteAlert caAlert = new BoardWriteAlert(thisClass, arrayList) {
		    		public void onClickOkButton() {
		    			btn.setText(getSelectedItem());
		    		}
		    	};
		    	caAlert.show();
			}
		});
    	
    	// 제목
    	String subject = network.getSubject();
//    	Log.d("debug",subject);
    	if (subject != null) {
    		((TextView)findViewById(R.id.board_write_title_edittext)).setText(subject);
    	}
    	
    	// 내용
    	String contents = network.getContents();
//    	Log.d("debug", contents);
    	if (contents != null) {
    		((TextView)findViewById(R.id.board_write_content_edittext)).setText(contents);
    	}
    	
    	String selectedCaName = network.getSelectedCaName();
    	if (selectedCaName != null && selectedCaName.length()>1) {
    		btn.setText(selectedCaName);
    	}
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		String filename = null;
    		if (requestCode==PICK_FROM_GELLARY) {
    			filename = Util.getUriToPath(this, data.getData());
    		} else if (requestCode==PICK_FROM_FILE) {
	    		filename  = data.getStringExtra("filename");
    		}
    		
    		if (filename!=null) {
    			filename = FileUtil.converFile(filename, this);
    			LinearLayout layout = (LinearLayout)this.findViewById(R.id.board_write_fileattach_layout);
    			
    			LayoutInflater vi = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			View v = vi.inflate(R.layout.board_write_attachlist, null);
    			TextView vTextView = (TextView)v.findViewById(R.id.board_write_attachfile_textview);
    			vTextView.setText(Util.getShortStr(filename,30));
    			Button vBtn = (Button)v.findViewById(R.id.board_write_attachfile_button);
    			vBtn.setTag(filename);
    			vBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						LinearLayout layout = (LinearLayout)thisClass.findViewById(R.id.board_write_fileattach_layout);
						for (int i=0;i<layout.getChildCount();i++) {
							if (layout.getChildAt(i).getTag().equals(arg0.getTag())) {
								layout.removeViewAt(i);
							}
						}
					}
				});
    			v.setTag(filename);
    			layout.addView(v);
    		}
    	}
    }
    
    public void onClickCaSelect(View v) {
    	
    }
    
    private class SendData extends AsyncTask<String, Integer, String> {
    	HashMap<String, String> data;
    	File[] files;
    	
    	public SendData(HashMap<String, String> data, File[] files) {
    		this.data = data;
    		this.files = files;
    		
    	}
    	
		protected String doInBackground(String... arg0) {
			String url = NetworkBase.CLIEN_URL+"cs2/bbs/write_update.php";
			String result = null;
			try {
				result = NetworkBase.getData(url, data, files);
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

			showLoading(false);
			Toast.makeText(thisClass, "글쓰기 완료", 1000).show();
			finish();
		}
	}
    
    /**
     * 글쓰기를 위한 기본 정보를 가져옴.
     * @author zinho79
     */
    private class GetData extends AsyncTask<String, Integer, String> {
    	String boardKey = null;
    	
    	public GetData(String boardKey) {
    		this.boardKey = boardKey;
    	}
    	
		protected String doInBackground(String... arg0) {
			String url = null;
			if (boardItem.getW().equals("u")) {
				url = NetworkBase.CLIEN_URL+"cs2/bbs/write.php?"+updateData;
			} else {
				url = NetworkBase.CLIEN_URL+"cs2/bbs/write.php?bo_table="+boardKey;
			}
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
			initSelectBox(result);
			showLoading(false);
		}
	}
}
