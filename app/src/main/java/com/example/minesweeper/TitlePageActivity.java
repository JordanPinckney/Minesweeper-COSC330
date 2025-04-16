package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class TitlePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_page);
    }

    //navigation
    public void onClickCreateAccount(View view){
        Intent createAccount = new Intent(this, CreateAccountPageActivity.class);
        startActivity(createAccount);
    }

    public void onClickLogIn(View view){
        Intent homePage = new Intent(this, HomePageActivity.class);
        startActivity(homePage);
    }
}