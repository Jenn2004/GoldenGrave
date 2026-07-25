package com.goldengrove.repository;

import com.goldengrove.entity.Product;
import com.goldengrove.entity.WineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByIsFeaturedTrue(Pageable pageable);

    Page<Product> findByIsGiftEligibleTrue(Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:wineType IS NULL OR p.wineType = :wineType)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Product> searchProducts(
            @Param("categoryId") Long categoryId,
            @Param("wineType") WineType wineType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("search") String search,
            Pageable pageable);

    List<Product> findByStockLessThanEqual(Integer stock);
}
