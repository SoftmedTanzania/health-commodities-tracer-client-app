package com.softmed.stockapp.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp.dom.dto.MessageUserDTO;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.OtherUsers;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface MessagesModelDao {

    @Query(" SELECT * from Message WHERE parentMessageId = '0' ")
    LiveData<List<Message>> getMessageThreads();


    @Query(" SELECT * from Message WHERE id =:messageId ")
    Message getMessageById(String messageId);


    @Query(" SELECT Message.id,Message.uuid,Message.creatorId,Message.parentMessageId,Message.subject,Message.messageBody,Message.createDate,OtherUsers.id as userId,OtherUsers.firstName,OtherUsers.health_facility,OtherUsers.middleName,OtherUsers.surname,OtherUsers.username from Message " +
            "INNER JOIN OtherUsers ON OtherUsers.id = Message.creatorId " +
            "WHERE parentMessageId =:parentMessageId OR Message.id = :parentMessageId ORDER BY createDate DESC")
    LiveData<List<MessageUserDTO>> getMessageByThread(String parentMessageId);


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



    @Insert(onConflict = REPLACE)
    void addMessage(Message message);

    @Update
    void updateMessage(Message message);

    @Delete
    void deleteMessage(Message message);

}
