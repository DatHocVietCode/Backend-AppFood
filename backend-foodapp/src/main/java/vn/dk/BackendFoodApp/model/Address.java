package vn.dk.BackendFoodApp.model;

import jakarta.persistence.*;
import lombok.*;
import vn.dk.BackendFoodApp.common.AddressType;
import vn.dk.BackendFoodApp.common.UserStatus;

@Entity
@Table(name = "address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 255)
    private AddressType addressType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean isDefault;
}
