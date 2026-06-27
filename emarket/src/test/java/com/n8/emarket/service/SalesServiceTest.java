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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void testGetOrdersForStaff() {
        // Setup
        // Configure OrdersRepository.findByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDesc(...).
        final Orders orders1 = new Orders();
        orders1.setIdOrders(0L);
        orders1.setTotal(0.0);
        orders1.setStatus("status");
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        orders1.setCustomer(customer);
        final Branch branch = new Branch();
        branch.setIdBranch(0L);
        orders1.setBranch(branch);
        orders1.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders1.setReceiverName("receiverName");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        orders1.setVoucher(voucher);
        final Page<Orders> orders = new PageImpl<>(List.of(orders1));
        when(mockOrdersRepository.findByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDesc(eq(0L), eq("status"),
                eq(0), any(Pageable.class))).thenReturn(orders);

        // Configure OrdersRepository.findByBranch_IdBranchAndIsDeleteOrderByCreatedAtDesc(...).
        final Orders orders3 = new Orders();
        orders3.setIdOrders(0L);
        orders3.setTotal(0.0);
        orders3.setStatus("status");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        orders3.setCustomer(customer1);
        final Branch branch1 = new Branch();
        branch1.setIdBranch(0L);
        orders3.setBranch(branch1);
        orders3.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders3.setReceiverName("receiverName");
        orders3.setReceiverPhone("receiverPhone");
        orders3.setShippingAddress("shippingAddress");
        orders3.setPaymentMethod("paymentMethod");
        orders3.setNote("note");
        final Voucher voucher1 = new Voucher();
        voucher1.setIdVoucher(0L);
        orders3.setVoucher(voucher1);
        final Page<Orders> orders2 = new PageImpl<>(List.of(orders3));
        when(mockOrdersRepository.findByBranch_IdBranchAndIsDeleteOrderByCreatedAtDesc(eq(0L), eq(0),
                any(Pageable.class))).thenReturn(orders2);

        // Configure OrderDetailsRepository.findByOrders_IdOrders(...).
        final OrderDetails orderDetails1 = new OrderDetails();
        orderDetails1.setQuantity(0);
        final Product product = new Product();
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        orderDetails1.setProduct(product);
        final List<OrderDetails> orderDetails = List.of(orderDetails1);
        when(mockOrderDetailsRepository.findByOrders_IdOrders(0L)).thenReturn(orderDetails);

        // Run the test
        final Page<OrderResponse> result = salesServiceUnderTest.getOrdersForStaff(0L, "status", 0, 0);

        // Verify the results
    }

    @Test
    void testGetOrdersForStaff_OrdersRepositoryFindByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDescReturnsNoItems() {
        // Setup
        when(mockOrdersRepository.findByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDesc(eq(0L), eq("status"),
                eq(0), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<OrderResponse> result = salesServiceUnderTest.getOrdersForStaff(0L, "status", 0, 0);

        // Verify the results
    }

    @Test
    void testGetOrdersForStaff_OrdersRepositoryFindByBranch_IdBranchAndIsDeleteOrderByCreatedAtDescReturnsNoItems() {
        // Setup
        when(mockOrdersRepository.findByBranch_IdBranchAndIsDeleteOrderByCreatedAtDesc(eq(0L), eq(0),
                any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<OrderResponse> result = salesServiceUnderTest.getOrdersForStaff(0L, "status", 0, 0);

        // Verify the results
    }

    @Test
    void testGetOrdersForStaff_OrderDetailsRepositoryReturnsNoItems() {
        // Setup
        // Configure OrdersRepository.findByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDesc(...).
        final Orders orders1 = new Orders();
        orders1.setIdOrders(0L);
        orders1.setTotal(0.0);
        orders1.setStatus("status");
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        orders1.setCustomer(customer);
        final Branch branch = new Branch();
        branch.setIdBranch(0L);
        orders1.setBranch(branch);
        orders1.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders1.setReceiverName("receiverName");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        orders1.setVoucher(voucher);
        final Page<Orders> orders = new PageImpl<>(List.of(orders1));
        when(mockOrdersRepository.findByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDesc(eq(0L), eq("status"),
                eq(0), any(Pageable.class))).thenReturn(orders);

        when(mockOrderDetailsRepository.findByOrders_IdOrders(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final Page<OrderResponse> result = salesServiceUnderTest.getOrdersForStaff(0L, "status", 0, 0);

        // Verify the results
    }

    @Test
    void testUpdateOrderStatus() {
        // Setup
        // Configure OrdersRepository.findById(...).
        final Orders orders1 = new Orders();
        orders1.setIdOrders(0L);
        orders1.setTotal(0.0);
        orders1.setStatus("status");
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        orders1.setCustomer(customer);
        final Branch branch = new Branch();
        branch.setIdBranch(0L);
        orders1.setBranch(branch);
        orders1.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders1.setReceiverName("receiverName");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        orders1.setVoucher(voucher);
        final Optional<Orders> orders = Optional.of(orders1);
        when(mockOrdersRepository.findById(0L)).thenReturn(orders);

        // Configure AvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(...).
        final AvailableVoucher availableVoucher = new AvailableVoucher();
        availableVoucher.setIdAvailableVoucher(0L);
        availableVoucher.setQuantity(0);
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        customer1.setName("name");
        availableVoucher.setCustomer(customer1);
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L))
                .thenReturn(availableVoucher);

        // Run the test
        final String result = salesServiceUnderTest.updateOrderStatus(0L, "newStatus", 0L);

        // Verify the results
        assertThat(result).isEqualTo("result");
        verify(mockAvailableVoucherRepository).save(any(AvailableVoucher.class));
        verify(mockOrdersRepository).save(any(Orders.class));
    }

    @Test
    void testUpdateOrderStatus_OrdersRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockOrdersRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> salesServiceUnderTest.updateOrderStatus(0L, "newStatus", 0L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testUpdateOrderStatus_AvailableVoucherRepositoryFindByCustomer_IdCustomerAndVoucher_IdVoucherReturnsNull() {
        // Setup
        // Configure OrdersRepository.findById(...).
        final Orders orders1 = new Orders();
        orders1.setIdOrders(0L);
        orders1.setTotal(0.0);
        orders1.setStatus("status");
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        orders1.setCustomer(customer);
        final Branch branch = new Branch();
        branch.setIdBranch(0L);
        orders1.setBranch(branch);
        orders1.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders1.setReceiverName("receiverName");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        orders1.setVoucher(voucher);
        final Optional<Orders> orders = Optional.of(orders1);
        when(mockOrdersRepository.findById(0L)).thenReturn(orders);

        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L)).thenReturn(null);

        // Run the test
        final String result = salesServiceUnderTest.updateOrderStatus(0L, "newStatus", 0L);

        // Verify the results
        assertThat(result).isEqualTo("result");
        verify(mockOrdersRepository).save(any(Orders.class));
    }
}
