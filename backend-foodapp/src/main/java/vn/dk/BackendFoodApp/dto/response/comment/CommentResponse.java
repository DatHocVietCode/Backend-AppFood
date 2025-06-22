package vn.dk.BackendFoodApp.dto.response.comment;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.dk.BackendFoodApp.model.Comment;
import vn.dk.BackendFoodApp.dto.response.BaseResponse;
import vn.dk.BackendFoodApp.dto.response.auth.UserResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CommentResponse extends BaseResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    private String content;

//    @JsonProperty("user")
//    private UserResponse user;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("rating")
    private Integer star;

    @JsonProperty("avatar_user")
    private String avatarUser;

    @JsonProperty("name")
    private String username;


    public static CommentResponse fromComment(Comment comment) {
        UserResponse userResponse = UserResponse.fromUser(comment.getUser());
        CommentResponse result = CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
//                .user(userResponse)
                .avatarUser(comment.getUser().getProfileImage())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .productId(comment.getProduct().getId())
                .star(comment.getStar())
                .build();
        return result;
    }
}
