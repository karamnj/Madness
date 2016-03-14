package com.sirius.madness.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;
import com.sirius.madness.R;
import com.sirius.madness.beans.SurveyQuestionsBean;
import com.sirius.madness.provider.IgniteContract;
import com.sirius.madness.receiver.models.BluemixSurveyAnswers;
import com.sirius.madness.ui.views.CustomFontTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class SurveyActivity extends BaseActivity {

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
    private ArrayList<String> surveyResult = new ArrayList<String>(Arrays.asList("", "", "", ""));
    Spinner spinner;

    private final String[] projectionList = {
            IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_QID,
            IgniteContract.SurveyQuestions.SURVEY_QUESTIONS_QUESTION,
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
            // Spinner element
            LayoutInflater inflater = this.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.layout_survey_role, null, false);
            TextView roleLabel = (TextView) rowView.findViewById(R.id.role_label);
            String text = "<font color=#2b2f35>"+roleLabel.getText()+"</font> <font color=#dd0000>*</font>";
            roleLabel.setText(Html.fromHtml(text));
            spinner = (Spinner) rowView.findViewById(R.id.spinner);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // On selecting a spinner item
                    String item = parent.getItemAtPosition(position).toString();
                    spinner.setPrompt(item);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.role_type, R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setPrompt(getResources().getString(R.string.spinner_default));
            TextView arrow = (TextView) rowView.findViewById(R.id.downArrow);
            arrow.setText(getResources().getString(R.string.custom_font_icon_down_arrow));
            layout.addView(rowView);

            //Layout Additions
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
        if(position==(survey_questions.size()-1)){
            rowView = inflater.inflate(R.layout.layout_survey_submit, null, false);
            CustomFontTextView question = (CustomFontTextView) rowView.findViewById(R.id.Question);
            question.setText(bean.getQuestion()+":");
            CustomFontTextView questionNumber = (CustomFontTextView) rowView.findViewById(R.id.question_number);
            questionNumber.setText("" + (position + 1));
            final EditText feedback = (EditText) rowView.findViewById(R.id.comments);
            TextView button = (TextView) rowView.findViewById(R.id.submitFeedback);
            TextView arrow = (TextView) rowView.findViewById(R.id.rightArrow);
            arrow.setText(getResources().getString(R.string.custom_font_icon_right_arrow));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!feedback.getText().toString().equals("")) {
                        surveyResult.set(position, feedback.getText().toString());
                    }
                    if (validate()) {
                        if (isNetworkAvailable()) {
                            storeSurveyResults();
                        } else {
                            Toast.makeText(SurveyActivity.this, "No Internet Connectivity", Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(SurveyActivity.this, "Thank You for the Feedback. Have a nice day", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        SurveyActivity.this.finish();
                    } else {
                        Toast.makeText(SurveyActivity.this, "Please ensure all Questions are answered", Toast.LENGTH_SHORT).show();
                        //AlertDialog();
                    }
                }
            });
        }else{
            rowView = inflater.inflate(R.layout.layout_survey_question, null, false);

            CustomFontTextView question = (CustomFontTextView) rowView.findViewById(R.id.Question);
            String questionText = bean.getQuestion();
            String[] separated = questionText.split("1=");
            String text = "<font color=#2b2f35>"+separated[0]+"</font> <font color=#dd0000>*</font><br> <font color=#909193>1="+separated[1]+"</font>";
            question.setText(Html.fromHtml(text));
            CustomFontTextView questionNumber = (CustomFontTextView) rowView.findViewById(R.id.question_number);
            questionNumber.setText("" + (position + 1));

            SeekBar seekBar = (SeekBar) rowView.findViewById(R.id.seekbar);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    surveyResult.set(position, (progress+1)+"");
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        return rowView;
    }

    private Boolean validate()
    {
        int i = surveyResult.size()-2;
        do{
            if(surveyResult.get(i).equals("")){
                Log.d("Validate", "SurveyResult:"+surveyResult.get(i));
                return false;
            }
            i--;
        }while(i>=0);
        if(spinner.getSelectedItem().toString().equals("Select One")){
            Log.d("Validate", "Spinner:"+spinner.getSelectedItem().toString());
            return false;
        }
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
                            surveyAnswers.setRole(spinner.getSelectedItem().toString());

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
                                    } else {
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


}
