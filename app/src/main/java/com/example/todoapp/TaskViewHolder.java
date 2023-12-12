package com.example.todoapp;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskViewHolder extends RecyclerView.ViewHolder{

    private final TextView txt_name;
    private final TextView txt_date;
    private final TextView txt_priority;
    private final CheckBox cb_done;
    private final FloatingActionButton fab_button;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_name = itemView.findViewById(R.id.txt_name);
        txt_date = itemView.findViewById(R.id.txt_date);
        txt_priority = itemView.findViewById(R.id.txt_priority);
        cb_done = itemView.findViewById(R.id.cb_done);
        fab_button = itemView.findViewById(R.id.fab_delete);
    }

    public int getListPosition(){
        return this.getLayoutPosition();
    }

    public TextView getTxt_name() {
        return txt_name;
    }

    public TextView getTxt_date() {
        return txt_date;
    }


    public TextView getTxt_priority() {
        return txt_priority;
    }

    public CheckBox getCb_done() {
        return cb_done;
    }

    public FloatingActionButton getFab_button() {
        return fab_button;
    }

}
