package com.softmed.stockapp_staging.dom;

import com.softmed.stockapp_staging.dom.entities.Category;
import com.softmed.stockapp_staging.dom.entities.Unit;
import com.softmed.stockapp_staging.dom.entities.UsersInfo;
import com.softmed.stockapp_staging.dom.responces.CategoriesResponse;
import com.softmed.stockapp_staging.dom.responces.LoginResponse;
import com.softmed.stockapp_staging.dom.responces.UnitsResponse;

public class DomConverter {

    public static UsersInfo getUserInfo(LoginResponse loginResponse) {
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setFirstName(loginResponse.getUser().getFirstName());
        usersInfo.setMiddleName(loginResponse.getUser().getMiddleName());
        usersInfo.setSurname(loginResponse.getUser().getSurname());
        usersInfo.setId(loginResponse.getUser().getId());
        usersInfo.setHealth_facility(loginResponse.getUser().getProfile().getHealthFacility());
        usersInfo.setId(loginResponse.getUser().getId());
        usersInfo.setSurname(loginResponse.getUser().getUsername());

        return usersInfo;

    }

    public static Category getCategory(CategoriesResponse categoriesResponse) {
        Category category = new Category();
        category.setId(categoriesResponse.getId());
        category.setName(categoriesResponse.getName());
        category.setDescription(categoriesResponse.getDescription());

        return category;

    }


    public static Unit getUnit(UnitsResponse unitsResponse) {
        Unit unit = new Unit();

        unit.setId(unitsResponse.getId());
        unit.setDescription(unitsResponse.getDescription());
        unit.setName(unitsResponse.getName());


        return unit;
    }

}
