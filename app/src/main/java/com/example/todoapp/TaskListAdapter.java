package com.example.todoapp;

import static com.example.todoapp.R.layout.task_rv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import controllers.DatabaseController;
import models.Task;

public class TaskListAdapter extends RecyclerView.Adapter<TaskViewHolder>{

    private final ArrayList<Task> tasks;

    private final Context context;

    private final LayoutInflater inflater;
    private DatabaseController controller;

    public TaskListAdapter(ArrayList<Task> tasks, Context context, DatabaseController controller) {
        this.tasks = tasks;
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.controller = controller;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myItemView = inflater.inflate(task_rv, parent, false);
        return new TaskViewHolder(myItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        final Task task = tasks.get(position);
        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(task.getDate());

        //Si la fecha de la tarea es igual o mayor que la actual, avisar al usuario
        //cambiado de color la fecha
        Date todayMinusOneDay = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        if(task.getDate().before(todayMinusOneDay)){
            holder.getTxt_date().setTextColor(ContextCompat.getColor(context, R.color.warningTextColor));
        }

        String priority = context.getString(task.getPriority().getStringId());
        holder.getFab_button().setVisibility( (task.isChecked()) ? View.VISIBLE : View.GONE );
        holder.getTxt_name().setText(task.getName());
        holder.getTxt_date().setText(date);
        holder.getTxt_priority().setText(priority);
        holder.getCb_done().setChecked(task.isChecked());

        //Anyadimos el listener a los botones de borrar y marcar como terminada.
        holder.getFab_button().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Si el botón de borrar se presiona, se borra la tarea seleccionada.
                int pos = holder.getListPosition();
                Log.d("MyApp", "POSITION: " + pos);
                controller.deleteTask(tasks.get(pos).getName());
                tasks.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, tasks.size());
            }
        });

        holder.getCb_done().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean checked = holder.getCb_done().isChecked();
                int pos = holder.getListPosition();
                //Actualizamos la tarea en la base de datos
                controller.checkTask(tasks.get(pos).getName(), checked);

                //Mostramos el botón de borrar si la tarea está completada.
                holder.getFab_button().setVisibility( (checked) ? View.VISIBLE : View.GONE );
                task.setChecked(checked);
                notifyItemChanged(pos);

                //Task movedTask = tasks.get(pos);
                if(checked){
                    //Lo llevamos al final de la lista
                    moveTaskToEnd(pos);
                }else{
                    moveTaskToStart(pos);
                }
            }
        });
    }

    public void moveTaskToStart(int pos){
        Task task = tasks.remove(pos);
        tasks.add(0, task);
        notifyItemMoved(pos, 0);
    }

    public void moveTaskToEnd(int pos){
        Task task = tasks.remove(pos);
        tasks.add(task);
        notifyItemMoved(pos, tasks.size() - 1);

    }

    public void addTask(Task task){
        tasks.add(0, task);
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() { return tasks.size(); }

}
