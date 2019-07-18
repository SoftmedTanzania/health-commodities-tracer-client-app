/******************************************************************************
 * Copyright 2016 stfalcon.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.softmed.stockapp.adapters;

import android.view.View;

import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IDialog;

/**
 * Adapter for {@link com.softmed.stockapp.activities.MessagesDialogsActivity}
 */
@SuppressWarnings("WeakerAccess")
public class DialogsListAdapter
        extends com.stfalcon.chatkit.dialogs.DialogsListAdapter {
    public DialogsListAdapter(ImageLoader imageLoader) {
        super(imageLoader);
    }

    public DialogsListAdapter(int itemLayoutId, ImageLoader imageLoader) {
        super(itemLayoutId, imageLoader);
    }

    public DialogsListAdapter(int itemLayoutId, Class holderClass, ImageLoader imageLoader) {
        super(itemLayoutId, holderClass, imageLoader);
    }

    public static class DialogViewHolder extends com.stfalcon.chatkit.dialogs.DialogsListAdapter.DialogViewHolder {


        public DialogViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(IDialog dialog) {
            super.onBind(dialog);


            //Set Dialog avatar
            if (imageLoader != null) {
                imageLoader.loadImage(ivAvatar, dialog.getDialogPhoto(), "24");
            }

            //Set Last message user avatar with check if there is last message
            if (imageLoader != null && dialog.getLastMessage() != null) {
                imageLoader.loadImage(ivLastMessageUser, dialog.getLastMessage().getUser().getAvatar(), "10");
            }
        }


    }


}
