package com.example.live;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView todoRecyclerView;
    private TodoAdapter todoAdapter;
    private List<String> todoList;

    private AppDatabase db;
    private TaskDao taskDao;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText email = findViewById(R.id.editTextEmail);
        EditText password = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.buttonLogin);
        Button registerButton = findViewById(R.id.buttonRegister);

        // Initialiser Room
        db = AppDatabase.getInstance(this);
        taskDao = db.taskDao();
        userDao = db.userDao();

        todoRecyclerView = findViewById(R.id.todoRecyclerView);
        todoList = new ArrayList<>();
        todoAdapter = new TodoAdapter(todoList);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoRecyclerView.setAdapter(todoAdapter);

        EditText newTaskField = findViewById(R.id.editTextNewTask);
        Button addTaskButton = findViewById(R.id.buttonAddTask);

        loginButton.setOnClickListener(view -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            Log.d(TAG, "Tentative de connexion avec email: '" + userEmail + "'");

            User foundUser = userDao.getUserByEmail(userEmail);
            if (foundUser != null) {
                Log.d(TAG, "Utilisateur trouvé en base: " + foundUser.email);
                Log.d(TAG, "Mot de passe stocké: " + foundUser.password);
            } else {
                Log.d(TAG, "Aucun utilisateur trouvé avec cet email");
            }

            if (foundUser != null && foundUser.password.equals(userPassword)) {
                findViewById(R.id.loginLayout).setVisibility(android.view.View.GONE);
                findViewById(R.id.todoLayout).setVisibility(android.view.View.VISIBLE);
                Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                loadTasksFromDatabase();
            } else {
                Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        addTaskButton.setOnClickListener(v -> {
            String newTask = newTaskField.getText().toString().trim();
            if (!newTask.isEmpty()) {
                Task task = new Task(newTask);
                taskDao.insert(task);

                todoList.add(newTask);
                todoAdapter.notifyItemInserted(todoList.size() - 1);
                newTaskField.setText("");
                todoRecyclerView.scrollToPosition(todoList.size() - 1);
            } else {
                Toast.makeText(MainActivity.this, "Écris une tâche avant d'ajouter", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTasksFromDatabase() {
        List<Task> tasksFromDb = taskDao.getAllTasks();
        for (Task task : tasksFromDb) {
            todoList.add(task.title);
        }
        todoAdapter.notifyDataSetChanged();
    }
}


