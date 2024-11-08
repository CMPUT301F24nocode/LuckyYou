package com.example.projectv2.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectv2.Model.Facility;
import com.example.projectv2.R;
import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {

    private Context context;
    private List<Facility> facilityList;

    public FacilityAdapter(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.facility_list_object, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);
        holder.nameTextView.setText(facility.getName());

        // Set OnClickListener to open the facility landing page with details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FacilityLandingPageActivity.class);
            intent.putExtra("facility_name", facility.getName());
            intent.putExtra("facility_description", facility.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.facility_list_object_text);
        }
    }
}
