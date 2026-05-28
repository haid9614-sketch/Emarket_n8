package com.n8.emarket.controller;
import com.n8.emarket.dto.ProductResponse;
import com.n8.emarket.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    // API (GET):
    // tat ca:  http://localhost:8080/api/products?page=0&size=10  + &idBranch=.. vao tat ca
    // tim kiem: http://localhost:8080/api/products?keyword=Thịt
    // loc theo danh muc: http://localhost:8080/api/products?categoryId=1
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "idBranch") Long idBranch
    ) {
        Page<ProductResponse> products = productService.getAllProducts(page, size, keyword, categoryId, idBranch);
        return ResponseEntity.ok(products);
    }

    // API Lấy chi tiết 1 sản phẩm
    // GET http://localhost:8080/api/products/1?idBranch=1
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "idBranch") Long idBranch
    ) {
        return ResponseEntity.ok(productService.getProductDetail(id, idBranch));
    }
}