package gr.hua.dit.it21525.doctorapp.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;
import gr.hua.dit.it21525.doctorapp.models.MyReview;

public class MyReviewsViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView image;
    public TextView score;
    public TextView review;

    public MyReviewsViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.doctor_imageview);
        score = itemView.findViewById(R.id.score);
        review = itemView.findViewById(R.id.review);
    }

    public void setItem(DoctorReview item){
        Picasso.get().load(item.getPatient().getImagePath()).fit().into(image);
        score.setText(String.valueOf(item.getReview().getRating()));
        review.setText(item.getReview().getReview());
    }
}
