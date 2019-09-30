package com.softmed.stockapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softmed.stockapp.R;
import com.softmed.stockapp.dom.dto.ContactUsersDTO;
import com.softmed.stockapp.dom.entities.OtherUsers;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by brad on 2017/02/12.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactsViewHolder> implements Filterable {
    private final OnItemClickListener listener;
    private List<ContactUsersDTO> contacts;
    private List<ContactUsersDTO> contactListFiltered;
    private Context context;

    public ContactAdapter(Context context, List<ContactUsersDTO> contacts, OnItemClickListener listener) {
        this.contacts = contacts;
        contactListFiltered = contacts;
        this.context = context;
        this.listener = listener;

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
        ContactUsersDTO user = contactListFiltered.get(position);
        String username = user.getFirstName() + " " + user.getSurname();
        viewHolder.setUsername(username);
        viewHolder.setFacilityName(user.getHealth_facility_name());
        viewHolder.imageViewContactDisplay.setImageResource(R.drawable.facebook_avatar);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }


    public interface OnItemClickListener {
        void onItemClick(ContactUsersDTO user);
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContactUsername;
        TextView facilityNameTextView;
        ImageView imageViewContactDisplay;
        View itemView;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewContactUsername = itemView.findViewById(R.id
                    .text_view_contact_username);
            facilityNameTextView = itemView.findViewById(R.id
                    .facility_name);
            imageViewContactDisplay = itemView.findViewById(R.id
                    .image_view_contact_display);
        }

        public void setUsername(String username) {
            textViewContactUsername.setText(username);
        }

        public void setFacilityName(String facilityName){
                facilityNameTextView.setText(facilityName);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contacts;
                } else {
                    List<ContactUsersDTO> filteredList = new ArrayList<>();
                    for (ContactUsersDTO row : contacts) {

                        Log.d("test","facility name "+row.getHealth_facility_name());

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getFirstName().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getSurname().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getHealth_facility_name().toLowerCase().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<ContactUsersDTO>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
