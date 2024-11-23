/**
 * Activity for displaying the list of facilities in the admin view.
 * Sets up the top bar with a title and back button functionality.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;

/**
 * AdminFacilityListActivity displays the list of facilities available for admin users to browse.
 * It initializes the UI layout and sets up the top bar with the title "Browse Facilities."
 */
public class AdminFacilityListActivity extends AppCompatActivity {

    /**
     * Called when the activity is created. Sets up the content view and configures the top bar
     * with the title "Browse Facilities" and the "more" button visibility set to invisible.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facility_list);
        topBarUtils.topBarSetup(this, "Browse Facilities", View.INVISIBLE);
    }
}
