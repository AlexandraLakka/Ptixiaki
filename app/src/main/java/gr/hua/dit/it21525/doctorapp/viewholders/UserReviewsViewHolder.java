package gr.hua.dit.it21525.doctorapp.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;

public class UserReviewsViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView image;
    public TextView userName;
    public TextView review;

    public UserReviewsViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.user_imageview);
        userName = itemView.findViewById(R.id.username);
        review = itemView.findViewById(R.id.review);
    }

    public void setItem(DoctorReview doctorReview){
        Picasso.get().load(doctorReview.getPatient().getImagePath()).fit().into(image);
        userName.setText(doctorReview.getPatient().getUsername());
        review.setText(doctorReview.getReview().getReview());
    }
}
