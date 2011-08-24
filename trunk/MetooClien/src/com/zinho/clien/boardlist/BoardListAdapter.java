package com.zinho.clien.boardlist;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zinho.clien.R;
import com.zinho.clien.util.AppData;
import com.zinho.clien.util.Util;
import com.zinho.clien.util.ZStyle;

public class BoardListAdapter extends BaseAdapter {

	private ArrayList<BoardListType> items;
	private BoardListActivity pActivity;

	public BoardListAdapter(BoardListActivity context, ArrayList<BoardListType> items) {
		this.pActivity = context;
		this.items = items;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		RowHolder rowHolder = null;
		if (convertView==null) {
			LayoutInflater vi = (LayoutInflater) pActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.board_list_row, null);
			rowHolder = new RowHolder(v);
			v.setTag(rowHolder);
		} else {
			v = convertView;
			rowHolder = (RowHolder)v.getTag();
		}

		final BoardListType p = items.get(position);
		if (p != null) {
			TextView tt = rowHolder.getTopText();
			if (tt != null)	{
				tt.setText(p.getName());
				RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) tt.getLayoutParams();
				if (p.isEmpty()) {
					tt.setTextSize(AppData.getListFontSize()*13);
					tt.setGravity(Gravity.RIGHT);
					layoutParam.height=Util.parseDPItoPx((int)(AppData.getListFontSize()*30));
					tt.setPaintFlags(tt.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
				} else {
					tt.setTextSize(AppData.getListFontSize()*20);
					tt.setGravity(Gravity.CENTER_VERTICAL);
					tt.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
					layoutParam.height=Util.parseDPItoPx((int)(AppData.getListFontSize()*50));
				}
				tt.setLayoutParams(layoutParam);
			}
			
			final View layout = rowHolder.getMainLayout();
			if (position == pActivity.selectedItemNum) {
				layout.setBackgroundColor(ZStyle.ListBackgroundSelectedColor);
			} else {
				layout.setBackgroundColor(ZStyle.ListBackgroundColor);
			}
			
			if (p.isEmpty() == false) {
				layout.setOnTouchListener(Util.getTouchChangeColor(layout));
				layout.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (pActivity.gestureSuccess()) return;
						BoardListActivity.getInstance().changeBoard(p);
					}
				});	
			} else {
				layout.setOnTouchListener(null);
				layout.setOnClickListener(null);
			}
		}
		return v;
	}

	public int getCount() {
		return items.size();
	}

	public BoardListType getItem(int arg0) {
		return items.get(arg0);
	}

	public long getItemId(int arg0) {
		return 0;
	}
	
	private class RowHolder {
		View v, mainLayout;
		TextView topText;
		
		public RowHolder(View v) {
			this.v = v;
		}
		
		public TextView getTopText() {
			if (topText==null) {
				topText = (TextView)v.findViewById(R.id.boardlistrow_item_toptext);
				topText.setTextSize(AppData.getListFontSize()*20);
			}
			return topText;
		}
		
		public View getMainLayout() {
			if (mainLayout==null) {
				mainLayout = v.findViewById(R.id.board_list_row_mainLayout);
			}
			return mainLayout;
		}
		
	}
}