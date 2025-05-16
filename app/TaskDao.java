package com.example.live;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// 📌 DAO = Data Access Object : contient les opérations autorisées sur la table "Task"
@Dao
public interface TaskDao {

    // ➕ Insérer une nouvelle tâche
    @Insert
    void insert(Task task);

    // 📋 Récupérer toutes les tâches (SELECT * FROM Task)
    @Query("SELECT * FROM Task")
    List<Task> getAllTasks();

    // 🗑️ Supprimer toutes les tâches (facultatif)
    @Query("DELETE FROM Task")
    void deleteAll();
}
