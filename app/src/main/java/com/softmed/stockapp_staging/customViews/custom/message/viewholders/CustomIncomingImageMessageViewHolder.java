package com.softmed.stockapp_staging.customViews.custom.message.viewholders;

import android.view.View;
import android.widget.TextView;

import com.softmed.stockapp_staging.dom.model.IMessageDTO;
import com.stfalcon.chatkit.messages.MessageHolders;

/*
 * Created by troy379 on 05.04.17.
 */
public class CustomIncomingImageMessageViewHolder
        extends MessageHolders.IncomingImageMessageViewHolder<IMessageDTO> {

    private TextView userName;

    public CustomIncomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
//        userName = itemView.findViewById(R.id.onlineIndicator);
    }

    @Override
    public void onBind(IMessageDTO message) {
        super.onBind(message);

        boolean isOnline = message.getUser().isOnline();
//        if (isOnline) {
//            userName.setText("");
//        } else {
//            userName.setText("");
//        }
    }
}