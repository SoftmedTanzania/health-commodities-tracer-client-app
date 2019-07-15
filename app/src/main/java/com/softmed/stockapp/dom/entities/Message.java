package com.softmed.stockapp.dom.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class Message implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private int id;


    @SerializedName("creator_id")
    private int creatorId;

    @SerializedName("subject")
    private String subject;

    @SerializedName("message_body")
    private String messageBody;

    @SerializedName("create_date")
    private long createDate;

    @SerializedName("parent_message_id")
    private int parentMessageId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(int parentMessageId) {
        this.parentMessageId = parentMessageId;
    }
}
