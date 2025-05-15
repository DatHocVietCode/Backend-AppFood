package vn.dk.BackendFoodApp.dto.request.address;

import lombok.*;
import vn.dk.BackendFoodApp.common.AddressType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {
    private String address;
    private AddressType addressType;
    private Boolean isDefault;
}