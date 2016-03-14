package com.sirius.madness.receiver.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Object which represents a single Partner from local data storage
 */
public class LocalSurveyQuestions implements Parcelable{
    public static final String CLASS_NAME = "LocalSurveyQuestions";

    //Class variables
    private String qid;
    private String question;

    /**
     * Default constructor
     */
    public LocalSurveyQuestions() {
        qid = null;
        question = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalSurveyQuestions(Parcel in) {
        qid = in.readString();
        question = in.readString();
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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
        out.writeString(qid);
        out.writeString(question);
    }

    public static final Creator<LocalSurveyQuestions> CREATOR
            = new Creator<LocalSurveyQuestions>() {
        public LocalSurveyQuestions createFromParcel(Parcel in) {
            return new LocalSurveyQuestions(in);
        }

        public LocalSurveyQuestions[] newArray(int size) {
            return new LocalSurveyQuestions[size];
        }
    };

    /**
     * Check whether any field has been populated
     * @return <code>true</code> if all fields match the default constructor, <code>false</code> otherwise
     */
    public boolean isEmpty() {
        boolean result = true;

        if(!this.equals(new LocalSurveyQuestions())) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalSurveyQuestions that = (LocalSurveyQuestions) o;

        if (qid != null ? !qid.equals(that.qid) : that.qid != null) return false;
        if (question != null ? !question.equals(that.question) : that.question != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qid.hashCode();
        result = 31 * result + (question != null ? question.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return qid + " - " + question;
    }
}
