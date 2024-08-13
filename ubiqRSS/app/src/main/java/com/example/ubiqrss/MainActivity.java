package com.example.ubiqrss;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int DOWNLOAD_JOB_KEY = 101;

    //declare database references
    DatabaseReference firebase = FirebaseDatabase.getInstance("https://ubiqrss-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    DatabaseReference email = firebase.child("email");
    DatabaseReference addHistory;

    //declare variables
    int NUM;
    String URL;
    ListView lvRss;

    //declare arraylist to store values
    ArrayList<String> titles = new ArrayList<>() ;
    ArrayList<String> subtitles = new ArrayList<>();
    ArrayList<String> links =new ArrayList<>();
    ArrayList<Drawable> pics = new ArrayList<>();
    Button returnToSetting;
    String EMAIL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        addHistory = firebase.child("UserID");
        //set variables to their xml counterparts
        lvRss = (ListView) findViewById(R.id.listRss);
        returnToSetting = (Button) findViewById(R.id.returnToSet);

        //set variables to the intent values which were passed
        NUM = Integer.parseInt(getIntent().getStringExtra("NUM"));
        URL = getIntent().getStringExtra("URL");

        //when the button is clicked return to settings
        returnToSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });

        //when an article is clicked store the users email, the title and description in the firebase
        // it will then redirect to the website
        lvRss.setOnItemClickListener((adapterView, view, i, l) -> {
            Uri uri = Uri.parse(links.get(i));
            History history = new History(EMAIL, titles.get(i), subtitles.get(i));
            addHistory.child("User").push().setValue(history);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        new processInBackground().execute();
       // initScheduler();

        // sets emails value to the databases value
        email.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EMAIL = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public InputStream getInputStream(URL url){

        try {
            return url.openConnection().getInputStream();
        }catch (IOException e){
            return null;
        }
    }

    public class processInBackground extends AsyncTask<Integer, Void, Exception>{

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        Exception exception = null;
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            //shows a progressdialog while the rss loads
            progressDialog.setMessage("Loading rss feed, Please wait");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... params) {
            try {
                //declare values
                int limiter = 0;
                URL url = new URL(URL);
                //create and configure parser
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(getInputStream(url), "UTF_8");


                boolean withinItem = false;

                int eventType = parser.getEventType();
                // help with logic came from https://www.youtube.com/watch?v=_rl7X172AWk&t=1s&ab_channel=CodeclubMaster
                //once the loop finds an item tag it gets the values from the desired tags and stores them in arrays
                //when it assigns an image the limiter is incremented. when this value matches the num assigned by the user the loop stops
                //setting the number of news articles displayed
                while (eventType != XmlPullParser.END_DOCUMENT && limiter < NUM){
                    if(eventType == XmlPullParser.START_TAG){
                        if(parser.getName().equalsIgnoreCase("item")){
                            withinItem = true;
                        }
                        else if(parser.getName().equalsIgnoreCase("title")){
                            if(withinItem){
                                titles.add(parser.nextText());

                            }
                        }
                        else if(parser.getName().equalsIgnoreCase("description")){
                            if(withinItem){
                                subtitles.add(parser.nextText());
                            }
                        }
                        else if(parser.getName().equalsIgnoreCase("link")){
                            if(withinItem){
                                links.add(parser.nextText());
                            }
                        }
                        else if(parser.getName().equalsIgnoreCase("media:content")){
                            if(withinItem){
                                try {
                                    InputStream is = (InputStream) new URL(parser.getAttributeValue(null, "url")).getContent();
                                    Drawable d = Drawable.createFromStream(is, "src name");
                                    pics.add(d);
                                    limiter++;
                                }catch (Exception e){
                                    return null;
                                }
                            }
                        }
                        else if(parser.getName().equalsIgnoreCase("enclosure")){
                            if(withinItem){
                                try {
                                    InputStream is = (InputStream) new URL(parser.getAttributeValue(null, "url")).getContent();
                                    Drawable d = Drawable.createFromStream(is, "src name");
                                    pics.add(d);
                                    limiter++;
                                }catch (Exception e){
                                    return null;
                                }
                            }
                        }


                    }else if(eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("item")){
                        withinItem = false;
                    }
                    eventType = parser.next();
                }
            }catch(MalformedURLException e){
                exception = e;
            }
            catch (XmlPullParserException e){
                exception = e;
            }
            catch (IOException e){
                exception = e;
            }

            return exception;
        }

        //when the rss is parsed the custom adapter is set to the list and the dialog is dismissed
        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
            MyListAdapter adapter = new MyListAdapter(MainActivity.this, titles, subtitles, pics);
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);
            lvRss.setAdapter(adapter);
            progressDialog.dismiss();

        }
    }
    //uses an intent to swap to the settings intent
    public void switchActivities(){
        Intent switchActivity= new Intent(this, MainActivity2.class);
        startActivity(switchActivity);

    }
}