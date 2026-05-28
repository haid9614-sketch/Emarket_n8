package com.n8.emarket.service;
import com.n8.emarket.dto.CheckoutRequest;
import com.n8.emarket.dto.OrderResponse;
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
    @Autowired private BranchRepository branchRepository;

    // ham thanh toan dat hang
    @Transactional
    public String checkout(CheckoutRequest request, Long idCustomer) {

        Carts cart = cartsRepository.findByCustomer_IdCustomer(idCustomer);

        if (cart == null) throw new RuntimeException("Giỏ hàng của bạn đang trống rỗng!");

        List<CartItems> cartItemsList = cartItemsRepository.findByCart_IdCarts(cart.getIdCarts());
        if (cartItemsList.isEmpty()) throw new RuntimeException("Giỏ hàng không có sản phẩm nào để thanh toán!");

        List<Long> productIds = cartItemsList.stream().map(item -> item.getProduct().getIdProduct()).toList();
        List<Stock> stocks = stockRepository.findByProduct_IdProductInAndBranch_IdBranch(productIds, request.getIdBranch());

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
        Orders order = new Orders();
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
            order.setVoucher(myVoucher.getVoucher());
            myVoucher.setQuantity(myVoucher.getQuantity() - 1);
            availableVoucherRepository.save(myVoucher);
        }

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
        Branch branch = branchRepository.findById(request.getIdBranch())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh này trong hệ thống!"));
        order.setBranch(branch);

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

    // ham xem don da dat
    public List<OrderResponse> getOrderHistory(Long idCustomer) {
        List<Orders> ordersList = ordersRepository.findByCustomer_IdCustomerAndIsDeleteOrderByCreatedAtDesc(idCustomer, 0);
        List<OrderResponse> responseList = new ArrayList<>();

        for (Orders order : ordersList) {
            OrderResponse orderDto = new OrderResponse();
            orderDto.setIdOrders(order.getIdOrders());
            orderDto.setReceiverName(order.getReceiverName());
            orderDto.setReceiverPhone(order.getReceiverPhone());
            orderDto.setShippingAddress(order.getShippingAddress());
            orderDto.setPaymentMethod(order.getPaymentMethod());
            orderDto.setStatus(order.getStatus());
            orderDto.setTotal(order.getTotal());
            orderDto.setNote(order.getNote());
            orderDto.setCreatedAt(order.getCreatedAt());

            List<OrderDetails> details = orderDetailsRepository.findByOrders_IdOrders(order.getIdOrders());
            List<OrderResponse.OrderDetailItem> itemDtos = new ArrayList<>();

            for (OrderDetails detail : details) {
                OrderResponse.OrderDetailItem itemDto = new OrderResponse.OrderDetailItem();
                itemDto.setProductName(detail.getProduct().getName());
                itemDto.setQuantity(detail.getQuantity());
                itemDto.setPrice(detail.getPrice());
                itemDtos.add(itemDto);
            }
            orderDto.setItems(itemDtos);
            responseList.add(orderDto);
        }
        return responseList;
    }

    // ham huy don hang
    @Transactional
    public String cancelOrder(Long idOrder, Long idCustomer) {
        Orders order = ordersRepository.findById(idOrder)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng này trong hệ thống!"));

        if (!order.getCustomer().getIdCustomer().equals(idCustomer)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng của tài khoản khác!");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Đơn hàng này đã được hệ thống xử lý hoặc đang giao, không thể hủy!");
        }

        order.setStatus("CANCELLED");
        ordersRepository.save(order);

        if (order.getVoucher() != null) {
            AvailableVoucher myVoucher = availableVoucherRepository
                    .findByCustomer_IdCustomerAndVoucher_IdVoucher(idCustomer, order.getVoucher().getIdVoucher());

            if (myVoucher != null) {
                myVoucher.setQuantity(myVoucher.getQuantity() + 1);
                availableVoucherRepository.save(myVoucher);
            }
        }

        List<OrderDetails> details = orderDetailsRepository.findByOrders_IdOrders(idOrder);
        List<Long> productIds = details.stream().map(d -> d.getProduct().getIdProduct()).toList();
        List<Stock> stocks = stockRepository.findByProduct_IdProductIn(productIds);

        Map<Long, Stock> stockMap = new HashMap<>();
        for (Stock stock : stocks) {
            stockMap.put(stock.getProduct().getIdProduct(), stock);
        }

        for (OrderDetails detail : details) {
            Stock stock = stockMap.get(detail.getProduct().getIdProduct());
            if (stock != null) {
                stock.setStockQuantity(stock.getStockQuantity() + detail.getQuantity());
            }
        }

        stockRepository.saveAll(stockMap.values());

        return "Hủy đơn hàng số " + idOrder + " thành công! Toàn bộ sản phẩm và Voucher đã được hoàn trả.";
    }

}