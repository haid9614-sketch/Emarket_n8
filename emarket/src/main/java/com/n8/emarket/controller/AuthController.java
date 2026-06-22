package com.n8.emarket.controller;

import com.n8.emarket.dto.LoginRequest;
import com.n8.emarket.dto.JwtResponse;
import com.n8.emarket.dto.RegisterRequest;
import com.n8.emarket.entity.Customer;
import com.n8.emarket.entity.Sales;
import com.n8.emarket.repository.CustomerRepository;
import com.n8.emarket.repository.SalesRepository;
import com.n8.emarket.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // dang nhap khach hang
    @PostMapping("/customer/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail());

        if (customer == null || !passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            return ResponseEntity.badRequest().body("Email hoặc mật khẩu không chính xác!");
        }

        String token = jwtUtils.generateToken(customer.getEmail(), "ROLE_CUSTOMER", null, customer.getIdCustomer());
        return ResponseEntity.ok(new JwtResponse(token, "ROLE_CUSTOMER", customer.getIdCustomer(), null));
    }

    // dang nhap nhan vien
    @PostMapping("/sales/login")
    public ResponseEntity<?> loginSales(@RequestBody LoginRequest request) {
        Sales sales = salesRepository.findByEmail(request.getEmail());

        if (sales == null || !passwordEncoder.matches(request.getPassword(), sales.getPassword())) {
            return ResponseEntity.badRequest().body("Email hoặc mật khẩu không chính xác!");
        }

        String token = jwtUtils.generateToken(sales.getEmail(), "ROLE_SALES", sales.getBranch().getIdBranch(), sales.getIdSales());
        return ResponseEntity.ok(new JwtResponse(token, "ROLE_SALES", sales.getIdSales(), sales.getBranch().getIdBranch()));
    }

    // dang ky
    @PostMapping("/customer/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody RegisterRequest request) {

        Customer existingCustomer = customerRepository.findByEmail(request.getEmail());
        if (existingCustomer != null) {
            return ResponseEntity.badRequest().body("Thất bại! Email này đã được sử dụng.");
        }

        Customer newCustomer = new Customer();
        newCustomer.setName(request.getName());
        newCustomer.setEmail(request.getEmail());
        newCustomer.setPhone(request.getPhone());
        newCustomer.setAge(request.getAge());
        newCustomer.setIsDelete(0);
        newCustomer.setCreatedAt(LocalDateTime.now());
        newCustomer.setUpdatedAt(LocalDateTime.now());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        newCustomer.setPassword(encodedPassword);

        customerRepository.save(newCustomer);

        return ResponseEntity.ok("Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay.");
    }

}