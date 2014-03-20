package com.pt.myva_mobile;

import android.content.Context;

public class DBAdapter {
	DBEventAdapter dbEventAdapter;
	DBLocalTypeAdapter dbLocalTypeAdapter;
	DBImageAdapter dbImageAdapter;
	DBLocalAdapter dbLocalAdapter;
	DBUserAdapter dbUserAdapter;
	DBUserLocalAdapter dbUserLocalAdapter;

	public DBAdapter() {
	}

	public DBEventAdapter getDBEventAdapter(Context ctx) {
		return dbEventAdapter = new DBEventAdapter(ctx);
	}

	public DBImageAdapter getDBImageAdapter(Context ctx) {
		return dbImageAdapter = new DBImageAdapter(ctx);
	}

	public DBLocalAdapter getDBLocalAdapter(Context ctx) {
		return dbLocalAdapter = new DBLocalAdapter(ctx);
	}

	public DBLocalTypeAdapter getDBLocalTypeAdapter(Context ctx) {
		return dbLocalTypeAdapter = new DBLocalTypeAdapter(ctx);
	}

	public DBUserAdapter getDBUserAdapter(Context ctx) {
		return dbUserAdapter = new DBUserAdapter(ctx);
	}

	public DBUserLocalAdapter getDBUserLocalAdapter(Context ctx) {
		return dbUserLocalAdapter = new DBUserLocalAdapter(ctx);
	}
}
