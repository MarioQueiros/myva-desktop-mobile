package com.pt.myva_mobile;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;

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

public class ReSendPassActivity extends Activity {

	EditText mEditUsername;
	DBAdapter dbAdapter = new DBAdapter();
	int REQUEST_EXIT = 1024;
	int responseCode = 0;
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Resend Password");
		setContentView(R.layout.resendpass);

		context = getApplicationContext();

		mEditUsername = (EditText) findViewById(R.id.editTextUsername);

		final Button btSend = (Button) findViewById(R.id.btnSend);

		btSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (!mEditUsername.getText().toString().trim().equals("")) {
					new RequestPassword().execute();
				} else {
					Toast.makeText(ReSendPassActivity.this, "Insert username!",
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

	private class RequestPassword extends AsyncTask<String, Void, String> {
		String username = "";

		ProgressDialog dialog = ProgressDialog.show(ReSendPassActivity.this,
				"", "Please wait...");

		@Override
		protected String doInBackground(String... params) {
			getRequest();
			return "";
		}

		private void getRequest() {
			java.util.Date d = new java.util.Date();
			long now = d.getTime() / 1000;

			try {
				username = mEditUsername.getText().toString();

				String priv = dbAdapter.getDBUserAdapter(
						getApplicationContext()).getUserPrivateKey(username);

				String enc = Utils.hmacSha1((username + now), priv);

				HttpRequestBase request = new HttpGet();
				request.setURI(new URI(Utils.SERVER_URL + "/test"));
				request.setHeader("Content-Type",
						"application/json; charset=utf-8");
				request.setHeader(new BasicHeader("X-MICROTIME", now + ""));
				request.setHeader(new BasicHeader("X-USERNAME", username));
				request.setHeader(new BasicHeader("X-HASH", enc));

				// create HttpClient
				HttpClient httpclient = new MyHttpsClient(
						getApplicationContext());

				// make GET request to the given URL
				HttpResponse httpResponse = httpclient.execute(request);

				responseCode = httpResponse.getStatusLine().getStatusCode();

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

		protected void onPreExecute() {
			dialog.show();
		}

		protected void onPostExecute(String result) {

			if (dialog != null)
				dialog.dismiss();

			if (responseCode == 200) {
				ReSendPassActivity.this.finish();
			} else {
				Toast.makeText(context, "Username doesn't exists!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
