package com.example.projectv2.View;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;


import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import java.util.ArrayList;

public class EventOptionsActivity extends AppCompatActivity {

    private EditText eventDeadline, eventAttendees, eventEntrants;
    private EditText eventStartDate, eventTicketPrice;  // New fields
    private CheckBox geolocationCheckbox, notificationsCheckbox;
    private String name, detail, rules, facility;
    private Uri selectedImageUri; // Ensure this is set if image is chosen in EventCreatorActivity
    private static final String DATE_PATTERN = "^\\d{2}-\\d{2}-\\d{4}$";

    public EventController eventController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_options); // Displays create_event_options.xml

        // Initialize UI components
        eventDeadline = findViewById(R.id.create_event_deadline_view);

        eventAttendees = findViewById(R.id.create_event_attendees_num_view);
        eventEntrants = findViewById(R.id.create_event_entrants_num_view);
        eventStartDate = findViewById(R.id.create_event_start_date);  // Initialize new fields
        eventTicketPrice = findViewById(R.id.create_event_ticket_price);
        geolocationCheckbox = findViewById(R.id.create_event_geolocation_checkbox_view);
        notificationsCheckbox = findViewById(R.id.create_event_notification_checkbox_view);

        // Retrieve event name, details, and image URI from Intent
        name = getIntent().getStringExtra("name");
        detail = getIntent().getStringExtra("detail");
        rules = getIntent().getStringExtra("rules");
        facility = getIntent().getStringExtra("facility");
        if (facility == null || facility.isEmpty()) {
            facility = "Online"; // Ensure the facility is set to "Online" if not provided
        }
        if (getIntent().hasExtra("imageUri")) {
            selectedImageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
        }

        // Initialize EventController
        eventController = new EventController(this);

        // Set up the button to create event
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

                // Validate date formats
                if (!isValidDate(deadline)) {
                    eventDeadline.setError("Invalid date format. Use DD-MM-YYYY");
                    return;
                }
                if (!isValidDate(startDate)) {
                    eventStartDate.setError("Invalid date format. Use DD-MM-YYYY");
                    return;
                }

                // Call EventController to save the event in Firestore
                eventController.createEvent(
                        name,
                        detail,
                        rules,
                        deadline,
                        attendees,
                        entrants,
                        startDate,
                        ticketPrice,
                        geolocationEnabled,
                        notificationsEnabled,
                        selectedImageUri,
                        facility,
                        new EventController.EventCallback() {
                            @Override
                            public void onEventListLoaded(ArrayList<Event> events) {
                                // Show success message when event creation is successful
                                Toast.makeText(EventOptionsActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Optionally close the activity after success
                            }

                            @Override
                            public void onError(Exception e) {
                                // Handle error, such as showing an error message
                                Toast.makeText(EventOptionsActivity.this, "Error creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("EventOptionsActivity", "Error creating event", e);
                            }
                        }
                );
            }
        });
    }

    // Method to check if date matches pattern
    private boolean isValidDate(String date) {
        return date.matches(DATE_PATTERN);
    }


}