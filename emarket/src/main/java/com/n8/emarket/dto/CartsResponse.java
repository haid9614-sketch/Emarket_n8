package com.n8.emarket.dto;

import com.n8.emarket.entity.CartItems;
import lombok.Data;
import java.util.List;

@Data
public class CartsResponse {
    private Long idCart;
    private List<CartItemsResponse> items;
    // Tổng tiền cả giỏ hàng
    private Double totalPrice;
}