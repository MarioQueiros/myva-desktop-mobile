package com.pt.myva_mobile;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageAdapter extends PagerAdapter{
	
	Context context;
	private int[] GalImages = new int[] {
			R.drawable.cake,
			R.drawable.baloons,
			R.drawable.ball,
			R.drawable.beach_ball,
			R.drawable.beer,
			R.drawable.bike,
			R.drawable.champanhe,
			R.drawable.cinema,
			R.drawable.gift,
			R.drawable.meeting,
			R.drawable.party,
			R.drawable.popcorn,
			R.drawable.race,
			R.drawable.study2,
			R.drawable.umbrella,
			R.drawable.volei_ball,
			R.drawable.wedding,
	};
	
	
	ImageAdapter(Context context){
		this.context=context;
	}
	
	
	@Override
	public int getCount() {
		return GalImages.length;
	}
		 
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(context);
		int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
		imageView.setPadding(padding, padding, padding, padding);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView.setImageResource(GalImages[position]);
		((ViewGroup) container).addView(imageView, 0);
		return imageView;
	}
	 
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewGroup) container).removeView((ImageView) object);
	}
	
	public int getGalImagesItem(int position) {
		return GalImages[position];
	}
}
