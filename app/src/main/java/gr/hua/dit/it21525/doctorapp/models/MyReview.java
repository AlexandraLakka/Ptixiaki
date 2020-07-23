package gr.hua.dit.it21525.doctorapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MyReview implements Parcelable {
    private Doctor doctor;
    private float rating;
    private String review;

    public MyReview() {

    }

    public MyReview(Doctor doctor, float rating, String review) {
        this.doctor = doctor;
        this.rating = rating;
        this.review = review;
    }

    protected MyReview(Parcel in) {
        doctor = in.readParcelable(Doctor.class.getClassLoader());
        rating = in.readFloat();
        review = in.readString();
    }

    public static final Creator<MyReview> CREATOR = new Creator<MyReview>() {
        @Override
        public MyReview createFromParcel(Parcel in) {
            return new MyReview(in);
        }

        @Override
        public MyReview[] newArray(int size) {
            return new MyReview[size];
        }
    };

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(doctor, i);
        parcel.writeFloat(rating);
        parcel.writeString(review);
    }
}
