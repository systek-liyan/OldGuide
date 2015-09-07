package com.systek.guide.entity;

public class CityModule {
	
	private String name;//城市名称
	private String alpha;//城市名称首字母
	
	
	
	public CityModule() {
		super();
	}

	public CityModule(String name, String alpha) {
		super();
		this.name = name;
		this.alpha = alpha;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlpha() {
		return alpha;
	}
	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}
	@Override
	public String toString() {
		return "CityEntity [name=" + name + ", alpha=" + alpha + "]";
	}
	
}
