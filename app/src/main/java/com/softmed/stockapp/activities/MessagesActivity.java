package com.softmed.stockapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.softmed.stockapp.R;
import com.softmed.stockapp.adapters.MessagesListAdapter;
import com.softmed.stockapp.customviews.MessageInput;
import com.softmed.stockapp.customviews.custom.message.viewholders.CustomIncomingImageMessageViewHolder;
import com.softmed.stockapp.customviews.custom.message.viewholders.CustomIncomingTextMessageViewHolder;
import com.softmed.stockapp.customviews.custom.message.viewholders.CustomOutcomingImageMessageViewHolder;
import com.softmed.stockapp.customviews.custom.message.viewholders.CustomOutcomingTextMessageViewHolder;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.dto.MessageUserDTO;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.dom.entities.OtherUsers;
import com.softmed.stockapp.dom.model.IMessageDTO;
import com.softmed.stockapp.dom.model.IMessageUser;
import com.softmed.stockapp.utils.AppUtils;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.viewmodels.MessageListViewModel;
import com.softmed.stockapp.workers.SendMessageRecipientWorker;
import com.softmed.stockapp.workers.SendMessagesWorker;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
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
    private SessionManager sessionManager;
    private ArrayList<Integer> usersIds;
    private ArrayList<String> userNames;
    private AppDatabase appDatabase;
    private IMessageUser currentIMessageUser;
    private boolean isFIrstLoad = true;
    private Typeface muliBoldTypeface;

    public static void open(Context context, String parentMessageId, ArrayList<Integer> usersIds, ArrayList<String> userNames) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra("parentMessageId", parentMessageId);
        intent.putIntegerArrayListExtra("userIds", usersIds);
        intent.putStringArrayListExtra("userNames", userNames);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMessages();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        sessionManager = new SessionManager(this);
        appDatabase = AppDatabase.getDatabase(MessagesActivity.this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        muliBoldTypeface = ResourcesCompat.getFont(MessagesActivity.this, R.font.muli_bold);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {

                Log.d(TAG, "Image Text = " + url);
                Log.d(TAG, "Payload = " + new Gson().toJson(payload));
                TextDrawable drawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .useFont(muliBoldTypeface)
                        .fontSize(32) /* size in px */
                        .bold()
                        .toUpperCase()
                        .endConfig()
                        .buildRect(url, getResources().getColor(R.color.color_primary));
                imageView.setImageDrawable(drawable);
            }
        };

        parentMessageId = getIntent().getStringExtra("parentMessageId");

        usersIds = getIntent().getIntegerArrayListExtra("userIds");
        userNames = getIntent().getStringArrayListExtra("userNames");
        loadCurrentUser();

        messagesList = findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);


        //Initializing toolbar information
        ImageView avatar = findViewById(R.id.user_avatar);
        TextView subjectView = findViewById(R.id.subject);
        TextView usersView = findViewById(R.id.users);


        if (usersIds.size() == 1) {
            usersView.setText(userNames.get(0));
            String[] name = userNames.get(0).split(" ");
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(muliBoldTypeface)
                    .fontSize(32) /* size in px */
                    .bold()
                    .toUpperCase()
                    .endConfig()
                    .buildRound(name[0].charAt(0) + "" + name[1].charAt(0), getResources().getColor(R.color.color_primary));
            avatar.setImageDrawable(drawable);

        } else {
            Glide.with(MessagesActivity.this).load(R.drawable.ic_round_supervised_user_circle_24px).into(avatar);

            for (String username : userNames) {
                String firstName = username.split(" ")[0];
                usersView.append(firstName + ", ");
            }
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return appDatabase.messagesModelDao().getMessageById(parentMessageId).getSubject();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                subjectView.setText(s);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onSubmit(CharSequence input) {
        Log.d(TAG, "saving new message");
        Log.d(TAG, "parent message Id = " + parentMessageId);

        Message newMessage = new Message();
        newMessage.setId(UUID.randomUUID().toString());
        newMessage.setCreateDate(Calendar.getInstance().getTimeInMillis());
        newMessage.setCreatorId(Integer.parseInt(sessionManager.getUserUUID()));
        newMessage.setMessageBody(input.toString());
        newMessage.setUuid(newMessage.getId());

        if (parentMessageId == null) {
            newMessage.setParentMessageId("0");
            parentMessageId = newMessage.getId();
        } else {
            newMessage.setParentMessageId(parentMessageId);
        }

        newMessage.setSubject("");


        new AsyncTask<Message, Void, Void>() {

            @Override
            protected Void doInBackground(Message... newMessages) {
                Message parentMessage = appDatabase.messagesModelDao().getParentMessageById(newMessages[0].getParentMessageId());

                Message messageToBeSaved = newMessages[0];
                messageToBeSaved.setSubject(parentMessage.getSubject());

                messageToBeSaved.setParentMessageId(parentMessage.getId());

                appDatabase.messagesModelDao().addMessage(messageToBeSaved);
                Log.d(TAG, "saving new message = " + new Gson().toJson(newMessages[0]));
                for (Integer userId : usersIds) {
                    if (userId.equals(String.valueOf(sessionManager.getUserUUID())))
                        continue;
                    MessageRecipients messageRecipients = new MessageRecipients();
                    messageRecipients.setId(UUID.randomUUID().toString());
                    messageRecipients.setMessageId(newMessages[0].getId());
                    messageRecipients.setRead(false);
                    messageRecipients.setRecipientId(userId);

                    appDatabase.messageRecipientsModelDao().addRecipient(messageRecipients);

                    Log.d(TAG, "saving new message recipients = " + new Gson().toJson(messageRecipients));
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                Constraints networkConstraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                OneTimeWorkRequest sendMessage = new OneTimeWorkRequest.Builder(SendMessagesWorker.class)
                        .setConstraints(networkConstraints)
                        .setInputData(
                                new Data.Builder()
                                        .putString("messageId", newMessage.getId())
                                        .build()
                        )
                        .build();

                WorkManager.getInstance().enqueue(sendMessage);

            }
        }.execute(newMessage);


        IMessageDTO iMessageDTO = new IMessageDTO(newMessage.getId(), currentIMessageUser, newMessage.getMessageBody(), new Date(newMessage.getCreateDate()));
        messagesAdapter.addToStart(iMessageDTO, true);
        return true;
    }

    @Override
    public void onAddAttachments() {
       //Implement
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

        Log.d(TAG, "userIds count = " + usersIds.size());

        CustomIncomingTextMessageViewHolder.Payload payload = new CustomIncomingTextMessageViewHolder.Payload();

        payload.avatarClickListener = new CustomIncomingTextMessageViewHolder.OnAvatarClickListener() {
            @Override
            public void onAvatarClick() {
                Toast.makeText(MessagesActivity.this,
                        "Text message avatar clicked", Toast.LENGTH_SHORT).show();
            }
        };


        int incomingImageMessageLayout;
        int incomingTextMessageLayout;

        if (usersIds.size() == 1) {
            incomingImageMessageLayout = R.layout.item_incoming_image_message;
            incomingTextMessageLayout = R.layout.item_incoming_text_message;
        } else {

            incomingImageMessageLayout = R.layout.item_incoming_group_image_message;
            incomingTextMessageLayout = R.layout.item_incoming_group_text_message;
        }

        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        incomingTextMessageLayout,
                        payload)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        com.stfalcon.chatkit.R.layout.item_outcoming_text_message)
                .setIncomingImageConfig(
                        CustomIncomingImageMessageViewHolder.class,
                        incomingImageMessageLayout)
                .setOutcomingImageConfig(
                        CustomOutcomingImageMessageViewHolder.class,
                        com.stfalcon.chatkit.R.layout.item_outcoming_image_message);


        this.messagesAdapter = new MessagesListAdapter<>(sessionManager.getUserUUID(), holdersConfig, this.imageLoader);
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

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:

                final List<IMessageDTO> selectedMessages = messagesAdapter.getSelectedMessages();

                List<String> selectedMessagesIds = new ArrayList<>();
                for (IMessageDTO messageDTO : selectedMessages) {
                    selectedMessagesIds.add(messageDTO.getId());
                }

                String[] selectedMessagesArray = selectedMessagesIds.toArray(new String[selectedMessagesIds.size()]);

                Log.d(TAG, "messages to be deleted = " + new Gson().toJson(selectedMessagesArray));

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... aVoid) {
                        for (IMessageDTO message : selectedMessages) {

                            if (message.getUser().getId().equals(sessionManager.getUserUUID())) {
                                //TODO fix the deletion of  messages from the mailbox
//                                appDatabase.messagesModelDao().deleteMessage(true,message.getId());
                            } else {

                                Log.d(TAG, "Deleting message with id = " + new Gson().toJson(selectedMessagesArray));
                                appDatabase.messageRecipientsModelDao().deleteMessageFromMailBox(true, message.getId(), sessionManager.getUserUUID());
                            }


                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
//TODO implement deletion of message

//                        Constraints networkConstraints = new Constraints.Builder()
//                                .setRequiredNetworkType(NetworkType.CONNECTED)
//                                .build();
//                        OneTimeWorkRequest deleteMessage = new OneTimeWorkRequest.Builder(SendMessagesWorker.class)
//                                .setConstraints(networkConstraints)
//                                .setInputData(
//                                        new Data.Builder()
//                                                .putStringArray("messageId", selectedMessagesArray)
//                                                .build()
//                                )
//                                .build();
//
//                        WorkManager.getInstance().enqueue(deleteMessage);
                    }
                }.execute();


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
        Log.i(TAG, "onLoadMore: " + page + " " + totalItemsCount);
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
//        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    @SuppressLint("StaticFieldLeak")
    protected void loadMessages() {

        Log.d(TAG, "user UUID = " + sessionManager.getUserUUID());
        Log.d(TAG, "Loading more with parentID = " + parentMessageId);
        MessageListViewModel messageListViewModel = ViewModelProviders.of(this).get(MessageListViewModel.class);
        messageListViewModel.getMessageByThread(parentMessageId, sessionManager.getUserUUID()).observe(MessagesActivity.this, new Observer<List<com.softmed.stockapp.dom.dto.MessageUserDTO>>() {
            @Override
            public void onChanged(List<MessageUserDTO> messageUserDTOS) {
                Log.d(TAG, "Something changed");
                Log.d(TAG, "Messages = " + new Gson().toJson(messageUserDTOS));

                if (messageUserDTOS != null) {
                    ArrayList<IMessageDTO> IMessageDTOS = new ArrayList<>();
                    for (MessageUserDTO messageUserDTO : messageUserDTOS) {
                        if (messageUserDTO.getUuid().equals(parentMessageId) && !messageUserDTO.getId().equals(parentMessageId)) {
                            parentMessageId = messageUserDTO.getId();
                            loadMessages();
                            break;
                        }

                        IMessageDTOS.add(toIMessageDTO(messageUserDTO));
                        if (!isFIrstLoad) {
                            if (messagesAdapter.getMessagePositionById(messageUserDTO.getUuid()) == -1) {
                                messagesAdapter.addToStart(toIMessageDTO(messageUserDTO), true);
                            } else {
                                messagesAdapter.update(toIMessageDTO(messageUserDTO));
                            }
                        }
                    }

                    if (IMessageDTOS.size() > 0)
                        lastLoadedDate = IMessageDTOS.get(IMessageDTOS.size() - 1).getCreatedAt();

                    if (isFIrstLoad)
                        messagesAdapter.addToEnd(IMessageDTOS, false);
                    isFIrstLoad = false;
                    updateIsReadStatus(messageUserDTOS);

                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void updateIsReadStatus(List<MessageUserDTO> messageUserDTOS) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                Log.d(TAG, "message recipients = " + new Gson().toJson(appDatabase.messageRecipientsModelDao().getAllMessageRecipients()));

                List<String> updatedMessageId = new ArrayList<>();
                for (MessageUserDTO messageUserDTO : messageUserDTOS) {
                    int updateCount = appDatabase.messageRecipientsModelDao().updateIsReadStatus(true, messageUserDTO.getId(), Integer.parseInt(sessionManager.getUserUUID()));
                    Log.d(TAG, "Updated is read count = " + updateCount);

                    if (updateCount > 0) {
                        updatedMessageId.add(messageUserDTO.getId());
                    }
                }
                return updatedMessageId;
            }

            @Override
            protected void onPostExecute(List<String> messageIds) {
                super.onPostExecute(messageIds);

                Log.d(TAG, "message Ids to be update = " + new Gson().toJson(messageIds));
                for (String messageId : messageIds) {
                    Constraints networkConstraints = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();

                    OneTimeWorkRequest sendMessage = new OneTimeWorkRequest.Builder(SendMessageRecipientWorker.class)
                            .setConstraints(networkConstraints)
                            .setInputData(
                                    new Data.Builder()
                                            .putString("messageId", messageId)
                                            .build()
                            )
                            .build();

                    WorkManager.getInstance().enqueue(sendMessage);
                }

            }
        }.execute();
    }

    private MessagesListAdapter.Formatter<IMessageDTO> getMessageStringFormatter() {
        return IMessageDTO -> {
            String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                    .format(IMessageDTO.getCreatedAt());

            String text = IMessageDTO.getText();
            if (text == null) text = "[attachment]";

            return String.format(Locale.getDefault(), "%s: %s (%s)",
                    IMessageDTO.getUser().getName(), text, createdAt);
        };
    }

    public IMessageDTO toIMessageDTO(com.softmed.stockapp.dom.dto.MessageUserDTO messageUserDTO) {
        IMessageUser user = new IMessageUser(String.valueOf(messageUserDTO.getUserId()), messageUserDTO.getFirstName() + " " + messageUserDTO.getSurname(),
                messageUserDTO.getFirstName().charAt(0) + "" + messageUserDTO.getSurname().charAt(0), false);
        return new IMessageDTO(String.valueOf(messageUserDTO.getUuid()), user, messageUserDTO.getMessageBody(), new Date(messageUserDTO.getCreateDate()));
    }

    @SuppressLint("StaticFieldLeak")
    private void loadCurrentUser() {
        new AsyncTask<Void, Void, List<IMessageUser>>() {
            @Override
            protected List<IMessageUser> doInBackground(Void... voids) {
                OtherUsers user = appDatabase.usersModelDao().getUser(Integer.parseInt(sessionManager.getUserUUID()));
                currentIMessageUser = new IMessageUser(String.valueOf(user.getId()), user.getFirstName() + " " + user.getSurname(), null, false);
                return null;
            }

            @Override
            protected void onPostExecute(List<IMessageUser> IMessageUsers) {
                super.onPostExecute(IMessageUsers);
            }
        }.execute();
    }
}
