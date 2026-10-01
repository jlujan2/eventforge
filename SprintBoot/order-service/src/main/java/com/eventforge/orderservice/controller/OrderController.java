package com.eventforge.orderservice.controller;

import com.eventforge.orderservice.event.OrderCreatedEvent;
import com.eventforge.orderservice.producer.OrderProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping
    public ResponseEntity<OrderCreatedEvent> createOrder(
            @RequestBody OrderCreatedEvent order
    ) {
        orderProducer.send(order);

        return ResponseEntity.ok(order);
    }
}