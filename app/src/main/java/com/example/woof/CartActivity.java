package com.example.woof;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private LinearLayout cartContainer;
    private TextView totalPrice, taxAmount, finalAmount;
    private Button btnCheckout, btnBack;

    private static ArrayList<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        cartContainer = findViewById(R.id.cartContainer);
        totalPrice = findViewById(R.id.totalPrice);
        taxAmount = findViewById(R.id.taxAmount);
        finalAmount = findViewById(R.id.finalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(view -> finish());

        loadCartItems();
        calculateTotal();
    }

    private void loadCartItems() {
        cartContainer.removeAllViews();
        for (CartItem item : cartItems) {
            addCartItemToLayout(item);
        }
    }

    private void addCartItemToLayout(CartItem item) {
        View cartItemView = getLayoutInflater().inflate(R.layout.cart_item, cartContainer, false);

        TextView itemName = cartItemView.findViewById(R.id.item_name);
        TextView itemPrice = cartItemView.findViewById(R.id.item_price);
        ImageView itemImage = cartItemView.findViewById(R.id.item_image);
        EditText quantityText = cartItemView.findViewById(R.id.quantity);
        ImageButton btnAdd = cartItemView.findViewById(R.id.btnAdd);
        ImageButton btnRemove = cartItemView.findViewById(R.id.btnRemove);
        ImageButton btnDelete = cartItemView.findViewById(R.id.btnDelete);

        itemName.setText(item.getName());
        itemPrice.setText(String.format("Rs %.2f", item.getPrice()));
        itemImage.setImageResource(item.getImageResId());
        quantityText.setText(String.valueOf(item.getQuantity()));

        btnAdd.setOnClickListener(v -> {
            int quantity = item.getQuantity();
            item.setQuantity(quantity + 1);
            quantityText.setText(String.valueOf(item.getQuantity()));
            calculateTotal();
        });

        btnRemove.setOnClickListener(v -> {
            int quantity = item.getQuantity();
            if (quantity > 1) {
                item.setQuantity(quantity - 1);
                quantityText.setText(String.valueOf(item.getQuantity()));
            }
            calculateTotal();
        });

        btnDelete.setOnClickListener(v -> {
            cartItems.remove(item);
            cartContainer.removeView(cartItemView);
            calculateTotal();
        });
        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, BillActivity.class);
            startActivity(intent);
        });

        cartContainer.addView(cartItemView);
    }

    private void calculateTotal() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }

        double tax = total * 0.12;
        double finalTotal = total + tax;

        totalPrice.setText(String.format("Total: Rs %.2f", total));
        taxAmount.setText(String.format("Tax (12%%): Rs %.2f", tax));
        finalAmount.setText(String.format("Final Amount: Rs %.2f", finalTotal));
    }

    public static void addCartItem(CartItem item) {
        cartItems.add(item);
    }

    public static void removeCartItem(CartItem item) {
        cartItems.remove(item);
    }

    public static ArrayList<CartItem> getAllCartItems() {
        return new ArrayList<>(cartItems);
    }
}



