package controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.TaskListActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.Task;

public class DatabaseController {

    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ArrayList<Task> tasks;
    private TaskListActivity taskListActivity;
    private Context context;

    public DatabaseController(TaskListActivity taskListActivity, Context context){
        database = FirebaseDatabase.getInstance("https://to-do-app-9be18-default-rtdb.europe-west1.firebasedatabase.app/");
        //ref = database.getReference("tasks");
        ref = database.getReference("" + FirebaseAuth.getInstance().getUid() + "/tasks");
        tasks = new ArrayList<Task>(0);
        this.taskListActivity = taskListActivity;
        this.context = context;
    }

    public void saveTasks(ArrayList<Task> task){
        ref.setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i("MyApp", "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("MyApp", "Fail");
            }
        });
    }

    public void loadTasks() {
        ArrayList<Task> tasks;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String name = ds.child("name").getValue().toString();
                        Task.Priority priority = Task.Priority.valueOf(
                                ds.child("priority").getValue().toString());
                        Date date = new Date(Long.valueOf(ds.child("date").child("time").getValue().toString()));
                        boolean checked = Boolean.valueOf(ds.child("checked").toString());

                        Task task = new Task(name, date, priority);
                        task.setChecked(checked);
                        Log.d("MyApp", "Scnahcivas");
                        taskListActivity.addTask(task);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void deleteTask(String name){
        Query query = ref.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the key of the item with the specified attributes
                    String key = snapshot.getKey();
                    Log.d("MyApp", "KEY: " + key);
                    // Delete the item using the key
                    ref.child(key).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void checkTask(String name, boolean isChecked){
        Query query = ref.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the key of the item with the specified attributes
                    String key = snapshot.getKey();

                    ref.child(key).child("checked").setValue(isChecked);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void saveProfilePicture(Bitmap bitmap){
        //Si la imagen es nula, se pone de foto de perfil el logo de la aplicación
        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 25, baos);
        byte[] data = baos.toByteArray();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference foto2Ref = storageRef.child(FirebaseAuth.getInstance().getUid()
                +"/"+String.valueOf("profile_pic")+".png");
        UploadTask uploadTask = foto2Ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("MyApp","Picture could not be uploaded");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("MyApp","Picture uploaded");
            }
        });
    }

    public void getProfilePicture(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference foto2Ref = storageRef.child(FirebaseAuth.getInstance().getUid()
                +"/"+String.valueOf("profile_pic")+".png");
        final long tam_foto = 10240 * 1024; // tamaño máximo de la descarga de la imagen, si es mayor la descarga falla.
        foto2Ref.getBytes(tam_foto).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Log.i("firebase1","la foto se ha descargado correctamente");
                Bitmap imagenbitmapdescargada = bytes_to_bitmap(bytes, 200, 200);
                taskListActivity.setProfilePic(imagenbitmapdescargada);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                byte[] bytes = null;
                Log.i("firebase1","la foto no se pudo descargar");
                int errorCode = ((StorageException) exception).getErrorCode();
                String errorMessage = exception.getMessage();
                Log.i("firebase1",errorMessage);
                Log.i("firebase1","error code" + String.valueOf(errorCode));
                taskListActivity.setProfilePic(null);
            }
        });
    }

    //método que convierte byte[] a bitmap
    private Bitmap bytes_to_bitmap(byte[] b, int width, int height){
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height,config);
        try{
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        } catch (Exception e){
        }
        return bitmap;
    }



    /* PARA ESCRIBIR
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://to-do-app-9be18-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, world!"); */

}
