package com.example.live;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Query("SELECT * FROM Task WHERE userId = :userId")
    List<Task> getTasksForUser(int userId);

    // ⬇️ Méthode à ajouter si elle n'existe pas encore
    @Update
    void update(Task task); // ✅ Cette ligne manquait
}