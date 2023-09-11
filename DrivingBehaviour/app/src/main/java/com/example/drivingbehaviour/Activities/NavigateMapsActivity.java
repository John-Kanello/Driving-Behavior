package com.example.drivingbehaviour.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.drivingbehaviour.API.DirectionApiClient;
import com.example.drivingbehaviour.API.DistanceApiClient;
import com.example.drivingbehaviour.Classes.AverageDriveResults;
import com.example.drivingbehaviour.Classes.DriveResults;
import com.example.drivingbehaviour.Classes.User;
import com.example.drivingbehaviour.DirectionsAPI.DecodePolyline;
import com.example.drivingbehaviour.DirectionsAPI.DirectionResponse;
import com.example.drivingbehaviour.DirectionsAPI.Legs;
import com.example.drivingbehaviour.DirectionsAPI.Routes;
import com.example.drivingbehaviour.DirectionsAPI.Steps;
import com.example.drivingbehaviour.DistanceMatrixAPI.DistanceResponse;
import com.example.drivingbehaviour.DistanceMatrixAPI.Element;
import com.example.drivingbehaviour.HelperClasses.ConvertSecondsToTime;
import com.example.drivingbehaviour.HelperClasses.DriveOffences;
import com.example.drivingbehaviour.HelperClasses.LatAndLong;
import com.example.drivingbehaviour.R;
import com.example.drivingbehaviour.Retrofit.RestUtil;
import com.example.drivingbehaviour.Retrofit.RetrofitClient;
import com.example.drivingbehaviour.databinding.ActivityNavigateMapsBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigateMapsActivity extends FragmentActivity implements OnMapReadyCallback , LocationListener {

    private GoogleMap mMap;

    ImageView imgHome,imgStop;
    TextView tvSpeed;
    MaterialSearchBar materialSearchBar;
    View mapView;

    Polyline polyline = null;
    List<LatLng> latLngList = new ArrayList<>();

    private static final int DEFAULT_INTERVAL = 500, FASTEST_INTERVAL = 500;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private Location lastKnownLocation;
    private LocationCallback locationCallback;
    private LocationManager locationManager;
    private LocationRequest locationRequest;

    private MarkerOptions userMarker, destination;
    private Marker marker;

    private boolean cameraSetOnMyLocation = false, calculateVibration = true;

    private int time = 0, idleTime = 0, maxAccel = 0, minAccel = 0, intervalForAccelerometer = 0,
            highAcceleration,highDeceleration,highVibration ;

    private float distanceCovered = 0f, averageSpeed = 0f, maxSpeed = 0f, acceleration = 0f, speed, pSpeed;

    private Date prevTime = null;

    private long startTime;
    private String startHour,endHour,date;

    Gson gson = new Gson();
    User user;
    DriveResults driveResults;
    DriveOffences driveOffences;
    String json;

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

            if(changeInAcceleration>=12 && calculateVibration && lastKnownLocation!=null)
            {
                highVibration++;
                calculateVibration = false;
                intervalForAccelerometer = 0;
                LatAndLong latAndLong = new LatAndLong(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), "Sudden turning");
                saveDangerousLocation(latAndLong);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        com.example.drivingbehaviour.databinding.ActivityNavigateMapsBinding binding = ActivityNavigateMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        materialSearchBar = findViewById(R.id.searchBar);
        tvSpeed = findViewById(R.id.tvSpeed);

        imgHome = findViewById(R.id.imgHome);
        imgStop = findViewById(R.id.imgStop);

        imgHome.setOnClickListener(v -> {

            Intent intent = new Intent(NavigateMapsActivity.this, DriveActivity.class);

            if(user==null)
            {
                driveResults = new DriveResults((int)maxSpeed, (int)averageSpeed, startHour,
                        endHour, date, String.valueOf(time), (int)distanceCovered,
                        maxAccel, minAccel, String.valueOf(idleTime), highDeceleration, highAcceleration, highVibration);
            }
            else {
                driveResults = new DriveResults(user.getUserId(), (int)maxSpeed, (int)averageSpeed, startHour,
                        endHour, date, String.valueOf(time), (int)distanceCovered,
                        maxAccel, minAccel, String.valueOf(idleTime), highDeceleration, highAcceleration, highVibration
                );
            }

            user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
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

        imgStop.setOnClickListener(v -> pressBackButton());

        if (mapFragment != null) {
            mapView = mapFragment.getView();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(NavigateMapsActivity.this);
        locationRequest = LocationRequest.create();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Places.initialize(NavigateMapsActivity.this, getString(R.string.google_api_key));
        placesClient = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

                if(buttonCode == MaterialSearchBar.BUTTON_NAVIGATION)
                {
                    materialSearchBar.disableSearch();
                }
                else if(buttonCode == MaterialSearchBar.BUTTON_BACK)
                {
                    materialSearchBar.disableSearch();
                }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(s.toString())
//                        .setCountry("GR")
                        .build();

                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(task -> {

                    if(task.isSuccessful())
                    {
                        FindAutocompletePredictionsResponse predictionsResponse = task.getResult();

                        if(predictionsResponse != null)
                        {
                            predictionList = predictionsResponse.getAutocompletePredictions();
                            List<String> suggestionsList = new ArrayList<>();
                            for(AutocompletePrediction prediction : predictionList)
                            {
                                suggestionsList.add(prediction.getFullText(null).toString());
                            }

                            materialSearchBar.updateLastSuggestions(suggestionsList);
                            if(!materialSearchBar.isSuggestionsVisible())
                            {
                                materialSearchBar.showSuggestionsList();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(NavigateMapsActivity.this, "Prediction was unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {

                if (position >= predictionList.size())
                {
                    return;
                }

                materialSearchBar.disableSearch();

                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(() -> materialSearchBar.clearSuggestions(), 1000);

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(imm != null)
                {
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                final String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Collections.singletonList(Place.Field.LAT_LNG);
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();

                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {

                    Place place = fetchPlaceResponse.getPlace();
                    LatLng LatLngOfPlace = place.getLatLng();

                    if(LatLngOfPlace != null && userMarker!=null)
                    {
                        mMap.clear();
                        cameraSetOnMyLocation = false;

                        mMap.addMarker(new MarkerOptions().position(LatLngOfPlace));
                        destination = new MarkerOptions().position(LatLngOfPlace);

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        Marker markerOnDestination = mMap.addMarker(destination);

                        builder.include(userMarker.getPosition());
                        if (markerOnDestination != null) {
                            builder.include(markerOnDestination.getPosition());
                        }

                        LatLngBounds bounds = builder.build();

                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 350), 1000, null);

                        Navigate(place.getLatLng());

                        getDistanceInfo(place);
                    }

                })
                        .addOnFailureListener(e -> {

                            if(e instanceof ApiException)
                            {
                                ApiException apiException = (ApiException) e ;
                                apiException.printStackTrace();
                                Toast.makeText(NavigateMapsActivity.this, "Place could not be found", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

                List<String> suggestionsList = new ArrayList<>();
                for(AutocompletePrediction prediction : predictionList)
                {
                    if(prediction != predictionList.get(position))
                    {
                        suggestionsList.add(prediction.getFullText(null).toString());
                    }
                }

                materialSearchBar.updateLastSuggestions(suggestionsList);
                if(!materialSearchBar.isSuggestionsVisible())
                {
                    materialSearchBar.showSuggestionsList();
                }
            }
        });

        locationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if(intervalForAccelerometer>=2)
                {
                    calculateVibration=true;
                }
                else
                {
                    intervalForAccelerometer+=1;
                }

                if(mMap!=null)
                {
                    if(lastKnownLocation==null)
                    {
                        lastKnownLocation = locationResult.getLastLocation();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastKnownLocation != null ? lastKnownLocation.getLatitude() : 0,
                                lastKnownLocation != null ? lastKnownLocation.getLongitude() : 0)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 18.0f));
                        new Handler().postDelayed(() -> showLocationInMap(lastKnownLocation), 500);
                    }
                    else
                    {
                        lastKnownLocation = locationResult.getLastLocation();
                        showLocationInMap(lastKnownLocation);
                    }

                }
            }
        };

        initializeObjects();

        startTimer();

        startLocationUpdates();
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
                Toast.makeText(NavigateMapsActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(NavigateMapsActivity.this, "Failed to save dangerous location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void initializeObjects()
    {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        driveResults = gson.fromJson(getIntent().getStringExtra("results"), DriveResults.class);
        driveOffences = gson.fromJson(getIntent().getStringExtra("offences"), DriveOffences.class);
        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        startTime = getIntent().getLongExtra("startTime", 0L);

        if(driveResults!=null)
        {
            maxSpeed = driveResults.getMaxSpeed();
            averageSpeed = driveResults.getAvgSpeed();
            startHour = driveResults.getStartHour();
            endHour = driveResults.getEndHour();
            date = driveResults.getDate();
            time = Integer.parseInt(driveResults.getDuration());
            distanceCovered = driveResults.getDistance();
            maxAccel = driveResults.getMaxAcceleration();
            minAccel = driveResults.getMinAcceleration();
            idleTime = Integer.parseInt(driveResults.getIdleTime());

        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            date = simpleDateFormat.format(calendar.getTime());
            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            startHour = simpleDateFormat.format(calendar.getTime());
        }

        if(driveOffences!=null)
        {
            highAcceleration = driveOffences.getHighAcceleration();
            highDeceleration = driveOffences.getHighDeceleration();
            highVibration = driveOffences.getHighVibration();
        }

        if(startTime==0)
        {
            Calendar calendar = Calendar.getInstance();
            startTime = calendar.getTime().getTime();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        Date currentTime = Calendar.getInstance().getTime();

        speed = (float) (location.getSpeed() * 3.6);
        tvSpeed.setText(String.valueOf((int)speed));

        if (prevTime != null && prevTime != currentTime && speed!=0) {
            long duration = currentTime.getTime() - prevTime.getTime();
            if(duration!=0) {
                distanceCovered += location.getSpeed() * duration / 1000;
            }
        }

//        averageSpeed = (float) (distanceCovered / time * 3.6);
        if (currentTime.getTime() != startTime) {
            float sessionLength = (float)((currentTime.getTime() - startTime) / 1000);
            if(sessionLength!=0)
            {
                averageSpeed = (float) ((distanceCovered / sessionLength) * 3.6);
            }
        }

        prevTime = Calendar.getInstance().getTime();

        if (speed > maxSpeed)
        {
            maxSpeed = speed;
        }

    }

    @SuppressLint("MissingPermission")
    private void showLocationInMap(Location location)
    {
        if(location!=null)
        {
            LatLng locationInMap = new LatLng(location.getLatitude(), location.getLongitude());

            if(marker!=null)
            {
                marker.remove();
            }

            userMarker = new MarkerOptions().position(locationInMap);
            marker = mMap.addMarker(userMarker.title("Your current location"));
            if(cameraSetOnMyLocation)
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(locationInMap));
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        locationRequest.setInterval(DEFAULT_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void getDistanceInfo(Place place) {

        // http://maps.googleapis.com/maps/api/distancematrix/json?destinations=Atlanta,GA|New+York,NY&origins=Orlando,FL&units=imperial
        Map<String, String> mapQuery = new HashMap<>();
//        mapQuery.put("units", "imperial");
        mapQuery.put("origins", lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude());
        mapQuery.put("destinations", Objects.requireNonNull(place.getLatLng()).latitude + "," + place.getLatLng().longitude);
        mapQuery.put("departure_time", "now");
        mapQuery.put("key", getString(R.string.google_api_key));
        DistanceApiClient client = RestUtil.getInstance().getRetrofit().create(DistanceApiClient.class);

        Call<DistanceResponse> call = client.getDistanceInfo(mapQuery);
        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<DistanceResponse> call, @NonNull Response<DistanceResponse> response) {
                if (response.body() != null &&
                        response.body().getRows() != null &&
                        response.body().getRows().size() > 0 &&
                        response.body().getRows().get(0) != null &&
                        response.body().getRows().get(0).getElements() != null &&
                        response.body().getRows().get(0).getElements().size() > 0 &&
                        response.body().getRows().get(0).getElements().get(0) != null &&
                        response.body().getRows().get(0).getElements().get(0).getDistance() != null &&
                        response.body().getRows().get(0).getElements().get(0).getDuration() != null &&
                        response.body().getRows().get(0).getElements().get(0).getDurationInTraffic()!= null
                ) {

                    Element element = response.body().getRows().get(0).getElements().get(0);

//                    tts.speak("You are scheduled to arrive to destination in " + element.getDistance().getText());

                    Toast.makeText(NavigateMapsActivity.this,
                            "You are scheduled to arrive to destination in " +
                                    element.getDuration().getText(), Toast.LENGTH_LONG).show();


                }
            }

            @Override
            public void onFailure(@NonNull Call<DistanceResponse> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void measureAcceleration()
    {
        if(pSpeed!=0f)
        {
            acceleration = speed - pSpeed;
        }

        if(acceleration>=10f)
        {
            highAcceleration++;
            LatAndLong latAndLong = new LatAndLong(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), "High Acceleration");
            saveDangerousLocation(latAndLong);
        }

        if(acceleration<=-10f)
        {
            highDeceleration++;
            LatAndLong latAndLong = new LatAndLong(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), "Sudden Breaking");
            saveDangerousLocation(latAndLong);
        }

        maxAccel = Math.max(maxAccel, (int)acceleration);
        minAccel = Math.min(minAccel, (int)acceleration);
        pSpeed = speed;
    }

    private void startTimer()
    {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(() -> {

                    time++;

                    if (pSpeed == 0 && speed == 0) {
                        idleTime++;
                    }
                });

                measureAcceleration();
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        if(mapView != null && mapView.findViewById(Integer.parseInt("1")) != null)
        {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)  locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 10, 250);
        }

        mMap.setOnMyLocationButtonClickListener(() -> {

            if (materialSearchBar.isSuggestionsVisible()) {
                materialSearchBar.clearSuggestions();
            }

            if (materialSearchBar.isSearchEnabled()) {
                materialSearchBar.disableSearch();
            }

            if(!cameraSetOnMyLocation)
            {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 18.0f));

                fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                new Handler().postDelayed(this::startLocationUpdates, 1000);
            }
            else if(destination!=null)
            {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                Marker markerOnDestination = mMap.addMarker(destination);

                builder.include(userMarker.getPosition());
                if (markerOnDestination != null) {
                    builder.include(markerOnDestination.getPosition());
                }

                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 350), 1000, null);
            }

            cameraSetOnMyLocation = !cameraSetOnMyLocation;

            return true;
        });

//        mMap.setTrafficEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

    }

    private void Navigate(LatLng latLngOfPlace) {
        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("origin", lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude());
        mapQuery.put("destination", latLngOfPlace.latitude+","+latLngOfPlace.longitude);
        mapQuery.put("mode", "driving");
        mapQuery.put("key", getString(R.string.google_api_key));
        DirectionApiClient client = RestUtil.getInstance().getRetrofit().create(DirectionApiClient.class);

        Call<DirectionResponse> call = client.getDirectionInfo(mapQuery);

        call.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(@NonNull Call<DirectionResponse> call, @NonNull Response<DirectionResponse> response) {

                DirectionResponse directionResponse = response.body();
                if(directionResponse==null)
                {
                    Toast.makeText(NavigateMapsActivity.this, "Failed to find routing...", Toast.LENGTH_LONG).show();

                    return;
                }
                List<Routes> routes = directionResponse.getRoutes();
                List<Legs> legs = routes.get(0).getLegs();
                List<Steps> steps = legs.get(0).getStepsList();

                drawPolyline(steps);
            }

            @Override
            public void onFailure(@NonNull Call<DirectionResponse> call, @NonNull Throwable t) {
                Toast.makeText(NavigateMapsActivity.this, "Failed!!!!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawPolyline(List<Steps> steps) {

        List<List<HashMap<String, Double>>> routes = new ArrayList<>();
        List<HashMap<String, Double>> path = new ArrayList<>();

        DecodePolyline decodePolyline = new DecodePolyline();

        for (int i = 0; i < steps.size(); i++) {

            String polyline = steps.get(i).getPolylines().getPoints();

            latLngList = decodePolyline.decodePoly(polyline);

            for (int l = 0; l < latLngList.size(); l++) {
                HashMap<String, Double> hm = new HashMap<>();
                hm.put("lat", latLngList.get(l).latitude);
                hm.put("lng", (latLngList.get(l)).longitude);
                path.add(hm);
            }
        }
        routes.add(path);

       latLngList = new ArrayList<>();

        for(int i=0; i<routes.get(0).size(); i++)
        {
            for(int j=0; j<routes.get(0).get(i).size(); j++)
            {
                latLngList.add(new LatLng(routes.get(0).get(i).get("lat"),routes.get(0).get(i).get("lng")));
            }
        }

        if(polyline != null)
        {
            polyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(latLngList).clickable(true);
        polylineOptions.width(20);
        polylineOptions.color(Color.BLUE);
        polyline = mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onBackPressed() {

        this.finishAffinity();
        pressBackButton();
    }

    @SuppressLint("SimpleDateFormat")
    private void pressBackButton() {

        Intent intent = new Intent(NavigateMapsActivity.this, HomeActivity.class);

        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String endHour = sdf.format(cal.getTime());
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf.format(cal.getTime());

        if (user == null) {
            driveResults = new DriveResults((int) maxSpeed, (int) averageSpeed, startHour,
                    endHour, date, secondsToTime.convertSecondsToTime(time), (int) distanceCovered,
                    maxAccel, minAccel, secondsToTime.convertSecondsToTime(idleTime),highDeceleration, highAcceleration, highVibration);
        } else {

            AverageDriveResults averageDriveResults = new AverageDriveResults(user.getUserId(), (int) maxSpeed, (int) averageSpeed
                    , maxAccel, minAccel, idleTime, 1,
                    Math.round(time), (int) distanceCovered, highDeceleration, highAcceleration, highVibration,(int) distanceCovered,0, 0,
                    0.0,0.0,0.0);

            Call<ResponseBody> newCall = RetrofitClient
                    .getInstance()
                    .getUserAPI()
                    .updateAverageResults(averageDriveResults);

            newCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                }
            });

            driveResults = new DriveResults(user.getUserId(), (int) maxSpeed, (int) averageSpeed, startHour,
                    endHour, date, secondsToTime.convertSecondsToTime(time), (int) distanceCovered,
                    maxAccel, minAccel, secondsToTime.convertSecondsToTime(idleTime),highDeceleration, highAcceleration, highVibration);

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
                        Toast.makeText(NavigateMapsActivity.this, "Successfully added details", Toast.LENGTH_LONG).show();
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