package com.n8.emarket.dto;

import lombok.Data;

@Data
public class CartItemsResponse {
    private Long idProduct;
    private String productName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    // Tổng tiền của món này (giá * số lượng)
    private Double subTotal;
}