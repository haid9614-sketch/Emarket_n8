package com.n8.emarket.repository;
import com.n8.emarket.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    // Tìm cartItems = ID Giỏ hàng và ID Sản phẩm
    CartItems findByCart_IdCartsAndProduct_IdProduct(Long idCart, Long idProduct);

    // lấy list = id_carts
    List<CartItems> findByCart_IdCarts(Long idCart);
}
