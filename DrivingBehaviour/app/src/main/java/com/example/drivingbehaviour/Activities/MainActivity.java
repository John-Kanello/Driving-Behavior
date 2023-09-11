package com.example.drivingbehaviour.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.drivingbehaviour.Classes.User;
import com.example.drivingbehaviour.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView  logoName, sloganName, headerName, login, register, drive, profile, navigate, settings;
    ImageView ivLogin, ivRegister, ivDrive, ivProfile, ivLocate, ivSettings;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private de.hdodenhof.circleimageview.CircleImageView header_image;

    User user;
    Gson gson = new Gson();
    String json;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.homeMenu);

        logoName = findViewById(R.id.MainLogoName);
        sloganName = findViewById(R.id.MainSloganName);
        ivLogin = findViewById(R.id.ivLogin);
        ivRegister = findViewById(R.id.ivRegister);
        ivDrive = findViewById(R.id.ivDrive);
        ivProfile = findViewById(R.id.ivProfile);
        ivLocate = findViewById(R.id.ivLocate);
        ivSettings = findViewById(R.id.ivSettings);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        drive = findViewById(R.id.drive);
        navigate = findViewById(R.id.navigate);
        profile = findViewById(R.id.profile);
        settings = findViewById(R.id.settings);

        header_image = navigationView.getHeaderView(0).findViewById(R.id.header_image);
        headerName = navigationView.getHeaderView(0).findViewById(R.id.headerName);

        ivLogin.setOnClickListener(v -> openLogin());

        ivRegister.setOnClickListener(v -> openRegister());

        ivDrive.setOnClickListener(v -> openDriveIntent());

        ivProfile.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);

            if (user != null) {
                json = gson.toJson(user);

                intent.putExtra("user", json);

                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, R.string.sign_in_first, Toast.LENGTH_SHORT).show();
            }
        });

        ivLocate.setOnClickListener(v -> openNavigateIntent());

        ivSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//                Intent intent = new Intent(MainActivity.this, NavigateMapsActivity.class);
            json = gson.toJson(user);
            intent.putExtra("user", json);
            startActivity(intent);
        });

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);

        if (user != null) {
            retrieveImage();
            String fullName = user.getFirstName() + " " + user.getLastName();
            headerName.setText(fullName);
        } else {

            noProfileImage();
        }

        new Handler().postDelayed(this::setFontBasedOnLanguage, 340);

        setFontBasedOnLanguage();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setFontBasedOnLanguage()
    {
        if(SettingsActivity.isGreek)
        {
            Typeface typeface = ResourcesCompat.getFont(MainActivity.this, R.font.product_sans_regular);
            logoName.setTypeface(typeface);
            logoName.setTypeface(null, Typeface.BOLD);
            login.setTypeface(null, Typeface.BOLD);
            register.setTypeface(null, Typeface.BOLD);
            drive.setTypeface(null, Typeface.BOLD);
            profile.setTypeface(null, Typeface.BOLD);
            navigate.setTypeface(null, Typeface.BOLD);
            settings.setTypeface(null, Typeface.BOLD);
        }

        if(Locale.getDefault().getLanguage().equals("el"))
        {
            SettingsActivity.isGreek=true;
        }
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

            if (isLocationEnabled(MainActivity.this)) {
                intent = new Intent(MainActivity.this, DriveActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(MainActivity.this, PermissionsActivity.class);


            intent.putExtra("user", json);
            intent.putExtra("intent", "drive");
            startActivity(intent);
        }
    }

    private void openNavigateIntent()
    {
        Intent intent;

        String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;

        int fine = getBaseContext().checkCallingOrSelfPermission(fineLocationPermission);
        int coarse = getBaseContext().checkCallingOrSelfPermission(coarseLocationPermission);

        json = gson.toJson(user);

        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {

            if (isLocationEnabled(MainActivity.this)) {
//                intent = new Intent(MainActivity.this, NavigatingMapsActivity.class);
                intent = new Intent(MainActivity.this, NavigateMapsActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(MainActivity.this, PermissionsActivity.class);

            user = gson.fromJson(getIntent().getStringExtra("user"), User.class);

            intent.putExtra("user", json);

            intent.putExtra("intent", "navigate");

            startActivity(intent);
        }
    }

    private void openLogin()
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        json = gson.toJson(user);
        intent.putExtra("user", json);

        startActivity(intent);
    }

    private void openRegister()
    {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);
        json = gson.toJson(user);
        intent.putExtra("user", json);

        startActivity(intent);
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
        if(user==null)
        {
            headerName.setText(R.string.driving_behaviour);
        }
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
            finishAffinity();
        }

    }

    private void changeIntent(Class<?> cls)
    {
        Intent intent = new Intent(MainActivity.this, cls);
        json = gson.toJson(user);
        intent.putExtra("user", json);
        startActivity(intent);
        finishAffinity();
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        json = gson.toJson(user);

        switch(item.getItemId())
        {
            case R.id.loginMenu:
                openLogin();
                break;

            case R.id.registerMenu:
                openRegister();
                break;

            case R.id.profileMenu:
                if(user==null)
                {
                    Toast.makeText(MainActivity.this, R.string.sign_in_first, Toast.LENGTH_SHORT).show();
                    break;
                }
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