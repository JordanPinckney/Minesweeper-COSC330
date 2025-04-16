package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_page);
    }

    public void onClickCreateAccountReal(View view){
        Intent homePage = new Intent(this, HomePageActivity.class);
        startActivity(homePage);
    }
}
