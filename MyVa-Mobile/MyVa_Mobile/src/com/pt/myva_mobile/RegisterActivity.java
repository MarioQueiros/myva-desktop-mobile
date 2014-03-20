package com.pt.myva_mobile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	EditText mEditUsername;
	EditText mEditPassword;
	EditText mEditEmail;
	EditText mEditBirthdate;
	DBAdapter dbAdapter = new DBAdapter();
	int responseCode = 0;
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set View to register.xml
		setContentView(R.layout.register);
		setTitle("Register New Account");
		context = getApplicationContext();
		mEditUsername = (EditText) findViewById(R.id.reg_username);
		mEditPassword = (EditText) findViewById(R.id.reg_password);
		mEditEmail = (EditText) findViewById(R.id.reg_email);
		mEditBirthdate = (EditText) findViewById(R.id.reg_birthdate);

		Button btRegister = (Button) findViewById(R.id.btnRegister);
		btRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkUser();
			}
		});

		TextView loginScreen = (TextView) findViewById(R.id.link_to_login);

		String text = loginScreen.getText().toString();
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
		loginScreen.setText(spanString);

		loginScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// Closing registration screen
				// Switching to Login Screen/closing register screen
				finish();
			}
		});

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

		mEditPassword.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		final View root = findViewById(R.id.register_id);
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

							mEditPassword.setFocusable(false);
							mEditPassword.setFocusableInTouchMode(false);
						} else {
							// Soft KeyBoard Shown
						}
					}
				});
	}

	public void showDatePickerDialog(View v) {
		new DatePickerFragment((EditText) v).show(getFragmentManager(),
				"datePicker");
	}

	public void checkUser() {
		if (!mEditUsername.getText().toString().trim().equals("")
				&& !mEditPassword.getText().toString().trim().equals("")
				&& !mEditBirthdate.getText().toString().trim().equals("")
				&& validEmail(mEditEmail.getText().toString())) {

			// SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			// java.util.Date dob_var = null;
			// try {
			// dob_var = sdf.parse(mEditBirthdate.getText().toString());
			// } catch (ParseException e) {
			// e.printStackTrace();
			// }
			//
			// if (!dbAdapter.getDBUserAdapter(getApplicationContext())
			// .checkUsername(mEditUsername.getText().toString())) {
			// dbAdapter.getDBUserAdapter(getApplicationContext()).insertUser(
			// mEditUsername.getText().toString(),
			// mEditEmail.getText().toString(), dob_var,
			// mEditPassword.getText().toString());
			//
			// new LongRunningGetIO().execute();
			//
			// Intent i = new Intent(getApplicationContext(),
			// EventsActivity.class);
			// startActivity(i);
			// SaveSharedPreference.setUserName(getApplicationContext(),
			// mEditUsername.getText().toString());
			// setResult(RESULT_OK, null);
			//
			// finish();
			// } else {
			// Toast.makeText(getApplicationContext(),
			// "Username already exists!", Toast.LENGTH_SHORT).show();
			// }

			// call AsynTask to perform network operation on separate thread
			new RegisterAccount().execute();
		} else {
			Toast.makeText(getApplicationContext(), "Checks all fields!",
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean validEmail(String email) {
		Pattern pattern = Patterns.EMAIL_ADDRESS;
		return pattern.matcher(email).matches();
	}

	private class RegisterAccount extends AsyncTask<String, Void, String> {
		ProgressDialog dialog = ProgressDialog.show(RegisterActivity.this, "",
				"Saving, Please wait...");

		@Override
		protected String doInBackground(String... params) {
			postRequest();

			return "";
		}

		private void postRequest() {
			JSONObject object = new JSONObject();
			try {
				String str = mEditBirthdate.getText().toString()
						.replace('/', '-');

				object.put("birthday", str);
				object.put("email", mEditEmail.getText().toString());
				object.put("password", mEditPassword.getText().toString());
				object.put("username", mEditUsername.getText().toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			java.util.Date d = new java.util.Date();
			long now = d.getTime() / 1000;
			DefaultHttpClient client = null;

			try {

				String server = "";

				HttpRequestBase request = new HttpPost();
				request.setURI(new URI(Utils.SERVER_URL + "/register"));
				request.setHeader("Content-Type",
						"application/json; charset=utf-8");
				request.setHeader(new BasicHeader("X-MICROTIME", now + ""));

				HttpPost post = new HttpPost(Utils.SERVER_URL + "/register");
				post = (HttpPost) request;

				String json = object.toString();

				post.setEntity(new StringEntity(json));

				client = new MyHttpsClient(getApplicationContext());

				HttpResponse response = client.execute(request);

				responseCode = response.getStatusLine().getStatusCode();

				server = response.getFirstHeader("X-Status-Reason").getValue();
				if (server.contains("Username already registered")) {
					responseCode = 201;
				} else if (server.contains("Email already registered")) {
					responseCode = 202;
				}

			} catch (ClientProtocolException e) {
				client.getConnectionManager().shutdown();
				e.printStackTrace();
			} catch (IOException e) {
				client.getConnectionManager().shutdown();
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		protected void onPreExecute() {
			dialog.show();
		}

		protected void onPostExecute(String result) {

			if (responseCode == 200) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				java.util.Date dob_var = null;
				try {
					dob_var = sdf.parse(mEditBirthdate.getText().toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				dbAdapter.getDBUserAdapter(getApplicationContext()).insertUser(
						mEditUsername.getText().toString(),
						mEditEmail.getText().toString(), dob_var);

				Intent myIntent = new Intent(context,
						ConfirmAccountActivity.class);
				startActivity(myIntent);

				if (dialog != null)
					dialog.dismiss();

				RegisterActivity.this.finish();

			} else if (responseCode == 201) {
				if (dialog != null)
					dialog.dismiss();

				Toast.makeText(getApplicationContext(),
						"Username already registered!", Toast.LENGTH_SHORT)
						.show();

			} else if (responseCode == 202) {
				if (dialog != null)
					dialog.dismiss();

				Toast.makeText(getApplicationContext(),
						"Email already registered!", Toast.LENGTH_SHORT).show();

			} else {
				if (dialog != null)
					dialog.dismiss();

				Toast.makeText(getApplicationContext(), "Error during process!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
