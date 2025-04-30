package com.example.minesweeper;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountPageActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText email;

    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_page);

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);


        auth = FirebaseAuth.getInstance();
    }


    public void onClickCreateAccountReal(View view){

        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if(userEmail.isEmpty()){
            Toast.makeText(CreateAccountPageActivity.this, "You need to enter your email.",
                    Toast.LENGTH_SHORT).show();
        }else if(userPassword.isEmpty()){
            Toast.makeText(CreateAccountPageActivity.this, "You need to enter your password.",
                    Toast.LENGTH_SHORT).show();
        }else {
            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(CreateAccountPageActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
        Intent homePage = new Intent(this, HomePageActivity.class);
        startActivity(homePage);
    }

    private void updateUI(FirebaseUser user){

    }
}
