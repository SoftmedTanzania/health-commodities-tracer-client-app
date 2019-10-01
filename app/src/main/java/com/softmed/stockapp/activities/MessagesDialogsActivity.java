package com.softmed.stockapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.softmed.stockapp.R;
import com.softmed.stockapp.adapters.DialogsListAdapter;
import com.softmed.stockapp.customViews.CustomDialogsList;
import com.softmed.stockapp.customViews.CustomScrollView;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.dom.entities.OtherUsers;
import com.softmed.stockapp.dom.model.IMessageDTO;
import com.softmed.stockapp.dom.model.IMessageUser;
import com.softmed.stockapp.dom.model.MessageDialog;
import com.softmed.stockapp.utils.AppUtils;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.viewmodels.MessageListViewModel;
import com.softmed.stockapp.workers.DeleteMessagesWorker;
import com.softmed.stockapp.workers.SendMessagesWorker;
import com.stfalcon.chatkit.commons.ImageLoader;
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
    protected DialogsListAdapter dialogsAdapter;
    private CustomDialogsList dialogsList;
    private AppDatabase baseDatabase;
    private SessionManager session;
    private ExtendedFloatingActionButton floatingActionButton;
    private View emptyState;

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

                if (url.equals("group")) {
                    Glide.with(MessagesDialogsActivity.this).load(R.drawable.ic_round_supervised_user_circle_24px).into(imageView);
                } else {
                    Typeface muliBoldTypeface = ResourcesCompat.getFont(MessagesDialogsActivity.this, R.font.muli_bold);

                    TextDrawable drawable = null;

                    int spValue = 18;
                    if (payload != null)
                        spValue = Integer.parseInt(payload.toString());


                    drawable = TextDrawable.builder()
                            .beginConfig()
                            .textColor(Color.WHITE)
                            .useFont(muliBoldTypeface)
                            .fontSize(AppUtils.spToPx(spValue, MessagesDialogsActivity.this)) /* size in px */
                            .bold()
                            .toUpperCase()
                            .endConfig()
                            .buildRound(url, getResources().getColor(R.color.color_primary));


                    imageView.setImageDrawable(drawable);
                }
            }
        };

        emptyState = findViewById(R.id.empty_state);
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

                            if (message.getCreatorId() == Integer.parseInt(session.getUserUUID()) && message.isTrashedByCreator()) {
                                continue;
                            } else if (baseDatabase.messageRecipientsModelDao().isMessageDeletedFromMailbox(message.getId(), true, Integer.parseInt(session.getUserUUID())) > 0) {
                                continue;
                            }

                            Message m = baseDatabase.messagesModelDao().getLatestMessages(message.getId());
                            int unreadCount = baseDatabase.messageRecipientsModelDao().getUnreadMessageCountByParentMessageId(message.getId(), false, Integer.parseInt(session.getUserUUID()));
                            //Temporally using this field of unsync status cz its not needed at this point to store unread message count
                            //More elegant solutions can be implemented later on
                            m.setSyncStatus(unreadCount);


                            latestMessages.add(m);
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
                            MessageDialog messageDialog = getDialog(message);
                            if (messageDialog != null)
                                chats.add(getDialog(message));
                        }


                        return chats;
                    }

                    @Override
                    protected void onPostExecute(List<MessageDialog> messageDialogs) {
                        super.onPostExecute(messageDialogs);

                        if (messageDialogs.size() == 0) {
                            emptyState.setVisibility(View.VISIBLE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                        }

                        dialogsAdapter = new DialogsListAdapter(R.layout.item_message_dialog, DialogsListAdapter.DialogViewHolder.class, imageLoader);
                        dialogsAdapter.setItems(messageDialogs);
                        dialogsAdapter.setOnDialogClickListener(MessagesDialogsActivity.this);
                        dialogsAdapter.setOnDialogLongClickListener(MessagesDialogsActivity.this);
                        dialogsAdapter.setDatesFormatter(MessagesDialogsActivity.this);
                        dialogsList.setAdapter(dialogsAdapter, false);


                    }
                }.execute();

            }
        });

        floatingActionButton = findViewById(R.id.message_icon);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessagesDialogsActivity.this, ComposeNewMessageActivity.class));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_up);
            }
        });


        dialogsList.setMyScrollChangeListener(new CustomScrollView.OnMyScrollChangeListener() {
            @Override
            public void onScrollUp() {
                //Toast.makeText(getActivity(), "Scrolling up", Toast.LENGTH_SHORT).show();
                Log.d("scroll", "up");
                floatingActionButton.extend(true);

            }

            @Override
            public void onScrollDown() {
                // Toast.makeText(getActivity(), "Scrolling down", Toast.LENGTH_SHORT).show();
                Log.d("scroll", "down");

                floatingActionButton.shrink(true);

            }
        });
    }

    @Override
    public void onDialogLongClick(MessageDialog messageDialog) {
//        AppUtils.showToast(
//                this,
//                messageDialog.getDialogName(),
//                false);

        AlertDialog diaBox = AskOption( messageDialog);
        diaBox.show();


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

    private MessageDialog getDialog(Message message) {

        Log.d(TAG, "creator id = " + message.getCreatorId());

        ArrayList<IMessageUser> IMessageUsers = new ArrayList<>();
        if (message.getCreatorId() != Integer.parseInt(session.getUserUUID())) {
            IMessageUsers.add(getUser(message.getCreatorId()));
        }

        List<MessageRecipients> messageRecipients = baseDatabase.messageRecipientsModelDao().getAllMessageRecipientsByMessageId(message.getId());

        for (MessageRecipients messageRecipient : messageRecipients) {
            int recipientId = messageRecipient.getRecipientId();

            Log.d(TAG, "recipient id = " + recipientId);

            if (recipientId != Integer.parseInt(session.getUserUUID())) {
                IMessageUsers.add(getUser(recipientId));
            }
        }

        try {

            Log.d(TAG, "ImessageUsers = " + new Gson().toJson(IMessageUsers));
            return new MessageDialog(
                    message.getParentMessageId().equals("0") ? message.getId() : message.getParentMessageId(),
                    IMessageUsers.size() > 1 ? message.getSubject() : IMessageUsers.get(0).getName() + " - " + message.getSubject(),
                    IMessageUsers.size() > 1 ? "group" : getInitials(IMessageUsers.get(0)),
                    IMessageUsers,
                    getLastMessage(message),
                    message.getSyncStatus(),
                    message.getParentMessageId().equals("0") ? message.getId() : message.getParentMessageId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


    private String getInitials(IMessageUser IMessageUser) {
        String[] names = IMessageUser.getName().split(" ");
        return names[0].charAt(0) + "" + names[1].charAt(0);

    }


    private IMessageDTO getLastMessage(Message message) {

        Log.d(TAG, "create date timestamp = " + message.getCreateDate());

        Date d = new Date(message.getCreateDate());
        Log.d(TAG, "created date = " + d.toString());

        return new IMessageDTO(
                message.getId(),
                getUser(message.getCreatorId()),
                message.getMessageBody(),
                new Date(message.getCreateDate()));
    }

    private IMessageUser getUser(int userId) {
        OtherUsers otherUsers = baseDatabase.usersModelDao().getUser(userId);
        return new IMessageUser(
                String.valueOf(otherUsers.getId()),
                otherUsers.getFirstName() + " " + otherUsers.getSurname(),
                otherUsers.getFirstName().charAt(0) + "" + otherUsers.getSurname().charAt(0),
                false);
    }


    @Override
    public void onDialogClick(MessageDialog messageDialog) {
        ArrayList<Integer> userIds = new ArrayList<>();
        for (IMessageUser IMessageUser : messageDialog.getUsers()) {
            userIds.add(Integer.parseInt(IMessageUser.getId()));
        }


        ArrayList<String> userNames = new ArrayList<>();
        for (IMessageUser IMessageUser : messageDialog.getUsers()) {
            userNames.add(IMessageUser.getName());
        }

        Log.d(TAG, "Message DIalog = " + new Gson().toJson(messageDialog));

        String parentMessageId;
        if (messageDialog.getParentMessageId().equals("0"))
            parentMessageId = messageDialog.getId();
        else
            parentMessageId = messageDialog.getParentMessageId();


        Log.d(TAG, "Parent Message Id = " + parentMessageId);
        MessagesActivity.open(this, parentMessageId, userIds, userNames);
    }


    private AlertDialog AskOption(MessageDialog messageDialog)
    {
        AlertDialog myQuittingDialogBox =new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Message Thread")
                .setMessage("Do you want to Delete this Thread?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code

                        new AsyncTask<Void, Void, Boolean>() {

                            @Override
                            protected Boolean doInBackground(Void... aVoid) {

                                Log.d(TAG, "Message Dialog = " + new Gson().toJson(messageDialog));

                                Message parentMessage = baseDatabase.messagesModelDao().getParentMessageById(messageDialog.getParentMessageId());

                                boolean isTrashedByCreator ;
                                //if the message thread was not created by the user then delete the message from parent message recipient and set this value to false

                                if (parentMessage.getCreatorId() == Integer.parseInt(session.getUserUUID())) {
                                    baseDatabase.messagesModelDao().deleteMessageFromMailBox(true, parentMessage.getId(), session.getUserUUID());
                                    isTrashedByCreator = true;
                                } else {
                                    baseDatabase.messageRecipientsModelDao().deleteMessageFromMailBox(true, parentMessage.getId(), session.getUserUUID());

                                    //delete the message from parent message recipient and set this value to false

                                    isTrashedByCreator = false;
                                }

                                return isTrashedByCreator;
                            }

                            @Override
                            protected void onPostExecute(Boolean isTrashedByCreator) {
                                super.onPostExecute(isTrashedByCreator);

                                Constraints networkConstraints = new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build();

                                OneTimeWorkRequest deleteMessage = new OneTimeWorkRequest.Builder(DeleteMessagesWorker.class)
                                        .setConstraints(networkConstraints)
                                        .setInputData(
                                                new Data.Builder()
                                                        .putString("messageId", messageDialog.getParentMessageId())
                                                        .putBoolean("isTrashedByCreator",isTrashedByCreator)
                                                        .build()
                                        )
                                        .build();


                        WorkManager.getInstance().enqueue(deleteMessage);

                            }
                        }.execute();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

}
