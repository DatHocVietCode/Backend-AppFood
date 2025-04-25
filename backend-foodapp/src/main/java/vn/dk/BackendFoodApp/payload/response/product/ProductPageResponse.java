package vn.dk.BackendFoodApp.payload.response.product;

import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.payload.response.PageResponseAbstract;

import java.io.Serializable;
import java.util.List;

public class ProductPageResponse extends PageResponseAbstract implements Serializable {
    private List<ProductResponse> products;
}
