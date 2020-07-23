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
import gr.hua.dit.it21525.doctorapp.models.Doctor;
import gr.hua.dit.it21525.doctorapp.viewholders.DoctorSearchViewHolder;

public class DoctorSearchResultsAdapter extends RecyclerView.Adapter<DoctorSearchViewHolder> {

    private Context context;
    private ArrayList<Doctor> doctorArrayList;

    public DoctorSearchResultsAdapter(Context context, ArrayList<Doctor> doctorArrayList) {
        this.context = context;
        this.doctorArrayList = doctorArrayList;
    }

    @NonNull
    @Override
    public DoctorSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_doctor_item, parent, false);
        return new DoctorSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorSearchViewHolder holder, int position) {
        Doctor currentDoctor = doctorArrayList.get(position);
        String name = currentDoctor.getFullName();
        String specialty = currentDoctor.getSpecialty();
        String address = currentDoctor.getAddress();

        Picasso.get().load(currentDoctor.getImagePath()).fit().into(holder.image);
        holder.name.setText(name);
        holder.specialty.setText(specialty);
        holder.address.setText(address);
    }

    @Override
    public int getItemCount() {
        return doctorArrayList.size();
    }
}


