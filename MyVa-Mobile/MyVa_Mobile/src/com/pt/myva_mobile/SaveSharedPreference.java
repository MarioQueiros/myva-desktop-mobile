package com.pt.myva_mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
	static final String PREF_USER_NAME = "username";
	static final String PUBLIC_KEY = "";
	static final String PRIVATE_KEY = "";

	static SharedPreferences getSharedPreferences(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public static void setUserName(Context ctx, String userName) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putString(PREF_USER_NAME, userName);
		editor.commit();
	}

	public static String getUserName(Context ctx) {
		return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
	}

	public static void setPublicKey(Context ctx, String publicKey) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putString(PUBLIC_KEY, publicKey);
		editor.commit();
	}

	public static String getPublicKey(Context ctx) {
		return getSharedPreferences(ctx).getString(PUBLIC_KEY, "");
	}

	public static void setPrivateKey(Context ctx, String privateKey) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putString(PRIVATE_KEY, privateKey);
		editor.commit();
	}

	public static String getPrivateKey(Context ctx) {
		return getSharedPreferences(ctx).getString(PRIVATE_KEY, "");
	}
}