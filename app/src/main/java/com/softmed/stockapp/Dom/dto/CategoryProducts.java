package com.softmed.stockapp.Dom.dto;

import com.softmed.stockapp.Dom.entities.Category;
import com.softmed.stockapp.Dom.entities.Product;

import java.util.List;

/**
 * Created by cozej4 on 6/2/19.
 *
 * @cozej4 https://github.com/cozej4
 */
public class CategoryProducts {
    Category category;
    List<Product> products;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
