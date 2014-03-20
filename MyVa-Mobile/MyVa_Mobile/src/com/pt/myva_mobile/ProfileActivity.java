package com.pt.myva_mobile;

import java.sql.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	DBAdapter dbAdapter = new DBAdapter();
	int userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.profile);
		setContentView(R.layout.profile_activity);

		int upId = Resources.getSystem().getIdentifier("up", "id", "android");
		if (upId > 0) {
			ImageView up = (ImageView) findViewById(upId);
			up.setImageResource(R.drawable.ic_action_previous_item);
		}

		Intent intent = getIntent();
		userId = intent.getIntExtra("ID", 0);
		fillData(userId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.profile_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		// case R.id.action_edit_profile:
		// Intent j = new Intent(ProfileActivity.this,
		// EditProfileActivity.class);
		// j.putExtra("ID", userId);
		// startActivity(j);
		// break;
		default:
			break;
		}
		return true;
	}

	public void fillData(int userId) {
		User user = dbAdapter.getDBUserAdapter(getApplicationContext())
				.getUserByID(userId);

		TextView txtUsername = (TextView) findViewById(R.id.pro_username);
		txtUsername.setText(user.getUsername());

		TextView txtEmail = (TextView) findViewById(R.id.pro_email);
		txtEmail.setText(user.getEmail());

		Date date = new Date(user.getBirthdate().getTime());

		TextView txtBirthdate = (TextView) findViewById(R.id.pro_birthdate);
		txtBirthdate.setText(DateFormat.format("MM/dd/yyyy", date).toString());
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillData(userId);
	}
}
