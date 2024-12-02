package com.example.projectv2;

import android.content.Context;
import android.net.Uri;
import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Model.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EventControllerTest {

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private Context mockContext;

    @Mock
    private QuerySnapshot mockQuerySnapshot;

    @Mock
    private QueryDocumentSnapshot mockDocumentSnapshot;

    private EventController eventController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventController = new EventController(mockContext);
        eventController.db = mockFirestore; // Inject mocked Firestore instance
    }

    @Test
    public void testCreateEvent_withoutImage() {
        EventController.EventCallback mockCallback = mock(EventController.EventCallback.class);

        eventController.createEvent(
                "owner123",
                "Event Name",
                "Event Details",
                "Event Rules",
                "2024-12-31",
                "100",
                "200",
                "2024-12-01",
                "$50",
                false,
                null,
                "Facility Name",
                mockCallback
        );

        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mockFirestore.collection("events").document(anyString()))
                .set(eventCaptor.capture(), any());

        Map<String, Object> savedEvent = eventCaptor.getValue();
        assertEquals("owner123", savedEvent.get("owner"));
        assertEquals("Event Name", savedEvent.get("name"));
        assertNull(savedEvent.get("imageUri"));
    }

    @Test
    public void testCreateEvent_withImage() {
        EventController.EventCallback mockCallback = mock(EventController.EventCallback.class);

        Uri mockUri = mock(Uri.class);
        when(mockUri.toString()).thenReturn("http://example.com/image.jpg");

        eventController.createEvent(
                "owner123",
                "Event Name",
                "Event Details",
                "Event Rules",
                "2024-12-31",
                "100",
                "200",
                "2024-12-01",
                "$50",
                false,
                mockUri,
                "Facility Name",
                mockCallback
        );

        verify(mockFirestore.collection("events").document(anyString())).set(any(), any());
    }

    @Test
    public void testFetchEvents() {
        EventController.EventCallback mockCallback = mock(EventController.EventCallback.class);
        List<QueryDocumentSnapshot> mockDocuments = new ArrayList<>();
        mockDocuments.add(mockDocumentSnapshot);

        when(mockDocumentSnapshot.getString("name")).thenReturn("Event Name");
        when(mockDocumentSnapshot.getString("owner")).thenReturn("owner123");


        eventController.fetchEvents(mockCallback);
    }

    @Test
    public void testFetchRelatedEvents() {
        EventController.EventCallback mockCallback = mock(EventController.EventCallback.class);

        when(mockDocumentSnapshot.getString("name")).thenReturn("Event Name");
        when(mockDocumentSnapshot.getString("owner")).thenReturn("owner123");
        Map<String, List<String>> mockEntrantList = new HashMap<>();
        mockEntrantList.put("Waiting", List.of("deviceID123"));

        when(mockDocumentSnapshot.get("entrantList")).thenReturn(mockEntrantList);

        eventController.fetchRelatedEvents(mockCallback);
    }

    @Test
    public void testCheckAndAddEntrant() {
        EventController.EventCallback mockCallback = mock(EventController.EventCallback.class);

        when(mockDocumentSnapshot.get("entrantList.Waiting")).thenReturn(new ArrayList<>());
        when(mockDocumentSnapshot.getLong("entrants")).thenReturn(10L);

        eventController.checkAndAddEntrant("event123", "John Doe", "john@example.com", "1234567890", mockCallback);

        verify(mockFirestore.collection("events").document("event123"))
                .update(eq("entrantList.Waiting"), any());

        verify(mockCallback).onEventCreated("event123");
    }
}