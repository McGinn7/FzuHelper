package com.west2.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

import com.west2.main.R;

public class LoadingDialog extends Dialog {
	public LoadingDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
		this.setContentView(R.layout.dialog_progress);
		this.setCanceledOnTouchOutside(false);
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
	}
}
