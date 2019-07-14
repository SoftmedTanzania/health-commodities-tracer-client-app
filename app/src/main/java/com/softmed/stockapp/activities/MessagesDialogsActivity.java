package com.softmed.stockapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.softmed.stockapp.dom.model.Dialog;
import com.softmed.stockapp.R;
import com.softmed.stockapp.utils.AppUtils;
import com.softmed.stockapp.fixtures.DialogsFixtures;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.Date;

public class MessagesDialogsActivity extends AppCompatActivity
        implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog>,DateFormatter.Formatter {
    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;

    public static void open(Context context) {
        context.startActivity(new Intent(context, MessagesDialogsActivity.class));
    }

    private DialogsList dialogsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_dialogs);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Glide.with(MessagesDialogsActivity.this).load(url).into(imageView);
            }
        };

        dialogsList = (DialogsList) findViewById(R.id.dialogsList);
        initAdapter();
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {
        AppUtils.showToast(
                this,
                dialog.getDialogName(),
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


    @Override
    public void onDialogClick(Dialog dialog) {
        MessagesActivity.open(this);
    }
}
