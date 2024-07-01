package com.example.babysitter.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babysitter.adapters.BabysitterAdapter;
import com.example.babysitter.models.Babysitter;
import com.example.babysitter.R;
import com.example.babysitter.repositories.DataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityHomeParent extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BabysitterAdapter adapter;
    private List<Babysitter> babysitters;

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_parent);

        dataManager = new DataManager();

        recyclerView = findViewById(R.id.rvBabysitters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        babysitters = new ArrayList<>();
        adapter = new BabysitterAdapter(babysitters, this);
        recyclerView.setAdapter(adapter);

        loadBabysitters();

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            dataManager.logout(new DataManager.OnLogoutListener() {
                @Override
                public void onLogoutSuccess() {
                    Toast.makeText(ActivityHomeParent.this, "Logout successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityHomeParent.this, ActivityLogin.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onLogoutFailure(Exception exception) {
                    Toast.makeText(ActivityHomeParent.this, "Logout failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityHomeParent.this, ActivitySetting.class));
            }
        });

        findViewById(R.id.btnSortByExperience).setOnClickListener(v -> {
            Collections.sort(babysitters, (b1, b2) -> Double.compare(b2.getExperience(), b1.getExperience()));
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the UI.
        });

        findViewById(R.id.btnSortByHourlyWage).setOnClickListener(v -> {
            Collections.sort(babysitters, (b1, b2) -> Double.compare(b2.getHourlyWage(), b1.getHourlyWage()));
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the UI.
        });

        findViewById(R.id.btnSortByDistance).setOnClickListener(v -> {
            dataManager.sortBabysittersByDistance(new DataManager.OnBabysittersLoadedListener() {
                @Override
                public void onBabysittersLoaded(List<Babysitter> loadedBabysitters) {
                    babysitters.clear();
                    babysitters.addAll(loadedBabysitters);
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onFailure(Exception exception) {

                    Toast.makeText(ActivityHomeParent.this, "Failed to load babysitters: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadBabysitters() {
        dataManager.loadAllBabysitters(new DataManager.OnBabysittersLoadedListener() {
            @Override
            public void onBabysittersLoaded(List<Babysitter> loadedBabysitters) {
                babysitters.clear();
                babysitters.addAll(loadedBabysitters);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(ActivityHomeParent.this, "Failed to load babysitters: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


