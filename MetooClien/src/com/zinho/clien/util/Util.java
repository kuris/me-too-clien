package com.zinho.clien.util;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.zinho.clien.boardlist.BoardListActivity;
import com.zinho.clien.network.NetworkBase;

public class Util {
	
	public static String converClianURL(String url) {
		if (url.indexOf("..")>-1) {
			return NetworkBase.CLIEN_URL + "cs2"+url.replaceAll("\\.\\.", "");
		} else {
			return url;
		}
	}
	
	public static String[] getLinks(String str) {
		return null;
	}
	
	public static boolean hasObject(String str) {
		if (str.toLowerCase().indexOf("<object")>-1) return true;
		if (str.toLowerCase().indexOf("<embed")>-1) return true;
		return false;
	}
	
	public static String getShortStr(String str, int leng) {
		if (str.length()<leng) return str;
		return ".."+str.substring(str.length()-leng, str.length());
	}
	
	public static String getShortLink(String content, int linkLength) {
		int img1 = content.indexOf("<a");
		if (img1==-1) {
			img1 = content.indexOf("<A");
			if (img1==-1) return content;
		}
		int img2 = content.indexOf(">",img1+2)+1;
		if (img2!=-1) {
			int img3 = content.indexOf("<A", img2);
			if (img3==-1) {
				img3 = content.indexOf("<a", img2);
			}
			int img3_1 = content.indexOf("</", img2);
			if (img3 != -1 && img3_1>img3) {
				int img4 = content.indexOf(">", img3)+1;
				content = content.substring(0, img2) + content.substring(img4, content.length());
				content = content.replaceAll("</A>", "");
			}
			img3 = content.indexOf("<", img2);
			String txtLength = content.substring(img2, img3);
			if (txtLength.length()>linkLength) {
				return content.substring(0,img2) +
				 content.substring(img2,img2+linkLength)+"..."+
				 content.substring(img3);
			}
		}
		return content;
	}
	
	public static Bitmap getBitmapImage(String url, boolean downSampling) {
		Bitmap bm = null;
		HttpURLConnection conn = null;
		BufferedInputStream bis = null;
		if (url==null) return null;
		try {
			URL imageURL = new URL(url);
			conn = (HttpURLConnection) imageURL.openConnection();
			bis = new BufferedInputStream(conn.getInputStream(), 1024);
			if (downSampling) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				bm = BitmapFactory.decodeStream(bis, null, options);
			} else {
				bm = BitmapFactory.decodeStream(bis);
			}
		} catch (Exception e) {
			Log.e("debug", e.toString(), e);
		} finally {
			try {bis.close();} catch (Exception e) {}
			try {conn.disconnect();} catch (Exception e) {}
		}
		return bm;
	}
	
	/** Get Bitmap's Width **/
	 public static int getBitmapOfWidth( String fileName ){
	    try {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(fileName, options);
	        return options.outWidth;
	    } catch(Exception e) {
	    return 0;
	    }
	 }
	 
	 /** Get Bitmap's height **/
	 public static int getBitmapOfHeight( String fileName ){
	  
	    try {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(fileName, options);
	  
	        return options.outHeight;
	    } catch(Exception e) {
	        return 0;
	   }
	 }
	
	public static OnTouchListener getTouchChangeColor(final View layout) {
		return new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					layout.setBackgroundColor(ZStyle.ListBackgroundSelectedColor);
				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					layout.setBackgroundColor(ZStyle.ListBackgroundColor);
				} else if (arg1.getAction() == MotionEvent.ACTION_CANCEL) {
					layout.setBackgroundColor(ZStyle.ListBackgroundColor);
				}
				return false;
			}
		};
	}
	
	public static String getTodayTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd a HH:mm");
		return dateFormat.format(new Date());
	}
	
	public static String getUriToPath(Activity activity, Uri uri) {
		Cursor c = activity.getContentResolver().query(uri, null,null,null,null);
		c.moveToNext();
		String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
		c.close();
		return path;
	}
	
	public static int parseDPItoPx(int dpi) {
		Activity activity = BoardListActivity.getInstance();
		DisplayMetrics outMetrics = new DisplayMetrics();
		if (activity != null) {
			activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
			float dpip = outMetrics.density;
			return (int)(dpi*dpip);
		}
		
		return (int)(dpi*1.5);
	}
	
	public static void startActivity(Activity _context, Class<?> className) {
		Util.startActivity(_context, className, null);
	}
	
	public static void startActivity(Activity _context, Class<?> className, Object data) {
		String packageName = _context.getPackageName();
		Intent intent = null;
		if (data instanceof Intent) {
			intent = (Intent)data;
		} else {
			intent= new Intent();
		
			if (data != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> hdata = (HashMap<String, String>)data;
				Iterator<Entry<String, String>> iterator = hdata.entrySet().iterator();
				  while (iterator.hasNext()) {
				   Entry<String, String> entry = iterator.next();
				   intent.putExtra(entry.getKey() , entry.getValue());
				  }
			}
			
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		intent.setComponent(new ComponentName(packageName, className.getName()));
		_context.startActivity(intent);
	}
	
	public static void startActivity(Activity _context, Class<?> className, int requestCode, HashMap<String,String> data) {
		String packageName = _context.getPackageName();
		Intent intent = new Intent();
		
		if (data != null) {
			Iterator<Entry<String, String>> iterator = data.entrySet().iterator();
			  while (iterator.hasNext()) {
			   Entry<String, String> entry = iterator.next();
			   intent.putExtra(entry.getKey() , entry.getValue());
			  }
		}
		
		intent.setComponent(new ComponentName(packageName, className.getName()));
		_context.startActivityForResult(intent, requestCode);
	}
	
	private static ArrayList<Activity> activityList = new ArrayList<Activity>();
	public static void addActivity(Activity activity) {
		if (activityList.contains(activity)) {
			activityList.remove(activity);
		}
		activityList.add(activity);
	}
	
	public static Activity getActivity(int i) {
		return activityList.get(i);
	}
	
	public static void removeActivity(Activity activity) {
		activityList.remove(activity);
	}
	
	public static void removeAllActivity() {
		for (int i=activityList.size()-1;i>=0;i--) {
			activityList.get(i).finish();
		}
	}

	public static void setDimBehind(Activity _activity) {
		_activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	private static Context context;
	public static String getSharedData(Context context, String _key, String _defaultData) {
		if (Util.context == null) Util.context = context;
		SharedPreferences pref = null;
		try {
			pref=Util.context.getSharedPreferences("com.zinho.clien", Activity.MODE_PRIVATE);
		} catch (Exception e) {
			Log.w("debug", e.toString(), e);
			return _defaultData;
		}
		if (pref==null) return _defaultData;
		return pref.getString(_key, _defaultData);
	}

	public static void setSharedData(Context context, String _key, String _data) {
		if (Util.context == null) Util.context = context;
		SharedPreferences p = Util.context.getSharedPreferences("com.zinho.clien", Activity.MODE_PRIVATE);
		SharedPreferences.Editor e = p.edit();
		e.putString(_key, _data);
		e.commit();
	}
	
	public static String removeTag(String html) {
		if (html==null) return "";
		return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", ""); 
	}
	
	public static String removeATag(String html) {
		int a1 = html.indexOf("<a");
		if (a1==-1) return html;
		int a2 = html.indexOf(">",a1+1);
		html = html.substring(0, a1) + html.substring(a2+1);
		a1 = html.indexOf("</a>");
		if (a1==-1) return html;
		html = html.substring(0,a1)+ html.substring(a1+4);
		return html;
	}
	
	public static void ApplicationExit() {
		Util.removeAllActivity();
	}
}