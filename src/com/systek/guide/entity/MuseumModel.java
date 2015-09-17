package com.systek.guide.entity;

public class MuseumModel implements ModelInterface{
	private String id;
	private String museumId;
	private String name;// 博物馆名称
	private String address;// 博物馆地址
	private double longitudX;// 表示博物馆纬度坐标
	private double longitudY;// 表示博物馆经度坐标
	private String opentime;// 博物馆开放时间
	private boolean isOpen;// 当前博物馆是否开放
	private String iconUrl;// icon的Url地址
	private String city;
	private int version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMuseumId() {
		return museumId;
	}
	public void setMuseumId(String museumId) {
		this.museumId = museumId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getLongitudX() {
		return longitudX;
	}
	public void setLongitudX(double longitudX) {
		this.longitudX = longitudX;
	}
	public double getLongitudY() {
		return longitudY;
	}
	public void setLongitudY(double longitudY) {
		this.longitudY = longitudY;
	}
	public String getOpentime() {
		return opentime;
	}
	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	
}
