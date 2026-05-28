package com.n8.emarket.service;

import com.n8.emarket.dto.ProductResponse;
import com.n8.emarket.entity.Product;
import com.n8.emarket.entity.Stock;
import com.n8.emarket.repository.ProductRepository;
import com.n8.emarket.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockRepository stockRepository;

    // hiem thi danh sach san pham
    public Page<ProductResponse> getAllProducts(int page, int size, String keyword, Long idCategory, Long idBranch) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productRepository.findByKeywordAndBranch(keyword.trim(), idBranch, pageable);
        } else if (idCategory != null) {
            productPage = productRepository.findByCategoryAndBranch(idCategory, idBranch, pageable);
        } else {
            productPage = productRepository.findByBranch(idBranch, pageable);
        }

        List<Long> productIds = productPage.getContent().stream()
                .map(Product::getIdProduct)
                .toList();

        List<Stock> stocks = stockRepository.findByProduct_IdProductInAndBranch_IdBranch(productIds, idBranch);
        Map<Long, Integer> stockMap = new HashMap<>();
        for (Stock stock : stocks) {
            stockMap.put(stock.getProduct().getIdProduct(), stock.getStockQuantity());
        }

        return productPage.map(product -> {
            ProductResponse dto = new ProductResponse();
            dto.setIdProduct(product.getIdProduct());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            dto.setUnit(product.getUnit());
            dto.setDescription(product.getDescription());
            dto.setImageUrl(product.getImageUrl());

            if (product.getCategory() != null) {
                dto.setCategoryName(product.getCategory().getName());
            }

            dto.setStockQuantity(stockMap.getOrDefault(product.getIdProduct(), 0));

            return dto;
        });
    }

    // ham xem chi tiet 1 san pham
    public ProductResponse getProductDetail(Long idProduct, Long idBranch) {
        // 1. Tìm thông tin gốc của sản phẩm
        Product product = productRepository.findById(idProduct)
                .filter(p -> p.getIsDelete() == 0)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại hoặc đã bị ngừng kinh doanh!"));

        // 2. Đóng gói dữ liệu cơ bản
        ProductResponse dto = new ProductResponse();
        dto.setIdProduct(product.getIdProduct());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setUnit(product.getUnit());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }

        Stock stock = stockRepository.findByProduct_IdProductAndBranch_IdBranch(idProduct, idBranch);
        if (stock != null) {
            dto.setStockQuantity(stock.getStockQuantity());
        } else {
            dto.setStockQuantity(0); // Báo Front-end là hết hàng
        }

        return dto;
    }
}