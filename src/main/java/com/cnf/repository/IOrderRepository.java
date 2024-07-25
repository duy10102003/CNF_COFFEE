package com.cnf.repository;

import com.cnf.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Orders, Long> {
    @Query("SELECT o from Orders o WHERE o.user.id = ?1")
    List<Orders> findAllOrderByUserId(Long userId);

}
