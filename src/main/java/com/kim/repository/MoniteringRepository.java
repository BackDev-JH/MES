package com.kim.repository;

import com.kim.entity.Monitering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface MoniteringRepository extends JpaRepository<Monitering, Long>, QuerydslPredicateExecutor<Monitering> {

    public List<Monitering> findByOrderId(Long id);
}
