package net.arkellyga.adischedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(PrefFragment.KEY_PREF_NOTIFIES,true)) {
                if (!NotificationService.checkServiceAlarms(context))
                    NotificationService.updateAllServiceAlarms(context);
                else Log.d("BootCompletedArray", "Alarms already set");
            }
        }
    }
}
