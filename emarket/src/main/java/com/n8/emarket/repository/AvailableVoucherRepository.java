package com.n8.emarket.repository;

import com.n8.emarket.entity.AvailableVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableVoucherRepository extends JpaRepository<AvailableVoucher, Long> {
    AvailableVoucher findByCustomer_IdCustomerAndVoucher_IdVoucher(Long idCustomer, Long idVoucher);
}
