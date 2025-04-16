package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
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

    //this will eventually hold the log out logic
    public void onClickLogOut(View view){
        Intent logOut = new Intent(this, TitlePageActivity.class);
        startActivity(logOut);
    }
}
