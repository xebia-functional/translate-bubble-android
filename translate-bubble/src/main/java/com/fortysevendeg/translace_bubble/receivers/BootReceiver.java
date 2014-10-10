package com.fortysevendeg.translace_bubble.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.fortysevendeg.translace_bubble.ui.services.BubbleUIService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BubbleUIService.launchIfIsNecessary(context);
    }

}
