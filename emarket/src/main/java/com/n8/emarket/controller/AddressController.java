package com.n8.emarket.controller;

import com.n8.emarket.dto.AddressRequest;
import com.n8.emarket.entity.Address;
import com.n8.emarket.security.JwtUtils;
import com.n8.emarket.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private JwtUtils jwtUtils;

    private Long getCurrentCustomerId(HttpServletRequest request) {
        String jwt = null;
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
        }

        if (jwt == null) {
            throw new RuntimeException("Chưa đăng nhập hoặc thiếu Token!");
        }

        return jwtUtils.getIdUserFromToken(jwt);
    }

    @GetMapping("/my-addresses")
    public ResponseEntity<List<Address>> getMyAddresses(HttpServletRequest request) {
        Long idCustomer = getCurrentCustomerId(request);
        return ResponseEntity.ok(addressService.getMyAddresses(idCustomer));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addAddress(@RequestBody AddressRequest addressRequest, HttpServletRequest request) {
        Long idCustomer = getCurrentCustomerId(request);
        return ResponseEntity.ok(addressService.addAddress(addressRequest, idCustomer));
    }

    @DeleteMapping("/delete/{idAddress}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long idAddress, HttpServletRequest request) {
        Long idCustomer = getCurrentCustomerId(request);
        return ResponseEntity.ok(addressService.deleteAddress(idAddress, idCustomer));
    }
}
