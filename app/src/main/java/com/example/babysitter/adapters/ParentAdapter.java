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

import com.example.babysitter.R;
import com.example.babysitter.models.BabysittingEvent;
import com.example.babysitter.models.Parent;
import com.example.babysitter.repositories.DataManager;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {
    private List<BabysittingEvent> babysittingEvents;
    private Context context;
    private DataManager dataManager;

    public ParentAdapter(List<BabysittingEvent> babysittingEvents, Context context, DataManager dataManager) {
        this.babysittingEvents = babysittingEvents;
        this.context = context;
        this.dataManager = dataManager;
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
            dataManager.getParent(babysittingEvent.getParentUid(), new DataManager.OnParentLoadedListener() {
                @Override
                public void onParentLoaded(Parent parent) {
                    if (parent != null) {
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
                public void onFailure(Exception exception) {
                    Log.e("ParentAdapter", "Failed to load parent details", exception);
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
                holder.BtnCancel.setVisibility(View.VISIBLE);
            } else {
                holder.tvStatus.setText("Canceled");
                holder.BtnCancel.setVisibility(View.GONE);
                holder.BtnApprov.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tvStatus.setVisibility(View.GONE);
            holder.BtnApprov.setVisibility(View.VISIBLE);
            holder.BtnCancel.setVisibility(View.GONE);
        }

        holder.BtnApprov.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                babysittingEvent.setStatus(true);
                holder.tvStatus.setText("Approved");
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.BtnApprov.setVisibility(View.GONE);
                holder.BtnCancel.setVisibility(View.VISIBLE);
                updateEventInDataBase(babysittingEvent, position);
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
                updateEventInDataBase(babysittingEvent, position);
            }
        });
    }

    private void updateEventInDataBase(BabysittingEvent babysittingEvent, int position) {
        Log.d("ParentAdapter", "Updating event status in database"+babysittingEvent.toString());
        dataManager.updateEvent(babysittingEvent.getMessageId(), babysittingEvent, new DataManager.OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                Log.d("ParentAdapter", "Event status updated successfully");
                notifyItemChanged(position);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("ParentAdapter", "Failed to update event status", exception);
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
