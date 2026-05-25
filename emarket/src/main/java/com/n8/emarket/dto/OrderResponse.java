package com.n8.emarket.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long idOrders;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String paymentMethod;
    private String status;
    private Double total;
    private String note;
    private LocalDateTime createdAt;
    // frontend
    private List<OrderDetailItem> items;

    @Data
    public static class OrderDetailItem {
        private String productName;
        private Integer quantity;
        private Double price;
    }
}