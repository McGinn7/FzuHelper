package com.west2.domain;

public class BlankRoom {
	private String msg;
	private int siteIndex;
	private int timeIndex;
	private StringBuffer[] rooms;
	
	public BlankRoom(){
		rooms = new StringBuffer[5];
		for(int i=0;i<5;++i){
			rooms[i] = new StringBuffer();
			rooms[i].append("");
		}
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getSiteIndex() {
		return siteIndex;
	}
	public void setSiteIndex(int siteIndex) {
		this.siteIndex = siteIndex;
	}
	public int getTimeIndex() {
		return timeIndex;
	}
	public void setTimeIndex(int timeIndex) {
		this.timeIndex = timeIndex;
	}
	public StringBuffer[] getRooms() {
		return rooms;
	}
	public void setRooms(StringBuffer[] rooms) {
		this.rooms = rooms;
	}
}
