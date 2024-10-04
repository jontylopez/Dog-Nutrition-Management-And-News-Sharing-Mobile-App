package com.example.woof;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private ArrayList<Inventory> inventoryList;
    private dBaseOperations db;
    private Button btnAddItem;
    private SearchView srchInventory;

    @NonNull
    public static InventoryFragment newInstance() {
        return new InventoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        recyclerView = view.findViewById(R.id.rvInventoryItems);
        btnAddItem = view.findViewById(R.id.btnAddItem);
        srchInventory = view.findViewById(R.id.srchInventory);
        db = new dBaseOperations(getContext());

        inventoryList = db.getAllInventories();
        adapter = new InventoryAdapter(inventoryList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnAddItem.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddItemActivity.class)));

        // Set up the SearchView to filter the inventory list in real-time
        srchInventory.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // No need to handle query submission
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInventoryList();
    }

    private void updateInventoryList() {
        ArrayList<Inventory> newInventoryList = db.getAllInventories();
        if (adapter != null) {
            adapter.updateInventoryList(newInventoryList);
        }
    }
}
