package com.example.woof;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class CustomerHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar tb;
    private LinearLayout cartContainer;
    private ArrayList<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        // Initializing views
        drawerLayout = findViewById(R.id.drawer_layout);  // Corrected ID
        navigationView = findViewById(R.id.nav_view);
        tb = findViewById(R.id.toolBar2);
        cartContainer = findViewById(R.id.cartContainer);

        // Set up the toolbar and drawer toggle
        setSupportActionBar(tb);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CusHomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        cartItems = new ArrayList<>();
    }

    public void addToCart(String itemName, double itemPrice, int itemImageResId, int quantity) {
        CartItem newItem = new CartItem(itemName, itemPrice, itemImageResId, quantity);
        cartItems.add(newItem);
        Toast.makeText(this, itemName + " added to cart", Toast.LENGTH_SHORT).show();
        updateCartUI();
    }

    private void updateCartUI() {
        cartContainer.removeAllViews();
        for (CartItem item : cartItems) {
            View cartItemView = getLayoutInflater().inflate(R.layout.cart_item, cartContainer, false);
            TextView itemName = cartItemView.findViewById(R.id.item_name);
            TextView itemPrice = cartItemView.findViewById(R.id.item_price);
            ImageView itemImage = cartItemView.findViewById(R.id.item_image);

            itemName.setText(item.getName());
            itemPrice.setText(String.valueOf(item.getPrice()));
            itemImage.setImageResource(item.getImageResId());

            cartContainer.addView(cartItemView);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("cartItems", cartItems);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            selectedFragment = new CusHomeFragment();
        } else if (itemId == R.id.nav_shop) {
            selectedFragment = new ShopFragment();
        } else if (itemId == R.id.nav_view_profile) {
            selectedFragment = new ProfileFragment();
        } else if (itemId == R.id.nav_logout) {
            Intent intent = new Intent(CustomerHome.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
