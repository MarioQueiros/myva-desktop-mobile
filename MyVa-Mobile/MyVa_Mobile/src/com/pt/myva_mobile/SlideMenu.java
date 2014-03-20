package com.pt.myva_mobile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SlideMenu extends ArrayAdapter<String> {

	private Activity context;
	private String[] web;
	private Integer[] imageId;
	private String[] eventsNum;

	public SlideMenu(Activity context, String[] web, Integer[] imageId,
			String[] eventsNum) {
		super(context, R.layout.slidemenuitems, web);
		this.context = context;
		this.web = web;
		this.imageId = imageId;
		this.setEventsNum(eventsNum);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.slidemenuitems, null, true);

		TextView txtTitle1 = (TextView) rowView.findViewById(R.id.txt1);
		TextView txtTitle2 = (TextView) rowView.findViewById(R.id.txt2);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

		txtTitle1.setText(web[position]);
		txtTitle2.setText(getEventsNum()[position]);
		imageView.setImageResource(imageId[position]);

		return rowView;
	}

	public String[] getEventsNum() {
		return eventsNum;
	}

	public void setEventsNum(String[] eventsNum) {
		this.eventsNum = eventsNum;
	}

}
