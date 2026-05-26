package com.n8.emarket.service;

import com.n8.emarket.dto.ProductResponse;
import com.n8.emarket.entity.Product;
import com.n8.emarket.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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
            return dto;
        });
    }
    // ham xem chi tiet 1 san pham
    public ProductResponse getProductDetail(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> p.getIsDelete() == 0)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại hoặc đã bị ngừng kinh doanh!"));

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
        return dto;
    }
}