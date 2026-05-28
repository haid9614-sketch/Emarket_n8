package com.n8.emarket.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class CheckoutRequest {
//    @NotNull(message = "Thiếu thông tin khách hàng!")
//    private Long idCustomer;

    @NotNull(message = "Vui lòng chọn địa chỉ giao hàng!")
    private Long idAddress;

    private Long idVoucher;

    private Long idBranch;
    // frontend
    @NotBlank(message = "Vui lòng chọn phương thức thanh toán!")
    private String paymentMethod;

    private String note;

}
