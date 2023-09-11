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
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.drivingbehaviour.Classes.User;
import com.example.drivingbehaviour.R;
import com.example.drivingbehaviour.Retrofit.RetrofitClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView logoName, sloganName, btnLogin, btnRegister, headerName;

    private TextInputLayout tfEmail, tfPassword;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private de.hdodenhof.circleimageview.CircleImageView  header_image;

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
        setContentView(R.layout.activity_login);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        logoName = findViewById(R.id.logoName);
        sloganName = findViewById(R.id.sloganName);

        tfEmail = findViewById(R.id.tfEmail);
        tfPassword = findViewById(R.id.tfPassword);

        TextInputEditText tfEmailEditText = findViewById(R.id.tfEmailEditText);
        TextInputEditText tfPasswordEditText = findViewById(R.id.tfPasswordEditText);

        header_image = navigationView.getHeaderView(0).findViewById(R.id.header_image);

        headerName = navigationView.getHeaderView(0).findViewById(R.id.headerName);

        tfEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//here is your code
                if(tfEmail.getEditText()==null)
                {
                    return;
                }
                String email = tfEmail.getEditText().getText().toString();
                if(email.length()!=0)
                {
                    tfEmail.setErrorEnabled(false);
                }
                else
                {
                    tfEmail.setError("Email is required");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tfPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(tfPassword.getEditText()==null)
                {
                    return;
                }
                String password = String.valueOf(tfPassword.getEditText().getText());
                if(password.length()!=0)
                {
                    tfPassword.setErrorEnabled(false);
                }
                else
                {
                    tfPassword.setError("Password is required");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        btnRegister.setOnClickListener(v -> openRegister());

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.loginMenu);

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

        new Handler().postDelayed(this::setFontBasedOnLanguage, 300);
    }

    private void setFontBasedOnLanguage()
    {
        if(SettingsActivity.isGreek)
        {
            Typeface typeface = ResourcesCompat.getFont(LoginActivity.this, R.font.product_sans_regular);
            logoName.setTypeface(typeface);
            logoName.setTypeface(null, Typeface.BOLD);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void loginUser() {

        if(tfEmail.getEditText()==null || tfPassword.getEditText()==null)
        {
            return;
        }

        String email = String.valueOf(tfEmail.getEditText().getText()).trim();
        String password = String.valueOf(tfPassword.getEditText().getText()).trim();

        if(!isValidEmail(email))
        {
            Toast.makeText(LoginActivity.this, "Please enter a valid email address!", Toast.LENGTH_LONG).show();
            return;
        }

        if(tfPassword.getEditText().getText().length()<5)
        {
                Toast.makeText(LoginActivity.this, "Your password needs to be at lease 5 characters long",
                    Toast.LENGTH_SHORT).show();

                return;
        }


        if (email.isEmpty()) {
            tfEmail.setError("Email is required");
            tfEmail.requestFocus();
            return;
        }else if (password.isEmpty()) {
            tfPassword.setError("Password is required");
            tfPassword.requestFocus();
            return;
        }

        Call<User> call = RetrofitClient
                .getInstance()
                .getUserAPI()
                .checkUser(new User(email, password));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "User logged in!", Toast.LENGTH_SHORT).show();
                    user = response.body();
                    json = gson.toJson(user);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user", json);

                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "No such user exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                Log.d("tag", t.getMessage());
            }
        });
    }

    private void openRegister()
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

        json = gson.toJson(user);
        intent.putExtra("user", json);

        startActivity(intent);
    }

    private void openMainActivity()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        json = gson.toJson(user);
        intent.putExtra("user", json);

        startActivity(intent);
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

            if (isLocationEnabled(LoginActivity.this)) {
                intent = new Intent(LoginActivity.this, DriveActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(LoginActivity.this, PermissionsActivity.class);

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

            if (isLocationEnabled(LoginActivity.this)) {
                intent = new Intent(LoginActivity.this, NavigateMapsActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(LoginActivity.this, PermissionsActivity.class);

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
                    }).addOnFailureListener(e ->
                    noProfileImage());
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
            changeIntent(MainActivity.class);
            finishAffinity();
        }

    }

    private void changeIntent(Class<?> cls)
    {
        Intent intent = new Intent(LoginActivity.this, cls);
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

        switch(item.getItemId())
        {
            case R.id.homeMenu:
                openMainActivity();
                break;

            case R.id.loginMenu:
               changeIntent(LoginActivity.class);

            case R.id.registerMenu:
                openRegister();
                break;

            case R.id.profileMenu:

                if(user!=null)
                {
                   changeIntent(UserProfileActivity.class);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, R.string.sign_in_first, Toast.LENGTH_SHORT).show();
                }
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