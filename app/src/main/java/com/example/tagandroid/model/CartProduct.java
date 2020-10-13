package com.example.tagandroid.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CartProduct implements Serializable {

    private Product p;
    private int quantity;

    public CartProduct(Product p){
        this.p = p;
        quantity = 1;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public Product getProduct(){
        return p;
    }

    public double getTotalPrice(){
        return this.p.getPrice() * quantity;
    }
    @NonNull
    @Override
    public String toString() {
        return  p.getName() + " - Price: R$" + p.getPrice() + " - Quantity: " + this.quantity + " - Total Price: R$" + String.format("%.2f", p.getPrice() * quantity);
    }
}
