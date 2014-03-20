package com.pt.myva_mobile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {

	// Google Map
	private GoogleMap googleMap;
	Marker marker;
	double latitude;
	double longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.map_title);

		int upId = Resources.getSystem().getIdentifier("up", "id", "android");
		if (upId > 0) {
			ImageView up = (ImageView) findViewById(upId);
			up.setImageResource(R.drawable.ic_action_previous_item);
		}
		setContentView(R.layout.map_activity);

		Intent i = getIntent();
		latitude = i.getDoubleExtra("LAT", 0.0);
		longitude = i.getDoubleExtra("LON", 0.0);

		try {
			// Loading map
			initilizeMap();
			centerMapOnMyLocation();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Toast.makeText(MapActivity.this, "Press a location to select it!",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent i = new Intent();
			if (marker != null) {

				String place = getLocationString(marker.getPosition()) + "@"
						+ marker.getPosition().latitude + "@"
						+ marker.getPosition().longitude;

				i.putExtra("EXTRA_MESSAGE", place);
			} else {
				i.putExtra("EXTRA_MESSAGE", "");
			}
			setResult(Activity.RESULT_OK, i);
			finish();
			break;
		default:
			break;
		}
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Do stuff
			Intent i = new Intent();
			if (marker != null) {

				String place = getLocationString(marker.getPosition()) + "@"
						+ marker.getPosition().latitude + "@"
						+ marker.getPosition().longitude;

				i.putExtra("EXTRA_MESSAGE", place);
			} else {
				i.putExtra("EXTRA_MESSAGE", "");
			}
			setResult(Activity.RESULT_OK, i);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			googleMap.setMyLocationEnabled(true);

			googleMap
					.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

						@Override
						public void onMapLongClick(LatLng arg0) {
							if (marker != null) {
								marker.remove();
							}
							marker = googleMap.addMarker(new MarkerOptions()
									.position(
											new LatLng(arg0.latitude,
													arg0.longitude))
									.draggable(true).visible(true));
						}
					});

			if (latitude != 0.0 && longitude != 0.0) {
				CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
						latitude, longitude));
				CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

				googleMap.moveCamera(center);
				googleMap.animateCamera(zoom);

				marker = googleMap.addMarker(new MarkerOptions()
						.position(new LatLng(latitude, longitude))
						.draggable(true).visible(true));
			}

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void centerMapOnMyLocation() {

		LatLng myLocation = null;
		googleMap.setMyLocationEnabled(true);

		Location location = googleMap.getMyLocation();

		if (location != null) {
			myLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
		}
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
				16));
	}

	public String getLocationString(LatLng pos) {
		String myaddress = "";
		Geocoder gcd = new Geocoder(this, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = gcd.getFromLocation(pos.latitude, pos.longitude, 100);

			if (addresses.size() > 0 && addresses != null) {
				if (addresses.get(0).getAddressLine(0) != null) {
					myaddress = addresses.get(0).getAddressLine(0);
				} else {
					myaddress = "no";
				}

				if (addresses.get(0).getLocality() != null) {
					myaddress += "@" + addresses.get(0).getLocality();
				} else {
					myaddress += "@no";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return myaddress;
	}

}
