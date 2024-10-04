package com.example.woof;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityEditProfile extends AppCompatActivity {
    private EditText cName, cPhone, cMail;
    private TextView cDob;
    private Button cSave, cBack;
    private dBaseOperations db;
    private String userEmail;

    private static final String TAG = "ActivityEditProfile";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        cName = findViewById(R.id.txtNameUpdate);
        cPhone = findViewById(R.id.txtPhoneUpdate);
        cMail = findViewById(R.id.txtRegMailUpdate);
        cDob = findViewById(R.id.txtDoBUpdate);
        cSave = findViewById(R.id.btnSaveEditProfile);
        cBack = findViewById(R.id.btnBackProfile);

        db = new dBaseOperations(this);

        // Assuming you pass the user email to this activity
        userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail != null) {
            loadUserProfile(userEmail);
        }

        cDob.setOnClickListener(v -> showDatePickerDialog());

        cSave.setOnClickListener(v -> {
            // Get values from input fields
            String name = cName.getText().toString();
            String phoneStr = cPhone.getText().toString();
            String mail = cMail.getText().toString();
            String dobStr = cDob.getText().toString();

            // Log the inputs
            Log.d(TAG, "Name: " + name);
            Log.d(TAG, "Phone: " + phoneStr);
            Log.d(TAG, "Email: " + mail);
            Log.d(TAG, "Date of Birth: " + dobStr);

            // Validate inputs
            if (!isValidPhoneNumber(phoneStr)) {
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(mail)) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int phone = Integer.parseInt(phoneStr);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date dob = sdf.parse(dobStr);
                if (!isAgeValid(dob)) {
                    Toast.makeText(this, "You must be at least 18 years old", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = db.findUserByEmail(userEmail);

                if (user != null) {
                    user.setuName(name);
                    user.setuPhone(phone);
                    user.setuEmail(mail);
                    user.setDob(dob);

                    int rowsUpdated = db.updateUser(user);

                    if (rowsUpdated > 0) {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                }

            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        cBack.setOnClickListener(v -> finish());
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
                    cDob.setText(sdf.format(calendar.getTime()));
                }, year, month, day);
        datePickerDialog.show();
    }



    private void loadUserProfile(String email) {
        User user = db.findUserByEmail(email);
        if (user != null) {
            cName.setText(user.getuName());
            cPhone.setText(String.valueOf(user.getuPhone()));
            cMail.setText(user.getuEmail());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            cDob.setText(sdf.format(user.getDob()));
        }
    }

    private boolean isValidPhoneNumber(String phoneStr) {
        Log.d(TAG, "Validating phone number: " + phoneStr);
        boolean isValid = phoneStr.matches("\\d{9,10}");
        Log.d(TAG, "Is phone number valid: " + isValid);
        return isValid;
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
}
