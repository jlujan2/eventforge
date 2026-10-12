package com.eventforge.inventoryservice.consumer;

import com.eventforge.inventoryservice.event.OrderCreatedEvent;
import com.eventforge.inventoryservice.persistence.ProcessedEvent;
import com.eventforge.inventoryservice.persistence.ProcessedEventRepository;
import com.eventforge.inventoryservice.service.InventoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderCreatedConsumer {

    private final InventoryService inventoryService;

    public OrderCreatedConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order-created")
    public void consume(ConsumerRecord<String, OrderCreatedEvent> record) {
        inventoryService.process(record.value());
    }
}