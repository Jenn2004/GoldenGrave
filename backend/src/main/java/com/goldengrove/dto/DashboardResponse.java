package com.goldengrove.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal totalSales;
    private long totalOrders;
    private long pendingOrders;
    private long lowStockCount;
    private List<ProductResponse> lowStockProducts;
}
