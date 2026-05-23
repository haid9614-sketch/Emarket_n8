package com.n8.emarket.controller;

import com.n8.emarket.dto.CheckoutRequest;
import com.n8.emarket.service.OrdersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    // API đat hang
    // POST http://localhost:8080/api/orders/checkout
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@Valid @RequestBody CheckoutRequest request) {
        String result = ordersService.checkout(request);
        return ResponseEntity.ok(result);
    }
}