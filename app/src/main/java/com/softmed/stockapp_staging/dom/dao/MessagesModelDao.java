package com.softmed.stockapp_staging.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp_staging.dom.dto.MessageUserDTO;
import com.softmed.stockapp_staging.dom.entities.Message;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface MessagesModelDao {

    @Query(" SELECT Message.* from Message " +
            "LEFT JOIN MessageRecipients ON MessageRecipients.messageId = Message.id " +
            "WHERE parentMessageId = '0' GROUP BY Message.id")
    LiveData<List<Message>> getMessageThreads();


    @Query(" SELECT * from Message WHERE id =:messageId ")
    Message getMessageById(String messageId);



    @Query(" SELECT * from Message WHERE id =:messageId OR uuid=:messageId ")
    Message getParentMessageById(String messageId);

    @Query(" SELECT Message.*,OtherUsers.id as userId,OtherUsers.firstName,OtherUsers.health_facility,OtherUsers.middleName,OtherUsers.surname,OtherUsers.username,T1.deletedFromMailBox from Message " +
            "INNER JOIN OtherUsers ON OtherUsers.id = Message.creatorId " +
            "LEFT JOIN (Select * FROM MessageRecipients WHERE recipientId = :userId ) AS T1 ON T1.messageId = Message.id " +
            "WHERE  (parentMessageId =:parentMessageId OR Message.id = :parentMessageId OR Message.uuid = :parentMessageId) GROUP BY Message.id ORDER BY createDate DESC")
    LiveData<List<MessageUserDTO>> getMessageByThread(String parentMessageId,String userId);


    @Query(" SELECT * from Message WHERE parentMessageId = :parentMessageId OR id = :parentMessageId ORDER BY createDate DESC LIMIT 1 ")
    Message getLatestMessages(String parentMessageId);


    @Query( "Select Message.id from Message " +
            "INNER JOIN MessageRecipients ON MessageRecipients.messageId = Message.id " +
            "WHERE (Message.creatorId =:userId OR MessageRecipients.recipientId =:userId) AND parentMessageId = 0  ORDER BY createDate ASC LIMIT 1")
    String getParentMessageId(int userId);


    @Query(" SELECT * from Message WHERE syncStatus = 0 ")
    List<Message> getUnpostedMessages();


    @Query("UPDATE Message SET id = :newId WHERE id = :oldId")
    int updateMessageIds(String oldId, String newId);

    @Query("UPDATE Message SET trashedByCreator = :delete WHERE id = :messageId AND creatorId=:userId")
    int deleteMessageFromMailBox(boolean delete, String messageId,String userId);


    @Insert(onConflict = REPLACE)
    void addMessage(Message message);

    @Update
    void updateMessage(Message message);

    @Delete
    void deleteMessage(Message message);

}
