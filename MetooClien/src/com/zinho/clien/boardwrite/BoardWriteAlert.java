package com.zinho.clien.boardwrite;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class BoardWriteAlert extends AlertDialog.Builder {
	private ArrayList<String> data;
	private BoardWriteAlert thisClass;
	private int selectedItem = -1;

	public BoardWriteAlert(final BoardWriteActivity context, ArrayList<String> data) {
		super(context);
		this.data = data;
		this.thisClass = this;
		setTitle("분류 선택");


		setSingleChoiceItems(thisClass.data.toArray(new String[] {}), -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				selectedItem = which;
				dialog.dismiss();
				onClickOkButton();
			}
		});
	}
	
	/**
	 * 상위 객체에서 오버라이드 해서 사용함.
	 */
	public void onClickOkButton() {
		
	}
	
	public String getSelectedItem() {
		return this.data.get(selectedItem);
	}
}
