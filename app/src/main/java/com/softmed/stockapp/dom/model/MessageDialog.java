package com.softmed.stockapp.dom.model;

import com.stfalcon.chatkit.commons.models.IDialog;

import java.util.ArrayList;

/*
 * Created by troy379 on 04.04.17.
 */
public class MessageDialog implements IDialog<IMessageDTO> {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private String parentMessageId="";
    private ArrayList<MessageUserDTO> messageUserDTOS;
    private IMessageDTO lastIMessageDTO;

    private int unreadCount;

    public MessageDialog(String id, String name, String photo,
                         ArrayList<MessageUserDTO> messageUserDTOS, IMessageDTO lastIMessageDTO, int unreadCount) {

        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.messageUserDTOS = messageUserDTOS;
        this.lastIMessageDTO = lastIMessageDTO;
        this.unreadCount = unreadCount;
    }

    public MessageDialog(String id, String name, String photo,
                         ArrayList<MessageUserDTO> messageUserDTOS, IMessageDTO lastIMessageDTO, int unreadCount, String parentMessageId) {

        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.messageUserDTOS = messageUserDTOS;
        this.lastIMessageDTO = lastIMessageDTO;
        this.unreadCount = unreadCount;
        this.parentMessageId = parentMessageId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public ArrayList<MessageUserDTO> getUsers() {
        return messageUserDTOS;
    }

    @Override
    public IMessageDTO getLastMessage() {
        return lastIMessageDTO;
    }

    @Override
    public void setLastMessage(IMessageDTO lastIMessageDTO) {
        this.lastIMessageDTO = lastIMessageDTO;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(String parentMessageId) {
        this.parentMessageId = parentMessageId;
    }
}
