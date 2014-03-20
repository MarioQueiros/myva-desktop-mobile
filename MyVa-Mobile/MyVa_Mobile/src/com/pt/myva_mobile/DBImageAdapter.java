package com.pt.myva_mobile;

import java.sql.Blob;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBImageAdapter {
	private DBHelper dbHelper;

	private static final String TABLE = "IMAGES";
	private static final String _ID = "_id";
	private static final String IMAGE = "image";

	public DBImageAdapter(Context context) {
		dbHelper = new DBHelper(context, TABLE, _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + IMAGE + " BLOB");
	}

	public long insertImage(Image image) {

		String nullColumnHack = null;

		SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();

		initialValues.put(IMAGE, image.getImg());

		return sqlite.insert(TABLE, nullColumnHack, initialValues);

	}
	
	public Image getImage(int id_image) {

		SQLiteDatabase sqliteDB = dbHelper.getReadableDatabase();
		String s = "SELECT * FROM " + TABLE + " WHERE " + _ID + "="
				+ id_image;

		Cursor crsr = sqliteDB.rawQuery(s, null);
		crsr.moveToFirst();

		Image img = new Image(crsr.getBlob(1));
		img.setId(crsr.getInt(0));

		crsr.close();
		return img;
	}

}
