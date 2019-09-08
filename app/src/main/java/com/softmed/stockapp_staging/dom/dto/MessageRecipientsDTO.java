package com.softmed.stockapp_staging.dom.dto;

import com.google.gson.annotations.SerializedName;
import com.softmed.stockapp_staging.dom.entities.MessageRecipients;

import java.util.List;

/**
 * Created by cozej4 on 2019-07-18.
 *
 * @cozej4 https://github.com/cozej4
 */
public class MessageRecipientsDTO {
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


    @SerializedName("message_recipients")
    private List<MessageRecipients> messageRecipients;

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

    public List<MessageRecipients> getMessageRecipients() {
        return messageRecipients;
    }

    public void setMessageRecipients(List<MessageRecipients> messageRecipients) {
        this.messageRecipients = messageRecipients;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTrashedByCreator() {
        return trashedByCreator;
    }

    public void setTrashedByCreator(boolean trashedByCreator) {
        this.trashedByCreator = trashedByCreator;
    }
}
