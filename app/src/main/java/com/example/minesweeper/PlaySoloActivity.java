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

    public void onClickBeginner(View view){
        Intent beginnerBoard = new Intent(this, GameBoardActivity.class);
        beginnerBoard.putExtra("level", "beginner");  // Pass level here
        startActivity(beginnerBoard);
    }

    public void onClickIntermediate(View view){
        Intent intermediateBoard = new Intent(this, GameBoardActivity.class);
        intermediateBoard.putExtra("level", "intermediate");
        startActivity(intermediateBoard);
    }

    public void onClickAdvanced(View view){
        Intent advancedBoard = new Intent(this, GameBoardActivity.class);
        advancedBoard.putExtra("level", "advanced");
        startActivity(advancedBoard);
    }
}
