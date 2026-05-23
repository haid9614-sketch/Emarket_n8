package com.n8.emarket.dto;

import lombok.Data;

@Data
public class CartItemsResponse {
    private Long idProduct;
    private String productName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Double subTotal;
    // frontend
    private boolean isAvailable;
    private Integer maxAvailable;
}