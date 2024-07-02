package com.example.babysitter.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babysitter.adapters.ParentAdapter;
import com.example.babysitter.models.BabysittingEvent;
import com.example.babysitter.R;
import com.example.babysitter.repositories.DataManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityHomeBabysitter extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ParentAdapter adapter;
    private List<BabysittingEvent> events;

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_babysiter);

        dataManager = new DataManager();

        recyclerView = findViewById(R.id.rvParents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        events = new ArrayList<>();
        adapter = new ParentAdapter(events, this);
        recyclerView.setAdapter(adapter);

        loadEvents();

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            dataManager.logout(new DataManager.OnLogoutListener() {
                @Override
                public void onLogoutSuccess() {
                    Toast.makeText(ActivityHomeBabysitter.this, "Logout successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityHomeBabysitter.this, ActivityLogin.class);
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onLogoutFailure(Exception exception) {
                    Toast.makeText(ActivityHomeBabysitter.this, "Logout failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> startActivity(new Intent(this, ActivitySetting.class)));

        findViewById(R.id.btnSortByOldetoNewer).setOnClickListener(v -> sortEvents(true));
        findViewById(R.id.btnSortByNewerToOlder).setOnClickListener(v -> sortEvents(false));
    }

    private void loadEvents() {
            dataManager.loadAllEvents(new DataManager.OnEventsLoadedListener() {
                @Override
                public void onEventsLoaded(List<BabysittingEvent> loadedEvents) {
                    events.clear();
                    events.addAll(loadedEvents);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception exception) {
                    Log.e("HomeBabysitter", "Failed to load events: " + exception.getMessage());
                }
            });

    }

    private void sortEvents(boolean olderToNewer) {
        if (events != null && !events.isEmpty()) {
            Collections.sort(events, (event1, event2) -> {
                return compareDates(event1.getSelectedDate(), event2.getSelectedDate(), olderToNewer);
            });
            adapter.notifyDataSetChanged();
        }
    }

    private int compareDates(String dateStr1, String dateStr2, boolean olderToNewer) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date1 = sdf.parse(dateStr1);
            Date date2 = sdf.parse(dateStr2);
            if (date1 != null && date2 != null) {
                return olderToNewer ? date1.compareTo(date2) : date2.compareTo(date1);
            }
        } catch (ParseException e) {
            Log.e("sortEvents", "Error parsing dates", e);
        }
        return 0;
    }
}
