package gr.hua.dit.it21525.doctorapp.views;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.hua.dit.it21525.doctorapp.R;
import gr.hua.dit.it21525.doctorapp.models.DoctorReview;

public class UserReviewsView extends Item<GroupieViewHolder> {
    private DoctorReview doctorReview;

    public UserReviewsView(DoctorReview doctorReview){
        this.doctorReview = doctorReview;
    }

    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
        CircleImageView image = viewHolder.itemView.findViewById(R.id.user_imageview);
        TextView userName = viewHolder.itemView.findViewById(R.id.username);
        TextView review = viewHolder.itemView.findViewById(R.id.review);

        Picasso.get().load(doctorReview.getPatient().getImagePath()).fit().into(image);
        userName.setText(doctorReview.getPatient().getUsername());
        review.setText(doctorReview.getReview().getReview());
    }

    @Override
    public int getLayout() {
        return R.layout.user_review_item;
    }
}
