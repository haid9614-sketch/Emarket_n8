package com.n8.emarket.controller;

import com.n8.emarket.dto.CheckoutRequest;
import com.n8.emarket.dto.OrderResponse;
import com.n8.emarket.service.OrdersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    // 1. API xem lịch sử đơn hàng
    // GET http://localhost:8080/api/orders/history/1
    @GetMapping("/history/{idCustomer}")
    public ResponseEntity<List<OrderResponse>> getHistory(@PathVariable Long idCustomer) {
        return ResponseEntity.ok(ordersService.getOrderHistory(idCustomer));
    }

    // 2. API hủy đơn hàng
    // POST http://localhost:8080/api/orders/cancel?idOrder=2&idCustomer=1
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelOrder(@RequestParam Long idOrder, @RequestParam Long idCustomer) {
        String result = ordersService.cancelOrder(idOrder, idCustomer);
        return ResponseEntity.ok(result);
    }
}