package com.zinho.clien.util;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class GetWriterPicData extends AsyncTask<String, Integer, Bitmap> {

	private String url;
	private ImageView iv;
	private static Hashtable<String, Bitmap> backup = new Hashtable<String,Bitmap>();

	public GetWriterPicData(String url, ImageView iv) {
		this.url = url;
		this.iv = iv;
	}
	
	public static void clearBackup() {
		backup.clear();
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {
		Bitmap bitmap = backup.get(url);
		if (bitmap==null) {
			return Util.getBitmapImage(url, false);
		} else {
			return bitmap;
		}
	}

	@Override
	protected void onPostExecute(Bitmap tempData) {
		if (tempData!=null) {
			if (backup.containsKey(url)==false) backup.put(url, tempData);
			tempData.setDensity((int)(160 / AppData.getListFontSize()));
			iv.setImageBitmap(tempData);
		}
	}
}