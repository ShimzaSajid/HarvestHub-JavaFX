package com.example.fruitvegetablestall;

import java.io.Serializable;

public class Customer implements Serializable {
    private static int counter = 1;
    private int customerId;

    public Customer() {
        this.customerId = counter++;
    }

    public int getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return String.valueOf(customerId);
    }
}
