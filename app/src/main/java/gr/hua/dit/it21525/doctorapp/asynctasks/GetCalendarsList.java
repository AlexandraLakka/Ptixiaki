package gr.hua.dit.it21525.doctorapp.asynctasks;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.RegisterDoctorActivity;
import gr.hua.dit.it21525.doctorapp.adapters.NothingSelectedSpinnerAdapter;

public class GetCalendarsList extends AsyncTask<Void, Void, HashMap<String, String>> {
    private WeakReference<RegisterDoctorActivity> activityReference;
    private com.google.api.services.calendar.Calendar mService;
    private Exception mLastError = null;

    public GetCalendarsList(GoogleAccountCredential credential, RegisterDoctorActivity context) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Calling Google Calendar")
                .build();
        activityReference = new WeakReference<>(context);
    }

    @Override
    protected HashMap<String, String> doInBackground(Void... voids) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    private HashMap<String, String> getDataFromApi() throws IOException {
        String pageToken = null;
        HashMap<String, String> calendars = new HashMap<>();

        do {
            CalendarList list = mService.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = list.getItems();


            for (CalendarListEntry calendarListEntry : items) {
                calendars.put(calendarListEntry.getId(), calendarListEntry.getSummary());
            }
            pageToken = list.getNextPageToken();
        } while (pageToken != null);

        return calendars;
    }

    @Override
    protected void onPreExecute() {
        RegisterDoctorActivity activity;
        activity = activityReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }
        RegisterDoctorActivity.mProgress.show();
    }

    @Override
    protected void onPostExecute(final HashMap<String, String> output) {
        RegisterDoctorActivity activity;
        activity = activityReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }
        RegisterDoctorActivity.mProgress.hide();
        if (output == null || output.size() == 0) {
            output.put("", "No results returned");
        } else {
            for (Map.Entry summary : output.entrySet()) {
                RegisterDoctorActivity.arrayList.add(summary.getValue().toString());
            }
            RegisterDoctorActivity.adapter = new ArrayAdapter<>(activity,
                    android.R.layout.simple_spinner_item, RegisterDoctorActivity.arrayList);
            activity.userCalendars.setAdapter(
                    new NothingSelectedSpinnerAdapter(
                            RegisterDoctorActivity.adapter,
                            R.layout.spinner_row_nothing_selected,
                            activity));

            //https://stackoverflow.com/questions/18312043/how-to-change-spinner-text-color
            activity.userCalendars.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                    ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                    ((TextView) adapterView.getChildAt(0)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    for (Map.Entry<String, String> key : output.entrySet()) {
                        if (((TextView) adapterView.getChildAt(0)).getText().toString().equals(key.getValue())) {
                            RegisterDoctorActivity.calendarId = key.getKey();
                            Log.d("GetCalendarList", RegisterDoctorActivity.calendarId);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }

    @Override
    protected void onCancelled() {
        RegisterDoctorActivity activity;
        activity = activityReference.get();
        if (activity == null || activity.isFinishing())
        {
            return;
        }
        RegisterDoctorActivity.mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                activity.showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        RegisterDoctorActivity.REQUEST_AUTHORIZATION);
            } else {
                Log.d(RegisterDoctorActivity.TAG, "The following error occurred:\n"
                        + mLastError.getMessage());
            }
        } else {
            Log.d(RegisterDoctorActivity.TAG, "Request cancelled.");
        }
    }
}