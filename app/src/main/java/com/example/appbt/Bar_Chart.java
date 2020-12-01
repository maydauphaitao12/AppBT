package com.example.appbt;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Bar_Chart extends AppCompatActivity {
    public Handler mHandler;
    private ConnectedThread btt;
    TextView response2;

    public String getCmdString(int value) {
        return "{\"cmd_type\":0,\"cmd\":" + value + "}";
    }

    public String getTypeString(int cmdType) {
        return "{\"cmd_type\": " + cmdType + "}";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar__chart);

        response2 = findViewById(R.id.response2);
        btt = ConnectedThread.getInstance();
        btt.addHandler(mHandler);
        BarChart chart = findViewById(R.id.barchart);

        ArrayList<BarEntry> NoOfEmp = new ArrayList<>();



        Log.i("[THREAD-CT]", "Receiving Data");


        NoOfEmp.add(new BarEntry(1501f, 220));
        NoOfEmp.add(new BarEntry(1645f, 520));
        NoOfEmp.add(new BarEntry(1578f, 620));

        String senddate = getTypeString(4);
        btt.write(senddate.getBytes());

        BarDataSet bardataset = new BarDataSet(NoOfEmp, "No Of Employee");
        bardataset.setColors(ColorTemplate.MATERIAL_COLORS);
        bardataset.setValueTextSize(16f);
        bardataset.setValueTextColor(Color.BLACK);

        BarData data = new BarData(bardataset);
        chart.setFitBars(true);
        chart.getDescription().setText("Bar Chart Example");
        chart.setData(data);
        chart.animateY(2000);
        data.setBarWidth(50f);
    }

    private void createMessageListener() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String txt = (String) msg.obj;
                response2.append("\n" + txt);
            }
        };
    }

}