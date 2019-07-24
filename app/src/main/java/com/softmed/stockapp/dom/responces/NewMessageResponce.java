package com.softmed.stockapp.dom.responces;

import com.softmed.stockapp.dom.dto.MessageRecipientsDTO;

/**
 * Created by cozej4 on 2019-07-24.
 *
 * @cozej4 https://github.com/cozej4
 */
public class NewMessageResponce {
    String type;
    MessageRecipientsDTO data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MessageRecipientsDTO getData() {
        return data;
    }

    public void setData(MessageRecipientsDTO data) {
        this.data = data;
    }
}
