package com.example.babysitter.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babysitter.models.Babysitter;
import com.example.babysitter.models.Parent;
import com.example.babysitter.models.User;
import com.example.babysitter.services.BabysitterService;
import com.example.babysitter.repositories.DataManager;

import com.example.babysitter.services.ParentService;
import com.example.babysitter.services.UserService;
import com.example.babysitter.databinding.ActivityLoginBinding;
import com.google.gson.Gson;

public class ActivityLogin extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ProgressDialog progressDialog;
    private DataManager dataManager;
    private Gson gson = new Gson();
    private UserService userService;
    private ParentService parentService;
    private BabysitterService babysitterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataManager = new DataManager();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please Wait...");

        binding.alreadyAccount.setOnClickListener(v -> startActivity(new Intent(ActivityLogin.this, ActivityRegister.class)));

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    progressDialog.show();
                    login(email, password);
                } else {
                    Toast.makeText(ActivityLogin.this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(String email, String password) {
        dataManager.loginUser(email, password, new DataManager.OnLoginListener() {
            @Override
            public void onSuccess(User user) {
                progressDialog.dismiss();
                Toast.makeText(ActivityLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                if (binding.rbBabysitter.isChecked()) {
                    if(user instanceof Babysitter){
                        startActivity(new Intent(ActivityLogin.this, ActivityHomeBabysitter.class));
                    }
                    else {
                        Toast.makeText(ActivityLogin.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                } else if (binding.rbParent.isChecked()) {
                    if(user instanceof Parent){
                        startActivity(new Intent(ActivityLogin.this, ActivityHomeParent.class));
                    }
                    else {
                        Toast.makeText(ActivityLogin.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ActivityLogin.this, "Must select a user type", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
            @Override
            public void onFailure(Exception exception) {
                progressDialog.dismiss();
                Toast.makeText(ActivityLogin.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
