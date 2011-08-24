package com.zinho.clien.util;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class FileUtil {

	public static final int KB = 1024;
	public static final int MB = KB * KB;
	public static final int GB = MB * KB;

	public static String getMBDisplaySizeString(double _size) {
		double size = _size;
		String display_size="";
		
		if (size > GB) {
			display_size = String.format("%.1fGB", (double) size / GB);
		}
		else {
			display_size = String.format("%.1fMB", (double) size / MB);
		}

		display_size = display_size.replaceAll("\\.0","");	// .0 으로 끝나는 단위는 삭제함.
		
		return display_size;
	}
	
	public static String getKBDisplaySizeString(double _size) {
		double size = _size;
		String display_size="";
		
		if (size > GB) {
			display_size = String.format("%.1fGB", (double) size / GB);
		}
		else if (size < GB && size > MB) {
			display_size = String.format("%.1fMB", (double) size / MB);
		} else { 
			display_size = String.format("%.1fKB", (double) size / KB);
		}

		display_size = display_size.replaceAll("\\.0","");	// .0 으로 끝나는 단위는 삭제함.		
		return display_size;
	}
	
	public static int getByte(String filesize) {
		int result = 0, size=1;
		filesize = filesize.trim().toLowerCase();
		if (filesize.indexOf("k")>-1) {
			size = 1024;
			result = (int)(Double.parseDouble(filesize.substring(0,filesize.indexOf("k")))*size);
		} else if (filesize.indexOf("m")>-1) {
			size = 1024*1024;
			result = (int)(Double.parseDouble(filesize.substring(0,filesize.indexOf("m")))*size);
		} else if (filesize.indexOf("g")>-1) {
			size = 1024*1024*1024;
			result = (int)(Double.parseDouble(filesize.substring(0,filesize.indexOf("g")))*size);
		}
		return result;
	}

	
	/**
	 * 파일의 MineType을 결정한다.
	 *  
	 * @param filename
	 * @return
	 */
	public static String getMimeType(String filename) {
		
		MimeTypeMap mtm = MimeTypeMap.getSingleton(); // mime type 추출용
		String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase(); // 파일 확장자 추출
		String mimeType = mtm.getMimeTypeFromExtension(fileExtension);
		if (mimeType != null) return mimeType;

		
		File f = new File(filename);
		String sf = f.getName().substring((int)f.getName().length()-4).toLowerCase();
		if (sf.equals(".gif") || sf.equals(".png") || sf.equals(".jpg") || sf.equals(".bmp")) return "image/"+sf.substring(1);
		if (sf.equals("jpeg")) return "image/"+sf;
		if (sf.equals(".doc") || sf.equals("docx") || sf.equals(".dot"))	return "application/msword";
		if (sf.equals(".xls") || sf.equals("xlsx")|| sf.equals(".csv"))	return "application/msexcel";
		if (sf.equals(".ppt") || sf.equals("pptx"))	return "application/vnd.ms-powerpoint";
		if (sf.equals(".pdf")) return "application/pdf";	
		if (sf.equals(".txt") || sf.equals("text")) return "text/plain";
		if (sf.equals(".htm") || sf.equals("html")) return "text/html";
		if (sf.equals(".apk")) return "application/vnd.android.package-archive";
		
		return null;
	}
	
	public static String converFile(String filename, Activity activity) {
		File file = new File(filename);
		try {
			if (filename.toLowerCase().indexOf(".jpg")>-1 && file.length()>300*1024) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				Bitmap src = BitmapFactory.decodeFile(filename, options);
				String tmpFile = activity.getCacheDir() +"/"+ file.getName().substring(0,file.getName().length()-4)+"_down.jpg";
				src.compress(CompressFormat.JPEG, 97, new FileOutputStream(tmpFile));
				return tmpFile;
			}
		}catch (Exception e) {
			Log.e("debug", "",e);
			return filename;
		}
		return filename;
	}
}
