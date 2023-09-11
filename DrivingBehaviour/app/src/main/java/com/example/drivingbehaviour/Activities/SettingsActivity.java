package com.example.drivingbehaviour.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    TextView language,pickLanguage,headerName, nextBtn;

    ImageView nextArrow;

    private de.hdodenhof.circleimageview.CircleImageView header_image;

    MenuItem homeMenu, loginMenu, registerMenu, carMenu, navigateMenu, profileMenu, settingsMenu;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch aSwitch;

    public static boolean isGreek;

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
        setContentView(R.layout.activity_settings);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        header_image = navigationView.getHeaderView(0).findViewById(R.id.header_image);
        headerName = navigationView.getHeaderView(0).findViewById(R.id.headerName);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.settingsMenu);

        Menu menu  = navigationView.getMenu();
        homeMenu = menu.findItem(R.id.homeMenu);
        loginMenu = menu.findItem(R.id.loginMenu);
        registerMenu = menu.findItem(R.id.registerMenu);
        carMenu = menu.findItem(R.id.carMenu);
        navigateMenu = menu.findItem(R.id.navigateMenu);
        profileMenu = menu.findItem(R.id.profileMenu);
        settingsMenu = menu.findItem(R.id.settingsMenu);

        language = findViewById(R.id.language);
        pickLanguage = findViewById(R.id.pickLanguage);
        nextBtn = findViewById(R.id.nextBtn);

        nextArrow = findViewById(R.id.nextArrow);

        aSwitch = findViewById(R.id.aSwitch);

        aSwitch.setOnClickListener(v -> {
            if(aSwitch.isChecked())
            {
                language.setText("Ελληνικά");
                pickLanguage.setText("Επέλεξε μια γλώσσα");
                toolbar.setTitle("Ρυθμίσεις");
                changeDrawerLayoutLanguageToGreek();
                isGreek = true;
                setLocale("el");
            }
            else
            {
                language.setText(R.string.english);
                pickLanguage.setText(R.string.choose_a_language);
                toolbar.setTitle(R.string.settings_en);
                changeDrawerLayoutLanguageToEnglish();
                isGreek = false;
                setLocale("en");
            }
        });

        nextBtn.setOnClickListener(v -> changeIntent(MainActivity.class));

        nextArrow.setOnClickListener(v -> changeIntent(MainActivity.class));

        setLanguage();

        setHeaderImage();

    }

    private void setLanguage()
    {
        if (isGreek) {
            aSwitch.setChecked(true);
            language.setText("Ελληνικά");
            pickLanguage.setText("Επέλεξε μια γλώσσα");

        } else
        {
            aSwitch.setChecked(false);
            language.setText(R.string.english);
            pickLanguage.setText(R.string.choose_a_language);
        }
    }

    private void setLocale(String language) {

        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(language);
        resources.updateConfiguration(configuration,metrics);

        onConfigurationChanged(configuration);
    }

    private void changeDrawerLayoutLanguageToGreek()
    {
        homeMenu.setTitle("Αρχική");
        loginMenu.setTitle("Σύνδεση");
        registerMenu.setTitle("Εγγραφή");
        carMenu.setTitle("Οδήγηση");
        navigateMenu.setTitle("Πλοήγηση");
        profileMenu.setTitle("Προφίλ");
        settingsMenu.setTitle("Ρυθμίσεις");
        nextBtn.setText("Συνέχεια");
        nextBtn.setTypeface(null, Typeface.BOLD);
    }

    private void changeDrawerLayoutLanguageToEnglish()
    {
        homeMenu.setTitle(R.string.home_en);
        loginMenu.setTitle(R.string.login_en);
        registerMenu.setTitle(R.string.register_en);
        carMenu.setTitle(R.string.drive_en);
        navigateMenu.setTitle(R.string.navigate_en);
        profileMenu.setTitle(R.string.profile_en);
        settingsMenu.setTitle(R.string.settings_en);
        nextBtn.setText(R.string.continue_en);
    }

    private void setHeaderImage()
    {
        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(user!=null)
        {
            retrieveImage();
            String fullName = user.getFirstName()+ " " + user.getLastName();
            headerName.setText(fullName);
        }
        else {
            noProfileImage();
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void changeIntent(Class<?> cls)
    {
        Intent intent = new Intent(SettingsActivity.this, cls);
        json = gson.toJson(user);
        intent.putExtra("user", json);
        startActivity(intent);
        finishAffinity();
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
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

            if (isLocationEnabled(SettingsActivity.this)) {
                intent = new Intent(SettingsActivity.this, DriveActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(SettingsActivity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(SettingsActivity.this, PermissionsActivity.class);


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

            if (isLocationEnabled(SettingsActivity.this)) {
                intent = new Intent(SettingsActivity.this, NavigateMapsActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(SettingsActivity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(SettingsActivity.this, PermissionsActivity.class);

            user = gson.fromJson(getIntent().getStringExtra("user"), User.class);

            intent.putExtra("user", json);

            intent.putExtra("intent", "navigate");

            startActivity(intent);
        }
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
                if(user==null) {
                    Toast.makeText(SettingsActivity.this, R.string.sign_in_first, Toast.LENGTH_SHORT).show();
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
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            changeIntent(MainActivity.class);
            super.onBackPressed();
        }

    }
}