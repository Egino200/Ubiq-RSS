package com.example.ubiqrss;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {
    //create buttons
    Button submit;
    TextView urlInput;
    TextView numInput;

    //create databse references
    DatabaseReference Authy = FirebaseDatabase.getInstance("https://ubiqrss-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    DatabaseReference URL;
    DatabaseReference NUM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //assign values with their database values
        URL = Authy.child("URL");
        NUM = Authy.child("NUM");

        //assign values to their xml counterparts
        setContentView(R.layout.activity_main2);
        urlInput = (TextView)findViewById(R.id.urlInput);
        numInput = (TextView)findViewById(R.id.numInput);
        submit = (Button)findViewById(R.id.button);

        //when the button is clicked send users along with the url and number of articles to the rss feed activity
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL.setValue(urlInput.getText().toString());
                NUM.setValue(numInput.getText().toString());
                switchActivities();
            }
        });

    }

    public void onStart() {
        super.onStart();
        //sets numinput to its firebase value
        NUM.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numText = snapshot.getValue(String.class);
                numInput.setText(numText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //sets urlinput to firebase value
        URL.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String urlText = snapshot.getValue(String.class);
                urlInput.setText(urlText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void switchActivities(){
        Intent switchActivity= new Intent(this, MainActivity.class);
        switchActivity.putExtra("URL", urlInput.getText().toString() );
        switchActivity.putExtra("NUM", numInput.getText().toString());
        startActivity(switchActivity);

    }
}