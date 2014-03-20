package com.pt.myva_mobile;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class Event implements Serializable {

	private int id;
	private int id_local;
	private int id_user;
	private int id_image;
	private String name = null;
	private long timestampEvent;
	private Calendar calendar;
	private boolean selected = false;

	public Event() {
	}

	public Event(String name, int id_user, int id_local, int id_image,
			Calendar calendar) {
		this.setId(0);
		this.setName(name);
		this.setId_user(id_user);
		this.setId_local(id_local);
		this.setId_image(id_image);
		this.setTimestampEvent(timestampEvent);
		this.setCalendar(calendar);
		this.setSelected(false);
	}

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	boolean isSelected() {
		return selected;
	}

	void setSelected(boolean selected) {
		this.selected = selected;
	}

	Calendar getCalendar() {
		return calendar;
	}

	void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	int getId_local() {
		return id_local;
	}

	void setId_local(int id_local) {
		this.id_local = id_local;
	}

	int getId_user() {
		return id_user;
	}

	void setId_user(int id_user) {
		this.id_user = id_user;
	}

	int getId_image() {
		return id_image;
	}

	void setId_image(int id_image) {
		this.id_image = id_image;
	}

	int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}

	long getTimestampEvent() {
		return timestampEvent;
	}

	void setTimestampEvent(long timestampEvent) {
		this.timestampEvent = timestampEvent;
	}
}