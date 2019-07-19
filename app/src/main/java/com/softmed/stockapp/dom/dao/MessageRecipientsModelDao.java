package com.softmed.stockapp.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp.dom.dto.MessageUserDTO;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface MessageRecipientsModelDao {

    @Query("SELECT * FROM MessageRecipients")
    List<MessageRecipients> getAllMessageRecipients();


    @Query("SELECT * FROM MessageRecipients WHERE messageId = :messageId")
    List<MessageRecipients> getAllMessageRecipientsByMessageId(String messageId);

    @Query("SELECT COUNT(*) FROM MessageRecipients WHERE " +
            "isRead = :isRead AND " +
            "recipientId = :userId AND " +
            "messageId IN (SELECT id from Message WHERE parentMessageId = :parentMessageId OR id = :parentMessageId)")
    int getUnreadMessageCountByParentMessageId(String parentMessageId,boolean isRead,int userId);

    @Insert(onConflict = REPLACE)
    void addRecipient(MessageRecipients messageRecipients);

    @Update
    void updateRecipient(MessageRecipients messageRecipients);

    @Query("UPDATE MessageRecipients SET messageId = :newId WHERE messageId = :oldId")
    int updateMessageRecipientsIds(String oldId, String newId);

    @Query("UPDATE MessageRecipients SET id = :newId WHERE messageId = :oldId")
    int updateIds(String oldId, String newId);

    @Delete
    void deleteRecipient(MessageRecipients messageRecipients);

}
