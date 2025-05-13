package vn.dk.BackendFoodApp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseResponse {
    @JsonProperty("created_at")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
