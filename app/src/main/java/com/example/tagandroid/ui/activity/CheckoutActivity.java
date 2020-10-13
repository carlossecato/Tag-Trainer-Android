package com.example.tagandroid.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import com.example.tagandroid.R;
import com.example.tagandroid.dao.CartProductsDao;
import com.example.tagandroid.model.CartProduct;
import com.example.tagandroid.utils.AnalyticsEvents;
import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {
    private CartProductsDao cartDao = CartProductsDao.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Checkout");
        setTotalTextViewConfig();
        setPaymentConfirmButonConfg();
        sendBeginCheckoutEventToFirebase();
    }



    private void sendBeginCheckoutEventToFirebase() {
        ArrayList<CartProduct> products = cartDao.getCartProductsList();
        AnalyticsEvents.getAnalyticsEventsInstance().beginCheckout(products, this);
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
        ArrayList<CartProduct> products = cartDao.getCartProductsList();
        AnalyticsEvents.getAnalyticsEventsInstance().addPaymentInfo(products, paymentMethod, this);
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
        for (CartProduct cp : cartDao.getCartProductsList())
            totalPrice += cp.getProduct().getPrice() * cp.getQuantity();
        totalTextView.setText("Total: R$" + String.format("%.2f", totalPrice));

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
}
