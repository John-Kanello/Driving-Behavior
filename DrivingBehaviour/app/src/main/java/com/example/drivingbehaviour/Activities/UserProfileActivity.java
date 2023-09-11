package com.example.drivingbehaviour.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.drivingbehaviour.Classes.AverageDriveResults;
import com.example.drivingbehaviour.Classes.DriveResults;
import com.example.drivingbehaviour.Classes.User;
import com.example.drivingbehaviour.R;
import com.example.drivingbehaviour.Retrofit.RetrofitClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    TextView userFirstName, userLastName, userBirthday, userEmail, userEditProfile,
            userViewHistory,userViewStatistics,profileName,headerName,backBtnTV;

    ImageView backBtn;

    private de.hdodenhof.circleimageview.CircleImageView profile_image, header_image;

    Gson gson = new Gson();
    User user;
    String json;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.profileMenu);

        userFirstName = findViewById(R.id.userFirstName);
        userLastName = findViewById(R.id.userLastName);
        userBirthday = findViewById(R.id.userBirthday);
        userEmail = findViewById(R.id.userEmail);
        userEditProfile = findViewById(R.id.userEditProfile);
        userViewHistory = findViewById(R.id.userViewHistory);
        userViewStatistics = findViewById(R.id.userViewStatistics);
        profileName = findViewById(R.id.profileName);
        backBtnTV = findViewById(R.id.backBtnTV);

        backBtn = findViewById(R.id.backBtn);

        profile_image = findViewById(R.id.profile_image);

        header_image = navigationView.getHeaderView(0).findViewById(R.id.header_image);
        headerName = navigationView.getHeaderView(0).findViewById(R.id.headerName);

        backBtn.setOnClickListener(v -> changeIntent(MainActivity.class));
        backBtnTV.setOnClickListener(v -> changeIntent(MainActivity.class));

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);


        userEditProfile.setOnClickListener(v -> {
            if(user!=null)
            {
                Intent intent = new Intent(UserProfileActivity.this, ChangeProfileActivity.class);

                json = gson.toJson(user);
                intent.putExtra("user", json);

                startActivity(intent);
            }
            else
            {
                Toast.makeText(UserProfileActivity.this, "Something went wrong. Please try to login again",
                        Toast.LENGTH_SHORT).show();
            }
        });



        userViewHistory.setOnClickListener(v -> {

            checkForSessions();
        });

        userViewStatistics.setOnClickListener(v -> {

            checkAverageSession();
        });

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(user!=null)
        {
            userFirstName.setText(user.getFirstName());
            userLastName.setText(user.getLastName());
            userBirthday.setText(user.getBirthday());
            userEmail.setText(user.getEmail());

            retrieveImage();
            String fullName = user.getFirstName()+ " " + user.getLastName();
            headerName.setText(fullName);
            profileName.setText(fullName);
        }
        else {

            noProfileImage();
        }
    }

    private void checkAverageSession()
    {
        Call<AverageDriveResults> call = RetrofitClient
                .getInstance()
                .getUserAPI()
                .getUserAverageInformation(user.getUserId());

        call.enqueue(new Callback<AverageDriveResults>() {
            @Override
            public void onResponse(@NonNull Call<AverageDriveResults> call, @NonNull Response<AverageDriveResults> response) {

                if (response.body() ==null) {
                    Toast.makeText(UserProfileActivity.this, "No sessions found...", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(UserProfileActivity.this, DriveAverageActivity.class);
                intent.putExtra("intent","userAverage");
                json = gson.toJson(user);
                intent.putExtra("user", json);
                startActivity(intent);

            }

            @Override
            public void onFailure(@NonNull Call<AverageDriveResults> call, @NonNull Throwable t) {

            }
        });
    }

    private void checkForSessions()
    {
        Call<List<DriveResults>> call = RetrofitClient
                .getInstance()
                .getUserAPI()
                .getUserInformation(user.getUserId());

        call.enqueue(new Callback<List<DriveResults>>() {
            @Override
            public void onResponse(@NonNull Call<List<DriveResults>> call, @NonNull Response<List<DriveResults>> response) {


                if (response.body() != null && response.body().size()==0) {
                    Toast.makeText(UserProfileActivity.this, "No sessions found...", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(UserProfileActivity.this, DriveHistoryActivity.class);
                json = gson.toJson(user);
                intent.putExtra("user", json);
                startActivity(intent);
            }

            @Override
            public void onFailure(@NonNull Call<List<DriveResults>> call, @NonNull Throwable t) {

            }
        });
    }

    private void openDriveIntent()
    {
        Intent intent;

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        json = gson.toJson(user);

        String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;

        int fine = getBaseContext().checkCallingOrSelfPermission(fineLocationPermission);
        int coarse = getBaseContext().checkCallingOrSelfPermission(coarseLocationPermission);

        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {

            if (isLocationEnabled(UserProfileActivity.this)) {
                intent = new Intent(UserProfileActivity.this, DriveActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(UserProfileActivity.this, "Please enable your location", Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(UserProfileActivity.this, PermissionsActivity.class);

            intent.putExtra("user", json);
            intent.putExtra("intent", "drive");
            startActivity(intent);
        }
    }

    private void openNavigateIntent()
    {
        Intent intent;

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        json = gson.toJson(user);

        String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;

        int fine = getBaseContext().checkCallingOrSelfPermission(fineLocationPermission);
        int coarse = getBaseContext().checkCallingOrSelfPermission(coarseLocationPermission);

        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {

            if (isLocationEnabled(UserProfileActivity.this)) {
                intent = new Intent(UserProfileActivity.this, NavigateMapsActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(UserProfileActivity.this, "Please enable your location", Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(UserProfileActivity.this, PermissionsActivity.class);

            intent.putExtra("user", json);

            intent.putExtra("intent", "navigate");

            startActivity(intent);
        }
    }

    private void retrieveImage()
    {
        StorageReference ref
                = storageReference.child("images/"+"user" + user.getUserId());

        try {
            File file = File.createTempFile("user" + user.getUserId(), "jpg");
            ref.getFile(file)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        header_image.setImageBitmap(bitmap);
                        profile_image.setImageBitmap(bitmap);
                    }).addOnFailureListener(e -> noProfileImage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void noProfileImage()
    {
        String uri = "@drawable/car";  // where myresource (without the extension) is the file

        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        @SuppressLint("UseCompatLoadingForDrawables") Drawable res = getResources().getDrawable(imageResource);
        header_image.setImageDrawable(res);
        profile_image.setImageDrawable(res);
        if(user==null)
        {
            headerName.setText(R.string.driving_behaviour);
        }

//        profileName.setText(R.string.driving_behaviour);
    }


    private void changeIntent(Class<?> cls)
    {
        Intent intent = new Intent(UserProfileActivity.this, cls);
        json = gson.toJson(user);
        intent.putExtra("user", json);
        startActivity(intent);
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        changeIntent(MainActivity.class);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeMenu:
                changeIntent(MainActivity.class);
                break;

            case R.id.loginMenu:
                changeIntent(LoginActivity.class);
                break;

            case R.id.registerMenu:
                changeIntent(RegisterActivity.class);
                break;

            case R.id.profileMenu:
                changeIntent(UserProfileActivity.class);
                break;

            case R.id.carMenu:

                openDriveIntent();
                break;

            case R.id.navigateMenu:

                openNavigateIntent();
                break;

            case R.id.settingsMenu:
                changeIntent(SettingsActivity.class);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}