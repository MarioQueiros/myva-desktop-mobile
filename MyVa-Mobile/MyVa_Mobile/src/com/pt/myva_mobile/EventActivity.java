package com.pt.myva_mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventActivity extends Activity {
	// Google Map
	private GoogleMap googleMap;
	Marker marker;
	DBAdapter dbAdapter = new DBAdapter();
	Event event;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.event_desc);
		setContentView(R.layout.event_activity);

		int upId = Resources.getSystem().getIdentifier("up", "id", "android");
		if (upId > 0) {
			ImageView up = (ImageView) findViewById(upId);
			up.setImageResource(R.drawable.ic_action_previous_item);
		}

		Intent i = getIntent();
		event = (Event) i.getSerializableExtra("OBJECT");

		fillActivity(event);
	}

	public void fillActivity(Event event) {

		TextView tv = (TextView) findViewById(R.id.textViewEA1);
		TextView tv2 = (TextView) findViewById(R.id.textViewEA2);
		TextView tv3 = (TextView) findViewById(R.id.textViewEA3);
		TextView tv4 = (TextView) findViewById(R.id.textViewEA4);

		tv.setText(event.getName());
		tv2.setText(Utils.getStrDay(event.getCalendar()));
		tv3.setText(Utils.getSimpleDate(event.getCalendar()));
		tv4.setText(dbAdapter.getDBLocalAdapter(getApplicationContext())
				.getLocal(event).getName());

		int id_image = event.getId_image();
		ImageView img = (ImageView) findViewById(R.id.imageView4);
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
		try {
			// Loading map
			Local local = dbAdapter.getDBLocalAdapter(getApplicationContext())
					.getLocal(event);
			initilizeMap(local.getLatitude(), local.getLongitude());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initilizeMap(double lat, double lon) {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map2)).getMap();
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			} else {
				CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
						lat, lon));
				CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

				googleMap.moveCamera(center);
				googleMap.animateCamera(zoom);

				if (marker != null) {
					marker.remove();
				}
				marker = googleMap.addMarker(new MarkerOptions()
						.position(new LatLng(lat, lon)).draggable(true)
						.visible(true));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.event_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit_event:
			Intent j = new Intent(EventActivity.this, EditEventActivity.class);
			j.putExtra("EVENT", event);
			startActivity(j);
			break;
		case android.R.id.home:
			this.finish();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Event ev = dbAdapter.getDBEventAdapter(getApplicationContext())
				.getEventByID(event.getId());
		if (Utils.isEdited()) {
			googleMap = null;
		}
		fillActivity(ev);
	}
}
