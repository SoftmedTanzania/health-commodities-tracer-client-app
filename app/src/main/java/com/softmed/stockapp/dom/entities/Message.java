package com.softmed.stockapp.dom.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class Message implements Serializable {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private String id;

    @SerializedName("creator")
    private int creatorId;

    @SerializedName("subject")
    private String subject;

    @SerializedName("message_body")
    private String messageBody;

    @SerializedName("message_date_time")
    private long createDate;

    @SerializedName("parent_message_id")
    private String parentMessageId;

    @SerializedName("trashed_by_creator")
    private boolean trashedByCreator;

    @SerializedName("sync_status")
    private int syncStatus;

    //used to update message list when id is updated while the message is open in foreground
    @SerializedName("uuid")
    private String uuid;


    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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

    public String getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(String parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isTrashedByCreator() {
        return trashedByCreator;
    }

    public void setTrashedByCreator(boolean trashedByCreator) {
        this.trashedByCreator = trashedByCreator;
    }
}
