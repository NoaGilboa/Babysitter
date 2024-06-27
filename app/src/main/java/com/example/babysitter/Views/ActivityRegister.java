package com.example.babysitter.Views;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babysitter.GPSTracker;
import com.example.babysitter.Models.Babysitter;
import com.example.babysitter.Models.Parent;
import com.example.babysitter.Models.User;
import com.example.babysitter.R;
import com.example.babysitter.Utilities.DataManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ActivityRegister extends AppCompatActivity {

    double latitude, longitude;
    private EditText etName, etPhone, etMail, etAddress, etDateOfBirth, etMaritalStatus, etDescription, etNumberOfChildren, etPassword, etHourlyWage, etExperience;
    private RadioGroup rgUserType, rgSmoke;
    private RadioButton rbBabysitter, rbParent, rbSmokeYes, rbSmokeNo;
    private TextView alreadyAccount, tvAge;
    private Button btnRegister;
    private LinearLayout smokingLayout; // LinearLayout for smoking TextView and RadioButtons
    private ProgressDialog progressDialog;

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dataManager = new DataManager();

        initializeUIComponents();
        setupUIListeners();
    }

    private void initializeUIComponents() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etMail = findViewById(R.id.etMail);
        etAddress = findViewById(R.id.etAddress);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        tvAge = findViewById(R.id.tvAge);
        etMaritalStatus = findViewById(R.id.etMaritalStatus);
        etDescription = findViewById(R.id.etDescription);
        etNumberOfChildren = findViewById(R.id.etNumberOfChildren);
        etPassword = findViewById(R.id.etPassword);
        etHourlyWage = findViewById(R.id.etHourlyWage);
        etExperience = findViewById(R.id.etExperience);

        rgUserType = findViewById(R.id.rgUserType);
        rgSmoke = findViewById(R.id.rgSmoke);
        smokingLayout = findViewById(R.id.smokingLayout);

        rbBabysitter = findViewById(R.id.rbBabysitter);
        rbParent = findViewById(R.id.rbParent);
        rbSmokeYes = findViewById(R.id.rbSmokeYes);
        rbSmokeNo = findViewById(R.id.rbSmokeNo);

        btnRegister = findViewById(R.id.btnRegister);
        alreadyAccount = findViewById(R.id.tvAlreadyAccount);
    }

    private void setupUIListeners() {
        rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBabysitter) {
                showBabysitterFields();
            } else if (checkedId == R.id.rbParent) {
                showParentFields();
            }
        });

        btnRegister.setOnClickListener(v -> registerUser());
        alreadyAccount.setOnClickListener(v -> startActivity(new Intent(ActivityRegister.this, ActivityLogin.class)));

        etAddress.setOnClickListener(v -> {
            GPSTracker gpsTracker = new GPSTracker(ActivityRegister.this);
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                getAddressFromLocation(etAddress, latitude, longitude);
            } else {
                gpsTracker.showSettingsAlert();
            }
        });
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    etDateOfBirth.setText(selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    tvAge.setText(String.valueOf(calculateAge(selectedDate)));
                }, year, month, day);
        datePickerDialog.show();
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public void getAddressFromLocation(EditText etAddress, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addressObj = addresses.get(0);
                String address = addressObj.getAddressLine(0);
                etAddress.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etMail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String uid = UUID.randomUUID().toString();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = ProgressDialog.show(this, "Registering", "Please wait...", true);
        User user = null;
        if (rbBabysitter.isChecked()) {
            String dateOfBirth = etDateOfBirth.getText().toString().trim();
              Babysitter babysitter = new Babysitter(uid, name, phone, email, address, password,
                    dateOfBirth,
                    rgSmoke.getCheckedRadioButtonId() == R.id.rbSmokeYes,
                    etMaritalStatus.getText().toString().trim(),
                    etDescription.getText().toString().trim(),
                    Double.parseDouble(etHourlyWage.getText().toString().trim()),
                    Double.parseDouble(etExperience.getText().toString().trim()),
                    latitude, longitude);
            tvAge.setText(babysitter.getAge());
            user=babysitter;
        } else if (rbParent.isChecked()) {
             Parent parent = new Parent(uid, name, phone, email, address, password,
                    Integer.parseInt(etNumberOfChildren.getText().toString().trim()),
                    latitude, longitude);
             user=parent;
        }
        if (user != null) {
            dataManager.createUser(email, user instanceof Babysitter? (Babysitter) user : (Parent) user, new DataManager.OnUserCreationListener() {
                @Override
                public void onUserCreated(String email) {

                    Toast.makeText(ActivityRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ActivityRegister.this, ActivityLogin.class));
                }

                @Override
                public void onFailure(Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(ActivityRegister.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, new DataManager.OnDataSavedListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    Toast.makeText(ActivityRegister.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(ActivityRegister.this, "Data saving failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

//        if (user!=null) {
//            dataManager.createUser(email, user, new DataManager.OnUserCreationListener(){
//                @Override
//                public void onUserCreated(String email) {
//                    Toast.makeText(ActivityRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(ActivityRegister.this, ActivityLogin.class));
//                }
//                @Override
//                public void onFailure(Exception exception) {
//                    progressDialog.dismiss();
//                    Toast.makeText(ActivityRegister.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//                }, new DataManager.OnDataSavedListener() {
//                @Override
//                public void onSuccess() {
//                    progressDialog.dismiss();
//                    Toast.makeText(ActivityRegister.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void onFailure(Exception exception){
//                    progressDialog.dismiss();
//                    Toast.makeText(ActivityRegister.this, "Data saving failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }

        private void showBabysitterFields () {
            etDateOfBirth.setVisibility(View.VISIBLE);
            tvAge.setVisibility(View.VISIBLE);
            smokingLayout.setVisibility(View.VISIBLE);
            etMaritalStatus.setVisibility(View.VISIBLE);
            etDescription.setVisibility(View.VISIBLE);
            etHourlyWage.setVisibility(View.VISIBLE);
            etExperience.setVisibility(View.VISIBLE);

            etNumberOfChildren.setVisibility(View.GONE);
        }

        private void showParentFields () {
            tvAge.setVisibility(View.GONE);
            smokingLayout.setVisibility(View.GONE);
            etMaritalStatus.setVisibility(View.GONE);
            etDescription.setVisibility(View.GONE);
            etHourlyWage.setVisibility(View.GONE);
            etExperience.setVisibility(View.GONE);

            etNumberOfChildren.setVisibility(View.VISIBLE);
        }
    }
