package com.example.woof;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AdminDashboardFragment extends Fragment {

    private dBaseOperations db;
    private LinearLayout newsItemLayout;
    private EditText newsDescription;
    private ImageView selectedImage;
    private Button selectImageButton, addNewsButton;
    private byte[] imageByteArray;

    private ImageView editSelectedImage;

    private ActivityResultLauncher<Intent> imagePickerLauncherForDashboard;
    private ActivityResultLauncher<Intent> imagePickerLauncherForEditNews;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncherForDashboard = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        processImageSelectionForDashboard(imageUri);
                    }
                });

        imagePickerLauncherForEditNews = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        processImageSelectionForEditNews(imageUri);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        initializeViews(rootView);
        db = new dBaseOperations(getContext());
        loadNewsItems();
        return rootView;
    }

    private void initializeViews(View rootView) {
        newsItemLayout = rootView.findViewById(R.id.news_item_layout);
        newsDescription = rootView.findViewById(R.id.news_description);
        selectedImage = rootView.findViewById(R.id.selected_image);
        selectImageButton = rootView.findViewById(R.id.select_image_button);
        addNewsButton = rootView.findViewById(R.id.add_news_button);

        selectImageButton.setOnClickListener(v -> openImageSelectorForDashboard());
        addNewsButton.setOnClickListener(v -> addNewsItem());

    }

    private void openImageSelectorForDashboard() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncherForDashboard.launch(intent);
    }

    private void openImageSelectorForEditNews() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncherForEditNews.launch(intent);
    }

    private void processImageSelectionForDashboard(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            selectedImage.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageByteArray = stream.toByteArray();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void processImageSelectionForEditNews(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            editSelectedImage.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageByteArray = stream.toByteArray();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewsItem() {
        String description = newsDescription.getText().toString().trim();
        if (description.isEmpty() || imageByteArray == null) {
            Toast.makeText(getContext(), "Please provide a description and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        long newsId = db.insertNews(description, imageByteArray);
        if (newsId > 0) {
            Toast.makeText(getContext(), "News added successfully", Toast.LENGTH_SHORT).show();
            loadNewsItems();
            clearInputFields();
        } else {
            Toast.makeText(getContext(), "Failed to add news", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputFields() {
        newsDescription.setText("");
        selectedImage.setImageDrawable(null);
        imageByteArray = null;
    }

    private void loadNewsItems() {
        Log.d("AdminDashboardFragment", "loadNewsItems called");
        ArrayList<News> newsList = db.getAllNews();
        newsItemLayout.removeAllViews();
        for (News news : newsList) {
            addNewsItemView(news);
        }
    }

    private void addNewsItemView(News news) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.admin_news_layout, newsItemLayout, false);
        TextView description = itemView.findViewById(R.id.news_description);
        ImageView image = itemView.findViewById(R.id.news_image);
        Button editButton = itemView.findViewById(R.id.btnEdit);
        Button deleteButton = itemView.findViewById(R.id.btnDelete);

        description.setText(news.getnDescription());
        if (news.getnImage() != null) {
            image.setImageBitmap(BitmapFactory.decodeByteArray(news.getnImage(), 0, news.getnImage().length));
        }
        editButton.setOnClickListener(v -> editNewsItem(news.getId()));
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete News")
                    .setMessage("Are you sure you want to delete this news item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.deleteNews(news.getId());
                        Toast.makeText(getContext(), "News deleted successfully", Toast.LENGTH_SHORT).show();
                        loadNewsItems(); // Refresh the list after deletion
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        newsItemLayout.addView(itemView);
    }

    private void editNewsItem(int newsId) {
        News news = db.getNews(newsId);
        if (news == null) {
            Toast.makeText(getContext(), "News item not found", Toast.LENGTH_SHORT).show();
            return;
        }
        View editView = LayoutInflater.from(getContext()).inflate(R.layout.edit_news, null);
        EditText newsDescription = editView.findViewById(R.id.edit_news_description);
        editSelectedImage = editView.findViewById(R.id.edit_selected_image);
        Button selectImageButton = editView.findViewById(R.id.edit_select_image_button);
        Button saveButton = editView.findViewById(R.id.save_button);

        newsDescription.setText(news.getnDescription());
        if (news.getnImage() != null) {
            editSelectedImage.setImageBitmap(BitmapFactory.decodeByteArray(news.getnImage(), 0, news.getnImage().length));
            imageByteArray = news.getnImage();
        }

        selectImageButton.setOnClickListener(v -> openImageSelectorForEditNews());

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Edit News")
                .setView(editView)
                .setNegativeButton("Cancel", null)
                .create();

        saveButton.setOnClickListener(v -> {
            String updatedDescription = newsDescription.getText().toString().trim();

            if (updatedDescription.isEmpty() || imageByteArray == null) {
                Toast.makeText(getContext(), "Please provide a description and select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            int rowsUpdated = db.updateNews(newsId, updatedDescription, imageByteArray);
            if (rowsUpdated > 0) {
                Toast.makeText(getContext(), "News updated successfully", Toast.LENGTH_SHORT).show();
                loadNewsItems();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to update news", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
