package com.kim.repository;

import com.kim.entity.PreProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PreProductRepository extends JpaRepository<PreProduct, Long>, QuerydslPredicateExecutor<PreProduct> {
}
