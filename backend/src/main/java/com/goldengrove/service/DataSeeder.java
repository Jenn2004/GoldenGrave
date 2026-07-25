package com.goldengrove.service;

import com.goldengrove.entity.*;
import com.goldengrove.repository.CategoryRepository;
import com.goldengrove.repository.ProductRepository;
import com.goldengrove.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        userRepository.save(User.builder()
                .name(adminName)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        Category red = categoryRepository.save(Category.builder().name("Red Wines").build());
        Category white = categoryRepository.save(Category.builder().name("White Wines").build());
        Category rose = categoryRepository.save(Category.builder().name("Rosé Wines").build());
        Category aged = categoryRepository.save(Category.builder().name("Aged Reserve").build());

        productRepository.saveAll(java.util.List.of(
                Product.builder().name("Sunset Merlot").description("Rich berry notes with a velvet finish. Handcrafted in small batches from locally sourced grapes.")
                        .price(new BigDecimal("1299")).stock(24).category(red).wineType(WineType.RED)
                        .vintageYear(2023).abv(new BigDecimal("13.5"))
                        .imageUrl("https://images.unsplash.com/photo-1510812431400-5740f7d95b85?w=600")
                        .isFeatured(true).isGiftEligible(true).build(),
                Product.builder().name("Golden Chardonnay").description("Crisp apple and honey blossom with a clean, refreshing palate.")
                        .price(new BigDecimal("1199")).stock(18).category(white).wineType(WineType.WHITE)
                        .vintageYear(2024).abv(new BigDecimal("12.0"))
                        .imageUrl("https://images.unsplash.com/photo-1566995541428-f2242c740fd9?w=600")
                        .isFeatured(true).isGiftEligible(true).build(),
                Product.builder().name("Blush Rosé").description("Delicate strawberry and rose petal aromas. Perfect for warm evenings.")
                        .price(new BigDecimal("1099")).stock(30).category(rose).wineType(WineType.ROSE)
                        .vintageYear(2024).abv(new BigDecimal("11.5"))
                        .imageUrl("https://images.unsplash.com/photo-1553361371-9b22f78e09b9?w=600")
                        .isFeatured(true).isGiftEligible(true).build(),
                Product.builder().name("Heritage Reserve").description("Aged 18 months in oak. Deep complexity with notes of dark cherry and spice.")
                        .price(new BigDecimal("1899")).stock(8).category(aged).wineType(WineType.AGED)
                        .vintageYear(2021).abv(new BigDecimal("14.0"))
                        .imageUrl("https://images.unsplash.com/photo-1506377247377-780cac9fd50f?w=600")
                        .isFeatured(true).isGiftEligible(true).build(),
                Product.builder().name("Forest Pinot Noir").description("Earthy undertones with bright cherry and silky tannins.")
                        .price(new BigDecimal("1399")).stock(15).category(red).wineType(WineType.RED)
                        .vintageYear(2023).abv(new BigDecimal("13.0"))
                        .imageUrl("https://images.unsplash.com/photo-1474722883778-792eaa9f6609?w=600")
                        .isFeatured(false).isGiftEligible(false).build(),
                Product.builder().name("Citrus Sauvignon").description("Zesty lime and grapefruit with a mineral finish.")
                        .price(new BigDecimal("999")).stock(22).category(white).wineType(WineType.WHITE)
                        .vintageYear(2024).abv(new BigDecimal("11.0"))
                        .imageUrl("https://images.unsplash.com/photo-1586370434639-0fe43b01920e?w=600")
                        .isFeatured(false).isGiftEligible(true).build()
        ));
    }
}
