package com.goldengrove.repository;

import com.goldengrove.entity.OrderItem;
import com.goldengrove.entity.OrderStatus;
import com.goldengrove.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT COUNT(oi) > 0 FROM OrderItem oi
            JOIN oi.order o
            WHERE o.user = :user AND oi.product.id = :productId
            AND o.status IN :statuses
            """)
    boolean hasPurchasedProduct(
            @Param("user") User user,
            @Param("productId") Long productId,
            @Param("statuses") java.util.List<OrderStatus> statuses);
}
