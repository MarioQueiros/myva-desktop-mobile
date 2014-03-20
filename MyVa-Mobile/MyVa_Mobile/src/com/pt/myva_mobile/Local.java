package com.pt.myva_mobile;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Local implements Serializable {

	private int id;
	private double latitude;
	private double longitude;
	private String name = null;

	// private int id_localType;

	public Local() {
	}

	public Local(double latitude, double longitude, String name) {
		this.setId(0);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setName(name);
		//this.setId_localType(id_localType);
	}

	double getLatitude() {
		return latitude;
	}

	void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	double getLongitude() {
		return longitude;
	}

	void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	// int getId_localType() {
	// return id_localType;
	// }
	//
	// void setId_localType(int id_localType) {
	// this.id_localType = id_localType;
	// }

	int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}
}
