package com.softmed.stockapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softmed.stockapp.R;
import com.softmed.stockapp.adapters.ContactAdapter;
import com.softmed.stockapp.database.AppDatabase;
import com.softmed.stockapp.dom.entities.OtherUsers;
import com.softmed.stockapp.viewmodels.ContactChooserViewModel;

import java.util.List;

public class ContactChooserFragment extends Fragment {
    private ContactChooserViewModel mViewModel;
    private View mRootView;
    private RecyclerView mWhatsappRecycler;

    public static ContactChooserFragment newInstance() {
        return new ContactChooserFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.contact_chooser_fragment, container, false);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(ContactChooserViewModel.class);

        AppDatabase db = AppDatabase.getDatabase(getContext());
        mViewModel.setAppDatabase(db);
        mViewModel.getContacts().observe(getActivity(), new Observer<List<OtherUsers>>() {
            @Override
            public void onChanged(List<OtherUsers> otherUsers) {
                init(otherUsers);
            }
        });

    }

    private void init(List<OtherUsers> contacts) {
        mWhatsappRecycler = (RecyclerView) mRootView.findViewById(R.id.whatsapp_recycler);
        mWhatsappRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mWhatsappRecycler.setHasFixedSize(true);

        ContactAdapter mContactAdapter = new ContactAdapter(getContext(), contacts);
        mWhatsappRecycler.setAdapter(mContactAdapter);
    }

}
