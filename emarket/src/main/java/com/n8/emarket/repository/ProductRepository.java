package com.n8.emarket.repository;
import com.n8.emarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByIsDelete(Integer isDelete, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndIsDelete(String name, Integer isDelete, Pageable pageable);

    Page<Product> findByCategory_IdCategoryAndIsDelete(Long idCategory, Integer isDelete, Pageable pageable);

    // theo chi nhanh:
    // tất cả sản phẩm
    @Query("SELECT s.product FROM Stock s WHERE s.branch.idBranch = :idBranch AND s.product.isDelete = 0")
    Page<Product> findByBranch(@Param("idBranch") Long idBranch, Pageable pageable);

    // Tìm kiếm theo tên
    @Query("SELECT s.product FROM Stock s WHERE s.branch.idBranch = :idBranch AND LOWER(s.product.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND s.product.isDelete = 0")
    Page<Product> findByKeywordAndBranch(@Param("keyword") String keyword, @Param("idBranch") Long idBranch, Pageable pageable);

    // 3. Lọc theo danh mục
    @Query("SELECT s.product FROM Stock s WHERE s.branch.idBranch = :idBranch AND s.product.category.idCategory = :idCategory AND s.product.isDelete = 0")
    Page<Product> findByCategoryAndBranch(@Param("idCategory") Long idCategory, @Param("idBranch") Long idBranch, Pageable pageable);
}

