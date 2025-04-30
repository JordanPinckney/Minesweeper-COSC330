package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PrepareRaceActivity extends AppCompatActivity {

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepare_race);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    //This function should also send the challenge to the opponent (networking part)
    //That functionality can be in it's own function but it needs to be called here
    public void onClickStartRace(View view){
        Intent raceBoard = new Intent(this, GameBoardActivity.class);
        startActivity(raceBoard);
    }
}
