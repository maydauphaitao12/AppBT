package com.example.appbt;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public final static String MODULE_MAC = "98:D3:34:90:6F:A1";
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static final String TAG = MainActivity.class.getSimpleName();
    public Handler mHandler;


    BluetoothAdapter bta = null;
    Button led2, led1, led3, connect, show_all, time_bt;
    TextView response;
    View home_home;
    ViewGroup tContainer;
    Boolean connect2 = false;

    private static final int SOLICITA_ATIVACAO = 2;
    private static final int SOLICITA_CONNECT = 3;
    private static String MAC = null;
    private Button chart_bt;
    private ConnectedThread btt;
    private Button[] ledBtnArr;
    private boolean[] ledStateArr = {false, false, false};


    public String getCmdString(int value) {
        return "{\"cmd_type\":0,\"cmd\":" + value + "}";
    }

    public String getTypeString(int cmdType) {
        return "{\"cmd_type\": " + cmdType + "}";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMessageListener();


        btt = ConnectedThread.getInstance();
        btt.addHandler(mHandler);

        response = (TextView) findViewById(R.id.response);
        time_bt = (Button) findViewById(R.id.time_bt);
        led1 = (Button) findViewById(R.id.led1);
        led2 = (Button) findViewById(R.id.led2);
        led3 = (Button) findViewById(R.id.led3);

        ledBtnArr = new Button[]{led1, led2, led3};
        connect = (Button) findViewById(R.id.connect);
        show_all = (Button) findViewById(R.id.show_all);
        home_home = (View) findViewById(R.id.home_home);
        tContainer = findViewById(R.id.transitionContainer);
        chart_bt = (Button) findViewById(R.id.chart_bt);


        Resources res = getResources();
        int color = res.getColor(R.color.button_click);

        bta = BluetoothAdapter.getDefaultAdapter();
        if (bta == null) {
            Toast.makeText(getApplicationContext(), "BlueTooth not supported", Toast.LENGTH_SHORT).show();
        } else if (!bta.isEnabled()) {
            Intent ativiabluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ativiabluetooth, SOLICITA_ATIVACAO);
        }

        chart_bt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Chart_BT.class);
            startActivity(intent);
        });


        time_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetTimer.class);
                startActivityForResult(intent, SOLICITA_CONNECT);
            }
        });

        connect.setOnClickListener(v -> {
            if (!connect2) {
                Intent ListPD = new Intent(MainActivity.this, ListBluetooth.class);
                startActivityForResult(ListPD, SOLICITA_CONNECT);
            }
        });

        LedState();
        bta = BluetoothAdapter.getDefaultAdapter();
        //if bluetooth is not enabled then create Intent for user to turn it on
        if (!bta.isEnabled()) {

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
    }


    public void LedState() {
        for (int i = 0; i < ledBtnArr.length; i++) {
            Button led = ledBtnArr[i];
            int finalI = i;
            led.setOnClickListener(v -> {

                if (!btt.isConnected()) {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isOn = ledStateArr[finalI];
                int code = isOn ? finalI * 2 + 2 : finalI * 2 + 1;
                String sendtxt = getCmdString(code);
                btt.write(sendtxt.getBytes());
                ledStateArr[finalI] = !isOn;
                led.setText("Led " + (finalI + 1) + " " + (isOn ? "OFF" : "ON"));

            });
        }
    }

    //tự động nhận thông báo từ ngoài về
    //Handle nhận vào txt bảng thông báo
    private void createMessageListener() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String txt = (String) msg.obj;
                response.append("\n" + txt);
                Log.e("createMessageListener", txt);
                processMessage(txt);
            }
        };
    }

    private void processMessage(String text) {
        try {
            JSONObject jsonObject = new JSONObject(text);
            int cmdType = jsonObject.getInt("cmd_type");
            if (cmdType == 0) {
                JSONArray jsonArray = jsonObject.getJSONArray("state");
                int ch1 = jsonArray.getInt(0);
                int ch2 = jsonArray.getInt(1);
                int ch3 = jsonArray.getInt(2);

                led1.setText("Led 1 " + (ch1 == 2 ? "OFF" : "ON"));
                led2.setText("Led 2 " + (ch2 == 4 ? "OFF" : "ON"));
                led3.setText("Led 3 " + (ch3 == 6 ? "OFF" : "ON"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SOLICITA_ATIVACAO:
                if (requestCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is activated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is not activated", Toast.LENGTH_SHORT).show();

                }
                break;

            case SOLICITA_CONNECT:
                if (resultCode == Activity.RESULT_OK) {
                    MAC = data.getExtras().getString(ListBluetooth.Add_MAC);

                    btt.connect(getApplicationContext(), MAC);
                    home_home.setVisibility(View.VISIBLE);

                    btt.write(getTypeString(3).getBytes());

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                }
        }
    }
}