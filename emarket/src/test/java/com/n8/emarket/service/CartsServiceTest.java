package com.n8.emarket.service;

import com.n8.emarket.dto.AddToCartRequest;
import com.n8.emarket.dto.CartsResponse;
import com.n8.emarket.entity.*;
import com.n8.emarket.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartsServiceTest {

    @Mock
    private CartsRepository mockCartsRepository;
    @Mock
    private CartItemsRepository mockCartItemsRepository;
    @Mock
    private StockRepository mockStockRepository;
    @Mock
    private CustomerRepository mockCustomerRepository;
    @Mock
    private ProductRepository mockProductRepository;

    @InjectMocks
    private CartsService cartsServiceUnderTest;


    // 1. TEST LUỒNG THÊM VÀO GIỎ HÀNG


    @Test
    void testAddToCart_NewProduct_Success() {
        // Kịch bản: Khách thêm 1 sản phẩm MỚI TINH vào giỏ hàng
        AddToCartRequest request = new AddToCartRequest();
        request.setIdProduct(1L);
        request.setIdBranch(1L);
        request.setQuantity(2); // Khách muốn mua 2 cái

        // Trong kho đang có 10 cái
        when(mockStockRepository.findByProduct_IdProductAndBranch_IdBranch(1L, 1L))
                .thenReturn(createMockStock(10));

        // Khách đã có sẵn giỏ hàng
        Carts cart = createMockCart();
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(cart);

        // Sản phẩm này CHƯA TỪNG có trong giỏ (trả về null)
        when(mockCartItemsRepository.findByCart_IdCartsAndProduct_IdProduct(cart.getIdCarts(), 1L))
                .thenReturn(null);

        // Tìm thông tin sản phẩm để tạo mới CartItem
        when(mockProductRepository.findById(1L)).thenReturn(Optional.of(createMockProduct()));

        // Thực thi
        String result = cartsServiceUnderTest.addToCart(request, 1L);

        // Kiểm tra
        assertThat(result).isEqualTo("Đã thêm thành công sản phẩm vào giỏ hàng!");
        verify(mockCartItemsRepository, times(1)).save(any(CartItems.class));
    }

    @Test
    void testAddToCart_ExistingProduct_Success() {
        // Kịch bản: Khách thêm sản phẩm ĐÃ CÓ TRONG GIỎ (Cộng dồn số lượng)
        AddToCartRequest request = new AddToCartRequest();
        request.setIdProduct(1L);
        request.setIdBranch(1L);
        request.setQuantity(3); // Khách muốn mua THÊM 3 cái

        when(mockStockRepository.findByProduct_IdProductAndBranch_IdBranch(1L, 1L))
                .thenReturn(createMockStock(10)); // Kho có 10

        Carts cart = createMockCart();
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(cart);

        // Giỏ đã có sẵn 2 cái
        CartItems existingItem = createMockCartItem(2);
        when(mockCartItemsRepository.findByCart_IdCartsAndProduct_IdProduct(cart.getIdCarts(), 1L))
                .thenReturn(existingItem);

        // Thực thi
        String result = cartsServiceUnderTest.addToCart(request, 1L);

        // Kiểm tra: Tổng số lượng phải cập nhật thành 5 (2 cũ + 3 mới)
        assertThat(result).isEqualTo("Đã thêm thành công sản phẩm vào giỏ hàng!");
        assertThat(existingItem.getQuantity()).isEqualTo(5);
        verify(mockCartItemsRepository, times(1)).save(existingItem);
    }

    @Test
    void testAddToCart_Fail_NotEnoughStock() {
        // Kịch bản: Khách đòi mua nhiều hơn số lượng trong kho
        AddToCartRequest request = new AddToCartRequest();
        request.setIdProduct(1L);
        request.setIdBranch(1L);
        request.setQuantity(5); // Khách muốn mua 5 cái

        when(mockStockRepository.findByProduct_IdProductAndBranch_IdBranch(1L, 1L))
                .thenReturn(createMockStock(3)); // Nhưng kho chỉ còn 3

        Carts cart = createMockCart();
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(cart);
        when(mockCartItemsRepository.findByCart_IdCartsAndProduct_IdProduct(cart.getIdCarts(), 1L))
                .thenReturn(null);

        // Thực thi & Bắt lỗi
        assertThatThrownBy(() -> cartsServiceUnderTest.addToCart(request, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Thất bại! Chi nhánh này chỉ còn 3 sản phẩm.");
    }


    // 2. TEST LUỒNG XEM GIỎ HÀNG


    @Test
    void testGetCartsByCustomerId_Success() {
        // Kịch bản: Lấy giỏ hàng thành công, tính đúng tổng tiền
        Carts cart = createMockCart();
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(cart);

        // Giỏ có 1 sản phẩm (SL: 2, Giá: 50.000 => Tổng: 100.000)
        CartItems cartItem = createMockCartItem(2);
        when(mockCartItemsRepository.findByCart_IdCarts(cart.getIdCarts())).thenReturn(List.of(cartItem));

        // Kho vẫn còn đủ hàng (10 cái)
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(1L), 1L))
                .thenReturn(List.of(createMockStock(10)));

        // Thực thi
        CartsResponse response = cartsServiceUnderTest.getCartsByCustomerId(1L, 1L);

        // Kiểm tra
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getTotalPrice()).isEqualTo(100000.0); // 50k * 2
        assertThat(response.getItems().get(0).isAvailable()).isTrue();
    }

    @Test
    void testGetCartsByCustomerId_CartEmpty() {
        // Kịch bản: Khách chưa có giỏ hàng
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(null);

        CartsResponse response = cartsServiceUnderTest.getCartsByCustomerId(1L, 1L);

        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotalPrice()).isEqualTo(0.0);
    }

    // 3. TEST LUỒNG CẬP NHẬT & XÓA SẢN PHẨM TRONG GIỎ


    @Test
    void testUpdateQuantity_Success() {
        // Kịch bản: Vào giỏ hàng bấm tăng số lượng lên 4
        when(mockStockRepository.findByProduct_IdProductAndBranch_IdBranch(1L, 1L))
                .thenReturn(createMockStock(10));

        Carts cart = createMockCart();
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(cart);

        CartItems item = createMockCartItem(2);
        when(mockCartItemsRepository.findByCart_IdCartsAndProduct_IdProduct(cart.getIdCarts(), 1L))
                .thenReturn(item);

        // Thực thi
        String result = cartsServiceUnderTest.updateQuantity(1L, 1L, 1L, 4);

        // Kiểm tra
        assertThat(result).isEqualTo("Cập nhật số lượng thành công!");
        assertThat(item.getQuantity()).isEqualTo(4);
        verify(mockCartItemsRepository, times(1)).save(item);
    }

    @Test
    void testRemoveCartItem_Success() {
        // Kịch bản: Bấm nút Xóa sản phẩm khỏi giỏ
        Carts cart = createMockCart();
        when(mockCartsRepository.findByCustomer_IdCustomer(1L)).thenReturn(cart);

        CartItems item = createMockCartItem(2);
        when(mockCartItemsRepository.findByCart_IdCartsAndProduct_IdProduct(cart.getIdCarts(), 1L))
                .thenReturn(item);

        // Thực thi
        String result = cartsServiceUnderTest.removeCartItem(1L, 1L);

        // Kiểm tra
        assertThat(result).isEqualTo("Đã xóa sản phẩm khỏi giỏ hàng!");
        verify(mockCartItemsRepository, times(1)).delete(item);
    }


    // HÀM HỖ TRỢ TẠO DỮ LIỆU GIẢ (Helper Methods)


    private Carts createMockCart() {
        Carts cart = new Carts();
        cart.setIdCarts(100L);
        Customer customer = new Customer();
        customer.setIdCustomer(1L);
        cart.setCustomer(customer);
        return cart;
    }

    private Product createMockProduct() {
        Product product = new Product();
        product.setIdProduct(1L);
        product.setName("Sản phẩm Test");
        product.setPrice(50000.0);
        return product;
    }

    private CartItems createMockCartItem(int quantity) {
        CartItems item = new CartItems();
        item.setCart(createMockCart());
        item.setProduct(createMockProduct());
        item.setQuantity(quantity);
        return item;
    }

    private Stock createMockStock(int quantity) {
        Stock stock = new Stock();
        stock.setProduct(createMockProduct());
        stock.setStockQuantity(quantity);
        return stock;
    }
}