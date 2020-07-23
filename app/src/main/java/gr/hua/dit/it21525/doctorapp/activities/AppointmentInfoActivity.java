package gr.hua.dit.it21525.doctorapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.calendar.CalendarScopes;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.asynctasks.DeleteEvent;
import gr.hua.dit.it21525.doctorapp.dialog.CreateEventDialog;
import gr.hua.dit.it21525.doctorapp.dialog.UpdateEventDialog;
import gr.hua.dit.it21525.doctorapp.models.Appointment;

public class AppointmentInfoActivity extends AppCompatActivity{

    private Appointment appointment;
    private GoogleCredential credential;
    private CircleImageView image;
    private TextView name;
    private TextView specialty;
    private TextView address;
    private TextView phoneNumber;
    private TextView email;
    private TextView title;
    private TextView des;
    private TextView time;
    private TextView date;
    private Button update;
    private Button delete;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY, "https://www.googleapis.com/auth/calendar",
            "https://www.googleapis.com/auth/calendar.events", "https://www.googleapis.com/auth/calendar.events.readonly"};
    public static ProgressDialog mProgress;
    public UpdateEventDialog dialog;
    private static AppointmentInfoActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_info);

        appointment = getIntent().getParcelableExtra("Appointment");
        instance = this;

        findViewsById();
        setUpView();
        onClick();
    }

    private void findViewsById(){
        image = findViewById(R.id.doctor_imageview);
        name = findViewById(R.id.doctor_name);
        specialty = findViewById(R.id.doctor_specialty);
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        title = findViewById(R.id.title);
        des = findViewById(R.id.des);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        update = findViewById(R.id.update_appointment);
        delete = findViewById(R.id.delete_appointment);
    }

    private void setUpView(){
        try {
            InputStream is = getAssets().open("DoctroApp-a7e5c768f378.json");
            credential = GoogleCredential.fromStream(is)
                    .createScoped(Arrays.asList(SCOPES));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait ...");

        Picasso.get().load(appointment.getChosenDoctor().getImagePath()).fit().into(image);
        name.setText(appointment.getChosenDoctor().getFullName());
        specialty.setText(appointment.getChosenDoctor().getSpecialty());
        address.setText(appointment.getChosenDoctor().getAddress());
        phoneNumber.setText(appointment.getChosenDoctor().getPhoneNumber());
        email.setText(appointment.getChosenDoctor().getEmail());
        title.setText(appointment.getTitle());
        des.setText(appointment.getDes());
        time.setText(appointment.getStartTime() + "-" + appointment.getEndTime());
        try {
            date.setText(formatDate(appointment.getStartDate(), "yyyy-mm-dd", "dd/mm/yyyy"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onClick(){
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new UpdateEventDialog(credential, appointment, Integer.parseInt(appointment.getStartTime().substring(0,2)),
                        Integer.parseInt(appointment.getStartTime().substring(3,5)), Integer.parseInt(appointment.getStartDate().substring(5,7)),
                        Integer.parseInt(appointment.getStartDate().substring(8,10)));
                dialog.show(getSupportFragmentManager(), "dialog");
            }
        });

        //https://stackoverflow.com/questions/8227820/alert-dialog-two-buttons
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(instance);
                builder.setMessage("Are you sure you want to delete your appointment?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new DeleteEvent(credential, instance, appointment.getChosenDoctor().getCalendarId(), appointment.getId()).execute();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    //https://stackoverflow.com/questions/16426703/how-to-convert-a-date-dd-mm-yyyy-to-yyyy-mm-dd-hhmmss-android
    public static String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);

        return parsedDate;
    }

}
