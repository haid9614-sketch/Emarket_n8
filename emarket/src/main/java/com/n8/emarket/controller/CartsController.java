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

    // api xem gio hang http://localhost:8080/api/carts/1 (với 1 là ID khách)
    @GetMapping("/{idCustomer}")
    public ResponseEntity<CartsResponse> getCart(@PathVariable Long idCustomer) {
        CartsResponse cart = cartsService.getCartByCustomerId(idCustomer);
        return ResponseEntity.ok(cart);
    }

    // API Sửa số lượng (PUT)
    // URL: http://localhost:8080/api/carts/update?idCustomer=1&idProduct=1&quantity=5
    @PutMapping("/update")
    public ResponseEntity<String> updateQuantity(
            @RequestParam Long idCustomer,
            @RequestParam Long idProduct,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(cartsService.updateQuantity(idCustomer, idProduct, quantity));
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
