package com.west2.custom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.west2.domain.Credit;
import com.west2.main.R;

public class CreditListAdapter extends BaseAdapter{
	private Context mContext;
	private Credit credit;
	public CreditListAdapter(Context context,Credit credit){
		this.mContext=context;
		this.credit=credit;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
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
			view=LayoutInflater.from(mContext).inflate(R.layout.list_item_credit, null);
		TextView creditLeast=(TextView)view.findViewById(R.id.credit_least);
		TextView creditTotal=(TextView)view.findViewById(R.id.credit_total);
		TextView creditTotalMark=(TextView)view.findViewById(R.id.credit_totalmark);
		TextView creditGradePoint=(TextView)view.findViewById(R.id.credit_gradepoint);
		creditLeast.setText(credit.getCreditLeast());
		creditTotal.setText(credit.getCreditTotal());
		creditTotalMark.setText(credit.getCreditTotalMark());
		creditGradePoint.setText(credit.getGradePoint());
		return view;
	}
}
