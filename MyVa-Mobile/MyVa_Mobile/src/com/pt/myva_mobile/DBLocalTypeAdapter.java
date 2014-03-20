package com.pt.myva_mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBLocalTypeAdapter {

	private DBHelper dbHelper;

	private static final String TABLE = "LOCALTYPE";
	private static final String _ID = "_id";
	private static final String TYPE = "type";

	public DBLocalTypeAdapter(Context context) {
		dbHelper = new DBHelper(context, TABLE, _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TYPE
				+ " TEXT, UNIQUE(TYPE)");

		insertLocalType("Private");
		insertLocalType("Public");
	}

	private long insertLocalType(String type) {

		String nullColumnHack = null;

		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();

		initialValues.put(TYPE, type);

		return sqlite.insert(TABLE, nullColumnHack, initialValues);
	}

	public long getIDByLocalType(String localtype) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + TYPE + "='"
				+ localtype + "'";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		if (crsr.getCount() > 0) {
			int id = crsr.getInt(0);
			crsr.close();
			return id;
		}
		crsr.close();
		return 0;
	}
}
