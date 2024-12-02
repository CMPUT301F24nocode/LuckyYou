/**
 * Activity for configuring additional options for an event, such as date, attendee limits,
 * ticket price, geolocation, and notifications. The collected data is passed back to the
 * EventController to create the event in Firestore.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.EventController;
import com.example.projectv2.MainActivity;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;

import java.util.ArrayList;

/**
 * Activity for configuring additional options for an event, such as date, attendee limits,
 * ticket price, geolocation, and notifications. The collected data is passed back to the
 * EventController to create the event in Firestore.
 */
public class CreateEventOptionsActivity extends AppCompatActivity {

    private EditText eventDeadline, eventAttendees, eventEntrants, eventStartDate, eventTicketPrice;
    private CheckBox geolocationCheckbox, notificationsCheckbox;
    private String name, detail, rules, facility, imageUri;

    private static final String DATE_PATTERN = "^\\d{2}-\\d{2}-\\d{4}$";
    private EventController eventController;

    /**
     * Called when the activity is created. Sets up UI elements for inputting event options
     * and retrieves initial event details passed via Intent.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_options);

        // Initialize UI components
        eventDeadline = findViewById(R.id.create_event_deadline_view);
        eventAttendees = findViewById(R.id.create_event_attendees_num_view);
        eventEntrants = findViewById(R.id.create_event_entrants_num_view);
        eventStartDate = findViewById(R.id.create_event_start_date);
        eventTicketPrice = findViewById(R.id.create_event_ticket_price);
        geolocationCheckbox = findViewById(R.id.create_event_geolocation_checkbox_view);

        // Retrieve event details from the Intent
        name = getIntent().getStringExtra("name");
        detail = getIntent().getStringExtra("detail");
        rules = getIntent().getStringExtra("rules");
        facility = getIntent().getStringExtra("facility");
        imageUri = getIntent().getStringExtra("imageUri");

        // Initialize EventController
        eventController = new EventController(this);

        // Set up the "Create Event" button
        Button createEventButton = findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(v -> createEvent());
    }

    /**
     * Collects data from input fields, validates it, and creates an event using the EventController.
     */
    private void createEvent() {
        String deadline = eventDeadline.getText().toString();
        String attendees = eventAttendees.getText().toString();
        String entrants = eventEntrants.getText().toString();
        String startDate = eventStartDate.getText().toString();
        String ticketPrice = eventTicketPrice.getText().toString();
        boolean geolocationEnabled = geolocationCheckbox.isChecked();

        // Validate date format
        if (!isValidDate(deadline)) {
            eventDeadline.setError("Invalid date format. Use DD-MM-YYYY");
            return;
        }
        if (!isValidDate(startDate)) {
            eventStartDate.setError("Invalid date format. Use DD-MM-YYYY");
            return;
        }

        // Ensure deadline is before start date
        if (!isDeadlineBeforeStartDate(deadline, startDate)) {
            eventDeadline.setError("Deadline must be before the event start date.");
            return;
        }

        // Get device owner ID
        String owner = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Call EventController to create the event
        eventController.createEvent(
                owner,
                name,
                detail,
                rules,
                deadline,
                attendees,
                entrants,
                startDate,
                ticketPrice,
                geolocationEnabled,
                imageUri != null ? Uri.parse(imageUri) : null,
                facility,
                new EventController.EventCallback() {
                    // Callbacks for event creation
                    @Override
                    public void onEventListLoaded(ArrayList<Event> events) {
                        // Not used in this context
                    }

                    // Callbacks for event creation
                    @Override
                    public void onEventCreated(String eventId) {
                        Log.d("CreateEventOptions", "Event created with ID: " + eventId);
                        Toast.makeText(CreateEventOptionsActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate back to the home screen
                        Intent intent = new Intent(CreateEventOptionsActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("userID", owner);
                        startActivity(intent);

                        // Finish the current activity
                        finish();
                    }

                    // Callbacks for event creation
                    @Override
                    public void onError(Exception e) {
                        Log.e("CreateEventOptions", "Error creating event", e);
                        Toast.makeText(CreateEventOptionsActivity.this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    /**
     * Validates if the provided date string matches the expected date format (DD-MM-YYYY).
     *
     * @param date the date string to validate
     * @return true if the date matches the expected pattern, false otherwise
     */
    private boolean isValidDate(String date) {
        return date.matches(DATE_PATTERN);
    }

    /**
     * Ensures that the deadline date is before the start date.
     *
     * @param deadline the deadline date in DD-MM-YYYY format
     * @param startDate the start date in DD-MM-YYYY format
     * @return true if the deadline is before the start date, false otherwise
     */
    private boolean isDeadlineBeforeStartDate(String deadline, String startDate) {
        try {
            String[] deadlineParts = deadline.split("-");
            String[] startDateParts = startDate.split("-");

            int deadlineYear = Integer.parseInt(deadlineParts[2]);
            int deadlineMonth = Integer.parseInt(deadlineParts[1]);
            int deadlineDay = Integer.parseInt(deadlineParts[0]);

            int startYear = Integer.parseInt(startDateParts[2]);
            int startMonth = Integer.parseInt(startDateParts[1]);
            int startDay = Integer.parseInt(startDateParts[0]);

            if (startYear > deadlineYear) return true;
            if (startYear == deadlineYear && startMonth > deadlineMonth) return true;
            return startYear == deadlineYear && startMonth == deadlineMonth && startDay > deadlineDay;
        } catch (Exception e) {
            Log.e("DateValidation", "Error parsing dates: " + e.getMessage());
            return false;
        }
    }
}