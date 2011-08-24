package com.zinho.clien.boarditem;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zinho.clien.R;
import com.zinho.clien.boarddetail.ArticleType;
import com.zinho.clien.network.NetworkBase;
import com.zinho.clien.network.NetworkBoardList;
import com.zinho.clien.util.AppData;
import com.zinho.clien.util.GetWriterPicData;
import com.zinho.clien.util.Util;
import com.zinho.clien.util.ZStyle;

public class BoardItemListAdapter extends BaseAdapter {

	private BoardItemListActivity pActivity;
	private ArrayList<BoardItemType> items;
	private ArrayList<ArticleType> itemsPic;
	private View bufferView=null;
	private LayoutInflater vi;
	
	public BoardItemListAdapter(BoardItemListActivity pActivity) {
		this.pActivity = pActivity;
		this.items = new ArrayList<BoardItemType>();
		this.itemsPic = new ArrayList<ArticleType>();
		vi = (LayoutInflater) pActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		GetWriterPicData.clearBackup();
	}

	public View getView(int position, View v, ViewGroup parent) {
		if (itemsPic.size() > 0) return getViewPic(position, v, parent);

		RowHolder rowHolder = null;
		
		
		if (position == getCount() - 2) {
			View bottomMoreView = vi.inflate(R.layout.board_item_more, null);
			if (position > 0 && pActivity.isAutoReload()) {
				pActivity.onClickMoreButton(bottomMoreView);
			}
			return bottomMoreView;
		} else if (position == getCount()-1) {
			//공백 추가.
			int height = pActivity.getWindowManager().getDefaultDisplay().getHeight();
			height -= Util.parseDPItoPx(((getCount()-2)*55)+40+55+20);
			
			if (height<1) height=0;
			
			View bottomView = vi.inflate(R.layout.board_item_empty, null);
			LinearLayout layout = (LinearLayout)bottomView.findViewById(R.id.board_item_empty_center_layout);
			layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height));
			return bottomView;
		} 
		final BoardItemType p = this.getItem(position);
		if (bufferView == null) {
			v = vi.inflate(R.layout.board_item_row, null);
			rowHolder = new RowHolder(v);
			v.setTag(rowHolder);
		} else {
			v = bufferView;
			rowHolder = (RowHolder)v.getTag();
		}

		if (p != null) {

			final View layout = rowHolder.getRowMainLayout();
			if (position == pActivity.selectedItemNum) {
				layout.setBackgroundColor(ZStyle.ListBackgroundSelectedColor);
			} else {
				layout.setBackgroundColor(ZStyle.ListBackgroundColor);
			}
			layout.setOnTouchListener(Util.getTouchChangeColor(layout));
			layout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (pActivity.gestureSuccess()) return;
					BoardItemListActivity.getInstance().changeBoard(p);
				}
			});
			TextView tt = rowHolder.getBoardTitle();
			tt.setText(Html.fromHtml(p.getTitle()));

			rowHolder.getBoardCount().setText(p.getReCnt());
			rowHolder.getBoardDate().setText(p.getDate());

			tt = rowHolder.getBoardWriter();
			String w = p.getWriter();
			if (w.indexOf("img src") > -1) {
				tt.setVisibility(View.GONE);
				ImageView writerImage = rowHolder.getBoardWriterImageView();
				writerImage.setVisibility(View.VISIBLE);
				
				String tmp = "<img src='";
				int start = w.indexOf(tmp)+tmp.length();
				String url=w.substring(start, w.indexOf("'", start));
				
				new GetWriterPicData(url, writerImage).execute();
			} else {
				rowHolder.getBoardWriterImageView().setVisibility(View.GONE);
				tt.setVisibility(View.VISIBLE);
				tt.setText(p.getWriter());
			}
		}
		return v;
	}

	public View getViewPic(int position, View v, ViewGroup parent) {

		final ArticleType p;
		if (position >= getCount() - 1) {
			View bottomMoreView = vi.inflate(R.layout.board_item_more, null);
			if (position > 0)
				pActivity.onClickMoreButton(bottomMoreView);
			return bottomMoreView;
		} else {
			p = this.getItemPic(position);
			v = vi.inflate(R.layout.board_item_row_pic, null);
		}

		if (p != null) {
			final View layout = v.findViewById(R.id.board_item_row_main_layout);
			if (position == pActivity.selectedItemNum) {
				layout.setBackgroundColor(ZStyle.ListBackgroundSelectedColor);
			} else {
				layout.setBackgroundColor(ZStyle.ListBackgroundColor);
			}

			layout.setOnTouchListener(Util.getTouchChangeColor(layout));
			layout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (pActivity.gestureSuccess()) return;
					BoardItemType item = new BoardItemType();
					item.setBo_table("image");
					item.setUrl(NetworkBoardList.getBoard("image").getUrl() + "&wr_id=" + p.getWrId());
					BoardItemListActivity.getInstance().changeBoard(item);
				}
			});
			
			TextView boardTitle = ((TextView)v.findViewById(R.id.BoardTitle));
			boardTitle.setTextSize(AppData.getListFontSize()*20);
			boardTitle.setText(Html.fromHtml(p.getTitle()));
			((TextView)v.findViewById(R.id.BoardCnt)).setText(p.getHit());
			((TextView)v.findViewById(R.id.BoardDate)).setText(p.getDate());
			((TextView)v.findViewById(R.id.BoardWriter)).setText(p.getWriter());

			ImageView iv = (ImageView) v.findViewById(R.id.board_item_imageview);
			if (p.getImageBitmap() == null) {
				new GetPicData(p, iv).execute();
			} else {
				iv.setImageBitmap(p.getImageBitmap());
			}
		}
		return v;
	}

	public void add(BoardItemType item) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getWrId().equals(item.getWrId()))
				return;
		}
		items.add(item);
	}

	public void add(ArticleType item) {
		for (int i = 0; i < itemsPic.size(); i++) {
			if (itemsPic.get(i).getWrId().equals(item.getWrId()))
				return;
		}
		itemsPic.add(item);
	}

	public void clear() {
		items.clear();
		itemsPic.clear();
	}

	@Override
	public int getCount() {
		if (itemsPic.size() > 0) {
			return itemsPic.size() + 1;
		}
		if (items.size()>0) {
			return items.size() + 2;
		}
		return 1;
	}

	@Override
	public BoardItemType getItem(int position) {
		return items.get(position);
	}

	public ArticleType getItemPic(int position) {
		return itemsPic.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private class GetPicData extends AsyncTask<String, Integer, Bitmap> {

		private String url;
		private ImageView iv;
		private ArticleType p;

		public GetPicData(ArticleType p, ImageView iv) {
			if (p.getImage()==null) {
				this.url = null;
			} else {
				if (p.getImage().indexOf("http")==0) {
					this.url = p.getImage();
				} else {
					this.url = NetworkBase.CLIEN_URL + "cs2/bbs/" + p.getImage();
				}
			}
			this.iv = iv;
			this.p = p;
			this.iv.setImageResource(R.drawable.sample_loading);
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			Bitmap bm = null;
			try {
				bm = Util.getBitmapImage(url, true);
			}catch (Exception e) {
				Log.e("error", "",e);
			}
			return bm; 
		}

		@Override
		protected void onPostExecute(Bitmap tempData) {
			if (tempData==null) {
				iv.setImageBitmap(null);
				return;
			}
			iv.setImageBitmap(tempData);
			p.setImageBitmap(tempData);
		}
	}
	
	private class RowHolder {
		
		private View v, rowMainLayout;
		private TextView boardTitle, boardCount, boardDate, boardWriter;
		private ImageView writerImageView;
		boolean multiLine = false;
		
		public RowHolder(View v) {
			this.v = v;
			String multiLine = "1";
			try {
				multiLine = Util.getSharedData(pActivity, AppData.MULTI_LINE_ENABLE, "1");
			} catch (Exception e){}
			
			this.multiLine = multiLine.equals("1");
		}
		public View getRowMainLayout() {
			if (rowMainLayout==null) {
				rowMainLayout = v.findViewById(R.id.board_item_row_main_layout);
			}
			return rowMainLayout;
		}
		
		public TextView getBoardTitle() {
			if (boardTitle==null) {
				boardTitle = (TextView) v.findViewById(R.id.BoardTitle);
				boardTitle.setTextSize(AppData.getListFontSize()*20);
				
				if (multiLine) {
					boardTitle.setSingleLine(false);
				} else {
					boardTitle.setSingleLine(true);
				}
			}
			return boardTitle;
		}
		
		public TextView getBoardCount() {
			if (boardCount==null) {
				boardCount = (TextView) v.findViewById(R.id.BoardCnt);
				boardCount.setTextSize(AppData.getListFontSize()*14);
			}
			return boardCount;
		}
		
		public TextView getBoardDate() {
			if (boardDate==null) {
				boardDate=(TextView) v.findViewById(R.id.BoardDate);
				boardDate.setTextSize(AppData.getListFontSize()*14);
			}
			return boardDate;
		}
		
		public TextView getBoardWriter() {
			if (boardWriter==null) {
				boardWriter = (TextView) v.findViewById(R.id.board_item_row_boardwriter);
				boardWriter.setTextSize(AppData.getListFontSize()*14);
			}
			return boardWriter;
		}
		
		public ImageView getBoardWriterImageView() {
			if (writerImageView==null) {
				writerImageView = (ImageView) v.findViewById(R.id.board_item_row_boardwriter_imageview);
			}
			return writerImageView;
		}
	}
}
