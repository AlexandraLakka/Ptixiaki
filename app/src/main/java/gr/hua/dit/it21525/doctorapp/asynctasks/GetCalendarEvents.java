package gr.hua.dit.it21525.doctorapp.asynctasks;

import android.graphics.Color;
import android.os.AsyncTask;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import gr.hua.dit.it21525.doctorapp.activities.CalendarActivity;

public class GetCalendarEvents extends AsyncTask<Void, Void, List<WeekViewEvent>>{

    private WeakReference<CalendarActivity> activityReference;
    private com.google.api.services.calendar.Calendar mService;
    private String calId;

    public GetCalendarEvents(GoogleCredential credential, CalendarActivity context, String calendarId) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Calling Google Calendar")
                .build();
        activityReference = new WeakReference<>(context);
        this.calId = calendarId;
    }

    @Override
    protected List<WeekViewEvent> doInBackground(Void... voids) {
        return getEventsList();
    }

    private List<WeekViewEvent> getEventsList(){
        String pageToken = null;
        List<Event> items = new ArrayList<>();
        List<WeekViewEvent> newEvents = new ArrayList<>();
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
            newEvents.add(toWeekViewEvent(event));
        }
        return newEvents;
    }

    @Override
    protected void onPreExecute() {
        CalendarActivity.mProgress.show();
    }

    @Override
    protected void onPostExecute(List<WeekViewEvent> weekViewEvents) {
        CalendarActivity activity;
        activity = activityReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }
        CalendarActivity.mProgress.dismiss();
        CalendarActivity.events.addAll(weekViewEvents);
        activity.mWeekView.notifyDatasetChanged();
    }

    private WeekViewEvent toWeekViewEvent(Event event){
        String startHour = String.valueOf(event.getStart().getDateTime()).substring(11,13);
        String startMinute = String.valueOf(event.getStart().getDateTime()).substring(14,16);
        String startYear = String.valueOf(event.getStart().getDateTime()).substring(0,4);
        String startMonth = String.valueOf(event.getStart().getDateTime()).substring(5,7);
        String startDay = String.valueOf(event.getStart().getDateTime()).substring(8,10);

        String endHour = String.valueOf(event.getEnd().getDateTime()).substring(11,13);
        String endMinute = String.valueOf(event.getEnd().getDateTime()).substring(14,16);
        String endYear = String.valueOf(event.getEnd().getDateTime()).substring(0,4);
        String endMonth = String.valueOf(event.getEnd().getDateTime()).substring(5,7);
        String endDay = String.valueOf(event.getEnd().getDateTime()).substring(8,10);
        // Initialize start and end time.
        Calendar now = Calendar.getInstance();
        Calendar startTime = (Calendar) now.clone();
        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startHour));
        startTime.set(Calendar.MINUTE, Integer.parseInt(startMinute));
        startTime.set(Calendar.YEAR, Integer.parseInt(startYear));
        startTime.set(Calendar.MONTH, Integer.parseInt(startMonth)-1);
        startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startDay));
        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHour));
        endTime.set(Calendar.MINUTE, Integer.parseInt(endMinute));
        endTime.set(Calendar.YEAR, Integer.parseInt(endYear));
        endTime.set(Calendar.MONTH, Integer.parseInt(endMonth)-1);
        endTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDay));

        long range = 1234567L;
        Random r = new Random();
        long id = (long) (r.nextDouble()*range);
        // Create an week view event.
        WeekViewEvent weekViewEvent = new WeekViewEvent(id, event.getSummary(), startTime, endTime);
        //int color = ((int)(Math.random()*16777215)) | (0xFF << 24);
        weekViewEvent.setColor(Color.rgb(0, 147, 196));

        return weekViewEvent;
    }
}
