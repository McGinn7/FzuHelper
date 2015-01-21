package com.west2.custom.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.west2.domain.MyBook;
import com.west2.main.R;
import com.west2.service.LibraryService;
import com.west2.utils.LoadingDialog;

public class MyBookAdapter extends BaseAdapter{
	private int colorArr[] = {R.color.side_color1,R.color.side_color2,R.color.side_color3,R.color.side_color4};
	private Context mContext;
	private List<MyBook> list;
	private LibraryService libService;
	private MyBook book;
	private LoadingDialog loading;
	public MyBookAdapter(Context context,List<MyBook> list){
		this.mContext=context;
		this.list=list;
		loading = new LoadingDialog(context);
		libService = new LibraryService(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(view == null){
			holder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.list_item_mybook, null);
			holder.sideColor = (LinearLayout) view.findViewById(R.id.side_color);
			holder.textMore = (TextView)view.findViewById(R.id.text_more);
			holder.bookName =  (TextView)view.findViewById(R.id.lib_mybook_name);
			holder.bookId = (TextView)view.findViewById(R.id.lib_mybook_id);
			holder.bookReturn = (TextView)view.findViewById(R.id.lib_mybook_returndate);
			holder.button = (Button)view.findViewById(R.id.button_layout);
			view.setTag(holder);
		}
		else{
			holder = (ViewHolder)view.getTag();
		}
		book = list.get(position);
		holder.sideColor.setBackgroundResource(colorArr[position%4]);
		holder.bookName.setText(book.getBookName());
		holder.bookId.setText(book.getBarCode());
		holder.bookReturn.setText(book.getReturnDate());
		holder.button.setBackgroundResource(colorArr[position%4]);
		view.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				final ViewHolder holder = (ViewHolder)view.getTag();
				switch(event.getAction()){
				case MotionEvent.ACTION_UP:
					if(holder.button.isShown()){
						holder.textMore.setVisibility(View.VISIBLE);
						holder.button.setVisibility(View.GONE);
					}
					else{
						holder.textMore.setVisibility(View.GONE);
						TranslateAnimation mShowAction = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, -0.2f,
								Animation.RELATIVE_TO_SELF, 0.0f);
						mShowAction.setDuration(300);
						holder.button.startAnimation(mShowAction);
						holder.button.setVisibility(View.VISIBLE);
					}
					break;
				}
				return true;
			} 
		});
		final int pos = position;
		holder.button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showChooseDialog(list.get(pos));
			}
		});
		return view;
	}
	private void showChooseDialog(final MyBook sbook){
		final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.renew_dialog);
		Button btnCancel = (Button)window.findViewById(R.id.button_cancel);
		Button btnChoose = (Button)window.findViewById(R.id.button_choose);
		TextView bookname = (TextView)window.findViewById(R.id.mybook_renew);
		bookname.setText(sbook.getBookName());
		btnCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		btnChoose.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.cancel();
				loading.show();
				book=sbook;
				new Thread(renewBook).start();
 			}
		});
	}
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			if(loading.isShowing()) loading.cancel();
			switch(msg.what){
			case 1:Toast.makeText(mContext, "续借成功", Toast.LENGTH_SHORT).show();
				break;
			case 2:Toast.makeText(mContext, "已超过期限!请及时归还", Toast.LENGTH_SHORT).show();
				break;
			case 3:Toast.makeText(mContext, "已达到续借上限", Toast.LENGTH_SHORT).show();
				break;
			default:Toast.makeText(mContext, "续借失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	Runnable renewBook = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = dataHandler.obtainMessage();
			msg.what = 0;
			msg.what = libService.renewBook(book);
			dataHandler.sendMessage(msg);
		}
	};
	final static class ViewHolder{
		LinearLayout sideColor;
		TextView textMore;
		TextView bookName;
		TextView bookId;
		TextView bookReturn;
		Button button;
	}
}