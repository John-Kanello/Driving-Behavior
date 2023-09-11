package com.example.drivingbehaviour.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    TextView headerName;

    private TextInputLayout cpFirstName, cpLastName, cpBirthday, cpEmail, cpPassword;

    private de.hdodenhof.circleimageview.CircleImageView profile_image, header_image;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    Gson gson = new Gson();
    User user;
    String json;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_profile);

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

//        navigationView.setBackgroundColor(Color.parseColor("#D1BF21"));
//        navigationView.getHeaderView(0).setBackgroundColor(Color.parseColor("#CC5B81"));

        TextView userName = findViewById(R.id.userName);

        header_image = navigationView.getHeaderView(0).findViewById(R.id.header_image);
        headerName = navigationView.getHeaderView(0).findViewById(R.id.headerName);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.homeMenu);

        cpFirstName = findViewById(R.id.cpFirstName);
        cpLastName = findViewById(R.id.cpLastName);
        cpBirthday = findViewById(R.id.cpBirthday);
        cpEmail = findViewById(R.id.cpEmail);
        cpPassword = findViewById(R.id.cpPassword);

        TextInputEditText cpFirstNameET = findViewById(R.id.cpFirstNameET);
        TextInputEditText cpLastNameET = findViewById(R.id.cpLastNameET);
        TextInputEditText cpBirthdayET = findViewById(R.id.cpBirthdayET);
        TextInputEditText cpEmailET = findViewById(R.id.cpEmailET);
        TextInputEditText cpPasswordET = findViewById(R.id.cpPasswordET);

        profile_image = findViewById(R.id.profile_image);

        ImageView editImg = findViewById(R.id.editImg);

        TextView btnUpdate = findViewById(R.id.btnUpdate);

        cpBirthdayET.setOnFocusChangeListener((v, hasFocus) -> {

            if (v.isInTouchMode() && hasFocus) {
                v.performClick(); // picks up first tap
            }
        });

        cpBirthdayET.setOnClickListener(v -> {

            Calendar cal = Calendar.getInstance();

            int year = cal.get(Calendar.YEAR)-18;
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(ChangeProfileActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener,
                    year, month, day);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        cpFirstNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cpFirstName.getEditText()==null)
                {
                    return;
                }
                String firstName = String.valueOf(cpFirstName.getEditText().getText()).trim();
                if(firstName.trim().length()!=0)
                {
                    cpFirstName.setErrorEnabled(false);
                }
                else
                {
                    cpFirstName.setError("Name is required");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        cpLastNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cpLastName.getEditText() == null)
                {
                    return;
                }
                String lastName = String.valueOf(cpLastName.getEditText().getText()).trim();
                if(lastName.trim().length()!=0)
                {
                    cpLastName.setErrorEnabled(false);
                }
                else
                {
                    cpLastName.setError("Last Name is required");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        cpEmailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cpEmail.getEditText()==null)
                {
                    return;
                }
                String email = String.valueOf(cpEmail.getEditText().getText()).trim();
                if(email.trim().length()!=0)
                {
                    cpEmail.setErrorEnabled(false);
                }
                else
                {
                    cpEmail.setError("Email is required");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cpPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = String.valueOf(Objects.requireNonNull(cpPassword.getEditText()).getText()).trim();
                if(password.trim().length()!=0)
                {
                    cpPassword.setErrorEnabled(false);
                }
                else
                {
                    cpPassword.setError("Password is required");
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

            if(currentYear-year>=18 && cpBirthday.getEditText()!=null)
            {
                cpBirthday.getEditText().setText(date);
            }
            else
            {
                cpBirthday.setError("You need to be at least 18 years old");
                cpBirthday.requestFocus();
            }
        };

        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);

        if(user != null)
        {
            Objects.requireNonNull(cpFirstName.getEditText()).setText(user.getFirstName().trim());
            Objects.requireNonNull(cpLastName.getEditText()).setText(user.getLastName().trim());
            Objects.requireNonNull(cpBirthday.getEditText()).setText(user.getBirthday().trim());
            Objects.requireNonNull(cpEmail.getEditText()).setText(user.getEmail().trim());
            Objects.requireNonNull(cpPassword.getEditText()).setText(user.getPassword().trim());

            String fullName = user.getFirstName()+ " " + user.getLastName();

            userName.setText(fullName);
            headerName.setText(fullName);
        }

            profile_image.setOnClickListener(v -> SelectImage());

            editImg.setOnClickListener(v -> SelectImage());

        btnUpdate.setOnClickListener(v -> {

            user.setFirstName(Objects.requireNonNull(cpFirstName.getEditText()).getText().toString().trim());
            user.setLastName(Objects.requireNonNull(cpLastName.getEditText()).getText().toString().trim());
            user.setBirthday(Objects.requireNonNull(cpBirthday.getEditText()).getText().toString().trim());
            user.setEmail(Objects.requireNonNull(cpEmail.getEditText()).getText().toString().trim());
            user.setPassword(Objects.requireNonNull(cpPassword.getEditText()).getText().toString().trim());

            if(!isValidEmail(cpEmail.getEditText().getText().toString().trim()))
            {
                Toast.makeText(ChangeProfileActivity.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if(cpPassword.getEditText().getText().length()<5)
            {
                Toast.makeText(ChangeProfileActivity.this, "Your password needs to be at lease 5 characters long",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getUserAPI()
                    .updateUser(user);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    StringBuilder s = new StringBuilder();
                    try{
                        if (response.body() != null) {
                            s.append(response.body().string().replaceAll("\"", ""));
                        }
                        else
                        {
                            Toast.makeText(ChangeProfileActivity.this, "Failed to update user", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    if(s.toString().equals("SUCCESS"))
                    {
                        Toast.makeText(ChangeProfileActivity.this, "Successfully updated", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                    Toast.makeText(ChangeProfileActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        setHeaderAndProfileImage();

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void setHeaderAndProfileImage()
    {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            uploadImage();

        }
    }

    // Select Image method
    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference.child("images/"+"user" + user.getUserId());
//                    .child(
//                            "images/"
//                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
// percentage on the dialog box
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {

                                // Image uploaded successfully
                                // Dismiss dialog
                                progressDialog.dismiss();
                                Toast
                                        .makeText(ChangeProfileActivity.this,
                                                "Image Uploaded!!",
                                                Toast.LENGTH_SHORT)
                                        .show();

                                retrieveImage();
                            })

                    .addOnFailureListener(e -> {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(ChangeProfileActivity.this,
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded "
                                                + (int)progress + "%");
                            });
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
                        profile_image.setImageBitmap(bitmap);
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
        profile_image.setImageDrawable(res);
        if(user==null)
        {
            headerName.setText(R.string.driving_behaviour);
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

            if (isLocationEnabled(ChangeProfileActivity.this)) {
                intent = new Intent(ChangeProfileActivity.this, DriveActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(ChangeProfileActivity.this, "Please enable your location", Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(ChangeProfileActivity.this, PermissionsActivity.class);

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

            if (isLocationEnabled(ChangeProfileActivity.this)) {
                intent = new Intent(ChangeProfileActivity.this, NavigateMapsActivity.class);
                intent.putExtra("user", json);
                startActivity(intent);
            } else {
                Toast.makeText(ChangeProfileActivity.this, "Please enable your location", Toast.LENGTH_SHORT).show();
            }
        } else {
            intent = new Intent(ChangeProfileActivity.this, PermissionsActivity.class);

            intent.putExtra("user", json);

            intent.putExtra("intent", "navigate");

            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

       changeIntent(UserProfileActivity.class);
    }

    private void changeIntent(Class<?> cls)
    {
        Intent intent = new Intent(ChangeProfileActivity.this, cls);
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