package com.example.appbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.os.Handler;
import android.widget.Toast;

public class ConnectedThread extends Thread {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    public static final int RESPONSE_MESSAGE = 10;
    private String mac = "";
    private List<Handler> handlerList = new ArrayList<>();

    private static ConnectedThread instance;

    public static ConnectedThread getInstance() {
        if (instance == null) {
            instance = new ConnectedThread();
        }
        return instance;
    }

    public void addHandler(Handler uih) {
        handlerList.add(uih);
    }

    public void removeHandler(Handler uih) {
        handlerList.remove(uih);
    }

    public boolean isConnected() {
        return mmSocket.isConnected();
    }

    public void connect(Context context, String mac) {
        this.mac = mac;
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        if (!bta.isEnabled()) {
            Intent ativiabluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(ativiabluetooth);
            return;
        }

        BluetoothDevice mmDevice = bta.getRemoteDevice(mac);
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            mmSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        Log.i("[THREAD-CT]", "Creating thread");
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();

        } catch (IOException e) {
            Log.e("[THREAD-CT]", "Error:" + e.getMessage());
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        try {
            mmOutStream.flush();
        } catch (IOException e) {
            return;
        }
        Log.i("[THREAD-CT]", "IO's obtained");

        start();
    }

    public void run() {
        //byte[] buffer = new byte[1024];
        //int bytes;
        BufferedReader br = new BufferedReader(new InputStreamReader(mmInStream));
        Log.i("[THREAD-CT]", "Starting thread");
        while (true) {
            try {
                // bytes = mmInStream.read(buffer);
                String resp = br.readLine();
                //Transfer these data to the UI thread

                for (Handler uih : handlerList) {
                    Message msg = new Message();
                    msg.obj = resp;
                    if (uih != null) {
                        uih.sendMessage(msg);
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
        Log.i("[THREAD-CT]", "While loop ended");
    }

    public void write(byte[] bytes) {
        try {
            Log.i("[THREAD-CT]", "Writting bytes");
            mmOutStream.write(bytes);

        } catch (IOException e) {
        }
    }


    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}
