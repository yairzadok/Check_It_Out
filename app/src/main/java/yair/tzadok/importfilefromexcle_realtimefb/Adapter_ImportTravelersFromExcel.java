package yair.tzadok.importfilefromexcle_realtimefb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Adapter_ImportTravelersFromExcel extends RecyclerView.Adapter<Adapter_ImportTravelersFromExcel.TravelerViewHolder> {

    private final Context context;
    private final ArrayList<Traveler> travelersAttendanceInfo;
    private final DatabaseReference databaseReference;

    public Adapter_ImportTravelersFromExcel(@NonNull Context context, ArrayList<Traveler> travelersAttendanceInfo) {
        this.context = context;
        this.travelersAttendanceInfo = travelersAttendanceInfo;
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child("Travelers");
    }

    // ViewHolder class
    public static class TravelerViewHolder extends RecyclerView.ViewHolder {
        public TextView travelerFullNameTV;
        public CheckBox travelerAttendanceStateCheckBox;

        public TravelerViewHolder(View itemView) {
            super(itemView);
            travelerFullNameTV = itemView.findViewById(R.id.traverFullNameTV);
            travelerAttendanceStateCheckBox = itemView.findViewById(R.id.travelerAttendanceStateCheckBox);
        }
    }

    @NonNull
    @Override
    public TravelerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.traveler_attendance_excel_list_view_item, parent, false);
        return new TravelerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelerViewHolder holder, int position) {
        Traveler traveler = travelersAttendanceInfo.get(position);

        holder.travelerFullNameTV.setText(traveler.getFirstName() + " " + traveler.getLastName());
        holder.travelerAttendanceStateCheckBox.setOnCheckedChangeListener(null); // Remove previous listener
        holder.travelerAttendanceStateCheckBox.setChecked(traveler.getTravelerAttendanceState().equals("true")); // Set state

        holder.travelerAttendanceStateCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            traveler.setTravelerAttendanceState(isChecked ? "true" : "false");
            databaseReference.child(traveler.getTravelerId())
                    .child("travelerAttendanceState")
                    .setValue(traveler.getTravelerAttendanceState())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return travelersAttendanceInfo.size();
    }

    // Method to attach swipe functionality to the RecyclerView
    public void attachSwipeToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // No move operation
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Traveler traveler = travelersAttendanceInfo.get(position);

                // Remove from Firebase
                databaseReference.child(traveler.getTravelerId()).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Traveler removed successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to remove traveler: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                // Remove from local list and notify adapter
                travelersAttendanceInfo.remove(position);
                notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}

