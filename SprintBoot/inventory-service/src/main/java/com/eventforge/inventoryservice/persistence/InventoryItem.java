package com.eventforge.inventoryservice.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class InventoryItem {

    @Id
    private String product;

    private int quantity;

    protected InventoryItem() {
    }

    public InventoryItem(String product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void decrease(int amount) {
        this.quantity -= amount;
    }
}