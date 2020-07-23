package gr.hua.dit.it21525.doctorapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DoctorReview implements Parcelable {
    private Patient patient;
    private MyReview review;

    public DoctorReview(Patient patient, MyReview review) {
        this.patient = patient;
        this.review = review;
    }

    public DoctorReview(){}

    protected DoctorReview(Parcel in) {
        patient = in.readParcelable(Patient.class.getClassLoader());
        review = in.readParcelable(MyReview.class.getClassLoader());
    }

    public static final Creator<DoctorReview> CREATOR = new Creator<DoctorReview>() {
        @Override
        public DoctorReview createFromParcel(Parcel in) {
            return new DoctorReview(in);
        }

        @Override
        public DoctorReview[] newArray(int size) {
            return new DoctorReview[size];
        }
    };

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public MyReview getReview() {
        return review;
    }

    public void setReview(MyReview review) {
        this.review = review;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(patient, i);
        parcel.writeParcelable(review, i);
    }
}
