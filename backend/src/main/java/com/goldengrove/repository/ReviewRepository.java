package com.goldengrove.repository;

import com.goldengrove.entity.Product;
import com.goldengrove.entity.Review;
import com.goldengrove.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductOrderByCreatedAtDesc(Product product);
    Optional<Review> findByUserAndProduct(User user, Product product);
}
