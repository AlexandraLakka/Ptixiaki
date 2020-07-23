package gr.hua.dit.it21525.doctorapp.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Doctor implements Parcelable {
    private String uid;
    private String imagePath;
    private String fullName;
    private String address;
    private String phoneNumber;
    private String workingHours;
    private String userName;
    private String email;
    private String specialty;
    private String role;
    private String calendarId;

    public Doctor() {
    }

    public Doctor(String uid, String fullName, String address, String phoneNumber, String workingHours, String userName, String email, String specialty, String imagePath, String calendarId) {
        this.uid = uid;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.workingHours = workingHours;
        this.userName = userName;
        this.email = email;
        this.specialty = specialty;
        this.imagePath = imagePath;
        this.calendarId = calendarId;
    }

    public Doctor(String uid, String imagePath, String fullName, String address, String phoneNumber, String workingHours, String userName, String email, String specialty, String role, String calendarId) {
        this.uid = uid;
        this.imagePath = imagePath;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.workingHours = workingHours;
        this.userName = userName;
        this.email = email;
        this.specialty = specialty;
        this.role = role;
        this.calendarId = calendarId;
    }

    protected Doctor(Parcel in) {
        uid = in.readString();
        imagePath = in.readString();
        fullName = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        workingHours = in.readString();
        userName = in.readString();
        email = in.readString();
        specialty = in.readString();
        role = in.readString();
        calendarId = in.readString();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //https://medium.com/@nikhildhyani365/understand-parcelable-in-android-27ce420d695b
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(imagePath);
        parcel.writeString(fullName);
        parcel.writeString(address);
        parcel.writeString(phoneNumber);
        parcel.writeString(workingHours);
        parcel.writeString(userName);
        parcel.writeString(email);
        parcel.writeString(specialty);
        parcel.writeString(role);
        parcel.writeString(calendarId);
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };
}
