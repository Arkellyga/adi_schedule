package net.arkellyga.adischedule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class ThemeUtils {

    private static int mTheme;

    public static final int THEME_DARK = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_BLUE = 2;

    public static void changeToTheme(AppCompatActivity activity, int theme) {
        mTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void onActivityCreateSetTheme(AppCompatActivity activity, String theme) {
        int id = Integer.parseInt(theme);
        switch (id) {
            case THEME_DARK:
                activity.setTheme(R.style.ArrayStyle);
                break;
            case THEME_LIGHT:
                activity.setTheme(R.style.ArrayStyleLight);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.ArrayStyleBlue);
                break;
        }
    }
}
