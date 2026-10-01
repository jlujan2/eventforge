package com.eventforge.inventoryservice.consumer;

import com.eventforge.inventoryservice.event.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderCreatedConsumer {

    private final Set<Long> processedOrders =
            ConcurrentHashMap.newKeySet();

    @KafkaListener(topics = "order-created")
    public void consume(ConsumerRecord<String, OrderCreatedEvent> record) {

        OrderCreatedEvent event = record.value();

        if (!processedOrders.add(event.orderId())) {
            System.out.println(
                    "Duplicate ignored: orderId=" + event.orderId()
            );
            return;
        }

        System.out.println("Processing order: " + event.orderId());

        if ("FAIL".equalsIgnoreCase(event.product())) {
            throw new RuntimeException("Simulated inventory failure");
        }

        System.out.println("Inventory processed successfully");
    }
}