package com.softmed.stockapp.dom.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.OtherUsers;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface MessagesModelDao {

    @Query(" select T2.id,T2.createDate,T2.parentMessageId,T2.creatorId,T2.messageBody,T2.subject from (select * from Message WHERE parentMessageId = 0) T1" +
            " INNER JOIN (select * from Message ORDER BY createDate DESC) T2 ON T2.parentMessageId=T1.id  GROUP BY T1.id")
    LiveData<List<Message>> getMessageThreads();


    @Insert(onConflict = REPLACE)
    void addMessage(Message message);

    @Update
    void updateMessage(Message message);

    @Delete
    void deleteMessage(Message message);

}
