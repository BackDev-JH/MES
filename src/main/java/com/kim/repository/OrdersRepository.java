package com.kim.repository;


import com.kim.constant.Status;
import com.kim.entity.Orders;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface OrdersRepository extends JpaRepository<Orders, Long>, QuerydslPredicateExecutor<Orders> ,OrdersRepositoryCustom{

    Page<Orders> findById(Long keyword, Pageable pageable);

    Page<Orders> findByOrderFromContaining(String keyword, Pageable pageable);

    Page<Orders> findByProductContaining(String keyword, Pageable pageable);

    Page<Orders> findByOrderDateBetween(LocalDateTime start,LocalDateTime end, Pageable pageable);

    Page<Orders> findByComDateBetween(LocalDateTime start,LocalDateTime end, Pageable pageable);

    List<Orders> findByStatus(Status status);

    Page<Orders> findByStatus(Status status, Pageable pageable);


    Page<Orders> findByIdAndStatus(Long keyword, Status status, Pageable pageable);

    Page<Orders> findByOrderFromContainingAndStatus(String keyword,Status status, Pageable pageable);

    Page<Orders> findByProductContainingAndStatus(String keyword, Status status,Pageable pageable);

    Page<Orders> findByOrderDateBetweenAndStatus(LocalDateTime start,LocalDateTime end,Status status, Pageable pageable);

    Page<Orders> findByComDateBetweenAndStatus(LocalDateTime start,LocalDateTime end,Status status, Pageable pageable);

    List<Orders> findByStatusAndId(Status status, Long id);

    List<Orders> findByStatusAndProductContaining(Status status, String product);

    List<Orders> findByStatusAndOrderFromContaining(Status status, String orderFrom);

    List<Orders> findByStatusAndOrderDateBetween(Status status, LocalDateTime start, LocalDateTime end);

    List<Orders> findByStatusAndComDateBetween(Status status, LocalDateTime start, LocalDateTime end);


}
