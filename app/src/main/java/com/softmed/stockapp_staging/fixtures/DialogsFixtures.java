package com.softmed.stockapp_staging.fixtures;


import com.softmed.stockapp_staging.dom.model.IMessageDTO;
import com.softmed.stockapp_staging.dom.model.MessageDialog;
import com.softmed.stockapp_staging.dom.model.IMessageUser;

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
        ArrayList<IMessageUser> IMessageUsers = getUsers();
        return new MessageDialog(
                getRandomId(),
                IMessageUsers.size() > 1 ? groupChatTitles.get(IMessageUsers.size() - 2) : IMessageUsers.get(0).getName(),
                IMessageUsers.size() > 1 ? groupChatImages.get(IMessageUsers.size() - 2) : getRandomAvatar(),
                IMessageUsers,
                getMessage(lastMessageCreatedAt),
                i < 3 ? 3 - i : 0);
    }

    private static ArrayList<IMessageUser> getUsers() {
        ArrayList<IMessageUser> IMessageUsers = new ArrayList<>();
        int usersCount = 1 + rnd.nextInt(4);

        for (int i = 0; i < usersCount; i++) {
            IMessageUsers.add(getUser());
        }

        return IMessageUsers;
    }

    private static IMessageUser getUser() {
        return new IMessageUser(
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
