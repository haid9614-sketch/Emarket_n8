package com.n8.emarket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {
    private String houseNumber;
    private String ward;
    private String district;
    private String city;
    private String name;
    private String sdt;
}