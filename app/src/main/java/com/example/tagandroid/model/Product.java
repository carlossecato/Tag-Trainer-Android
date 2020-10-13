package com.example.tagandroid.model;


import androidx.annotation.NonNull;

public class Product {
    private String id, name, category, variant;
    private double price;

    public Product(String name, String id, String category, String variant, double price){
        this.name = name;
        this.id = id;
        this.category = category;
        this.variant = variant;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName() + " - " + getVariant() +  " - R$" + getPrice();
    }
}
