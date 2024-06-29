package com.example.babysitter.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babysitter.models.BabysittingEvent;
import com.example.babysitter.models.Parent;
import com.example.babysitter.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {
    private List<BabysittingEvent> babysittingEvents;
    private Context context;

    public ParentAdapter(List<BabysittingEvent> babysittingEvents, Context context) {
        this.babysittingEvents = babysittingEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parent, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        BabysittingEvent babysittingEvent = babysittingEvents.get(position);

        if (babysittingEvent.getParentUid() != null && !babysittingEvent.getParentUid().isEmpty()) {
            DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Parent").child(babysittingEvent.getParentUid());
            parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Parent parent = dataSnapshot.getValue(Parent.class);
                        holder.tvParentName.setText("Parent Name: " + parent.getName());
                        holder.tvParentPhone.setText("Parent Phone: " + parent.getPhone());
                        holder.tvParentEmail.setText("Parent Email: " + parent.getMail());
                        holder.tvParentAddress.setText("Parent Address: " + parent.getAddress());
                        holder.tvParentNumberOfChildren.setText("Number of Children: " + parent.getNumberOfChildren());
                        holder.tvMessage.setText("Message: " + babysittingEvent.getMessageText());
                        holder.tvSelectedDate.setText("Date: " + babysittingEvent.getSelectedDate());
                        updateStatus(holder, babysittingEvent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ParentAdapter", "Failed to load parent details", error.toException());
                }
            });
        } else {
            Log.d("ParentAdapter", "Message with missing parentUid: " + babysittingEvent.getMessageText());
        }
    }

    private void updateStatus(@NonNull ParentViewHolder holder, BabysittingEvent babysittingEvent) {
        if (babysittingEvent.getStatus() != null) {
            holder.tvStatus.setTypeface(null, Typeface.BOLD);
            holder.tvStatus.setVisibility(View.VISIBLE);
            if (babysittingEvent.getStatus()) {
                holder.tvStatus.setText("Approved");
                holder.BtnApprov.setVisibility(View.GONE);
            } else {
                holder.tvStatus.setText("Canceled");
                holder.BtnCancel.setVisibility(View.GONE);
            }
        }
        holder.BtnApprov.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                babysittingEvent.setStatus(true);
                holder.tvStatus.setText("Approved");
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.BtnApprov.setVisibility(View.GONE);
                holder.BtnCancel.setVisibility(View.VISIBLE);
                updateEventInFirebase(babysittingEvent, position);
            }
        });

        holder.BtnCancel.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                babysittingEvent.setStatus(false);
                holder.tvStatus.setText("Canceled");
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.BtnCancel.setVisibility(View.GONE);
                holder.BtnApprov.setVisibility(View.VISIBLE);
                updateEventInFirebase(babysittingEvent, position);
            }
        });

    }

    private void updateEventInFirebase(BabysittingEvent babysittingEvent, int position) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(babysittingEvents.get(position).getMessageId());

        eventRef.setValue(babysittingEvent).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ParentAdapter", "Event status updated successfully");
                notifyItemChanged(position);
            } else {
                Log.e("ParentAdapter", "Failed to update event status", task.getException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return babysittingEvents.size();
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView tvParentName, tvParentPhone, tvParentEmail, tvParentAddress, tvParentNumberOfChildren, tvMessage, tvSelectedDate, tvStatus;
        Button BtnApprov, BtnCancel;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvParentName = itemView.findViewById(R.id.tvParentName);
            tvParentPhone = itemView.findViewById(R.id.tvParentPhone);
            tvParentEmail = itemView.findViewById(R.id.tvParentEmail);
            tvParentAddress = itemView.findViewById(R.id.tvParentAddress);
            tvParentNumberOfChildren = itemView.findViewById(R.id.tvParentNumberOfChildren);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvSelectedDate = itemView.findViewById(R.id.tvSelectedDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            BtnApprov = itemView.findViewById(R.id.BtnApprov);
            BtnCancel = itemView.findViewById(R.id.BtnCancel);
        }
    }
}
