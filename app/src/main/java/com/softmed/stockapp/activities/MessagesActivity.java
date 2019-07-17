package com.softmed.stockapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.softmed.stockapp.R;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.dto.MessageUserDTO;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.dom.entities.OtherUsers;
import com.softmed.stockapp.dom.model.IMessageDTO;
import com.softmed.stockapp.dom.model.User;
import com.softmed.stockapp.fixtures.MessagesFixtures;
import com.softmed.stockapp.utils.AppUtils;
import com.softmed.stockapp.utils.Calendars;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.viewmodels.MessageListViewModel;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MessagesActivity extends AppCompatActivity
        implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener,
        MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        DateFormatter.Formatter {
    private static final String TAG = MessagesActivity.class.getSimpleName();
    private static final int TOTAL_MESSAGES_COUNT = 100;
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<IMessageDTO> messagesAdapter;
    private String parentMessageId = "";
    private Menu menu;
    private int selectionCount;
    private Date lastLoadedDate;
    private MessagesList messagesList;
    private  SessionManager sessionManager;
    private ArrayList<Integer> usersIds;
    private AppDatabase appDatabase;
    private User currentUser;

    public static void open(Context context, String parentMessageId,ArrayList<Integer> usersIds) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra("parentMessageId", parentMessageId);
        intent.putIntegerArrayListExtra("userIds",usersIds);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.addToStart(MessagesFixtures.getTextMessage(), true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        sessionManager = new SessionManager(this);
        appDatabase = AppDatabase.getDatabase(MessagesActivity.this);

        parentMessageId = getIntent().getStringExtra("parentMessageId");
        usersIds = getIntent().getIntegerArrayListExtra("userIds");
        loadCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messagesList = findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onSubmit(CharSequence input) {

        Log.d(TAG,"saving new message");


        Log.d(TAG,"parent message Id = "+parentMessageId);

        Message newMessage = new Message();
        newMessage.setId(UUID.randomUUID().toString());
        newMessage.setCreateDate(Calendar.getInstance().getTimeInMillis());
        newMessage.setCreatorId(Integer.parseInt(sessionManager.getUserUUID()));
        newMessage.setMessageBody(input.toString());

        if (parentMessageId == null) {
            newMessage.setParentMessageId("0");
            parentMessageId = newMessage.getId();
        }else{
            newMessage.setParentMessageId(parentMessageId);
        }

        newMessage.setSubject("");


        new AsyncTask<Message,Void,Void>(){

            @Override
            protected Void doInBackground(Message... newMessages) {

                appDatabase.messagesModelDao().addMessage(newMessages[0]);
                Log.d(TAG,"saving new message = "+new Gson().toJson(newMessages[0]));
                for(Integer userId:usersIds){
                    if(userId.equals(String.valueOf(sessionManager.getUserUUID())))
                        continue;
                    MessageRecipients messageRecipients = new MessageRecipients();
                    messageRecipients.setId(UUID.randomUUID().toString());
                    messageRecipients.setMessageId(newMessages[0].getId());
                    messageRecipients.setRead(false);
                    messageRecipients.setRecipientId(userId);

                    appDatabase.messageRecipientsModelDao().addRecipient(messageRecipients);

                    Log.d(TAG,"saving new message recipients = "+new Gson().toJson(messageRecipients));
                }



                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute(newMessage);


        IMessageDTO iMessageDTO = new IMessageDTO(newMessage.getId(),currentUser,newMessage.getMessageBody(),new Date(newMessage.getCreateDate()));
        messagesAdapter.addToStart(iMessageDTO, true);
        return true;
    }

    @Override
    public void onAddAttachments() {
        messagesAdapter.addToStart(MessagesFixtures.getImageMessage(), true);
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return getString(R.string.date_header_today);
        } else if (DateFormatter.isYesterday(date)) {
            return getString(R.string.date_header_yesterday);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }

    private void initAdapter() {
        this.messagesAdapter = new MessagesListAdapter<>(sessionManager.getUserUUID(), this.imageLoader);
        this.messagesAdapter.enableSelectionMode(this);
        this.messagesAdapter.setLoadMoreListener(this);
        this.messagesAdapter.setDateHeadersFormatter(this);
        messagesList.setAdapter(this.messagesAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                messagesAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                AppUtils.showToast(this, R.string.copied_message, true);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount == 0) {
            loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    protected void loadMessages() {
        MessageListViewModel messageListViewModel = ViewModelProviders.of(this).get(MessageListViewModel.class);
        messageListViewModel.getMessagesByThread(parentMessageId).observe(this, new Observer<List<MessageUserDTO>>() {
            @Override
            public void onChanged(List<MessageUserDTO> messages) {


//                ArrayList<IMessageDTO> IMessageDTOS = MessagesFixtures.getMessages(lastLoadedDate);
                ArrayList<IMessageDTO> IMessageDTOS = new ArrayList<>();
                for (MessageUserDTO messageUserDTO : messages) {
                    IMessageDTOS.add(toIMessageDTO(messageUserDTO));
                }

                if (IMessageDTOS.size() > 0)
                    lastLoadedDate = IMessageDTOS.get(IMessageDTOS.size() - 1).getCreatedAt();
                messagesAdapter.addToEnd(IMessageDTOS, false);
            }
        });
    }

    private MessagesListAdapter.Formatter<IMessageDTO> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<IMessageDTO>() {
            @Override
            public String format(IMessageDTO IMessageDTO) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(IMessageDTO.getCreatedAt());

                String text = IMessageDTO.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        IMessageDTO.getUser().getName(), text, createdAt);
            }
        };
    }

    public IMessageDTO toIMessageDTO(MessageUserDTO messageUserDTO) {
        User user = new User(String.valueOf(messageUserDTO.getUserId()), messageUserDTO.getFirstName() + " " + messageUserDTO.getSurname(), null, false);
        return new IMessageDTO(String.valueOf(messageUserDTO.getId()), user, messageUserDTO.getMessageBody(), new Date(messageUserDTO.getCreateDate()));
    }

    @SuppressLint("StaticFieldLeak")
    private void loadCurrentUser(){
        new AsyncTask<Void,Void,List<User>>(){

            @Override
            protected List<User> doInBackground(Void... voids) {
                OtherUsers user = appDatabase.usersModelDao().getUser(Integer.parseInt(sessionManager.getUserUUID()));
                currentUser = new User(String.valueOf(user.getId()),user.getFirstName()+" "+user.getSurname(),null,false);
                return null;
            }

            @Override
            protected void onPostExecute(List<User> users) {
                super.onPostExecute(users);
            }
        }.execute();
    }
}
