package com.zinho.clien.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zinho.clien.R;

/**
 * 파일 업로드 다운로드시 상태 알림
 * 
 * @author 
 * 
 */
public class ProgressbarAlert extends AlertDialog {
	private ProgressBar m_PrograssReceive;
	private TextView m_message;
	private AttachFileData filedata;
	private Handler activityhdlr;
	
	/**
	 * 생성자
	 */
	public ProgressbarAlert(Activity _activity, AttachFileData filedata) {
		super(_activity);
		this.filedata = filedata;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progressbar_alert);
		
		TextView titleTextView = (TextView)this.findViewById(R.id.progressbar_alert_title_textview);
		titleTextView.setText("다운로드");
		m_PrograssReceive = (ProgressBar) findViewById(R.id.progressbar_alert_progressbar);
		m_message = (TextView) findViewById(R.id.progressbar_alert_message);
		
		activityhdlr = new Handler(){
	        public void handleMessage(Message msg) {
	            super.handleMessage(msg);
	            if (filedata.isSuccess()) {
	            	dismiss();
	            	return;
	            }
	            m_message.setText((filedata.getRecieved())+"KB / "+filedata.getSize()+"KB");
	            int per=0;
	            if (filedata.getSize()>0) {
	            	per = (int)((filedata.getRecieved()/(float)filedata.getSize())*100);
	            }
	            
	            if (per>0 && per<101) {
	    			m_PrograssReceive.setProgress(per);
	    		}
	            activityhdlr.sendEmptyMessageDelayed(0, 100);
	        }
	    };
	}
    
    private void startThread() {
        activityhdlr.sendEmptyMessage(100);        
    }
    
    protected void onStart() {
        super.onStart();
        startThread();
    }
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// 업로드 / 다운로드 하던것을 멈춤.
		filedata.downloadCancel();
		
		dismiss();
	}
}