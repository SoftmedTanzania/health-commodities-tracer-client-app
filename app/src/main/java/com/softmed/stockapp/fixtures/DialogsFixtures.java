package com.softmed.stockapp.fixtures;


import com.softmed.stockapp.dom.model.IMessageDTO;
import com.softmed.stockapp.dom.model.MessageDialog;
import com.softmed.stockapp.dom.model.MessageUserDTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * Created by Anton Bevza on 07.09.16.
 */
public final class DialogsFixtures extends FixturesData {
    private DialogsFixtures() {
        throw new AssertionError();
    }

    public static ArrayList<MessageDialog> getDialogs() {
        ArrayList<MessageDialog> chats = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -(i * i));
            calendar.add(Calendar.MINUTE, -(i * i));

            chats.add(getDialog(i, calendar.getTime()));
        }

        return chats;
    }

    private static MessageDialog getDialog(int i, Date lastMessageCreatedAt) {
        ArrayList<MessageUserDTO> messageUserDTOS = getUsers();
        return new MessageDialog(
                getRandomId(),
                messageUserDTOS.size() > 1 ? groupChatTitles.get(messageUserDTOS.size() - 2) : messageUserDTOS.get(0).getName(),
                messageUserDTOS.size() > 1 ? groupChatImages.get(messageUserDTOS.size() - 2) : getRandomAvatar(),
                messageUserDTOS,
                getMessage(lastMessageCreatedAt),
                i < 3 ? 3 - i : 0);
    }

    private static ArrayList<MessageUserDTO> getUsers() {
        ArrayList<MessageUserDTO> messageUserDTOS = new ArrayList<>();
        int usersCount = 1 + rnd.nextInt(4);

        for (int i = 0; i < usersCount; i++) {
            messageUserDTOS.add(getUser());
        }

        return messageUserDTOS;
    }

    private static MessageUserDTO getUser() {
        return new MessageUserDTO(
                getRandomId(),
                getRandomName(),
                getRandomAvatar(),
                getRandomBoolean());
    }

    private static IMessageDTO getMessage(final Date date) {
        return new IMessageDTO(
                getRandomId(),
                getUser(),
                getRandomMessage(),
                date);
    }
}
