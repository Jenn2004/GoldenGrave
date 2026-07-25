package com.goldengrove.service;

import com.goldengrove.dto.ReviewRequest;
import com.goldengrove.dto.ReviewResponse;
import com.goldengrove.entity.OrderStatus;
import com.goldengrove.entity.Product;
import com.goldengrove.entity.Review;
import com.goldengrove.entity.User;
import com.goldengrove.exception.BadRequestException;
import com.goldengrove.exception.ResourceNotFoundException;
import com.goldengrove.repository.OrderItemRepository;
import com.goldengrove.repository.ProductRepository;
import com.goldengrove.repository.ReviewRepository;
import com.goldengrove.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    private static final List<OrderStatus> PAID_STATUSES =
            List.of(OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.DELIVERED);

    public List<ReviewResponse> getProductReviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return reviewRepository.findByProductOrderByCreatedAtDesc(product).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    @Transactional
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (reviewRepository.findByUserAndProduct(user, product).isPresent()) {
            throw new BadRequestException("You already reviewed this product");
        }

        boolean verified = orderItemRepository.hasPurchasedProduct(user, product.getId(), PAID_STATUSES);
        if (!verified) {
            throw new BadRequestException("You can only review products you have purchased");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .isVerifiedPurchase(true)
                .build();
        return ReviewResponse.from(reviewRepository.save(review));
    }
}
