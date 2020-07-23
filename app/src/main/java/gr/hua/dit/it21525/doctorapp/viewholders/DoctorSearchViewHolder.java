package gr.hua.dit.it21525.doctorapp.viewholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.Doctor;


public class DoctorSearchViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    public CircleImageView image;
    public TextView name;
    public TextView specialty;
    public TextView address;

    public DoctorSearchViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        image = itemView.findViewById(R.id.search_doctor_imageview);
        name = itemView.findViewById(R.id.search_doctor_name);
        specialty = itemView.findViewById(R.id.search_doctor_specialty);
        address = itemView.findViewById(R.id.search_doctor_address);
    }

    public void setItem(Doctor doctor){
        Picasso.get().load(doctor.getImagePath()).fit().into(image);
        name.setText(doctor.getFullName());
        specialty.setText(doctor.getSpecialty());
        address.setText(doctor.getAddress());
    }
}

