package com.example.tagandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tagandroid.R;
import com.example.tagandroid.analytics.AnalyticsEvents;
import com.example.tagandroid.model.Cart;
import com.example.tagandroid.model.CartProduct;
import com.example.tagandroid.model.Product;
import com.example.tagandroid.utils.ProductsGenerator;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private Cart cart = Cart.getInstance();
    private ArrayAdapter<Product> adapter;
    private AnalyticsEvents analyticsEvents;
    private Tracker analyticsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        setTitle("Products");
        analyticsEvents = AnalyticsEvents.getAnalyticsEventsInstance();
        productListConfig();
        goToCartButtonConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        analyticsTracker = analyticsEvents.getDefaultTracker(this);
        sendProductImpressionsEventToFirebase();
        sendProductImpressionsToAnalytics();
    }

    private void sendProductImpressionsToAnalytics() {
        analyticsTracker.setScreenName("Home Products Screen");
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();

        for (int i = 0; i < adapter.getCount(); i++) {
            Product adapterProduct = adapter.getItem(i);
            adapterProduct.setPosition(i);

            builder.addImpression(new com.google.android.gms.analytics.ecommerce.Product()
                    .setName(adapterProduct.getName())
                    .setId(adapterProduct.getId())
                    .setPrice(adapterProduct.getPrice())
                    //.setBrand(adapterAppProduct.getBrand())
                    .setCategory(adapterProduct.getCategory())
                    .setVariant(adapterProduct.getVariant())
                    .setPosition(i), "Home Products");

        }

        builder
                .setCustomDimension(1, "true")
                .setCustomDimension(2, "123abc456def");

        analyticsTracker.send(builder.build());

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product clickedProduct = (Product) parent.getItemAtPosition(position);
                cart.addToCart(clickedProduct);
                sendAddToCartEventToFirebase(clickedProduct);
                Toast.makeText(ProductsActivity.this, clickedProduct.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendAddToCartEventToFirebase(Product clickedProduct) {
        CartProduct cartProduct = new CartProduct(clickedProduct);

        analyticsEvents.addToCart(cartProduct, this);
    }


    private void sendProductImpressionsEventToFirebase() {
        ArrayList<Product> products = new ArrayList<>();

        for (int i = 0; i < adapter.getCount(); i++) {
            Product product = adapter.getItem(i);
            products.add(product);
        }

        analyticsEvents.productImpressions(products, this);
    }

    private void setProductListAdapterConfig(ListView listView) {
        List<Product> products = ProductsGenerator.getInstance().getProductsList();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        listView.setAdapter(adapter);

    }
}
