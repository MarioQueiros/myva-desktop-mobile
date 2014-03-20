package com.pt.myva_mobile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class EventsActivity extends Activity implements
		TextToSpeech.OnInitListener {

	EventsListAdapter dataAdapter = null;
	Calendar c = Calendar.getInstance();
	int userId;
	DBAdapter dbAdapter = new DBAdapter();
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	int slideMenuNumbers[];
	ArrayList<Event> eventList;
	ArrayList<Event> eventsListFinal;
	SlideMenu adapter;
	int slideMenuPosition = 0;

	private TextToSpeech speech;
	private static final ScheduledExecutorService worker = Executors
			.newSingleThreadScheduledExecutor();
	// Publicity
	private AdView adView;
	int responseCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.events_all);
		setContentView(R.layout.activity_events);

		userId = dbAdapter.getDBUserAdapter(getApplicationContext()).getUserID(
				SaveSharedPreference.getUserName(getApplicationContext()));

		slideMenuCreation();

		// Create the adView.
		adView = new AdView(this, AdSize.SMART_BANNER,
				"ca-app-pub-5724491097453649/5012063011");
		adView.setAdListener(new MyClass());

		AdRequest adRequest = new AdRequest();
		// check if GPS enabled
		GPSTracker gpsTracker = new GPSTracker(this);
		if (gpsTracker.canGetLocation()) {
			adRequest.setLocation(gpsTracker.location);
		}

		// displayListView();
		speech = new TextToSpeech(getApplicationContext(), this);

		new LocalsSynch().execute();
		new EventsSynch().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_synchronize:
			new LocalsSynch().execute();
			new EventsSynch().execute();
			break;
		case R.id.action_add:
			Intent j = new Intent(EventsActivity.this,
					CreateEventActivity.class);
			startActivity(j);
			break;
		case R.id.action_settings:
			Intent i = new Intent(EventsActivity.this, ProfileActivity.class);
			i.putExtra("ID", userId);
			startActivity(i);
			break;
		case R.id.action_help:
			sendEmail();
			break;
		case R.id.action_logout:
			logout();
			break;
		case android.R.id.home:
			// Slide Menu - fazer com que seja possivel clicar no botao e ir
			// para o slide menu
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
			break;
		default:
			break;
		}

		return true;
	}

	private void displayListView() {

		slideMenuNumbers = new int[4];

		String strTodayDate = Utils.getSimpleDate(c) + ", "
				+ c.get(Calendar.YEAR);
		TextView t = (TextView) findViewById(R.id.txtView2);
		t.setText(strTodayDate);

		// Array list of events
		eventList = dbAdapter.getDBEventAdapter(getApplicationContext())
				.getEvents(userId);

		String[] eventsNum = fillArrayNumberEvent();

		// mDrawerList.setAdapter(eventsNum);
		adapter.setEventsNum(eventsNum);
		mDrawerList.setAdapter(adapter);

		if (Utils.getGetEventsByDate() == "All") {
			mDrawerList.setItemChecked(0, true);
		} else if (Utils.getGetEventsByDate() == "Today") {
			mDrawerList.setItemChecked(1, true);
		} else if (Utils.getGetEventsByDate() == "Tomorrow") {
			mDrawerList.setItemChecked(2, true);
		} else {
			mDrawerList.setItemChecked(3, true);
		}

		slideMenuOptionsListener();

		slideMenuButtonsListener();
		// create an ArrayAdaptar from the String Array
		dataAdapter = new EventsListAdapter(this, R.layout.event_info,
				eventList);
		final ListView listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent i = new Intent(EventsActivity.this, EventActivity.class);
				i.putExtra("OBJECT", eventList.get(arg2));
				startActivity(i);
			}
		});
	}

	private String[] fillArrayNumberEvent() {

		final ArrayList<Event> eventList = dbAdapter.getDBEventAdapter(
				getApplicationContext()).getAllEvents(userId);

		for (Event ev : eventList) {
			int difference = ((int) ((ev.getCalendar().getTime().getTime() / (24 * 60 * 60 * 1000)) - (int) (Calendar
					.getInstance().getTime().getTime() / (24 * 60 * 60 * 1000))));

			// All
			if (difference > -1) {
				slideMenuNumbers[0]++;
			}
			// Today
			if (difference == 0) {
				slideMenuNumbers[1]++;
			}
			// Tomorrow
			if (difference == 1) {
				slideMenuNumbers[2]++;
			}
			// Next 7 Days
			if (difference > 0 && difference < 8) {
				slideMenuNumbers[3]++;
			}
		}

		String[] eventsNum = new String[4];
		for (int i = 0; i < slideMenuNumbers.length; i++) {
			eventsNum[i] = slideMenuNumbers[i] + "";
		}

		return eventsNum;
	}

	private class EventsListAdapter extends ArrayAdapter<Event> implements
			CompoundButton.OnCheckedChangeListener {

		private ArrayList<Event> eventList;
		SparseBooleanArray mCheckStates;

		public EventsListAdapter(Context context, int textViewResourceId,
				ArrayList<Event> eventList) {
			super(context, textViewResourceId, eventList);
			this.eventList = new ArrayList<Event>();
			this.eventList.addAll(eventList);
			mCheckStates = new SparseBooleanArray(eventList.size());
		}

		private class ViewHolder {
			CheckBox check;
			TextView name;
			TextView place;
			TextView date;
			TextView year;
			TextView strDate;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.event_info, parent, false);

				holder = new ViewHolder();
				holder.check = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				holder.name = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.place = (TextView) convertView
						.findViewById(R.id.textView2);
				holder.strDate = (TextView) convertView
						.findViewById(R.id.textView4);
				holder.date = (TextView) convertView
						.findViewById(R.id.textView3);
				holder.year = (TextView) convertView
						.findViewById(R.id.textView5);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Event event = eventList.get(position);
			holder.check.setTag(position);
			holder.check.setChecked(mCheckStates.get(position, false));
			holder.check.setOnCheckedChangeListener(this);

			holder.name.setText(event.getName());
			holder.name.setTag(event);

			String strPlace = dbAdapter
					.getDBLocalAdapter(getApplicationContext()).getLocal(event)
					.getName();
			if (strPlace.contains(",")) {
				String[] shortAdress = strPlace.split(",");
				holder.place.setText(shortAdress[0]);
			} else {
				holder.place.setText(strPlace);
			}
			holder.place.setTag(event);
			holder.date.setText(Utils.getSimpleDate(event.getCalendar()));
			holder.date.setTag(event);

			holder.strDate.setText(Utils.getStrDay(event.getCalendar()));
			holder.strDate.setTag(event);

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(event.getCalendar().getTimeInMillis());

			holder.year.setText(cal.get(Calendar.YEAR) + "");
			holder.year.setTag(event);

			return convertView;
		}

		public boolean isChecked(int position) {
			return mCheckStates.get(position, false);
		}

		public void setChecked(int position, boolean isChecked) {
			mCheckStates.put(position, isChecked);
		}

		public void toggle(int position) {
			setChecked(position, !isChecked(position));
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			View row = (View) buttonView.getParent().getParent().getParent();
			if (isChecked) {
				// row.setBackgroundResource(android.R.color.holo_blue_light);
			} else {
				// row.setBackgroundResource(android.R.color.transparent);
			}

			mCheckStates.put((Integer) buttonView.getTag(), isChecked);
		}
	}

	public void slideMenuCreation() {
		slideMenuNumbers = new int[4];

		User user = dbAdapter.getDBUserAdapter(getApplicationContext())
				.getUserByID(userId);

		// Nome do user
		TextView user_tv = (TextView) findViewById(R.id.txt1);
		user_tv.setText(user.getUsername());

		// E-mail do user
		TextView email_tv = (TextView) findViewById(R.id.txt2);
		email_tv.setText(user.getEmail());

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// Realizar o slide mais perto do centro do ecra
		try {
			Field mDragger = mDrawerLayout.getClass().getDeclaredField(
					"mLeftDragger");
			mDragger.setAccessible(true);

			ViewDragHelper draggerObj = (ViewDragHelper) mDragger
					.get(mDrawerLayout);

			Field mEdgeSize = draggerObj.getClass().getDeclaredField(
					"mEdgeSize");
			mEdgeSize.setAccessible(true);
			int edge = mEdgeSize.getInt(draggerObj);

			mEdgeSize.setInt(draggerObj, edge * 10);
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
		}

		// Criar o botao para ir ao slide Menu
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ----------------Atualizar a action bar------------------
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.events_all) {

			public void onDrawerClosed(View view) {
				switch (slideMenuPosition) {
				case 0:
					getActionBar().setTitle(R.string.events_all);
					invalidateOptionsMenu();
					break;
				case 1:
					getActionBar().setTitle(R.string.events_today);
					invalidateOptionsMenu();
					break;
				case 2:
					getActionBar().setTitle(R.string.events_tomorrow);
					invalidateOptionsMenu();
					break;
				case 3:
					getActionBar().setTitle(R.string.events_7days);
					invalidateOptionsMenu();
					break;
				default:
					getActionBar().setTitle(R.string.app_name);
					break;
				}
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(R.string.app_name);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		String[] web = { "All", "Today", "Tomorrow", "Next 7 Days", };

		Integer[] imageId = { android.R.drawable.ic_menu_agenda,
				android.R.drawable.ic_menu_today,
				android.R.drawable.ic_menu_day,
				android.R.drawable.ic_menu_week, };

		String[] eventsNum = { slideMenuNumbers[0] + "",
				slideMenuNumbers[1] + "", slideMenuNumbers[2] + "",
				slideMenuNumbers[3] + "" };

		/*
		 * public void updateReceiptsList(List<Receipt> newlist) {
		 * receiptlist.clear(); receiptlist.addAll(newlist);
		 * this.notifyDataSetChanged(); }
		 */

		adapter = new SlideMenu(EventsActivity.this, web, imageId, eventsNum);

		mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setItemChecked(0, true); // Colocar a primeira opcao da //
												// lista activa
	}

	public void slideMenuOptionsListener() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						mDrawerLayout.closeDrawers();

						slideMenuPosition = position;
						switch (position) {
						case 0:
							getActionBar().setTitle(R.string.events_all);
							Utils.setGetEventsByDate("All");
							displayListView();
							break;
						case 1:
							getActionBar().setTitle(R.string.events_today);
							Utils.setGetEventsByDate("Today");
							displayListView();
							break;
						case 2:
							getActionBar().setTitle(R.string.events_tomorrow);
							Utils.setGetEventsByDate("Tomorrow");
							displayListView();
							break;
						case 3:
							getActionBar().setTitle(R.string.events_7days);
							Utils.setGetEventsByDate("Next7Days");
							displayListView();
							break;
						default:
							getActionBar().setTitle(R.string.help);
							break;
						}
					}
				});

	}

	public void slideMenuButtonsListener() {

		// ------------------------ Botao
		// adicionar----------------------------------------
		Button btnNavigatorAdd = (Button) findViewById(R.id.button_add);

		btnNavigatorAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawerLayout.closeDrawers();

				Intent i = new Intent(EventsActivity.this,
						CreateEventActivity.class);
				startActivity(i);
			}
		});

		// ------------------------- Botao
		// eliminar----------------------------------------
		Button btnNavigatorDelete = (Button) findViewById(R.id.button_delete);

		btnNavigatorDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				eventsListFinal = new ArrayList<Event>();

				for (int i = 0; i < eventList.size(); i++) {
					if (dataAdapter.mCheckStates.get(i) == true) {
						eventsListFinal.add(eventList.get(i));
					}
				}

				if (eventsListFinal.size() > 0) {

					final AlertDialog alertDialog = new AlertDialog.Builder(
							EventsActivity.this).create();

					alertDialog.setTitle("Delete event");
					alertDialog.setIcon(R.drawable.ic_action_warning);

					if (eventsListFinal.size() == 1) {
						alertDialog.setMessage("Delete this event?");
					} else {
						alertDialog.setMessage("Delete this events?");
					}

					alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new DeleteEvent().execute();
								}

							});
					alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									alertDialog.cancel();
								}

							});
					alertDialog.show();

				} else {
					Toast.makeText(EventsActivity.this,
							"Select at least 1 event!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// deslocacao da imagem do slide menu
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	public void sendEmail() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "leniker.gomes@gmail.com" });
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
		i.putExtra(Intent.EXTRA_TEXT, "body of email");
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(EventsActivity.this,
					"There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void logout() {
		final AlertDialog alertDialog = new AlertDialog.Builder(
				EventsActivity.this).create();

		alertDialog.setTitle("Logout");
		alertDialog.setIcon(R.drawable.ic_action_error);
		alertDialog.setMessage("Are you sure?");
		alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SaveSharedPreference.setUserName(
								getApplicationContext(), "");
						finish();
					}

				});

		alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.cancel();
					}
				});

		alertDialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();

		User user = dbAdapter.getDBUserAdapter(getApplicationContext())
				.getUserByID(userId);

		// Nome do user
		TextView user_tv = (TextView) findViewById(R.id.txt1);
		user_tv.setText(user.getUsername());
		// E-mail do user
		TextView email_tv = (TextView) findViewById(R.id.txt2);
		email_tv.setText(user.getEmail());

		if (Utils.isAdded() || Utils.isEdited()) {
			Utils.setAdded(false);
			Utils.setEdited(false);
			displayListView();
		}
	}

	@Override
	public void onInit(int arg0) {
		if (arg0 == TextToSpeech.SUCCESS) {

			int result = speech.setLanguage(Locale.UK);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
				Toast.makeText(EventsActivity.this,
						"This Language is not supported", Toast.LENGTH_SHORT)
						.show();
			} else {
				speak(slideMenuNumbers[1], slideMenuNumbers[2]);
			}
		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}

	private void speak(final int i, final int j) {

		Runnable task = new Runnable() {
			public void run() {
				String text = "";
				String strEvents = "events";

				if (i != 0 && j != 0) {
					if (i == 1) {
						strEvents = "event";
					}
					text = "You have " + i + " " + strEvents + " today and "
							+ j + " tomorrow!";
				} else if (i != 0) {
					if (i == 1) {
						strEvents = "event";
					}
					text = "You have " + i + " " + strEvents + " today!";
				} else if (j != 0) {
					if (j == 1) {
						strEvents = "event";
					}
					text = "You have " + j + " " + strEvents + " tomorrow!";
				}

				speech.setSpeechRate((float) 0.8);
				speech.setPitch((float) 0.9);
				speech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			}
		};
		worker.schedule(task, (long) 1.2, TimeUnit.SECONDS);
	}

	@Override
	public void onDestroy() {
		if (speech != null) {
			speech.stop();
			speech.shutdown();
		}
		adView.destroy();
		super.onDestroy();
	}

	private class LocalsSynch extends AsyncTask<String, Void, String> {
		ProgressDialog dialog = ProgressDialog.show(EventsActivity.this, "",
				"Please wait...");

		@Override
		protected String doInBackground(String... params) {
			getRequest();
			return "";
		}

		private void getRequest() {

			String username = SaveSharedPreference
					.getUserName(getApplicationContext());

			int id_user = dbAdapter.getDBUserAdapter(getApplicationContext())
					.getUserID(username);

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
			try {

				JSONObject object = new JSONObject();
				object.put("username", username);

				HttpClient client = new MyHttpsClient(getApplicationContext());

				URI website = new URI(Utils.SERVER_URL + "/getprivatelocals");
				HttpGet request = new HttpGet();
				request.setURI(website);

				request.setURI(new URI(Utils.SERVER_URL + "/getprivatelocals"));

				request.setHeader("Content-Type",
						"application/json; charset=utf-8");
				request.setHeader(new BasicHeader("X-PUBKEY",
						SaveSharedPreference
								.getPublicKey(getApplicationContext())));
				request.setHeader(new BasicHeader("X-MICROTIME", now + ""));
				request.setHeader(new BasicHeader("X-HASH", enc));
				request.setHeader(new BasicHeader("X-USERNAME", username));

				// make GET request to the given URL
				HttpResponse httpResponse = client.execute(request);

				responseCode = httpResponse.getStatusLine().getStatusCode();

				String responseText = null;
				responseText = EntityUtils.toString(httpResponse.getEntity());
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					String server = httpResponse.getFirstHeader(
							"X-Status-Reason").getValue();
				} else {
					JSONObject json1;
					try {
						json1 = new JSONObject(responseText);
						JSONArray locals = json1.getJSONArray("privateLocals");

						for (int i = 0; i < locals.length(); i++) {
							String server_id = locals.getJSONObject(i)
									.getString("id");
							String latitude = locals.getJSONObject(i)
									.getString("latitude");
							String longitude = locals.getJSONObject(i)
									.getString("longitude");
							String name = locals.getJSONObject(i).getString(
									"name");

							Local local = new Local(
									Double.parseDouble(latitude),
									Double.parseDouble(longitude), name);

							if (dbAdapter.getDBLocalAdapter(
									getApplicationContext()).getLocalByName(
									name) == null) {
								long id_local = dbAdapter.getDBLocalAdapter(
										getApplicationContext()).insertLocal(
										local);
								local.setId((int) id_local);
								dbAdapter.getDBLocalAdapter(
										getApplicationContext())
										.updateServerID(local, server_id);

								dbAdapter.getDBUserLocalAdapter(
										getApplicationContext())
										.insertUserLocal(id_user,
												(int) id_local);
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
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
				// Toast.makeText(getApplicationContext(), "Sucess!",
				// Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(getApplicationContext(),
				// "Error during process!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class EventsSynch extends AsyncTask<String, Void, String> {
		ProgressDialog dialog = ProgressDialog.show(EventsActivity.this, "",
				"Please wait...");

		@Override
		protected String doInBackground(String... params) {
			getRequest();
			return "";
		}

		private void getRequest() {

			String username = SaveSharedPreference
					.getUserName(getApplicationContext());

			int id_user = dbAdapter.getDBUserAdapter(getApplicationContext())
					.getUserID(username);

			ArrayList<String> listServerEvents = new ArrayList<String>();
			ArrayList<Event> listAuxEvents = new ArrayList<Event>();

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
			try {

				JSONObject object = new JSONObject();
				object.put("username", username);

				HttpClient client = new MyHttpsClient(getApplicationContext());

				URI website = new URI(Utils.SERVER_URL + "/getallevents");
				HttpGet request = new HttpGet();
				request.setURI(website);

				request.setURI(new URI(Utils.SERVER_URL + "/getallevents"));

				request.setHeader("Content-Type",
						"application/json; charset=utf-8");
				request.setHeader(new BasicHeader("X-PUBKEY",
						SaveSharedPreference
								.getPublicKey(getApplicationContext())));
				request.setHeader(new BasicHeader("X-MICROTIME", now + ""));
				request.setHeader(new BasicHeader("X-HASH", enc));
				request.setHeader(new BasicHeader("X-USERNAME", username));

				// make GET request to the given URL
				HttpResponse httpResponse = client.execute(request);

				responseCode = httpResponse.getStatusLine().getStatusCode();

				String responseText = null;
				responseText = EntityUtils.toString(httpResponse.getEntity());
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					String server = httpResponse.getFirstHeader(
							"X-Status-Reason").getValue();
				} else {
					JSONObject json1;
					try {
						json1 = new JSONObject(responseText);
						JSONArray events = json1.getJSONArray("events");

						for (int i = 0; i < events.length(); i++) {
							String event_server_id = events.getJSONObject(i)
									.getString("id");
							String local_server_id = events.getJSONObject(i)
									.getString("local");
							String name = events.getJSONObject(i).getString(
									"name");
							String timestamp = events.getJSONObject(i)
									.getString("timestamp");
							String dateevent = events.getJSONObject(i)
									.getString("dateevent");
							// String open = events.getJSONObject(i).getString(
							// "open");

							Calendar calendar = Calendar.getInstance();
							DateFormat formatter = new SimpleDateFormat(
									"yyyy-MM-dd");
							Date date = formatter.parse(dateevent);
							calendar.setTime(date);

							int local_id = dbAdapter.getDBLocalAdapter(
									getApplicationContext()).getLocalID(
									local_server_id);

							Event eveAux = new Event(name, id_user, local_id,
									0, calendar);

							if (dbAdapter.getDBEventAdapter(
									getApplicationContext())
									.getEventByServerID(event_server_id) != null) {

								Event event = dbAdapter.getDBEventAdapter(
										getApplicationContext())
										.getEventByServerID(event_server_id);

								if (Utils.compareTimestamp(
										(Long.parseLong(timestamp) * 1000),
										(event.getTimestampEvent() * 1000))) {

									dbAdapter.getDBEventAdapter(
											getApplicationContext())
											.updateEvent(event, eveAux);

									listServerEvents.add(event_server_id);

								} else {
									listServerEvents.add(event_server_id);
								}
							} else {
								long id = dbAdapter.getDBEventAdapter(
										getApplicationContext()).insertEvent(
										eveAux);
								eveAux.setId((int) id);

								dbAdapter.getDBEventAdapter(
										getApplicationContext()).updateEvent(
										eveAux, (Long.parseLong(timestamp)),
										event_server_id);

								listServerEvents.add(event_server_id);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			ArrayList<String> listLocalEvents = dbAdapter.getDBEventAdapter(
					getApplicationContext()).getLocalEvents(id_user);
			for (String id : listLocalEvents) {
				if (!listServerEvents.contains(id)) {
					listAuxEvents.add(dbAdapter.getDBEventAdapter(
							getApplicationContext()).getEventByServerID(id));
				}
			}
			dbAdapter.getDBEventAdapter(getApplicationContext()).deleteEvent(
					listAuxEvents);
		}

		protected void onPreExecute() {
			dialog.show();
		}

		protected void onPostExecute(String result) {

			displayListView();
			if (dialog != null)
				dialog.dismiss();
			speak(slideMenuNumbers[1], slideMenuNumbers[2]);

			if (responseCode == 200) {
				// Toast.makeText(getApplicationContext(), "Sucess!",
				// Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(getApplicationContext(),
				// "Error during process!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class DeleteEvent extends AsyncTask<String, Void, String> {
		ProgressDialog dialog = ProgressDialog.show(EventsActivity.this, "",
				"Please wait...");

		@Override
		protected String doInBackground(String... params) {
			postRequest();
			return "";
		}

		private void postRequest() {

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

			try {
				client = new MyHttpsClient(getApplicationContext());

				HttpRequestBase request = new HttpPost();
				request.setURI(new URI(Utils.SERVER_URL + "/deleteevent"));
				request.setHeader("Content-Type",
						"application/json; charset=utf-8");
				request.setHeader(new BasicHeader("X-PUBKEY",
						SaveSharedPreference
								.getPublicKey(getApplicationContext())));
				request.setHeader(new BasicHeader("X-MICROTIME", now + ""));
				request.setHeader(new BasicHeader("X-HASH", enc));
				// HttpDelete post = new HttpDelete(Utils.SERVER_URL +
				// "/delete");
				HttpPost delete = new HttpPost(Utils.SERVER_URL
						+ "/deleteevent");
				delete = (HttpPost) request;
				// eventsListFinal
				JSONObject object = new JSONObject();
				JSONArray array = new JSONArray();
				for (Event ev : eventsListFinal) {
					JSONObject o = new JSONObject();
					o.put("id",
							dbAdapter
									.getDBEventAdapter(getApplicationContext())
									.getEventServerID(ev));
					array.put(o);
				}
				object.put("username", username);
				object.put("eventids", array);
				String njson = object.toString();
				delete.setEntity(new StringEntity(njson));

				HttpResponse response = client.execute(request);
				responseCode = response.getStatusLine().getStatusCode();

				String responseText = null;
				responseText = EntityUtils.toString(response.getEntity());
				if (response.getStatusLine().getStatusCode() != 200) {
					String server = response.getFirstHeader("X-Status-Reason")
							.getValue();
				}
			} catch (ClientProtocolException e) {
				client.getConnectionManager().shutdown();
				e.printStackTrace();
			} catch (IOException e) {
				client.getConnectionManager().shutdown();
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		protected void onPreExecute() {
			dialog.show();
		}

		protected void onPostExecute(String result) {

			if (responseCode == 200) {
				mDrawerLayout.closeDrawers();
				dbAdapter.getDBEventAdapter(getApplicationContext())
						.deleteEvent(eventsListFinal);
				displayListView();

				if (dialog != null)
					dialog.dismiss();
			} else {
				if (dialog != null)
					dialog.dismiss();

				Toast.makeText(getApplicationContext(),
						"Error during process!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
