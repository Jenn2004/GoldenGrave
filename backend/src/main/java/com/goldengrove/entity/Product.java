package com.goldengrove.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WineType wineType;

    private Integer vintageYear;

    @Column(precision = 4, scale = 1)
    private BigDecimal abv;

    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isGiftEligible = false;
}
