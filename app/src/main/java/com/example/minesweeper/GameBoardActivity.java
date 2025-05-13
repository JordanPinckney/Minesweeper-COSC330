package com.example.minesweeper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.GridLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


import java.util.ArrayList;
import java.util.Random;

public class GameBoardActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseFirestore db;

    public static String soloLevel = PlaySoloActivity.level;
    private Button[][] gameBoard;
    private int[][] board;
    private int BOMB_NUMBER;
    private int bsize = 5;

    //dimensions for each board
    private static final int brow = 8;
    private static final int bcol = 8;

    private static final int irow = 16;
    private static final int icol = 16;

    private static final int arow = 30;
    private static final int acol = 16;
    private static int ROW;
    private static int COLUMN;
    private static Boolean started;
    private Boolean isMultiplayer;
    int[] seconds;
    LinearLayout holdBoard;
    Handler handler;
    Runnable runnable;
    TextView timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_template);

        isMultiplayer = getIntent().getBooleanExtra("isMultiplayer", false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        started = false;
        // Get the level from Intent
        String soloLevel = getIntent().getStringExtra("level");
        chooseBoard(soloLevel);

        // Popup page after player clicked a bomb or player won the game
        LinearLayout gameOverPopup = findViewById(R.id.GameOverPopup);
        Button againBtn = findViewById(R.id.btnAgain);
        Button backBtn = findViewById(R.id.btnBack);
        againBtn.setOnClickListener(this::OnClickAgain);
        backBtn.setOnClickListener(this::OnClickBack);

        // for timer
        seconds = new int[]{0};
        timer = findViewById(R.id.timer);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                seconds[0]++;
                int totalSeconds = seconds[0];
                int minutes = totalSeconds / 60;
                int sec = totalSeconds % 60;
                String timeFormatted = String.format("%d:%02d", minutes, sec);
                timer.setText(timeFormatted);
                handler.postDelayed(this, 1000);  // Repeat every 1 second
            }
        };

        if (isMultiplayer) {
            handler.post(runnable);
            String matchID = getIntent().getStringExtra("matchID");
            String myUID = user.getUid();

            // Listen for opponent winning
            FirebaseDatabase.getInstance()
                    .getReference("matches")
                    .child(matchID)
                    .child("status")
                    .child("winner")
                    .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) return;
                            String winnerUID = snapshot.getValue(String.class);
                            if (!winnerUID.equals(myUID)) {
                                handler.removeCallbacks(runnable);
                                disableBoard();
                                showYouLosePopup();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
                    });

            // Listen for end-of-game choices (back)
            FirebaseDatabase.getInstance()
                    .getReference("matches")
                    .child(matchID)
                    .child("end_decisions")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                if ("back".equals(child.getValue(String.class))) {
                                    Intent intent = new Intent(GameBoardActivity.this, HomePageActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

        }
    }


    //this chooses a board based off of the level decided, default means
    //race was chosen
    public void chooseBoard(String level){
        switch(level){
            case "beginner":
                ROW = brow;
                COLUMN = bcol;
                BOMB_NUMBER = 10;
                Log.d("LEVEL", level);
                break;
            case "intermediate":
                ROW = irow;
                COLUMN = icol;
                BOMB_NUMBER = 40;
                Log.d("LEVEL", level);
                break;
            case "advanced":
                ROW = arow;
                COLUMN = acol;
                BOMB_NUMBER = 99;
                Log.d("LEVEL", level);
                break;
            default:
                ROW = brow;
                COLUMN = bcol;
                BOMB_NUMBER = 10;
                Log.d("LEVEL", "default");
                break;
        }
        buildBoard();
    }


    public void buildBoard() {
        holdBoard = findViewById(R.id.board_template);
        holdBoard.removeAllViews(); // Clear previous views if any

        // Create GridLayout instead of LinearLayout
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(COLUMN);
        gridLayout.setRowCount(ROW);
        gridLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        gridLayout.setBackgroundColor(Color.GRAY);




        gameBoard = new Button[ROW][COLUMN];

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                Button button = new Button(this);
                button.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
                button.setId(View.generateViewId());
                button.setPadding(0,0,0,0);
                button.setBackgroundResource(R.drawable.border_button);
                button.setText("");
                //button.setGravity(Gravity.FILL);
                button.setOnClickListener(this::OnClickButton);
                button.setTag(R.id.button_position_tag, new int[]{i, j});


                gameBoard[i][j] = button;
                gridLayout.addView(button);
            }
        }
        holdBoard.addView(gridLayout);
    }

    // action after the button clicked
    private void OnClickButton(View view) {
        int[] pos = (int[]) view.getTag(R.id.button_position_tag);
        int i = pos[0];
        int j = pos[1];
        if (!started) {
            placeBomb(i, j);
            started = true;
            if (!isMultiplayer) {
                handler.post(runnable);
            }
        }
        // if clicked cell was 0
        if (board[i][j] == 0) {
            setBoard(i, j, "update");
        }
        // if clicked cell was a bomb
        else if (board[i][j] == -1) {
            // stops the timer
            if (!isMultiplayer) {
                handler.removeCallbacks(runnable);
            }
            // set bomb picture on the -1 cells
            // disable buttons
            for (int row = 0; row < ROW; row++) {
                for (int col = 0; col < COLUMN; col++) {
                    if (board[row][col] == -1) {
                        gameBoard[row][col].setBackgroundResource(R.drawable.bomb);
                    }
                    gameBoard[row][col].setEnabled(false);
                }
            }
            gameOver();
        }
        else {
            gameBoard[i][j].setText(String.valueOf(board[i][j]));
        }
        checkWin();
    }

    // randomly place bombs
    private void placeBomb(int row, int col) {
        board = new int[ROW][COLUMN];
        // store Array that stores all the positions of each bomb
        ArrayList<ArrayList<Integer>> bombPosArray = new ArrayList<>();
        Random rand = new Random();

        // initialize board
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                board[i][j] = 0;
            }
        }

        for (int i = 0; i < BOMB_NUMBER; i++) {
            int r = rand.nextInt(ROW);
            int c = rand.nextInt(COLUMN);

            // first clicked button can't be a bomb.
            if (r != row && c != col) {
                // set the bomb value as -1
                board[r][c] = -1;
                ArrayList<Integer> bombPos = new ArrayList<>();
                bombPos.add(r);
                bombPos.add(c);
                // avoid same place
                if (bombPosArray.contains(bombPos)) {
                    i--;
                }
                else {
                    bombPosArray.add(bombPos);
                }
            }
            else {
                // skipping this for loop by decrement i value
                i--;
            }
        }
        for (int k = 0; k < BOMB_NUMBER; k++) {
            int r = bombPosArray.get(k).get(0);
            int c = bombPosArray.get(k).get(1);
            setBoard(r, c, "create");
        }
    }

    // set the board
    private void setBoard(int r, int c, String order) {
        int istart = -1, iend = 1, jstart = -1, jend = 1;

        // decide start point of loop and end point of loop so if the bomb
        // is on the edge, it can ignore the elements that's out of the grid
        if (r == 0) {
             istart = 0;
        }
        if (r == ROW - 1) {
            iend = 0;
        }
        if (c == 0) {
            jstart = 0;
        }
        if (c == COLUMN - 1) {
            jend = 0;
        }

        if (order.equals("create")) {
            // inclement the number of cells that's surrounding the bomb.
            for (int i = istart; i <= iend; i++) {
                for (int j = jstart; j <= jend; j++) {
                    // make sure not to inclement bomb cell
                    if (board[r + i][c + j] != -1) {
                        board[r + i][c + j] += 1;
                    }
                }
            }
        }
        else if (order.equals("update")) {
            for (int i = istart; i <= iend; i++) {
                for (int j = jstart; j <= jend; j++) {
                    // make sure gameBoard is not updated yet
                    if (gameBoard[r + i][c + j].getText() == "") {
                        gameBoard[r + i][c + j].setText(String.valueOf(board[r + i][c + j]));
                        if (board[r + i][c + j] == 0) {
                            // use " " instead of "" so the text in a button can stay blank
                            // but be able to classify if the button is revealed or not
                            gameBoard[r + i][c + j].setText(" ");
                            gameBoard[r + i][c + j].setBackgroundColor(Color.GRAY);
                            setBoard(r + i, c + j, "update");
                        }
                    }
                }
            }
        }
    }

    private void gameOver() {
        if (isMultiplayer) {
            holdBoard.removeAllViews();
            Toast.makeText(this, "BOOM! Reset board!", Toast.LENGTH_SHORT).show();
            started = false;
            buildBoard();
        }
        else {
            handler.removeCallbacks(runnable);
            LinearLayout gameOverPopup = findViewById(R.id.GameOverPopup);
            TextView gameOverMessage = findViewById(R.id.GameOverMessage);
            gameOverMessage.setText("Game Over!");
            gameOverPopup.setVisibility(View.VISIBLE);
        }
    }

    private void OnClickAgain(View view) {
        holdBoard.removeAllViews();
        started = false;
        seconds = new int[]{0};
        timer.setText("");
        buildBoard();
        findViewById(R.id.GameOverPopup).setVisibility(View.GONE);
    }

    private void OnClickBack(View view) {
        if (isMultiplayer) {
            // If you're navigating immediately, send correct intent data:
            Intent intent = new Intent(this, HomePageActivity.class);
            startActivity(intent);
            finish();
        } else {
            // solo mode
            Intent intent = new Intent(this, PlaySoloActivity.class);
            startActivity(intent);
            finish();
        }
    }



    private void checkWin() {
        boolean won = true;

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                // If any non-bomb cell is still not revealed, player has not won yet
                if (board[i][j] != -1 && gameBoard[i][j].getText().equals("")) {
                    won = false;
                    break;
                }
            }
            if (!won) break;
        }

        if (won) {
            // Stop the timer
            handler.removeCallbacks(runnable);

            // update firebase to let another player know you won the game
            if (isMultiplayer) {
                FirebaseDatabase.getInstance()
                        .getReference("matches")
                        .child(getIntent().getStringExtra("matchID"))
                        .child("status")
                        .child("winner")
                        .setValue(user.getUid());
                // Hide the "Again" button in multiplayer mode
                findViewById(R.id.btnAgain).setVisibility(View.GONE);
            }

            String currentTime = timer.getText().toString();

            // Upload to backend
            Map<String, Object> data = new HashMap<>();
            data.put("duration", currentTime);
            data.put("timestamp", System.currentTimeMillis());

            String[] parts = currentTime.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            int durationInSeconds = minutes * 60 + seconds;

            data.put("seconds", durationInSeconds);

            db.collection("times")
                    .add(data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firebase", "Game ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firebase", "Error adding game result", e);
                    });

            // Show popup with "You Won!"
            LinearLayout gameOverPopup = findViewById(R.id.GameOverPopup);
            TextView gameOverMessage = findViewById(R.id.GameOverMessage);
            gameOverMessage.setText("You Won! (" + currentTime + ")");
            gameOverPopup.setVisibility(View.VISIBLE);

            // Disable all buttons
            disableBoard();
        }
    }

    // if your opponent finished before you finish, your board is disabled
    private void disableBoard() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                gameBoard[i][j].setEnabled(false);
            }
        }
    }

    // show you lost the game on the screen
    private void showYouLosePopup() {
        LinearLayout gameOverPopup = findViewById(R.id.GameOverPopup);
        TextView gameOverMessage = findViewById(R.id.GameOverMessage);
        gameOverMessage.setText("You Lose!");

        // Hide the "Again" button in multiplayer mode
        findViewById(R.id.btnAgain).setVisibility(View.GONE);
        gameOverPopup.setVisibility(View.VISIBLE);
    }

}
