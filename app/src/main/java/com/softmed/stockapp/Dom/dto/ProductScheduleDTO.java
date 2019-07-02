package com.softmed.stockapp.Dom.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cozej4 on 7/1/19.
 *
 * @cozej4 https://github.com/cozej4
 */
public class ProductScheduleDTO {
    long reportingDate;
    List<String> productNames = new ArrayList<>();

    public long getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(long reportingDate) {
        this.reportingDate = reportingDate;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }
}
