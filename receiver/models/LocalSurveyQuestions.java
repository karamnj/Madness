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
    private String option1;
    private String option2;
    private String option3;

    /**
     * Default constructor
     */
    public LocalSurveyQuestions() {
        qid = null;
        question = null;
        option1 = null;
        option2 = null;
        option3 = null;
    }

    /**
     * Overloaded constructor to comply with "Parcelable" interface. Describes how to build an
     * object of this type from a Parcel, including the order in which the data must be read.
     * @param in
     */
    public LocalSurveyQuestions(Parcel in) {
        qid = in.readString();
        question = in.readString();
        option1 = in.readString();
        option2 = in.readString();
        option3 = in.readString();
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

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
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
        out.writeString(option1);
        out.writeString(option2);
        out.writeString(option3);
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
        if (option1 != null ? !option1.equals(that.option1) : that.option1 != null) return false;
        if (option2 != null ? !option2.equals(that.option2) : that.option2 != null) return false;
        if (option3 != null ? !option3.equals(that.option3) : that.option3 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qid.hashCode();
        result = 31 * result + (question != null ? question.hashCode() : 0);
        result = 31 * result + (option1 != null ? option1.hashCode() : 0);
        result = 31 * result + (option2 != null ? option2.hashCode() : 0);
        result = 31 * result + (option3 != null ? option3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return qid + " - " + question;
    }
}
