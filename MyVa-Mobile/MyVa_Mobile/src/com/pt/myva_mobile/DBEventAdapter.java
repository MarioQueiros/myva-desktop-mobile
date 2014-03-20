package com.pt.myva_mobile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBEventAdapter {
	private DBHelper dbHelper;

	private static final String TABLE = "EVENTS";
	private static final String _ID = "_id";
	private static final String NAME = "name";
	private static final String ID_USER = "id_user";
	private static final String ID_LOCAL = "id_local";
	private static final String ID_IMAGE = "id_image";
	private static final String TIMESTAMPEVENT = "timestampevent";
	private static final String CALENDAR = "calendar";
	private static final String EVENT_SERVER_ID = "event_server_id";
	private static final String STATUS = "STATUS";

	public DBEventAdapter(Context context) {
		dbHelper = new DBHelper(context, TABLE, _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, "
				+ ID_USER + " INTEGER, " + ID_LOCAL + " INTEGER, " + ID_IMAGE
				+ " INTEGER, " + TIMESTAMPEVENT + " LONG, " + CALENDAR
				+ " LONG, " + EVENT_SERVER_ID + " TEXT, " + STATUS + " TEXT");
	}

	public long insertEvent(Event event) {

		String nullColumnHack = null;

		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();

		initialValues.put(NAME, event.getName());
		initialValues.put(ID_USER, event.getId_user());
		initialValues.put(ID_LOCAL, event.getId_local());
		initialValues.put(ID_IMAGE, event.getId_image());
		initialValues.put(CALENDAR, event.getCalendar().getTimeInMillis());
		initialValues.put(STATUS, "open");

		return sqlite.insert(TABLE, nullColumnHack, initialValues);

	}

	public Event getEventByID(int event_id) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + _ID + "=" + event_id;

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		Calendar cal = Calendar.getInstance();
		int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
		Date da = new Date();
		da = new Date(crsr.getLong(6) - (long) offset);
		cal.setTime(da);

		Event event = new Event(crsr.getString(1), crsr.getInt(2),
				crsr.getInt(3), crsr.getInt(4), cal);
		event.setId(crsr.getInt(0));
		event.setTimestampEvent(crsr.getLong(5));

		crsr.close();
		return event;

	}

	public Event getEventByName(String name) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + NAME + "='" + name
				+ "';";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		Calendar cal = Calendar.getInstance();
		int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
		Date da = new Date();
		da = new Date(crsr.getLong(6) - (long) offset);
		cal.setTime(da);

		Event event = new Event(crsr.getString(1), crsr.getInt(2),
				crsr.getInt(3), crsr.getInt(4), cal);
		event.setId(crsr.getInt(0));
		event.setTimestampEvent(crsr.getLong(5));

		crsr.close();
		return event;

	}

	public Event getEventByServerID(String server_id) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + EVENT_SERVER_ID
				+ "='" + server_id + "';";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();
		if (crsr.getCount() > 0) {
			Calendar cal = Calendar.getInstance();
			int offset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
			Date da = new Date();
			da = new Date(crsr.getLong(6) - (long) offset);
			cal.setTime(da);

			Event event = new Event(crsr.getString(1), crsr.getInt(2),
					crsr.getInt(3), crsr.getInt(4), cal);
			event.setId(crsr.getInt(0));
			event.setTimestampEvent(crsr.getLong(5));

			crsr.close();
			return event;
		}
		return null;

	}

	public boolean checkEvent(String name) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + NAME + "='" + name
				+ "';";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		if (crsr.getCount() > 0) {
			return true;
		}
		return false;
	}

	public ArrayList<Event> getEvents(int id_user) {
		Calendar cal = Calendar.getInstance();
		ArrayList<Event> events = new ArrayList<Event>();
		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "";

		if (Utils.getGetEventsByDate().equals("All")) {

			s = "SELECT * FROM " + TABLE + " WHERE ((" + CALENDAR
					+ "/ (24 * 60 * 60 * 1000))-(" + cal.getTime().getTime()
					+ "/ (24 * 60 * 60 * 1000))) > -1 AND " + ID_USER + "="
					+ id_user + " AND " + STATUS + "='open' ORDER by "
					+ CALENDAR + ";";

		} else if (Utils.getGetEventsByDate().equals("Today")) {

			s = "SELECT * FROM " + TABLE + " WHERE ((" + CALENDAR
					+ "/ (24 * 60 * 60 * 1000))-(" + cal.getTime().getTime()
					+ "/ (24 * 60 * 60 * 1000))) = 0 AND " + ID_USER + "="
					+ id_user + " AND " + STATUS + "='open' ORDER by "
					+ CALENDAR + ";";

		} else if (Utils.getGetEventsByDate().equals("Tomorrow")) {

			s = "SELECT * FROM " + TABLE + " WHERE ((" + CALENDAR
					+ "/ (24 * 60 * 60 * 1000))-(" + cal.getTime().getTime()
					+ "/ (24 * 60 * 60 * 1000))) = 1 AND " + ID_USER + "="
					+ id_user + " AND " + STATUS + "='open' ORDER by "
					+ CALENDAR + ";";

		} else {

			s = "SELECT * FROM " + TABLE + " WHERE ((" + CALENDAR
					+ "/ (24 * 60 * 60 * 1000))-(" + cal.getTime().getTime()
					+ "/ (24 * 60 * 60 * 1000))) > 0 AND ((" + CALENDAR
					+ "/ (24 * 60 * 60 * 1000))-(" + cal.getTime().getTime()
					+ "/ (24 * 60 * 60 * 1000))) < 8 AND " + ID_USER + "="
					+ id_user + " AND " + STATUS + "='open' ORDER by "
					+ CALENDAR + ";";

		}

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		for (int i = 0; i < crsr.getCount(); i++) {

			Calendar cal2 = Calendar.getInstance();
			Date da = new Date();
			da = new Date(crsr.getLong(6));
			cal2.setTime(da);

			Event event = new Event(crsr.getString(1), crsr.getInt(2),
					crsr.getInt(3), crsr.getInt(4), cal2);
			event.setId(crsr.getInt(0));
			event.setTimestampEvent(crsr.getLong(5));
			events.add(event);

			crsr.moveToNext();
		}
		return events;
	}

	public ArrayList<String> getLocalEvents(int id_user) {
		ArrayList<String> events = new ArrayList<String>();
		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "";

		s = "SELECT * FROM " + TABLE + " WHERE " + ID_USER + "=" + id_user
				+ " AND " + STATUS + "='open';";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		for (int i = 0; i < crsr.getCount(); i++) {

			Calendar cal2 = Calendar.getInstance();
			Date da = new Date();
			da = new Date(crsr.getLong(6));
			cal2.setTime(da);

			events.add(crsr.getString(7));

			crsr.moveToNext();
		}
		return events;
	}

	public ArrayList<String> getEventServerData(String name) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		ArrayList<String> list = new ArrayList<String>();
		String s = "SELECT * FROM " + TABLE + " WHERE " + NAME + "='" + name
				+ "';";

		Cursor crsr2 = sqliteDB.rawQuery(s, null);
		if (crsr2.getCount() > 0) {
			crsr2.moveToFirst();

			list.add(crsr2.getString(7));
			list.add(crsr2.getString(5));
		}
		crsr2.close();
		return list;
	}

	public String getEventServerID(Event ev) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		String s = "SELECT * FROM " + TABLE + " WHERE " + _ID + "="
				+ ev.getId() + ";";

		Cursor crsr2 = sqliteDB.rawQuery(s, null);
		String id = "";
		if (crsr2.getCount() > 0) {
			crsr2.moveToFirst();

			id = crsr2.getString(7);
		}
		crsr2.close();
		return id;
	}

	public ArrayList<Event> getAllEvents(int id_user) {
		Calendar cal = Calendar.getInstance();
		ArrayList<Event> events = new ArrayList<Event>();
		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "";

		s = "SELECT * FROM " + TABLE + " WHERE ((" + CALENDAR
				+ "/ (24 * 60 * 60 * 1000))-(" + cal.getTime().getTime()
				+ "/ (24 * 60 * 60 * 1000))) > -1 AND " + ID_USER + "="
				+ id_user + " AND " + STATUS + "='open' ORDER by " + CALENDAR
				+ ";";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		for (int i = 0; i < crsr.getCount(); i++) {

			Calendar cal2 = Calendar.getInstance();
			Date da = new Date();
			da = new Date(crsr.getLong(6));
			cal2.setTime(da);

			Event event = new Event(crsr.getString(1), crsr.getInt(2),
					crsr.getInt(3), crsr.getInt(4), cal2);
			event.setId(crsr.getInt(0));
			event.setTimestampEvent(crsr.getLong(5));
			events.add(event);

			crsr.moveToNext();
		}
		return events;
	}

	public int updateEvent(Event ev, Event eveAux) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		ContentValues values = new ContentValues();
		values.put(NAME, eveAux.getName());
		values.put(ID_USER, eveAux.getId_user());
		values.put(ID_LOCAL, eveAux.getId_local());
		// values.put(ID_IMAGE, eveAux.getId_image());
		values.put(CALENDAR, eveAux.getCalendar().getTimeInMillis());

		// updating row
		return sqliteDB.update(TABLE, values, _ID + " = ?",
				new String[] { String.valueOf(ev.getId()) });
	}

	public int updateEvent2(Event ev, Event eveAux) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		ContentValues values = new ContentValues();
		values.put(NAME, eveAux.getName());
		values.put(ID_USER, eveAux.getId_user());
		values.put(ID_LOCAL, eveAux.getId_local());
		values.put(ID_IMAGE, eveAux.getId_image());
		values.put(CALENDAR, eveAux.getCalendar().getTimeInMillis());

		// updating row
		return sqliteDB.update(TABLE, values, _ID + " = ?",
				new String[] { String.valueOf(ev.getId()) });
	}

	public int updateEvent(Event ev, long timestamp, String server_event_id) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(TIMESTAMPEVENT, timestamp);
		values.put(EVENT_SERVER_ID, server_event_id);

		// updating row
		return sqliteDB.update(TABLE, values, _ID + " = ?",
				new String[] { String.valueOf(ev.getId()) });
	}

	public void deleteEvent(ArrayList<Event> eventsList) {
		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		for (int i = 0; i < eventsList.size(); i++) {

			ContentValues values = new ContentValues();
			values.put(STATUS, "closed");

			sqliteDB.update(TABLE, values, _ID + " = ?",
					new String[] { String.valueOf(eventsList.get(i).getId()) });
		}
	}
}
