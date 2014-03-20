package com.pt.myva_mobile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditEventActivity extends Activity {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private static final int MY_REQUEST_CODE = 1;
	private static final int MY_REQUEST_CODE2 = 2;

	int iCount = 0;
	AutoCompleteTextView etName;
	EditText etLocal;
	EditText etDate;
	Double latitude = 0.0;
	Double longitude = 0.0;
	int userId;
	DBAdapter dbAdapter = new DBAdapter();
	Event event;
	Intent i;
	Local local;
	int responseCode;
	boolean new_local = false;
	byte[] byteArray;
	boolean saveImage = false;
	Bitmap imageBitmap;

	protected void onCreate(Bundle savedInstanceState) {
		i = getIntent();
		super.onCreate(savedInstanceState);
		setTitle(R.string.edit_event);
		setContentView(R.layout.edit_event_activity);

		int upId = Resources.getSystem().getIdentifier("up", "id", "android");
		if (upId > 0) {
			ImageView up = (ImageView) findViewById(upId);
			up.setImageResource(R.drawable.ic_action_previous_item);
		}

		event = (Event) i.getSerializableExtra("EVENT");

		userId = dbAdapter.getDBUserAdapter(getApplicationContext()).getUserID(
				SaveSharedPreference.getUserName(getApplicationContext()));

		int id_image = event.getId_image();
		ImageView img = (ImageView) findViewById(R.id.imageView6);
		img.setBackgroundResource(R.layout.image_border);

		if (id_image != 0) {
			Image image = dbAdapter.getDBImageAdapter(getApplicationContext())
					.getImage(id_image);

			Bitmap b1 = BitmapFactory.decodeByteArray(image.getImg(), 0,
					image.getImg().length);

			img.setImageBitmap(b1);
			img.buildDrawingCache();
		} else {
			img.setImageResource(R.drawable.back);
		}

		openMicrophone();
		imageEditionListener();

		List<String> eventsDesc = new ArrayList<String>();
		for (Event event : dbAdapter.getDBEventAdapter(getApplicationContext())
				.getEvents(userId)) {
			eventsDesc.add(event.getName());
		}

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, eventsDesc);

		etName = (AutoCompleteTextView) findViewById(R.id.edit_editTextToShowDesc);
		etName.setText(event.getName());
		etName.setAdapter(adapter1);

		etName.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		etName.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> p, View v, int pos, long id) {

				etName.setFocusable(false);
				etName.setFocusableInTouchMode(false);
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(etName.getWindowToken(), 0);
			}
		});

		final View linearLayout = findViewById(R.id.edit_event_id);
		linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						linearLayout.getWindowVisibleDisplayFrame(r);

						int heightDiff = linearLayout.getRootView().getHeight()
								- (r.bottom - r.top);
						if (heightDiff > 100) {
						} else {
							etName.setFocusable(false);
							etName.setFocusableInTouchMode(false);
						}
					}
				});

		Local loc = dbAdapter.getDBLocalAdapter(getApplicationContext())
				.getLocal(event);
		etLocal = (EditText) findViewById(R.id.edit_editTextToShowLocal);
		etLocal.setText(loc.getName());
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();

		Format formatter = new SimpleDateFormat("MM/dd/yyyy");
		String str = formatter.format(event.getCalendar().getTime());
		etDate = (EditText) findViewById(R.id.edit_editTextToShowDate);
		etDate.setText(str);

		List<String> localsName = new ArrayList<String>();
		for (Local local : dbAdapter.getDBLocalAdapter(getApplicationContext())
				.getLocals(userId, getApplicationContext())) {
			localsName.add(local.getName());
		}
		localsName = removeDuplicateAndOrder(localsName);

		Spinner s = (Spinner) findViewById(R.id.spinner2);
		ArrayAdapter<?> adapter = new ArrayAdapter<Object>(this,
				R.layout.spinner_item, localsName.toArray());
		s.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_event_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit_event_save:
			saveData();
			break;
		case R.id.action_edit_event_microphone:
			if (isNetworkConnected()) {
				startVoiceRecognitionActivity();
			} else {
				showAlertDialog("Voice recognition not available.");
			}
			break;
		case R.id.action_edit_event_map:
			if (isNetworkConnected()) {
				Intent j = new Intent(EditEventActivity.this, MapActivity.class);
				j.putExtra("LAT", latitude);
				j.putExtra("LON", longitude);
				startActivityForResult(j, MY_REQUEST_CODE);
			} else {
				showAlertDialog("Map not available.");
			}
			break;
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return true;
	}

	public void showDatePickerDialog(View v) {
		new DatePickerFragment((EditText) v).show(getFragmentManager(),
				"datePicker");
	}

	public void saveData() {
		boolean save = false;
		boolean insertLocal = false;
		String localName = "";
		Calendar cal = Calendar.getInstance();
		java.util.Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		// long id_localType = 1;

		AutoCompleteTextView etName = (AutoCompleteTextView) findViewById(R.id.edit_editTextToShowDesc);
		EditText etLocal = (EditText) findViewById(R.id.edit_editTextToShowLocal);
		EditText etDate = (EditText) findViewById(R.id.edit_editTextToShowDate);

		// RadioGroup rdGroup = (RadioGroup) findViewById(R.id.radioGroup2);

		CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox3);
		Spinner spinner = (Spinner) findViewById(R.id.spinner2);

		if (!etName.getText().toString().trim().equals("")) {
			if (!etDate.getText().toString().trim().equals("")) {
				if (!etLocal.getText().toString().trim().equals("")
						|| checkBox.isChecked()) {
					try {
						d = sdf.parse(etDate.getText().toString());
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
					cal.setTime(d);
					if (!Utils.getStrDay(cal).equals("error")) {

						if (checkBox.isChecked()) {
							localName = spinner.getSelectedItem().toString();
							local = dbAdapter.getDBLocalAdapter(
									getApplicationContext()).getLocalByName(
									localName);
						} else {
							localName = etLocal.getText().toString();

							// int selectedId =
							// rdGroup.getCheckedRadioButtonId();
							// RadioButton radioButton = (RadioButton)
							// findViewById(selectedId);
							// String localType =
							// radioButton.getText().toString();

							// id_localType = dbAdapter.getDBLocalTypeAdapter(
							// getApplicationContext()).getIDByLocalType(
							// localType);
							insertLocal = true;
						}
						save = true;
					} else {
						Toast.makeText(getApplicationContext(),
								"Invalid date!", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Checks all fields!", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Checks all fields!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Checks all fields!",
					Toast.LENGTH_SHORT).show();
		}

		if (save) {
			if (insertLocal) {
				// if ((int) id_localType != 1) {
				//
				// if (dbAdapter.getDBLocalAdapter(getApplicationContext())
				// .getPublicLocalByName(localName) == null) {
				//
				// id_local = dbAdapter.getDBLocalAdapter(
				// getApplicationContext()).insertLocal(
				// new Local(latitude, longitude, localName,
				// (int) id_localType));
				// } else {
				//
				// Local local = dbAdapter.getDBLocalAdapter(
				// getApplicationContext()).getPublicLocalByName(
				// localName);
				// id_local = local.getId();
				// }
				//
				// } else {
				if (dbAdapter.getDBLocalAdapter(getApplicationContext())
						.getLocalByName(localName) == null) {
					local = new Local(latitude, longitude, localName);
					long id_local = dbAdapter.getDBLocalAdapter(
							getApplicationContext()).insertLocal(local);
					local.setId((int) id_local);
					new_local = true;
				} else {
					local = dbAdapter
							.getDBLocalAdapter(getApplicationContext())
							.getLocalByName(localName);
				}
				// }
			}

			long id_image = -1;
			if (saveImage) {
				id_image = dbAdapter.getDBImageAdapter(getApplicationContext())
						.insertImage(new Image(byteArray));
			} else {
				id_image = 0;
			}

			if (new_local == true) {
				dbAdapter.getDBUserLocalAdapter(getApplicationContext())
						.insertUserLocal((int) userId, local.getId());
			}
			Event eve = new Event(etName.getText().toString(), (int) userId,
					local.getId(), (int) id_image, cal);
			int id = event.getId();
			dbAdapter.getDBEventAdapter(getApplicationContext()).updateEvent2(
					event, eve);
			event = eve;
			event.setId(id);
			new EditEventSync().execute();
			Utils.setEdited(true);
		}
	}

	@SuppressLint("ShowToast")
	void openMicrophone() {
		// Disable button if no recognition service is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			Toast.makeText(getApplicationContext(), "Recognizer Not Found",
					1000).show();
		}
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"AndroidBite Voice Recognition...");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {
				ArrayList<String> textMatchList = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				if (!textMatchList.isEmpty()) {
					// If first Match contains the 'search' word
					// Then start web search.
					if (textMatchList.get(0).contains("search")) {

						String searchQuery = textMatchList.get(0);
						searchQuery = searchQuery.replace("search", "");
						Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
						search.putExtra(SearchManager.QUERY, searchQuery);
						startActivity(search);

					} else {
						// populate the Matches
						EditText ed = (EditText) findViewById(R.id.edit_editTextToShowDesc);
						String text = textMatchList.get(0);
						if (text.contains("clear")) {
							ed.setText("");
						} else {
							ed.setText(text);
						}
					}
				}
				// Result code for various error.
			} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
				showToastMessage("Audio Error");
			} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
				showToastMessage("Client Error");
			} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
				showToastMessage("Network Error");
			} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
				showToastMessage("No Match");
			} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
				showToastMessage("Server Error");
			}
			super.onActivityResult(requestCode, resultCode, data);
		} else if (requestCode == MY_REQUEST_CODE2) {
			if (data.hasExtra("EXTRA_MESSAGE")) {
				byteArray = data.getByteArrayExtra("EXTRA_MESSAGE");
				imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0,
						byteArray.length);
				saveImage = true;
				ImageView img = (ImageView) findViewById(R.id.imageView6);
				img.setImageBitmap(imageBitmap);
				img.buildDrawingCache();
			}
		} else {
			getMapResult(data.getStringExtra("EXTRA_MESSAGE"));
		}
	}

	public void getMapResult(String msg) {
		TextView tv = (TextView) findViewById(R.id.edit_editTextToShowLocal);
		if (!msg.equals("")) {

			StringTokenizer tokens = new StringTokenizer(msg, "@");
			String first = tokens.nextToken();
			String second = tokens.nextToken();

			String address = "";
			try {
				latitude = Double.parseDouble(tokens.nextToken());
				longitude = Double.parseDouble(tokens.nextToken());
			} catch (Exception e) {
			}

			if ((!first.equals("no") && !isNumeric(first))
					&& (!second.equals("no") && !isNumeric(second))) {
				address = first + ", " + second;
			} else {
				if (!first.equals("no") && !isNumeric(first)) {

					address = first;
				} else {
					if (!isNumeric(second))
						address = second;
				}
			}
			tv.setText(address);
		}
	}

	public static boolean isNumeric(String str) {
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Helper method to show the toast message
	 **/
	void showToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	public void imageEditionListener() {
		ImageView imageEdition = (ImageView) findViewById(R.id.imageView6);

		imageEdition.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(EditEventActivity.this,
						ImageEditionActivity.class);
				startActivityForResult(i, MY_REQUEST_CODE2);
			}
		});

	}

	private void showAlertDialog(String err) {

		final AlertDialog alertDialog = new AlertDialog.Builder(
				EditEventActivity.this).create();

		alertDialog.setTitle("Info");
		alertDialog.setIcon(R.drawable.ic_action_error);
		alertDialog.setMessage(err + "\nCheck your internet connectivity!");
		alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.cancel();
					}

				});
		alertDialog.show();
	}

	public List<String> removeDuplicateAndOrder(List<String> localsName) {

		Set<String> set = new HashSet<String>();
		set.addAll(localsName);
		localsName.clear();
		localsName.addAll(set);

		Collections.sort(localsName);

		return localsName;
	}

	private class EditEventSync extends AsyncTask<String, Void, String> {
		String str_value = "";
		String str_value2 = "";
		long timestamp = 0;
		ProgressDialog dialog = ProgressDialog.show(EditEventActivity.this, "",
				"Please wait...");

		@Override
		protected String doInBackground(String... params) {
			postRequest();
			return "";
		}

		private void postRequest() {
			JSONObject object = new JSONObject();
			DefaultHttpClient client = new DefaultHttpClient();
			String username = SaveSharedPreference
					.getUserName(getApplicationContext());

			java.util.Date d = new java.util.Date();
			long now = d.getTime() / 1000;

			String enc = null;
			try {
				String pubKey = SaveSharedPreference
						.getPublicKey(getApplicationContext());
				String priKey = dbAdapter.getDBUserAdapter(
						getApplicationContext()).getUserPrivateKey(username);

				enc = Utils.hmacSha1((pubKey + now), priKey);
			} catch (InvalidKeyException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}

			String str = event.getCalendar().getTimeInMillis() + "";
			long longDate = Long.valueOf(str);

			try {
				client = new MyHttpsClient(getApplicationContext());

				HttpRequestBase request = new HttpPost();
				request.setURI(new URI(Utils.SERVER_URL + "/editevent"));

				request.setHeader("Content-Type",
						"application/json; charset=utf-8");
				request.setHeader(new BasicHeader("X-PUBKEY",
						SaveSharedPreference
								.getPublicKey(getApplicationContext())));
				request.setHeader(new BasicHeader("X-MICROTIME", now + ""));
				request.setHeader(new BasicHeader("X-HASH", enc));
				request.setHeader(new BasicHeader("X-USERNAME", username));
				request.setHeader(new BasicHeader("X-NAME", event.getName()));
				request.setHeader(new BasicHeader("X-DATE", longDate + ""));

				//
				ArrayList<String> list = dbAdapter.getDBEventAdapter(
						getApplicationContext()).getEventServerData(
						event.getName());
				request.setHeader(new BasicHeader("X-TIMESTAMP", list.get(1)));
				request.setHeader(new BasicHeader("X-EVENTID", list.get(0)));
				//

				if (new_local == true) {

					request.setHeader(new BasicHeader("X-LOCALNAME", local
							.getName()));
					request.setHeader(new BasicHeader("X-LATITUDE", local
							.getLatitude() + ""));
					request.setHeader(new BasicHeader("X-LONGITUDE", local
							.getLongitude() + ""));
				} else {
					request.setHeader(new BasicHeader("X-LOCALID", dbAdapter
							.getDBLocalAdapter(getApplicationContext())
							.getLocalServerID(local.getName())));
				}

				HttpPost post = new HttpPost(Utils.SERVER_URL + "/editevent");
				post = (HttpPost) request;

				String njson = object.toString();
				post.setEntity(new StringEntity(njson));

				HttpResponse response = client.execute(request);
				responseCode = response.getStatusLine().getStatusCode();

				String server = "";
				String responseText = null;
				responseText = EntityUtils.toString(response.getEntity());
				if (response.getStatusLine().getStatusCode() != 200) {
					server = response.getFirstHeader("X-Status-Reason")
							.getValue();
					String x = server;
				} else {
					JSONObject json1;
					try {
						json1 = new JSONObject(responseText);
						str_value = json1.getString("EditEventID");
						str_value2 = json1.getString("LocalID");
						timestamp = json1.getLong("Timestamp");
					} catch (JSONException e) {
						e.printStackTrace();
					}
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
				if (new_local == true) {
					dbAdapter.getDBLocalAdapter(getApplicationContext())
							.updateServerID(local, str_value2);
				}
				dbAdapter.getDBEventAdapter(getApplicationContext())
						.updateEvent(event, timestamp, str_value);

				if (dialog != null)
					dialog.dismiss();
			} else {
				if (dialog != null)
					dialog.dismiss();

				Toast.makeText(getApplicationContext(),
						"Error during process!", Toast.LENGTH_SHORT).show();
			}

			finish();
		}
	}

}