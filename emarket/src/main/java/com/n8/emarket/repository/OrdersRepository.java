package com.n8.emarket.repository;

import com.n8.emarket.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByCustomer_IdCustomerAndIsDeleteOrderByCreatedAtDesc(Long idCustomer, Integer isDelete);
    List<Orders> findByCustomer_IdCustomerAndStatusAndIsDeleteOrderByCreatedAtDesc(Long idCustomer, String status, Integer isDelete);
    // cho nhan vien
    List<Orders> findByBranch_IdBranchAndIsDeleteOrderByCreatedAtDesc(Long idBranch, Integer isDelete);


    List<Orders> findByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDesc(Long idBranch, String status, Integer isDelete);
}
