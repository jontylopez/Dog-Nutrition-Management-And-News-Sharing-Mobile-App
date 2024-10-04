package com.example.woof;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ManageUsersFragment extends Fragment {

    private dBaseOperations db;
    private LinearLayout userListContainer;
    private SearchView searchUser;
    private Button btnSearch;
    private ArrayList<User> userList;
    private ArrayList<User> filteredUserList;

    public ManageUsersFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_users, container, false);

        userListContainer = rootView.findViewById(R.id.userListContainer);
        searchUser = rootView.findViewById(R.id.searchUser);
        btnSearch = rootView.findViewById(R.id.btnSearch);

        db = new dBaseOperations(getContext());
        loadUsers();

        btnSearch.setOnClickListener(v -> filterUsers());

        return rootView;
    }

    private void loadUsers() {
        userList = db.getUsers();
        filteredUserList = new ArrayList<>(userList);
        displayUsers(filteredUserList);
    }

    private void displayUsers(ArrayList<User> users) {
        userListContainer.removeAllViews();
        for (User user : users) {
            addUserView(user);
        }
    }

    private void addUserView(User user) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View userView = inflater.inflate(R.layout.view_user, userListContainer, false);

        EditText username = userView.findViewById(R.id.username);
        EditText userEmail = userView.findViewById(R.id.userEmail);
        EditText userPhone = userView.findViewById(R.id.userPhone);
        Button btnDeleteUser = userView.findViewById(R.id.btnDeleteUser);

        username.setText(user.getuName());
        userEmail.setText(user.getuEmail());
        userPhone.setText(String.valueOf(user.getuPhone()));

        btnDeleteUser.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.deleteUser(user.getuName());
                        userList.remove(user);
                        userListContainer.removeView(userView);
                        Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        userListContainer.addView(userView);
    }

    private void filterUsers() {
        String query = searchUser.getQuery().toString().toLowerCase();
        filteredUserList = (ArrayList<User>) userList.stream()
                .filter(user -> user.getuName().toLowerCase().contains(query) ||
                        user.getuEmail().toLowerCase().contains(query))
                .collect(Collectors.toList());

        displayUsers(filteredUserList);
    }
}
