/**
 * ImageDownloaderTask is an AsyncTask for downloading and displaying an image from a URL.
 * It downloads the image in the background and sets it in the provided ImageView.
 * If the download fails, it displays a placeholder image.
 *
 * <p>Outstanding Issues: None currently identified.</p>
 */
package com.example.projectv2.Controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.projectv2.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask for downloading an image from a given URL and setting it on an ImageView.
 * It handles network operations on a background thread and updates the UI on the main thread.
 */
public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final ImageView imageView;

    /**
     * Constructs an ImageDownloaderTask with the specified ImageView to set the downloaded image.
     *
     * @param imageView the ImageView to display the downloaded image
     */
    public ImageDownloaderTask(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * Downloads the image in the background.
     *
     * @param params the URL of the image to download
     * @return the downloaded Bitmap, or null if the download failed
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        String urlString = params[0];
        Bitmap bmp = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream input = connection.getInputStream();
            bmp = BitmapFactory.decodeStream(input);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * Sets the downloaded image on the ImageView if successful; otherwise, sets a placeholder image.
     *
     * @param result the downloaded Bitmap, or null if the download failed
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            imageView.setImageBitmap(result);
        } else {
            imageView.setImageResource(R.drawable.placeholder_event);
        }
    }
}
