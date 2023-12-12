package models;

import android.content.Context;
import android.app.Application;

import androidx.annotation.Nullable;

import com.example.todoapp.R;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {

    public enum Priority{
        LOW(R.string.Low), MED(R.string.Medium), HIGH(R.string.High);

        private int stringId;

        Priority(int stringId) {
            this.stringId = stringId;
        }

        public int getStringId(){
            return stringId;
        }

    }

    private String name;
    private Date date;

    private Priority priority;

    private boolean isChecked;

    public Task(String name, Date date, Priority priority) {
        this.name = name;
        this.date = date;
        this.priority = priority;
        isChecked = false;
    }

    public Task(Task task){
        this.name = task.name;
        this.date = task.date;
        this.priority = task.priority;
        this.isChecked = task.isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Task){
            Task task = (Task) obj;
            if(task.getName().equals(this.name)) {
                return true;
            }
            return false;
        }
        return false;
    }
}
