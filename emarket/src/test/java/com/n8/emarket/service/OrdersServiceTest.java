package com.n8.emarket.service;

import com.n8.emarket.dto.CheckoutRequest;
import com.n8.emarket.dto.OrderResponse;
import com.n8.emarket.entity.*;
import com.n8.emarket.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    private OrdersRepository mockOrdersRepository;
    @Mock
    private OrderDetailsRepository mockOrderDetailsRepository;
    @Mock
    private CartsRepository mockCartsRepository;
    @Mock
    private CartItemsRepository mockCartItemsRepository;
    @Mock
    private StockRepository mockStockRepository;
    @Mock
    private CustomerRepository mockCustomerRepository;
    @Mock
    private AddressRepository mockAddressRepository;
    @Mock
    private AvailableVoucherRepository mockAvailableVoucherRepository;
    @Mock
    private BranchRepository mockBranchRepository;

    @InjectMocks
    private OrdersService ordersServiceUnderTest;

    // 1. TEST LUỒNG THANH TOÁN (CHECKOUT)

    @Test
    void testCheckout() {
        // Setup dữ liệu đầu vào
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(1L);
        request.setIdVoucher(1L);
        request.setIdBranch(1L);
        request.setPaymentMethod("COD");
        request.setNote("Giao trong giờ hành chính");

        // Giả lập Khách hàng và Giỏ hàng
        final Customer customer = new Customer();
        customer.setIdCustomer(1L);
        customer.setName("Nguyễn Văn A");

        final Carts carts = new Carts();
        carts.setIdCarts(1L);
        carts.setCustomer(customer);
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(carts);

        // Giả lập Sản phẩm trong giỏ (Giá 50.000đ, SL: 2 => Tổng: 100.000đ)
        final Product product = new Product();
        product.setIdProduct(1L);
        product.setName("Sản phẩm Test");
        product.setPrice(50000.0);

        final CartItems cartItems1 = new CartItems();
        cartItems1.setQuantity(2);
        cartItems1.setProduct(product);
        when(mockCartItemsRepository.findByCart_IdCarts(1L)).thenReturn(List.of(cartItems1));

        // Giả lập Kho hàng (Đang có 10 sản phẩm)
        final Stock stock = new Stock();
        stock.setStockQuantity(10);
        stock.setProduct(product);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(1L), 1L)).thenReturn(List.of(stock));

        // Giả lập Địa chỉ
        final Address address = new Address();
        address.setCity("Hà Nội");
        address.setSdt("0123456789");
        address.setCustomer(customer);
        when(mockAddressRepository.findById(1L)).thenReturn(Optional.of(address));

        // Giả lập Tìm khách hàng
        when(mockCustomerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Giả lập Voucher (Giảm 15.000đ)
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(1L);
        voucher.setDiscount(15000.0);
        final AvailableVoucher availableVoucher = new AvailableVoucher();
        availableVoucher.setVoucher(voucher);
        availableVoucher.setQuantity(1);
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(1L, 1L)).thenReturn(availableVoucher);


        final Branch branch = new Branch();
        branch.setIdBranch(1L);
        when(mockBranchRepository.findById(1L)).thenReturn(Optional.of(branch));

        final Orders orders = new Orders();
        orders.setIdOrders(100L);
        orders.setTotal(85000.0); // 100k - 15k
        orders.setStatus("PENDING");
        when(mockOrdersRepository.save(any(Orders.class))).thenReturn(orders);


        final String result = ordersServiceUnderTest.checkout(request, 1L);


        assertThat(result).isNotNull();
        verify(mockAvailableVoucherRepository).save(any(AvailableVoucher.class));
        verify(mockOrderDetailsRepository).saveAll(any());
        verify(mockStockRepository).saveAll(any());
        verify(mockCartItemsRepository).deleteAll(any());
    }

    // 2. TEST LUỒNG LẤY LỊCH SỬ ĐƠN HÀNG

    @Test
    void testGetOrderHistory() {
        // Setup
        final Orders orders1 = new Orders();
        orders1.setIdOrders(100L);
        orders1.setTotal(85000.0);
        orders1.setStatus("PENDING");
        orders1.setReceiverName("Nguyễn Văn A");
        orders1.setReceiverPhone("0123456789");
        orders1.setCreatedAt(LocalDateTime.now());

        when(mockOrdersRepository.findByCustomer_IdCustomerAndStatusAndIsDeleteOrderByCreatedAtDesc(1L, "PENDING", 0))
                .thenReturn(List.of(orders1));

        final OrderDetails orderDetails1 = new OrderDetails();
        orderDetails1.setQuantity(2);
        orderDetails1.setPrice(50000.0);

        final Product product = new Product();
        product.setIdProduct(1L);
        product.setName("Sản phẩm Test");
        orderDetails1.setProduct(product);
        orderDetails1.setOrders(orders1);

        when(mockOrderDetailsRepository.findByOrders_IdOrders(100L)).thenReturn(List.of(orderDetails1));


        final List<OrderResponse> result = ordersServiceUnderTest.getOrderHistory(1L, "PENDING");


        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getIdOrders()).isEqualTo(100L);
        assertThat(result.get(0).getTotal()).isEqualTo(85000.0);
        assertThat(result.get(0).getItems().get(0).getProductName()).isEqualTo("Sản phẩm Test");
    }


    // 3. TEST LUỒNG HỦY ĐƠN HÀNG

    @Test
    void testCancelOrder() {
        // Setup đơn hàng giả đang chờ xác nhận
        final Orders orders1 = new Orders();
        orders1.setIdOrders(100L);
        orders1.setStatus("PENDING");

        final Customer customer = new Customer();
        customer.setIdCustomer(1L);
        orders1.setCustomer(customer);

        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(1L);
        orders1.setVoucher(voucher);

        final Branch branch = new Branch();
        branch.setIdBranch(1L);
        orders1.setBranch(branch);

        when(mockOrdersRepository.findById(100L)).thenReturn(Optional.of(orders1));

        // Setup Voucher để hoàn lại
        final AvailableVoucher availableVoucher = new AvailableVoucher();
        availableVoucher.setQuantity(0); // Số lượng voucher ban đầu
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(1L, 1L))
                .thenReturn(availableVoucher);

        // Setup chi tiết đơn hàng để hoàn kho
        final OrderDetails orderDetails1 = new OrderDetails();
        orderDetails1.setQuantity(2); // Khách mua 2 cái
        final Product product = new Product();
        product.setIdProduct(1L);
        orderDetails1.setProduct(product);
        when(mockOrderDetailsRepository.findByOrders_IdOrders(100L)).thenReturn(List.of(orderDetails1));

        // Setup Kho để cộng lại hàng
        final Stock stock = new Stock();
        stock.setStockQuantity(8); // Trong kho còn 8
        stock.setProduct(product);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(any(), any()))
                .thenReturn(List.of(stock));


        final String result = ordersServiceUnderTest.cancelOrder(100L, 1L);


        assertThat(result).isNotNull();
        assertThat(orders1.getStatus()).isEqualTo("CANCELLED");

        verify(mockOrdersRepository).save(any(Orders.class));
        verify(mockAvailableVoucherRepository).save(any(AvailableVoucher.class));
        verify(mockStockRepository).saveAll(any());
    }


    @Test
    void testCheckout_CartsRepositoryReturnsNull() {
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(1L);
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(null);
        assertThatThrownBy(() -> ordersServiceUnderTest.checkout(request, 1L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCheckout_CartItemsRepositoryFindByCart_IdCartsReturnsNoItems() {
        final CheckoutRequest request = new CheckoutRequest();
        final Carts carts = new Carts();
        carts.setIdCarts(1L);
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(carts);
        when(mockCartItemsRepository.findByCart_IdCarts(1L)).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> ordersServiceUnderTest.checkout(request, 1L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCheckout_StockRepositoryFindByProductReturnsNoItems() {
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdBranch(1L);

        final Carts carts = new Carts();
        carts.setIdCarts(1L);
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(carts);

        final CartItems cartItem = new CartItems();
        final Product product = new Product();
        product.setIdProduct(1L);
        cartItem.setProduct(product);
        when(mockCartItemsRepository.findByCart_IdCarts(1L)).thenReturn(List.of(cartItem));

        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(1L), 1L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> ordersServiceUnderTest.checkout(request, 1L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testGetOrderHistory_OrdersRepositoryReturnsNoItems() {
        when(mockOrdersRepository.findByCustomer_IdCustomerAndStatusAndIsDeleteOrderByCreatedAtDesc(1L, "PENDING", 0))
                .thenReturn(Collections.emptyList());
        final List<OrderResponse> result = ordersServiceUnderTest.getOrderHistory(1L, "PENDING");
        assertThat(result).isEmpty();
    }

    @Test
    void testCancelOrder_OrdersRepositoryFindByIdReturnsAbsent() {
        when(mockOrdersRepository.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ordersServiceUnderTest.cancelOrder(100L, 1L)).isInstanceOf(RuntimeException.class);
    }
}