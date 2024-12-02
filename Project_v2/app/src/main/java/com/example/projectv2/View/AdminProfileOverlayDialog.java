package com.example.projectv2.View;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.projectv2.Controller.NotificationService;
import com.example.projectv2.Model.Notification;
import com.example.projectv2.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AdminProfileOverlayDialog extends DialogFragment {

    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_IS_ADMIN = "is_admin";

    /**
     * Creates a new instance of the AdminProfileOverlayDialog.
     *
     * @param userID The ID of the user profile to display.
     * @param isAdmin Whether the user is an admin.
     * @return A new instance of AdminProfileOverlayDialog.
     */
    public static AdminProfileOverlayDialog newInstance(String userID, boolean isAdmin) {
        AdminProfileOverlayDialog dialog = new AdminProfileOverlayDialog();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userID);
        args.putBoolean(ARG_IS_ADMIN, isAdmin);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Initializes the UI layout for the AdminProfileOverlayDialog.
     * Sets up the delete profile, delete image, and admin mode checkbox buttons.
     * Deletes the user profile, profile image, and updates the admin status when the buttons are clicked.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_admin_overlay, container, false);

        // Retrieve the userID and admin boolean from arguments
        String userID = getArguments() != null ? getArguments().getString(ARG_USER_ID) : "";
        boolean isAdmin = getArguments() != null && getArguments().getBoolean(ARG_IS_ADMIN);

        Button deleteProfile = view.findViewById(R.id.delete_profile_button);
        Button deleteImage = view.findViewById(R.id.delete_profile_image_button);
        CheckBox admin = view.findViewById(R.id.profile_admin_mode_checkbox_view);

        admin.setChecked(isAdmin);

        NotificationService notificationService = new NotificationService();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        admin.setOnCheckedChangeListener((buttonView, isChecked) -> db.collection("Users").document(userID)
                .update("admin", isChecked)
                .addOnSuccessListener(aVoid -> {
                    if (!isChecked) {
                        Notification notification = new Notification(userID, "Your admin privileges have been revoked", false, true);
                        notificationService.sendNotification(requireActivity(), notification, "-1");

                        SharedPreferences preferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("AdminMode", false);
                        editor.apply();
                    } else {
                        Notification notification = new Notification(userID, "You are now an admin", false, true);
                        notificationService.sendNotification(requireActivity(), notification, "-1");
                    }
                    Toast.makeText(requireContext(), "Admin status updated successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to update admin status. Please try again", Toast.LENGTH_SHORT).show();
                    dismiss();
                }));

        deleteProfile.setOnClickListener(v -> {
            Notification notification = new Notification(userID, "Your profile has been deleted by an admin. Please make a new profile.", false, true);
            notificationService.sendNotification(requireActivity(), notification, "-2");

            // Step 1: Delete all events with owner = userID
            db.collection("Events")
                    .whereEqualTo("owner", userID)
                    .get()
                    .addOnSuccessListener(eventSnapshot -> {
                        for (DocumentSnapshot document : eventSnapshot.getDocuments()) {
                            db.collection("Events").document(document.getId()).delete();
                        }

                        // Step 2: Delete all facilities with owner = userID
                        db.collection("Facilities")
                                .whereEqualTo("owner", userID)
                                .get()
                                .addOnSuccessListener(facilitySnapshot -> {
                                    for (DocumentSnapshot document : facilitySnapshot.getDocuments()) {
                                        db.collection("Facilities").document(document.getId()).delete();
                                    }

                                    // Step 3: Delete the user profile
                                    db.collection("Users").document(userID)
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(requireContext(), "User, related events, and facilities have been removed", Toast.LENGTH_SHORT).show();
                                                dismiss();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(requireContext(), "User could not be removed. Please try again", Toast.LENGTH_SHORT).show();
                                                dismiss();
                                            });
                                })
                                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to delete facilities. Please try again.", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to delete events. Please try again.", Toast.LENGTH_SHORT).show());
        });

        deleteImage.setOnClickListener(v -> {
            String imagePath = "profile_pictures/user_" + userID + ".jpg";
            StorageReference imageRef = storage.getReference().child(imagePath);

            imageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Notification notification = new Notification(userID, "Your profile picture has been removed by an admin", false, true);
                        notificationService.sendNotification(requireActivity(), notification, "-1");

                        Toast.makeText(requireContext(), "Profile image has been removed", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to remove profile image. Please try again", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
        });

        return view;
    }

    /**
     * Sets the dialog window size to wrap content.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
