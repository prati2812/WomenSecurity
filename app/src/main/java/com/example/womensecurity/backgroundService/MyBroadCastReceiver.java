package com.example.womensecurity.backgroundService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MyBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent serviceIntent = new Intent(context , LocationRunningService.class);
                context.startForegroundService(serviceIntent);
            }
        }
    }
}
