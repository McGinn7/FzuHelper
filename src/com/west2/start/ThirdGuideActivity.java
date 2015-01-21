package com.west2.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.west2.main.R;

public class ThirdGuideActivity extends Activity{
	private ImageButton buttonStart;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide_view3);

		findView();
		initValue();
		setListener();
	}
	private void findView(){
		buttonStart=(ImageButton)findViewById(R.id.button_start);
	}
	/*
	 * 初始化数据
	 */
	private void initValue(){
		
	}
	/*
	 * 设置监听
	 */
	private void setListener(){
		buttonStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ThirdGuideActivity.this,LoginActivity.class);
				ThirdGuideActivity.this.startActivity(intent);
				ThirdGuideActivity.this.finish();
			}
		});
	}
}