package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomePageActivity extends AppCompatActivity {

    FirebaseUser user;
    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        email = findViewById(R.id.insert_email);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null ){
            email.setText(user.getEmail());
        }else{

        }
    }

    //This navigates to the solo screen
    public void onClickPlaySolo(View view){
        Intent playSolo = new Intent(this, PlaySoloActivity.class);
        startActivity(playSolo);
    }

    //This navigates to the race screen
    public void onClickPlayRace(View view){
        Intent playRace = new Intent(this, PrepareRaceActivity.class);
        startActivity(playRace);
    }

    public void onClickLeaderboard(View view){
        Intent leaderboard = new Intent(this, LeaderBoardActivity.class);
        startActivity(leaderboard);
    }

    //
    public void onClickLogOut(View view){
        Intent logOut = new Intent(this, TitlePageActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(logOut);
    }
}
