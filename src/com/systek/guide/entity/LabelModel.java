package com.systek.guide.entity;


public class LabelModel implements ModelInterface{

	private String id;
	private String museumId;
	private String name;
	private String lables;

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


	public String getLables() {
		return lables;
	}

	public void setLables(String lables) {
		this.lables = lables;
	}


	@Override
	public String toString() {
		return "OfflineLabelBean [id=" + id + ", museumId=" + museumId
				+ ", name=" + name + ", labels=" + lables + "]";
	}

}
