package com.example.live;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore; // ⬅️ Import nécessaire

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String email;

    public String password;

    public String fullName;

    // Constructeur utilisé par Room (sans id)
    public User() {}

    // Constructeur principal avec fullName (utilisé quand tu crées un utilisateur complet)
    public User(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    // Si tu veux un autre constructeur, ignore-le avec @Ignore
    @Ignore
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.fullName = ""; // Valeur par défaut
    }
}