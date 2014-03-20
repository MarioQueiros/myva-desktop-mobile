package com.pt.myva_mobile;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.pt.myva_mobile.R.color;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ImageEditionActivity extends Activity implements OnTouchListener {
	int cont = 0;
	final Handler handler = new Handler();
	ViewPager viewPager;
	private ImageView imageViewEdition;
	boolean success = false;
	DisplayMetrics metrics;
	int width = 0, height = 0;
	int viewPagerPosition = 0;
	int yy = 0;
	int aux = 0;
	int imageID = 0;
	AlphaAnimation alpha;
	float alphaValue = (float) 1.0;
	float oldAlphaValue = (float) 0.0;
	RelativeLayout rl;
	int imageId = -1;

	// Save the images Id and their alpha
	HashMap<Integer, Integer> imageIdAlpha = new HashMap<Integer, Integer>();

	// Get the touch time
	double timeSeconds = 0;
	long up = 0;
	long down = 0;

	private android.widget.RelativeLayout.LayoutParams layoutParams;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.event_image);
		setContentView(R.layout.image_edition_activity);

		// Buscar a dimensao do ecra do telemovel
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;

		rl = (RelativeLayout) findViewById(R.id.areaTrabalho);

		// Fazer o botao para voltar atras na action bar
		int upId = Resources.getSystem().getIdentifier("up", "id", "android");
		if (upId > 0) {
			ImageView up = (ImageView) findViewById(upId);
			up.setImageResource(R.drawable.ic_action_previous_item);
		}

		// Tratar da apresentacao das imagens para edicao
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		ImageAdapter adapter = new ImageAdapter(this);
		viewPager.setAdapter(adapter);

		imageEditionButtonsListener();
		setupDragDropStuff();

	}

	public void count() {
		cont++;
	}

	public void criaImageView() {
		viewPagerPosition = viewPager.getCurrentItem();

		// Ir buscar a imagem
		ImageAdapter imgAdp = new ImageAdapter(null);
		int item = imgAdp.getGalImagesItem(viewPagerPosition);

		// Colocar a imagem na imageView
		final ImageView imageView = new ImageView(this);
		imageView.setId(imageID);

		// ImageId associated to an alpha
		imageIdAlpha.put(imageID, 100);

		imageView.setImageResource(item);

		// Colocar a imagem no layout
		rl.addView(imageView);
		layoutParams = (RelativeLayout.LayoutParams) imageView
				.getLayoutParams();
		imageViewEdition = imageView;
		imageID++;
		imageView.setOnTouchListener(this);
		imageView.setOnClickListener(clickImageListener);
	}

	Runnable mLongPressed = new Runnable() {
		public void run() {
			count();
			criaImageView();
			success = true;
		}
	};

	private void setupDragDropStuff() {
		viewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.postDelayed(mLongPressed, 700);
					int y_cord1 = (int) event.getRawY();
					yy = y_cord1 - 5;
					break;

				case MotionEvent.ACTION_MOVE:
					if (success) {
						// The coords where the user click on the screen
						int x_cord = (int) event.getRawX();
						int y_cord = (int) event.getRawY();

						int auxY = y_cord - yy;
						if (auxY >= 0) {
							layoutParams.topMargin = auxY;
							imageViewEdition.setLayoutParams(layoutParams);
						} else {
							break;
						}
						int auxX = x_cord - 25;
						if (auxX >= 0 && auxX <= (width * 0.85)) {
							layoutParams.leftMargin = auxX;
							imageViewEdition.setLayoutParams(layoutParams);
						} else {
							break;
						}

						return true;
						// break;
					} else {
						// return true;
						break;
					}

				case MotionEvent.ACTION_UP:
					success = false;
					handler.removeCallbacks(mLongPressed);
					break;

				default:
					break;
				}

				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.image_edition_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.blue:
			rl.setBackgroundColor(Color.parseColor("#2952CC"));
			break;
		case R.id.red:
			rl.setBackgroundColor(Color.parseColor("#CC0000"));
			break;
		case R.id.green:
			rl.setBackgroundColor(Color.parseColor("#33CC33"));
			break;
		case R.id.yellow:
			rl.setBackgroundColor(Color.parseColor("#E6E600"));
			break;
		case R.id.white:
			rl.setBackgroundColor(Color.WHITE);
			break;
		case R.id.grey:
			rl.setBackgroundColor(Color.GRAY);
			break;
		case R.id.black:
			rl.setBackgroundColor(Color.BLACK);
			break;
		case R.id.action_overflow:
			break;
		case android.R.id.home:
			Intent i = new Intent();
			setResult(Activity.RESULT_OK, i);
			finish();
			break;
		default:
			break;
		}
		return true;
	}

	public void imageEditionButtonsListener() {
		Button btnNavigatorClear = (Button) findViewById(R.id.button_clear);
		Button btnNavigatorSave = (Button) findViewById(R.id.button_create);

		btnNavigatorClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rl.removeAllViews();
				rl.setBackgroundColor(Color.WHITE);
				imageIdAlpha.clear();
				AlphaAnimation alpha = new AlphaAnimation(oldAlphaValue, 1.0F);
				alpha.setFillAfter(true);
				rl.startAnimation(alpha);
				alphaValue = 1.0F;
			}
		});

		btnNavigatorSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					saveImage();
				} catch (IOException e) {
					Toast.makeText(ImageEditionActivity.this, "Erro!",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});

	}

	public void saveImage() throws IOException {
		Intent i = new Intent();

		// View content = findViewById(R.id.areaTrabalho);
		rl.setDrawingCacheEnabled(true);
		Bitmap bitmap = rl.getDrawingCache();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();

		i.putExtra("EXTRA_MESSAGE", byteArray);

		setResult(Activity.RESULT_OK, i);
		finish();
	}

	// Image movement inside layout
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			down = System.currentTimeMillis();
			imageId = v.getId();
			imageViewEdition = (ImageView) findViewById(imageId);

			layoutParams = (RelativeLayout.LayoutParams) imageViewEdition
					.getLayoutParams();
			aux = (int) event.getRawY() - (int) imageViewEdition.getY();

			break;
		case MotionEvent.ACTION_MOVE:

			int x_cord = (int) event.getRawX();
			int y_cord = (int) event.getRawY();

			int auxY = y_cord - aux;
			if (auxY >= 0) {
				layoutParams.topMargin = auxY;
				imageViewEdition.setLayoutParams(layoutParams);
			} else {
				break;
			}

			int auxX = x_cord - 25;
			if (auxX >= 0 && auxX <= (width * 0.85)) {
				layoutParams.leftMargin = auxX;
				imageViewEdition.setLayoutParams(layoutParams);
			} else {
				break;
			}

			break;
		case MotionEvent.ACTION_UP:
			up = System.currentTimeMillis() - down;
			timeSeconds = (float) (up / 10000.0f);
			break;

		default:
			break;
		}
		return false;
	}

	public void createSeekBar(final int imageId) {
		// Popup
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Image Opacity");

		LinearLayout linear = new LinearLayout(this);
		linear.setOrientation(1);

		final SeekBar seek = new SeekBar(this);
		seek.setMax(100);

		alphaValue = (float) imageIdAlpha.get(imageId);
		seek.setProgress((int) alphaValue);

		final TextView text = new TextView(this);
		text.setText((int) alphaValue + " %");
		text.setGravity(1);

		linear.addView(seek);
		linear.addView(text);

		alert.setView(linear);

		// Seek Bar change listener
		OnSeekBarChangeListener yourSeekBarListener = new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBark, int progress,
					boolean fromUser) {
				int prog = seek.getProgress();
				alphaValue = prog / (float) 100;

				AlphaAnimation alpha = new AlphaAnimation(oldAlphaValue,
						alphaValue); // From -> To
				alpha.setFillAfter(true);
				imageViewEdition.startAnimation(alpha);
				// rl.startAnimation(alpha);
				oldAlphaValue = alphaValue;
				text.setText(Integer.toString(progress) + "%");
				imageIdAlpha.put(imageId, prog);
			}
		};
		seek.setOnSeekBarChangeListener(yourSeekBarListener);

		// Ok Buttom
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		alert.show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent();
			setResult(Activity.RESULT_OK, i);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public OnClickListener clickImageListener = new OnClickListener() {
		public void onClick(View v) {
			if (timeSeconds <= 0.015) {
				createSeekBar(v.getId());
			}
		}
	};

}