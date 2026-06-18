package com.n8.emarket.controller;

import com.n8.emarket.dto.CheckoutRequest;
import com.n8.emarket.dto.OrderResponse;
import com.n8.emarket.security.CustomUserDetails;
import com.n8.emarket.service.OrdersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    private Long getCurrentCustomerId() {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getId();
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@Valid @RequestBody CheckoutRequest request) {
        String result = ordersService.checkout(request, getCurrentCustomerId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponse>> getHistory(@RequestParam(name = "status") String status) {
        return ResponseEntity.ok(ordersService.getOrderHistory(getCurrentCustomerId(), status));
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelOrder(@RequestParam(name = "idOrder") Long idOrder) {
        String result = ordersService.cancelOrder(idOrder, getCurrentCustomerId());
        return ResponseEntity.ok(result);
    }
}