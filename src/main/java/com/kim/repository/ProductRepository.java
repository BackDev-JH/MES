package com.kim.repository;

import com.kim.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product> {

    Product findByProduct(String product);

}
