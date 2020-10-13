package com.example.tagandroid.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.example.tagandroid.R;
import com.example.tagandroid.dao.CartProductsDao;
import com.example.tagandroid.model.CartProduct;
import com.example.tagandroid.utils.AnalyticsEvents;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private ArrayAdapter<CartProduct> adapter;
    private CartProductsDao cartDao = CartProductsDao.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setProductsListViewConfig();
        setCheckoutButtonConfig();
        sendViewCartEventToFirebase();

    }

    private  void sendViewCartEventToFirebase() {
        ArrayList<CartProduct> cartProducts = cartDao.getCartProductsList();
        AnalyticsEvents.getAnalyticsEventsInstance().viewCart(cartProducts, this);
    }

    private void setCheckoutButtonConfig() {
        Button bnt = findViewById(R.id.activity_cart_goToCheckout_button);
        bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartDao.getCartProductsList().size() > 0)
                    startCheckoutActivity();
            }
        });
    }
    public void startCheckoutActivity (){
        Intent intent = new Intent(this, CheckoutActivity.class);
        startActivity(intent);;
    }
    public void setProductsListViewConfig(){

        ListView productsLv = findViewById(R.id.activity_cart_products_lv);
        setAdapterConfig(productsLv);
        registerForContextMenu(productsLv);

    }

    public void refreshProductsList(){
        adapter.clear();
        adapter.addAll(cartDao.getCartProductsList());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.activity_cart_products_lv_menu, menu);
    }

    @Override
    protected void onResume() {
        refreshProductsList();
        super.onResume();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        CartProduct productClicked = adapter.getItem(menuInfo.position);

        if(itemId == R.id.activity_cart_products_lv_menu_add){
            sendAddToCartEventToFirebase(productClicked);

            cartDao.addToCart(productClicked.getProduct());

            refreshProductsList();
        }
        else {
            sendRemoveFromCartEventToFirebase(productClicked);

            cartDao.removeFromCart(productClicked);

            refreshProductsList();
        }

        return super.onContextItemSelected(item);
    }

    private void sendRemoveFromCartEventToFirebase(CartProduct product) {
        AnalyticsEvents.getAnalyticsEventsInstance().removeFromCart(product, this);
    }


    private void sendAddToCartEventToFirebase(CartProduct product) {
        AnalyticsEvents.getAnalyticsEventsInstance().addToCart(product, this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setAdapterConfig(ListView lv) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            lv.setAdapter(adapter);
    }

}
