package com.example.jacob.myindependentstudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

public class IntroActivity extends AppCompatActivity {

    private static final String TAG="Main Activity";

    //Function on click to go to main menu
    public void GoToMenu (View view){
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    // display if opencv is loaded in logs
    static{
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV successfully loaded!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        else{
            Log.d(TAG, "OpenCV not Loaded!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //Display in textview if opencv is loaded correctly
        final TextView textView=(TextView)findViewById(R.id.OpenCVstatus);
        if(OpenCVLoader.initDebug()){
            textView.setText("OpenCV loaded");
        }
        else{
            textView.setText("OpenCV did not load");
        }
    }
}
