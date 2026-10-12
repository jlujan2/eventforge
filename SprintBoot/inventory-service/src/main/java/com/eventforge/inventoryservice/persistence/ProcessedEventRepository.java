package com.eventforge.inventoryservice.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, Long> {
}