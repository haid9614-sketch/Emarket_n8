package com.n8.emarket.controller;

import com.n8.emarket.dto.AddToCartRequest;
import com.n8.emarket.dto.CartsResponse;
import com.n8.emarket.service.CartsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartsController {
    @Autowired
    private CartsService cartsService;
    // api them san pham vao gio: http://localhost:8080/api/carts/add
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@Valid @RequestBody AddToCartRequest request) {
        String resultMessage = cartsService.addToCart(request);
        return ResponseEntity.ok(resultMessage);
    }

    // api xem gio hang http://localhost:8080/api/carts/1?idBranch=1
    @GetMapping("/{idCustomer}")
    public ResponseEntity<CartsResponse> getCart(@PathVariable Long idCustomer,
                                                 @RequestParam(name = "idBranch") Long idBranch) {
        CartsResponse cart = cartsService.getCartsByCustomerId(idCustomer, idBranch);
        return ResponseEntity.ok(cart);
    }

    // API Sửa số lượng (PUT)
    // URL: http://localhost:8080/api/carts/update?idCustomer=1&idProduct=1&idBranch=1&newQuantity=10
    @PutMapping("/update")
    public ResponseEntity<String> updateCartQuantity(
            @RequestParam(name = "idCustomer") Long idCustomer,
            @RequestParam(name = "idProduct") Long idProduct,
            @RequestParam(name = "idBranch") Long idBranch,
            @RequestParam(name = "newQuantity") Integer newQuantity
    ) {
        return ResponseEntity.ok(cartsService.updateQuantity(idCustomer, idProduct, idBranch, newQuantity));
    }

    // API Xóa sản phẩm (DELETE)
    // URL: http://localhost:8080/api/carts/remove?idCustomer=1&idProduct=1
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCartItem(
            @RequestParam Long idCustomer,
            @RequestParam Long idProduct) {

        return ResponseEntity.ok(cartsService.removeCartItem(idCustomer, idProduct));
    }
}
