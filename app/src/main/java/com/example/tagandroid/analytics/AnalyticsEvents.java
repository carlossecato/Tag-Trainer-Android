package com.example.tagandroid.analytics;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.example.tagandroid.R;
import com.example.tagandroid.model.CartProduct;
import com.example.tagandroid.model.Product;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;


public class AnalyticsEvents extends Application {
    private static AnalyticsEvents analyticsEvents;
    private GoogleAnalytics sAnalytics;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Tracker sTracker;

    private AnalyticsEvents() {
    }

    public static AnalyticsEvents getAnalyticsEventsInstance() {

        if (analyticsEvents == null) {
            analyticsEvents = new AnalyticsEvents();
        }

        return analyticsEvents;
    }

    public void setAnalyticsContext(Context context) {
        sAnalytics = GoogleAnalytics.getInstance(context);
    }

    public void setFirebaseAnalyticsContext(Context context) {
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    synchronized public Tracker getDefaultTracker(Context context) {
        setAnalyticsContext(context);
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

    public FirebaseAnalytics getFirebaseAnalytics() {
        return this.mFirebaseAnalytics;
    }

    public void login(String method, Context context) {
        setFirebaseAnalyticsContext(context);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);

        this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public void productImpressions(ArrayList<Product> products, Context context) {
        setFirebaseAnalyticsContext(context);

        Bundle viewItemListParams = new Bundle();
        viewItemListParams.putString(FirebaseAnalytics.Param.ITEM_LIST_ID, "001");
        viewItemListParams.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "Home Products");
        viewItemListParams.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, prepareProductBundleList(products));

        this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, viewItemListParams);
    }



    public void addToCart(CartProduct product, Context context) {
        setFirebaseAnalyticsContext(context);

        Bundle addToCartBundle = prepareCartProductBundleItem(product);

        this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, addToCartBundle);
    }

    public void removeFromCart(CartProduct product, Context context) {
        setFirebaseAnalyticsContext(context);

        Bundle removeFromCartBundle = prepareCartProductBundleItem(product);

        this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, removeFromCartBundle);

    }

    public void viewCart(List<CartProduct> cartProducts, Context context){
        setFirebaseAnalyticsContext(context);

        Bundle viewCartParams = new Bundle();
        viewCartParams.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, prepareCartProductBundleList(cartProducts));

        this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_CART, viewCartParams);
    }

    public void beginCheckout(ArrayList<CartProduct> cartProducts, Context context) {
        setFirebaseAnalyticsContext(context);

        Bundle beginCheckoutParams = new Bundle();
        beginCheckoutParams.putString(FirebaseAnalytics.Param.CURRENCY, "BRL");
        beginCheckoutParams.putDouble(FirebaseAnalytics.Param.VALUE, getTotalPrice(cartProducts));
        beginCheckoutParams.putString(FirebaseAnalytics.Param.COUPON, "RACCOON_MOBILE");
        beginCheckoutParams.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, prepareCartProductBundleList(cartProducts));

        this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, beginCheckoutParams);
    }

    public void addPaymentInfo(ArrayList<CartProduct> cartProducts, String paymentMethod, Context context) {
        setFirebaseAnalyticsContext(context);

        Bundle addPaymentParams = new Bundle();
        addPaymentParams.putString(FirebaseAnalytics.Param.CURRENCY, "BRL");
        addPaymentParams.putDouble(FirebaseAnalytics.Param.VALUE, getTotalPrice(cartProducts));
        addPaymentParams.putString(FirebaseAnalytics.Param.COUPON, "RACCOON_MOBILE");
        addPaymentParams.putString(FirebaseAnalytics.Param.PAYMENT_TYPE, paymentMethod);
        addPaymentParams.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS,
                prepareCartProductBundleList(cartProducts));

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, addPaymentParams);
    }

    public void purchase(ArrayList<CartProduct> items, Context context) {
        setFirebaseAnalyticsContext(context);

        Bundle purchaseParams = new Bundle();
        purchaseParams.putString(FirebaseAnalytics.Param.TRANSACTION_ID, "T12345");
        purchaseParams.putString(FirebaseAnalytics.Param.AFFILIATION, "Raccoon Store");
        purchaseParams.putString(FirebaseAnalytics.Param.CURRENCY, "BRL");
        purchaseParams.putDouble(FirebaseAnalytics.Param.VALUE, getTotalPrice(items));
        purchaseParams.putDouble(FirebaseAnalytics.Param.TAX, 2.58);
        purchaseParams.putDouble(FirebaseAnalytics.Param.SHIPPING, 5.34);
        purchaseParams.putString(FirebaseAnalytics.Param.COUPON, "RACCOON_MOBILE");
        purchaseParams.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, prepareCartProductBundleList(items));

        this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, purchaseParams);
    }

    private Bundle prepareProductBundleItem(Product product){
        Bundle productBundle = new Bundle();

        productBundle.putString( FirebaseAnalytics.Param.ITEM_ID, product.getId());  // ITEM_ID or ITEM_NAME is required
        productBundle.putString( FirebaseAnalytics.Param.ITEM_NAME, product.getName());
        productBundle.putString( FirebaseAnalytics.Param.ITEM_CATEGORY, product.getCategory());
        productBundle.putString( FirebaseAnalytics.Param.ITEM_VARIANT, product.getVariant());
        productBundle.putString( FirebaseAnalytics.Param.ITEM_BRAND, "raccoon");
        productBundle.putDouble( FirebaseAnalytics.Param.PRICE, product.getPrice() );

        return  productBundle;
    }

    private  Bundle prepareCartProductBundleItem(CartProduct cartProduct) {
        Bundle cartProductBundle  = prepareProductBundleItem(cartProduct.getProduct());
        cartProductBundle.putLong(FirebaseAnalytics.Param.QUANTITY, cartProduct.getQuantity());

        return  cartProductBundle;
    }

    private ArrayList<Bundle> prepareProductBundleList(List<Product> products){
        ArrayList<Bundle> productsBundle = new ArrayList<>();

        for (Product product :
                products) {
            productsBundle.add(prepareProductBundleItem(product));
        }

        return  productsBundle;
    }

    private  ArrayList<Bundle> prepareCartProductBundleList(List<CartProduct> cartProducts){
        ArrayList<Bundle> cartProductsBundle = new ArrayList<>();

        for (CartProduct cartProduct:
             cartProducts) {
            cartProductsBundle.add(prepareCartProductBundleItem(cartProduct));
        }

        return  cartProductsBundle;
    }

    private Double getTotalPrice(List<CartProduct> cartProducts){
        double total = 0.0;
        for (CartProduct cartProduct:
             cartProducts) {
            total += cartProduct.getTotalPrice();
        }

        return total;
    }

}
