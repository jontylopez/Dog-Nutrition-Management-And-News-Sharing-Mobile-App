package com.example.woof;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    private dBaseOperations db;
    private String userEmail;
    private TextView txtNameView, txtPhoneView, txtRegMailView, txtDoBView;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = new dBaseOperations(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        txtNameView = rootView.findViewById(R.id.txtNameView);
        txtPhoneView = rootView.findViewById(R.id.txtPhoneView);
        txtRegMailView = rootView.findViewById(R.id.txtEmailView);
        txtDoBView = rootView.findViewById(R.id.txtDobView);

        // Get the current user email
        userEmail = getCurrentUserEmail();
        if (userEmail != null) {
            loadUserProfile(userEmail);
        } else {
            Log.e("ProfileFragment", "User email is null");
            // Handle the case where user email is null
        }

        Button editProfile = rootView.findViewById(R.id.btnEditProfile);
        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivityForResult(intent, 1);
        });

        Button changePassword = rootView.findViewById(R.id.btnChangePassword);
        changePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivityChangePassword.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            if (userEmail != null) {
                loadUserProfile(userEmail);
            }
        }
    }

    private void loadUserProfile(String email) {
        User user = db.findUserByEmail(email);
        if (user != null) {
            txtNameView.setText(user.getuName());
            txtPhoneView.setText(String.valueOf(user.getuPhone()));
            txtRegMailView.setText(user.getuEmail());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            txtDoBView.setText(sdf.format(user.getDob()));
        }
    }

    private String getCurrentUserEmail() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("WoofAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", null);
    }
}
