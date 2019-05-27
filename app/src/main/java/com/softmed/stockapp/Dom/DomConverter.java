package com.softmed.stockapp.Dom;

import com.softmed.stockapp.Dom.entities.Category;
import com.softmed.stockapp.Dom.entities.Product;
import com.softmed.stockapp.Dom.entities.Unit;
import com.softmed.stockapp.Dom.entities.UsersInfo;
import com.softmed.stockapp.Dom.responces.CategoriesResponse;
import com.softmed.stockapp.Dom.responces.LoginResponse;
import com.softmed.stockapp.Dom.responces.ProductsResponse;
import com.softmed.stockapp.Dom.responces.SubCategoriesResponse;
import com.softmed.stockapp.Dom.responces.UnitsResponse;

import java.util.ArrayList;
import java.util.List;

public class DomConverter {

    public static UsersInfo getUserInfo(LoginResponse loginResponse){
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setEmail(loginResponse.getEmail());
        usersInfo.setFirstName(loginResponse.getFirstName());
        usersInfo.setMiddleName(loginResponse.getMiddleName());
        usersInfo.setSurname(loginResponse.getSurname());
        usersInfo.setId(loginResponse.getId());
        usersInfo.setUuid(loginResponse.getUuid());
        usersInfo.setId(loginResponse.getId());
        usersInfo.setSurname(loginResponse.getUsername());
        try {
            usersInfo.setLevelId(loginResponse.getLevelResponses().get(0).getId());
            usersInfo.setLocationId(loginResponse.getLocationResponses().get(0).getId());
        }catch (Exception e){
            e.printStackTrace();
        }

        return usersInfo;

    }

    public static Category getCategory(CategoriesResponse categoriesResponse){
        Category category = new Category();
        category.setId(categoriesResponse.getId());
        category.setName(categoriesResponse.getName());
        category.setDescription(categoriesResponse.getDescription());
        category.setUuid(categoriesResponse.getUuid());

        return category;

    }


    public static Product getProduct(ProductsResponse productsResponse){
        Product product = new Product();

        product.setId(productsResponse.getId());
        product.setName(productsResponse.getName());
        product.setDescription(productsResponse.getDescription());
        product.setUuid(productsResponse.getUuid());
        product.setCategory_id(productsResponse.getCategory_id());
        product.setUnit_id(productsResponse.getUnitResponses().get(0).getId());


        return product;
    }

    public static Unit getUnit(UnitsResponse unitsResponse){
        Unit unit = new Unit();

        unit.setId(unitsResponse.getId());
        unit.setUuid(unitsResponse.getUuid());
        unit.setDescription(unitsResponse.getDescription());
        unit.setName(unitsResponse.getName());


        return unit;
    }

}
