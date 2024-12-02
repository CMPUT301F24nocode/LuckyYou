    package com.example.projectv2.Controller;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.projectv2.Model.User;
    import com.example.projectv2.R;

    import java.util.List;

    public class AdminProfileAdapter extends RecyclerView.Adapter<AdminProfileAdapter.ViewHolder> {

        private final List<User> userList;
        private final OnItemClickListener onItemClickListener;

        public interface OnItemClickListener {
            void onItemClick(String userID);
        }

        public AdminProfileAdapter(List<User> userList, OnItemClickListener onItemClickListener) {
            this.userList = userList;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.admin_profile_list_object, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = userList.get(position);
            holder.adminProfileName.setText(user.getName());

            // Set the click listener
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(user.getDeviceID());
                }
            });
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView adminProfileName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                adminProfileName = itemView.findViewById(R.id.admin_profile_name);
            }
        }
    }
