package vn.dk.BackendFoodApp.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.model.Address;
import vn.dk.BackendFoodApp.model.Order;
import vn.dk.BackendFoodApp.model.ShippingMethod;
import vn.dk.BackendFoodApp.model.Voucher;
import vn.dk.BackendFoodApp.repository.AddressRepository;
import vn.dk.BackendFoodApp.repository.OrderRepository;
import vn.dk.BackendFoodApp.repository.ShippingMethodRepository;
import vn.dk.BackendFoodApp.repository.VoucherRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private AddressRepository addressRepository;

    public void createOrder(Order order) {
        // Ở đây bạn xử lý lưu order và order details nếu có
        orderRepository.save(order);
    }

    public ShippingMethod findShippingMethodByName(String name) {
        return shippingMethodRepository.findByMethodName(name)
                .orElseThrow(() -> new EntityNotFoundException("Shipping method not found with name: " + name));
    }
    public void saveVoucher(Voucher voucher) {
        voucherRepository.save(voucher);
    }

    public Voucher findVoucherById(Long id) {
        if (id == null) return null;
        return voucherRepository.findById(id).orElse(null);
    }


    public Address findAddressById(Long id) {
        if (id == null) return null;
        return addressRepository.findById(id).orElse(null);
    }
}
