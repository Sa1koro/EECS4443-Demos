package ca.yorku.eecs.mack.demotiltball56809;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.Locale;
//add Result Activity
public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        int totalLaps = extras.getInt("totalLaps");
        float avgLapTime = extras.getFloat("avgLapTime");
        int wallHits = extras.getInt("wallHits");
        float inPathPercentage = extras.getFloat("inPathPercentage");

        // Update UI with result data
        TextView lapsText = (TextView) findViewById(R.id.result_laps);
        TextView lapTimeText = (TextView) findViewById(R.id.result_lap_time);
        TextView wallHitsText = (TextView) findViewById(R.id.result_wall_hits);
        TextView inPathText = (TextView) findViewById(R.id.result_in_path_time);

        lapsText.setText(String.format(Locale.CANADA, "Laps = %d", totalLaps));
        lapTimeText.setText(String.format(Locale.CANADA, "Lap time = %.2f s (mean/lap)", avgLapTime / 1000f));
        wallHitsText.setText(String.format(Locale.CANADA, "Wall Hits = %d", wallHits));
        inPathText.setText(String.format(Locale.CANADA, "In-path time = %.1f%%", inPathPercentage));
    }

    /**
     * Called when the "Return to Setup" button is pressed.
     */
    public void clickReturnToSetup(View view) {
        // Create intent to return to setup
        Intent i = new Intent(getApplicationContext(), DemoTiltBallSetup.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack
        startActivity(i);
        finish();
    }

    /**
     * Called when the "Exit" button is pressed.
     */
    public void clickExit(View view) {
        // Finish all activities and exit the app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }
}