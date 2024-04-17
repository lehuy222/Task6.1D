package com.example.ass6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class InterestSelectionActivity extends AppCompatActivity {

    Button submitButton;
    List<String> enabledToggleTexts = new ArrayList<>();
    DatabaseHelper db = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_interest_selection);

        submitButton = findViewById(R.id.submitButton);
        // Initialize each toggle button and check if it's enabled
        int[] toggleButtonIds = {R.id.toggleButton1, R.id.toggleButton2, R.id.toggleButton3, R.id.toggleButton4,
                R.id.toggleButton5, R.id.toggleButton6, R.id.toggleButton7, R.id.toggleButton8,
                R.id.toggleButton9, R.id.toggleButton10, R.id.toggleButton11, R.id.toggleButton12,
                R.id.toggleButton13, R.id.toggleButton14, R.id.toggleButton15, R.id.toggleButton16};

        for (int id : toggleButtonIds) {
            ToggleButton toggleButton = findViewById(id);
            if (toggleButton.isEnabled()) {
                // Add the text to the list
                enabledToggleTexts.add(toggleButton.getText().toString());
            }
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getIntent().getStringExtra("USERNAME");
                db.updateUserInterest(username, enabledToggleTexts);
                Intent intent = new Intent(InterestSelectionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}