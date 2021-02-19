package com.example.chatapp.Aadapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chatapp.Fragments.ChatFragment;
import com.example.chatapp.Fragments.PeopleFragment;

public class ViewPagerAdapter extends FragmentStateAdapter { // (FragmentStateAdapter) allows usage of fragments as pages for the view pager

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) { // Gets called by the super call
        if (position==0)
            return ChatFragment.getInstance(); // Instantiates the Chat page
        else
            return PeopleFragment.getInstance(); // Instantiates the People page
    }

    @Override
    public int getItemCount() {
        return 2;
    } // Amount of pages
}
