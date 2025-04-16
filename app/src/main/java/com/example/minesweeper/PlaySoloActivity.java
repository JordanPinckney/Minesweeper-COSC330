package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PlaySoloActivity extends AppCompatActivity {

    //variable used for determining level of gameplay
    public static String level = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_solo_page);
    }

    //navigates to the game board and marks beginner
    public void onClickBeginner(View view){
        level = "beginner";
        Intent beginnerBoard = new Intent(this, GameBoardActivity.class);
        startActivity(beginnerBoard);

    }

    //navigates to game board and marks intermediate
    public void onClickIntermediate(View view){
        level = "intermediate";
        Intent intermediateBoard = new Intent(this, GameBoardActivity.class);
        startActivity(intermediateBoard);
    }

    //navigates to game board and marks advanced
    public void onClickAdvanced(View view){
        level = "advanced";
        Intent advancedBoard = new Intent(this, GameBoardActivity.class);
        startActivity(advancedBoard);
    }
}
