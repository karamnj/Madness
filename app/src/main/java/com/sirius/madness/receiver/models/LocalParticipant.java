package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Object which represents a single Developer from local data storage
 */
public class LocalParticipant implements Parcelable{
    private static final String CLASS_NAME = "LocalParticipant";

    //Class variables
    private String firstName;
    private String lastName;
    private String phone;
    private String mailId;
    private String company;
    private String empId;
    private String jobTitle;
    private String twitterLink;
    private String linkedInLink;
    private String userName;
    private String password;



    /**
     * Default constructor
     */
    public LocalParticipant() {
        firstName = null;
        lastName = null;
        phone = null;
        mailId = null;
        company = null;
        empId = null;
        jobTitle = null;
        twitterLink = null;
        linkedInLink = null;
        userName = null;
        password = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalParticipant(Parcel in) {
        firstName = in.readString();;
        lastName = in.readString();;
        phone = in.readString();;
        mailId = in.readString();;
        company = in.readString();;
        empId = in.readString();;
        jobTitle = in.readString();;
        twitterLink = in.readString();;
        linkedInLink = in.readString();;
        userName = in.readString();;
        password = in.readString();;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getLinkedInLink() {
        return linkedInLink;
    }

    public void setLinkedInLink(String linkedInLink) {
        this.linkedInLink = linkedInLink;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int describeContents() {
        return 0;
    }



    /**
     * Implementation of method from "Parcelable" interface. Describes the data to be written
     * when this class is packaged into a Parcel, and in what order to package it
     * @param out
     * @param flags
     */
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(phone);
        out.writeString(mailId);
        out.writeString(company);
        out.writeString(empId);
        out.writeString(jobTitle);
        out.writeString(twitterLink);
        out.writeString(linkedInLink);
        out.writeString(userName);
        out.writeString(password);
    }

    public static final Creator<LocalParticipant> CREATOR
            = new Creator<LocalParticipant>() {
        public LocalParticipant createFromParcel(Parcel in) {
            return new LocalParticipant(in);
        }

        public LocalParticipant[] newArray(int size) {
            return new LocalParticipant[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalParticipant())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalParticipant that = (LocalParticipant) o;

        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
            return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (mailId != null ? !mailId.equals(that.mailId) : that.mailId != null) return false;
        if (company != null ? !company.equals(that.company) : that.company != null)
            return false;
        if (empId != null ? !empId.equals(that.empId) : that.empId != null)
        return false;
        if (jobTitle != null ? !jobTitle.equals(that.jobTitle) : that.jobTitle != null)
        return false;
        if (twitterLink != null ? !twitterLink.equals(that.twitterLink) : that.twitterLink != null)
            return false;
        if (linkedInLink != null ? !linkedInLink.equals(that.linkedInLink) : that.linkedInLink != null)
            return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = empId.hashCode();
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (mailId != null ? mailId.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (empId != null ? empId.hashCode() : 0);
        result = 31 * result + (jobTitle != null ? jobTitle.hashCode() : 0);
        result = 31 * result + (twitterLink != null ? twitterLink.hashCode() : 0);
        result = 31 * result + (linkedInLink != null ? linkedInLink.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return empId + " - " + firstName + " " + lastName;
    }
}
