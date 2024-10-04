package com.example.woof;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
    private EditText txtNameUpdate, txtPhoneUpdate, txtRegMailUpdate;
    private TextView txtDoBUpdate;
    private Button btnSaveProfile, btnBackProfile;
    private dBaseOperations db;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        db = new dBaseOperations(this);
        currentEmail = getIntent().getStringExtra("userEmail");

        // Initialize views
        txtNameUpdate = findViewById(R.id.txtNameUpdate);
        txtPhoneUpdate = findViewById(R.id.txtPhoneUpdate);
        txtRegMailUpdate = findViewById(R.id.txtRegMailUpdate);
        txtDoBUpdate = findViewById(R.id.txtDoBUpdate);
        btnSaveProfile = findViewById(R.id.btnSaveEditProfile);
        btnBackProfile = findViewById(R.id.btnBackProfile);

        // Load user data based on the current email
        User user = db.findUserByEmail(currentEmail);
        if (user != null) {
            txtNameUpdate.setText(user.getuName());
            txtPhoneUpdate.setText(String.valueOf(user.getuPhone()));
            txtRegMailUpdate.setText(user.getuEmail());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            txtDoBUpdate.setText(sdf.format(user.getDob()));
        }

        // Set click listener for Date of Birth field
        txtDoBUpdate.setOnClickListener(v -> showDatePickerDialog());

        // Set click listener for save button
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        // Set click listener for back button
        btnBackProfile.setOnClickListener(v -> finish());
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
                    txtDoBUpdate.setText(sdf.format(calendar.getTime()));
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveProfile() {
        String name = txtNameUpdate.getText().toString().trim();
        String phone = txtPhoneUpdate.getText().toString().trim();
        String regMail = txtRegMailUpdate.getText().toString().trim();
        String dobString = txtDoBUpdate.getText().toString().trim();

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(regMail)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.isEmailExists(regMail) && !regMail.equals(currentEmail)) {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dob = null;
        try {
            dob = sdf.parse(dobString);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isAgeValid(dob)) {
            Toast.makeText(this, "You must be at least 18 years old", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an updated user object with the new details
        User updatedUser = new User();
        updatedUser.setuName(name);
        updatedUser.setuPhone(Integer.parseInt(phone));
        updatedUser.setuEmail(regMail);
        updatedUser.setDob(dob);

        // Update the user profile using the current email
        int success = db.updateUserProfile(updatedUser, currentEmail);
        if (success > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhoneNumber(String phoneStr) {
        return phoneStr.matches("\\d{9,10}"); // Adjust the regex as per your phone number format requirements
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
