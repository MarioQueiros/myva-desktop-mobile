package com.pt.myva_mobile;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUserLocalAdapter {

	private DBHelper dbHelper;

	private static final String TABLE = "USERLOCAL";
	// private static final String _ID = "_id";
	private static final String ID_USER = "id_user";
	private static final String ID_LOCAL = "id_local";

	public DBUserLocalAdapter(Context context) {
		dbHelper = new DBHelper(context, TABLE, ID_USER + " INTEGER, "
				+ ID_LOCAL + " INTEGER, PRIMARY KEY (" + ID_USER + ","
				+ ID_LOCAL + ")");
	}

	public void insertUserLocal(int id_user, int id_local) {
		String nullColumnHack = null;

		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();

		initialValues.put(ID_USER, id_user);
		initialValues.put(ID_LOCAL, id_local);

		try {
			sqlite.insert(TABLE, nullColumnHack, initialValues);
		} catch (Exception e) {
		}
	}

	public int getIdUserLocal(int id_user, int id_PrevLocal) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + ID_USER + "="
				+ id_user + " AND " + ID_LOCAL + "=" + id_PrevLocal;

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		if (crsr.getCount() > 0) {
			crsr.close();
			return crsr.getInt(0);
		}

		crsr.close();
		return -1;
	}

	public ArrayList<Integer> getListUserLocal(int id_user) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + ID_USER + "="
				+ id_user;

		ArrayList<Integer> listlocalsId = new ArrayList<Integer>();

		try {
			Cursor crsr = sqliteDB.rawQuery(s, null);
			crsr.moveToFirst();

			for (int i = 0; i < crsr.getCount(); i++) {
				listlocalsId.add(crsr.getInt(1));
				crsr.moveToNext();
			}
			crsr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return listlocalsId;
	}

	/*
	 * public int updateUserLocal(int id_user, int id_PrevLocal, int
	 * id_NewLocal) {
	 * 
	 * SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
	 * 
	 * int id = getIdUserLocal(id_user, id_PrevLocal);
	 * 
	 * ContentValues values = new ContentValues(); values.put(ID_USER, id_user);
	 * values.put(ID_LOCAL, id_NewLocal);
	 * 
	 * // updating row return sqliteDB.update(TABLE, values, _ID + " = ?", new
	 * String[] { String.valueOf(id) }); }
	 */
}
