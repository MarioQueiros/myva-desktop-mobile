package com.pt.myva_mobile;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfirmAccountActivity extends Activity {

	EditText mEditUsername;
	EditText mEditCode;
	DBAdapter dbAdapter = new DBAdapter();
	int REQUEST_EXIT = 1024;
	int responseCode = 0;
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Confirm your Account");
		setContentView(R.layout.confirm_account);

		context = getApplicationContext();

		mEditUsername = (EditText) findViewById(R.id.editTextUser);
		mEditCode = (EditText) findViewById(R.id.editTextCode);

		final Button btConfirm = (Button) findViewById(R.id.btnConfirm);

		btConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (!mEditUsername.getText().toString().trim().equals("")
						&& !mEditCode.getText().toString().trim().equals("")) {
					new ConfirmAccount().execute();
				} else {
					Toast.makeText(ConfirmAccountActivity.this,
							"Check all fields!", Toast.LENGTH_SHORT).show();
				}
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

		mEditCode.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		final View root = findViewById(R.id.confirmAccount_id);
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

							mEditCode.setFocusable(false);
							mEditCode.setFocusableInTouchMode(false);
						} else {
							// Soft KeyBoard Shown
						}
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_EXIT) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
	}

	private class ConfirmAccount extends AsyncTask<String, Void, String> {
		String username = "";
		String email = "";
		String birthdate = "";

		ProgressDialog dialog = ProgressDialog.show(
				ConfirmAccountActivity.this, "", "Please wait...");

		@Override
		protected String doInBackground(String... params) {
			getRequest();

			return "";
		}

		private void getRequest() {
			java.util.Date d = new java.util.Date();
			long now = d.getTime() / 1000;

			String server = "";
			try {
				String priv = mEditCode.getText().toString();
				String enc = Utils.hmacSha1(
						(mEditUsername.getText().toString() + now), priv);

				HttpRequestBase request = new HttpGet();
				request.setURI(new URI(Utils.SERVER_URL + "/test"));
				request.setHeader("Content-Type",
						"application/json; charset=utf-8");
				request.setHeader(new BasicHeader("X-MICROTIME", now + ""));
				request.setHeader(new BasicHeader("X-USERNAME", mEditUsername
						.getText().toString()));
				if (!dbAdapter.getDBUserAdapter(getApplicationContext())
						.checkUsername(mEditUsername.getText().toString())) {
					request.setHeader(new BasicHeader("X-RESENDTYPE",
							"reconfirmation"));
				}
				request.setHeader(new BasicHeader("X-HASH", enc));

				// create HttpClient
				HttpClient httpclient = new MyHttpsClient(
						getApplicationContext());

				// make GET request to the given URL
				HttpResponse httpResponse = httpclient.execute(request);

				responseCode = httpResponse.getStatusLine().getStatusCode();

				try {
					server = httpResponse.getFirstHeader("X-Status-Reason")
							.getValue();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (server.contains("User already confirmed.")) {
					responseCode = 201;
				}

				String responseText = null;
				responseText = EntityUtils.toString(httpResponse.getEntity());

				JSONObject json1;
				try {
					json1 = new JSONObject(responseText);
					username = json1.get("username").toString();
					email = json1.get("email").toString();
					birthdate = json1.get("birthday").toString();

					if (!username.trim().equals("")) {
						responseCode = 201;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

		protected void onPreExecute() {
			dialog.show();
		}

		protected void onPostExecute(String result) {

			if (responseCode == 200) {

				dbAdapter.getDBUserAdapter(getApplicationContext())
						.updateUserPrivateKey(
								mEditUsername.getText().toString(),
								mEditCode.getText().toString());
				Intent myIntent = new Intent(context, LoginActivity.class);
				startActivity(myIntent);

				if (dialog != null)
					dialog.dismiss();

				ConfirmAccountActivity.this.finish();

			} else if (responseCode == 201) {
				if (dbAdapter.getDBUserAdapter(getApplicationContext())
						.checkUsername(mEditUsername.getText().toString())) {
					if (dialog != null)
						dialog.dismiss();

					Toast.makeText(context, "User already confirmed!!",
							Toast.LENGTH_SHORT).show();
				} else {

					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					java.util.Date dob_var = null;
					try {
						dob_var = sdf.parse(birthdate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					dbAdapter.getDBUserAdapter(context).insertUser(username,
							email, dob_var);

					dbAdapter.getDBUserAdapter(getApplicationContext())
							.updateUserPrivateKey(username,
									mEditCode.getText().toString());

					Intent myIntent = new Intent(context, LoginActivity.class);
					startActivity(myIntent);

					if (dialog != null)
						dialog.dismiss();
					
					ConfirmAccountActivity.this.finish();
				}
			} else {
				if (dialog != null)
					dialog.dismiss();

				Toast.makeText(context, "Wrong username or code!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
