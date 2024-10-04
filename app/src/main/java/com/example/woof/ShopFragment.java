package com.example.woof;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ShopFragment extends Fragment {

    private dBaseOperations db;
    private LinearLayout shoppingItemLayout;
    private SearchView searchView;
    private Spinner categorySpinner;

    private ArrayList<Inventory> inventoryList;
    private ArrayList<Inventory> filteredInventoryList;

    public ShopFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop, container, false);

        shoppingItemLayout = rootView.findViewById(R.id.shopping_item);
        searchView = rootView.findViewById(R.id.txtSearch);
        categorySpinner = rootView.findViewById(R.id.spnItems);

        db = new dBaseOperations(getContext());
        loadInventoryItems();

        setupSearchView();
        setupSpinner();

        return rootView;
    }

    private void loadInventoryItems() {
        inventoryList = db.getAllInventories();
        filteredInventoryList = new ArrayList<>(inventoryList);
        displayInventoryItems(filteredInventoryList);
    }

    private void displayInventoryItems(ArrayList<Inventory> items) {
        shoppingItemLayout.removeAllViews();
        for (Inventory inventory : items) {
            addInventoryItem(inventory);
        }
    }

    private void addInventoryItem(Inventory inventory) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View itemView = inflater.inflate(R.layout.shopping_item, shoppingItemLayout, false);

        TextView name = itemView.findViewById(R.id.iName);
        TextView description = itemView.findViewById(R.id.iDescription);
        TextView price = itemView.findViewById(R.id.iPrice);
        ImageView image = itemView.findViewById(R.id.iImage);
        Button addToCartButton = itemView.findViewById(R.id.btnAddToCart);

        name.setText(inventory.getName());
        description.setText(inventory.getDescription());
        price.setText(String.format("Rs %.2f", inventory.getPrice()));

        byte[] imageBytes = inventory.getImage();
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            image.setImageBitmap(bitmap);
        } else {
            image.setImageResource(R.drawable.placeholder_image);
        }

        Button btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(v -> {
            CartItem cartItem = new CartItem(inventory.getName(), inventory.getPrice(), R.drawable.placeholder_image, 1);
            CartActivity.addCartItem(cartItem);
            Toast.makeText(getContext(), inventory.getName() + " added to cart", Toast.LENGTH_SHORT).show();

        });

        shoppingItemLayout.addView(itemView);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterInventoryItems();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterInventoryItems();
                return true;
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.item_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterInventoryItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                displayInventoryItems(inventoryList);
            }
        });
    }

    private void filterInventoryItems() {
        String searchText = searchView.getQuery().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        Log.d("ShopFragment", "Selected Category: " + selectedCategory);
        Log.d("ShopFragment", "Search Text: " + searchText);

        filteredInventoryList = new ArrayList<>();
        for (Inventory item : inventoryList) {
            Log.d("ShopFragment", "Item Category: " + item.getCategory());
            if (item.getName().toLowerCase().contains(searchText) &&
                    (selectedCategory.equals("All") || item.getCategory().equalsIgnoreCase(mapCategoryToDatabase(selectedCategory)))) {
                filteredInventoryList.add(item);
            }
        }
        displayInventoryItems(filteredInventoryList);
    }
    private String mapCategoryToDatabase(String category) {
        switch (category) {
            case "All":
                return "all";
            case "Food":
                return "food";
            case "Medicine":
                return "med";
            case "Treats":
                return "treat";
            case "Toys":
                return "toy";
            default:
                return "";
        }
    }

}
