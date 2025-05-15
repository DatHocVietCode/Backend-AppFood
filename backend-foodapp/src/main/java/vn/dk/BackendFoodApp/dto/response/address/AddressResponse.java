package vn.dk.BackendFoodApp.dto.response.address;


import lombok.*;
import vn.dk.BackendFoodApp.common.AddressType;
import vn.dk.BackendFoodApp.model.Address;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private String address;
    private AddressType addressType;
    private Boolean isDefault;
    public static AddressResponse fromEntity(Address entity) {
        return AddressResponse.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .addressType(entity.getAddressType())
                .isDefault(entity.getIsDefault())
                .build();
    }
}
