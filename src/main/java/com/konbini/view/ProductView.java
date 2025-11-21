package com.konbini.view;

import java.util.List;

import com.konbini.dto.ProductDTO;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

public interface ProductView extends BaseView {

    void displayProductMenu();

    int getProductMenuChoice();

    void displayProducts(List<ProductDTO> products);

    void displayProduct(ProductDTO product);

    void displayLowStockProducts(List<ProductDTO> products);

    void displayExpiredProducts(List<ProductDTO> products);

    ProductCategory getCategoryInput();

    ProductSubcategory getSubcategoryInput(ProductCategory category);
}