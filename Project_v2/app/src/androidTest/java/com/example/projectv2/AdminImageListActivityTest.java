package com.example.projectv2;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.R;
import com.example.projectv2.View.AdminImageListActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.view.View;
/**
 *
 * This class contains UI tests for the AdminImageListActivity. It ensures the layout and functionality
 * of components such as RecyclerView, SwipeRefreshLayout, and the top bar title are correctly implemented.
 * The tests also verify the RecyclerView's grid layout configuration and the swipe-to-refresh functionality.
 *
 */
@RunWith(AndroidJUnit4.class)
public class AdminImageListActivityTest {

    @Rule
    public ActivityScenarioRule<AdminImageListActivity> activityRule =
            new ActivityScenarioRule<>(AdminImageListActivity.class);

    /**
     * Test that the activity layout is loaded correctly
     */
    @Test
    public void testActivityLayoutLoaded() {
        // Wait for layout to stabilize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check that the RecyclerView exists
        onView(withId(R.id.recycler_view))
                .check((view, noViewFoundException) -> {
                    if (noViewFoundException != null) {
                        throw noViewFoundException;
                    }

                    // Print out view details for debugging
                    System.out.println("RecyclerView details: " + view);
                    System.out.println("Width: " + view.getWidth());
                    System.out.println("Height: " + view.getHeight());
                });

        // Check SwipeRefreshLayout
        onView(withId(R.id.swipe_refresh_layout))
                .check(matches(isDisplayed()));
    }

    /**
     * Test the top bar title
     */
    @Test
    public void testTopBarTitle() {
        // Check that the top bar is present with the correct title
        onView(withText("Browse Images"))
                .check(matches(isDisplayed()));
    }

    /**
     * Test RecyclerView grid layout
     */
    @Test
    public void testRecyclerViewGridLayout() {
        // This test would typically use a custom matcher to verify
        // the grid layout configuration
        // For example, checking the number of columns
        onView(withId(R.id.recycler_view))
                .check((view, noViewFoundException) -> {
                    if (noViewFoundException != null) {
                        throw noViewFoundException;
                    }

                    RecyclerView recyclerView = (RecyclerView) view;
                    GridLayoutManager layoutManager =
                            (GridLayoutManager) recyclerView.getLayoutManager();

                    // Assert that the grid layout has 2 columns
                    assert layoutManager != null;
                    assertEquals(2, layoutManager.getSpanCount());
                });
    }

    /**
     * Test SwipeRefreshLayout functionality
     */
    @Test
    public void testSwipeRefreshLayout() {
        // Perform swipe down to refresh
        onView(withId(R.id.swipe_refresh_layout))
                .perform(SwipeRefreshLayoutActions.swipeDown());

        // You might want to add a custom matcher or idling resource
        // to verify that the refresh actually occurred
    }

    /**
     * Custom SwipeRefreshLayout action for Espresso
     */
    public static class SwipeRefreshLayoutActions {
        public static ViewAction swipeDown() {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return ViewMatchers.isAssignableFrom(SwipeRefreshLayout.class);
                }

                @Override
                public String getDescription() {
                    return "Swipe down on SwipeRefreshLayout";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view;
                    swipeRefreshLayout.setRefreshing(true);
                    // Simulate a refresh
                    uiController.loopMainThreadForAtLeast(1000);
                }
            };
        }
    }

}