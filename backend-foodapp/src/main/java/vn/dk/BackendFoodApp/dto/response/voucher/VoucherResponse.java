package vn.dk.BackendFoodApp.dto.response.voucher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.dk.BackendFoodApp.dto.response.BaseResponse;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.model.Voucher;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class VoucherResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("min_amount")
    private Integer minAmount;

    @JsonProperty("user_id")
    private Long userId;

    public static VoucherResponse fromVoucher(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .minAmount(voucher.getMinAmount())
                .name(voucher.getName())
                .userId(Optional.ofNullable(voucher.getUser()).map(User::getId).orElse(null))
                .build();
    }
}