package vn.dk.BackendFoodApp.model;

import jakarta.persistence.*;
import lombok.*;
import vn.dk.BackendFoodApp.common.AddressType;
import vn.dk.BackendFoodApp.common.ShippingType;

@Entity
@Table(name = "shipping_method")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "methodName", length = 255)
    private String methodName;


    private Float price;
}