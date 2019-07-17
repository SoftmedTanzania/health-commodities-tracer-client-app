package com.softmed.stockapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.softmed.stockapp.R;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.dom.entities.OtherUsers;
import com.softmed.stockapp.dom.model.IMessageDTO;
import com.softmed.stockapp.dom.model.MessageDialog;
import com.softmed.stockapp.dom.model.User;
import com.softmed.stockapp.fixtures.DialogsFixtures;
import com.softmed.stockapp.utils.AppUtils;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.viewmodels.MessageListViewModel;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MessagesDialogsActivity extends AppCompatActivity
        implements DialogsListAdapter.OnDialogClickListener<MessageDialog>,
        DialogsListAdapter.OnDialogLongClickListener<MessageDialog>, DateFormatter.Formatter {
    private static final String TAG = MessagesDialogsActivity.class.getSimpleName();
    protected ImageLoader imageLoader;
    protected DialogsListAdapter<MessageDialog> dialogsAdapter;
    private DialogsList dialogsList;
    private AppDatabase baseDatabase;
    private SessionManager session;

    public static void open(Context context) {
        context.startActivity(new Intent(context, MessagesDialogsActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_dialogs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(this);


        baseDatabase = AppDatabase.getDatabase(this);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {

                Typeface muliBoldTypeface = ResourcesCompat.getFont(MessagesDialogsActivity.this, R.font.muli_bold);


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

        dialogsList = findViewById(R.id.dialogsList);
        MessageListViewModel messageListViewModel = ViewModelProviders.of(this).get(MessageListViewModel.class);
        messageListViewModel.getParentMessages().observe(MessagesDialogsActivity.this, new Observer<List<Message>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onChanged(List<Message> messages) {

                Log.d(TAG, "Message Schedules = " + new Gson().toJson(messages));

                new AsyncTask<Void, Void, List<MessageDialog>>() {

                    @Override
                    protected List<MessageDialog> doInBackground(Void... voids) {
                        List<Message> latestMessages = new ArrayList<>();
                        for (Message message : messages) {
                            latestMessages.add(baseDatabase.messagesModelDao().getLatestMessages(message.getId()));
                        }

                        Collections.sort(latestMessages, new Comparator<Message>() {
                            @Override
                            public int compare(Message u1, Message u2) {
                                Date date1 = new Date(u1.getCreateDate());
                                Date date2 = new Date(u2.getCreateDate());
                                return date2.compareTo(date1);
                            }
                        });

                        ArrayList<MessageDialog> chats = new ArrayList<>();
                        for (Message message : latestMessages) {
                            chats.add(getDialog(message));
                        }


                        return chats;
                    }

                    @Override
                    protected void onPostExecute(List<MessageDialog> messageDialogs) {
                        super.onPostExecute(messageDialogs);


                        dialogsAdapter = new DialogsListAdapter<>(imageLoader);
                        dialogsAdapter.setItems(messageDialogs);
//                        dialogsAdapter.setItems(DialogsFixtures.getDialogs());
                        dialogsAdapter.setOnDialogClickListener(MessagesDialogsActivity.this);
                        dialogsAdapter.setOnDialogLongClickListener(MessagesDialogsActivity.this);
                        dialogsAdapter.setDatesFormatter(MessagesDialogsActivity.this);
                        dialogsList.setAdapter(dialogsAdapter);


                    }
                }.execute();

            }
        });

        findViewById(R.id.message_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessagesDialogsActivity.this, ContactChooserActivity.class));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_up);
            }
        });
    }

    @Override
    public void onDialogLongClick(MessageDialog messageDialog) {
        AppUtils.showToast(
                this,
                messageDialog.getDialogName(),
                false);
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return DateFormatter.format(date, DateFormatter.Template.TIME);
        } else if (DateFormatter.isYesterday(date)) {
            return getString(R.string.date_header_yesterday);
        } else if (DateFormatter.isCurrentYear(date)) {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }

    private void initAdapter() {
        this.dialogsAdapter = new DialogsListAdapter<>(this.imageLoader);
        this.dialogsAdapter.setItems(DialogsFixtures.getDialogs());
        this.dialogsAdapter.setOnDialogClickListener(this);
        this.dialogsAdapter.setOnDialogLongClickListener(this);
        this.dialogsAdapter.setDatesFormatter(this);
        dialogsList.setAdapter(this.dialogsAdapter);
    }


    private MessageDialog getDialog(Message message) {

        Log.d(TAG, "creator id = " + message.getCreatorId());

        ArrayList<User> users = new ArrayList<>();
        if (message.getCreatorId() != Integer.parseInt(session.getUserUUID())) {
            users.add(getUser(message.getCreatorId()));
        }

        List<MessageRecipients> messageRecipients = baseDatabase.messageRecipientsModelDao().getAllMessageRecipientsByMessageId(message.getId());

        for (MessageRecipients messageRecipient : messageRecipients) {
            int recipientId = messageRecipient.getRecipientId();

            Log.d(TAG, "recipient id = " + recipientId);

            if (recipientId != Integer.parseInt(session.getUserUUID())) {
                users.add(getUser(recipientId));
            }
        }


        return new MessageDialog(
                message.getParentMessageId(),
                users.size() > 1 ? getGroupNames(users) : users.get(0).getName(),
                users.size() > 1 ? getGroupInitials(users) : getInitials(users.get(0)),
                users, getLastMessage(message), 0, message.getParentMessageId());


    }

    private String getGroupNames(ArrayList<User> users ){
        String names = "";
        for(User user:users){
            names.concat(user.getName());
            names.concat(" ");
        }
        return names;

    }

    private String getInitials(User user){
        String[] names = user.getName().split(" ");
        return names[0].charAt(0) + "" + names[1].charAt(0);

    }



    private String getGroupInitials(ArrayList<User> users){
        String names="";
        for(User user:users){
            String[] namesArray = user.getName().split(" ");
            names = namesArray[0].charAt(0) + "" + namesArray[1].charAt(0);
        }

        return names;

    }


    private IMessageDTO getLastMessage(Message message) {
        return new IMessageDTO(
                message.getId(),
                getUser(message.getCreatorId()),
                message.getMessageBody(),
                new Date(message.getCreateDate()));
    }

    private User getUser(int userId) {
        OtherUsers otherUsers = baseDatabase.usersModelDao().getUser(userId);
        return new User(
                String.valueOf(otherUsers.getId()),
                otherUsers.getFirstName() + " " + otherUsers.getSurname(),
                otherUsers.getFirstName().charAt(0) + "" + otherUsers.getSurname().charAt(0),
                false);
    }


    @Override
    public void onDialogClick(MessageDialog messageDialog) {
        ArrayList<Integer> userIds = new ArrayList<>();
        for (User user : messageDialog.getUsers()) {
            userIds.add(Integer.parseInt(user.getId()));
        }
        MessagesActivity.open(this, messageDialog.getParentMessageId(), userIds);
    }
}
