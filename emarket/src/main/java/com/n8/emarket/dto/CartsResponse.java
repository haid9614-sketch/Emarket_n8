package com.n8.emarket.dto;

import com.n8.emarket.entity.CartItems;
import lombok.Data;
import java.util.List;

@Data
public class CartsResponse {
    private Long idCarts;
    private List<CartItemsResponse> items;
    // frontend
    private Double totalPrice;
}