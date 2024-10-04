package com.example.woof;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class EditItemActivity extends AppCompatActivity {

    private EditText editName, editDescription, editPrice;
    private ImageView imageView;
    private Button btnSave,btnBack;
    private dBaseOperations db;
    private int itemId;  // This should be set from intent or saved instance state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);

        db = new dBaseOperations(this);
        itemId = getIntent().getIntExtra("item_id", -1); // Make sure this key matches with what you pass in Intent

        editName = findViewById(R.id.edit_item_name);
        editDescription = findViewById(R.id.edit_item_description);
        editPrice = findViewById(R.id.edit_item_price);
        imageView = findViewById(R.id.image_edit);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back_edit);
        btnSave.setOnClickListener(v -> saveItemChanges());
        btnBack.setOnClickListener(v -> {
            finish();
        });
        // Initialize views with item data
        initializeViews();
    }

    private void initializeViews() {
        Inventory item = db.getInventory(itemId);
        if (item != null) {
            editName.setText(item.getName());
            editDescription.setText(item.getDescription());
            editPrice.setText(String.valueOf(item.getPrice()));

            if (item.getImage() != null && item.getImage().length > 0) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(item.getImage(), 0, item.getImage().length);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                    // Optionally set a placeholder if the image fails to load
                    imageView.setImageResource(R.drawable.placeholder_image);
                }
            } else {
                // Set a placeholder image if no image data is available
                imageView.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveItemChanges() {
        String name = editName.getText().toString();
        String description = editDescription.getText().toString();
        double price;
        try {
            price = Double.parseDouble(editPrice.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] image = imageViewToByte(imageView);

        Inventory updatedItem = new Inventory(itemId, "Category", name, image, price, description, 1);
        int success = db.updateInventory(updatedItem.getId(), updatedItem.getCategory(), updatedItem.getName(), updatedItem.getImage(), updatedItem.getPrice(), updatedItem.getDescription(), updatedItem.getQuantity());
        if (success > 0) {
            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show();
        }
    }


    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
