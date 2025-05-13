package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlaySoloActivity extends AppCompatActivity {

    public static String level = "";
    FirebaseUser user;
    private boolean matchReady = false;
    private boolean isGuest = false;
    private String matchID;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_solo_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        matchID = getIntent().getStringExtra("matchID");
        role = getIntent().getStringExtra("role");
        isGuest = "guest".equals(role);

        // If no multiplayer info is provided, you're in solo mode
        if (matchID == null || role == null) {
            matchReady = true; // No need to wait in solo mode
            return;
        }

        // Multiplayer logic
        FirebaseDatabase.getInstance().getReference("matches")
                .child(matchID).child("status").child("declined")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue(Boolean.class)) {
                            Toast.makeText(PlaySoloActivity.this, "Opponent declined the invitation", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PlaySoloActivity.this, PrepareRaceActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        DatabaseReference playersRef = FirebaseDatabase.getInstance()
                .getReference("matches").child(matchID).child("players");

        playersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("player1") && snapshot.hasChild("player2")) {
                    matchReady = true;

                    if (isGuest) {
                        DatabaseReference levelRef = FirebaseDatabase.getInstance()
                                .getReference("matches").child(matchID).child("level");

                        levelRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String level = snapshot.getValue(String.class);
                                    Intent intent = new Intent(PlaySoloActivity.this, GameBoardActivity.class);
                                    intent.putExtra("matchID", matchID);
                                    intent.putExtra("level", level);
                                    intent.putExtra("isMultiplayer", true);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void onClickBeginner(View view) {
        chooseLevel("beginner");
    }

    public void onClickIntermediate(View view) {
        chooseLevel("intermediate");
    }

    public void onClickAdvanced(View view) {
        chooseLevel("advanced");
    }

    private void chooseLevel(String level) {
        if (matchID == null || role == null) {
            // Solo mode: Start game directly
            Intent intent = new Intent(PlaySoloActivity.this, GameBoardActivity.class);
            intent.putExtra("level", level);
            intent.putExtra("isMultiplayer", false);
            startActivity(intent);
            finish();
            return;
        }

        if (!matchReady) {
            Toast.makeText(this, "Waiting for opponent to join.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!"host".equals(role)) {
            Toast.makeText(this, "Only the host can select the level.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("matches").child(matchID).child("level").setValue(level);

        Intent intent = new Intent(PlaySoloActivity.this, GameBoardActivity.class);
        intent.putExtra("matchID", matchID);
        intent.putExtra("level", level);
        intent.putExtra("isMultiplayer", true);
        startActivity(intent);
        finish();
    }

    public void onClickPageBack(View view) {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}
