package com.pt.myva_mobile;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	public EditText activity_edittext;

	public DatePickerFragment(EditText edit_text) {
		activity_edittext = edit_text;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		if (activity_edittext.getText().length() > 0) {
			String[] date = activity_edittext.getText().toString().split("/");
			return new DatePickerDialog(getActivity(), this,
					Integer.parseInt(date[2]), (Integer.parseInt(date[0]) - 1),
					Integer.parseInt(date[1]));
		} else {
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		activity_edittext.setText(String.valueOf(month + 1) + "/"
				+ String.valueOf(day) + "/" + String.valueOf(year));
	}
}