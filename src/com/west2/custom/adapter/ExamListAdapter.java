package com.west2.custom.adapter;

import java.util.List;

import com.west2.domain.Exam;
import com.west2.main.R;
import com.west2.service.DateService;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExamListAdapter extends BaseAdapter{
	private int colorArr[] = {R.color.side_color1,R.color.side_color2,R.color.side_color3,R.color.side_color4};
	private Context mContext;
	private List<Exam> list;
	public ExamListAdapter(Context context,List<Exam> list){
		this.mContext=context;
		this.list=list;
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
		if(view==null)
			view=LayoutInflater.from(mContext).inflate(R.layout.list_item_exam, null);
		LinearLayout side_color = (LinearLayout)view.findViewById(R.id.side_color);
		TextView viewName=(TextView)view.findViewById(R.id.exam_name);
		TextView viewPlace=(TextView)view.findViewById(R.id.exam_place);
		TextView viewDate=(TextView)view.findViewById(R.id.exam_date);
		TextView viewTime=(TextView)view.findViewById(R.id.exam_time);
		side_color.setBackgroundResource(colorArr[position%4]);
		Exam exam = list.get(position);
		viewName.setText(exam.getCourseName());
		viewPlace.setText(exam.getRoom());
		viewDate.setText(exam.getDate());
		viewTime.setText(exam.getTime());

		if(exam.getCourseName().contains("‘›Œﬁ")){
			TextView placeFlag = (TextView)view.findViewById(R.id.exam_place_flag);
			TextView timeFlag = (TextView)view.findViewById(R.id.exam_time_flag);
			TextView dateFlag = (TextView)view.findViewById(R.id.exam_date_flag);
			placeFlag.setVisibility(View.INVISIBLE);
			timeFlag.setVisibility(View.INVISIBLE);
			dateFlag.setVisibility(View.INVISIBLE);
		}
		else{
			// ≈–∂œøº ‘ «∑Ò“—øºÕÍ
			try{
				DateService dateService = new DateService(mContext);
				int cntYear = Integer.parseInt(dateService.getYear());
				int cntMonth = Integer.parseInt(dateService.getMonth());
				int cntDay = Integer.parseInt(dateService.getDay());
				int cntHour = Integer.parseInt(dateService.getHour());
				int cntMinute = Integer.parseInt(dateService.getMinute());
				int year = Integer.parseInt(exam.getYear());
				int month = Integer.parseInt(exam.getMonth());
				int day = Integer.parseInt(exam.getDay());
				int hour = Integer.parseInt(exam.getHour());
				int minute = Integer.parseInt(exam.getMinute());
				Boolean done = false;
				if(year<cntYear || (year==cntYear && month<cntMonth) || (year==cntYear && month==cntMonth && day<cntDay))
					done = true;
				if(!done){
					if(hour<cntHour || (hour==cntHour && minute<cntMinute)) done = true;
				}
				if(done){
					viewName.setTextColor(mContext.getResources().getColor(R.color.course_grey));
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return view;
	}
}