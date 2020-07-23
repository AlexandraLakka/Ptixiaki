package gr.hua.dit.it21525.doctorapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.util.Calendar;

import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.asynctasks.GetCalendarEvents;
import gr.hua.dit.it21525.doctorapp.dialog.CreateEventDialog;
import gr.hua.dit.it21525.doctorapp.models.Doctor;

public class CalendarActivity extends AppCompatActivity implements WeekView.EventClickListener,WeekView.EmptyViewClickListener, MonthLoader.MonthChangeListener {

    private Doctor currentDoctor;
    private String calendarId;
    private GoogleCredential credential;
    public static ProgressDialog mProgress;
    public WeekView mWeekView;
    public static List<WeekViewEvent> events;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY,
            "https://www.googleapis.com/auth/calendar.events", "https://www.googleapis.com/auth/calendar"};
    public CreateEventDialog eventDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        currentDoctor = getIntent().getParcelableExtra("Doctor");
        assert currentDoctor != null;
        calendarId = currentDoctor.getCalendarId();

        events = new ArrayList<>();

        // Get a reference for the week view in the layout.
        mWeekView = findViewById(R.id.weekView);


        mWeekView.setMonthChangeListener(this);
        mWeekView.setEmptyViewClickListener(this);
        mWeekView.setOnEventClickListener(this);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait ...");

        // Initialize credentials and service object.
        try {
            InputStream is = getAssets().open("DoctroApp-a7e5c768f378.json");
            credential = GoogleCredential.fromStream(is)
                    .createScoped(Arrays.asList(SCOPES));
        } catch (IOException e) {
            e.printStackTrace();
        }
        new GetCalendarEvents(credential, this, calendarId).execute();
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        //https://github.com/alamkanak/Android-Week-View/issues/381
        List<WeekViewEvent> matchedEvents = new ArrayList<>();
        for (WeekViewEvent event : events) {
            if (eventMatches(event, newYear, newMonth)) {
                matchedEvents.add(event);
            }
        }
        return matchedEvents;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
        eventDialog = new CreateEventDialog(credential, currentDoctor, time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
        eventDialog.show(getSupportFragmentManager(), "dialog");
    }

    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month-1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }
}
