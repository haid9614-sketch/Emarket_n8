package com.n8.emarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String role;
    private Long idUser;
    // null neu la khach hang
    private Long idBranch;
}
