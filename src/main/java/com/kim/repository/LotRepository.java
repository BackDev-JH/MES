package com.kim.repository;

import com.kim.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface LotRepository extends JpaRepository<Lot, Long>, QuerydslPredicateExecutor<Lot> {
}
