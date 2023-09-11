package com.example.drivingbehaviour.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivingbehaviour.Classes.AverageDriveResults;
import com.example.drivingbehaviour.Classes.User;
import com.example.drivingbehaviour.HelperClasses.ConvertSecondsToTime;
import com.example.drivingbehaviour.R;
import com.example.drivingbehaviour.Retrofit.RetrofitClient;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriveAverageActivity extends AppCompatActivity {

    ProgressBar maxprogressBar, avgprogressBar, maxaccelprogressbar, minaccelprogressbar;

    TextView maxtvProgress, avgtvProgress, maxacceltvProgress, minacceltvProgress, titleTv, distanceVal, durationTv, idleTv, breakTv,
            sudAccelTv, vibrationTv, avgBtn,dur,dist,idle,sBreak,sAccel,sudTurn,date, nextBtn;

    User user;
    Gson gson = new Gson();

    int countMaxSpeed, countAverageSpeed, countMaxAcceleration, countMinAcceleration;

    ConvertSecondsToTime secondsToTime = new ConvertSecondsToTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drive_average);

        maxprogressBar = findViewById(R.id.maxprogressbar);
        maxtvProgress = findViewById(R.id.maxtvProgress);
        avgprogressBar = findViewById(R.id.avgprogressbar);
        avgtvProgress = findViewById(R.id.avgtvProgress);
        maxaccelprogressbar = findViewById(R.id.maxaccelprogressbar);
        maxacceltvProgress = findViewById(R.id.maxacceltvProgress);
        minaccelprogressbar = findViewById(R.id.minaccelprogressbar);
        minacceltvProgress = findViewById(R.id.minacceltvProgress);

        titleTv = findViewById(R.id.titleTv);
        distanceVal = findViewById(R.id.distanceTv);
        durationTv = findViewById(R.id.durationTv);
        idleTv = findViewById(R.id.idleTimeTv);
        breakTv = findViewById(R.id.breakTv);
        sudAccelTv = findViewById(R.id.sudAccelTv);
        vibrationTv = findViewById(R.id.vibrationTv);
        avgBtn = findViewById(R.id.avgBtn);
        dur = findViewById(R.id.dur);
        dist = findViewById(R.id.dist);
        idle = findViewById(R.id.idle);
        sBreak = findViewById(R.id.sBreak);
        sAccel = findViewById(R.id.sAccel);
        sudTurn = findViewById(R.id.sudTurn);
        date = findViewById(R.id.date);
        nextBtn = findViewById(R.id.nextBtn);

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);

        Intent intent = getIntent();
        String fromIntent = intent.getStringExtra("intent");

        avgBtn.setOnClickListener(v -> {

            Intent in = null;

            if(fromIntent.equals("userAverage"))
            {
                in = new Intent(DriveAverageActivity.this, DriveAverageActivity.class);
                in.putExtra("intent","globalAverage");
            }
            else if(fromIntent.equals("globalAverage"))
            {
                in = new Intent(DriveAverageActivity.this, DriveAverageActivity.class);
                in.putExtra("intent","userAverage");
            }

            String json = gson.toJson(user);
            if (in != null) {
                in.putExtra("user", json);
            }

            finishAffinity();
            startActivity(in);
        });

        nextBtn.setOnClickListener(v -> next());

        setLayout(fromIntent);

        setFontBasedOnLanguage();
    }

    private void setLayout(String fromIntent)
    {
        if(fromIntent.equals("userAverage"))
        {
            titleTv.setText(R.string.your_average);
            avgBtn.setText(R.string.global_avg);
            userAverageStatistics();
        }
        else if(fromIntent.equals("globalAverage"))
        {
            titleTv.setText(R.string.global_avg);
            avgBtn.setText(R.string.your_average);
            globalAverageStatistics();
        }
    }

    private void setFontBasedOnLanguage()
    {
        if(SettingsActivity.isGreek)
        {
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

    private void setUpMaxSpeed(@NonNull AverageDriveResults driveResults)
    {

        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                countMaxSpeed++;
                if(countMaxSpeed<=driveResults.getMaxSpeed())
                {
                    maxprogressBar.setProgress(countMaxSpeed);
                }
                runOnUiThread(() -> {

                    if(countMaxSpeed<=driveResults.getMaxSpeed())
                    {
                        maxtvProgress.setText(String.valueOf(countMaxSpeed));
                    }
                });

                if(countMaxSpeed >= driveResults.getMaxSpeed())
                {
                    timer.cancel();
                }
            }
        };

        timer.schedule(timerTask, 0, 30);
    }

    private void setUpAverageSpeed(@NonNull AverageDriveResults driveResults)
    {

        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                countAverageSpeed++;
                if(countAverageSpeed<=driveResults.getAvgSpeed())
                {
                    avgprogressBar.setProgress(countAverageSpeed);
                }
                runOnUiThread(() -> {

                    if(countAverageSpeed<=driveResults.getAvgSpeed())
                    {
                        avgtvProgress.setText(String.valueOf(countAverageSpeed));
                    }
                });

                if(countAverageSpeed >= driveResults.getAvgSpeed())
                {
                    timer.cancel();
                }
            }
        };

        timer.schedule(timerTask, 0, 30);
    }

    private void setUpMaxAcceleration(@NonNull AverageDriveResults driveResults)
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

    private void setUpMinAcceleration(@NonNull AverageDriveResults driveResults)
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

    private void userAverageStatistics()
    {
        Call<AverageDriveResults> call = RetrofitClient
                .getInstance()
                .getUserAPI()
                .getUserAverageInformation(user.getUserId());

        call.enqueue(new Callback<AverageDriveResults>() {
            @Override
            public void onResponse(@NonNull Call<AverageDriveResults> call, @NonNull Response<AverageDriveResults> response) {

                if (response.body() != null) {
                    getData(response.body());

                    maxprogressBar.setProgress(0);
                    avgprogressBar.setProgress(0);
                    maxaccelprogressbar.setProgress(0);
                    minaccelprogressbar.setProgress(0);

                    setUpMaxSpeed(response.body());
                    setUpAverageSpeed(response.body());
                    setUpMaxAcceleration(response.body());
                    setUpMinAcceleration(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AverageDriveResults> call, @NonNull Throwable t) {

            }
        });
    }

    private void globalAverageStatistics()
    {
        Call<AverageDriveResults> call = RetrofitClient
                .getInstance()
                .getUserAPI()
                .getGlobalAverageInformation();

        call.enqueue(new Callback<AverageDriveResults>() {
            @Override
            public void onResponse(@NonNull Call<AverageDriveResults> call, @NonNull Response<AverageDriveResults> response) {

                if (response.body() != null) {
                    getData(response.body());
//                    setMaxAndAvgSpeed(response.body());

                    maxtvProgress.setText("-");
                    maxacceltvProgress.setText("-");
                    minacceltvProgress.setText("-");
                    setUpAverageSpeed(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AverageDriveResults> call, @NonNull Throwable t) {

            }
        });
    }

    private void getData(AverageDriveResults averageDriveResults)
    {
        distanceVal.setText(String.valueOf(averageDriveResults.getAvgDistance()));
        maxtvProgress.setText(String.valueOf(averageDriveResults.getMaxSpeed()));
        maxacceltvProgress.setText(String.valueOf(averageDriveResults.getMaxAcceleration()));
        avgtvProgress.setText(String.valueOf(averageDriveResults.getAvgSpeed()));
        minacceltvProgress.setText(String.valueOf(averageDriveResults.getMinAcceleration()));
        idleTv.setText(String.valueOf(averageDriveResults.getAvgIdleTime()));
        breakTv.setText(String.valueOf(averageDriveResults.getAvgSuddenBreaks()));
        sudAccelTv.setText(String.valueOf(averageDriveResults.getAvgSuddenAcceleration()));
        vibrationTv.setText(String.valueOf(averageDriveResults.getAvgSuddenTurns()));
        durationTv.setText(secondsToTime.convertSecondsToTime(averageDriveResults.getAvgDuration()));
    }

    public void onBackPressed() {

        super.onBackPressed();

        next();
    }

    private void next() {

        Intent in = new Intent(DriveAverageActivity.this, UserProfileActivity.class);
        String json = gson.toJson(user);
        in.putExtra("user", json);
        startActivity(in);
    }

}