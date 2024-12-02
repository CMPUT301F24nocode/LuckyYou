package com.example.projectv2.Controller;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Helper class for managing a SwipeRefreshLayout and its refresh listener.
 */
public class SwipeRefreshHelper {

    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Constructs a SwipeRefreshHelper with the specified SwipeRefreshLayout.
     *
     * @param swipeRefreshLayout the SwipeRefreshLayout to manage
     */
    public SwipeRefreshHelper(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        init();
    }

    /**
     * Initializes the SwipeRefreshLayout and sets the refresh listener.
     */
    private void init() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Define the refresh logic here
            onRefresh();
        });
    }

    /**
     * Triggered when the user performs a refresh gesture.
     */
    protected void onRefresh() {
        // Override this method in subclasses or specific implementations
        swipeRefreshLayout.setRefreshing(false); // Stop the animation
    }

    /**
     * Sets whether the SwipeRefreshLayout is refreshing.
     *
     * @param refreshing true if the SwipeRefreshLayout is refreshing, false otherwise
     */
    public void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }
}
