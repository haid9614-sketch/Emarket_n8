package com.n8.emarket.repository;

import com.n8.emarket.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    // Tìm tồn kho = ID Sản phẩm
    Stock findByProduct_IdProduct(Long idProduct);

    // lấy list sp
    List<Stock> findByProduct_IdProductIn(List<Long> idProducts);
}
