package com.pt.myva_mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;

public class Utils {

	static Calendar c;
	private static String getEventsByDate = "All";
	private static boolean isEdited = false;
	private static boolean isAdded = false;
	public static String SERVER_URL = "https://mpapq.myftp.org/api/api";
	public static int responseCode;

	@SuppressLint("SimpleDateFormat")
	public static String getSimpleDate(Calendar cal) {

		String weekDay = "";

		int day = cal.get(Calendar.DATE);

		SimpleDateFormat month_date = new SimpleDateFormat("MMM");
		String month_name = month_date.format(cal.getTime());

		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		if (Calendar.MONDAY == dayOfWeek)
			weekDay = "Mon";
		else if (Calendar.TUESDAY == dayOfWeek)
			weekDay = "Tue";
		else if (Calendar.WEDNESDAY == dayOfWeek)
			weekDay = "Wed";
		else if (Calendar.THURSDAY == dayOfWeek)
			weekDay = "Thu";
		else if (Calendar.FRIDAY == dayOfWeek)
			weekDay = "Fri";
		else if (Calendar.SATURDAY == dayOfWeek)
			weekDay = "Sat";
		else if (Calendar.SUNDAY == dayOfWeek)
			weekDay = "Sun";

		return weekDay + ", " + month_name + " " + day;
	}

	public static String getStrDay(Calendar cal) {

		c = Calendar.getInstance();
		int difference = ((int) ((cal.getTime().getTime() / (24 * 60 * 60 * 1000)) - (int) (c
				.getTime().getTime() / (24 * 60 * 60 * 1000))));
		if ((int) difference == 0) {
			return "Today";
		} else if ((int) difference == 1) {
			return "Tomorrow";
		} else if ((int) difference < 0) {
			return "error";
		} else {
			return "";
		}
	}

	public static String getGetEventsByDate() {
		return getEventsByDate;
	}

	public static void setGetEventsByDate(String getEventsByDate) {
		Utils.getEventsByDate = getEventsByDate;
	}

	public static boolean isEdited() {
		return isEdited;
	}

	public static void setEdited(boolean isEdited) {
		Utils.isEdited = isEdited;
	}

	public static boolean isAdded() {
		return isAdded;
	}

	public static void setAdded(boolean isAdded) {
		Utils.isAdded = isAdded;
	}

	public static String hmacSha1(String value, String key)
			throws UnsupportedEncodingException, NoSuchAlgorithmException,
			InvalidKeyException {
		String type = "HmacSHA1";
		SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
		Mac mac = Mac.getInstance(type);
		mac.init(secret);
		byte[] bytes = mac.doFinal(value.getBytes());
		return bytesToHex(bytes);
	}

	private final static char[] hexArray = "0123456789abcdef".toCharArray();

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String makeSHA1Hash(String input)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.reset();
		byte[] buffer = input.getBytes();
		md.update(buffer);
		byte[] digest = md.digest();

		String hexStr = "";
		for (int i = 0; i < digest.length; i++) {
			hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		return hexStr;
	}

	public static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}

	public static boolean compareTimestamp(long lg1, long lg2) {

		// int difference = ((int) ((lg1 / (24 * 60 * 60 * 1000)) - (int) (lg2 /
		// (24 * 60 * 60 * 1000))));
		//
		//
		// if ((int) difference > 0) {
		// return true;
		// } else {
		// return false;
		// }

		if (lg1 > lg2) {
			return true;
		}
		return false;
	}
}
