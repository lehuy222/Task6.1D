package com.example.ass6;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    TextView questionTv, nextQuestionTv;
    RadioGroup optionsRadioGroup;
    Button switchBt, submitBt;
    List<Question> questions;
    private int currentQuestionIndex = 0;
    // URL of the API endpoint
    private static final String QUIZ_API_URL = "http://10.0.2.2:5000/getQuiz?topic=Science";

    // Volley request queue
    private RequestQueue requestQueue;
    ArrayList<String> correctAnswers = new ArrayList<>();
    ArrayList<String> selectedAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTv = findViewById(R.id.questionTv);
        nextQuestionTv = findViewById(R.id.nextQuestionTv);
        optionsRadioGroup = findViewById(R.id.radioGroup);
        switchBt = findViewById(R.id.switchBt);
        submitBt = findViewById(R.id.submitBt);

        requestQueue = Volley.newRequestQueue(this);
        // Fetch questions from the Python API
        fetchQuestions();

        switchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedOptionId = optionsRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedOptionId);
                selectedAnswers.add(selectedRadioButton.getText().toString());

                if (currentQuestionIndex < questions.size() - 1) {
                    currentQuestionIndex++;
                    displayCurrentQuestion();
                } else {
                    nextQuestionTv.setText("All question Finished!");
                    switchBt.setEnabled(false);
                    finishQuestionnaire();
                }
            }
        });
    }

    private void displayCurrentQuestion() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionTv.setText(currentQuestion.getQuestionText());
        optionsRadioGroup.removeAllViews(); // Clear previous options

        for (String option : currentQuestion.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            optionsRadioGroup.addView(radioButton);
        }

        // This is necessary to avoid having a pre-selected option when the question changes
        optionsRadioGroup.clearCheck();
    }

    private void fetchQuestions() {
        // Create a request for retrieving the quiz data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                QUIZ_API_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray quizArray = response.getJSONArray("quiz");
                            questions = parseQuestionsJson(quizArray);
                            if (!questions.isEmpty()) {
                                displayCurrentQuestion();
                            }
                        } catch (JSONException e) {
                            Log.e("Quiz API", "Error parsing JSON response", e);
                            // Handle parsing error
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("Quiz API", "Error fetching quiz data", error);
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, // Timeout in ms. Default is 2500
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Number of retries. Default is 1
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); // Backoff multiplier

        requestQueue.add(jsonObjectRequest);
        // Add the request to the RequestQueue to execute it
        requestQueue.add(jsonObjectRequest);
    }

    private List<Question> parseQuestionsJson(JSONArray jsonArray) {
        List<Question> parsedQuestions = new ArrayList<>();
        try {
            // Loop through the JSON array and create Question objects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject questionJson = jsonArray.getJSONObject(i);
                String questionText = questionJson.getString("question");
                String correctAnswer = questionJson.getString("correct_answer");
                JSONArray optionsJsonArray = questionJson.getJSONArray("options");
                if(correctAnswer.equals("A")){
                    correctAnswers.add(optionsJsonArray.getString(0));
                } else if (correctAnswer.equals("B")) {
                    correctAnswers.add(optionsJsonArray.getString(1));
                } else if (correctAnswer.equals("C")) {
                    correctAnswers.add(optionsJsonArray.getString(2));
                } else if (correctAnswer.equals("D")) {
                    correctAnswers.add(optionsJsonArray.getString(3));
                }
                List<String> options = new ArrayList<>();
                for (int j = 0; j < optionsJsonArray.length(); j++) {
                    options.add(optionsJsonArray.getString(j));
                }
                // Create a new Question object including the correct answer
                Question question = new Question(questionText, options, correctAnswer);
                parsedQuestions.add(question);
            }
        } catch (JSONException e) {
            Log.e("Quiz API", "Error parsing JSON response", e);
        }
        return parsedQuestions;
    }

    private void finishQuestionnaire() {
        // TODO: Handle the result, pass the selectedAnswers to the ResultActivity
        Intent resultIntent = new Intent(QuizActivity.this, ResultActivity.class);
        resultIntent.putStringArrayListExtra("SELECTED_ANSWERS", selectedAnswers);
        resultIntent.putStringArrayListExtra("CORRECTED_ANSWERS", correctAnswers);
        startActivity(resultIntent);
    }

//    private List<Question> getDummyQuestions() {
//        // This method should be replaced with actual API call
//        // Here we create dummy data to simulate the API response
//        List<Question> dummyQuestions = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            List<String> options = Arrays.asList("Option 1", "Option 2", "Option 3");
//            Question question = new Question("Question " + (i + 1), options);
//            dummyQuestions.add(question);
//        }
//        return dummyQuestions;
//    }

    static class Question {
        private String questionText;
        private List<String> options;
        private String correctAnswer;

        public Question(String questionText, List<String> options, String correctAnswer) {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestionText() {
            return questionText;
        }

        public List<String> getOptions() {
            return options;
        }

        public String getCorrectAnswer(){
            return correctAnswer;
        }
    }
}
