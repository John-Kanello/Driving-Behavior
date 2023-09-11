package com.example.drivingbehaviour.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView logoName, sloganName, btnRregister, btnRlogin,headerName;

    private TextInputLayout tfRfirstName, tfRlastName, tfRbirthday, tfRemail, tfRpassword;

    private de.hdodenhof.circleimageview.CircleImageView  header_image;

//    private ImageView logoImage;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    User user;
    Gson gson = new Gson();
    String json;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.registerMenu);

//        navigationView.getHeaderView(0).setBackgroundColor(Color.parseColor("#D1BF21"));

        logoName = findViewById(R.id.RlogoName);
        sloganName = findViewById(R.id.RsloganName);

        tfRfirstName = findViewById(R.id.tfRfirstName);
        tfRlastName = findViewById(R.id.tfRlastName);
        tfRbirthday = findViewById(R.id.tfRbirthday);
        tfRemail = findViewById(R.id.tfRemail);
        tfRpassword = findViewById(R.id.tfRpassword);

        TextInputEditText tfRbirthEditText = findViewById(R.id.tfRbirthEditText);
        TextInputEditText tfRFirstNameEditText = findViewById(R.id.tfRFirstNameEditText);
        TextInputEditText tfRLastNameEditText = findViewById(R.id.tfRLastNameEditText);
        TextInputEditText tfREmailEditText = findViewById(R.id.tfREmailEditText);
        TextInputEditText tfRPasswordEditText = findViewById(R.id.tfRPaasswordEditText);

        btnRlogin = findViewById(R.id.btnRlogin);
        btnRregister = findViewById(R.id.btnRregister);

        header_image = navigationView.getHeaderView(0).findViewById(R.id.header_image);

        headerName = navigationView.getHeaderView(0).findViewById(R.id.headerName);

        btnRregister.setOnClickListener(v -> registerUser());

        btnRlogin.setOnClickListener(v -> openLogin());

        tfRbirthEditText.setOnFocusChangeListener((v, hasFocus) -> {

            if (v.isInTouchMode() && hasFocus) {
                v.performClick(); // picks up first tap
            }
        });

        tfRbirthEditText.setOnClickListener(v -> {

            Calendar cal = Calendar.getInstance();

            int year = cal.get(Calendar.YEAR)-18;
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener,
                    year, month, day);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        tfRFirstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(tfRfirstName.getEditText()==null)
                {
                    return;
                }

                String firstName = String.valueOf(tfRfirstName.getEditText().getText());
                if(firstName.length()!=0)
                {
                    tfRfirstName.setErrorEnabled(false);
                }
                else
                {
                    tfRfirstName.setError("Name is required");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        tfRLastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String lastName = String.valueOf(tfRlastName.getEditText());
                if(lastName.length()!=0)
                {
                    tfRlastName.setErrorEnabled(false);
                }
                else
                {
                    tfRlastName.setError("Last Name is required");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        tfREmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(tfRemail.getEditText()==null)
                {
                    return;
                }

                String email = String.valueOf(tfRemail.getEditText().getText());
                if(email.length()!=0)
                {
                    tfRemail.setErrorEnabled(false);
                }
                else
                {
                    tfRemail.setError("Email is required");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tfRPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (tfRpassword.getEditText() == null)
                {
                    return;
                }

                String password = String.valueOf(tfRpassword.getEditText().getText());
                if(password.length()!=0)
                {
                    tfRpassword.setErrorEnabled(false);
                }
                else
                {
                    tfRpassword.setError("Password is required");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dateSetListener = (view, year, month, day) -> {

            String date = day  + "/" + (month+1) + "/" + year;

            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);

            if(currentYear-year>=18 && tfRbirthday.getEditText()!=null)
            {
                tfRbirthday.getEditText().setText(date);
            }
            else
            {
                tfRbirthday.setError(getString(R.string.birthday_check));
                tfRbirthday.requestFocus();
            }
        };

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.registerMenu);

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

        setFontBasedOnLanguage();
    }

    private void setFontBasedOnLanguage()
    {
        if(SettingsActivity.isGreek)
        {
            Typeface typeface = ResourcesCompat.getFont(RegisterActivity.this, R.font.product_sans_regular);
            logoName.setTypeface(typeface);
            logoName.setTypeface(null, Typeface.BOLD);
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

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void registerUser()
    {
        if(tfRfirstName.getEditText()==null || tfRlastName.getEditText()==null ||
        tfRbirthday.getEditText()==null || tfRemail.getEditText()==null || tfRpassword.getEditText()==null)
        {
            Toast.makeText(RegisterActivity.this, "Failed to register, try again!", Toast.LENGTH_SHORT).show();

            return;
        }
        String firstName = String.valueOf(tfRfirstName.getEditText().getText()).trim();
        String lastName = String.valueOf(tfRlastName.getEditText().getText()).trim();
        String birthday = String.valueOf(tfRbirthday.getEditText().getText()).trim();
        String email = String.valueOf(tfRemail.getEditText().getText()).trim();
        String password = String.valueOf(tfRpassword.getEditText().getText()).trim();

        if(!isValidEmail(email))
        {
            Toast.makeText(RegisterActivity.this, "Please enter a valid email address!", Toast.LENGTH_LONG).show();
            return;
        }

        if(tfRpassword.getEditText().getText().length()<5)
        {
            Toast.makeText(RegisterActivity.this, "Your password needs to be at lease 5 characters long",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if(firstName.isEmpty())
        {
            tfRfirstName.setError("Name is required");
            tfRfirstName.requestFocus();
            return;
        }
        else
        {
            tfRfirstName.clearFocus();
            tfRfirstName.setErrorEnabled(false);
        }

        if(lastName.isEmpty())
        {
            tfRlastName.setError("Last name is required");
            tfRlastName.requestFocus();
            return;
        }
        else
        {
            tfRlastName.clearFocus();
            tfRlastName.setErrorEnabled(false);
        }


        if(birthday.isEmpty())
        {
            tfRbirthday.setError("Date of birth is required");
            tfRbirthday.requestFocus();
            return;
        }
        else
        {
            tfRbirthday.clearFocus();
            tfRbirthday.setErrorEnabled(false);
        }

        if(email.isEmpty())
        {
            tfRemail.setError("Email is required");
            tfRemail.requestFocus();
            return;
        }
        else
        {
            tfRemail.clearFocus();
            tfRemail.setErrorEnabled(false);
        }

        if(password.isEmpty())
        {
            tfRpassword.setError("Password is required");
            tfRpassword.requestFocus();
            return;
        }
        else
        {
            tfRpassword.clearFocus();
            tfRpassword.setErrorEnabled(false);
        }

        Call<User> call = RetrofitClient
                .getInstance()
                .getUserAPI()
                .createUser(new User(firstName, lastName, birthday, email, password));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                if(response.isSuccessful())
                {
                    user = response.body();
                    Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    if (user != null) {
                        json = gson.toJson(user);
                        intent.putExtra("user", json);
                    }

                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Something went wrong. Please, try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {


            }
        });
    }

    private void openLogin()
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);


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

            if (isLocationEnabled(RegisterActivity.this)) {
                intent = new Intent(RegisterActivity.this, DriveActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(RegisterActivity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(RegisterActivity.this, PermissionsActivity.class);

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

            if (isLocationEnabled(RegisterActivity.this)) {
                intent = new Intent(RegisterActivity.this, NavigateMapsActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(RegisterActivity.this, R.string.enable_location, Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(RegisterActivity.this, PermissionsActivity.class);

            intent.putExtra("user", json);

            intent.putExtra("intent", "navigate");

            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
        Intent intent = new Intent(RegisterActivity.this, cls);
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
                Intent homeIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.loginMenu:
                openLogin();
                break;

            case R.id.registerMenu:
                changeIntent(RegisterActivity.class);
                break;

            case R.id.profileMenu:

                if(user!=null)
                {
                    changeIntent(UserProfileActivity.class);
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, R.string.sign_in_first, Toast.LENGTH_SHORT).show();
                }

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