package com.n8.emarket.repository;

import com.n8.emarket.entity.Carts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CartsRepository extends JpaRepository<Carts, Long> {
}
