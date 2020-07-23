package gr.hua.dit.it21525.doctorapp.asynctasks;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gr.hua.dit.it21525.doctorapp.models.Appointment;

public class GetCalendarEventId extends AsyncTask<Void, Void, Void> {
    private com.google.api.services.calendar.Calendar mService;
    private String calId;
    private String des;

    public GetCalendarEventId(GoogleCredential credential, String calendarId, String des){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Calling Google Calendar")
                .build();
        this.calId = calendarId;
        this.des = des;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return getEventId();
    }

    private Void getEventId(){
        String pageToken = null;
        List<Event> items = new ArrayList<>();
        do {
            Events events;
            try {
                events = mService.events().list(calId).setPageToken(pageToken).setSingleEvents(true).execute();
                items = events.getItems();
                pageToken = events.getNextPageToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (pageToken != null);

        for(Event event : items){
            if(event.getDescription()!= null && event.getDescription().equals(des)){
                InsertCalendarEvent.saveEventToFirebaseDatabase(event.getId());
            }
        }

        return null;
    }
}
