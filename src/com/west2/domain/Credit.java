package com.west2.domain;

public class Credit {
	private String creditLeast,creditTotal,creditTotalMark,gradePoint;
	public void setCreditLeast(String creditleast){
		this.creditLeast = creditleast;
	}
	public void setCreditTotal(String credittotal){
		this.creditTotal = credittotal;
	}
	public void setCreditTotalMark(String credittotalmark){
		this.creditTotalMark = credittotalmark;
	}
	public void setGradePoint(String gradepoint){
		this.gradePoint = gradepoint;
	}
	public String getCreditLeast(){
		return this.creditLeast;
	}
	public String getCreditTotal(){
		return this.creditTotal;
	}
	public String getCreditTotalMark(){
		return this.creditTotalMark;
	}
	public String getGradePoint(){
		return this.gradePoint;
	}
}