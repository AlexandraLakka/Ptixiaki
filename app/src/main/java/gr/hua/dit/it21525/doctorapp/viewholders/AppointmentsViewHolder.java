package gr.hua.dit.it21525.doctorapp.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.activities.AppointmentInfoActivity;
import gr.hua.dit.it21525.doctorapp.models.Appointment;

public class AppointmentsViewHolder extends RecyclerView.ViewHolder{
    public LinearLayout root;
    public CircleImageView image;
    public TextView name;
    public TextView title;
    public TextView startDate;
    public Button rateButton;

    public AppointmentsViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.root);
        image = itemView.findViewById(R.id.doctor_imageview);
        name = itemView.findViewById(R.id.doctor_name);
        title = itemView.findViewById(R.id.appointment_title);
        startDate = itemView.findViewById(R.id.appointment_start_date);
        rateButton = itemView.findViewById(R.id.rate_button);
    }

    public void setItem(Appointment appointment){
        Picasso.get().load(appointment.getChosenDoctor().getImagePath()).fit().into(image);
        name.setText(appointment.getChosenDoctor().getFullName());
        title.setText(appointment.getTitle());
        try {
            startDate.setText(AppointmentInfoActivity.formatDate(appointment.getStartDate(), "yyyy-mm-dd",
                    "dd/mm/yyyy") + ", " + appointment.getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
