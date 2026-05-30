package com.n8.emarket.service;

import com.n8.emarket.dto.AddressRequest;
import com.n8.emarket.entity.Address;
import com.n8.emarket.entity.Customer;
import com.n8.emarket.repository.AddressRepository;
import com.n8.emarket.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Address> getMyAddresses(Long idCustomer) {
        return addressRepository.findByCustomer_IdCustomer(idCustomer).stream()
                .filter(address -> address.getIsDelete() == 0)
                .toList();
    }

    @Transactional
    public String addAddress(AddressRequest request, Long idCustomer) {
        Customer customer = customerRepository.findById(idCustomer)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng!"));

        Address newAddress = new Address();
        newAddress.setCustomer(customer);
        newAddress.setHouseNumber(request.getHouseNumber());
        newAddress.setWard(request.getWard());
        newAddress.setDistrict(request.getDistrict());
        newAddress.setCity(request.getCity());
        newAddress.setIsDelete(0);

        addressRepository.save(newAddress);
        return "Thêm địa chỉ giao hàng thành công!";
    }

    @Transactional
    public String deleteAddress(Long idAddress, Long idCustomer) {
        Address address = addressRepository.findById(idAddress)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ này!"));

        if (!address.getCustomer().getIdCustomer().equals(idCustomer)) {
            throw new RuntimeException("Bạn không có quyền xóa địa chỉ của tài khoản khác!");
        }

        address.setIsDelete(1);
        addressRepository.save(address);

        return "Đã xóa địa chỉ thành công!";
    }
}
