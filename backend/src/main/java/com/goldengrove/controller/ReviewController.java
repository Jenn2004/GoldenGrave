package com.goldengrove.controller;

import com.goldengrove.dto.ReviewRequest;
import com.goldengrove.dto.ReviewResponse;
import com.goldengrove.service.ReviewService;
import com.goldengrove.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{productId}")
    public List<ReviewResponse> getReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(@Valid @RequestBody ReviewRequest request) {
        return reviewService.createReview(SecurityUtils.getCurrentUser().getId(), request);
    }
}
