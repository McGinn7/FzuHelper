package com.west2.custom.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

final class PagerAdapter extends FragmentStatePagerAdapter {

	public PagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}