package com.n8.emarket.repository;

import com.n8.emarket.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    // Tìm tồn kho = ID Sản phẩm
    Stock findByProduct_IdProduct(Long idProduct);

    // lấy list theo id san pham
    List<Stock> findByProduct_IdProductIn(List<Long> idProducts);

    // lấy 1 stock theo branch
    Stock findByProduct_IdProductAndBranch_IdBranch(Long idProduct, Long idBranch);

    // lấy list theo branch
    List<Stock> findByProduct_IdProductInAndBranch_IdBranch(List<Long> ids, Long idBranch);

    // List<Stock> findByProduct_IdProductInAndBranch_IdBranch(List<Long> productIds, Long idBranch);
}
