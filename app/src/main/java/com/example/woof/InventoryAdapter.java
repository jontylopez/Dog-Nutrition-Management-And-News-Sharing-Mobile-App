package com.example.woof;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private ArrayList<Inventory> inventoryList;
    private ArrayList<Inventory> originalInventoryList;
    private Context context;

    public InventoryAdapter(ArrayList<Inventory> inventoryList, Context context) {
        this.inventoryList = new ArrayList<>(inventoryList);
        this.originalInventoryList = new ArrayList<>(inventoryList);
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_shopping_item, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Inventory item = inventoryList.get(position);
        holder.iName.setText(item.getName());
        holder.iDescription.setText(item.getDescription());
        holder.iPrice.setText(String.valueOf(item.getPrice()));

        byte[] imageBytes = item.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.iImage.setImageBitmap(bitmap);
        } else {
            holder.iImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditItemActivity.class);
            intent.putExtra("item_id", item.getId());
            context.startActivity(intent);
        });

        holder.deleteIcon.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dBaseOperations db = new dBaseOperations(context);
                        db.deleteInventory(item.getId());
                        inventoryList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, inventoryList.size());
                        Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public void filter(String query) {
        if (query.isEmpty()) {
            inventoryList.clear();
            inventoryList.addAll(originalInventoryList);
        } else {
            inventoryList.clear();
            inventoryList.addAll(originalInventoryList.stream()
                    .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)) ||
                            item.getDescription().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList()));
        }
        notifyDataSetChanged();
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {

        TextView iName, iDescription, iPrice;
        ImageView iImage;
        Button editIcon, deleteIcon;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            iName = itemView.findViewById(R.id.iName);
            iDescription = itemView.findViewById(R.id.iDescription);
            iPrice = itemView.findViewById(R.id.iPrice);
            iImage = itemView.findViewById(R.id.iImage);
            editIcon = itemView.findViewById(R.id.edit_button_item);
            deleteIcon = itemView.findViewById(R.id.delete_button_item);
        }
    }
    public void updateInventoryList(ArrayList<Inventory> newInventoryList) {
        inventoryList.clear();
        inventoryList.addAll(newInventoryList);
        notifyDataSetChanged();
    }
}
