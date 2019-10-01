package com.softmed.stockapp.dom.dto;

/**
 * Created by cozej4 on 2019-10-01.
 *
 * @cozej4 https://github.com/cozej4
 */
public class DeleteMessegeDTO {
    boolean is_trashed,trashed_by_creator;
    String id;

    public boolean isIs_trashed() {
        return is_trashed;
    }

    public void setIs_trashed(boolean is_trashed) {
        this.is_trashed = is_trashed;
    }

    public boolean isTrashed_by_creator() {
        return trashed_by_creator;
    }

    public void setTrashed_by_creator(boolean trashed_by_creator) {
        this.trashed_by_creator = trashed_by_creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
