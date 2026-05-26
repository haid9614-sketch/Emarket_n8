package com.n8.emarket.repository;

import com.n8.emarket.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
   List<Branch> findByIsDelete(Integer isDelete);
}
