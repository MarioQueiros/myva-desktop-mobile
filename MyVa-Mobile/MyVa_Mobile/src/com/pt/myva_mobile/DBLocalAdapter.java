package com.pt.myva_mobile;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBLocalAdapter {
	private DBHelper dbHelper;
	private static final String TABLE = "LOCALS";
	private static final String _ID = "_id";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String NAME = "name";
	private static final String SERVER_ID = "server_id";

	// private static final String ID_LOCALTYPE = "id_localType";

	public DBLocalAdapter(Context context) {
		dbHelper = new DBHelper(context, TABLE, _ID

		+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + LATITUDE + " DOUBLE, "
				+ LONGITUDE + " DOUBLE, " + NAME + " TEXT, " + SERVER_ID
				+ " TEXT, UNIQUE(NAME)");
		// + ID_LOCALTYPE + " INTEGER, UNIQUE(NAME)");
	}

	public long insertLocal(Local local) {
		String nullColumnHack = null;

		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();

		initialValues.put(LATITUDE, local.getLatitude());
		initialValues.put(LONGITUDE, local.getLongitude());
		initialValues.put(NAME, local.getName());
		// initialValues.put(ID_LOCALTYPE, local.getId_localType());

		return sqlite.insert(TABLE, nullColumnHack, initialValues);
	}

	public Local getLocal(Event event) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + _ID + "="
				+ event.getId_local();

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		Local local = new Local(crsr.getDouble(1), crsr.getDouble(2),
				crsr.getString(3));
		local.setId(crsr.getInt(0));

		crsr.close();
		return local;
	}

	public int getLocalID(String local_server_id) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + SERVER_ID + "='"
				+ local_server_id + "';";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		int id = crsr.getInt(0);
		crsr.close();

		return id;
	}

	public Local getLocalByName(String name) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		String s = "SELECT * FROM " + TABLE + " WHERE " + NAME + "='" + name
				+ "'";

		Cursor crsr2 = sqliteDB.rawQuery(s, null);
		if (crsr2.getCount() > 0) {
			crsr2.moveToFirst();

			Local local = new Local(crsr2.getDouble(1), crsr2.getDouble(2),
					crsr2.getString(3));
			local.setId(crsr2.getInt(0));

			crsr2.close();
			return local;
		}
		return null;
	}

	public String getLocalServerID(String name) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		String s = "SELECT * FROM " + TABLE + " WHERE " + NAME + "='" + name
				+ "';";

		Cursor crsr2 = sqliteDB.rawQuery(s, null);
		if (crsr2.getCount() > 0) {
			crsr2.moveToFirst();

			String server_id = crsr2.getString(4);
			crsr2.close();
			return server_id;
		}
		return "";
	}

	public int updateServerID(Local local, String server_id) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		ContentValues values = new ContentValues();
		values.put(SERVER_ID, server_id);

		// updating row
		return sqliteDB.update(TABLE, values, _ID + " = ?",
				new String[] { String.valueOf(local.getId()) });
	}

	// public Local getPublicLocalByName(String name) {
	//
	// SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
	// String s = "SELECT * FROM " + TABLE + " WHERE " + NAME + "='" + name
	// + "' AND " + ID_LOCALTYPE + "=2;";
	//
	// Cursor crsr = sqliteDB.rawQuery(s, null);
	// crsr.moveToFirst();
	// if (crsr.getCount() > 0) {
	// Local local = new Local(crsr.getDouble(1), crsr.getDouble(2),
	// crsr.getString(3), crsr.getInt(4));
	// local.setId(crsr.getInt(0));
	//
	// crsr.close();
	// return local;
	// }
	// return null;
	// }
	//
	// public Local getPrivateLocalByName(String name, int id_user, Context
	// context) {
	//
	// SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
	// ArrayList<Integer> listlocalsId = new ArrayList<Integer>();
	//
	// listlocalsId = new DBUserLocalAdapter(context)
	// .getListUserLocal(id_user);
	//
	// for (int i = 0; i < listlocalsId.size(); i++) {
	// String s = "SELECT * FROM " + TABLE + " WHERE " + NAME + "='"
	// + name + "' AND " + ID_LOCALTYPE + "=1 AND " + _ID + "="
	// + listlocalsId.get(i);
	//
	// Cursor crsr = sqliteDB.rawQuery(s, null);
	// crsr.moveToFirst();
	// if (crsr.getCount() > 0) {
	// Local local = new Local(crsr.getDouble(1), crsr.getDouble(2),
	// crsr.getString(3), crsr.getInt(4));
	// local.setId(crsr.getInt(0));
	//
	// crsr.close();
	//
	// return local;
	// }
	// }
	// if (listlocalsId.size() == 0) {
	// String s = "SELECT * FROM " + TABLE + " WHERE " + NAME + "='"
	// + name + "' AND " + ID_LOCALTYPE + "=1";
	//
	// Cursor crsr2 = sqliteDB.rawQuery(s, null);
	// crsr2.moveToFirst();
	// Local local = new Local(crsr2.getDouble(1), crsr2.getDouble(2),
	// crsr2.getString(3), crsr2.getInt(4));
	// local.setId(crsr2.getInt(0));
	// crsr2.close();
	//
	// return local;
	// }
	// return null;
	// }

	public ArrayList<Local> getLocals(int id_user, Context context) {

		ArrayList<Local> locals = new ArrayList<Local>();
		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		ArrayList<Integer> listlocalsId = new ArrayList<Integer>();

		// listlocalsId = new DBUserLocalAdapter(context)
		// .getListUserLocal(id_user);
		//
		// for (int i = 0; i < listlocalsId.size(); i++) {
		// String s = "SELECT * FROM " + TABLE + " WHERE (" + _ID + "="
		// + listlocalsId.get(i) + ")"; // OR
		// // (" + ID_LOCALTYPE + "=2);";
		//
		// Cursor crsr2 = sqliteDB.rawQuery(s, null);
		// crsr2.moveToFirst();
		// for (int j = 0; j < crsr2.getCount(); j++) {
		// locals.add(new Local(crsr2.getDouble(1), crsr2.getDouble(2),
		// crsr2.getString(3)));
		// crsr2.moveToNext();
		// }
		// }
		if (listlocalsId.size() == 0) {
			String s = "SELECT * FROM " + TABLE;
			// + " WHERE " + ID_LOCALTYPE + "=2;";

			Cursor crsr2 = sqliteDB.rawQuery(s, null);
			crsr2.moveToFirst();
			for (int j = 0; j < crsr2.getCount(); j++) {
				locals.add(new Local(crsr2.getDouble(1), crsr2.getDouble(2),
						crsr2.getString(3)));
				crsr2.moveToNext();
			}
		}
		return locals;
	}

	public ArrayList<Local> getLocalsByUser(int id_user, Context context) {

		ArrayList<Local> locals = new ArrayList<Local>();
		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		ArrayList<Integer> listlocalsId = new ArrayList<Integer>();

		listlocalsId = new DBUserLocalAdapter(context)
				.getListUserLocal(id_user);

		for (int i = 0; i < listlocalsId.size(); i++) {
			String s = "SELECT * FROM " + TABLE + " WHERE (" + _ID + "="
					+ listlocalsId.get(i);

			Cursor crsr2 = sqliteDB.rawQuery(s, null);
			crsr2.moveToFirst();
			for (int j = 0; j < crsr2.getCount(); j++) {
				locals.add(new Local(crsr2.getDouble(1), crsr2.getDouble(2),
						crsr2.getString(3)));
				crsr2.moveToNext();
			}
		}
		return locals;
	}
}
