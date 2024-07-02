package com.example.babysitter.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babysitter.R;
import com.example.babysitter.models.Babysitter;
import com.example.babysitter.repositories.DataManager;

import java.util.Calendar;
import java.util.List;

public class BabysitterAdapter extends RecyclerView.Adapter<BabysitterAdapter.BabysitterViewHolder> {
    private List<Babysitter> babysitters;
    private Context context;
    private DataManager dataManager;

    public BabysitterAdapter(List<Babysitter> babysitters, Context context, DataManager dataManager) {
        this.babysitters = babysitters;
        this.context = context;
        this.dataManager = dataManager;
    }

    @NonNull
    @Override
    public BabysitterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.babysitter, parent, false);
        return new BabysitterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BabysitterViewHolder holder, int position) {
        Babysitter babysitter = babysitters.get(position);
        holder.tvBabysitterName.setText("Name: " + babysitter.getName());
        holder.tvBabysitterPhone.setText("Phone: " + babysitter.getPhone());
        holder.tvBabysitterEmail.setText("Email: " + babysitter.getMail());
        holder.tvBabysitterAddress.setText("Address: " + babysitter.getAddress());
        holder.tvBabysitterAge.setText("Age: " + babysitter.getAge());
        holder.tvBabysitterSmokes.setText("Smokes: " + (babysitter.isSmoke() ? "Yes" : "No"));
        holder.tvBabysitterMaritalStatus.setText("Marital Status: " + babysitter.getMaritalStatus());
        holder.tvBabysitterDescription.setText("Description: " + babysitter.getDescription());
        holder.tvBabysitterHourlyWage.setText("Hourly Wage: $" + babysitter.getHourlyWage());
        holder.tvBabysitterExperience.setText("Experience:  " + babysitter.getExperience());

        holder.btnSendMessage.setOnClickListener(v -> {
            // Make the EditText and DatePicker Button visible
            holder.messageEditText.setVisibility(View.VISIBLE);
            holder.showDatePickerButton.setVisibility(View.VISIBLE);
            holder.btnSendMessage.setVisibility(View.GONE);
            // Handle DatePicker dialog
            holder.showDatePickerButton.setOnClickListener(view -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        (view1, year1, monthOfYear, dayOfMonth) -> {
                            String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1;
                            holder.tvSelectedDate.setText("Selected Date: " + date);
                            holder.tvSelectedDate.setVisibility(View.VISIBLE);
                            holder.btnSendMessage.setVisibility(View.VISIBLE);
                        }, year, month, day);
                datePickerDialog.show();
            });

            holder.btnSendMessage.setOnClickListener(t -> {
                String messageText = holder.messageEditText.getText().toString();
                String selectedDate = holder.tvSelectedDate.getText().toString().replace("Selected Date: ", "");

                if (messageText.isEmpty() || selectedDate.isEmpty()) {
                    Toast.makeText(context, "Message or Date is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String babysitterUID = babysitter.getUid();
                dataManager.createEvent(messageText, selectedDate, babysitterUID, new DataManager.OnDataSavedListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT).show();
                        holder.messageEditText.setVisibility(View.GONE);
                        holder.showDatePickerButton.setVisibility(View.GONE);
                        holder.btnSendMessage.setVisibility(View.GONE);
                        holder.tvSelectedDate.setVisibility(View.GONE);
                        holder.tvSelectedDate.setText("Message sent successfully");
                        holder.tvSelectedDate.setTypeface(null, Typeface.BOLD);
                        holder.tvSelectedDate.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return babysitters.size();
    }

    static class BabysitterViewHolder extends RecyclerView.ViewHolder {
        TextView tvBabysitterName, tvBabysitterPhone, tvBabysitterEmail, tvBabysitterAddress, tvBabysitterAge, tvBabysitterSmokes, tvBabysitterMaritalStatus, tvBabysitterDescription, tvBabysitterHourlyWage, tvBabysitterExperience, tvSelectedDate;
        EditText messageEditText;
        Button showDatePickerButton, btnSendMessage;

        public BabysitterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBabysitterName = itemView.findViewById(R.id.tvBabysitterName);
            tvBabysitterPhone = itemView.findViewById(R.id.tvBabysitterPhone);
            tvBabysitterEmail = itemView.findViewById(R.id.tvBabysitterEmail);
            tvBabysitterAddress = itemView.findViewById(R.id.tvBabysitterAddress);
            tvBabysitterAge = itemView.findViewById(R.id.tvBabysitterAge);
            tvBabysitterSmokes = itemView.findViewById(R.id.tvBabysitterSmokes);
            tvBabysitterMaritalStatus = itemView.findViewById(R.id.tvBabysitterMaritalStatus);
            tvBabysitterDescription = itemView.findViewById(R.id.tvBabysitterDescription);
            tvBabysitterHourlyWage = itemView.findViewById(R.id.tvBabysitterHourlyWage);
            tvBabysitterExperience = itemView.findViewById(R.id.tvBabysitterExperience);
            messageEditText = itemView.findViewById(R.id.messageEditText);
            showDatePickerButton = itemView.findViewById(R.id.showDatePickerButton);
            tvSelectedDate = itemView.findViewById(R.id.tvSelectedDate);
            btnSendMessage = itemView.findViewById(R.id.btnSendMessage);
        }
    }
}