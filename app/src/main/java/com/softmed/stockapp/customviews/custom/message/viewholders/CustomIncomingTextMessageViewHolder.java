package com.softmed.stockapp.customviews.custom.message.viewholders;

import android.view.View;
import android.widget.TextView;

import com.softmed.stockapp.R;
import com.softmed.stockapp.dom.model.IMessageDTO;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomIncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<IMessageDTO> {

    private TextView userName;

    public CustomIncomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        userName = itemView.findViewById(R.id.userName);
    }

    @Override
    public void onBind(IMessageDTO message) {
        super.onBind(message);

        userName.setText(message.getUser().getName());

        //We can set click listener on view from payload
        final Payload payload = (Payload) this.payload;
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (payload != null && payload.avatarClickListener != null) {
                    payload.avatarClickListener.onAvatarClick();
                }
            }
        });
    }

    public interface OnAvatarClickListener {
        void onAvatarClick();
    }

    public static class Payload {
        public OnAvatarClickListener avatarClickListener;
    }
}
