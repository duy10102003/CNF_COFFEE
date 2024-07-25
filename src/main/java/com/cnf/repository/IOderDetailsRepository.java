package com.cnf.repository;

import com.cnf.entity.OrderDetails;
import com.cnf.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    @Query("SELECT o from OrderDetails o WHERE o.orders.id = ?1")
    List<OrderDetails> findAllOrderDetailsByOrderId(Long id);

    @Query("SELECT od.product, SUM(od.total_money) AS revenue " +
            "FROM OrderDetails od " +
            "GROUP BY od.product " +
            "ORDER BY revenue DESC")
    List<Product> findTop5ProductsByRevenue(Pageable pageable);
}
