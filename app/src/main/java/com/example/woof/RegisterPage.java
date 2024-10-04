package com.example.woof;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterPage extends AppCompatActivity {
    dBaseOperations db;
    EditText txtName, txtMobile, txtMail;
    TextView txtDate;
    TextInputEditText txtPw1, txtPw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtName = findViewById(R.id.txtName);
        txtMobile = findViewById(R.id.txtPhone);
        txtMail = findViewById(R.id.txtRegMail);
        txtDate = findViewById(R.id.txtDoB);
        txtPw1 = findViewById(R.id.txtpw).findViewById(R.id.textPass);
        txtPw2 = findViewById(R.id.txtCnfPw).findViewById(R.id.textConfPass);

        db = new dBaseOperations(this);

        txtDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    calendar.set(year1, monthOfYear, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    txtDate.setText(sdf.format(calendar.getTime()));
                }, year, month, day);
        datePickerDialog.show();
    }

    public void register(View view) {
        String uName = txtName.getText().toString();
        String phoneStr = txtMobile.getText().toString();
        String email = txtMail.getText().toString();
        String dateStr = txtDate.getText().toString();
        String pw1 = txtPw1.getText().toString();
        String pw2 = txtPw2.getText().toString();

        if (!pw1.equals(pw2)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phoneStr)) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.isEmailExists(email)) {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int phone = Integer.parseInt(phoneStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dob = sdf.parse(dateStr);
            if (!isAgeValid(dob)) {
                Toast.makeText(this, "You must be at least 18 years old", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(uName, phone, email, dob, pw1);
            db.createUser(user);

            Toast.makeText(this, "User Created Successfully", Toast.LENGTH_SHORT).show();
            clearFields();
            txtName.requestFocus();
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhoneNumber(String phoneStr) {
        return phoneStr.matches("\\d{9,10}");
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    private boolean isAgeValid(Date dob) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dob);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age >= 18;
    }

    private void clearFields() {
        txtName.setText("");
        txtMobile.setText("");
        txtMail.setText("");
        txtDate.setText("");
        txtPw1.setText("");
        txtPw2.setText("");
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
