package com.honghe.jetpacktest;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class BluetoothDevicesAdapter extends RecyclerView.Adapter<BluetoothDevicesAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private static final String TAG = "BluetoothDevicesAdapter";

    public BluetoothDevicesAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<BluetoothDevice> bluetoothDevices) {
        if (null != bluetoothDevices) {
            this.bluetoothDevices.clear();
            this.bluetoothDevices.addAll(bluetoothDevices);
            notifyDataSetChanged();
        } else {
            Log.e(TAG, "setData: bluetoothDevices为空");
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth_device, viewGroup, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        viewHolder.tv_bt_name.setText(bluetoothDevices.get(i).getName());
        viewHolder.tv_bt_address.setText(bluetoothDevices.get(i).getAddress());
        final BluetoothDevice bluetoothDevice = bluetoothDevices.get(i);
        viewHolder.btn_connect.setOnClickListener(v -> {
            A2dpConnectionHelper.getInstance().connectA2dp(bluetoothDevice);
        });
        viewHolder.btn_disconnect.setOnClickListener(v -> {
            A2dpConnectionHelper.getInstance().disConnectA2dp(bluetoothDevice);
        });
    }

    @Override
    public int getItemCount() {
        return bluetoothDevices.size();
    }

    class MyViewHolder extends ViewHolder {
        TextView tv_bt_name, tv_bt_address;
        Button btn_disconnect, btn_connect;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_bt_name = itemView.findViewById(R.id.tv_bt_name);
            tv_bt_address = itemView.findViewById(R.id.tv_bt_address);
            btn_disconnect = itemView.findViewById(R.id.btn_disconnect);
            btn_connect = itemView.findViewById(R.id.btn_connect);
        }
    }
}
