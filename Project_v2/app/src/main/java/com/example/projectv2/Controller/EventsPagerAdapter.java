/**
 * EventsPagerAdapter is a FragmentStateAdapter that provides fragments for a ViewPager2.
 * It manages three fragments: AvailableEventsFragment, EventStatusFragment, and YourEventsFragment,
 * for navigating between different event-related views in the application.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectv2.View.AvailableEventsFragment;
import com.example.projectv2.View.EventStatusFragment;
import com.example.projectv2.View.YourEventsFragment;

/**
 * Adapter for providing fragments to a ViewPager2. It displays different fragments
 * based on the selected tab position, allowing users to switch between views of available
 * events, event statuses, and their own events.
 */
public class EventsPagerAdapter extends FragmentStateAdapter {

    /**
     * Constructs an EventsPagerAdapter with the given FragmentActivity.
     *
     * @param fragmentActivity the FragmentActivity that this adapter will be attached to
     */
    public EventsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Returns the fragment associated with a specified position.
     *
     * @param position the position of the fragment in the ViewPager
     * @return the fragment corresponding to the specified position
     */
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

    /**
     * Returns the total number of items (fragments) in the adapter.
     *
     * @return the total number of fragments
     */
    @Override
    public int getItemCount() {
        return 3;
    }
}
