package com.n8.emarket.controller;

import com.n8.emarket.dto.OrderResponse;
import com.n8.emarket.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales/orders")
public class SalesController {

    @Autowired
    private SalesService salesService;

    // nhan vien tai danh sanh don hang + loc trang thai
    // xem tat ca GET  http://localhost:8080/api/sales/orders/branch/1?page=0&size=10
    // Lọc theo trạng thái GET  http://localhost:8080/api/sales/orders/branch/1?status=PENDING&page=0&size=10
    @GetMapping("/branch/{idBranch}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByBranch(
            @PathVariable(name = "idBranch") Long idBranch,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(salesService.getOrdersForStaff(idBranch, status, page, size));
    }

    // API cap nhap trang thai don hang:
    // PUT http://localhost:8080/api/sales/orders/1/status?newStatus=CANCELLED&idBranch=1
    @PutMapping("/{idOrder}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable(name = "idOrder") Long idOrder,
            @RequestParam(name = "newStatus") String newStatus,
            @RequestParam(name = "idBranch") Long idBranch
    ) {
        return ResponseEntity.ok(salesService.updateOrderStatus(idOrder, newStatus, idBranch));
    }
}