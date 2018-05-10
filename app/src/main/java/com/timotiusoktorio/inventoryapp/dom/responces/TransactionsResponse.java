package com.timotiusoktorio.inventoryapp.dom.responces;

import com.google.gson.annotations.SerializedName;
import com.timotiusoktorio.inventoryapp.dom.objects.Category;
import com.timotiusoktorio.inventoryapp.dom.objects.Transactions;

import java.io.Serializable;
import java.util.List;


public class TransactionsResponse implements Serializable {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    List<Transactions> transactions;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }
}
