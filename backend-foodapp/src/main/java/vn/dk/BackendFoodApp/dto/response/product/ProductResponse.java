package vn.dk.BackendFoodApp.dto.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.dk.BackendFoodApp.model.Comment;
import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.model.ProductImage;
import vn.dk.BackendFoodApp.dto.response.BaseResponse;
import vn.dk.BackendFoodApp.dto.response.comment.CommentResponse;
import vn.dk.BackendFoodApp.dto.response.favorite.FavoriteResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductResponse extends BaseResponse {

    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    private Long sold;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();

    @JsonProperty("comments")
    private List<CommentResponse> comments = new ArrayList<>();

    @JsonProperty("favorites")
    private List<FavoriteResponse> favorites = new ArrayList<>();

    @JsonProperty("category_id")
    private Long categoryId;

    public static ProductResponse fromProduct(Product product) {
        List<Comment> sortedComments = product.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .productImages(product.getProductImages())
                .comments(sortedComments.stream().map(CommentResponse::fromComment).toList())
                .favorites(product.getFavorites().stream().map(FavoriteResponse::fromFavorite).toList())
                .sold(product.getSold())
                .createdAt(product.getCreatedAt()) // kế thừa từ BaseResponse
                .updatedAt(product.getUpdatedAt()) // kế thừa từ BaseResponse
                .build();
    }
}

