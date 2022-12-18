package com.si.pendataanseserahan;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    int day, month, year;

    public static DatePickerFragment newInstance(String date) {
        String[] d = date.split("/");
        Bundle args = new Bundle();
        args.putInt("day", Integer.parseInt(d[0]));
        args.putInt("month", Integer.parseInt(d[1])-1);
        args.putInt("year", Integer.parseInt(d[2]));
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerFragment newInstance() {

        Bundle args = new Bundle();

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);


        if (getArguments() != null){
            day = getArguments().getInt("day", currentDay);
            month = getArguments().getInt("month", currentMonth);
            year = getArguments().getInt("year", currentYear);
        }
        else{
            day = currentDay;
            month = currentMonth;
            year = currentYear;
        }

        return new android.app.DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)
                getActivity(), year, month, day);
    }

}