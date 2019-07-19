package com.softmed.stockapp.fixtures;


import com.softmed.stockapp.dom.model.IMessageDTO;
import com.softmed.stockapp.dom.model.IMessageUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * Created by troy379 on 12.12.16.
 */
public final class MessagesFixtures extends FixturesData {
    private MessagesFixtures() {
        throw new AssertionError();
    }

    public static IMessageDTO getImageMessage() {
        IMessageDTO IMessageDTO = new IMessageDTO(getRandomId(), getUser(), null);
        IMessageDTO.setImage(new IMessageDTO.Image(getRandomImage()));
        return IMessageDTO;
    }

    public static IMessageDTO getVoiceMessage() {
        IMessageDTO IMessageDTO = new IMessageDTO(getRandomId(), getUser(), null);
        IMessageDTO.setVoice(new IMessageDTO.Voice("http://example.com", rnd.nextInt(200) + 30));
        return IMessageDTO;
    }

    public static IMessageDTO getTextMessage() {
        return getTextMessage(getRandomMessage());
    }

    public static IMessageDTO getTextMessage(String text) {
        return new IMessageDTO(getRandomId(), getUser(), text);
    }

    public static ArrayList<IMessageDTO> getMessages(Date startDate) {
        ArrayList<IMessageDTO> IMessageDTOS = new ArrayList<>();
        for (int i = 0; i < 10/*days count*/; i++) {
            int countPerDay = rnd.nextInt(5) + 1;

            for (int j = 0; j < countPerDay; j++) {
                IMessageDTO IMessageDTO;
                if (i % 2 == 0 && j % 3 == 0) {
                    IMessageDTO = getImageMessage();
                } else {
                    IMessageDTO = getTextMessage();
                }

                Calendar calendar = Calendar.getInstance();
                if (startDate != null) calendar.setTime(startDate);
                calendar.add(Calendar.DAY_OF_MONTH, -(i * i + 1));

                IMessageDTO.setCreatedAt(calendar.getTime());
                IMessageDTOS.add(IMessageDTO);
            }
        }
        return IMessageDTOS;
    }

    private static IMessageUser getUser() {
        boolean even = rnd.nextBoolean();
        return new IMessageUser(
                even ? "0" : "1",
                even ? names.get(0) : names.get(1),
                even ? avatars.get(0) : avatars.get(1),
                true);
    }
}
