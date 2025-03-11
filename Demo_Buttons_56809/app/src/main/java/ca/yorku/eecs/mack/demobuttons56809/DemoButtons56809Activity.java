package ca.yorku.eecs.mack.demobuttons56809;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Demo_Buttons- with modifications by...
 *
 * Login ID - saikoro
 * Student ID - 219256809
 * Last name - CAO
 * First name(s) - Huanrui
 */
@SuppressWarnings("unused")
public class DemoButtons56809Activity extends Activity {
    private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    private final static String BUTTON_CLICK_KEY = "BUTTON_CLICK_KEY";// storage button click string
    private final static String BACK_CLICK_KEY = "BACK_CLICK_KEY";// storage backspace string

    Button b;
    CheckBox cb;
    RadioButton rb1, rb2, rb3;
    ToggleButton tb;
    ImageButton backspaceButton;
    TextView buttonClickStatus, checkBoxClickStatus, radioButtonClickStatus, toggleButtonClickStatus,
            backspaceButtonClickStatus;
    Button exitButton;

    String buttonClickString, backspaceString;
    String checkStatus; //change boolean to string for savedInstanceState.getBoolean()
    String rb1Status, rb2Status, rb3Status; //change boolean to string for savedInstanceState.getBoolean()
    String tbStatus; //change boolean to string for savedInstanceState.getBoolean()

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    private void init() {
        b = (Button) findViewById(R.id.button);
        cb = (CheckBox) findViewById(R.id.checkbox);
        rb1 = (RadioButton) findViewById(R.id.radiobutton1);
        rb2 = (RadioButton) findViewById(R.id.radiobutton2);
        rb3 = (RadioButton) findViewById(R.id.radiobutton3);
        rb1.toggle();
        tb = (ToggleButton) findViewById(R.id.togglebutton);
        backspaceButton = (ImageButton) findViewById(R.id.backspacebutton);
//        remove exitButton
//        exitButton = (Button) findViewById(R.id.exitbutton);

        buttonClickStatus = (TextView) findViewById(R.id.buttonclickstatus);
        checkBoxClickStatus = (TextView) findViewById(R.id.checkboxclickstatus);
        radioButtonClickStatus = (TextView) findViewById(R.id.radiobuttonclickstatus);
        toggleButtonClickStatus = (TextView) findViewById(R.id.togglebuttonclickstatus);
        backspaceButtonClickStatus = (TextView) findViewById(R.id.backspacebuttonclickstatus);

        buttonClickString = "";
        backspaceString = "";

        buttonClickStatus.setText(buttonClickString);
        checkBoxClickStatus.setText(R.string.unchecked);
        radioButtonClickStatus.setText(R.string.red);
        radioButtonClickStatus.setTextColor(Color.RED);
        toggleButtonClickStatus.setText(R.string.off);
    }

    // handle button clicks
    public void buttonClick(View v) {
        // plain button
        if (v == b) {
            buttonClickString += ".";
            buttonClickStatus.setText(buttonClickString);
        }

        // checkbox
        else if (v == cb) {
            if (cb.isChecked()) {
                cb.setChecked(true);
                checkBoxClickStatus.setText(R.string.checked);
            } else {
                cb.setChecked(false);
                checkBoxClickStatus.setText(R.string.unchecked);
            }
        }

        // radio button #1 (RED)
        else if (v == rb1) {
            rb1.setChecked(true);
            radioButtonClickStatus.setText(R.string.red);
            radioButtonClickStatus.setTextColor(Color.RED);
        }

        // radio button #2 (GREEN)
        else if (v == rb2) {
            rb2.setChecked(true);
            radioButtonClickStatus.setText(R.string.green);
            radioButtonClickStatus.setTextColor(Color.GREEN);
        }

        // radio button #3 (BLUE)
        else if (v == rb3) {
            rb3.setChecked(true);
            radioButtonClickStatus.setText(R.string.blue);
            radioButtonClickStatus.setTextColor(Color.BLUE);
        }

        // toggle button
        else if (v == tb) {
            tb.setActivated(tb.isChecked());
            if (tb.isChecked())
                toggleButtonClickStatus.setText(R.string.on);
            else
                toggleButtonClickStatus.setText(R.string.off);
        }

        // backspace button
        else if (v == backspaceButton) {
            backspaceString += "BK ";
            backspaceButtonClickStatus.setText(backspaceString);
        }

        // exit button
        else if (v == exitButton) {
            this.finish();
        }
    }

    /**
     *  save the Instance State
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUTTON_CLICK_KEY, buttonClickString);
        outState.putString(BACK_CLICK_KEY, backspaceString);
        outState.putBoolean(checkStatus,cb.isChecked());
        outState.putBoolean(checkStatus,rb1.isChecked());
        outState.putBoolean(checkStatus,rb2.isChecked());
        outState.putBoolean(checkStatus,rb3.isChecked());
        outState.putBoolean(checkStatus,tb.isChecked());
    }

    /**
     *  restore the Instance State
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        buttonClickString = savedInstanceState.getString(BUTTON_CLICK_KEY);
        backspaceString = savedInstanceState.getString(BACK_CLICK_KEY);
        buttonClickStatus.setText(buttonClickString);
        backspaceButtonClickStatus.setText(backspaceString);

        tb.setActivated(savedInstanceState.getBoolean(tbStatus));
        if (tb.isChecked())
            toggleButtonClickStatus.setText(R.string.on);
        else
            toggleButtonClickStatus.setText(R.string.off);

        cb.setActivated(savedInstanceState.getBoolean(checkStatus));
        rb1.setActivated(savedInstanceState.getBoolean(rb1Status));
        rb2.setActivated(savedInstanceState.getBoolean(rb2Status));
        rb3.setActivated(savedInstanceState.getBoolean(rb3Status));

        if (cb.isChecked()){
            checkBoxClickStatus.setText(R.string.checked);
        } else {
            checkBoxClickStatus.setText(R.string.unchecked);
        }

        if (rb1.isChecked()){
            radioButtonClickStatus.setText(R.string.red);
            radioButtonClickStatus.setTextColor(Color.RED);
        }
        if (rb3.isChecked()) {
            radioButtonClickStatus.setText(R.string.blue);
            radioButtonClickStatus.setTextColor(Color.BLUE);
        }
        if (rb2.isChecked()) {
            radioButtonClickStatus.setText(R.string.green);
            radioButtonClickStatus.setTextColor(Color.GREEN);
        }

    }
}
