package com.example.tagandroid.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.tagandroid.R;
import com.example.tagandroid.dao.CartProductsDao;
import com.example.tagandroid.model.CartProduct;
import com.example.tagandroid.utils.AnalyticsEvents;


import java.util.ArrayList;

public class PurchaseActivity extends AppCompatActivity {
    private CartProductsDao cartDao = CartProductsDao.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        setTitle("Purchase");
        setProductsListViewConfig();
        setTotalPriceTextViewConfgi();
        sendPurchaseEventToFirebase();
        setHomeButtonConfig();
    }

    private void setHomeButtonConfig() {
        Button btn = findViewById(R.id.activity_purchase_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PurchaseActivity.this, ProductsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    private void sendPurchaseEventToFirebase() {
        ArrayList<CartProduct> items = cartDao.getCartProductsList();

        AnalyticsEvents.getAnalyticsEventsInstance().purchase(items, this);
    }

    private void setProductsListViewConfig() {
        ListView lv = findViewById(R.id.activity_purchase_products_lv);
        setProductsListAdapterConfig(lv);
    }

    private void setProductsListAdapterConfig(ListView lv) {
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cartDao.getCartProductsList()));
    }

    private void setTotalPriceTextViewConfgi() {
        TextView totalTextView = findViewById(R.id.activity_purchase_total_tv);

        totalTextView.setText("Total Purchase: R$" + String.format("%.2f",getTotalPriceFromItems()));
    }

    private double getTotalPriceFromItems() {
        double total = 0;
        for (CartProduct cp : cartDao.getCartProductsList())
            total += cp.getProduct().getPrice() * cp.getQuantity();
        return total;
    }


}
