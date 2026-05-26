package com.n8.emarket.service;

import com.n8.emarket.dto.OrderResponse;
import com.n8.emarket.entity.AvailableVoucher;
import com.n8.emarket.entity.OrderDetails;
import com.n8.emarket.entity.Orders;
import com.n8.emarket.repository.AvailableVoucherRepository;
import com.n8.emarket.repository.OrderDetailsRepository;
import com.n8.emarket.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalesService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private AvailableVoucherRepository availableVoucherRepository;

    // ham lay danh sanh don loc theo trang thai
    public List<OrderResponse> getOrdersForStaff(Long idBranch, String status) {
        List<Orders> ordersList;

        if (status != null && !status.trim().isEmpty()) {
            ordersList = ordersRepository.findByBranch_IdBranchAndStatusAndIsDeleteOrderByCreatedAtDesc(
                    idBranch,
                    status.trim().toUpperCase(),
                    0
            );
        }

        else {
            ordersList = ordersRepository.findByBranch_IdBranchAndIsDeleteOrderByCreatedAtDesc(idBranch, 0);
        }

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
                itemDto.setPrice(detail.getProduct().getPrice());
                itemDtos.add(itemDto);
            }
            orderDto.setItems(itemDtos);
            responseList.add(orderDto);
        }
        return responseList;
    }

    // ham cap nhap trang thai don hang
    @Transactional
    public String updateOrderStatus(Long idOrder, String newStatus, Long idBranch) {
        Orders order = ordersRepository.findById(idOrder)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        if (!order.getBranch().getIdBranch().equals(idBranch)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên đơn hàng của chi nhánh khác!");
        }

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Đơn hàng này đã bị hủy từ trước, không thể thao tác thêm!");
        }

        if ("CANCELLED".equalsIgnoreCase(newStatus.trim())) {

            if (order.getVoucher() != null) {
                AvailableVoucher myVoucher = availableVoucherRepository
                        .findByCustomer_IdCustomerAndVoucher_IdVoucher(order.getCustomer().getIdCustomer(), order.getVoucher().getIdVoucher());
                if (myVoucher != null) {
                    myVoucher.setQuantity(myVoucher.getQuantity() + 1);
                    availableVoucherRepository.save(myVoucher);
                }
            }

        }

        order.setStatus(newStatus.toUpperCase());
        ordersRepository.save(order);

        return "Cập nhật trạng thái đơn hàng số " + idOrder + " thành " + newStatus.toUpperCase() + " thành công!";
    }
}