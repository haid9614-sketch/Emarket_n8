package com.n8.emarket.service;
import com.n8.emarket.dto.CheckoutRequest;
import com.n8.emarket.entity.*;
import com.n8.emarket.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrdersService {

    @Autowired private OrdersRepository ordersRepository;
    @Autowired private OrderDetailsRepository orderDetailsRepository;
    @Autowired private CartsRepository cartsRepository;
    @Autowired private CartItemsRepository cartItemsRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private AvailableVoucherRepository availableVoucherRepository;

    @Transactional
    public String checkout(CheckoutRequest request) {

        Carts cart = cartsRepository.findByCustomer_IdCustomer(request.getIdCustomer());
        if (cart == null) throw new RuntimeException("Giỏ hàng của bạn đang trống rỗng!");

        List<CartItems> cartItemsList = cartItemsRepository.findByCart_IdCarts(cart.getIdCarts());
        if (cartItemsList.isEmpty()) throw new RuntimeException("Giỏ hàng không có sản phẩm nào để thanh toán!");

        List<Long> productIds = cartItemsList.stream().map(item -> item.getProduct().getIdProduct()).toList();
        List<Stock> stocks = stockRepository.findByProduct_IdProductIn(productIds);

        Map<Long, Stock> stockMap = new HashMap<>();
        for (Stock stock : stocks) {
            stockMap.put(stock.getProduct().getIdProduct(), stock);
        }

        for (CartItems item : cartItemsList) {
            Stock stock = stockMap.get(item.getProduct().getIdProduct());
            if (stock == null || stock.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm [" + item.getProduct().getName() + "] đã hết hoặc không đủ hàng trong kho!");
            }
        }

        Address address = addressRepository.findById(request.getIdAddress())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng trong hệ thống!"));

        if (!address.getCustomer().getIdCustomer().equals(request.getIdCustomer())) {
            throw new RuntimeException("Địa chỉ giao hàng không hợp lệ cho tài khoản này!");
        }

        Customer customer = customerRepository.findById(request.getIdCustomer())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng!"));

        Double totalPrice = 0.0;
        for (CartItems item : cartItemsList) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }

        Double finalPrice = totalPrice;

        if (request.getIdVoucher() != null) {

            AvailableVoucher myVoucher = availableVoucherRepository
                    .findByCustomer_IdCustomerAndVoucher_IdVoucher(request.getIdCustomer(), request.getIdVoucher());

            if (myVoucher == null || myVoucher.getQuantity() <= 0) {
                throw new RuntimeException("Bạn không sở hữu Voucher này hoặc đã dùng hết lượt!");
            }

            Double discountValue = myVoucher.getVoucher().getDiscount();
            finalPrice = finalPrice - discountValue;

            if (finalPrice < 0) {
                finalPrice = 0.0;
            }

            myVoucher.setQuantity(myVoucher.getQuantity() - 1);
            availableVoucherRepository.save(myVoucher);
        }

        Orders order = new Orders();
        order.setCustomer(customer);
        order.setReceiverName(customer.getName());
        order.setReceiverPhone(customer.getPhone());

        String fullShippingAddress = address.getHouseNumber() + ", " + address.getWard() + ", " + address.getDistrict() + ", " + address.getCity();
        order.setShippingAddress(fullShippingAddress);

        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setIsDelete(0);
        order.setTotal(finalPrice);

        order = ordersRepository.save(order);

        List<OrderDetails> orderDetailsList = new ArrayList<>();

        for (CartItems item : cartItemsList) {
            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setOrders(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setPrice(item.getProduct().getPrice());
            orderDetail.setIsDelete(0);
            orderDetailsList.add(orderDetail);

            Stock stock = stockMap.get(item.getProduct().getIdProduct());
            stock.setStockQuantity(stock.getStockQuantity() - item.getQuantity());
        }

        orderDetailsRepository.saveAll(orderDetailsList);
        stockRepository.saveAll(stockMap.values());

        cartItemsRepository.deleteAll(cartItemsList);

        return "Đặt hàng thành công! Tổng tiền phải trả: " + finalPrice + " VNĐ. Mã đơn: " + order.getIdOrders();
    }
}