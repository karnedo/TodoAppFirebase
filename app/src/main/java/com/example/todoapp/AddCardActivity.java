package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import controllers.DatabaseController;
import models.Task;

public class AddCardActivity extends AppCompatActivity {

    public static final String NEW_TASK = "com.example.todoapp.MainActivity.Task";
    private EditText edt_task_name;
    private EditText edt_date;
    private Spinner sp_priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        edt_date = findViewById(R.id.edt_date);
        edt_task_name = findViewById(R.id.edt_task_name);
        sp_priority = findViewById(R.id.sp_priority);

        //Introducimos las prioridades en el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.priorities,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_priority.setAdapter(adapter);
    }

    //When invoked, goes back to the main activity
    public void goBack(View view){
        Intent intent = new Intent(this, MainActivity.class);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void addTask(View view){
        String name = String.valueOf(edt_task_name.getText());
        String dateStr = String.valueOf(edt_date.getText());
        boolean error = false;
        if(name.trim().isEmpty()){
            edt_task_name.setError(getResources().getString(R.string.Need_choose_name));
            error = true;
        }else{
            if(dateStr.trim().isEmpty()){
                edt_date.setError(getResources().getString(R.string.Need_choose_date));
                error = true;
            }
        }
        if(error) return;

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Date date = null;
        try{
            date = format.parse(dateStr);
        }catch(ParseException e){return;}

        Task.Priority priority = null;
        int selectedPos = sp_priority.getSelectedItemPosition();
        switch(selectedPos){
            case 0:
                priority = Task.Priority.LOW;
                break;
            case 1:
                priority = Task.Priority.MED;
                break;
            case 2:
                priority = Task.Priority.HIGH;
                break;
        }

        Task introducedTask = new Task(name, date, priority);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(NEW_TASK, introducedTask);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void showCalendar(View view){
        DatePickerFragment calendar = new DatePickerFragment();
        calendar.show(getSupportFragmentManager(), "DatePicker");
    }

    public void setDate(int year, int month, int day){
        edt_date.setText(year + "/" + month + "/" + day);
    }

}