package com.example.woof;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityChangePassword extends AppCompatActivity {
    private EditText oldPw, newPw, cnfNewPw;
    private TextView userEmailView;
    private dBaseOperations db;
    private String userEmail;
    private Button btnSave, btnBack;

    private static final String TAG = "ActivityChangePassword";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        userEmailView = findViewById(R.id.txtRegMailView);
        oldPw = findViewById(R.id.txtPassOld);
        newPw = findViewById(R.id.textPassNew);
        cnfNewPw = findViewById(R.id.textConfPassNew);
        btnSave = findViewById(R.id.btnSavePassword);
        btnBack = findViewById(R.id.btnBackProfile);

        db = new dBaseOperations(this);
        userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail != null) {
            userEmailView.setText(userEmail);
        } else {
            Toast.makeText(this, "User email is missing", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User email is null");
            finish();
        }

        btnSave.setOnClickListener(v -> {
            String oldPass = oldPw.getText().toString();
            String newPass = newPw.getText().toString();
            String cnfNewPass = cnfNewPw.getText().toString();

            if (newPass.equals(cnfNewPass)) {
                try {
                    User user = db.findUserByEmail(userEmail);
                    if (user != null) {
                        Log.d(TAG, "User found: " + user.getuEmail());
                        String hashedOldPass = db.hashPassword(oldPass);
                        if (user.getPass().equals(hashedOldPass)) {
                            user.setPass(newPass);
                            int rowsUpdated = db.updateUserPassword(user);
                            if (rowsUpdated > 0) {
                                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Password change failed", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Password update failed in database");
                            }
                        } else {
                            Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "User not found");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error while changing password", e);
                    Toast.makeText(this, "Error changing password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "New password and confirmation do not match", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
