package com.example.ubiqrss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity3 extends AppCompatActivity {

    Button toLoginBut, registerBut ;
    TextView emailInput, passInput, confirmPassInput;
    ProgressDialog progressDialog;

    FirebaseAuth Authy;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        //assign variables to their xml counterparts
        registerBut = (Button)findViewById(R.id.registerBut);
        emailInput=(TextView)findViewById(R.id.emailTv);
        passInput = (TextView)findViewById(R.id.passwordTv);
        confirmPassInput=(TextView)findViewById(R.id.confirmPassTv);

        progressDialog= new ProgressDialog(this);


        Authy=FirebaseAuth.getInstance();
        user = Authy.getCurrentUser();

        toLoginBut = (Button) findViewById(R.id.toLoginBut);

        //login button brings users to login page
        toLoginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               switchActivities();
            }
        });
        registerBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerforAuth();
            }
        });
    }
    //registers a user on the firebase
    private void PerforAuth() {
        //greabs values from textviews and sets them to string
        String email = emailInput.getText().toString();
        String password = passInput.getText().toString();
        String confpass = confirmPassInput.getText().toString();

        //checks for a number of issues before assigning them to the database
        if(email.isEmpty()){
            emailInput.setError("Please Enter Email");
        }else if(!password.equals(confpass)){
            passInput.setError("Passwords do not match");
        }
        else if(password.length() < 6){
            passInput.setError("Please enter a password with 6 or more letters");
        }
        //if the values inputted are valid create a user in the firebase
        else{
        progressDialog.setMessage("Please wait while registering...");
        progressDialog.setTitle("Registration");
        progressDialog.setCanceledOnTouchOutside(false);
        //show a progress dialog when loading
        progressDialog.show();

        Authy.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            //once loaded successfully the user is brought to the login page
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();

                    switchActivities();

                }
                else{
                    progressDialog.dismiss();

                }
            }
        });


        }
    }
    //uses intents to switch activities
    public void switchActivities(){
        Intent switchActivity= new Intent(this, MainActivity4.class);
        startActivity(switchActivity);
    }
}