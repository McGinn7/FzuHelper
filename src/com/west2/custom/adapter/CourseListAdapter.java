package com.west2.custom.adapter;

import java.util.ArrayList;
import java.util.List;

import com.west2.domain.Course;
import com.west2.main.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CourseListAdapter extends BaseAdapter{
	private int colorArr[] = {R.color.side_color1,R.color.side_color2,R.color.side_color3,R.color.side_color4};
	private Context mContext;
	private List<Course> list;
	private final String[] jieArray={" 1\r\n-\r\n2 "," 3\r\n-\r\n4 "," 5\r\n-\r\n6 "," 7\r\n-\r\n8 "," 9\r\n-\r\n10 "};
	public CourseListAdapter(Context context,List<Course> List){
		this.mContext=context;
		this.list = new ArrayList<Course>();
		for(int i=0;i<5;++i){
			Course c=new Course();
			c.setCourseName("");
			c.setTeacherName("");
			c.setPlace("");
			c.setJie(""+(i+1));
			this.list.add(c);
		}
		if(List!=null){
			for(Course c:List)
				if(c.getJie()!=null && !c.getJie().equals("")){
					int jie = Integer.parseInt(c.getJie());
					this.list.get(jie-1).setPlace(c.getPlace());
					this.list.get(jie-1).setCourseName(c.getCourseName());
					this.list.get(jie-1).setTeacherName(c.getTeacherName());
				}
		}
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
			view=LayoutInflater.from(mContext).inflate(R.layout.list_item_course, null);
		LinearLayout side_color = (LinearLayout)view.findViewById(R.id.side_color);
		TextView courseTime=(TextView)view.findViewById(R.id.course_time);
		TextView courseName=(TextView)view.findViewById(R.id.course_name);
		TextView courseTeacher=(TextView)view.findViewById(R.id.course_teachername);
		TextView coursePlace=(TextView)view.findViewById(R.id.course_place);
		side_color.setBackgroundResource(colorArr[position%4]);
		courseTime.setText(jieArray[position]);
		Course course = list.get(position);
		if(course!=null){
			courseName.setText(course.getCourseName());
			courseTeacher.setText(course.getTeacherName());
			String place = course.getPlace();
			place = place.replace("[", "");
			place = place.replace("]", "");
			coursePlace.setText(place);
		}
		return view;
	}
}