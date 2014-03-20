package com.pt.myva_mobile;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Image implements Serializable {

	private int id;
	private byte[] img;

	public Image() {
	}

	public Image(byte[] img) {
		this.setId(0);
		this.setImg(img);
	}

	int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}

	byte[] getImg() {
		return img;
	}

	void setImg(byte[] img) {
		this.img = img;
	}
}
