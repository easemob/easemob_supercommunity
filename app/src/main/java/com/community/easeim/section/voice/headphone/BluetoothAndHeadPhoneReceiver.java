package com.community.easeim.section.voice.headphone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothAndHeadPhoneReceiver extends BroadcastReceiver {
    public BluetoothAndHeadPhoneListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        switch (action) {
            case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                int state = adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
                if (BluetoothProfile.STATE_CONNECTED == state) {
                    mListener.bluetoothHeadset(true);
                } else if (BluetoothProfile.STATE_DISCONNECTED == state) {
                    mListener.bluetoothHeadset(false);
                }
                break;
            case Intent.ACTION_HEADSET_PLUG:
                if (intent.hasExtra("state")) {
                    int extra = intent.getIntExtra("state", 0);
                    mListener.headPhoneState(extra != 0);
                }
                break;
            default:
                break;
        }


    }

    public void setListener(BluetoothAndHeadPhoneListener listener) {
        this.mListener = listener;
    }
}
