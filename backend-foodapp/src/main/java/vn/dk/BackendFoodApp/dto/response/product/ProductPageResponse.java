package vn.dk.BackendFoodApp.dto.response.product;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import vn.dk.BackendFoodApp.dto.response.PageResponseAbstract;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
public class ProductPageResponse extends PageResponseAbstract implements Serializable {
    private List<ProductResponse> products;
}
