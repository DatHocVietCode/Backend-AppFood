package vn.dk.BackendFoodApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

//    @Column(name = "fullname", length = 100)
//    private String fullName;
//
//    @Column(name = "email", length = 100)
//    private String email;
//
//    @Column(name = "phone_number",nullable = false, length = 100)
//    private String phoneNumber;
//
//    @Column(name = "note", length = 100)
//    private String note;

    @CreatedBy
    @Column(name="order_date")
    private LocalDateTime orderDate;

    @Column(name = "status")
    private String status;

    @Column(name = "total_money")
    private Float totalMoney;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "payment_method")
    private String paymentMethod = "";

    @Column(name = "active")
    private Boolean active;//thuộc về admin

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = true)
    @JsonBackReference
    private Voucher voucher = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
}

