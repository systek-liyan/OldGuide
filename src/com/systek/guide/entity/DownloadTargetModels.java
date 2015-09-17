package com.systek.guide.entity;

import java.io.Serializable;
import java.util.List;

public class DownloadTargetModels implements ModelInterface,Serializable {
	
	/**
	 * 序列化默认版本ID
	 */
	private static final long serialVersionUID = 1L;

	int id;

	/** 外层(组,城市)*/
	String city; 
	/** 内层(子层,博物馆列表)*/
	List<DownloadInfoModel> list;
	
	public DownloadTargetModels() {
		super();
	}

	public DownloadTargetModels(String city, List<DownloadInfoModel> list) {
		super();
		this.city = city;
		this.list = list;
	}
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getInfoCount() {
            return list .size();
        }

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<DownloadInfoModel> getList() {
		return list;
	}

	public void setList(List<DownloadInfoModel> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "DownloadTargetModels [id=" + id + ", city=" + city + ", list=" + list + "]";
	}
	
	
	
}
