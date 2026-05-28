package com.n8.emarket.controller;

import com.n8.emarket.dto.AddToCartRequest;
import com.n8.emarket.dto.CartsResponse;
import com.n8.emarket.security.CustomUserDetails;
import com.n8.emarket.service.CartsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartsController {

    @Autowired
    private CartsService cartsService;

    private Long getCurrentCustomerId() {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getId();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@Valid @RequestBody AddToCartRequest request) {
        String resultMessage = cartsService.addToCart(request, getCurrentCustomerId());
        return ResponseEntity.ok(resultMessage);
    }

    @GetMapping("/my-cart")
    public ResponseEntity<CartsResponse> getCart(@RequestParam(name = "idBranch") Long idBranch) {
        CartsResponse cart = cartsService.getCartsByCustomerId(getCurrentCustomerId(), idBranch);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCartQuantity(
            @RequestParam(name = "idProduct") Long idProduct,
            @RequestParam(name = "idBranch") Long idBranch,
            @RequestParam(name = "newQuantity") Integer newQuantity
    ) {
        return ResponseEntity.ok(cartsService.updateQuantity(getCurrentCustomerId(), idProduct, idBranch, newQuantity));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCartItem(@RequestParam(name = "idProduct") Long idProduct) {
        return ResponseEntity.ok(cartsService.removeCartItem(getCurrentCustomerId(), idProduct));
    }
}