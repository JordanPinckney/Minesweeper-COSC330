package com.example.minesweeper;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class GameBoardActivity extends AppCompatActivity {

    public static String soloLevel = PlaySoloActivity.level;
    private Button[][] gameBoard;
    private int bsize = 5;

    //dimensions for each board
    private static final int brow = 8;
    private static final int bcol = 8;

    private static final int irow = 16;
    private static final int icol = 16;

    private static final int arow = 30;
    private static final int acol = 16;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_template);
        chooseBoard(soloLevel);

    }


    //this chooses a board based off of the level decided, default means
    //race was chosen
    public void chooseBoard(String level){
        switch(level){
            case "beginner":
                buildBoard(brow, bcol);
                break;
            case "intermediate":
                buildBoard(irow, icol);
                break;
            case "advanced":
                buildBoard(arow, acol);
                break;
            default:
                buildBoard(brow, bcol);
                break;
        }
    }

    public void buildBoard(int row, int col){
        LinearLayout holdBoard = findViewById(R.id.board_template);
        gameBoard = new Button[row][col];

        for(int i = 0; i<row; i++){
            LinearLayout gameRows = new LinearLayout(this);
            gameRows.setOrientation(LinearLayout.HORIZONTAL);
            for(int j = 0; j<col; j++){
                Button button = new Button(this);
                button.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                button.setPadding(bsize, bsize, bsize, bsize);
                button.setText("");
                button.setTag("button_"+i+"_"+j);

                gameBoard[i][j] = button;
                gameRows.addView(button);
            }
            holdBoard.addView(gameRows);
        }
    }

}
