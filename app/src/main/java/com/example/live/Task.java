package com.example.live;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;

    public int userId; // Lier la tâche à un utilisateur

    public boolean isCompleted; // ← Champ ajouté

    public Task(String title, int userId) {
        this.title = title;
        this.userId = userId;
        this.isCompleted = false; // Par défaut, la tâche n'est pas terminée
    }
}