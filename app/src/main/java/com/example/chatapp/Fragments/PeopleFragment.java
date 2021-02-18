package com.example.chatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chatapp.R;

public class PeopleFragment extends Fragment {

    private PeopleViewModel pViewModel;

    static PeopleFragment instance;

    public static PeopleFragment getInstance() {
        return instance == null ? new PeopleFragment() : instance;
    }

    @Override
    // Creates and returns the view hierarchy associated with the fragment
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.people_fragment, container, false); // Show the People fragment
    }

    @Override
    // Tells the fragment that its activity has completed its own Activity.onCreate()
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pViewModel = new ViewModelProvider(this).get(PeopleViewModel.class);
        // TODO: Use the ViewModel
    }

}