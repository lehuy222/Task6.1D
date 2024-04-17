package com.example.ass6;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ass6.databinding.ActivityResultBinding;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ArrayList<String> correctAnswers = getIntent().getStringArrayListExtra("CORRECTED_ANSWERS");
        ArrayList<String> selectedAnswers = getIntent().getStringArrayListExtra("SELECTED_ANSWERS");

        Log.d("dmmm", correctAnswers.get(0));
        Log.d("dmmm", selectedAnswers.get(0));
        if (correctAnswers != null && selectedAnswers != null) {
            for (int i = 0; i < selectedAnswers.size(); i++) {
                int correctId = getResources().getIdentifier("tv_correct_answer_" + (i + 1), "id", getPackageName());
                int chosenId = getResources().getIdentifier("tv_chosen_answer_" + (i + 1), "id", getPackageName());

                TextView tvCorrectAnswer = findViewById(correctId);
                TextView tvChosenAnswer = findViewById(chosenId);

                // Set the text for the correct and chosen answers
                tvCorrectAnswer.setText(String.format("Correct Answer: %s", correctAnswers.get(i)));
                tvChosenAnswer.setText(String.format("Your Answer: %s", selectedAnswers.get(i)));

                // Set the background color based on whether the answers match
                int colorId = correctAnswers.get(i).equals(selectedAnswers.get(i)) ?
                        android.R.color.holo_green_light : android.R.color.holo_red_light;
                tvChosenAnswer.setBackgroundColor(getResources().getColor(colorId));
            }
        } else {
            // Handle the case where there are no answers or the lists do not match in size
            Toast.makeText(this, "There was an error retrieving the quiz results.", Toast.LENGTH_LONG).show();
        }
    }
}