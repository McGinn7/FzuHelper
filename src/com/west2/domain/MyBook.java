package com.west2.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBook {
	private String bookName;
	private String barCode;
	private String collection;
	private String returnDate;
	private String year,month,day;
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public String getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
		Matcher m;
		// ∆•≈‰ƒÍ
		Pattern patYear = Pattern.compile("[0-9]+/");
		m = patYear.matcher(returnDate);
		if(m.find()){
			year = m.group().toString();
			year = year.replace("/", "");
		}
		else
			year = "0";
		// ∆•≈‰‘¬
		Pattern patMonth = Pattern.compile("/[0-9]+/");
		m = patMonth.matcher(returnDate);
		if(m.find()){
			month = m.group().toString();
			month = month.replace("/", "");
		}
		else
			month = "0";
		//∆•≈‰»’
		Pattern patDay = Pattern.compile("/[0-9]+");
		m = patDay.matcher(returnDate);
		if(m.find()){
			day = m.group().toString();
			day = day.replace("/", "");
		}
		else
			day = "0";
	}
	public String getYear(){
		return this.year;
	}
	public String getMonth(){
		return this.month;
	}
	public String getDay(){
		return this.day;
	}
}
