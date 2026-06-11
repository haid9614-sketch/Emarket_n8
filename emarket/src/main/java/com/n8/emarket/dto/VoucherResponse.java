package com.n8.emarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {

    private Long idVoucher;
    private Double discount;
    private Integer quantity;
}