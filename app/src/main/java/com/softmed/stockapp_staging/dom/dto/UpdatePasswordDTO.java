package com.softmed.stockapp_staging.dom.dto;

/**
 * Created by cozej4 on 2019-10-01.
 *
 * @cozej4 https://github.com/cozej4
 */
public class UpdatePasswordDTO {
    String old_password,new_password;

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
