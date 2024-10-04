package com.example.woof;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BillActivity extends AppCompatActivity {

    private TableLayout billSummaryTable;
    private TextView tvTotalPrice, tvTaxAmount, tvFinalAmount;
    private ArrayList<CartItem> cartItems;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill);

        billSummaryTable = findViewById(R.id.billSummary);
        tvTotalPrice = findViewById(R.id.totalTV);
        tvTaxAmount = findViewById(R.id.totalTaxTV);
        tvFinalAmount = findViewById(R.id.finalAmntTV);
        btnBack = findViewById(R.id.btnBackBill);

        cartItems = CartActivity.getAllCartItems();

        loadBillSummary();
        calculateTotal();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadBillSummary() {
        for (CartItem item : cartItems) {
            addBillItemToTable(item);
        }
    }

    private void addBillItemToTable(CartItem item) {

        TableRow tableRow = new TableRow(this);


        TextView itemName = new TextView(this);
        TextView itemQuantity = new TextView(this);
        TextView itemPrice = new TextView(this);

        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        itemName.setLayoutParams(params);
        itemQuantity.setLayoutParams(params);
        itemPrice.setLayoutParams(params);

        itemName.setText(item.getName());
        itemQuantity.setText(String.valueOf(item.getQuantity()));
        itemPrice.setText(String.format("Rs %.2f", item.getPrice() * item.getQuantity()));

        itemPrice.setGravity(Gravity.END);

        tableRow.addView(itemName);
        tableRow.addView(itemQuantity);
        tableRow.addView(itemPrice);

        billSummaryTable.addView(tableRow);
    }

    private void calculateTotal() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }

        double tax = total * 0.12;
        double finalAmount = total + tax;

        tvTotalPrice.setText(String.format("Total: Rs %.2f", total));
        tvTaxAmount.setText(String.format("Tax (12%%): Rs %.2f", tax));
        tvFinalAmount.setText(String.format("Final Amount: Rs %.2f", finalAmount));
    }
}
