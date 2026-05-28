package com.n8.emarket.dto;

import lombok.Data;

@Data
public class ProductResponse {
    private Long idProduct;
    private String name;
    private Double price;
    private String unit;
    private String description;
    private String imageUrl;
    private String categoryName;
    private Integer stockQuantity;
}