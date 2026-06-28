package com.n8.emarket.service;

import com.n8.emarket.dto.OrderResponse;
import com.n8.emarket.entity.*;
import com.n8.emarket.repository.AvailableVoucherRepository;
import com.n8.emarket.repository.OrderDetailsRepository;
import com.n8.emarket.repository.OrdersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesServiceTest {

    @Mock
    private OrdersRepository mockOrdersRepository;
    @Mock
    private OrderDetailsRepository mockOrderDetailsRepository;
    @Mock
    private AvailableVoucherRepository mockAvailableVoucherRepository;

    @InjectMocks
    private SalesService salesServiceUnderTest;


    // 1. TEST LUỒNG LẤY DANH SÁCH ĐƠN HÀNG (CÓ PHÂN TRANG)


    @Test
    void testGetOrdersForStaff_WithSpecificStatus() {
        // Kịch bản: Nhân viên muốn lọc ra các đơn đang "PENDING"
        final Orders order = createMockOrder(1L, "PENDING", 1L);
        final Page<Orders> ordersPage = new PageImpl<>(List.of(order));

        // Báo cho kho biết là nếu tìm đơn "PENDING" thì trả về danh sách có 1 đơn này
        when(mockOrdersRepository.findByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDesc(
                eq(1L), eq("PENDING"), eq(0), any(Pageable.class))).thenReturn(ordersPage);

        // Giả lập chi tiết đơn hàng
        final OrderDetails orderDetails = createMockOrderDetails();
        when(mockOrderDetailsRepository.findByOrders_IdOrders(1L)).thenReturn(List.of(orderDetails));

        // Thực thi
        final Page<OrderResponse> result = salesServiceUnderTest.getOrdersForStaff(1L, "PENDING", 0, 10);

        // Kiểm tra
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo("PENDING");
        assertThat(result.getContent().get(0).getItems().get(0).getProductName()).isEqualTo("Sản phẩm Test");
    }

    @Test
    void testGetOrdersForStaff_WithoutStatus() {
        // Kịch bản: Nhân viên không chọn trạng thái (Lấy tất cả đơn của chi nhánh)
        final Orders order = createMockOrder(1L, "SHIPPING", 1L);
        final Page<Orders> ordersPage = new PageImpl<>(List.of(order));

        when(mockOrdersRepository.findByBranch_IdBranchAndIsDeleteOrderByCreatedAtDesc(
                eq(1L), eq(0), any(Pageable.class))).thenReturn(ordersPage);

        when(mockOrderDetailsRepository.findByOrders_IdOrders(1L)).thenReturn(Collections.emptyList());

        // Thực thi (Truyền status = null)
        final Page<OrderResponse> result = salesServiceUnderTest.getOrdersForStaff(1L, null, 0, 10);

        // Kiểm tra
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo("SHIPPING");
    }


    // 2. TEST LUỒNG CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG


    @Test
    void testUpdateOrderStatus_Success() {
        // Kịch bản: Cập nhật thành công từ PENDING sang SHIPPING
        final Orders order = createMockOrder(1L, "PENDING", 1L);
        when(mockOrdersRepository.findById(1L)).thenReturn(Optional.of(order));

        // Thực thi
        final String result = salesServiceUnderTest.updateOrderStatus(1L, "SHIPPING", 1L);

        // Kiểm tra
        assertThat(result).isEqualTo("Cập nhật trạng thái đơn hàng số 1 thành SHIPPING thành công!");
        assertThat(order.getStatus()).isEqualTo("SHIPPING");
        verify(mockOrdersRepository).save(order);
    }

    @Test
    void testUpdateOrderStatus_Cancel_And_RestoreVoucher() {
        // Kịch bản: Hủy đơn hàng và hệ thống phải hoàn lại Voucher cho khách
        final Orders order = createMockOrder(1L, "PENDING", 1L);

        // Khách hàng có áp dụng 1 Voucher vào đơn này
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(99L);
        order.setVoucher(voucher);

        when(mockOrdersRepository.findById(1L)).thenReturn(Optional.of(order));

        // Giả lập kho Voucher đang còn 0 lượt dùng
        final AvailableVoucher myVoucher = new AvailableVoucher();
        myVoucher.setQuantity(0);
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(
                order.getCustomer().getIdCustomer(), 99L)).thenReturn(myVoucher);

        // Thực thi
        final String result = salesServiceUnderTest.updateOrderStatus(1L, "CANCELLED", 1L);

        // Kiểm tra
        assertThat(result).isEqualTo("Cập nhật trạng thái đơn hàng số 1 thành CANCELLED thành công!");
        assertThat(order.getStatus()).isEqualTo("CANCELLED");

        // Cực kỳ quan trọng: Kiểm tra xem số lượng Voucher đã được cộng lại thành 1 chưa
        assertThat(myVoucher.getQuantity()).isEqualTo(1);
        verify(mockAvailableVoucherRepository).save(myVoucher);
    }


    // 3. CÁC KỊCH BẢN NÉM LỖI (BẮT BUG)

    @Test
    void testUpdateOrderStatus_Fail_WrongBranch() {
        // Kịch bản: Nhân viên chi nhánh 2 cố tình sửa đơn của chi nhánh 1
        final Orders order = createMockOrder(1L, "PENDING", 1L); // Đơn thuộc chi nhánh 1
        when(mockOrdersRepository.findById(1L)).thenReturn(Optional.of(order));

        // Thực thi và kỳ vọng ném ra lỗi RuntimeException
        assertThatThrownBy(() -> salesServiceUnderTest.updateOrderStatus(1L, "SHIPPING", 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bạn không có quyền thao tác trên đơn hàng của chi nhánh khác!");

        // Đảm bảo không có lệnh lưu DB nào được gọi
        verify(mockOrdersRepository, never()).save(any());
    }

    @Test
    void testUpdateOrderStatus_Fail_AlreadyCancelled() {
        // Kịch bản: Đơn hàng đã bị hủy, không cho phép sửa nữa
        final Orders order = createMockOrder(1L, "CANCELLED", 1L);
        when(mockOrdersRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> salesServiceUnderTest.updateOrderStatus(1L, "DELIVERED", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Đơn hàng này đã bị hủy từ trước, không thể thao tác thêm!");
    }

    @Test
    void testUpdateOrderStatus_Fail_OrderNotFound() {
        // Kịch bản: Tìm đơn hàng không tồn tại
        when(mockOrdersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesServiceUnderTest.updateOrderStatus(99L, "SHIPPING", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Không tìm thấy đơn hàng!");
    }


    // HÀM HỖ TRỢ TẠO DỮ LIỆU GIẢ NHANH (Helper Methods)

    private Orders createMockOrder(Long idOrder, String status, Long idBranch) {
        final Orders order = new Orders();
        order.setIdOrders(idOrder);
        order.setStatus(status);
        order.setTotal(50000.0);
        order.setCreatedAt(LocalDateTime.now());

        final Branch branch = new Branch();
        branch.setIdBranch(idBranch);
        order.setBranch(branch);

        final Customer customer = new Customer();
        customer.setIdCustomer(1L);
        order.setCustomer(customer);

        return order;
    }

    private OrderDetails createMockOrderDetails() {
        final OrderDetails details = new OrderDetails();
        details.setQuantity(2);

        final Product product = new Product();
        product.setIdProduct(1L);
        product.setName("Sản phẩm Test");
        product.setPrice(25000.0);
        details.setProduct(product);

        return details;
    }
}