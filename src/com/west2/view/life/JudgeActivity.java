package com.west2.view.life;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.west2.domain.Judge;
import com.west2.main.R;
import com.west2.service.JudgeService;
import com.west2.service.LoginService;
import com.west2.service.ViewService;

public class JudgeActivity extends Activity{
	private final String[] status = {"��������","��������.","��������..","��������..."};
	private boolean run;
	private boolean succeed;
	private Button btnStart;
	private Judge judge;
	private TextView textRuntime,textContent;
	private LinearLayout textLayout;
	private LoginService loginService;
	private JudgeService judgeService;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_judge);
		
		findView();
		initValue();
		setListener();
	}
	private void findView(){
		btnStart = (Button)findViewById(R.id.button_start);
		textRuntime = (TextView)findViewById(R.id.text_runtime);
		textContent = (TextView)findViewById(R.id.judge_text_content);
		textLayout = (LinearLayout)findViewById(R.id.text_layout);
	}
	/*
	 * ��ʼ������
	 */
	private void initValue(){
		succeed=false;
		judgeService = new JudgeService(this);
		loginService = new LoginService(this);
	}
	/*
	 * ���ü���
	 */
	private void setListener(){
		btnStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(succeed){
					run=false;
					//����Ҫ��finish()�����,������ʾ
					JudgeActivity.this.finish();
					new ViewService(JudgeActivity.this).changeActivityAnim(JudgeActivity.this, 1);
					return ;
				}
				if(run){ return ;}
				succeed=false;
				TranslateAnimation mShowAction = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, -1.5f,
						Animation.RELATIVE_TO_SELF, 0.0f);
				mShowAction.setDuration(300);
				mShowAction.setFillAfter(true);
				mShowAction.setAnimationListener(new AnimationListener(){
					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						run=true;
						textLayout.setVisibility(View.VISIBLE);
						new Thread(runBtnRefresh).start();
					}
					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				LayoutParams params = (LayoutParams) btnStart.getLayoutParams();
				params.setMargins(0, 3*btnStart.getHeight()/2, 0,0);
				btnStart.setVisibility(View.GONE);
				btnStart.setLayoutParams(params);
				btnStart.startAnimation(mShowAction);
				btnStart.setVisibility(View.VISIBLE);
				
				//���������̷߳�������
//				dataHandler.post(getJudgeFromNet);
				new Thread(getJudgeFromNet).start();
			}
		});
	}
	/*
	 * �������ⷵ�ؽ��
	 */
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what<2) run=false;
			switch(msg.what){
			case 0:
				textLayout.setVisibility(View.GONE);
				Toast.makeText(JudgeActivity.this, "����������!���Ժ�����", Toast.LENGTH_SHORT).show();
				succeed=false;
				TranslateAnimation mShowAction = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 1.5f,
						Animation.RELATIVE_TO_SELF, 0.0f);
				mShowAction.setDuration(300);
				mShowAction.setFillAfter(true);
				LayoutParams params = (LayoutParams) btnStart.getLayoutParams();
				params.setMargins(0, 0, 0,0);
				btnStart.setVisibility(View.GONE);
				btnStart.setLayoutParams(params);
				btnStart.startAnimation(mShowAction);
				btnStart.setVisibility(View.VISIBLE);
				btnStart.setText("��ʼ����");
				break;
			case 1:
				succeed=true;
				btnStart.setText("�������");
				textRuntime.setText(judge.getRuntime());
				textContent.setText(judge.getContent());
				break;
			case 2:btnStart.setText(status[0]);
				break;
			case 3:btnStart.setText(status[1]);
				break;
			case 4:btnStart.setText(status[2]);
				break;
			case 5:btnStart.setText(status[3]);
				break;
			}
		}
	};
	/*
	 * ����UI�߳�
	 */
	Runnable runBtnRefresh = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int x=0;
			Message msg = dataHandler.obtainMessage();
			while(run){
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!run) break;
				msg = dataHandler.obtainMessage();
				msg.what = 2+x;
				dataHandler.sendMessage(msg);
				x=(x+1)%status.length;
			}
		}
	};
	/*
	 * �����ȡ
	 */
	Runnable getJudgeFromNet = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String username = loginService.getUsername();
			String password = loginService.getPassword();
			judge = judgeService.queryJudge(username, password);
			Message msg = dataHandler.obtainMessage();
			if(judge==null) msg.what = 0;
			else msg.what = 1;
			dataHandler.sendMessage(msg);
		}
	};
	
	public boolean onKeyDown(int KeyCode,KeyEvent event){
		if(KeyCode==KeyEvent.KEYCODE_BACK){
			run=false;
			this.finish();
			new ViewService(this).changeActivityAnim(this, 1);
		}
		return false;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("Judge"); //ͳ��ҳ��
	    MobclickAgent.onResume(this);          //ͳ��ʱ��
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("Judge"); // ��֤ onPageEnd ��onPause ֮ǰ����,��Ϊ onPause �лᱣ����Ϣ 
	    MobclickAgent.onPause(this);
	}
	public void onDestroy(){
		run=false;
		super.onDestroy();
	}
}
