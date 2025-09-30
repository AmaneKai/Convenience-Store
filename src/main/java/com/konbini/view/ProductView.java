package com.konbini.view;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.util.List;

public interface ProductView extends BaseView {
    void displayProductMenu();
    int getProductMenuChoice();
    void displayProducts(List<Product> products);
    void displayProduct(Product product);
    void displayLowStockProducts(List<Product> products);
    void displayExpiredProducts(List<Product> products);
    ProductCategory getCategoryInput();
    ProductSubcategory getSubcategoryInput(ProductCategory category);
}
