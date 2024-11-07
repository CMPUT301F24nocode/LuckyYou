package com.example.projectv2.Controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectv2.View.AvailableEventsFragment;
import com.example.projectv2.View.EventStatusFragment;
import com.example.projectv2.View.YourEventsFragment;

public class EventsPagerAdapter extends FragmentStateAdapter {

    public EventsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AvailableEventsFragment();
            case 1:
                return new EventStatusFragment();
            case 2:
                return new YourEventsFragment();
            default:
                return new AvailableEventsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
