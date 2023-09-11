package com.example.drivingbehaviour.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivingbehaviour.Classes.DriveResults;
import com.example.drivingbehaviour.Classes.User;
import com.example.drivingbehaviour.R;
import com.example.drivingbehaviour.Retrofit.RetrofitClient;
import com.google.gson.Gson;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriveHistoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ProgressBar maxprogressBar, avgprogressBar, maxaccelprogressbar, minaccelprogressbar;

    Spinner spinner;
    ArrayAdapter<String> adapter;

    List<DriveResults> driveResults;

    TextView maxtvProgress, avgtvProgress, maxacceltvProgress, minacceltvProgress, distanceVal, durationTv, idleTv, breakTv,
    sudAccelTv, vibrationTv, startTimeTv, endTimeTv, dateTv, stTime,endTime,dur,dist,idle,sBreak,sAccel,sudTurn,date,nextBtn;

    ImageView nextArrow;

    int countSpeed, countMaxAcceleration, countMinAcceleration;

    User user;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drive_history);

        maxprogressBar = findViewById(R.id.maxprogressbar);
        maxtvProgress = findViewById(R.id.maxtvProgress);
        avgprogressBar = findViewById(R.id.avgprogressbar);
        avgtvProgress = findViewById(R.id.avgtvProgress);
        maxaccelprogressbar = findViewById(R.id.maxaccelprogressbar);
        maxacceltvProgress = findViewById(R.id.maxacceltvProgress);
        minaccelprogressbar = findViewById(R.id.minaccelprogressbar);
        minacceltvProgress = findViewById(R.id.minacceltvProgress);

        distanceVal = findViewById(R.id.distanceTv);
        durationTv = findViewById(R.id.durationTv);
        idleTv = findViewById(R.id.idleTimeTv);
        breakTv = findViewById(R.id.breakTv);
        sudAccelTv = findViewById(R.id.sudAccelTv);
        vibrationTv = findViewById(R.id.vibrationTv);
        startTimeTv = findViewById(R.id.startTimeTv);
        endTimeTv = findViewById(R.id.endTimeTv);
        dateTv = findViewById(R.id.dateTv);
        stTime = findViewById(R.id.stTime);
        endTime = findViewById(R.id.endTime);
        dur = findViewById(R.id.dur);
        dist = findViewById(R.id.dist);
        idle = findViewById(R.id.idle);
        sBreak = findViewById(R.id.sBreak);
        sAccel = findViewById(R.id.sAccel);
        sudTurn = findViewById(R.id.sudTurn);
        date = findViewById(R.id.date);
        nextBtn = findViewById(R.id.nextBtn);

        nextArrow = findViewById(R.id.nextArrow);

        spinner = findViewById(R.id.spinner);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        nextBtn.setOnClickListener(v -> next());
        nextArrow.setOnClickListener(v -> next());


        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);

        Call<List<DriveResults>> call = RetrofitClient
                .getInstance()
                .getUserAPI()
                .getUserInformation(user.getUserId());

        call.enqueue(new Callback<List<DriveResults>>() {
            @Override
            public void onResponse(@NonNull Call<List<DriveResults>> call, @NonNull Response<List<DriveResults>> response) {

                adapter.add("Choose:");
                adapter.notifyDataSetChanged();

                int size = 0;
                if (response.body() != null) {
                    size = response.body().size();
                }

                while (size>0) {
                    adapter.add("session" + size);
                    size--;
                    adapter.notifyDataSetChanged();
                }

                driveResults = response.body();
            }

            @Override
            public void onFailure(@NonNull Call<List<DriveResults>> call, @NonNull Throwable t) {

            }
        });

        setFontBasedOnLanguage();
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
            date.setTypeface(null, Typeface.BOLD);
            date.setTextSize(17);
        }
    }

    private void setMaxAndAvgSpeed(DriveResults driveResults)
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

        timer.schedule(timerTask, 0, 20);

        countSpeed = 0;
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

        timer.schedule(timerTask, 0, 200);

        countMaxAcceleration = 0;
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

        timer.schedule(timerTask, 0, 200);

        countMinAcceleration = 0;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position>0) {

            maxprogressBar.setProgress(0);
            avgprogressBar.setProgress(0);
            maxaccelprogressbar.setProgress(0);
            minaccelprogressbar.setProgress(0);

            getData(driveResults.get(driveResults.size()-position));
            setMaxAndAvgSpeed(driveResults.get(driveResults.size()-position));
            setUpMaxAcceleration(driveResults.get(driveResults.size()-position));
            setUpMinAcceleration(driveResults.get(driveResults.size()-position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getData(DriveResults driveResults)
    {
        distanceVal.setText(String.valueOf(driveResults.getDistance()));
        maxtvProgress.setText(String.valueOf(driveResults.getMaxSpeed()));
        maxacceltvProgress.setText(String.valueOf(driveResults.getMaxAcceleration()));
        durationTv.setText(String.valueOf(driveResults.getDuration()));
        avgtvProgress.setText(String.valueOf(driveResults.getAvgSpeed()));
        minacceltvProgress.setText(String.valueOf(driveResults.getMinAcceleration()));
        idleTv.setText(String.valueOf(driveResults.getIdleTime()));
        breakTv.setText(String.valueOf(driveResults.getSuddenBreaks()));
        sudAccelTv.setText(String.valueOf(driveResults.getSuddenAccelerations()));
        vibrationTv.setText(String.valueOf(driveResults.getSuddenTurns()));
        startTimeTv.setText(String.valueOf(driveResults.getStartHour()));
        endTimeTv.setText(String.valueOf(driveResults.getEndHour()));
        dateTv.setText(String.valueOf(driveResults.getDate()));

    }

    public void onBackPressed() {

        super.onBackPressed();

        next();
    }

    private void next() {

        Intent in = new Intent(DriveHistoryActivity.this, UserProfileActivity.class);
        String json = gson.toJson(user);
        in.putExtra("user", json);
        startActivity(in);
    }

}