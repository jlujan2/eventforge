package com.eventforge.inventoryservice.event;

public record OrderCreatedEvent(
        Long orderId,
        String customerId,
        String product,
        int quantity
) {
}