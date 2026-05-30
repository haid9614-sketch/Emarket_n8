package com.n8.emarket.repository;

import com.n8.emarket.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomer_IdCustomer(Long idCustomer);
}
