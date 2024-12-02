package com.example.projectv2.Utils;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.example.projectv2.Controller.ProfileImageController;

public class ProfilePictureGenerator {

    // Generate a profile picture bitmap with initials and random background color
    public static Bitmap generateProfilePicture( String name, int imageSize) {
        // Split the name to get initials
        String[] nameParts = name.split(" ");
        String initials = "";
        for (String part : nameParts) {
            if (!part.isEmpty() && initials.length() < 2) {
                initials += part.charAt(0);
            }
        }
        initials = initials.toUpperCase();

        // Create a Bitmap
        Bitmap bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Generate a random background color
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(getColor());
        backgroundPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, imageSize, imageSize, backgroundPaint);

        // Draw the initials
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(imageSize / 2);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Measure the text bounds
        Rect textBounds = new Rect();
        textPaint.getTextBounds(initials, 0, initials.length(), textBounds);
        float x = imageSize / 2f;
        float y = imageSize / 2f - textBounds.exactCenterY();

        canvas.drawText(initials, x, y, textPaint);

        return bitmap;
    }

    // Generate a random color
    private static int getColor() {
//        Random random = new Random();
        return Color.parseColor("#5669FF");
    }
}
