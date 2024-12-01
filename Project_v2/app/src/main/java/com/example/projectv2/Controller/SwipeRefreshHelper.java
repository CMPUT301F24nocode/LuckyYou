package com.example.projectv2.Controller;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SwipeRefreshHelper {

    private SwipeRefreshLayout swipeRefreshLayout;

    public SwipeRefreshHelper(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        init();
    }

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

    public void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }
}
