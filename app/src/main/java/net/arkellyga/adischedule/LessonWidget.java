package net.arkellyga.adischedule;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

public class LessonWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int widgetId : appWidgetIds) {
            updateWidget(context, widgetId, appWidgetManager);
        }
    }

    static void updateWidget(Context context, int widgetId, AppWidgetManager manager) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

        String currentWeek;
        if (DayHelper.getCurrentWeek().equals("up"))
            currentWeek = context.getResources().getString(R.string.up_week);
        else
            currentWeek = context.getResources().getString(R.string.down_week);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        rv.setOnClickPendingIntent(R.id.layout_widget, pIntent);
        rv.setOnClickPendingIntent(R.id.text_view_current_day_widget, pIntent);
        rv.setOnClickPendingIntent(R.id.text_view_current_week_widget, pIntent);
        rv.setTextViewText(R.id.text_view_current_week_widget, currentWeek);
        rv.setTextColor(R.id.text_view_current_week_widget, Color.BLACK);
        rv.setTextViewText(R.id.text_view_current_day_widget, DayHelper.getNameCurrentDay());
        rv.setTextColor(R.id.text_view_current_day_widget, Color.WHITE);
        setList(rv, context, widgetId);
        manager.updateAppWidget(widgetId, rv);
    }

    static private void setList(RemoteViews rv, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, LessonService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        rv.setRemoteAdapter(R.id.list_view_lessons_widget, adapter);
    }


}
