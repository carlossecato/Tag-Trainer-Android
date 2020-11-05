package com.example.tagandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tagandroid.R;
import com.example.tagandroid.analytics.AnalyticsEvents;
import com.example.tagandroid.model.Cart;
import com.example.tagandroid.model.CartProduct;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {
    private Cart cart = Cart.getInstance();
    private AnalyticsEvents analyticsEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        analyticsEvents = AnalyticsEvents.getAnalyticsEventsInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Checkout");
        setTotalTextViewConfig();
        setPaymentConfirmButonConfg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendBeginCheckoutEventToFirebase();
        sendScreenViewToAnalytics();
    }

    private void sendScreenViewToAnalytics() {
        analyticsEvents.getDefaultTracker(this).setScreenName("Checkout Screen");
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();

        ProductAction productAction = new ProductAction(ProductAction.ACTION_CHECKOUT);
        builder.setProductAction(productAction);

        builder
                .setCustomDimension(1, "true")
                .setCustomDimension(2, "123abc456def");

        analyticsEvents.getDefaultTracker(this).send(builder.build());

    }

    private void sendBeginCheckoutEventToFirebase() {
        ArrayList<CartProduct> products = cart.getCartProductsList();
        analyticsEvents.beginCheckout(products, this);
    }


    private void setPaymentConfirmButonConfg() {
        Button btn = findViewById(R.id.activity_checkout_payment_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!paymentMethodChoosed().equals("")){
                    sendPaymentInfoEventToFirebase(paymentMethodChoosed());
                    goToPurchaseActivity();
                }
            }
        });
    }

    private void sendPaymentInfoEventToFirebase(String paymentMethod) {
        ArrayList<CartProduct> products = cart.getCartProductsList();
        analyticsEvents.addPaymentInfo(products, paymentMethod, this);
    }
    
    private void goToPurchaseActivity() {
        Intent intent = new Intent(this, PurchaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private String paymentMethodChoosed(){
        RadioButton boletoCheck = findViewById(R.id.activity_checkout_rb_boleto);
        RadioButton cartaoCheck = findViewById(R.id.activity_checkout_rb_cartao);

        if(boletoCheck.isChecked()) return boletoCheck.getText().toString();
        if(cartaoCheck.isChecked()) return cartaoCheck.getText().toString();

        return "";
    }

    private void setTotalTextViewConfig() {
        TextView totalTextView = findViewById(R.id.activity_checkout_payment_tv);
        double totalPrice = 0;

        for (CartProduct cp : cart.getCartProductsList())
            totalPrice += cp.getTotalPrice();

        totalTextView.setText("Total: R$" + String.format("%.2f", totalPrice));

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
