package com.example.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chatapp.Aadapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.tab_dots)
    TabLayout tabLayout;
    @BindView(R.id.view_pager_home)
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize();
        setupViewPager();
    }

    private void initialize() {
        ButterKnife.bind(this);
    }


    private void setupViewPager() {
        viewPager2.setOffscreenPageLimit(2); // Leave pages that are offscreen active
        // (getSupportFragmentManager()) for interacting with fragments associated with this activity.
        // Starting point of showing the fragments
        viewPager2.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), new Lifecycle() { // Defines an object that has an Android Lifecycle
            @Override
            public void addObserver(@NonNull LifecycleObserver observer) { // notifies when the LifecycleOwner changes state

            }

            @Override
            public void removeObserver(@NonNull LifecycleObserver observer) { // Removes the given observer from the observers list.

            }

            @NonNull
            @Override
            public State getCurrentState() { // Returns the current state of the Lifecycle
                return null;
            }
        }));
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> { // The mediator will synchronize the ViewPager2's position with the selected tab when a tab is selected
            if (position == 0)
                tab.setText("Chat");
            else
                tab.setText("People");
        }).attach();
    }
}