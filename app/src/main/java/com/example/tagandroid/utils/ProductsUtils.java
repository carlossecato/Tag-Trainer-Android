package com.example.tagandroid.utils;

import com.example.tagandroid.dao.ProductsDao;
import com.example.tagandroid.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductsUtils {

    public static List<Product> products(){

        ProductsDao productsDao = new ProductsDao();
        List<Product> productList = new ArrayList<>(Arrays.asList(new Product("Tenis DC", "sku_1234", "Shoes", "42", 199.95), new Product("Camiseta Nike", "sku_3234", "T-SHIRT", "M", 85.00), new Product("Camiseta A", "sku_4321", "T-SHIRT", "Black", 159.99), new Product("Camiseta B", "sku_5321", "T-SHIRT", "White", 159.99), new Product("Tenis A", "sku_6321", "Shoes", "40", 159.99),  new Product("Tenis B", "sku_7321", "Shoes", "41", 159.99), new Product("Calca B", "sku_9321", "Pants", "Black", 159.99)));
        productsDao.saveProduct(productList);

        return productsDao.getProductsList();
    }
}
