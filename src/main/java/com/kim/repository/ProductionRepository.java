package com.kim.repository;

import com.kim.entity.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProductionRepository extends JpaRepository<Production, Long>, QuerydslPredicateExecutor<Production> {
}
