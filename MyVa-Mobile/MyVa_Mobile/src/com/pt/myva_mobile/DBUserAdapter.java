package com.pt.myva_mobile;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUserAdapter {
	private DBHelper dbHelper;

	private static final String TABLE = "USERS";
	private static final String _ID = "_id";
	private static final String USERNAME = "username";
	private static final String EMAIL = "email";
	private static final String PASSWORD = "password";
	private static final String BIRTHDATE = "birthdate";
	private static final String PRIVKEY = "private_key";
	private static final String PUBKEY = "public_key";

	public DBUserAdapter(Context context) {
		dbHelper = new DBHelper(context, TABLE, _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + USERNAME + " TEXT, "
				+ EMAIL + " TEXT, " + BIRTHDATE + " LONG, " + PASSWORD
				+ " TEXT, " + PRIVKEY + " TEXT, " + PUBKEY
				+ " TEXT,  UNIQUE(USERNAME)");
	}

	public long insertUser(String username, String email,
			java.util.Date birthdate) {

		String nullColumnHack = null;

		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		// initialValues.put(_ID, personId);
		initialValues.put(USERNAME, username);
		initialValues.put(EMAIL, email);
		initialValues.put(BIRTHDATE, birthdate.getTime());
		initialValues.put(PASSWORD, "");

		return sqlite.insert(TABLE, nullColumnHack, initialValues);
	}

	public User getUser(String username, String password) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + USERNAME + "='"
				+ username + "'" + " AND " + PASSWORD + "='" + password + "'";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		if (crsr.getCount() > 0) {
			Date date = new Date(crsr.getLong(3));
			User user = new User(crsr.getString(1), crsr.getString(2),
					crsr.getString(4), date);
			user.setId(crsr.getInt(0));

			crsr.close();
			return user;
		}

		crsr.close();
		return new User();
	}

	public User getUserByID(int userId) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + _ID + "=" + userId;

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		if (crsr.getCount() > 0) {
			Date date = new Date(crsr.getLong(3));
			User user = new User(crsr.getString(1), crsr.getString(2),
					crsr.getString(4), date);
			user.setId(crsr.getInt(0));

			crsr.close();
			return user;
		}

		crsr.close();
		return new User();
	}

	public boolean checkUsername(String username) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + USERNAME + "='"
				+ username + "'";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		if (crsr.getCount() > 0) {
			crsr.close();
			return true;
		}

		crsr.close();
		return false;
	}

	public boolean checkUsername(String username, int userId) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + USERNAME + "='"
				+ username + "' AND " + _ID + "!=" + userId;

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		if (crsr.getCount() > 0) {
			crsr.close();
			return true;
		}

		crsr.close();
		return false;
	}

	public int getUserID(String username) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + USERNAME + "='"
				+ username + "';";

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

	public String getUserPrivateKey(String username) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + USERNAME + "='"
				+ username + "'";

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		if (crsr.getCount() > 0) {
			return crsr.getString(5);
		}

		crsr.close();
		return "";
	}

	public int updateUserPublicKey(String username, String key) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		int id_user = getUserID(username);

		ContentValues values = new ContentValues();
		values.put(PUBKEY, key);
		// updating row
		return sqliteDB.update(TABLE, values, _ID + " = ?",
				new String[] { String.valueOf(id_user) });
	}

	public int updateUserPrivateKey(String username, String key) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		int id_user = getUserID(username);

		ContentValues values = new ContentValues();
		values.put(PRIVKEY, key);
		// updating row
		return sqliteDB.update(TABLE, values, _ID + " = ?",
				new String[] { String.valueOf(id_user) });
	}

	public int updateUser(User user, User userAux, Context ctx) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();

		User userAux2 = user;

		if (!userAux.getUsername().equals("")) {
			userAux2.setUsername(userAux.getUsername());
		}
		if (userAux.getBirthdate() != null) {
			userAux2.setBirthdate(userAux.getBirthdate());
		}
		if (!userAux.getEmail().equals("")) {
			userAux2.setEmail(userAux.getEmail());
		}
		if (!userAux.getPassword().equals("")) {
			userAux2.setPassword(userAux.getPassword());
		}

		ContentValues values = new ContentValues();
		values.put(USERNAME, userAux2.getUsername());
		values.put(EMAIL, userAux2.getEmail());
		values.put(BIRTHDATE, userAux2.getBirthdate().getTime());
		values.put(PASSWORD, userAux2.getPassword());

		SaveSharedPreference.setUserName(ctx, userAux2.getUsername());
		// updating row
		return sqliteDB.update(TABLE, values, _ID + " = ?",
				new String[] { String.valueOf(user.getId()) });
	}

}
