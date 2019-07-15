package com.softmed.stockapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.softmed.stockapp.R;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.OtherUsers;
import com.softmed.stockapp.dom.model.IMessageDTO;
import com.softmed.stockapp.dom.model.MessageDialog;
import com.softmed.stockapp.dom.model.User;
import com.softmed.stockapp.fixtures.DialogsFixtures;
import com.softmed.stockapp.utils.AppUtils;
import com.softmed.stockapp.viewmodels.MessageListViewModel;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
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

    public static void open(Context context) {
        context.startActivity(new Intent(context, MessagesDialogsActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_dialogs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        baseDatabase = AppDatabase.getDatabase(this);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Glide.with(MessagesDialogsActivity.this).load(url).into(imageView);
            }
        };

        dialogsList = (DialogsList) findViewById(R.id.dialogsList);
        MessageListViewModel messageListViewModel = ViewModelProviders.of(this).get(MessageListViewModel.class);
        messageListViewModel.getMessageThreads().observe(MessagesDialogsActivity.this, new Observer<List<Message>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onChanged(List<Message> messages) {

                new AsyncTask<Void, Void, List<MessageDialog>>() {

                    @Override
                    protected List<MessageDialog> doInBackground(Void... voids) {

                        ArrayList<MessageDialog> chats = new ArrayList<>();
                        for (Message message : messages) {
                            chats.add(getDialog(message));
                        }

                        return chats;
                    }

                    @Override
                    protected void onPostExecute(List<MessageDialog> messageDialogs) {
                        super.onPostExecute(messageDialogs);


                        dialogsAdapter = new DialogsListAdapter<>(imageLoader);
                        dialogsAdapter.setItems(DialogsFixtures.getDialogs());
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
        ArrayList<User> users = new ArrayList<>();
        users.add(getUser(message.getCreatorId()));
        return new MessageDialog(
                String.valueOf(message.getParentMessageId()),
                users.get(0).getName(),
                "",
                users, getLastMessage(message), 0);
    }

    private IMessageDTO getLastMessage(Message message) {
        return new IMessageDTO(
                String.valueOf(message.getId()),
                getUser(message.getCreatorId()),
                message.getMessageBody(),
                new Date(message.getCreateDate()));
    }

    private User getUser(int userId) {
        OtherUsers otherUsers = baseDatabase.usersModelDao().getUser(userId);
        return new User(
                String.valueOf(otherUsers.getId()),
                otherUsers.getFirstName() + " " + otherUsers.getMiddleName() + " " + otherUsers.getSurname(),
                "",
                false);
    }


    @Override
    public void onDialogClick(MessageDialog messageDialog) {
        MessagesActivity.open(this);
    }
}
