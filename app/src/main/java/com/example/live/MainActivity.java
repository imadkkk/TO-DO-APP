package com.example.live;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView todoRecyclerView;
    private TodoAdapter todoAdapter;
    private List<Task> todoList;
    private AppDatabase db;
    private TaskDao taskDao;
    private UserDao userDao;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération des vues de connexion
        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonRegister = findViewById(R.id.buttonRegister);

        // Récupération des vues de la To-Do List
        TextView textViewWelcome = findViewById(R.id.textViewWelcome);
        Button buttonLogout = findViewById(R.id.buttonLogout);
        RecyclerView recyclerView = findViewById(R.id.todoRecyclerView);
        EditText editTextNewTask = findViewById(R.id.editTextNewTask);
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        EditText editTextCity = findViewById(R.id.editTextCity);
        Button buttonGetWeather = findViewById(R.id.buttonGetWeather);

        // Initialisation de la base de données
        db = AppDatabase.getInstance(this);
        taskDao = db.taskDao();
        userDao = db.userDao();

        // Configuration de la liste des tâches
        todoList = new ArrayList<>();
        todoAdapter = new TodoAdapter(todoList, taskDao);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(todoAdapter);

        // Connexion utilisateur
        buttonLogin.setOnClickListener(v -> {
            String userEmail = emailEditText.getText().toString().trim();
            String userPassword = passwordEditText.getText().toString().trim();

            User foundUser = userDao.getUserByEmail(userEmail);

            if (foundUser != null && foundUser.password.equals(userPassword)) {
                loggedInUser = foundUser;

                String displayName = foundUser.fullName;
                if (displayName == null || displayName.isEmpty()) {
                    String[] parts = foundUser.email.split("@");
                    displayName = parts[0];
                }

                textViewWelcome.setText("Bienvenue, " + displayName);

                findViewById(R.id.loginLayout).setVisibility(View.GONE);
                findViewById(R.id.todoLayout).setVisibility(View.VISIBLE);

                Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                loadTasksFromDatabase();
            } else {
                Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
            }
        });

        // Inscription
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Ajout de tâche
        buttonAddTask.setOnClickListener(v -> {
            String newTask = editTextNewTask.getText().toString().trim();
            if (!newTask.isEmpty()) {
                if (loggedInUser != null) {
                    Task task = new Task(newTask, loggedInUser.id);
                    taskDao.insert(task);

                    todoList.add(task);
                    todoAdapter.notifyItemInserted(todoList.size() - 1);
                    editTextNewTask.setText("");
                    recyclerView.scrollToPosition(todoList.size() - 1);
                } else {
                    Toast.makeText(MainActivity.this, "Aucun utilisateur connecté", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Écris une tâche avant d'ajouter", Toast.LENGTH_SHORT).show();
            }
        });

        // Déconnexion
        buttonLogout.setOnClickListener(v -> {
            findViewById(R.id.todoLayout).setVisibility(View.GONE);
            findViewById(R.id.loginLayout).setVisibility(View.VISIBLE);

            emailEditText.setText("");
            passwordEditText.setText("");
            editTextNewTask.setText("");
            emailEditText.requestFocus();

            loggedInUser = null;
            textViewWelcome.setText("");

            todoList.clear();
            todoAdapter.notifyDataSetChanged();

            Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show();
        });

        // Météo
        buttonGetWeather.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();
            if (!city.isEmpty()) {
                getWeather(city);
            } else {
                Toast.makeText(MainActivity.this, "Entrez une ville", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTasksFromDatabase() {
        if (loggedInUser != null) {
            List<Task> tasksFromDb = taskDao.getTasksForUser(loggedInUser.id);
            todoList.clear();
            todoList.addAll(tasksFromDb);
            todoAdapter.notifyDataSetChanged();
        }
    }

    private void getWeather(String city) {
        String apiKey = "ce2dad10bbb94ac1fabe64ad6d420da7";
        String baseUrl = "https://api.openweathermap.org/data/2.5/weather ";
        String url = baseUrl + "?q=" + city.trim().replaceAll(" ", "%20") + "&appid=" + apiKey + "&units=metric";

        Log.d("MainActivity", "Requête météo : " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        double temperature = main.getDouble("temp");

                        JSONArray weatherArray = response.getJSONArray("weather");
                        JSONObject weather = weatherArray.getJSONObject(0);
                        String description = weather.getString("description");

                        Toast.makeText(this, "Météo à " + city + ": " + temperature + "°C - " + description, Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur de parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MainActivity", "Erreur API Météo : " + error.toString());
                    Toast.makeText(this, "Erreur API Météo : " + error.toString(), Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }
}