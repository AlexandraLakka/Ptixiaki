package gr.hua.dit.it21525.doctorapp.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.util.Calendar;
import java.util.Locale;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.AppointmentInfoActivity;
import gr.hua.dit.it21525.doctorapp.asynctasks.UpdateEvent;
import gr.hua.dit.it21525.doctorapp.models.Appointment;

//used code from project "Google Calendar" from Khushvinders
public class UpdateEventDialog extends DialogFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private TimePicker startTime;
    private DatePicker startDate;
    private TimePicker endTime;
    private DatePicker endDate;
    final Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);
    private Button createEvent;
    private Button cancelEvent;
    private EditText eventTitle;
    private EditText eventDes;
    private EditText eventLocation;
    private Dialog dialog;

    private GoogleCredential credential;
    private String calendarId;
    private int startHour;
    private int startMin;
    private int startMonth;
    private int startDay;
    private Appointment appointment;

    private String startD;
    private String startT;
    private String startTDB;
    private String endD;
    private String endT;
    private String endTDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event_layout, container, false);
        eventTitle = view.findViewById(R.id.eventTitle);
        eventDes = view.findViewById(R.id.eventDes);
        eventLocation = view.findViewById(R.id.eventLocation);

        startDate = view.findViewById(R.id.startDate);

        startTime = view.findViewById(R.id.startTime);
        endTime = view.findViewById(R.id.endTime);
        endDate = view.findViewById(R.id.endDate);

        createEvent = view.findViewById(R.id.createEvent);
        cancelEvent = view.findViewById(R.id.cancelEvent);

        createEvent.setOnClickListener(this);
        cancelEvent.setOnClickListener(this);

        eventTitle.setText(appointment.getTitle());
        eventLocation.setText(appointment.getChosenDoctor().getAddress());
        eventDes.setText(appointment.getDes());
        //https://stackoverflow.com/questions/43882682/timepicker-sethour-method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startTime.setHour(startHour);
            startTime.setMinute(startMin);
            endTime.setHour(startHour + 1);
            endTime.setHour(startMin);
        } else {
            startTime.setCurrentHour(startHour);
            startTime.setCurrentMinute(startMin);
            endTime.setCurrentHour(startHour + 1);
            endTime.setCurrentMinute(startMin);
        }

        //https://stackoverflow.com/questions/47090863/how-to-set-a-specific-month-or-year-for-slection-in-datepicker
        startDate.init(c.get(Calendar.YEAR), startMonth - 1, startDay, null);
        endDate.init(c.get(Calendar.YEAR), startMonth - 1, startDay, null);
        return view;
    }

    public UpdateEventDialog(GoogleCredential credential, Appointment appointment, int startHour, int startMin, int startMonth, int startDay) {
        this.appointment = appointment;
        this.credential = credential;
        this.calendarId = appointment.getChosenDoctor().getCalendarId();
        this.startHour = startHour;
        this.startMin = startMin;
        this.startMonth = startMonth;
        this.startDay = startDay;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        startDate.updateDate(year, month, day);
        endDate.updateDate(year, month, day);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startTime.setHour(hour);
            startTime.setMinute(minute);
            endTime.setHour(hour + 1);
            endTime.setHour(minute);
        } else {
            startTime.setCurrentHour(hour);
            startTime.setCurrentMinute(minute);
            endTime.setCurrentHour(hour + 1);
            endTime.setCurrentMinute(minute);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createEvent:
                startD = startDate.getYear() + "-" + String.format(Locale.getDefault(), "%02d", startDate.getMonth() + 1) + "-" + String.format(Locale.getDefault(), "%02d", startDate.getDayOfMonth());
                endD = endDate.getYear() + "-" + String.format(Locale.getDefault(), "%02d", endDate.getMonth() + 1) + "-" + String.format(Locale.getDefault(), "%02d", endDate.getDayOfMonth());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startT = String.format(Locale.getDefault(), "%02d", startTime.getHour()-1) + ":" + String.format(Locale.getDefault(), "%02d", startTime.getMinute());
                    endT = String.format(Locale.getDefault(), "%02d", endTime.getHour()-1) + ":" + String.format(Locale.getDefault(), "%02d", endTime.getMinute());
                    startTDB = String.format(Locale.getDefault(), "%02d", startTime.getHour()) + ":" + String.format(Locale.getDefault(), "%02d", startTime.getMinute());
                    endTDB = String.format(Locale.getDefault(), "%02d", endTime.getHour()) + ":" + String.format(Locale.getDefault(), "%02d", endTime.getMinute());
                } else {
                    startT = String.format(Locale.getDefault(), "%02d", startTime.getCurrentHour()-1) + ":" + String.format(Locale.getDefault(), "%02d", startTime.getCurrentMinute());
                    endT = String.format(Locale.getDefault(), "%02d", endTime.getCurrentHour()-1) + ":" + String.format(Locale.getDefault(), "%02d", endTime.getCurrentMinute());
                    startTDB = String.format(Locale.getDefault(), "%02d", startTime.getCurrentHour()) + ":" + String.format(Locale.getDefault(), "%02d", startTime.getCurrentMinute());
                    endTDB = String.format(Locale.getDefault(), "%02d", endTime.getCurrentHour()) + ":" + String.format(Locale.getDefault(), "%02d", endTime.getCurrentMinute());
                }

                if(eventTitle.getText().toString().isEmpty() || eventDes.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please add title and description", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Appointment newAppointment = new Appointment(appointment.getId(), eventTitle.getText().toString(), eventLocation.getText().toString(),
                            eventDes.getText().toString(), startD, startT, endD, endT, appointment.getChosenDoctor());
                    Appointment newAppointmentDB = new Appointment(appointment.getId(), eventTitle.getText().toString(), eventLocation.getText().toString(),
                            eventDes.getText().toString(), startD, startTDB, endD, endTDB, appointment.getChosenDoctor());
                    new UpdateEvent(credential, (AppointmentInfoActivity) getActivity(), newAppointment, newAppointmentDB).execute();
                }
            case R.id.cancelEvent:
                dialog.dismiss();
        }
    }
}
