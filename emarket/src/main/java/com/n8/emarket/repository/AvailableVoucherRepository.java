package com.n8.emarket.repository;

import com.n8.emarket.entity.AvailableVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailableVoucherRepository extends JpaRepository<AvailableVoucher, Long> {
    AvailableVoucher findByCustomer_IdCustomerAndVoucher_IdVoucher(Long idCustomer, Long idVoucher);

    @Query("""
            SELECT av
            FROM AvailableVoucher av
            WHERE av.customer.idCustomer = :customerId
            AND av.isDelete = 0 AND av.quantity > 0
            """)
    List<AvailableVoucher> findListByCustomerId(@Param("customerId") Long customerId);
}
