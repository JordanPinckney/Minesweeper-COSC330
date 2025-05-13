package com.example.minesweeper;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LeaderBoardActivity extends AppCompatActivity {
    private LinearLayout leaderboardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_page);

        leaderboardContainer = findViewById(R.id.leaderboard_page);

        FirebaseFirestore.getInstance().collection("times")
                .orderBy("seconds") // Order by duration in seconds (fastest first)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String duration = doc.getString("duration");
                        if (duration != null) {
                            TextView timeView = new TextView(LeaderBoardActivity.this);
                            timeView.setText(duration);
                            timeView.setTextSize(24);
                            timeView.setTextColor(Color.WHITE);
                            timeView.setPadding(50, 10, 0, 10);
                            leaderboardContainer.addView(timeView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting leaderboard data", e);
                });
    }
}