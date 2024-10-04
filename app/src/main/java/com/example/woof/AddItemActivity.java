package com.example.woof;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class AddItemActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText etItemName, etItemDescription, etItemPrice;
    private Spinner spinnerCategory;
    private Button btnSelectImage, btnInsert, btnCancel;
    private dBaseOperations db;

    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
                        } catch (Exception e) {
                            Toast.makeText(AddItemActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);

        db = new dBaseOperations(this);
        imageView = findViewById(R.id.image_preview);
        etItemName = findViewById(R.id.etItemName);
        etItemDescription = findViewById(R.id.etItemDescription);
        etItemPrice = findViewById(R.id.etItemPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnInsert = findViewById(R.id.btnInsert);
        btnCancel = findViewById(R.id.btnBackAdd);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_categories, android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnSelectImage.setOnClickListener(v -> openImageSelector());
        btnInsert.setOnClickListener(v -> insertItem());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void insertItem() {
        String category = spinnerCategory.getSelectedItem().toString();
        String name = etItemName.getText().toString().trim();
        String description = etItemDescription.getText().toString().trim();
        String priceStr = etItemPrice.getText().toString().trim();

        // Validate fields
        if (category.equals("All")) {
            Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()) {
            Toast.makeText(this, "Item name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Item description is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (priceStr.isEmpty()) {
            Toast.makeText(this, "Item price is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageView.getDrawable() == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert image to byte array
        byte[] image = imageViewToByte(imageView);

        // Map category to database
        String dbCategory = mapCategoryToDatabase(category);

        // Insert into database
        long result = db.insertInventory(dbCategory, name, image, price, description, 1);
        if (result > 0) {
            Toast.makeText(this, "Item inserted successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Insertion failed", Toast.LENGTH_SHORT).show();
        }
    }


    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private String mapCategoryToDatabase(String category) {
        switch (category) {
            case "Food": return "food";
            case "Medicine": return "med";
            case "Treats": return "treat";
            case "Toys": return "toy";
            default: return "";
        }
    }
}
