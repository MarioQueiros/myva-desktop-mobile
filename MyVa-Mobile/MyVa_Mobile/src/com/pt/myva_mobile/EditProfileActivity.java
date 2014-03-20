package com.pt.myva_mobile;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditProfileActivity extends Activity {

	DBAdapter dbAdapter = new DBAdapter();
	int userId;
	User user;
	EditText mEditUsername;
	EditText mEditCurrPassword;
	EditText mEditEmail;
	EditText mEditBirthdate;
	EditText mEditNewPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.edit_event);
		setContentView(R.layout.edit_profile);

		int upId = Resources.getSystem().getIdentifier("up", "id", "android");
		if (upId > 0) {
			ImageView up = (ImageView) findViewById(upId);
			up.setImageResource(R.drawable.ic_action_previous_item);
		}

		Intent intent = getIntent();
		userId = intent.getIntExtra("ID", 0);

		user = dbAdapter.getDBUserAdapter(getApplicationContext()).getUserByID(
				userId);

		mEditUsername = (EditText) findViewById(R.id.edit_username);
		mEditUsername.setText(user.getUsername());

		mEditCurrPassword = (EditText) findViewById(R.id.edit_curr_password);
		mEditNewPassword = (EditText) findViewById(R.id.edit_new_password);

		mEditEmail = (EditText) findViewById(R.id.edit_email);
		mEditEmail.setText(user.getEmail());

		mEditBirthdate = (EditText) findViewById(R.id.edit_birthdate);
		Format formatter = new SimpleDateFormat("MM/dd/yyyy");
		String s = formatter.format(user.getBirthdate());
		mEditBirthdate.setText(s);

		mEditUsername.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		mEditEmail.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		mEditNewPassword.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		mEditCurrPassword.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		final View root = findViewById(R.id.edit_profile_id);
		root.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int heightDiff = root.getRootView().getHeight()
								- root.getHeight();

						Rect rectgle = new Rect();
						Window window = getWindow();
						window.getDecorView().getWindowVisibleDisplayFrame(
								rectgle);
						int contentViewTop = window.findViewById(
								Window.ID_ANDROID_CONTENT).getTop();

						if (heightDiff <= contentViewTop) {
							// Soft KeyBoard Hidden
							mEditUsername.setFocusable(false);
							mEditUsername.setFocusableInTouchMode(false);

							mEditEmail.setFocusable(false);
							mEditEmail.setFocusableInTouchMode(false);

							mEditCurrPassword.setFocusable(false);
							mEditCurrPassword.setFocusableInTouchMode(false);

							mEditNewPassword.setFocusable(false);
							mEditNewPassword.setFocusableInTouchMode(false);
						}
					}
				});
	}

	public void showDatePickerDialog(View v) {
		new DatePickerFragment((EditText) v).show(getFragmentManager(),
				"datePicker");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_save_profile:
			updateUser();
			break;
		default:
			break;
		}
		return true;
	}

	void updateUser() {
		User user = dbAdapter.getDBUserAdapter(getApplicationContext())
				.getUserByID(userId);

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		java.util.Date dob_var = null;

		if (!mEditBirthdate.getText().toString().trim().equals("")) {
			try {
				dob_var = sdf.parse(mEditBirthdate.getText().toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if (!mEditCurrPassword.getText().toString().trim().equals("")) {
			User userAux1 = dbAdapter.getDBUserAdapter(getApplicationContext())
					.getUser(user.getUsername(),
							mEditCurrPassword.getText().toString());
			if (userAux1.getUsername() == null) {
				Toast.makeText(EditProfileActivity.this, "Incorrect password!",
						Toast.LENGTH_SHORT).show();
			} else {

				if (!mEditUsername.getText().toString().trim().equals("")) {
					if (!dbAdapter.getDBUserAdapter(getApplicationContext())
							.checkUsername(mEditUsername.getText().toString(),
									userId)) {

						if (!mEditEmail.getText().toString().trim().equals("")) {

							if (validEmail(mEditEmail.getText().toString())) {

								User userAuxUser = new User(mEditUsername
										.getText().toString(), mEditEmail
										.getText().toString(), mEditNewPassword
										.getText().toString(), dob_var);
								dbAdapter.getDBUserAdapter(
										getApplicationContext()).updateUser(
										user, userAuxUser,
										getApplicationContext());
								finish();
							}

							else {
								Toast.makeText(EditProfileActivity.this,
										"Invalid email!", Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							User userAuxUser = new User(mEditUsername.getText()
									.toString(), mEditEmail.getText()
									.toString(), mEditNewPassword.getText()
									.toString(), dob_var);
							dbAdapter.getDBUserAdapter(getApplicationContext())
									.updateUser(user, userAuxUser,
											getApplicationContext());
							finish();
						}

					} else {
						Toast.makeText(EditProfileActivity.this,
								"Username already in use!", Toast.LENGTH_SHORT)
								.show();
					}

				} else {

					if (!mEditEmail.getText().toString().trim().equals("")) {

						if (validEmail(mEditEmail.getText().toString())) {

							User userAuxUser = new User(mEditUsername.getText()
									.toString(), mEditEmail.getText()
									.toString(), mEditNewPassword.getText()
									.toString(), dob_var);
							dbAdapter.getDBUserAdapter(getApplicationContext())
									.updateUser(user, userAuxUser,
											getApplicationContext());
							finish();
						}

						else {
							Toast.makeText(EditProfileActivity.this,
									"Invalid email!", Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						User userAuxUser = new User(mEditUsername.getText()
								.toString(), mEditEmail.getText().toString(),
								mEditNewPassword.getText().toString(), dob_var);
						dbAdapter.getDBUserAdapter(getApplicationContext())
								.updateUser(user, userAuxUser,
										getApplicationContext());
						finish();
					}
				}
			}
		} else {
			Toast.makeText(EditProfileActivity.this,
					"Provide current password!", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean validEmail(String email) {
		Pattern pattern = Patterns.EMAIL_ADDRESS;
		return pattern.matcher(email).matches();
	}
}