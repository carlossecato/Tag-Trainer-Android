package com.example.tagandroid.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.example.tagandroid.R;
import com.example.tagandroid.dao.CartProductsDao;
import com.example.tagandroid.model.CartProduct;
import com.example.tagandroid.model.Product;
import com.example.tagandroid.utils.AnalyticsEvents;
import com.example.tagandroid.utils.ProductsUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private CartProductsDao cpDao =  CartProductsDao.getInstance();
    private ArrayAdapter<Product> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        setTitle("Products");
        productListConfig();
        goToCartButtonConfig();

    }


    private void goToCartButtonConfig() {
        Button goToCart = findViewById(R.id.activity_products_goToCart_button);

        goToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCartActivity();
            }
        });
    }

    private void goToCartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);

    }

    private void productListConfig() {

        ListView listView = findViewById(R.id.activity_products_lv);

        setProductListAdapterConfig(listView);

        sendProductImpressionsEventToFirebase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Product clickedProduct = (Product) parent.getItemAtPosition(position);

                cpDao.addToCart(clickedProduct);

                sendAddToCartEventToFirebase(clickedProduct);

                Toast.makeText(ProductsActivity.this, clickedProduct.getName() + " added to cart!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendAddToCartEventToFirebase(Product clickedProduct) {
        CartProduct cartProduct = new CartProduct(clickedProduct);

        AnalyticsEvents.getAnalyticsEventsInstance().addToCart(cartProduct, this);
    }


    private void sendProductImpressionsEventToFirebase() {
        ArrayList<Product> products = new ArrayList<>();

        for ( int i = 0; i < adapter.getCount(); i++) {
            Product product = adapter.getItem(i);
            products.add(product);
        }

        AnalyticsEvents.getAnalyticsEventsInstance().productImpressions(products, this);
    }

    private void setProductListAdapterConfig(ListView listView) {
        List<Product> products = new ProductsUtils().products();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        listView.setAdapter(adapter);

    }
}
