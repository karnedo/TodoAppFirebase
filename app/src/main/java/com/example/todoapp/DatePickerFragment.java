package com.example.todoapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp1 = new DatePickerDialog(getActivity(), this, year, month, day);
        //Evitamos que el usuario ponga una fecha anterior a la de hoy
        dp1.getDatePicker().setMinDate(new Date().getTime());
        return dp1;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        AddCardActivity act = (AddCardActivity) getActivity();
        act.setDate(year, month+1, dayOfMonth);
    }
}