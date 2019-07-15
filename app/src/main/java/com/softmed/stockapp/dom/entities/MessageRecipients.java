package com.softmed.stockapp.dom.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class MessageRecipients implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private int id;


    @SerializedName("recipient_id")
    private int recipientId;

    @SerializedName("message_id")
    private int messageId;

    @SerializedName("is_read")
    private boolean isRead;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
