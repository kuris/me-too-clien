package com.zinho.clien.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class AttachFileData {
	
	public static final String TEMP_SAVE = "__temp__save__";
	public static final String TEMP_FOLDER = Environment.getExternalStorageDirectory()+"/data/com.nate.mail/";
	
	private Context context;
	
	private int recievedFilesize = 0;
	private int filesize = 0;
	
	public boolean filecancel = false;
	public boolean mediaScannerFinish = false;
	private MediaScannerConnection mScanner;
	private Uri savedFileUri = null;
	
	public AttachFileData(Context context, String mboxid, String msgid, String fileid) {
		this.context = context;
	}
	
	public void downloadCancel() {
		filecancel= true;
	}
	
	/**
	 * 다운로드 받은 파일을 저장함.
	 * 
	 * @param filename
	 * @return
	 */
	public boolean saveFile(String filename, boolean isTemp) {
		final String fullPath;
		
		String download_url = "";
		
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		try {
			
			
			HttpURLConnection conn = (HttpURLConnection)new URL(download_url).openConnection();
			conn.setDoInput(true);
            conn.connect();

			bis = new BufferedInputStream(conn.getInputStream());
			if (isTemp) {
					
				File tmpFolder = new File(AttachFileData.TEMP_FOLDER);
				if (!tmpFolder.isDirectory()) {
					tmpFolder.mkdirs();
				}
				fullPath = AttachFileData.TEMP_FOLDER+filename;
				fos = new FileOutputStream(fullPath);
			} else {
				fullPath = filename;
				fos = new FileOutputStream(fullPath);
				
			}
			
			recievedFilesize = 0;
			int nReadByte = 0;

			while ((nReadByte = bis.read()) != -1) {
				if (filecancel==true) {
					return false;
				}
				fos.write(nReadByte);
				
				recievedFilesize ++;
			}
			
		} catch (Exception e) {
			Log.e("error","", e);
			return false;
		} finally {
			try {bis.close();} catch (Exception e) {}
			try {fos.close();} catch (Exception e) {}
		}
		
		mediaScannerFinish = false;
		mScanner = new MediaScannerConnection(context, 
			new MediaScannerConnection.MediaScannerConnectionClient() { 
		        public void onMediaScannerConnected() {
		        	File file = new File(fullPath);
		        	mScanner.scanFile(file.getAbsolutePath(), null); 
		        } 
		        public void onScanCompleted(String path, Uri uri) {
		        	try {
			        	savedFileUri = uri;
			        	mediaScannerFinish = true;
			        	Log.i("debug", "onScanCompleted(" + path + ", " + uri + ")");
			        	mScanner.disconnect();
		        	} catch (Exception e) {
		        		Log.e("debug", "", e);
		        	}
		        } 
			});
		mScanner.connect(); 
		
		// 미디어 스캔이 끝날때까지 기다린다.
		for(int i=0;i<100;i++) {
			try { Thread.sleep(100);}catch (Exception e) {}
			if (mediaScannerFinish) break;
		}
		recievedFilesize = -1;	// End.
		return true;
	}
	
	
	/**
	 * 이미지 파일의 경우 Uri 객체를 반환하여 갤러리에 바로 표현한다.
	 * 
	 * @return
	 */
	public Uri getSavedFileUri() {
		return savedFileUri;
	}
	
	public int getSize() {
		if (filesize<0) return 0;
		return filesize;
	}
	
	public boolean isSuccess() {
		return filesize==-1;
	}
	
	public int getRecieved() {
		return (int)(this.recievedFilesize/1024.0);
	}
	
}
