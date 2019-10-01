package com.softmed.stockapp_staging.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp_staging.dom.entities.MessageRecipients;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface MessageRecipientsModelDao {

    @Query("SELECT * FROM MessageRecipients")
    List<MessageRecipients> getAllMessageRecipients();


    @Query("SELECT * FROM MessageRecipients WHERE messageId = :messageId")
    List<MessageRecipients> getAllMessageRecipientsByMessageId(String messageId);

    @Query("SELECT * FROM MessageRecipients WHERE messageId = :messageId AND recipientId =:recipientId ")
    MessageRecipients getMessageRecipientsByMessageIdAndRecipientId(String messageId, int recipientId);

    @Query("SELECT COUNT(*) FROM MessageRecipients WHERE " +
            "isRead = :isRead AND " +
            "deletedFromMailBox = 0 AND " +
            "recipientId = :userId AND " +
            "messageId IN (SELECT id from Message WHERE parentMessageId = :parentMessageId OR id = :parentMessageId)")
    int getUnreadMessageCountByParentMessageId(String parentMessageId,boolean isRead,int userId);

    @Query("SELECT COUNT(*) FROM MessageRecipients WHERE " +
            "deletedFromMailBox = :deletedFromMailBox AND " +
            "recipientId = :userId AND " +
            "messageId IN (SELECT id from Message WHERE  id = :parentMessageId)")
    int isMessageDeletedFromMailbox(String parentMessageId,boolean deletedFromMailBox,int userId);

    @Query("SELECT COUNT(*) FROM MessageRecipients WHERE " +
            "isRead = :isRead AND " +
            "recipientId = :userId")
    LiveData<Integer> getUnreadMessageCountUserId(boolean isRead,int userId);

    @Insert(onConflict = REPLACE)
    void addRecipient(MessageRecipients messageRecipients);

    @Update
    void updateRecipient(MessageRecipients messageRecipients);

    @Query("UPDATE MessageRecipients SET messageId = :newId WHERE messageId = :oldId")
    int updateMessageRecipientsIds(String oldId, String newId);

    @Query("UPDATE MessageRecipients SET id = :newId WHERE messageId = :oldId")
    int updateIds(String oldId, String newId);

    @Query("UPDATE MessageRecipients SET deletedFromMailBox = :delete WHERE messageId = :messageId AND recipientId=:userId")
    int deleteMessageFromMailBox(boolean delete, String messageId,String userId);

    @Query("UPDATE MessageRecipients SET isRead = :isRead WHERE messageId = :messageId AND recipientId =:userId AND isRead=0 ")
    int updateIsReadStatus(boolean isRead, String messageId,int userId);

    @Delete
    void deleteRecipient(MessageRecipients messageRecipients);

}
