package ca.yorku.eecs.mack.demoandroid56809;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

/**
 * Demo_Android - with modification by ...
 *
 * Login ID - saikoro
 * Student ID - 219256809
 * Last Name - CAO
 * First Name - Huanrui
 */
public class DemoAndroid56809Activity extends Activity implements OnClickListener {
    private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    private Button incrementButton, decrementButton, exitButton;
    private Button resetButton; // declare the restButton
    private TextView textview;
    private int clickCount;

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
        Log.i(MYDEBUG, "Initialization done. Application running.");
    }


    private void initialize() {
        // get references to buttons and text view from the layout manager (rather than instantiate them)
        incrementButton = (Button) findViewById(R.id.incbutton);
        decrementButton = (Button) findViewById(R.id.decbutton);
        exitButton = (Button) findViewById(R.id.exitbutton);
        textview = (TextView) findViewById(R.id.textview);

        // some code is missing here
        resetButton = (Button) findViewById(R.id.resbutton); // initialize the resetButton

        //bind the buttons to OnClickListener
        incrementButton.setOnClickListener(this);
        decrementButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        // initialize the click count
        clickCount = 0;

        // initialize the text field with the click count
        textview.setText(String.format(Locale.CANADA, "Count: %d", clickCount));
    }

    /**
     * Implemented onClick for increment, decrement, and reset
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == incrementButton) {
            Log.i(MYDEBUG, "Increment button clicked!");
            ++clickCount;

        } else if (v == decrementButton) {
            Log.i(MYDEBUG, "Decrement button clicked!");
            --clickCount;

        } else if (v == exitButton) {
            Log.i(MYDEBUG, "Good bye!");
            this.finish();

        } else if(v == resetButton) {
            Log.i(MYDEBUG, "Reset button clicked!");
            clickCount = 0;
            //reset the clickCount when click the ResetButton
        }else
            Log.i(MYDEBUG, "Oops: Invalid Click Event!");

        // update click count
        textview.setText(String.format(Locale.CANADA, "Count: %d", clickCount));
    }
}
