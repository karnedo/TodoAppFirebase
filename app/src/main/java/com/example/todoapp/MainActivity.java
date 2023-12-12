package com.example.todoapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import controllers.DatabaseController;
import models.Task;


public class MainActivity extends AppCompatActivity implements TaskListActivity{

    public static final int NEW_IMAGE = 1;

    private RecyclerView rv_tasks;
    private DatabaseController controller;
    private ImageButton ib_Profile;
    private TaskListAdapter adapter;
    private ArrayList<Task> taskList;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ib_Profile = findViewById(R.id.ib_Profile);
        controller = new DatabaseController(this, getApplicationContext());
        taskList = new ArrayList<>(0);
        controller.loadTasks();
        controller.getProfilePicture();

        //Guardado del usuario
        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("email");
        String provider = bundle.getString("provider");

        //Guardamos el usuario en el dispositivo, para así no requerir el inición de sesión
        //cada vez que abra la app
        SharedPreferences preferences = getSharedPreferences("com.example.todoapp", MODE_PRIVATE);
        preferences.edit().putString("email", email).apply();
        preferences.edit().putString("provider", provider).apply();

        rv_tasks = findViewById(R.id.rv_tasks);

        adapter = new TaskListAdapter(taskList, this, controller);
        rv_tasks.setAdapter(adapter);
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            //En horizontal
            rv_tasks.setLayoutManager(new GridLayoutManager(this, 2));
        }else{
            //En vertical
            rv_tasks.setLayoutManager(new LinearLayoutManager(this));
        }

        //Cuando la imagen de perfil se mantiene presionada, nos da la opción de cerrar la sesión
        //Si solo se toca se podrá cambiar la imagen de perfil
        ib_Profile.setOnTouchListener(new View.OnTouchListener() {
            private boolean longClickPerformed = false;
            private Handler handler = new Handler();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    longClickPerformed = false;
                    handler.postDelayed(longClickRunnable, 1000);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacks(longClickRunnable);
                    changeProfilePicture();
                }
                return false;
            }
            private Runnable longClickRunnable = new Runnable() {
                @Override
                public void run() {
                    longClickPerformed = true;
                    closeSession();
                }
            };
        });
    }
    private void closeSession(){
        FirebaseAuth.getInstance().signOut();
        //Al cerrar sesión, eliminamos también los datos de la cuenta en el dispositivo.
        SharedPreferences preferences = getSharedPreferences("com.example.todoapp", MODE_PRIVATE);
        preferences.edit().putString("email", null).apply();
        preferences.edit().putString("provider", null).apply();

        Intent intent = new Intent(this, LoginActivtity.class);
        startActivity(intent);
    }

    private void changeProfilePicture() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, NEW_IMAGE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("taskList", taskList);
    }

    public void goToAddCard(View view){
        Intent intent = new Intent(this, AddCardActivity.class);
        taskAdder.launch(intent);
    }

    public void saveTasks(){
        controller.saveTasks(taskList);
    }

    @Override
    protected void onDestroy() {
        saveTasks();
        super.onDestroy();
    }

    ActivityResultLauncher<Intent> taskAdder = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == RESULT_OK){
                    //Obtenemos el Bundle y la tarea
                    Bundle extras = o.getData().getExtras();
                    Task task = (Task) extras.getSerializable(AddCardActivity.NEW_TASK);

                    //Y la anyadimos al RecyclerView
                    adapter.addTask(task);
                    controller.saveTasks(taskList);
                }
            }
    });

    @Override
    public void addTask(Task task) {
        if(!taskList.contains(task)) {
            adapter.addTask(task);
        }
    }

    @Override
    public void setProfilePic(Bitmap bitmap){
        //Si la descarga ha fallado, poner de imagen el logo de la app
        if(bitmap == null){
            ib_Profile.setImageDrawable(getDrawable(R.drawable.logo));
        }else{
            ib_Profile.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                Toast.makeText(MainActivity.this, getString(R.string.Loading_picture),
                    Toast.LENGTH_LONG).show();
                //Obtenemos la imagen como un bitmap y la subimos a Storage tal cual
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ib_Profile.setImageBitmap(bitmap);
                controller.saveProfilePicture(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}