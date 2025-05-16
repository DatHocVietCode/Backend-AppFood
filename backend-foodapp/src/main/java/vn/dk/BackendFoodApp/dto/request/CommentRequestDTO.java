package vn.dk.BackendFoodApp.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDTO {
    private Long productId;
    private int star;       // điểm đánh giá, vd 1-5
    private String content; // nội dung comment
}
