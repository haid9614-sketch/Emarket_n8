package com.n8.emarket.controller;

import com.n8.emarket.dto.VoucherResponse;
import com.n8.emarket.security.CustomUserDetails;
import com.n8.emarket.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/voucher")
public class VoucherController {
    @Autowired
    VoucherService voucherService;

    private Long getCurrentCustomerId() {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getId();
    }
    @GetMapping
    public ResponseEntity<List<VoucherResponse>> getVoucherByCustomer() {

        return ResponseEntity.ok(
                voucherService.getAvailableVoucherByCustomer(getCurrentCustomerId())
        );
    }

}
