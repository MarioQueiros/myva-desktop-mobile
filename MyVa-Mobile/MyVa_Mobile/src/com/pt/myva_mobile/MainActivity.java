package com.pt.myva_mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("MyVa");
		setContentView(R.layout.login);

		if (SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(i);
		} else {
			Intent i = new Intent(getApplicationContext(), EventsActivity.class);
			startActivity(i);	
		}
		finish();
	}
}
