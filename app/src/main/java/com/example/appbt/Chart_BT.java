package com.example.appbt;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Chart_BT extends AppCompatActivity {

    Button date_bt, week_bt, month_bt;
    TextView response1;
    public Handler mHandler;

    private StringBuilder recDataString = new StringBuilder();

    public String getTypeString(int cmdType) {
        return "{\"cmd_type\": " + cmdType + "}";
    }

    private static String MAC = null;
    private ConnectedThread btt;
    ArrayList<BarEntry> NoOfEmp = new ArrayList<>();
    private BarChart chart;
    private int spaceItemChar = 100;
    private int startItemChar = 0;
    BarData data;
    BarDataSet bardataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart__b_t);

        createMessageListener();
        // gọi handle để show ra màn hình
        btt = ConnectedThread.getInstance();
        btt.addHandler(mHandler);

        date_bt = findViewById(R.id.date_bt);
        week_bt = findViewById(R.id.week_bt);
        month_bt = findViewById(R.id.month_bt);
        response1 = findViewById(R.id.response1);

        chart_Bar();

        date_bt.setOnClickListener(view -> {
            String senddate = getTypeString(4);
            Log.e("Char send", senddate);
            btt.write(senddate.getBytes());


        });
        week_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senddate = getTypeString(5);
                Log.e("Char send", senddate);
                btt.write(senddate.getBytes());
            }
        });

        month_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senddate = getTypeString(6);
                Log.e("Char send", senddate);
                btt.write(senddate.getBytes());

            }
        });

    }

    private void chart_Bar() {
        chart = findViewById(R.id.barchart);
        for (int i = 0; i < 3; i++) {
            NoOfEmp.add(new BarEntry(startItemChar + spaceItemChar * i, 0));
        }

        bardataset = new BarDataSet(NoOfEmp, "No Of Employee");
        bardataset.setColors(ColorTemplate.MATERIAL_COLORS);
        bardataset.setValueTextSize(16f);
        bardataset.setValueTextColor(Color.BLACK);

        data = new BarData(bardataset);
        chart.setFitBars(true);
        chart.getDescription().setText("Bar Chart Example");
        chart.setData(data);
        chart.animateY(2000);
        data.setBarWidth(50f);
        chart.getAxisLeft().setAxisMaximum(24 * 60);

    }

    //tự động nhận thông báo từ ngoài về
    //Handle nhận vào txt bảng thông báo
    private void createMessageListener() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String txt = (String) msg.obj;
                response1.append("\n" + txt);
                Log.e("Char", txt);
                //lắng nghe sự kiện
                processMessage(txt);
            }
        };
    }

    //xét sự kiện và bóc tách sự kiện
    private void processMessage(String text) {
        /*
            {"cmd_type":4, dev":0, "time_active_day":[0, 0, 0]}
        */
        try {
            JSONObject jsonObject = new JSONObject(text);
            int cmdType = jsonObject.getInt("cmd_type");
            // cmd == 4 => for time active to draw chart
            if (cmdType == 4) {
                int devIndex = jsonObject.getInt("dev");
                JSONArray jsonArray = jsonObject.getJSONArray("time_active_day");
                /* dev 0 */
                int hour = jsonArray.getInt(0);
                int minute = jsonArray.getInt(1);
                int totalMinute_dev = hour * 60 + minute;
                updateChart(devIndex, totalMinute_dev);
            }
            if (cmdType == 5) {
                int devIndex = jsonObject.getInt("dev");
                int hour = jsonObject.getInt("time_active_week");

                int totalMinute_dev = hour*60;
                updateChart(devIndex, totalMinute_dev);
            }
          if (cmdType == 6) {
                int devIndex = jsonObject.getInt("dev");
                int hour = jsonObject.getInt("time_active_month");
                int totalMinute_dev = hour*60;
                updateChart(devIndex, totalMinute_dev);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateChart(int index, int val) {
        NoOfEmp.set(index, new BarEntry(startItemChar + spaceItemChar * index, val));
        bardataset.notifyDataSetChanged();
        chart.notifyDataSetChanged(); // let the chart know it's data changed
        chart.invalidate(); // refresh
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                break;
        }
        return false;
    }
}