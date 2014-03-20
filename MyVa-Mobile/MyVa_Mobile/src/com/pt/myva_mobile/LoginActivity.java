package com.pt.myva_mobile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	EditText mEditUsername;
	EditText mEditPassword;
	DBAdapter dbAdapter = new DBAdapter();
	int REQUEST_EXIT = 1024;
	int responseCode;
	private String key = "";
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Login to your Account");
		setContentView(R.layout.login);
		context = getApplicationContext();
		mEditUsername = (EditText) findViewById(R.id.editTextUsername);
		mEditPassword = (EditText) findViewById(R.id.editTextPassword);

		TextView registerScreen = (TextView) findViewById(R.id.link_to_register);

		TextView confirmScreen = (TextView) findViewById(R.id.link_to_confirm);

		// TextView resendPass = (TextView)
		// findViewById(R.id.link_to_resend_pass);

		String text = registerScreen.getText().toString();
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
		registerScreen.setText(spanString);

		text = confirmScreen.getText().toString();
		spanString = new SpannableString(text);
		spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
		confirmScreen.setText(spanString);

		// text = resendPass.getText().toString();
		// spanString = new SpannableString(text);
		// spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
		// resendPass.setText(spanString);

		final Button btLogin = (Button) findViewById(R.id.btnLogin);

		registerScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivityForResult(i, REQUEST_EXIT);
			}
		});

		confirmScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						ConfirmAccountActivity.class);
				startActivityForResult(i, REQUEST_EXIT);
			}
		});

		// resendPass.setOnClickListener(new View.OnClickListener() {
		//
		// public void onClick(View v) {
		// Intent i = new Intent(getApplicationContext(),
		// ReSendPassActivity.class);
		// startActivityForResult(i, REQUEST_EXIT);
		// }
		// });

		btLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (!mEditUsername.getText().toString().trim().equals("")
						&& !mEditPassword.getText().toString().trim()
								.equals("")) {
					// User user = dbAdapter.getDBUserAdapter(
					// getApplicationContext()).getUser(
					// mEditUsername.getText().toString(),
					// mEditPassword.getText().toString());
					//
					// if (user.getUsername() == null) {
					//
					// Toast.makeText(LoginActivity.this,
					// "Invalid credentials!", Toast.LENGTH_SHORT)
					// .show();
					// } else {
					// Intent i = new Intent(LoginActivity.this,
					// EventsActivity.class);
					// startActivity(i);
					// SaveSharedPreference.setUserName(
					// getApplicationContext(), user.getUsername());
					// finish();
					// }

					new LoginAccount().execute();
				} else {
					Toast.makeText(LoginActivity.this, "Insert credentials!",
							Toast.LENGTH_SHORT).show();
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

		mEditPassword.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		final View root = findViewById(R.id.login_id);
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

							mEditPassword.setFocusable(false);
							mEditPassword.setFocusableInTouchMode(false);
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

	private class LoginAccount extends AsyncTask<String, Void, String> {
		ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
				"Logging in...");

		@Override
		protected String doInBackground(String... params) {
			postRequest();

			return "";
		}

		private void postRequest() {
			JSONObject object = new JSONObject();
			InputStream inputStream = null;
			String result = "";
			DefaultHttpClient client = new DefaultHttpClient();

			java.util.Date d = new java.util.Date();
			long now = d.getTime() / 1000;

			String auth = "";

			try {
				auth = Utils.makeSHA1Hash((mEditUsername.getText().toString()
						+ ":" + mEditPassword.getText().toString()));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			try {

				client = new MyHttpsClient(getApplicationContext());

				String server = "";
				HttpRequestBase request = new HttpPost();
				request.setURI(new URI(Utils.SERVER_URL + "/login"));
				request.setHeader("Content-Type",
						"application/json; charset=utf-8");
				request.setHeader("Authentication", auth);
				request.setHeader("X-MICROTIME", now + "");
				//
				try {
					object.put("password", mEditPassword.getText().toString());
					object.put("username", mEditUsername.getText().toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				//
				HttpPost post = new HttpPost(Utils.SERVER_URL + "/login");
				post = (HttpPost) request;

				String json = object.toString();
				post.setEntity(new StringEntity(json));

				// client = new DefaultHttpClient();

				HttpResponse response = client.execute(request);
				responseCode = response.getStatusLine().getStatusCode();
				// receive response as inputStream
				inputStream = response.getEntity().getContent();

				server = response.getFirstHeader("X-Status-Reason").getValue();
				if (server.contains("User not registered")) {
					responseCode = 201;
				} else if (server.contains("Password incorrect"))
					responseCode = 202;

				// convert inputstream to string
				if (inputStream != null)
					result = Utils.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

				if (result != "") {
					String[] pubKey = result.split(":");
					result = pubKey[1];
					key = result.substring(1, result.length() - 2);
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

				if (dbAdapter.getDBUserAdapter(getApplicationContext())
						.checkUsername(mEditUsername.getText().toString())) {

					SaveSharedPreference.setUserName(context, mEditUsername
							.getText().toString());
					SaveSharedPreference.setPublicKey(context, key);

					Intent i = new Intent(LoginActivity.this,
							EventsActivity.class);
					startActivity(i);

					if (dialog != null)
						dialog.dismiss();
					LoginActivity.this.finish();
				} else {

					new ReSendUserConfirmationEmail().execute();

					if (dialog != null)
						dialog.dismiss();
				}

			} else if (responseCode == 201) {
				if (dialog != null)
					dialog.dismiss();

				Toast.makeText(getApplicationContext(), "User not registered!",
						Toast.LENGTH_SHORT).show();
			} else if (responseCode == 202) {
				if (dialog != null)
					dialog.dismiss();

				Toast.makeText(getApplicationContext(), "Incorrect password!",
						Toast.LENGTH_SHORT).show();
			} else {
				if (dialog != null)
					dialog.dismiss();

				Toast.makeText(getApplicationContext(),
						"Error during process!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class ReSendUserConfirmationEmail extends
			AsyncTask<String, Void, String> {
		ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
				"Logging in...");

		@Override
		protected String doInBackground(String... params) {
			postRequest();
			return "";
		}

		private void postRequest() {

			DefaultHttpClient client = new DefaultHttpClient();

			java.util.Date d = new java.util.Date();
			long now = d.getTime() / 1000;

			try {

				client = new MyHttpsClient(getApplicationContext());

				HttpRequestBase request = new HttpGet();
				request.setURI(new URI(Utils.SERVER_URL
						+ "/resendconfirmationemail"));
				request.setHeader("Content-Type",
						"application/json; charset=utf-8");
				request.setHeader("X-MICROTIME", now + "");
				request.setHeader("X-USERNAME", mEditUsername.getText()
						.toString());

				HttpGet get = new HttpGet(Utils.SERVER_URL
						+ "/resendconfirmationemail");
				get = (HttpGet) request;

				HttpResponse response = client.execute(request);
				responseCode = response.getStatusLine().getStatusCode();

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
				Intent i = new Intent(LoginActivity.this,
						ConfirmAccountActivity.class);
				startActivity(i);

				if (dialog != null)
					dialog.dismiss();
				finish();
			}
		}
	}
}