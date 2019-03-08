package com.teksun.serialchatservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            //Log.i(StbService.TAG, "boot receiver");
            Intent i = new Intent(context, SerialChatService.class);
            context.startService(i);
        }
    }
}
