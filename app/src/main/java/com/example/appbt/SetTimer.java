package com.example.appbt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class SetTimer extends AppCompatActivity implements View.OnClickListener {
    Button btnTimePicker, btnTimePicker1;
    EditText txtTime, txtTime1;

    RadioGroup radioGroup;
    RadioButton radioButton, radio_on, radio_off;
    TextView textView;
    ConnectedThread btt;
    TextView response2;

    private int hourTimer = 0;
    private int minuteTimer = 0;

    private int mHour, mMinute;
    private EditText[] txtTimer;
    private boolean[] btnTimerState = {false, false};
    int variable;
    MainActivity mainActivity;

    public String getTypeString(int cmdType) {
        return "{\"cmd_type\": " + cmdType + "}";
    }


    public String getTimeString(int cmd_type, int cmd, int hour, int minutes, int second) {
        return "{\"cmd_type\": " + cmd_type + ",\"cmd\": " + cmd + ",\"hour\": " + hour +
                ",\"minutes\": " + minutes + ",\"minutes\": " + second + "}";
    }

    boolean setRadio = false;
    public Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_timer);

        createMessageListener();
        btt = ConnectedThread.getInstance();
        btt.addHandler(mHandler);

        response2 = findViewById(R.id.response2);
        btnTimePicker1 = (Button) findViewById(R.id.btn_time1);
        txtTime1 = (EditText) findViewById(R.id.in_time1);

        txtTimer = new EditText[]{txtTime, txtTime1};
        radioGroup = findViewById(R.id.radioGroup);
        radio_on = findViewById(R.id.radio_on);

        Button buttonApply = findViewById(R.id.button_apply);

        btnTimePicker1.setOnClickListener(v -> {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(SetTimer.this,
                    (view, hourOfDay, minute) -> {
                        hourTimer = hourOfDay;
                        minuteTimer = minute;
                        txtTime1.setText(hourOfDay + ":" + minute);

                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        buttonApply.setOnClickListener(v -> {
            //        /*{cmd_type:1, "cmd":1, "hour":17, "minutes":10, "second":0}*/

            int cmdType = radio_on.isChecked() ? 1 : 2;

            String sendTime = getTimeString(cmdType, 1, hourTimer, minuteTimer, 0);
            Log.e("Char send", sendTime);
            btt.write(sendTime.getBytes());
        });
    }

    private void createMessageListener() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String txt = (String) msg.obj;
                response2.append("\n" + txt);
                Log.e("Char", txt);
            }
        };
    }
//    private void processMessage(String text){
//        /*{cmd_type:1, "cmd":1, "hour":17, "minutes":10, "second":0}*/
//        try {
//            JSONObject jsonObject = new JSONObject(text);
//            int cmdType = jsonObject.getInt("cmd_type");
//
//            if (cmdType == 1){
//                int devIndex = jsonObject.getInt("dev");
//                int hour = jsonObject.getInt("");
//                int totalMinute_dev = hour*60;
//
//
//            }
//
//        }
//        catch (JSONException e){
//            e.printStackTrace();
//        }
//    }

    public void onClick(View v) {
    }


    public void checkButton(View v) {

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        Toast.makeText(this, "Selected Radio Button: " + radioButton.getText(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}