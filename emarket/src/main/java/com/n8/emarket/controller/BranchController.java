package com.n8.emarket.controller;

import com.n8.emarket.entity.Branch;
import com.n8.emarket.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    @Autowired
    private BranchRepository branchRepository;

    // API nhả ra danh sách chi nhánh cho Front-end vẽ lên màn hình Pop-up
    @GetMapping
    public ResponseEntity<List<Branch>> getAllBranches() {
        // Lấy tất cả chi nhánh chưa bị xóa (isDelete = 0)
        List<Branch> branches = branchRepository.findByIsDelete(0);
        return ResponseEntity.ok(branches);
    }
}
