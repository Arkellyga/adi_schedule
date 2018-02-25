package net.arkellyga.adischedule;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Calendar;
import java.util.List;

public class LessonFactory implements RemoteViewsService.RemoteViewsFactory {

    private List<Lesson> mData;
    private Context mContext;
    private int mWidgetId;
    private DataBaseHandler mDb;
    private String mCurrentWeek;
    private String mCurrentDay;

    LessonFactory(Context context, Intent intent, DataBaseHandler db) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mDb = db;

        mCurrentWeek = DayHelper.getCurrentWeek();
        mCurrentDay = DayHelper.getCurrentDayTable();
        mData = mDb.getLessons(mCurrentDay, mCurrentWeek);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Lesson lesson = mData.get(position);
        RemoteViews rView = new RemoteViews(mContext.getPackageName(), R.layout.lesson);
        rView.setTextViewText(R.id.text_view_lesson_and_room_lesson,
                (lesson.getOrder() + 1) + "." + lesson.getName() + " (" + lesson.getRoom() + ")");
        rView.setTextColor(R.id.text_view_lesson_and_room_lesson, Color.WHITE);
        rView.setTextViewText(R.id.text_view_teacher_lesson, lesson.getTeacher());
        rView.setTextColor(R.id.text_view_teacher_lesson, Color.WHITE);
        rView.setTextViewText(R.id.text_view_time_lesson, lesson.getTimeFrom() + " - " +
                lesson.getTimeTo());
        rView.setTextColor(R.id.text_view_time_lesson, Color.WHITE);
        return rView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}

