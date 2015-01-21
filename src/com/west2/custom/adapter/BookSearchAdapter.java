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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.west2.domain.Book;
import com.west2.main.R;
import com.west2.service.LibraryService;
import com.west2.utils.LoadingDialog;

public class BookSearchAdapter extends BaseAdapter{
	private int colorArr[] = {R.color.side_color1,R.color.side_color2,R.color.side_color3,R.color.side_color4};
	private Context mContext;
	private List<Book> list;
	private LoadingDialog loading;
	private AlertDialog dialog;
	private Book book;
	private String username = null;
	private String password = null;
	public BookSearchAdapter(Context context,List<Book> list){
		this.mContext=context;
		this.list=list;
		loading = new LoadingDialog(context);
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
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(view == null){
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.list_item_booksearch, null);
			viewHolder.side_color = (LinearLayout)view.findViewById(R.id.lib_search_side_color);
			viewHolder.textBookName = (TextView)view.findViewById(R.id.lib_search_bookname);
			viewHolder.textAuthor = (TextView)view.findViewById(R.id.lib_search_author);
			viewHolder.textPublisher = (TextView)view.findViewById(R.id.lib_search_publisher);
			viewHolder.textPlace = (TextView)view.findViewById(R.id.lib_search_place);
			viewHolder.textMore = (TextView)view.findViewById(R.id.text_more);
			viewHolder.layoutMore = (LinearLayout)view.findViewById(R.id.lib_search_more);
			viewHolder.textYear = (TextView)view.findViewById(R.id.lib_search_year);
			viewHolder.textStore = (TextView)view.findViewById(R.id.lib_search_store);
			viewHolder.textLeadnum = (TextView)view.findViewById(R.id.lib_search_leadnum);
			viewHolder.buttonReser = (Button)view.findViewById(R.id.lib_search_button_reser);
			view.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder)view.getTag();
		}
		Book book = list.get(position);
		viewHolder.side_color.setBackgroundResource(colorArr[position%4]);
		viewHolder.buttonReser.setBackgroundResource(colorArr[position%4]);
		viewHolder.textBookName.setText(book.getName());
		viewHolder.textAuthor.setText(book.getAuthor());
		viewHolder.textPublisher.setText(book.getPublisher());
		viewHolder.textPlace.setText(book.getPlace());
		viewHolder.textStore.setText(book.getStore()+"");
		viewHolder.textLeadnum.setText(book.getCntAmount()+"");
		viewHolder.textYear.setText(book.getYear());
		view.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				final ViewHolder holder = (ViewHolder)view.getTag();
				switch(event.getAction()){
				case MotionEvent.ACTION_UP:
					if(holder.layoutMore.isShown()){
						holder.textMore.setVisibility(View.VISIBLE);
						holder.layoutMore.setVisibility(View.GONE);
					}
					else{
						holder.textMore.setVisibility(View.GONE);
						TranslateAnimation mShowAction = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, -0.2f,
								Animation.RELATIVE_TO_SELF, 0.0f);
						mShowAction.setDuration(300);
						holder.layoutMore.startAnimation(mShowAction);
						holder.layoutMore.setVisibility(View.VISIBLE);
					}
				}
				return true;
			}
		});
		viewHolder.buttonReser.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Book book = list.get(position);
				if(book.getCntAmount()>0)
					Toast.makeText(mContext, "不可预约", Toast.LENGTH_SHORT).show();
				else{
					showChooseDialog(book);
				}
			}
		});
		return view;
	}
	private void showChooseDialog(final Book sbook){
		book = sbook;
		dialog = new AlertDialog.Builder(mContext).create();
		// 必须先调用dialog.setView(),否则dialog中的EditText无法调用输入法
		dialog.setView(LayoutInflater.from(mContext).inflate(R.layout.reser_dialog,null));
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.reser_dialog);
		final EditText usernameText = (EditText)window.findViewById(R.id.edit_username);
		final EditText passwordText = (EditText)window.findViewById(R.id.edit_password);
		Button btnCancel = (Button)window.findViewById(R.id.button_cancel);
		Button btnChoose = (Button)window.findViewById(R.id.button_choose);

		LibraryService libService = new LibraryService(mContext);
		usernameText.setText(libService.getUsername());
		passwordText.setText(libService.getPassword());

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
				username = usernameText.getText().toString();
				password = passwordText.getText().toString();
				new Thread(reserBook).start();
 			}
		});
	}
	Handler dataHandler = new Handler(){
		public void handleMessage(Message msg){
			if(loading.isShowing()) loading.cancel();
			switch(msg.what){
			case 0:Toast.makeText(mContext, "预约失败", Toast.LENGTH_SHORT).show();
				break;
			case 1:Toast.makeText(mContext, "预约成功", Toast.LENGTH_SHORT).show();
				break;
			case 2:Toast.makeText(mContext, "账号或密码错误", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	Runnable reserBook = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = dataHandler.obtainMessage();
			msg.what = 0;
			msg.what = new LibraryService(mContext).reserBook(username,password,book);
			dataHandler.sendMessage(msg);
		}
	};
	final static class ViewHolder{
		LinearLayout side_color;
		LinearLayout layoutMore;
		TextView textMore;
		TextView textBookName;
		TextView textAuthor;
		TextView textPublisher;
		TextView textYear;
		TextView textStore;
		TextView textLeadnum;
		TextView textPlace;
		Button buttonReser;
	}
}