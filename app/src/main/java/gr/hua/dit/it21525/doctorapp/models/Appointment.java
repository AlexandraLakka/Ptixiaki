package gr.hua.dit.it21525.doctorapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Appointment implements Parcelable {
    private String id;
    private String title;
    private String location;
    private String des;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private Doctor chosenDoctor;

    public Appointment(){

    }

    public Appointment(String id, String title, String location, String des, String startDate, String startTime, String endDate, String endTime, Doctor chosenDoctor) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.des = des;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.chosenDoctor = chosenDoctor;
    }

    protected Appointment(Parcel in) {
        id = in.readString();
        title = in.readString();
        location = in.readString();
        des = in.readString();
        startDate = in.readString();
        startTime = in.readString();
        endDate = in.readString();
        endTime = in.readString();
        chosenDoctor = in.readParcelable(Doctor.class.getClassLoader());
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Doctor getChosenDoctor() {
        return chosenDoctor;
    }

    public void setChosenDoctor(Doctor chosenDoctor) {
        this.chosenDoctor = chosenDoctor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(location);
        parcel.writeString(des);
        parcel.writeString(startDate);
        parcel.writeString(startTime);
        parcel.writeString(endDate);
        parcel.writeString(endTime);
        parcel.writeParcelable(chosenDoctor, i);
    }
}
