package com.example.live;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String email;

    public String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
