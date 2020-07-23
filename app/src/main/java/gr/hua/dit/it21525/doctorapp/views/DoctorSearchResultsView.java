package gr.hua.dit.it21525.doctorapp.views;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.Doctor;

public class DoctorSearchResultsView extends Item<GroupieViewHolder> {

    private Doctor currentDoctor;

    public DoctorSearchResultsView(Doctor doctor) {
        this.currentDoctor = doctor;
    }

    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
        CircleImageView image = viewHolder.itemView.findViewById(R.id.search_doctor_imageview);
        TextView name = viewHolder.itemView.findViewById(R.id.search_doctor_name);
        TextView specialty = viewHolder.itemView.findViewById(R.id.search_doctor_specialty);
        TextView address = viewHolder.itemView.findViewById(R.id.search_doctor_address);

        Picasso.get().load(currentDoctor.getImagePath()).fit().into(image);
        name.setText(currentDoctor.getFullName());
        specialty.setText(currentDoctor.getSpecialty());
        address.setText(currentDoctor.getAddress());
    }

    @Override
    public int getLayout() {
        return R.layout.search_doctor_item;
    }
}
