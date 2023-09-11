package com.example.drivingbehaviour.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.drivingbehaviour.API.AltitudeApiClient;
import com.example.drivingbehaviour.Classes.AverageDriveResults;
import com.example.drivingbehaviour.Classes.DriveResults;
import com.example.drivingbehaviour.Classes.User;
import com.example.drivingbehaviour.DistanceMatrixAPI.LocationResponse;
import com.example.drivingbehaviour.HelperClasses.ConvertSecondsToTime;
import com.example.drivingbehaviour.HelperClasses.DriveOffences;
import com.example.drivingbehaviour.HelperClasses.LatAndLong;
import com.example.drivingbehaviour.R;
import com.example.drivingbehaviour.Retrofit.RestUtil;
import com.example.drivingbehaviour.Retrofit.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import in.unicodelabs.kdgaugeview.KdGaugeView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriveActivity extends AppCompatActivity implements LocationListener {

    private static final int DEFAULT_INTERVAL = 2000, FASTEST_INTERVAL = 2000;

    TextView distanceVal, maxSpeedTv, averageSpeedTv, altitudeTv,
            latTv, lonTv, durationTv, addressTv, idleTv, accelTv, tempTv,
            dur, dist, spMax, spAvg, acc, idle, address, alt, lat, lon, temp;

    AppCompatButton mapButton, pressBackBtn;

    KdGaugeView speedometerValue;

    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    LocationManager locationManager;
    Location currentLocation;

    int time = 0, idleTime = 0, maxAccel = 0, minAccel = 0, intervalForAltitudeAndTemperature = 0,
            intervalForAccelerometer = 0, highAcceleration,highDeceleration,highVibration;

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String date,startHour;

    long startTime;
    float distanceCovered = 0f, averageSpeed = 0f, acceleration = 0f, maxSpeed = 0f, speed, pSpeed;

    Date prevTime = null;

    Gson gson = new Gson();
    User user;
    DriveResults driveResults;
    DriveOffences driveOffences;
    String json;

    private boolean calculateVibration = true;

    ConvertSecondsToTime secondsToTime = new ConvertSecondsToTime();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationPreviousValue;

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double accelerationCurrentValue = Math.sqrt((x * x + y * y + z * z));
            double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;

            if(changeInAcceleration>=12 && calculateVibration && currentLocation!=null)
            {
                highVibration++;
                LatAndLong latAndLong = new LatAndLong(currentLocation.getLatitude(), currentLocation.getLongitude(), "Sudden turning");
                calculateVibration = false;
                intervalForAccelerometer = 0;
                saveDangerousLocation(latAndLong);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drive);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        distanceVal = findViewById(R.id.distanceVal);
        maxSpeedTv = findViewById(R.id.maxSpeedTv);
        averageSpeedTv = findViewById(R.id.averageSpeedTv);
        altitudeTv = findViewById(R.id.altitudeTv);
        latTv = findViewById(R.id.latTv);
        lonTv = findViewById(R.id.lonTv);
        durationTv = findViewById(R.id.durationTv);
        addressTv = findViewById(R.id.addressTv);
        idleTv = findViewById(R.id.idleTv);
        accelTv = findViewById(R.id.accelTv);
        tempTv = findViewById(R.id.tempTv);
        dur = findViewById(R.id.dur);
        dist = findViewById(R.id.dist);
        spMax = findViewById(R.id.spMax);
        spAvg = findViewById(R.id.spAvg);
        acc = findViewById(R.id.acc);
        idle = findViewById(R.id.idle);
        address = findViewById(R.id.address);
        alt = findViewById(R.id.alt);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        temp = findViewById(R.id.temp);

        mapButton = findViewById(R.id.tvMap);
        pressBackBtn = findViewById(R.id.pressBackBtn);

        speedometerValue = findViewById(R.id.speedMeter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationRequest = LocationRequest.create();

        locationCallback = new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                currentLocation = locationResult.getLastLocation();

                latTv.setText(String.valueOf(currentLocation != null ? currentLocation.getLatitude() : 0));

                lonTv.setText(String.valueOf(currentLocation.getLongitude()));

                Geocoder geocoder = new Geocoder(DriveActivity.this);

                try {
                    List<Address> addresses = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), 1);
                    addressTv.setText(addresses.get(0).getThoroughfare());
//                    String locality = addresses.get(0).getLocality();
//                    Log.d("Locality", locality);
                } catch (IOException e) {

                    addressTv.setText("Not available");
                }

                if(intervalForAltitudeAndTemperature==0)
                {
                    calculateAltitude(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                    LatAndLong latAndLong = new LatAndLong(
                            locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()
                    );
                    calculateWeatherTemperature(latAndLong);
                    intervalForAltitudeAndTemperature+=2;
                }
                else if(intervalForAltitudeAndTemperature<8)
                {
                    intervalForAltitudeAndTemperature+=2;
                }
                else{
                    intervalForAltitudeAndTemperature=0;
                }

                if(intervalForAccelerometer>=4)
                {
                    calculateVibration=true;
                }
                else
                {
                    intervalForAccelerometer+=2;
                }
            }
        };

        mapButton.setOnClickListener(v -> {

            Intent intent = new Intent(DriveActivity.this, NavigateMapsActivity.class);

            Calendar cal = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String endHour = sdf.format(cal.getTime());
            if (user == null) {
                driveResults = new DriveResults((int) maxSpeed, (int) averageSpeed, startHour,
                        endHour, date, String.valueOf(time), (int) distanceCovered,
                        maxAccel, minAccel, String.valueOf(idleTime), highDeceleration, highAcceleration, highVibration);


            } else {
                driveResults = new DriveResults(user.getUserId(), (int) maxSpeed, (int) averageSpeed, startHour,
                        endHour, date, String.valueOf(time), (int) distanceCovered,
                        maxAccel, minAccel, String.valueOf(idleTime), highDeceleration, highAcceleration, highVibration);
            }

            json = gson.toJson(user);
            intent.putExtra("user", json);

            json = gson.toJson(driveResults);
            intent.putExtra("results", json);

            driveOffences = new DriveOffences(idleTime, highAcceleration, highDeceleration, highVibration);
            json = gson.toJson(driveOffences);
            intent.putExtra("offences", json);

            intent.putExtra("startTime", startTime);

            startActivity(intent);
            finishAffinity();
        });

        pressBackBtn.setOnClickListener(v -> pressBackButton());

        setDateAndTime();

        startLocationUpdates();

        startTimer();

        measureAcceleration();

        initializeObjects();


        if(SettingsActivity.isGreek)
        {
            new Handler().postDelayed(this::setFontBasedOnLanguage, 340);
        }
//        setFontBasedOnLanguage();
    }

    private void setFontBasedOnLanguage()
    {
        dur.setTypeface(null, Typeface.BOLD);
        dur.setTextSize(17);
        dist.setTypeface(null, Typeface.BOLD);
        dist.setTextSize(17);
        spMax.setTypeface(null, Typeface.BOLD);
        spMax.setTextSize(17);
        spAvg.setTypeface(null, Typeface.BOLD);
        spAvg.setTextSize(17);
        acc.setTypeface(null, Typeface.BOLD);
        acc.setTextSize(17);
        idle.setTypeface(null, Typeface.BOLD);
        idle.setTextSize(17);
        address.setTypeface(null, Typeface.BOLD);
        address.setTextSize(17);
        alt.setTypeface(null, Typeface.BOLD);
        alt.setTextSize(17);
        lat.setTypeface(null, Typeface.BOLD);
        lat.setTextSize(17);
        lon.setTypeface(null, Typeface.BOLD);
        lon.setTextSize(17);
        temp.setTypeface(null, Typeface.BOLD);
        temp.setTextSize(17);
        addressTv.setTypeface(null, Typeface.BOLD);

    }

    @SuppressLint("SimpleDateFormat")
    private void setDateAndTime()
    {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        date = simpleDateFormat.format(calendar.getTime());
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        startHour = simpleDateFormat.format(calendar.getTime());
        startTime = getIntent().getLongExtra("startTime", 0L);

        if(startTime==0)
        {
            startTime = calendar.getTime().getTime();
        }

    }

    private void initializeObjects()
    {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        driveResults = gson.fromJson(getIntent().getStringExtra("results"), DriveResults.class);
        driveOffences = gson.fromJson(getIntent().getStringExtra("offences"), DriveOffences.class);


        if (driveResults != null) {
//            if (driveResults.getUserId() != null) {
//                user.setUserId(driveResults.getUserId());
//            }

            idleTime = Integer.parseInt(driveResults.getIdleTime());
            time = Integer.parseInt(driveResults.getDuration());
            startHour = driveResults.getStartHour();
            maxSpeed = driveResults.getMaxSpeed();
            averageSpeed = driveResults.getAvgSpeed();
            distanceCovered = driveResults.getDistance();


            idleTv.setText(String.valueOf(idleTime));
            durationTv.setText(driveResults.getDuration());
            maxSpeedTv.setText(String.valueOf((int)maxSpeed));
            averageSpeedTv.setText(String.valueOf((int)averageSpeed));
            distanceVal.setText(String.valueOf((int)distanceCovered));
        }

        if(driveOffences !=null)
        {
            highAcceleration = driveOffences.getHighAcceleration();
            highDeceleration = driveOffences.getHighDeceleration();
            highVibration = driveOffences.getHighVibration();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void measureAcceleration() {
        DecimalFormat df = new DecimalFormat("#.##");
        if(pSpeed!=0f)
        {
            acceleration = speed - pSpeed;
            accelTv.setText(df.format(acceleration));
    }

        if (acceleration >= 10f) {
            highAcceleration++;

            LatAndLong latAndLong = new LatAndLong(currentLocation.getLatitude(), currentLocation.getLongitude(), "High Acceleration");

            saveDangerousLocation(latAndLong);
        }
        else if (acceleration <= -10f) {
            highDeceleration++;

            LatAndLong latAndLong = new LatAndLong(currentLocation.getLatitude(), currentLocation.getLongitude(), "Sudden Breaking");

            saveDangerousLocation(latAndLong);
        }

        maxAccel = Math.max(maxAccel, (int) acceleration);
        minAccel = Math.min(minAccel, (int) acceleration);

        pSpeed = speed;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        locationRequest.setInterval(DEFAULT_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void saveDangerousLocation(LatAndLong latAndLong)
    {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getUserAPI()
                .saveDangerousLocation(latAndLong);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(@NonNull Location location) {

        Date currentTime = Calendar.getInstance().getTime();

        speed = (float) (location.getSpeed() * 3.6);
        speedometerValue.setSpeed(speed);

        if (prevTime != null && prevTime != currentTime && speed != 0) {
            long duration = currentTime.getTime() - prevTime.getTime();
            distanceCovered += location.getSpeed() * duration / 1000;
            distanceVal.setText(String.valueOf((int)distanceCovered));
        }

        if (currentTime.getTime() != startTime) {
            float sessionLength = (float)((currentTime.getTime() - startTime) / 1000);
            if(sessionLength!=0)
            {
                averageSpeed = (float) ((distanceCovered / sessionLength) * 3.6);
                averageSpeedTv.setText(String.valueOf(Math.round(averageSpeed)));
            }
        }

        prevTime = Calendar.getInstance().getTime();

        if (speed > maxSpeed) {
            maxSpeed = speed;
            maxSpeedTv.setText(String.valueOf((int) maxSpeed));
        }

    }

    private void startTimer() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(() -> {

                    time++;
                    durationTv.setText(secondsToTime.convertSecondsToTime(time));

                    if (pSpeed == 0 && speed == 0) {
                        idleTime++;
                    }
                    idleTv.setText(secondsToTime.convertSecondsToTime(idleTime));

                });

                measureAcceleration();
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBackPressed() {

        super.onBackPressed();

        pressBackButton();
    }

    private void calculateAltitude(double latitude, double longitude){

            Map<String, String> mapQuery = new HashMap<>();

            mapQuery.put("locations", latitude + "," + longitude);
            mapQuery.put("key", getString(R.string.google_api_key));

            AltitudeApiClient client = RestUtil.getInstance().getRetrofit().create(AltitudeApiClient.class);

            Call<LocationResponse> call = client.getAltitude(mapQuery);
            call.enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(@NonNull Call<LocationResponse> call, @NonNull Response<LocationResponse> response) {
                    if (response.body() != null
                    ) {

                        altitudeTv.setText((String.valueOf((int)Math.round(response.body().getResults().get(0).getElevation()))));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LocationResponse> call, @NonNull Throwable t) {

                }

            });
        }

        private void calculateWeatherTemperature(LatAndLong latAndLong)
        {
            Call<Double> call = RetrofitClient.getInstance()
                    .getWeatherAPI().getWeather(latAndLong);

            call.enqueue(new Callback<Double>() {
                @Override
                public void onResponse(@NonNull Call<Double> call, @NonNull Response<Double> response) {

                    if(response.body()!=null)
                    {
                        tempTv.setText(String.valueOf(response.body()));
                    }
                    else
                    {
                        Toast.makeText(DriveActivity.this, "Response body was null", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Double> call, @NonNull Throwable t) {

                }
            });
        }

    @SuppressLint("SimpleDateFormat")
    private void pressBackButton() {

        Intent intent = new Intent(DriveActivity.this, HomeActivity.class);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String endHour = sdf.format(cal.getTime());
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf.format(cal.getTime());

        if (user == null) {
            driveResults = new DriveResults((int) maxSpeed, (int) averageSpeed, startHour,
                    endHour, date, String.valueOf(durationTv.getText()), (int) distanceCovered,
                    maxAccel, minAccel, String.valueOf(idleTv.getText()), highDeceleration, highAcceleration, highVibration);
        } else {

            AverageDriveResults averageDriveResults = new AverageDriveResults(user.getUserId(), (int) maxSpeed, (int) averageSpeed
                    , maxAccel, minAccel, idleTime, 1,
                    Math.round(time), (int) distanceCovered, highDeceleration, highAcceleration, highVibration, (int) distanceCovered ,0, 0,
                    0.0,0.0,0.0);

            Call<ResponseBody> newCall = RetrofitClient
                    .getInstance()
                    .getUserAPI()
                    .updateAverageResults(averageDriveResults);

            newCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Toast.makeText(DriveActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(DriveActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                }
            });

            driveResults = new DriveResults(user.getUserId(), (int) maxSpeed, (int) averageSpeed, startHour,
                    endHour, date, String.valueOf(durationTv.getText()), (int) distanceCovered,
                    maxAccel, minAccel, String.valueOf(idleTv.getText()), highDeceleration, highAcceleration, highVibration);

            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getUserAPI()
                    .addDriveResults(driveResults);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    StringBuilder s = new StringBuilder();
                    try {
                        if (response.body() != null) {
                            s.append(response.body().string().replaceAll("\"", ""));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (s.toString().equals("SUCCESS")) {
                        Toast.makeText(DriveActivity.this, "Successfully added details", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("DriveDetails", "Api request was half-way consumed");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                    Log.d("DriveDetails", "Failed to send Api request");
                }
            });
        }

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        json = gson.toJson(user);
        intent.putExtra("user", json);

        json = gson.toJson(driveResults);
        intent.putExtra("results", json);

        driveOffences = new DriveOffences(idleTime, highAcceleration, highDeceleration, highVibration);
        json = gson.toJson(driveOffences);
        intent.putExtra("offences", json);

        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

}