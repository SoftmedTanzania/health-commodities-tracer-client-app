package com.softmed.stockapp.activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.softmed.stockapp.R;
import com.softmed.stockapp.adapters.ContactAdapter;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.Message;
import com.softmed.stockapp.dom.entities.MessageRecipients;
import com.softmed.stockapp.dom.entities.OtherUsers;
import com.softmed.stockapp.utils.SessionManager;
import com.softmed.stockapp.viewmodels.ContactChooserViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ContactChooserActivity extends AppCompatActivity implements ContactAdapter.OnItemClickListener {
    private static final String TAG = ContactChooserActivity.class.getSimpleName();
    private RecyclerView contactsRecyclerView;
    private ChipGroup entryChipGroup, recipientsChipGroup;
    private BottomSheetBehavior sheetBehavior;
    private ExtendedFloatingActionButton composeFab;
    private TextInputLayout subjectInputLayout, messageInputLayout;
    private ArrayList<Integer> userIds;


    public static ContactChooserActivity newInstance() {
        return new ContactChooserActivity();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_chooser);
        entryChipGroup = findViewById(R.id.entry_chip_group);
        recipientsChipGroup = findViewById(R.id.recipients_entry_chip_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("New Message");

        RelativeLayout bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        ContactChooserViewModel mViewModel = ViewModelProviders.of(this).get(ContactChooserViewModel.class);
        AppDatabase db = AppDatabase.getDatabase(this);
        mViewModel.setAppDatabase(db);
        SessionManager session = new SessionManager(this);
        mViewModel.getContacts(Integer.parseInt(session.getUserUUID())).observe(this, otherUsers -> init(otherUsers));

        composeFab = findViewById(R.id.fab);
        composeFab.extend();


        composeFab.setOnClickListener(view -> {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                composeFab.setText("Close");
                findViewById(R.id.contactchooser).setAlpha(0.1f);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                composeFab.setText("Compose Message");

                findViewById(R.id.contactchooser).setAlpha(1f);
            }
        });


        // callback for do something
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        composeFab.setText("Close");
                        findViewById(R.id.contactchooser).setAlpha(0.1f);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        composeFab.setText("Compose Message");
                        findViewById(R.id.contactchooser).setAlpha(1f);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        subjectInputLayout = findViewById(R.id.subject_input_layout);
        messageInputLayout = findViewById(R.id.message_text_input_layout);

        findViewById(R.id.send_button).setOnClickListener(view -> {
            if (checkInputs()) {

                Message newMessage = new Message();
                newMessage.setId(UUID.randomUUID().toString());
                newMessage.setCreateDate(Calendar.getInstance().getTimeInMillis());
                newMessage.setCreatorId(Integer.parseInt(session.getUserUUID()));
                newMessage.setMessageBody(messageInputLayout.getEditText().getText().toString());
                newMessage.setParentMessageId("0");
                newMessage.setSubject(subjectInputLayout.getEditText().getText().toString());

                userIds = new ArrayList<>();
                for (int i = 0; i < entryChipGroup.getChildCount(); i++) {
                    userIds.add(Integer.parseInt(entryChipGroup.getChildAt(i).getTag().toString()));
                }


                new AsyncTask<Message, Void, Void>() {

                    @Override
                    protected Void doInBackground(Message... newMessages) {
                        AppDatabase appDatabase = AppDatabase.getDatabase(ContactChooserActivity.this);

                        appDatabase.messagesModelDao().addMessage(newMessages[0]);
                        Log.d(TAG, "saving new message = " + new Gson().toJson(newMessages[0]));
                        for (Integer userId : userIds) {
                            if (userId.equals(String.valueOf(session.getUserUUID())))
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


                        MessagesActivity.open(ContactChooserActivity.this, newMessage.getId(), userIds);
                    }
                }.execute(newMessage);
            }

        });


    }

    private void init(List<OtherUsers> contacts) {
        contactsRecyclerView = findViewById(R.id.whatsapp_recycler);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsRecyclerView.setHasFixedSize(true);

        ContactAdapter mContactAdapter = new ContactAdapter(this, contacts, ContactChooserActivity.this);
        contactsRecyclerView.setAdapter(mContactAdapter);
    }

    private Chip getChip(String text, String id) {
        final Chip chip = new Chip(this);
        chip.setTag(id);
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.view_contact_chip));
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(text);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Chip c = entryChipGroup.findViewWithTag(chip.getTag());
                Chip c2 = recipientsChipGroup.findViewWithTag(chip.getTag());

                entryChipGroup.removeView(c);
                recipientsChipGroup.removeView(c2);
            }
        });
        return chip;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onItemClick(OtherUsers user) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return AppDatabase.getDatabase(ContactChooserActivity.this).messagesModelDao().getParentMessageId(user.getId());
            }

            @Override
            protected void onPostExecute(String parentMessageId) {
                super.onPostExecute(parentMessageId);

                if (entryChipGroup.findViewWithTag(String.valueOf(user.getId())) == null) {
                    Chip chip = getChip(user.getFirstName() + " " + user.getSurname(), String.valueOf(user.getId()));
                    entryChipGroup.addView(chip);

                    Chip chip2 = getChip(user.getFirstName() + " " + user.getSurname(), String.valueOf(user.getId()));
                    recipientsChipGroup.addView(chip2);
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_message, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_action) {
            finish();

            overridePendingTransition(R.anim.none, R.anim.slide_down);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean checkInputs() {
        if (entryChipGroup.getChildCount() == 0) {
            return false;
        } else if (subjectInputLayout.getEditText().getText().toString().equals("")) {
            subjectInputLayout.getEditText().setError("Please fill the subject");
            return false;
        } else if (messageInputLayout.getEditText().getText().toString().equals("")) {
            messageInputLayout.getEditText().setError("Please fill the message");
            return false;
        }

        return true;
    }
}
