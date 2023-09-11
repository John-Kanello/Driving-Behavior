package com.example.drivingbehaviour.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivingbehaviour.Classes.DriveResults;
import com.example.drivingbehaviour.Classes.User;
import com.example.drivingbehaviour.HelperClasses.DriveOffences;
import com.example.drivingbehaviour.R;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity{

    private ProgressBar maxprogressBar, avgprogressBar, maxaccelprogressbar, minaccelprogressbar;

    private TextView maxtvProgress,avgtvProgress,maxacceltvProgress,minacceltvProgress,
            stTime,endTime,dur,dist,idle,sBreak,sAccel,sudTurn,startTimeTv,endTimeTv,durationTv,
            distanceTv,idleTimeTv,breakTv,sudAccelTv,vibrationTv;

    int countSpeed, countMaxAcceleration, countMinAcceleration;

    Gson gson = new Gson();
    User user;
    DriveResults driveResults;
    DriveOffences driveOffences;
    String json;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        maxprogressBar = findViewById(R.id.maxprogressbar);
        maxtvProgress = findViewById(R.id.maxtvProgress);
        avgprogressBar = findViewById(R.id.avgprogressbar);
        avgtvProgress = findViewById(R.id.avgtvProgress);
        maxaccelprogressbar = findViewById(R.id.maxaccelprogressbar);
        maxacceltvProgress = findViewById(R.id.maxacceltvProgress);
        minaccelprogressbar = findViewById(R.id.minaccelprogressbar);
        minacceltvProgress = findViewById(R.id.minacceltvProgress);
        stTime = findViewById(R.id.stTime);
        endTime = findViewById(R.id.endTime);
        dur = findViewById(R.id.dur);
        dist = findViewById(R.id.dist);
        idle = findViewById(R.id.idle);
        sBreak = findViewById(R.id.sBreak);
        sAccel = findViewById(R.id.sAccel);
        sudTurn = findViewById(R.id.sudTurn);
        startTimeTv = findViewById(R.id.startTimeTv);
        endTimeTv = findViewById(R.id.endTimeTv);
        durationTv = findViewById(R.id.durationTv);
        distanceTv = findViewById(R.id.distanceTv);
        idleTimeTv = findViewById(R.id.idleTimeTv);
        breakTv = findViewById(R.id.breakTv);
        sudAccelTv = findViewById(R.id.sudAccelTv);
        vibrationTv = findViewById(R.id.vibrationTv);


        TextView continueBtn = findViewById(R.id.nextBtn);
        ImageView nextArrow = findViewById(R.id.nextArrow);

        continueBtn.setOnClickListener(v -> next());
        nextArrow.setOnClickListener(v -> next());


        initializeObjects();

        setMaxAndAvgSpeed();

        setUpMaxAcceleration(driveResults);
        setUpMinAcceleration(driveResults);

        setFontBasedOnLanguage();

    }

    private void initializeObjects()
    {
        driveResults = gson.fromJson(getIntent().getStringExtra("results"), DriveResults.class);
        driveOffences = gson.fromJson(getIntent().getStringExtra("offences"), DriveOffences.class);


        maxtvProgress.setText(String.valueOf(driveResults.getMaxSpeed()));
        avgtvProgress.setText(String.valueOf(driveResults.getAvgSpeed()));
        startTimeTv.setText(driveResults.getStartHour());
        endTimeTv.setText(driveResults.getEndHour());
        distanceTv.setText(String.valueOf(driveResults.getDistance()));
        durationTv.setText(driveResults.getDuration());
        idleTimeTv.setText(driveResults.getIdleTime());
        breakTv.setText(String.valueOf(driveOffences.getHighDeceleration()));
        sudAccelTv.setText(String.valueOf(driveOffences.getHighAcceleration()));
        vibrationTv.setText(String.valueOf(driveOffences.getHighVibration()));
    }

    private void setFontBasedOnLanguage()
    {
        if(SettingsActivity.isGreek)
        {
            stTime.setTypeface(null, Typeface.BOLD);
            stTime.setTextSize(17);
            endTime.setTypeface(null, Typeface.BOLD);
            endTime.setTextSize(17);
            dur.setTypeface(null, Typeface.BOLD);
            dur.setTextSize(17);
            dist.setTypeface(null, Typeface.BOLD);
            dist.setTextSize(17);
            idle.setTypeface(null, Typeface.BOLD);
            idle.setTextSize(17);
            sBreak.setTypeface(null, Typeface.BOLD);
            sBreak.setTextSize(17);
            sAccel.setTypeface(null, Typeface.BOLD);
            sAccel.setTextSize(17);
            sudTurn.setTypeface(null, Typeface.BOLD);
            sudTurn.setTextSize(17);
        }
    }

    private void setMaxAndAvgSpeed()
    {
        final Timer timer = new Timer();
        TimerTask timerTask =   new TimerTask() {
            @Override
            public void run() {

                countSpeed++;

                if(driveResults!=null && countSpeed<=driveResults.getMaxSpeed())
                {
                    maxprogressBar.setProgress(countSpeed);
                }
                if(driveResults!=null && countSpeed<=driveResults.getAvgSpeed())
                {
                    avgprogressBar.setProgress(countSpeed);
                }
                runOnUiThread(() -> {

                    if(driveResults!=null && countSpeed<=driveResults.getMaxSpeed())
                    {
                        maxtvProgress.setText(String.valueOf(countSpeed));
                    }

                    if(driveResults!=null && countSpeed<=driveResults.getAvgSpeed())
                    {
                        avgtvProgress.setText(String.valueOf(countSpeed));
                    }
                });

                if(driveResults!=null && countSpeed >= driveResults.getMaxSpeed())
                {
                    timer.cancel();
                }
            }
        };

        timer.schedule(timerTask, 0, 30);
    }

    private void setUpMaxAcceleration(@NonNull DriveResults driveResults)
    {

        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                countMaxAcceleration++;
                if(countMaxAcceleration<=driveResults.getMaxAcceleration())
                {
                    maxaccelprogressbar.setProgress(countMaxAcceleration);
                }
                runOnUiThread(() -> {

                    if(countMaxAcceleration<=driveResults.getMaxAcceleration())
                    {
                        maxacceltvProgress.setText(String.valueOf(countMaxAcceleration));
                    }
                });

                if(countMaxAcceleration >= driveResults.getMaxAcceleration())
                {
                    timer.cancel();
                }
            }
        };

        timer.schedule(timerTask, 0, 250);
    }

    private void setUpMinAcceleration(@NonNull DriveResults driveResults)
    {
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                countMinAcceleration--;
                if(countMinAcceleration>=driveResults.getMinAcceleration())
                {
                    minaccelprogressbar.setProgress(-countMinAcceleration);
                }
                runOnUiThread(() -> {

                    if(countMinAcceleration>=driveResults.getMinAcceleration())
                    {
                        minacceltvProgress.setText(String.valueOf(countMinAcceleration));
                    }
                });

                if(countMinAcceleration <= driveResults.getMinAcceleration())
                {
                    timer.cancel();
                }
            }
        };

        timer.schedule(timerTask, 0, 250);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        next();
    }

    private void next()
    {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        json = gson.toJson(user);
        intent.putExtra("user", json);
        startActivity(intent);
    }

}