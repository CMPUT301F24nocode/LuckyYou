/**
 * Adapter for displaying a list of facilities in a RecyclerView. Each facility item displays
 * the facility name and can be clicked to open a detailed view of the facility.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.View;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectv2.Model.Facility;
import com.example.projectv2.R;
import java.util.List;

/**
 * FacilityAdapter manages the display of a list of facilities in a RecyclerView.
 * Each item displays the facility's name and can be clicked to navigate to a detailed view.
 */
public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {

    private final Context context;
    private final List<Facility> facilityList;

    /**
     * Constructs a FacilityAdapter with the specified context and list of facilities.
     *
     * @param context      the context in which the adapter is operating
     * @param facilityList the list of facilities to display
     */
    public FacilityAdapter(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    /**
     * Inflates the layout for each facility item in the RecyclerView.
     *
     * @param parent   the ViewGroup into which the new view will be added
     * @param viewType the view type of the new view
     * @return a new FacilityViewHolder that holds the view for each facility item
     */
    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.facility_list_object, parent, false);
        return new FacilityViewHolder(view);
    }

    /**
     * Binds data to the view elements of each item in the RecyclerView.
     *
     * @param holder   the FacilityViewHolder containing view elements to bind data to
     * @param position the position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);
        holder.nameTextView.setText(facility.getName());

        // Set OnClickListener to open the facility landing page with details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FacilityLandingPageActivity.class);
            intent.putExtra("facility_id", facility.getId());
            intent.putExtra("facility_name", facility.getName());
            intent.putExtra("facility_description", facility.getDescription());
            context.startActivity(intent);
        });
    }

    /**
     * Returns the total number of facilities in the list.
     *
     * @return the total number of facilities
     */
    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    /**
     * ViewHolder class for each facility item in the RecyclerView.
     * Holds references to the UI elements within each facility item.
     */
    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        /**
         * Constructs a FacilityViewHolder and initializes UI elements for each facility item.
         *
         * @param itemView the view that holds facility item elements
         */
        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.facility_list_object_text);
        }
    }
}
