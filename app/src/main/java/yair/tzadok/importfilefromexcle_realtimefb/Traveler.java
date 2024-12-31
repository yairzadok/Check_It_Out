package yair.tzadok.importfilefromexcle_realtimefb;

public class Traveler {

    private String firstName, lastName, travelerId, phoneNumber, email;
    private boolean status;
    private String travelerAttendanceState;

    public Traveler(String firstName, String lastName, String travelerId, String phoneNumber, String email, boolean status, String travelerAttendanceState) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.travelerId = travelerId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.status = status;
        this.travelerAttendanceState = travelerAttendanceState;
    }

    public Traveler() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(String travelerId) {
        this.travelerId = travelerId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTravelerAttendanceState() {
        return travelerAttendanceState;
    }

    public void setTravelerAttendanceState(String travelerAttendanceState) {
        this.travelerAttendanceState = travelerAttendanceState;
    }


    @Override
    public String toString() {
        String attendanceReportComponent = firstName + " " + lastName + " - ";
        attendanceReportComponent += travelerAttendanceState.equals("true") ? "present" : "absent";
        return attendanceReportComponent;
    }
}

