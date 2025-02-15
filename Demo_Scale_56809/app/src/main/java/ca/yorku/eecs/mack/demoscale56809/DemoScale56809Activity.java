package ca.yorku.eecs.mack.demoscale56809;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;


/**
 * Demo_Android - with modifications by...
 *
 * Login ID - saikoro
 * Student ID - 219256809
 * Last name - Cao
 * First name(s) - Huanrui
 */

public class DemoScale56809Activity extends Activity
{
    PaintPanel imagePanel; // the panel in which to paint the image
    StatusPanel statusPanel; // a status panel the display the image coordinates, size, and scale

    // define the request code for the image picker
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title bar
        setContentView(R.layout.main);

        // get references to UI components
        // cast removed (not needed anymore, avoids warning message)
        imagePanel = findViewById(R.id.paintpanel);
        statusPanel = findViewById(R.id.statuspanel);

        // give the image panel a reference to the status panel
        imagePanel.setStatusPanel(statusPanel);
    }

    /**
     * Add method：Handle Pick Photo Button Click
     * @param view
     */
    public void clickSelectImage(View view) {
        // Create Intent to pick up photos
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // set the type to image
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                // 将URI转换为Drawable
                Drawable newImage = Drawable.createFromStream(
                        getContentResolver().openInputStream(selectedImageUri),
                        null
                );

                // 更新PaintPanel中的图片
                imagePanel.setTargetImage(newImage);

                // 重置位置和缩放
                clickReset(null);
            } catch (Exception e) {
                Log.e("ImageLoad", "Error loading image: " + e.getMessage());
            }
        }
    }

    // Called when the "Reset" button is pressed.
    public void clickReset(View view)
    {
        imagePanel.xPosition = 10;
        imagePanel.yPosition = 10;
        imagePanel.scaleFactor = 1f;
        imagePanel.invalidate();
    }


}
