// EventDisplayController.java
package com.example.projectv2.Controller;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import com.example.projectv2.Model.Event;
import com.example.projectv2.View.EventListAdapter;

import java.util.ArrayList;

public class EventDisplayController {
    private ArrayList<Event> eventList;
    private EventListAdapter adapter;

    public EventDisplayController(Context context, ListView listView, ArrayList<Event> initialEventList) {
        this.eventList = initialEventList != null ? initialEventList : new ArrayList<>();
        this.adapter = new EventListAdapter(context, eventList);
        listView.setAdapter(adapter);
        Log.d("EventDisplayController", "Adapter initialized with " + eventList.size() + " events.");
    }

    // Method to update the list and refresh the adapter
    public void updateEventList(ArrayList<Event> newEvents) {
        eventList.clear();
        eventList.addAll(newEvents);
        adapter.notifyDataSetChanged();
        Log.d("EventDisplayController", "Event list updated with " + newEvents.size() + " items.");
    }

    // Method to get a specific event at a position
    public Event getEventAt(int position) {
        return eventList.get(position);
    }
}

