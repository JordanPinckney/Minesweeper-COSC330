package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.UUID;

public class PrepareRaceActivity extends AppCompatActivity {

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepare_race);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String myEmail = user.getEmail().replace(".", "_");
        DatabaseReference invitationRef = FirebaseDatabase.getInstance()
                .getReference("invitations").child(myEmail);

        invitationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Invitation invite = snapshot.getValue(Invitation.class);
                    showRaceInvitePopup(invite, myEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    //This function should also send the challenge to the opponent (networking part)
    //That functionality can be in it's own function but it needs to be called here
    public void onClickStartRace(View view) {
        EditText opponentInput = findViewById(R.id.opponents_username);
        String opponentEmail = opponentInput.getText().toString().trim().replace(".", "_");
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (opponentEmail.isEmpty()) {
            Toast.makeText(this, "Enter opponent email", Toast.LENGTH_SHORT).show();
            return;
        }

        String myUID = user.getUid();
        String matchID = UUID.randomUUID().toString();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        // Send invitation to opponent
        db.child("invitations").child(opponentEmail).setValue(new Invitation(myUID, matchID));
        db.child("matches").child(matchID).child("players").child("player1").setValue(myUID);
        db.child("matches").child(matchID).child("status").child(myUID).setValue("waiting");

        // Save matchID to shared location
        Intent intent = new Intent(this, PlaySoloActivity.class);
        intent.putExtra("matchID", matchID);
        intent.putExtra("role", "host");
        startActivity(intent);
    }
    private void showRaceInvitePopup(Invitation invite, String myEmail) {
        new AlertDialog.Builder(this)
                .setTitle("Race Invitation")
                .setMessage("You've been invited to a race!")
                .setPositiveButton("Accept", (dialog, which) -> {
                    String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // Join the match
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("matches").child(invite.matchID).child("players").child("player2").setValue(myUID);
                    db.child("matches").child(invite.matchID).child("status").child(myUID).setValue("waiting");

                    // Remove invitation
                    db.child("invitations").child(myEmail).removeValue();

                    // Move to level selection screen
                    Intent intent = new Intent(this, PlaySoloActivity.class);
                    intent.putExtra("matchID", invite.matchID);
                    intent.putExtra("role", "guest");
                    startActivity(intent);
                })
                .setNegativeButton("Decline", (dialog, which) -> {
                    // Remove the invitation
                    FirebaseDatabase.getInstance().getReference("invitations").child(myEmail).removeValue();

                    // Set declined status
                    FirebaseDatabase.getInstance().getReference("matches")
                            .child(invite.matchID)
                            .child("status")
                            .child("declined").setValue(true);

                    dialog.dismiss();
                })
                .show();
    }

    public void onClickPageBack(View view) {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}

