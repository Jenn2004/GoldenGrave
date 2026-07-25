package com.goldengrove.repository;

import com.goldengrove.entity.Order;
import com.goldengrove.entity.OrderStatus;
import com.goldengrove.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED')")
    BigDecimal totalSales();
}
