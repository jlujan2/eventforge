package com.eventforge.inventoryservice.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository
        extends JpaRepository<InventoryItem, String> {
}