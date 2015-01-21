package com.west2.domain;

public class Weather {
	private String date,weather,wind,temperature;
	public String getDate(){
		return date;
	}
	public String getWeather(){
		return weather;
	}
	public String getWind(){
		return wind;
	}
	public String getTemperature(){
		return temperature;
	}
	public void setDate(String date){
		this.date=date;
	}
	public void setWeather(String weather){
		this.weather=weather;
	}
	public void setWind(String wind){
		this.wind=wind;
	}
	public void setTemperature(String temperature){
		this.temperature=temperature;
	}
}
