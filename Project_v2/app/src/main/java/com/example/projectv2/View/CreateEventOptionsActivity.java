/**
 * Activity for configuring additional options for an event, such as date, attendee limits,
 * ticket price, geolocation, and notifications. The collected data is passed back to the
 * previous activity upon completion.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectv2.R;

/**
 * CreateEventOptionsActivity allows users to input additional options for an event.
 * Users can specify event details such as deadline, start date, attendee limits, and
 * optional geolocation and notification settings.
 */
public class CreateEventOptionsActivity extends AppCompatActivity {
    private EditText eventDeadline, eventAttendees, eventEntrants;
    private EditText eventStartDate, eventTicketPrice;
    private CheckBox geolocationCheckbox, notificationsCheckbox;
    private String name, detail, rules, facility;
    private static final String DATE_PATTERN = "^\\d{2}-\\d{2}-\\d{4}$";

    /**
     * Called when the activity is created. Sets up UI elements for inputting event options
     * and retrieves initial event details passed via Intent.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_options); // Displays create_event_options.xml

        // Initialize UI components
        eventDeadline = findViewById(R.id.create_event_deadline_view);
        eventAttendees = findViewById(R.id.create_event_attendees_num_view);
        eventEntrants = findViewById(R.id.create_event_entrants_num_view);
        eventStartDate = findViewById(R.id.create_event_start_date);
        eventTicketPrice = findViewById(R.id.create_event_ticket_price);
        geolocationCheckbox = findViewById(R.id.create_event_geolocation_checkbox_view);
        notificationsCheckbox = findViewById(R.id.create_event_notification_checkbox_view);

        // Retrieve event name and details from Intent
        name = getIntent().getStringExtra("name");
        detail = getIntent().getStringExtra("detail");
        rules = getIntent().getStringExtra("rules");
        facility = getIntent().getStringExtra("facility");

        Button createEventButton = findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect data
                String deadline = eventDeadline.getText().toString();
                String attendees = eventAttendees.getText().toString();
                String entrants = eventEntrants.getText().toString();
                String startDate = eventStartDate.getText().toString();
                String ticketPrice = eventTicketPrice.getText().toString();
                boolean geolocationEnabled = geolocationCheckbox.isChecked();
                boolean notificationsEnabled = notificationsCheckbox.isChecked();

                // Validate date format
                if (!isValidDate(deadline)) {
                    eventDeadline.setError("Invalid date format. Use DD-MM-YYYY");
                    return;
                }
                if (!isValidDate(startDate)) {
                    eventStartDate.setError("Invalid date format. Use DD-MM-YYYY");
                    return;
                }

                // Create intent to pass data back to EventCreatorActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", name);
                resultIntent.putExtra("detail", detail);
                resultIntent.putExtra("rules", rules);
                resultIntent.putExtra("facility", facility);
                resultIntent.putExtra("deadline", deadline);
                resultIntent.putExtra("attendees", attendees);
                resultIntent.putExtra("entrants", entrants);
                resultIntent.putExtra("startDate", startDate);
                resultIntent.putExtra("ticketPrice", ticketPrice);
                resultIntent.putExtra("geolocationEnabled", geolocationEnabled);
                resultIntent.putExtra("notificationsEnabled", notificationsEnabled);

                // Set result and finish activity
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
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
}
