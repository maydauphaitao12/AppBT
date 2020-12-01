package com.example.appbt;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Set;

public class ListBluetooth extends ListActivity {
    private BluetoothAdapter bluetoothAdapter2 = null;
    static String Add_MAC = null;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> arrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        bluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> dispositivosPareados = bluetoothAdapter2.getBondedDevices();

        if (dispositivosPareados.size() > 0){
            for (BluetoothDevice dispositivo : dispositivosPareados) {
                String nomeBt = dispositivo.getName();
                String macBt = dispositivo.getAddress();
                arrayBluetooth.add(nomeBt + "\n" + macBt);
            }
        }

        setListAdapter(arrayBluetooth);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String information = ((TextView) v).getText().toString();

        String addressMAC = information.substring(information.length() - 17);
        // Toast.makeText(this, "Address: " + addressMAC, Toast.LENGTH_SHORT).show();
        Intent userMAC = new Intent();
        userMAC.putExtra(Add_MAC, addressMAC);

        setResult(RESULT_OK, userMAC);
        finish();
    }
}
