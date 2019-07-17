package com.softmed.stockapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.softmed.stockapp.R;
import com.softmed.stockapp.activities.MessagesActivity;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.OtherUsers;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by brad on 2017/02/12.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactsViewHolder> {
    private List<OtherUsers> contacts;
    private Context context;

    public ContactAdapter(Context context, List<OtherUsers> contacts) {
        this.contacts = contacts;
        this.context = context;

    }


    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_contact, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder viewHolder, int position) {
        OtherUsers user = contacts.get(position);
        String username = user.getFirstName() + " " + user.getSurname();
        viewHolder.setUsername(username);
        viewHolder.imageViewContactDisplay.setImageResource(R.drawable.facebook_avatar);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {

                new AsyncTask<Void,Void,String>(){

                    @Override
                    protected String doInBackground(Void... voids) {
                        return AppDatabase.getDatabase(context).messagesModelDao().getParentMessageId(user.getId());
                    }

                    @Override
                    protected void onPostExecute(String parentMessageId) {
                        super.onPostExecute(parentMessageId);

                        ArrayList<Integer> userIds = new ArrayList<>();
                        userIds.add(user.getId());
                        MessagesActivity.open(context,parentMessageId,userIds);
                        ((Activity)context).finish();
                    }
                }.execute();

            }
        });

    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContactUsername;
        ImageView imageViewContactDisplay;
        View itemView;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewContactUsername = itemView.findViewById(R.id
                    .text_view_contact_username);
            imageViewContactDisplay = itemView.findViewById(R.id
                    .image_view_contact_display);
        }

        public void setUsername(String username) {
            textViewContactUsername.setText(username);
        }
    }
}
