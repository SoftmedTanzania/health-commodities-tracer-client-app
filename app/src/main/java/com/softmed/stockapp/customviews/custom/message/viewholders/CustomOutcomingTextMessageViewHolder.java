package com.softmed.stockapp.customviews.custom.message.viewholders;

import android.view.View;

import com.softmed.stockapp.dom.model.IMessageDTO;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomOutcomingTextMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<IMessageDTO> {

    public CustomOutcomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }

    @Override
    public void onBind(IMessageDTO message) {
        super.onBind(message);

        time.setText(message.getStatus() + " " + time.getText());
    }
}
