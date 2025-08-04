package com.example.fruitvegetablestall;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String productName;
    private String type; // ✅ make sure this field exists
    private int quantity;
    private double price;

    public OrderItem(String productName, String type, int quantity, double price) {
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public String getType() {
        return type; // ✅ this must exist
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}





