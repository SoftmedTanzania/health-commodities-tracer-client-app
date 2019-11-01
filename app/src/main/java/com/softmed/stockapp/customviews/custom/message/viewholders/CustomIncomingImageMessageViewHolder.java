package com.softmed.stockapp.customviews.custom.message.viewholders;

import android.view.View;

import com.softmed.stockapp.dom.model.IMessageDTO;
import com.stfalcon.chatkit.messages.MessageHolders;

/*
 * Created by troy379 on 05.04.17.
 */
public class CustomIncomingImageMessageViewHolder
        extends MessageHolders.IncomingImageMessageViewHolder<IMessageDTO> {

    public CustomIncomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }

    @Override
    public void onBind(IMessageDTO message) {
        super.onBind(message);
    }
}