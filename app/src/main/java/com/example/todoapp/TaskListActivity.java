package com.example.todoapp;

import android.graphics.Bitmap;

import java.util.ArrayList;

import models.Task;

public interface TaskListActivity {
    public void addTask(Task task);
    public void setProfilePic(Bitmap bitmap);
}
