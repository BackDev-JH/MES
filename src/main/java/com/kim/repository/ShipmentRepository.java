package com.kim.repository;

import com.kim.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ShipmentRepository extends JpaRepository<Shipment, Long>, QuerydslPredicateExecutor<Shipment> {
}
