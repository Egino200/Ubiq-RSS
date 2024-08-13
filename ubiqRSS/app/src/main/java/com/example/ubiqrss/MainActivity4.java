package com.example.ubiqrss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity4 extends AppCompatActivity {
    DatabaseReference database = FirebaseDatabase.getInstance("https://ubiqrss-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    TextView usernameInput, passwordInput;
    Button toRegister, loginBut;
    ProgressDialog progressDialog;
    FirebaseAuth Authy;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Authy=FirebaseAuth.getInstance();
        user=Authy.getCurrentUser();
        //assign values to their xml counterparts
        progressDialog = new ProgressDialog(this);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBut=findViewById(R.id.loginBut);
        toRegister = (Button) findViewById(R.id.goregisterBut);

        //sends users to the register page
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
        //attempts to log in the user
        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              login();
            }
        });
    }

    private void login(){
        //takes in values from users through textviews
        String email = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        //checks to see if values entered are valid
        if(email.isEmpty()){
            usernameInput.setError("Please Enter Email");
        }else if(password.length() < 6){
            passwordInput.setError("Please enter a password with 6 or more letters");
        }
        //if there is no issues show progressdialog and begin to sign in the user
        else {
            progressDialog.setMessage("Please wait while loggin in...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        //attempt to sign in user with firebase method
            Authy.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                //if successful set the value of the firebase email and switch to the actual application
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        database.child("email").setValue(email);
                        switchToApp();
                    }else{
                        //if it fails show a toast
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity4.this, "Login Failed, please try again", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
    public void switchActivities(){
        Intent switchActivity= new Intent(this, MainActivity3.class);
        startActivity(switchActivity);
    }
    public void switchToApp(){
        Intent switchActivity= new Intent(this, MainActivity2.class);
        startActivity(switchActivity);
    }

}