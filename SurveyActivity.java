package com.sirius.madness.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.beans.SurveyQuestionsBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.receiver.models.BluemixSurveyAnswers;
import com.sirius.madness.ui.views.CustomFontTextView;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class SurveyActivity extends ActionBarActivity {

    private static final String CLASS_NAME = "Survey";
    private List<SurveyQuestionsBean> survey_questions;
    private String sessionId;
    private String sessionName;
    private String participantId;
    private String participantName;
    private String participantNumber;
    private String participantEmail;
    private boolean error;
    SharedPreferences prefs;
    private ArrayList<String> surveyResult = new ArrayList<String>(Arrays.asList("", "", "", "", ""));

    private final String[] projectionList = {
            IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_QID,
            IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_QUESTION,
            IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_OPTION1,
            IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_OPTION2,
            IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_OPTION3,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        prefs = PreferenceManager.getDefaultSharedPreferences(SurveyActivity.this);

        sessionId = getIntent().getStringExtra("sessionId");
        sessionName = getIntent().getStringExtra("sessionName");

        participantId = prefs.getString("participantId", null);
        participantName = prefs.getString("participantName", null);
        participantNumber = prefs.getString("participantNumber", null);
        participantEmail = prefs.getString("participantEmail", null);
        LinearLayout layout = (LinearLayout) findViewById(R.id.surveyList);

        Cursor cursor = getContentResolver().query(IgniteContract.SurveyQuestions.CONTENT_URI, projectionList, null, null, IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_QID);
        survey_questions = new ArrayList<SurveyQuestionsBean>();
        initDataset(cursor);

        //If we have survey_questions data, show it to the user
        if(survey_questions != null && survey_questions.size() > 0) {
            Log.d(CLASS_NAME, "survey_questions is > 0");
            for(int position = 0; position < survey_questions.size(); position++){
                layout.addView(initSurvey(position));
            }
        }
        //If there's no session data, display an error
        else {
            Log.d(CLASS_NAME, "session_data is null or empty");
            layout.setVisibility(ListView.GONE);
            TextView surveyError = (TextView) findViewById(R.id.surveyErrorMessage);
            surveyError.setVisibility(View.VISIBLE);
        }
    }

    public void initDataset(Cursor cursor) {

        Log.d(CLASS_NAME, "Entering initDataset");

        cursor.moveToFirst();
        int i = 0;
        if (cursor.getCount() > 0) {
            do {
                SurveyQuestionsBean survey = new SurveyQuestionsBean();
                survey.setQid(cursor.getString(cursor.getColumnIndex(IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_QID)));
                survey.setQuestion(cursor.getString(cursor.getColumnIndex(IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_QUESTION)));
                survey.setOption1(cursor.getString(cursor.getColumnIndex(IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_OPTION1)));
                survey.setOption2(cursor.getString(cursor.getColumnIndex(IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_OPTION2)));
                survey.setOption3(cursor.getString(cursor.getColumnIndex(IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_OPTION3)));
                survey_questions.add(survey);
                i++;
            } while (cursor.moveToNext());
        }else {
            Log.d(CLASS_NAME, "Cursor returned no results!");
        }
        Log.d(CLASS_NAME, "Exiting initDataset");
    }

    public View initSurvey(final int position){
        LayoutInflater inflater = this.getLayoutInflater();
        final SurveyQuestionsBean bean = survey_questions.get(position);
        final View rowView;
        if(position==4){
            rowView = inflater.inflate(R.layout.layout_survey_submit, null, false);
            //final EditText feedback = (EditText) rowView.findViewById(R.id.Feedback);
            TextView button = (TextView) rowView.findViewById(R.id.submitFeedback);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if(!feedback.getText().toString().equals("")){
                        surveyResult.set(position,feedback.getText().toString());
                    }*/
                    if(validate()){
                        if(isNetworkAvailable()) {
                            storeSurveyResults();
                        }else{
                            Toast.makeText(SurveyActivity.this, "No Internet Connectivity", Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(SurveyActivity.this, "Thank You for the Feedback. Have a nice day", Toast.LENGTH_SHORT).show();
                        SurveyActivity.this.finish();
                    }else{
                        Toast.makeText(SurveyActivity.this, "Please ensure all Questions are answered", Toast.LENGTH_SHORT).show();
                        //AlertDialog();
                    }
                }
            });
        }else{
            rowView = inflater.inflate(R.layout.layout_survey_question, null, false);

            CustomFontTextView question = (CustomFontTextView) rowView.findViewById(R.id.Question);
            question.setText(bean.getQuestion());
            CustomFontTextView questionNumber = (CustomFontTextView) rowView.findViewById(R.id.question_number);
            questionNumber.setText(""+(position+1));
            RadioButton option1 = (RadioButton) rowView.findViewById(R.id.option1);
            option1.setText(bean.getOption1());
            RadioButton option2 = (RadioButton) rowView.findViewById(R.id.option2);
            option2.setText(bean.getOption2());
            RadioButton option3 = (RadioButton) rowView.findViewById(R.id.option3);
            option3.setText(bean.getOption3());
            RadioGroup radioGroup = (RadioGroup) rowView.findViewById(R.id.options);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.option1:
                            surveyResult.set(position, bean.getOption1());
                            break;
                        case R.id.option2:
                            surveyResult.set(position, bean.getOption2());
                            break;
                        case R.id.option3:
                            surveyResult.set(position, bean.getOption3());
                            break;
                    }
                }
            });

            /*LinearLayout surveyListBackground = (LinearLayout) rowView.findViewById(R.id.surveyItemBackground);
            int color;
            if(position%2==0){
                color = getColorForCell(0);
            }else{
                color = getColorForCell(1);
            }
            surveyListBackground.setBackgroundColor(color);*/
        }

        return rowView;
    }

    private Boolean validate()
    {
        int i = surveyResult.size()-2;
        do{
            if(surveyResult.get(i).equals("")){
                return false;
            }
            i--;
        }while(i>=0);
        return true;
    }
    public void storeSurveyResults(){
        IBMQuery<BluemixSurveyAnswers> query = IBMQuery.queryForClass("SurveyAnswers");
        // Query all the BluemixParticipant objects from the server

        query.whereKeyEqualsTo("sessionId", sessionId);
        query.whereKeyEqualsTo("participantId", participantId);
        query.find().onSuccess(new Continuation<List<BluemixSurveyAnswers>, Void>() {
            @Override
            public Void then(Task<List<BluemixSurveyAnswers>> task) throws Exception {
                final List<BluemixSurveyAnswers> objects = task.getResult();

                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception in BluemixSurveyAnswers : Task " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception in BluemixSurveyAnswers : " + task.getError().getMessage());
                } else {
                    if (objects.size() == 0) {
                        for(int i = 0; i < surveyResult.size(); i++) {
                            Log.d("Registering: ", participantId);
                            BluemixSurveyAnswers surveyAnswers = new BluemixSurveyAnswers();

                            surveyAnswers.setSessionId(sessionId);
                            surveyAnswers.setParticipantId(participantId);
                            surveyAnswers.setQuestionId(String.valueOf(i + 1));
                            surveyAnswers.setAnswer(surveyResult.get(i));
                            surveyAnswers.setSessionName(sessionName);
                            surveyAnswers.setQuestion(survey_questions.get(i).getQuestion());
                            surveyAnswers.setParticipantName(participantName);
                            surveyAnswers.setParticipantNumber(participantNumber);
                            surveyAnswers.setParticipantEmail(participantEmail);

                            // Use the IBMDataObject to create and persist the Item object.
                            surveyAnswers.save().continueWith(new Continuation<IBMDataObject, Void>() {

                                @Override
                                public Void then(Task<IBMDataObject> task) throws Exception {
                                    // Log if the save was cancelled.
                                    if (task.isCancelled()) {
                                        Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                                        error = true;
                                    }
                                    // Log error message, if the save task fails.
                                    else if (task.isFaulted()) {
                                        Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                                        error = true;
                                    }else{
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("surveyCompleted", true);
                                        editor.commit();
                                    }
                                    return null;
                                }

                            });
                            if(error){
                                break;
                            }
                        }
                        if(error){
                            Log.d("Feedback:", "Feedback Unsuccessful, Please ");
                        }else {
                            Toast.makeText(SurveyActivity.this, "Feedback Successful", Toast.LENGTH_SHORT).show();
                            Log.d("Feedback:", "Feedback Details Added Successfully");
                        }
                    } else {
                        Log.d("Feedback:", "Survey Already Submitted");
                    }
                }
                return null;
            }
        });
    }
    /*private void AlertDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage("Please ensure all Questions are answered").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }*/

    private int getColorForCell(int index) {

        int red;
        int blue;
        int green;
        int alpha = 255;

        if (index > 0) {
            index = index % 2;
        }

        switch (index) {
            case 0:
                red = 255;
                green = 160;
                blue = 57;
                //result = R.color.theme_tertiary_light_orange;
                break;
            case 1:
                red = 5;
                green = 147;
                blue = 188;
                //result = R.color.theme_secondary_blue;
                break;

            default:
                red = 255;
                green = 160;
                blue = 57;
                //result = R.color.theme_tertiary_light_orange;
                break;
        }

        return Color.argb(alpha, red, green, blue);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
