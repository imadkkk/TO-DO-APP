package com.example.live;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        userDao = AppDatabase.getInstance(this).userDao();

        EditText emailField = findViewById(R.id.editTextEmailRegister);
        EditText passwordField = findViewById(R.id.editTextPasswordRegister);
        Button submitButton = findViewById(R.id.buttonRegisterSubmit);

        submitButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            Log.d(TAG, "Tentative d'inscription : email = '" + email + "'");

            if(email.isEmpty() || password.isEmpty()) {
                Log.d(TAG, "Champs vides détectés");
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            User existingUser = userDao.getUserByEmail(email);
            if (existingUser != null) {
                Log.d(TAG, "Email déjà utilisé : " + email);
                Toast.makeText(this, "Cet email est déjà utilisé", Toast.LENGTH_SHORT).show();
                return;
            }

            userDao.insert(new User(email, password));
            Log.d(TAG, "Utilisateur inséré : " + email);
            Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
