package com.systek.guide.entity;

public class ExhibitModel implements ModelInterface{

	private String id;
	private String name;
	private String museumId;
	private String beaconId;
	private String introduce;
	private String address;
	private float mapx;
	private float mapy;
	private int floor;
	private String iconurl;
	private String imgsurl;
	private String audiourl;
	private String texturl;
	private String labels;
	private String lexhibit;
	private String rexhibit;
	private int version;
	private int priority;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMuseumId() {
		return museumId;
	}

	public void setMuseumId(String museumId) {
		this.museumId = museumId;
	}

	public String getBeaconId() {
		return beaconId;
	}

	public void setBeaconId(String beaconId) {
		this.beaconId = beaconId;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public float getMapx() {
		return mapx;
	}

	public void setMapx(float mapx) {
		this.mapx = mapx;
	}

	public float getMapy() {
		return mapy;
	}

	public void setMapy(float mapy) {
		this.mapy = mapy;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	public String getImgsurl() {
		return imgsurl;
	}

	public void setImgsurl(String imgsurl) {
		this.imgsurl = imgsurl;
	}

	public String getAudiourl() {
		return audiourl;
	}

	public void setAudiourl(String audiourl) {
		this.audiourl = audiourl;
	}

	public String getTexturl() {
		return texturl;
	}

	public void setTexturl(String texturl) {
		this.texturl = texturl;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getLexhibit() {
		return lexhibit;
	}

	public void setLexhibit(String lexhibit) {
		this.lexhibit = lexhibit;
	}

	public String getRexhibit() {
		return rexhibit;
	}

	public void setRexhibit(String rexhibit) {
		this.rexhibit = rexhibit;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
