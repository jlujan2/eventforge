package com.eventforge.orderservice.event;

public record OrderCreatedEvent(
        Long orderId,
        String customerId,
        String product,
        int quantity
) {
}