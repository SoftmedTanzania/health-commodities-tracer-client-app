package com.softmed.stockapp.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softmed.stockapp.R;
import com.softmed.stockapp.dom.entities.OtherUsers;

import java.util.List;


/**
 * Created by brad on 2017/02/12.
 */

public class ContactAdapter  extends RecyclerView.Adapter<ContactAdapter.ContactsViewHolder> {
    private List<OtherUsers> contacts;

    public ContactAdapter(Context context, List<OtherUsers> contacts) {
        this.contacts=contacts;

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
        String username = user.getFirstName()+" "+user.getSurname();
        viewHolder.setUsername(username);
        viewHolder.imageViewContactDisplay.setImageResource(R.drawable.facebook_avatar);

    }



    @Override
    public int getItemCount() {
        return contacts.size();
    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContactUsername;
        ImageView imageViewContactDisplay;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            textViewContactUsername = (TextView) itemView.findViewById(R.id
                    .text_view_contact_username);
            imageViewContactDisplay = (ImageView) itemView.findViewById(R.id
                    .image_view_contact_display);
        }

        public void setUsername(String username) {
            textViewContactUsername.setText(username);
        }
    }
}
