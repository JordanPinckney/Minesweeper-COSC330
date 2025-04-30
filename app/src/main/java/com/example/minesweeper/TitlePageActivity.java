package com.example.minesweeper;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/*Home page is the page that loads when starting the app, allows for login or navigating to another page
* to log in*/
public class TitlePageActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private EditText email;
    private EditText password;

    private FirebaseAuth auth;
    private FirebaseUser loggedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_page);
        checkLogin(loggedin);
        password = findViewById(R.id.password_login);
        email = findViewById(R.id.email_address);

        auth = FirebaseAuth.getInstance();

    }

    /*Check if the user is logged in*/
    public void checkLogin(FirebaseUser user){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Intent homePage = new Intent(this, HomePageActivity.class);
            startActivity(homePage);
        }
    }

    /*Navigate to the create account screen*/
    public void onClickCreateAccount(View view){
        Intent createAccount = new Intent(this, CreateAccountPageActivity.class);
        startActivity(createAccount);
    }

    /*If the user exists and the username and password match user will be able to login*/
    public void onClickLogIn(View view){

        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        if(userEmail.isEmpty() ){
            Toast.makeText(TitlePageActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }else if(userPassword.isEmpty()){
            Toast.makeText(TitlePageActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }else {

            Intent homePage = new Intent(this, HomePageActivity.class);
            auth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                updateUI(user);
                                startActivity(homePage);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(TitlePageActivity.this, "Wrong email/password. Try again.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }

    }

    private void updateUI(FirebaseUser user){

    }
}