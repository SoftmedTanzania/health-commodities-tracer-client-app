package com.softmed.stockapp.dom.responces;

import com.google.gson.annotations.SerializedName;


public class ProductReportingScheduleResponse{

    @SerializedName("id")
    private int id;

    @SerializedName("health_commodity")
    private int productId;

    @SerializedName("location")
    private int facilityId;

    @SerializedName("scheduled_date")
    private String scheduledDate;

    @SerializedName("status")
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
