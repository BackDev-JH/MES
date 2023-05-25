package com.kim.repository;

import com.kim.entity.Proce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface ProceRepository extends JpaRepository<Proce, Long>, QuerydslPredicateExecutor<Proce> {

     public Proce findByIdIs(Long id);

}
