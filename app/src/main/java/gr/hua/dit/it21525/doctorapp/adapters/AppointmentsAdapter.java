package gr.hua.dit.it21525.doctorapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.Appointment;
import gr.hua.dit.it21525.doctorapp.viewholders.AppointmentsViewHolder;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsViewHolder> {

    private Context context;
    private ArrayList<Appointment> appointmentList;

    public AppointmentsAdapter(Context context, ArrayList<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        String name = appointment.getChosenDoctor().getFullName();
        String title = appointment.getTitle();
        String startDate = appointment.getStartDate() + ", " + appointment.getStartTime();

        Picasso.get().load(appointment.getChosenDoctor().getImagePath()).fit().into(holder.image);
        holder.name.setText(name);
        holder.title.setText(title);
        holder.startDate.setText(startDate);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }
}
