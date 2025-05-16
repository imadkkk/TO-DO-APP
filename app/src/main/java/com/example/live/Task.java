package com.example.live;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;

    public Task(String title) {
        this.title = title;
    }
}