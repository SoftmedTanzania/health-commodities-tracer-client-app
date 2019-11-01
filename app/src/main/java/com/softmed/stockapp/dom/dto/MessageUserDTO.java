package com.softmed.stockapp.dom.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class MessageUserDTO implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("uuid")
    private String uuid;

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

    @SerializedName("userId")
    private int userId;

    @SerializedName("health_facility")
    private int health_facility;

    @SerializedName("firstname")
    private String firstName;

    @SerializedName("middlename")
    private String middleName;

    @SerializedName("surname")
    private String surname;

    @SerializedName("username")
    private String username;

    //TODO delete this
    @SerializedName("deletedFromMailBox")
    private boolean deletedFromMailBox;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHealth_facility() {
        return health_facility;
    }

    public void setHealth_facility(int health_facility) {
        this.health_facility = health_facility;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isDeletedFromMailBox() {
        return deletedFromMailBox;
    }

    public void setDeletedFromMailBox(boolean deletedFromMailBox) {
        this.deletedFromMailBox = deletedFromMailBox;
    }
}
