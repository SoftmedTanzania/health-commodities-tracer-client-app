package com.softmed.rucodia.dom;

import com.softmed.rucodia.dom.objects.Category;
import com.softmed.rucodia.dom.objects.Location;
import com.softmed.rucodia.dom.objects.Product;
import com.softmed.rucodia.dom.objects.SubCategory;
import com.softmed.rucodia.dom.objects.Unit;
import com.softmed.rucodia.dom.objects.UsersInfo;
import com.softmed.rucodia.dom.responces.CategoriesResponse;
import com.softmed.rucodia.dom.responces.LocationResponse;
import com.softmed.rucodia.dom.responces.LoginResponse;
import com.softmed.rucodia.dom.responces.ProductsResponse;
import com.softmed.rucodia.dom.responces.SubCategoriesResponse;
import com.softmed.rucodia.dom.responces.UnitsResponse;

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

    public static Location getLocation(LoginResponse loginResponse){
        LocationResponse locationResponse = loginResponse.getLocationResponses().get(0);

        Location location = new Location();
        location.setId(locationResponse.getId());
        location.setLatitude(locationResponse.getLatitude());
        location.setLatitude(locationResponse.getLongitude());
        location.setName(locationResponse.getName());
        location.setUuid(locationResponse.getUuid());


        return location;

    }

    public static Category getCategory(CategoriesResponse categoriesResponse){
        Category category = new Category();
        category.setId(categoriesResponse.getId());
        category.setName(categoriesResponse.getName());
        category.setDescription(categoriesResponse.getDescription());
        category.setUuid(categoriesResponse.getUuid());

        return category;

    }

    public static List<SubCategory> getSubCategories(CategoriesResponse categoriesResponse){

        List<SubCategory> subCategories = new ArrayList<>();
        for(SubCategoriesResponse subCategoriesResponse : categoriesResponse.getSubCategoriesResponses()){
            SubCategory subCategory = new SubCategory();

            subCategory.setCategoryId(categoriesResponse.getId());
            subCategory.setDescription(subCategoriesResponse.getDescription());
            subCategory.setId(subCategoriesResponse.getId());
            subCategory.setName(subCategoriesResponse.getName());
            subCategory.setUid(subCategoriesResponse.getUuid());

            subCategories.add(subCategory);
        }

        return subCategories;

    }

    public static Product getProduct(ProductsResponse productsResponse){
        Product product = new Product();

        product.setId(productsResponse.getId());
        product.setName(productsResponse.getName());
        product.setDescription(productsResponse.getDescription());
        product.setUuid(productsResponse.getUuid());
        product.setSubcategoryId(productsResponse.getSubcategories().get(0).getId());
        product.setUnitId(productsResponse.getUnitResponses().get(0).getId());


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
