package com.n8.emarket.service;

import com.n8.emarket.dto.VoucherResponse;
import com.n8.emarket.entity.AvailableVoucher;
import com.n8.emarket.repository.AvailableVoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherService {
    @Autowired
    AvailableVoucherRepository availableVoucherRepository;

    public List<VoucherResponse> getAvailableVoucherByCustomer(Long customerId) {

        List<AvailableVoucher> availableVouchers =
                availableVoucherRepository.findListByCustomerId(customerId);

        return availableVouchers.stream()
                .map(av -> new VoucherResponse(
                        av.getVoucher().getIdVoucher(),
                        av.getVoucher().getDiscount(),
                        av.getQuantity()
                ))
                .toList();
    }
}
