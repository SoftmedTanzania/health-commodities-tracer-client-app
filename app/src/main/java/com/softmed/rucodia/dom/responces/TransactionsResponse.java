package com.softmed.rucodia.dom.responces;

import com.google.gson.annotations.SerializedName;
import com.softmed.rucodia.dom.objects.TransactionType;

import java.io.Serializable;


public class TransactionsResponse implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("transactiontype_id")
    private int transactiontype_id;

    @SerializedName("product_id")
    private int product_id;

    @SerializedName("status_id")
    private int status_id;

    @SerializedName("amount")
    private int amount;

    @SerializedName("price")
    private int price;


    @SerializedName("transactiontype")
    TransactionType transactionType;

}
