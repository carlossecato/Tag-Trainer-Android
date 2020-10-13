package com.example.tagandroid.dao;

import com.example.tagandroid.model.CartProduct;
import com.example.tagandroid.model.Product;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartProductsDao {
    private  static CartProductsDao cartProductsDao;

    protected Map<String, CartProduct> products;

    private CartProductsDao(){
        products = new HashMap<>();
    }

    public static CartProductsDao getInstance(){
        if(cartProductsDao == null)
            cartProductsDao = new CartProductsDao();

        return  cartProductsDao;
    }

    public ArrayList<CartProduct> getCartProductsList(){
        return  new ArrayList<CartProduct>(products.values());
    }

    public void addToCart(Product p){
        if(products.containsKey(p.getId())){
           CartProduct product = products.get(p.getId());
           product.setQuantity(product.getQuantity() + 1);
        }
        else {
            products.put(p.getId(), new CartProduct(p));
        }
    }


    public void removeFromCart(CartProduct productClicked) {
        System.out.println("quantidade do produto:˜ " + productClicked.getQuantity());
        int productQuantity= productClicked.getQuantity();
        if(productQuantity == 1) {
            products.remove(productClicked.getProduct().getId());
        }
        else {
            System.out.println("entrou aqui wtf");
            productClicked.setQuantity(productQuantity - 1);
        }
    }


}


