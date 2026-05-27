package com.n8.emarket.service;
import com.n8.emarket.dto.AddToCartRequest;
import com.n8.emarket.dto.CartItemsResponse;
import com.n8.emarket.dto.CartsResponse;
import com.n8.emarket.entity.*;
import com.n8.emarket.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartsService {
    @Autowired
    private CartsRepository cartsRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    // thêm sản phẩm vào giỏ
    @Transactional
    public String addToCart(AddToCartRequest request) {
        Stock stock = stockRepository.findByProduct_IdProductAndBranch_IdBranch(
                request.getIdProduct(),
                request.getIdBranch()
        );

        if (stock == null) {
            throw new RuntimeException("Sản phẩm này hiện không kinh doanh tại chi nhánh bạn chọn!");
        }

        Carts cart = cartsRepository.findByCustomer_IdCustomer(request.getIdCustomer());
        if (cart == null) {
            Customer customer = customerRepository.findById(request.getIdCustomer())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng trong hệ thống!"));
            cart = new Carts();
            cart.setCustomer(customer);
            cart = cartsRepository.save(cart);
        }

        CartItems cartItem = cartItemsRepository.findByCart_IdCartsAndProduct_IdProduct(
                cart.getIdCarts(),
                request.getIdProduct()
        );

        int totalQuantityWanted = request.getQuantity();
        if (cartItem != null) {
            totalQuantityWanted += cartItem.getQuantity();
        }

        if (stock.getStockQuantity() < totalQuantityWanted) {
            throw new RuntimeException("Thất bại! Chi nhánh này chỉ còn " + stock.getStockQuantity() + " sản phẩm.");
        }

        if (cartItem != null) {
            cartItem.setQuantity(totalQuantityWanted);
        } else {
            Product product = productRepository.findById(request.getIdProduct())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
            cartItem = new CartItems();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        }

        cartItemsRepository.save(cartItem);
        return "Đã thêm thành công sản phẩm vào giỏ hàng!";
    }


    // hàm xem gio hang
    public CartsResponse getCartsByCustomerId(Long idCustomer, Long idBranch) {
        CartsResponse cartsResponse = new CartsResponse();

        Carts cart = cartsRepository.findByCustomer_IdCustomer(idCustomer);
        if (cart == null) {
            cartsResponse.setItems(new java.util.ArrayList<>());
            cartsResponse.setTotalPrice(0.0);
            return cartsResponse;
        }

        List<CartItems> listItems = cartItemsRepository.findByCart_IdCarts(cart.getIdCarts());
        if (listItems.isEmpty()) {
            cartsResponse.setIdCarts(cart.getIdCarts());
            cartsResponse.setItems(new java.util.ArrayList<>());
            cartsResponse.setTotalPrice(0.0);
            return cartsResponse;
        }

        List<Long> productIds = listItems.stream()
                .map(item -> item.getProduct().getIdProduct())
                .toList();

        List<Stock> stocks = stockRepository.findByProduct_IdProductInAndBranch_IdBranch(productIds, idBranch);

        Map<Long, Stock> stockMap = new HashMap<>();
        for (Stock stock : stocks) {
            Long key = stock.getProduct().getIdProduct();
            stockMap.put(key, stock);
        }

        List<CartItemsResponse> itemResponses = new java.util.ArrayList<>();
        Double totalPrice = 0.0;

        for (CartItems item : listItems) {
            CartItemsResponse itemDto = new CartItemsResponse();
            itemDto.setIdProduct(item.getProduct().getIdProduct());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setImageUrl(item.getProduct().getImageUrl());
            itemDto.setPrice(item.getProduct().getPrice());

            itemDto.setQuantity(item.getQuantity());

            Double subTotal = item.getProduct().getPrice() * item.getQuantity();
            itemDto.setSubTotal(subTotal);

            Stock stock = stockMap.get(item.getProduct().getIdProduct());

            if (stock != null && stock.getStockQuantity() >= item.getQuantity()) {
                itemDto.setAvailable(true);
                itemDto.setMaxAvailable(stock.getStockQuantity());
                totalPrice += subTotal;
            } else {
                itemDto.setAvailable(false);
                itemDto.setMaxAvailable(stock != null ? stock.getStockQuantity() : 0);
            }

            itemResponses.add(itemDto);
        }

        cartsResponse.setIdCarts(cart.getIdCarts());
        cartsResponse.setItems(itemResponses);
        cartsResponse.setTotalPrice(totalPrice);

        return cartsResponse;
    }

    // hàm Cập nhật số lượng
    @Transactional
    public String updateQuantity(Long idCustomer, Long idProduct, Long idBranch, Integer newQuantity) {

        Stock stock = stockRepository.findByProduct_IdProductAndBranch_IdBranch(idProduct, idBranch);

        if (stock == null) {
            throw new RuntimeException("Sản phẩm này hiện không kinh doanh tại chi nhánh bạn chọn!");
        }

        if (stock.getStockQuantity() < newQuantity) {
            throw new RuntimeException("Thất bại! Chi nhánh này chỉ còn " + stock.getStockQuantity() + " sản phẩm.");
        }

        Carts cart = cartsRepository.findByCustomer_IdCustomer(idCustomer);
        if (cart == null) throw new RuntimeException("Không tìm thấy giỏ hàng!");

        CartItems item = cartItemsRepository.findByCart_IdCartsAndProduct_IdProduct(cart.getIdCarts(), idProduct);
        if (item == null) throw new RuntimeException("Sản phẩm không có trong giỏ hàng!");

        item.setQuantity(newQuantity);
        cartItemsRepository.save(item);

        return "Cập nhật số lượng thành công!";
    }

    // hàm xóa sản phẩm khỏi giỏ
    @Transactional
    public String removeCartItem(Long idCustomer, Long idProduct) {
        Carts cart = cartsRepository.findByCustomer_IdCustomer(idCustomer);
        if (cart == null) throw new RuntimeException("Không tìm thấy giỏ hàng!");

        CartItems item = cartItemsRepository.findByCart_IdCartsAndProduct_IdProduct(cart.getIdCarts(), idProduct);
        if (item == null) throw new RuntimeException("Sản phẩm không có trong giỏ hàng!");

        cartItemsRepository.delete(item);

        return "Đã xóa sản phẩm khỏi giỏ hàng!";
    }
}
