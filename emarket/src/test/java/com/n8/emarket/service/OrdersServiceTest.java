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

    @Test
    void testCheckout() {
        // Setup
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(0L);
        request.setIdVoucher(0L);
        request.setIdBranch(0L);
        request.setPaymentMethod("paymentMethod");
        request.setNote("note");

        // Configure CartsRepository.findByCustomer_IdCustomer(...).
        final Carts carts = new Carts();
        carts.setIdCarts(0L);
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        customer.setName("name");
        customer.setAge(0);
        carts.setCustomer(customer);
        when(mockCartsRepository.findByCustomer_IdCustomer(0L)).thenReturn(carts);

        // Configure CartItemsRepository.findByCart_IdCarts(...).
        final CartItems cartItems1 = new CartItems();
        cartItems1.setQuantity(0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        cartItems1.setProduct(product);
        final List<CartItems> cartItems = List.of(cartItems1);
        when(mockCartItemsRepository.findByCart_IdCarts(0L)).thenReturn(cartItems);

        // Configure StockRepository.findByProduct_IdProductInAndBranch_IdBranch(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        stock.setProduct(product1);
        final List<Stock> stocks = List.of(stock);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L)).thenReturn(stocks);

        // Configure AddressRepository.findById(...).
        final Address address1 = new Address();
        address1.setHouseNumber("houseNumber");
        address1.setWard("ward");
        address1.setDistrict("district");
        address1.setCity("city");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        address1.setCustomer(customer1);
        address1.setName("name");
        address1.setSdt("receiverPhone");
        final Optional<Address> address = Optional.of(address1);
        when(mockAddressRepository.findById(0L)).thenReturn(address);

        // Configure CustomerRepository.findById(...).
        final Customer customer3 = new Customer();
        customer3.setIdCustomer(0L);
        customer3.setName("name");
        customer3.setAge(0);
        customer3.setEmail("email");
        customer3.setPhone("phone");
        final Optional<Customer> customer2 = Optional.of(customer3);
        when(mockCustomerRepository.findById(0L)).thenReturn(customer2);

        // Configure AvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(...).
        final AvailableVoucher availableVoucher = new AvailableVoucher();
        availableVoucher.setIdAvailableVoucher(0L);
        availableVoucher.setQuantity(0);
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        voucher.setDiscount(0.0);
        availableVoucher.setVoucher(voucher);
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L))
                .thenReturn(availableVoucher);

        // Configure BranchRepository.findById(...).
        final Branch branch1 = new Branch();
        branch1.setIdBranch(0L);
        branch1.setName("name");
        branch1.setWard("ward");
        branch1.setDistrict("district");
        branch1.setCity("city");
        final Optional<Branch> branch = Optional.of(branch1);
        when(mockBranchRepository.findById(0L)).thenReturn(branch);

        // Configure OrdersRepository.save(...).
        final Orders orders = new Orders();
        orders.setIdOrders(0L);
        orders.setTotal(0.0);
        orders.setStatus("status");
        final Customer customer4 = new Customer();
        customer4.setIdCustomer(0L);
        orders.setCustomer(customer4);
        final Branch branch2 = new Branch();
        branch2.setIdBranch(0L);
        orders.setBranch(branch2);
        orders.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders.setIsDelete(0);
        orders.setReceiverName("name");
        orders.setReceiverPhone("receiverPhone");
        orders.setShippingAddress("shippingAddress");
        orders.setPaymentMethod("paymentMethod");
        orders.setNote("note");
        final Voucher voucher1 = new Voucher();
        voucher1.setIdVoucher(0L);
        voucher1.setDiscount(0.0);
        orders.setVoucher(voucher1);
        when(mockOrdersRepository.save(any(Orders.class))).thenReturn(orders);

        // Run the test
        final String result = ordersServiceUnderTest.checkout(request, 0L);

        // Verify the results
        assertThat(result).isEqualTo("result");
        verify(mockAvailableVoucherRepository).save(any(AvailableVoucher.class));

        // Confirm OrderDetailsRepository.saveAll(...).
        final OrderDetails orderDetails = new OrderDetails();
        orderDetails.setQuantity(0);
        orderDetails.setPrice(0.0);
        final Product product2 = new Product();
        product2.setIdProduct(0L);
        product2.setName("name");
        product2.setPrice(0.0);
        product2.setImageUrl("imageUrl");
        orderDetails.setProduct(product2);
        final Orders orders1 = new Orders();
        orders1.setIdOrders(0L);
        orders1.setTotal(0.0);
        orders1.setStatus("status");
        final Customer customer5 = new Customer();
        customer5.setIdCustomer(0L);
        orders1.setCustomer(customer5);
        final Branch branch3 = new Branch();
        branch3.setIdBranch(0L);
        orders1.setBranch(branch3);
        orders1.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders1.setIsDelete(0);
        orders1.setReceiverName("name");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher2 = new Voucher();
        voucher2.setIdVoucher(0L);
        voucher2.setDiscount(0.0);
        orders1.setVoucher(voucher2);
        orderDetails.setOrders(orders1);
        orderDetails.setIsDelete(0);
        final List<OrderDetails> entities = List.of(orderDetails);
        verify(mockOrderDetailsRepository).saveAll(entities);

        // Confirm StockRepository.saveAll(...).
        final Stock stock1 = new Stock();
        stock1.setStockQuantity(0);
        final Product product3 = new Product();
        product3.setIdProduct(0L);
        product3.setName("name");
        product3.setPrice(0.0);
        product3.setImageUrl("imageUrl");
        stock1.setProduct(product3);
        final Collection<Stock> entities1 = List.of(stock1);
        verify(mockStockRepository).saveAll(entities1);

        // Confirm CartItemsRepository.deleteAll(...).
        final CartItems cartItems2 = new CartItems();
        cartItems2.setQuantity(0);
        final Product product4 = new Product();
        product4.setIdProduct(0L);
        product4.setName("name");
        product4.setPrice(0.0);
        product4.setImageUrl("imageUrl");
        cartItems2.setProduct(product4);
        final List<CartItems> entities2 = List.of(cartItems2);
        verify(mockCartItemsRepository).deleteAll(entities2);
    }

    @Test
    void testCheckout_CartsRepositoryReturnsNull() {
        // Setup
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(0L);
        request.setIdVoucher(0L);
        request.setIdBranch(0L);
        request.setPaymentMethod("paymentMethod");
        request.setNote("note");

        when(mockCartsRepository.findByCustomer_IdCustomer(0L)).thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> ordersServiceUnderTest.checkout(request, 0L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCheckout_CartItemsRepositoryFindByCart_IdCartsReturnsNoItems() {
        // Setup
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(0L);
        request.setIdVoucher(0L);
        request.setIdBranch(0L);
        request.setPaymentMethod("paymentMethod");
        request.setNote("note");

        // Configure CartsRepository.findByCustomer_IdCustomer(...).
        final Carts carts = new Carts();
        carts.setIdCarts(0L);
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        customer.setName("name");
        customer.setAge(0);
        carts.setCustomer(customer);
        when(mockCartsRepository.findByCustomer_IdCustomer(0L)).thenReturn(carts);

        when(mockCartItemsRepository.findByCart_IdCarts(0L)).thenReturn(Collections.emptyList());

        // Run the test
        assertThatThrownBy(() -> ordersServiceUnderTest.checkout(request, 0L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCheckout_StockRepositoryFindByProduct_IdProductInAndBranch_IdBranchReturnsNoItems() {
        // Setup
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(0L);
        request.setIdVoucher(0L);
        request.setIdBranch(0L);
        request.setPaymentMethod("paymentMethod");
        request.setNote("note");

        // Configure CartsRepository.findByCustomer_IdCustomer(...).
        final Carts carts = new Carts();
        carts.setIdCarts(0L);
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        customer.setName("name");
        customer.setAge(0);
        carts.setCustomer(customer);
        when(mockCartsRepository.findByCustomer_IdCustomer(0L)).thenReturn(carts);

        // Configure CartItemsRepository.findByCart_IdCarts(...).
        final CartItems cartItems1 = new CartItems();
        cartItems1.setQuantity(0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        cartItems1.setProduct(product);
        final List<CartItems> cartItems = List.of(cartItems1);
        when(mockCartItemsRepository.findByCart_IdCarts(0L)).thenReturn(cartItems);

        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L))
                .thenReturn(Collections.emptyList());

        // Configure AddressRepository.findById(...).
        final Address address1 = new Address();
        address1.setHouseNumber("houseNumber");
        address1.setWard("ward");
        address1.setDistrict("district");
        address1.setCity("city");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        address1.setCustomer(customer1);
        address1.setName("name");
        address1.setSdt("receiverPhone");
        final Optional<Address> address = Optional.of(address1);
        when(mockAddressRepository.findById(0L)).thenReturn(address);

        // Configure CustomerRepository.findById(...).
        final Customer customer3 = new Customer();
        customer3.setIdCustomer(0L);
        customer3.setName("name");
        customer3.setAge(0);
        customer3.setEmail("email");
        customer3.setPhone("phone");
        final Optional<Customer> customer2 = Optional.of(customer3);
        when(mockCustomerRepository.findById(0L)).thenReturn(customer2);

        // Configure AvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(...).
        final AvailableVoucher availableVoucher = new AvailableVoucher();
        availableVoucher.setIdAvailableVoucher(0L);
        availableVoucher.setQuantity(0);
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        voucher.setDiscount(0.0);
        availableVoucher.setVoucher(voucher);
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L))
                .thenReturn(availableVoucher);

        // Configure BranchRepository.findById(...).
        final Branch branch1 = new Branch();
        branch1.setIdBranch(0L);
        branch1.setName("name");
        branch1.setWard("ward");
        branch1.setDistrict("district");
        branch1.setCity("city");
        final Optional<Branch> branch = Optional.of(branch1);
        when(mockBranchRepository.findById(0L)).thenReturn(branch);

        // Configure OrdersRepository.save(...).
        final Orders orders = new Orders();
        orders.setIdOrders(0L);
        orders.setTotal(0.0);
        orders.setStatus("status");
        final Customer customer4 = new Customer();
        customer4.setIdCustomer(0L);
        orders.setCustomer(customer4);
        final Branch branch2 = new Branch();
        branch2.setIdBranch(0L);
        orders.setBranch(branch2);
        orders.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders.setIsDelete(0);
        orders.setReceiverName("name");
        orders.setReceiverPhone("receiverPhone");
        orders.setShippingAddress("shippingAddress");
        orders.setPaymentMethod("paymentMethod");
        orders.setNote("note");
        final Voucher voucher1 = new Voucher();
        voucher1.setIdVoucher(0L);
        voucher1.setDiscount(0.0);
        orders.setVoucher(voucher1);
        when(mockOrdersRepository.save(any(Orders.class))).thenReturn(orders);

        // Run the test
        final String result = ordersServiceUnderTest.checkout(request, 0L);

        // Verify the results
        assertThat(result).isEqualTo("result");
        verify(mockAvailableVoucherRepository).save(any(AvailableVoucher.class));

        // Confirm OrderDetailsRepository.saveAll(...).
        final OrderDetails orderDetails = new OrderDetails();
        orderDetails.setQuantity(0);
        orderDetails.setPrice(0.0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        orderDetails.setProduct(product1);
        final Orders orders1 = new Orders();
        orders1.setIdOrders(0L);
        orders1.setTotal(0.0);
        orders1.setStatus("status");
        final Customer customer5 = new Customer();
        customer5.setIdCustomer(0L);
        orders1.setCustomer(customer5);
        final Branch branch3 = new Branch();
        branch3.setIdBranch(0L);
        orders1.setBranch(branch3);
        orders1.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders1.setIsDelete(0);
        orders1.setReceiverName("name");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher2 = new Voucher();
        voucher2.setIdVoucher(0L);
        voucher2.setDiscount(0.0);
        orders1.setVoucher(voucher2);
        orderDetails.setOrders(orders1);
        orderDetails.setIsDelete(0);
        final List<OrderDetails> entities = List.of(orderDetails);
        verify(mockOrderDetailsRepository).saveAll(entities);

        // Confirm StockRepository.saveAll(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product2 = new Product();
        product2.setIdProduct(0L);
        product2.setName("name");
        product2.setPrice(0.0);
        product2.setImageUrl("imageUrl");
        stock.setProduct(product2);
        final Collection<Stock> entities1 = List.of(stock);
        verify(mockStockRepository).saveAll(entities1);

        // Confirm CartItemsRepository.deleteAll(...).
        final CartItems cartItems2 = new CartItems();
        cartItems2.setQuantity(0);
        final Product product3 = new Product();
        product3.setIdProduct(0L);
        product3.setName("name");
        product3.setPrice(0.0);
        product3.setImageUrl("imageUrl");
        cartItems2.setProduct(product3);
        final List<CartItems> entities2 = List.of(cartItems2);
        verify(mockCartItemsRepository).deleteAll(entities2);
    }

    @Test
    void testCheckout_AddressRepositoryReturnsAbsent() {
        // Setup
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(0L);
        request.setIdVoucher(0L);
        request.setIdBranch(0L);
        request.setPaymentMethod("paymentMethod");
        request.setNote("note");

        // Configure CartsRepository.findByCustomer_IdCustomer(...).
        final Carts carts = new Carts();
        carts.setIdCarts(0L);
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        customer.setName("name");
        customer.setAge(0);
        carts.setCustomer(customer);
        when(mockCartsRepository.findByCustomer_IdCustomer(0L)).thenReturn(carts);

        // Configure CartItemsRepository.findByCart_IdCarts(...).
        final CartItems cartItems1 = new CartItems();
        cartItems1.setQuantity(0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        cartItems1.setProduct(product);
        final List<CartItems> cartItems = List.of(cartItems1);
        when(mockCartItemsRepository.findByCart_IdCarts(0L)).thenReturn(cartItems);

        // Configure StockRepository.findByProduct_IdProductInAndBranch_IdBranch(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        stock.setProduct(product1);
        final List<Stock> stocks = List.of(stock);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L)).thenReturn(stocks);

        when(mockAddressRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> ordersServiceUnderTest.checkout(request, 0L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCheckout_CustomerRepositoryReturnsAbsent() {
        // Setup
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(0L);
        request.setIdVoucher(0L);
        request.setIdBranch(0L);
        request.setPaymentMethod("paymentMethod");
        request.setNote("note");

        // Configure CartsRepository.findByCustomer_IdCustomer(...).
        final Carts carts = new Carts();
        carts.setIdCarts(0L);
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        customer.setName("name");
        customer.setAge(0);
        carts.setCustomer(customer);
        when(mockCartsRepository.findByCustomer_IdCustomer(0L)).thenReturn(carts);

        // Configure CartItemsRepository.findByCart_IdCarts(...).
        final CartItems cartItems1 = new CartItems();
        cartItems1.setQuantity(0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        cartItems1.setProduct(product);
        final List<CartItems> cartItems = List.of(cartItems1);
        when(mockCartItemsRepository.findByCart_IdCarts(0L)).thenReturn(cartItems);

        // Configure StockRepository.findByProduct_IdProductInAndBranch_IdBranch(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        stock.setProduct(product1);
        final List<Stock> stocks = List.of(stock);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L)).thenReturn(stocks);

        // Configure AddressRepository.findById(...).
        final Address address1 = new Address();
        address1.setHouseNumber("houseNumber");
        address1.setWard("ward");
        address1.setDistrict("district");
        address1.setCity("city");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        address1.setCustomer(customer1);
        address1.setName("name");
        address1.setSdt("receiverPhone");
        final Optional<Address> address = Optional.of(address1);
        when(mockAddressRepository.findById(0L)).thenReturn(address);

        when(mockCustomerRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> ordersServiceUnderTest.checkout(request, 0L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCheckout_AvailableVoucherRepositoryFindByCustomer_IdCustomerAndVoucher_IdVoucherReturnsNull() {
        // Setup
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(0L);
        request.setIdVoucher(0L);
        request.setIdBranch(0L);
        request.setPaymentMethod("paymentMethod");
        request.setNote("note");

        // Configure CartsRepository.findByCustomer_IdCustomer(...).
        final Carts carts = new Carts();
        carts.setIdCarts(0L);
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        customer.setName("name");
        customer.setAge(0);
        carts.setCustomer(customer);
        when(mockCartsRepository.findByCustomer_IdCustomer(0L)).thenReturn(carts);

        // Configure CartItemsRepository.findByCart_IdCarts(...).
        final CartItems cartItems1 = new CartItems();
        cartItems1.setQuantity(0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        cartItems1.setProduct(product);
        final List<CartItems> cartItems = List.of(cartItems1);
        when(mockCartItemsRepository.findByCart_IdCarts(0L)).thenReturn(cartItems);

        // Configure StockRepository.findByProduct_IdProductInAndBranch_IdBranch(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        stock.setProduct(product1);
        final List<Stock> stocks = List.of(stock);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L)).thenReturn(stocks);

        // Configure AddressRepository.findById(...).
        final Address address1 = new Address();
        address1.setHouseNumber("houseNumber");
        address1.setWard("ward");
        address1.setDistrict("district");
        address1.setCity("city");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        address1.setCustomer(customer1);
        address1.setName("name");
        address1.setSdt("receiverPhone");
        final Optional<Address> address = Optional.of(address1);
        when(mockAddressRepository.findById(0L)).thenReturn(address);

        // Configure CustomerRepository.findById(...).
        final Customer customer3 = new Customer();
        customer3.setIdCustomer(0L);
        customer3.setName("name");
        customer3.setAge(0);
        customer3.setEmail("email");
        customer3.setPhone("phone");
        final Optional<Customer> customer2 = Optional.of(customer3);
        when(mockCustomerRepository.findById(0L)).thenReturn(customer2);

        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L)).thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> ordersServiceUnderTest.checkout(request, 0L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCheckout_BranchRepositoryReturnsAbsent() {
        // Setup
        final CheckoutRequest request = new CheckoutRequest();
        request.setIdAddress(0L);
        request.setIdVoucher(0L);
        request.setIdBranch(0L);
        request.setPaymentMethod("paymentMethod");
        request.setNote("note");

        // Configure CartsRepository.findByCustomer_IdCustomer(...).
        final Carts carts = new Carts();
        carts.setIdCarts(0L);
        final Customer customer = new Customer();
        customer.setIdCustomer(0L);
        customer.setName("name");
        customer.setAge(0);
        carts.setCustomer(customer);
        when(mockCartsRepository.findByCustomer_IdCustomer(0L)).thenReturn(carts);

        // Configure CartItemsRepository.findByCart_IdCarts(...).
        final CartItems cartItems1 = new CartItems();
        cartItems1.setQuantity(0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        cartItems1.setProduct(product);
        final List<CartItems> cartItems = List.of(cartItems1);
        when(mockCartItemsRepository.findByCart_IdCarts(0L)).thenReturn(cartItems);

        // Configure StockRepository.findByProduct_IdProductInAndBranch_IdBranch(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        stock.setProduct(product1);
        final List<Stock> stocks = List.of(stock);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L)).thenReturn(stocks);

        // Configure AddressRepository.findById(...).
        final Address address1 = new Address();
        address1.setHouseNumber("houseNumber");
        address1.setWard("ward");
        address1.setDistrict("district");
        address1.setCity("city");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        address1.setCustomer(customer1);
        address1.setName("name");
        address1.setSdt("receiverPhone");
        final Optional<Address> address = Optional.of(address1);
        when(mockAddressRepository.findById(0L)).thenReturn(address);

        // Configure CustomerRepository.findById(...).
        final Customer customer3 = new Customer();
        customer3.setIdCustomer(0L);
        customer3.setName("name");
        customer3.setAge(0);
        customer3.setEmail("email");
        customer3.setPhone("phone");
        final Optional<Customer> customer2 = Optional.of(customer3);
        when(mockCustomerRepository.findById(0L)).thenReturn(customer2);

        // Configure AvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(...).
        final AvailableVoucher availableVoucher = new AvailableVoucher();
        availableVoucher.setIdAvailableVoucher(0L);
        availableVoucher.setQuantity(0);
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        voucher.setDiscount(0.0);
        availableVoucher.setVoucher(voucher);
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L))
                .thenReturn(availableVoucher);

        when(mockBranchRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> ordersServiceUnderTest.checkout(request, 0L)).isInstanceOf(RuntimeException.class);
        verify(mockAvailableVoucherRepository).save(any(AvailableVoucher.class));
    }

    @Test
    void testGetOrderHistory() {
        // Setup
        final OrderResponse orderResponse = new OrderResponse();
        orderResponse.setIdOrders(0L);
        orderResponse.setReceiverName("name");
        orderResponse.setReceiverPhone("receiverPhone");
        orderResponse.setShippingAddress("shippingAddress");
        orderResponse.setPaymentMethod("paymentMethod");
        orderResponse.setStatus("status");
        orderResponse.setTotal(0.0);
        orderResponse.setNote("note");
        orderResponse.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        final OrderResponse.OrderDetailItem orderDetailItem = new OrderResponse.OrderDetailItem();
        orderDetailItem.setProductName("name");
        orderDetailItem.setQuantity(0);
        orderDetailItem.setPrice(0.0);
        orderDetailItem.setImageUrl("imageUrl");
        orderResponse.setItems(List.of(orderDetailItem));
        final List<OrderResponse> expectedResult = List.of(orderResponse);

        // Configure OrdersRepository.findByCustomer_IdCustomerAndStatusAndIsDeleteOrderByCreatedAtDesc(...).
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
        orders1.setIsDelete(0);
        orders1.setReceiverName("name");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        voucher.setDiscount(0.0);
        orders1.setVoucher(voucher);
        final List<Orders> orders = List.of(orders1);
        when(mockOrdersRepository.findByCustomer_IdCustomerAndStatusAndIsDeleteOrderByCreatedAtDesc(0L, "status",
                0)).thenReturn(orders);

        // Configure OrderDetailsRepository.findByOrders_IdOrders(...).
        final OrderDetails orderDetails1 = new OrderDetails();
        orderDetails1.setQuantity(0);
        orderDetails1.setPrice(0.0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        orderDetails1.setProduct(product);
        final Orders orders2 = new Orders();
        orders2.setIdOrders(0L);
        orders2.setTotal(0.0);
        orders2.setStatus("status");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        orders2.setCustomer(customer1);
        final Branch branch1 = new Branch();
        branch1.setIdBranch(0L);
        orders2.setBranch(branch1);
        orders2.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders2.setIsDelete(0);
        orders2.setReceiverName("name");
        orders2.setReceiverPhone("receiverPhone");
        orders2.setShippingAddress("shippingAddress");
        orders2.setPaymentMethod("paymentMethod");
        orders2.setNote("note");
        final Voucher voucher1 = new Voucher();
        voucher1.setIdVoucher(0L);
        voucher1.setDiscount(0.0);
        orders2.setVoucher(voucher1);
        orderDetails1.setOrders(orders2);
        orderDetails1.setIsDelete(0);
        final List<OrderDetails> orderDetails = List.of(orderDetails1);
        when(mockOrderDetailsRepository.findByOrders_IdOrders(0L)).thenReturn(orderDetails);

        // Run the test
        final List<OrderResponse> result = ordersServiceUnderTest.getOrderHistory(0L, "status");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetOrderHistory_OrdersRepositoryReturnsNoItems() {
        // Setup
        when(mockOrdersRepository.findByCustomer_IdCustomerAndStatusAndIsDeleteOrderByCreatedAtDesc(0L, "status",
                0)).thenReturn(Collections.emptyList());

        // Run the test
        final List<OrderResponse> result = ordersServiceUnderTest.getOrderHistory(0L, "status");

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetOrderHistory_OrderDetailsRepositoryReturnsNoItems() {
        // Setup
        final OrderResponse orderResponse = new OrderResponse();
        orderResponse.setIdOrders(0L);
        orderResponse.setReceiverName("name");
        orderResponse.setReceiverPhone("receiverPhone");
        orderResponse.setShippingAddress("shippingAddress");
        orderResponse.setPaymentMethod("paymentMethod");
        orderResponse.setStatus("status");
        orderResponse.setTotal(0.0);
        orderResponse.setNote("note");
        orderResponse.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        final OrderResponse.OrderDetailItem orderDetailItem = new OrderResponse.OrderDetailItem();
        orderDetailItem.setProductName("name");
        orderDetailItem.setQuantity(0);
        orderDetailItem.setPrice(0.0);
        orderDetailItem.setImageUrl("imageUrl");
        orderResponse.setItems(List.of(orderDetailItem));
        final List<OrderResponse> expectedResult = List.of(orderResponse);

        // Configure OrdersRepository.findByCustomer_IdCustomerAndStatusAndIsDeleteOrderByCreatedAtDesc(...).
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
        orders1.setIsDelete(0);
        orders1.setReceiverName("name");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        voucher.setDiscount(0.0);
        orders1.setVoucher(voucher);
        final List<Orders> orders = List.of(orders1);
        when(mockOrdersRepository.findByCustomer_IdCustomerAndStatusAndIsDeleteOrderByCreatedAtDesc(0L, "status",
                0)).thenReturn(orders);

        when(mockOrderDetailsRepository.findByOrders_IdOrders(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<OrderResponse> result = ordersServiceUnderTest.getOrderHistory(0L, "status");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCancelOrder() {
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
        orders1.setIsDelete(0);
        orders1.setReceiverName("name");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        voucher.setDiscount(0.0);
        orders1.setVoucher(voucher);
        final Optional<Orders> orders = Optional.of(orders1);
        when(mockOrdersRepository.findById(0L)).thenReturn(orders);

        // Configure AvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(...).
        final AvailableVoucher availableVoucher = new AvailableVoucher();
        availableVoucher.setIdAvailableVoucher(0L);
        availableVoucher.setQuantity(0);
        final Voucher voucher1 = new Voucher();
        voucher1.setIdVoucher(0L);
        voucher1.setDiscount(0.0);
        availableVoucher.setVoucher(voucher1);
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L))
                .thenReturn(availableVoucher);

        // Configure OrderDetailsRepository.findByOrders_IdOrders(...).
        final OrderDetails orderDetails1 = new OrderDetails();
        orderDetails1.setQuantity(0);
        orderDetails1.setPrice(0.0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        orderDetails1.setProduct(product);
        final Orders orders2 = new Orders();
        orders2.setIdOrders(0L);
        orders2.setTotal(0.0);
        orders2.setStatus("status");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        orders2.setCustomer(customer1);
        final Branch branch1 = new Branch();
        branch1.setIdBranch(0L);
        orders2.setBranch(branch1);
        orders2.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders2.setIsDelete(0);
        orders2.setReceiverName("name");
        orders2.setReceiverPhone("receiverPhone");
        orders2.setShippingAddress("shippingAddress");
        orders2.setPaymentMethod("paymentMethod");
        orders2.setNote("note");
        final Voucher voucher2 = new Voucher();
        voucher2.setIdVoucher(0L);
        voucher2.setDiscount(0.0);
        orders2.setVoucher(voucher2);
        orderDetails1.setOrders(orders2);
        orderDetails1.setIsDelete(0);
        final List<OrderDetails> orderDetails = List.of(orderDetails1);
        when(mockOrderDetailsRepository.findByOrders_IdOrders(0L)).thenReturn(orderDetails);

        // Configure StockRepository.findByProduct_IdProductInAndBranch_IdBranch(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        stock.setProduct(product1);
        final List<Stock> stocks = List.of(stock);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L)).thenReturn(stocks);

        // Run the test
        final String result = ordersServiceUnderTest.cancelOrder(0L, 0L);

        // Verify the results
        assertThat(result).isEqualTo("result");
        verify(mockOrdersRepository).save(any(Orders.class));
        verify(mockAvailableVoucherRepository).save(any(AvailableVoucher.class));

        // Confirm StockRepository.saveAll(...).
        final Stock stock1 = new Stock();
        stock1.setStockQuantity(0);
        final Product product2 = new Product();
        product2.setIdProduct(0L);
        product2.setName("name");
        product2.setPrice(0.0);
        product2.setImageUrl("imageUrl");
        stock1.setProduct(product2);
        final Collection<Stock> entities = List.of(stock1);
        verify(mockStockRepository).saveAll(entities);
    }

    @Test
    void testCancelOrder_OrdersRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockOrdersRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> ordersServiceUnderTest.cancelOrder(0L, 0L)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testCancelOrder_AvailableVoucherRepositoryFindByCustomer_IdCustomerAndVoucher_IdVoucherReturnsNull() {
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
        orders1.setIsDelete(0);
        orders1.setReceiverName("name");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        voucher.setDiscount(0.0);
        orders1.setVoucher(voucher);
        final Optional<Orders> orders = Optional.of(orders1);
        when(mockOrdersRepository.findById(0L)).thenReturn(orders);

        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L)).thenReturn(null);

        // Configure OrderDetailsRepository.findByOrders_IdOrders(...).
        final OrderDetails orderDetails1 = new OrderDetails();
        orderDetails1.setQuantity(0);
        orderDetails1.setPrice(0.0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        orderDetails1.setProduct(product);
        final Orders orders2 = new Orders();
        orders2.setIdOrders(0L);
        orders2.setTotal(0.0);
        orders2.setStatus("status");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        orders2.setCustomer(customer1);
        final Branch branch1 = new Branch();
        branch1.setIdBranch(0L);
        orders2.setBranch(branch1);
        orders2.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders2.setIsDelete(0);
        orders2.setReceiverName("name");
        orders2.setReceiverPhone("receiverPhone");
        orders2.setShippingAddress("shippingAddress");
        orders2.setPaymentMethod("paymentMethod");
        orders2.setNote("note");
        final Voucher voucher1 = new Voucher();
        voucher1.setIdVoucher(0L);
        voucher1.setDiscount(0.0);
        orders2.setVoucher(voucher1);
        orderDetails1.setOrders(orders2);
        orderDetails1.setIsDelete(0);
        final List<OrderDetails> orderDetails = List.of(orderDetails1);
        when(mockOrderDetailsRepository.findByOrders_IdOrders(0L)).thenReturn(orderDetails);

        // Configure StockRepository.findByProduct_IdProductInAndBranch_IdBranch(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        stock.setProduct(product1);
        final List<Stock> stocks = List.of(stock);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L)).thenReturn(stocks);

        // Run the test
        final String result = ordersServiceUnderTest.cancelOrder(0L, 0L);

        // Verify the results
        assertThat(result).isEqualTo("result");
        verify(mockOrdersRepository).save(any(Orders.class));

        // Confirm StockRepository.saveAll(...).
        final Stock stock1 = new Stock();
        stock1.setStockQuantity(0);
        final Product product2 = new Product();
        product2.setIdProduct(0L);
        product2.setName("name");
        product2.setPrice(0.0);
        product2.setImageUrl("imageUrl");
        stock1.setProduct(product2);
        final Collection<Stock> entities = List.of(stock1);
        verify(mockStockRepository).saveAll(entities);
    }

    @Test
    void testCancelOrder_OrderDetailsRepositoryReturnsNoItems() {
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
        orders1.setIsDelete(0);
        orders1.setReceiverName("name");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        voucher.setDiscount(0.0);
        orders1.setVoucher(voucher);
        final Optional<Orders> orders = Optional.of(orders1);
        when(mockOrdersRepository.findById(0L)).thenReturn(orders);

        // Configure AvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(...).
        final AvailableVoucher availableVoucher = new AvailableVoucher();
        availableVoucher.setIdAvailableVoucher(0L);
        availableVoucher.setQuantity(0);
        final Voucher voucher1 = new Voucher();
        voucher1.setIdVoucher(0L);
        voucher1.setDiscount(0.0);
        availableVoucher.setVoucher(voucher1);
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L))
                .thenReturn(availableVoucher);

        when(mockOrderDetailsRepository.findByOrders_IdOrders(0L)).thenReturn(Collections.emptyList());

        // Configure StockRepository.findByProduct_IdProductInAndBranch_IdBranch(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        stock.setProduct(product);
        final List<Stock> stocks = List.of(stock);
        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L)).thenReturn(stocks);

        // Run the test
        final String result = ordersServiceUnderTest.cancelOrder(0L, 0L);

        // Verify the results
        assertThat(result).isEqualTo("result");
        verify(mockOrdersRepository).save(any(Orders.class));
        verify(mockAvailableVoucherRepository).save(any(AvailableVoucher.class));

        // Confirm StockRepository.saveAll(...).
        final Stock stock1 = new Stock();
        stock1.setStockQuantity(0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        stock1.setProduct(product1);
        final Collection<Stock> entities = List.of(stock1);
        verify(mockStockRepository).saveAll(entities);
    }

    @Test
    void testCancelOrder_StockRepositoryFindByProduct_IdProductInAndBranch_IdBranchReturnsNoItems() {
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
        orders1.setIsDelete(0);
        orders1.setReceiverName("name");
        orders1.setReceiverPhone("receiverPhone");
        orders1.setShippingAddress("shippingAddress");
        orders1.setPaymentMethod("paymentMethod");
        orders1.setNote("note");
        final Voucher voucher = new Voucher();
        voucher.setIdVoucher(0L);
        voucher.setDiscount(0.0);
        orders1.setVoucher(voucher);
        final Optional<Orders> orders = Optional.of(orders1);
        when(mockOrdersRepository.findById(0L)).thenReturn(orders);

        // Configure AvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(...).
        final AvailableVoucher availableVoucher = new AvailableVoucher();
        availableVoucher.setIdAvailableVoucher(0L);
        availableVoucher.setQuantity(0);
        final Voucher voucher1 = new Voucher();
        voucher1.setIdVoucher(0L);
        voucher1.setDiscount(0.0);
        availableVoucher.setVoucher(voucher1);
        when(mockAvailableVoucherRepository.findByCustomer_IdCustomerAndVoucher_IdVoucher(0L, 0L))
                .thenReturn(availableVoucher);

        // Configure OrderDetailsRepository.findByOrders_IdOrders(...).
        final OrderDetails orderDetails1 = new OrderDetails();
        orderDetails1.setQuantity(0);
        orderDetails1.setPrice(0.0);
        final Product product = new Product();
        product.setIdProduct(0L);
        product.setName("name");
        product.setPrice(0.0);
        product.setImageUrl("imageUrl");
        orderDetails1.setProduct(product);
        final Orders orders2 = new Orders();
        orders2.setIdOrders(0L);
        orders2.setTotal(0.0);
        orders2.setStatus("status");
        final Customer customer1 = new Customer();
        customer1.setIdCustomer(0L);
        orders2.setCustomer(customer1);
        final Branch branch1 = new Branch();
        branch1.setIdBranch(0L);
        orders2.setBranch(branch1);
        orders2.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        orders2.setIsDelete(0);
        orders2.setReceiverName("name");
        orders2.setReceiverPhone("receiverPhone");
        orders2.setShippingAddress("shippingAddress");
        orders2.setPaymentMethod("paymentMethod");
        orders2.setNote("note");
        final Voucher voucher2 = new Voucher();
        voucher2.setIdVoucher(0L);
        voucher2.setDiscount(0.0);
        orders2.setVoucher(voucher2);
        orderDetails1.setOrders(orders2);
        orderDetails1.setIsDelete(0);
        final List<OrderDetails> orderDetails = List.of(orderDetails1);
        when(mockOrderDetailsRepository.findByOrders_IdOrders(0L)).thenReturn(orderDetails);

        when(mockStockRepository.findByProduct_IdProductInAndBranch_IdBranch(List.of(0L), 0L))
                .thenReturn(Collections.emptyList());

        // Run the test
        final String result = ordersServiceUnderTest.cancelOrder(0L, 0L);

        // Verify the results
        assertThat(result).isEqualTo("result");
        verify(mockOrdersRepository).save(any(Orders.class));
        verify(mockAvailableVoucherRepository).save(any(AvailableVoucher.class));

        // Confirm StockRepository.saveAll(...).
        final Stock stock = new Stock();
        stock.setStockQuantity(0);
        final Product product1 = new Product();
        product1.setIdProduct(0L);
        product1.setName("name");
        product1.setPrice(0.0);
        product1.setImageUrl("imageUrl");
        stock.setProduct(product1);
        final Collection<Stock> entities = List.of(stock);
        verify(mockStockRepository).saveAll(entities);
    }
}
