package com.softmed.stockapp.adapters;

import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageHolders;

/**
 * Created by cozej4 on 2019-07-19.
 *
 * @cozej4 https://github.com/cozej4
 */
public class MessagesListAdapter<MESSAGE extends IMessage> extends com.stfalcon.chatkit.messages.MessagesListAdapter {
    public MessagesListAdapter(String senderId, ImageLoader imageLoader) {
        super(senderId, imageLoader);
    }

    public MessagesListAdapter(String senderId, MessageHolders holders, ImageLoader imageLoader) {
        super(senderId, holders, imageLoader);
    }


    public int getMessagePositionById(String id) {
        for (int i = 0; i < items.size(); i++) {
            Wrapper wrapper = (Wrapper) items.get(i);
            if (wrapper.item instanceof IMessage) {
                IMessage message = (IMessage) wrapper.item;
                if (message.getId().contentEquals(id)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
