package net.arkellyga.adischedule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

public class PrefFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_NOTIFIES = "pref_notifies";
    SharedPreferences mSp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSp.registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_NOTIFIES)) {
            Log.d("PrefFragment", "Key_pref_notifies");
            if (sharedPreferences.getBoolean(KEY_PREF_NOTIFIES, true))
                if (!NotificationService.checkServiceAlarms(getActivity().getApplicationContext()))
                    NotificationService.updateAllServiceAlarms(getActivity().getApplicationContext());
            else
                NotificationService.deleteServiceAlarms(getActivity().getApplicationContext());
        }
    }
}
