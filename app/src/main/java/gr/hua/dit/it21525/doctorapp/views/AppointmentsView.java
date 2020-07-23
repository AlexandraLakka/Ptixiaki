package gr.hua.dit.it21525.doctorapp.views;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.AppointmentInfoActivity;
import gr.hua.dit.it21525.doctorapp.activities.RatingActivity;
import gr.hua.dit.it21525.doctorapp.models.Appointment;

public class AppointmentsView extends Item<GroupieViewHolder> {

    private Appointment appointment;
    private Context context;

    public AppointmentsView(Appointment appointment, Context context) {
        this.appointment = appointment;
        this.context = context;
    }

    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
        CircleImageView image = viewHolder.itemView.findViewById(R.id.doctor_imageview);
        TextView name = viewHolder.itemView.findViewById(R.id.doctor_name);
        TextView title = viewHolder.itemView.findViewById(R.id.appointment_title);
        TextView startDate = viewHolder.itemView.findViewById(R.id.appointment_start_date);
        Button rateButton = viewHolder.itemView.findViewById(R.id.rate_button);

        Calendar c = Calendar.getInstance();
        String date = c.get(Calendar.YEAR) + "-"
                + String.format(Locale.getDefault(), "%02d", c.get(Calendar.MONTH) + 1)
                + "-" + String.format(Locale.getDefault(), "%02d", c.get(Calendar.DAY_OF_MONTH));

        Picasso.get().load(appointment.getChosenDoctor().getImagePath()).fit().into(image);
        name.setText(appointment.getChosenDoctor().getFullName());
        title.setText(appointment.getTitle());
        try {
            startDate.setText(AppointmentInfoActivity.formatDate(appointment.getStartDate(), "yyyy-mm-dd",
                    "dd/mm/yyyy") + ", " + appointment.getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, RatingActivity.class).putExtra("doctor", appointment.getChosenDoctor()));
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.appointment_item;
    }
}
