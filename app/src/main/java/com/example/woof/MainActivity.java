package com.example.woof;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    dBaseOperations db;
    EditText txtEmail;
    TextInputEditText txtPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtEmail = findViewById(R.id.txtEmail);
        txtPw = findViewById(R.id.txtPassword).findViewById(R.id.textInputPassword);
        db = new dBaseOperations(this);
    }

    public void login(View view) {
        String email = txtEmail.getText().toString().trim();
        String pass = txtPw.getText().toString().trim();
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email or Password is Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        handleLogin(email, pass);
    }

    private void handleLogin(String email, String password) {
        User user = db.checkLogin(email, password);
        if (user != null) {
            saveUserEmailToPreferences(email);
            String userRole = user.getuRole();
            if ("adm".equals(userRole)) {
                Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AdminControl.class);
                startActivity(intent);
            } else if ("cus".equals(userRole)) {
                Toast.makeText(this, "Welcome " + email, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, CustomerHome.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User Role Unknown " + email, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserEmailToPreferences(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("WoofAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.apply();
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    }
}
