package com.example.drivingbehaviour.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.location.LocationManagerCompat;

import com.example.drivingbehaviour.Classes.User;
import com.example.drivingbehaviour.R;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PermissionsActivity extends AppCompatActivity {

    Gson gson = new Gson();
    User user;
    String json;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_permissions);

        TextView grantTv = findViewById(R.id.grantTv);

        grantTv.setOnClickListener(v -> {

            Intent intent = getIntent();
            String fromIntent = intent.getStringExtra("intent");

            if (fromIntent.equals("drive")) {
                Dexter.withContext(PermissionsActivity.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                if (isLocationEnabled(PermissionsActivity.this))
                                {
                                    changeIntent(DriveActivity.class);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(PermissionsActivity.this, R.string.turn_on_location, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PermissionsActivity.this);
                                    builder.setTitle("Permission Denied")
                                            .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                            .setNegativeButton("Cancel", null)
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                Intent intent1 = new Intent();
                                                intent1.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent1.setData(Uri.fromParts("package", getPackageName(), null));
                                            })
                                            .show();
                                } else {
                                    Toast.makeText(PermissionsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();
            } else if (fromIntent.equals("navigate")) {
                Dexter.withContext(PermissionsActivity.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                if(isLocationEnabled(PermissionsActivity.this))
                                {
                                    changeIntent(NavigateMapsActivity.class);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(PermissionsActivity.this, R.string.turn_on_location, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PermissionsActivity.this);
                                    builder.setTitle("Permission Denied")
                                            .setMessage("Permission to access device location is permanently denied. you need to go to setting to allow the permission.")
                                            .setNegativeButton("Cancel", null)
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                Intent intent12 = new Intent();
                                                intent12.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent12.setData(Uri.fromParts("package", getPackageName(), null));
                                            })
                                            .show();
                                } else {
                                    Toast.makeText(PermissionsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();
            }

        });

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    private void changeIntent(Class<?> cls) {
        Intent intent = new Intent(PermissionsActivity.this, cls);
        json = gson.toJson(user);
        intent.putExtra("user", json);
        startActivity(intent);
    }

}