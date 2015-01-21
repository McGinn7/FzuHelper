package com.west2.start;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.west2.custom.adapter.ViewPagerAdapter;
import com.west2.main.R;
public class GuideActivity extends Activity{
	private int[] circleId = {R.id.circle1,R.id.circle2,R.id.circle3};

	private int curPosition;
	private ViewPager viewpager;
	private ImageView[] circleView;
	private LocalActivityManager mManager;
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);

		mManager = new LocalActivityManager(this,true);
		mManager.dispatchCreate(savedInstanceState);
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		circleView = new ImageView[3];
		for(int i=0;i<3;++i) circleView[i] = (ImageView)findViewById(circleId[i]);
		circleView[0].setImageDrawable(getResources().getDrawable(R.drawable.circle_select));
		viewpager=(ViewPager)findViewById(R.id.viewpager);
	}
	private void initValue(){
		curPosition=0;
		List<View> views = new ArrayList<View>();
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view1=inflater.inflate(R.layout.guide_view1, null);
		View view2=inflater.inflate(R.layout.guide_view2, null);
		views.add(view1);
		views.add(view2);
		Intent intent = new Intent(this,ThirdGuideActivity.class);
		views.add(getView("ThirdGuideActivity",intent));
		ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(views);
		viewpager.setAdapter(mViewPagerAdapter);		
	}
	private void setListener(){
		viewpager.setOnPageChangeListener(new OnPageChangeListener(){
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				circleView[curPosition].setImageDrawable(getResources().getDrawable(R.drawable.circle_unselect));
				circleView[position].setImageDrawable(getResources().getDrawable(R.drawable.circle_select));
				curPosition = position;
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	private View getView(String id,Intent intent){
		if(intent==null) return null;
		return mManager.startActivity(id, intent).getDecorView();
	}
}
