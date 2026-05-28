package com.n8.emarket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private String password;
}