package com.n8.emarket.repository;

import com.n8.emarket.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByCustomer_IdCustomerAndIsDeleteOrderByCreatedAtDesc(Long idCustomer, Integer isDelete);
    List<Orders> findByCustomer_IdCustomerAndStatusAndIsDeleteOrderByCreatedAtDesc(Long idCustomer, String status, Integer isDelete);


    // cho nhan vien
    Page<Orders> findByBranch_IdBranchAndIsDeleteOrderByCreatedAtDesc(Long idBranch, Integer isDelete, Pageable pageable);


    Page<Orders> findByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDesc(Long idBranch, String status, Integer isDelete, Pageable pageable);
}
