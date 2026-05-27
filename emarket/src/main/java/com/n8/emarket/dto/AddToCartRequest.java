package com.n8.emarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class AddToCartRequest {
    @NotNull(message = "Thiếu thông tin khách hàng!")
    private Long idCustomer;

    @NotNull(message = "Thiếu thông tin sản phẩm!")
    private Long idProduct;

    @NotNull(message = "Vui lòng nhập số lượng!")
    @Min(value = 1, message = "Số lượng mua ít nhất phải là 1!")
    private Integer quantity;

    private Long idBranch;
}
