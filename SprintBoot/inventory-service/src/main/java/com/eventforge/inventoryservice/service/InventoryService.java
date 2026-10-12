package com.eventforge.inventoryservice.service;

import com.eventforge.inventoryservice.event.OrderCreatedEvent;
import com.eventforge.inventoryservice.persistence.InventoryItem;
import com.eventforge.inventoryservice.persistence.InventoryRepository;
import com.eventforge.inventoryservice.persistence.ProcessedEvent;
import com.eventforge.inventoryservice.persistence.ProcessedEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProcessedEventRepository processedEventRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProcessedEventRepository processedEventRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void process(OrderCreatedEvent event) {

        if (processedEventRepository.existsById(event.orderId())) {
            System.out.println("Duplicate ignored: " + event.orderId());
            return;
        }

        InventoryItem item = inventoryRepository
                .findById(event.product())
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        item.decrease(event.quantity());

        inventoryRepository.save(item);

        if ("CRASH".equalsIgnoreCase(event.customerId())) {
            throw new RuntimeException("Simulated crash after inventory update");
        }

        processedEventRepository.save(
                new ProcessedEvent(
                        event.orderId(),
                        Instant.now()
                )
        );

        System.out.println("Order processed: " + event.orderId());
    }
}