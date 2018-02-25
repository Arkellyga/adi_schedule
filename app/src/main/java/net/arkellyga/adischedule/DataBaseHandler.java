package net.arkellyga.adischedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "weekManager";

    private static final int COL_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_TEACHER = 2;
    private static final int COL_ROOM = 3;
    private static final int COL_WEEK = 4;
    private static final int COL_ORDER = 5;
    private static final int COL_WINDOW = 6;
    private static final int COL_TIMEFROM = 1;
    private static final int COL_TIMETO = 2;

    private static final String KEY_NAME = "name";
    private static final String KEY_TEACHER = "teacher";
    private static final String KEY_ROOM = "room";
    private static final String KEY_WEEK = "week";
    private static final String KEY_ORDER = "sequence";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_TIME_ORDER = "lessonid";
    private static final String KEY_TIME_FROM = "timefrom";
    private static final String KEY_TIME_TO = "timeto";
    public DataBaseHandler(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableValues = "(" +
                KEY_NAME + " TEXT," +
                KEY_TEACHER + " TEXT," +
                KEY_ROOM + " TEXT," +
                KEY_WEEK + " TEXT," +
                KEY_WINDOW + " INTEGER," +
                KEY_ORDER + " INTEGER" + ")";
        // Create db for lessons;
        db.execSQL("CREATE TABLE " + Values.TABLE_MONDAY + tableValues);
        db.execSQL("CREATE TABLE " + Values.TABLE_TUESDAY + tableValues);
        db.execSQL("CREATE TABLE " + Values.TABLE_WEDNESDAY + tableValues);
        db.execSQL("CREATE TABLE " + Values.TABLE_THURSDAY + tableValues);
        db.execSQL("CREATE TABLE " + Values.TABLE_FRIDAY + tableValues);
        // Create db for time for lessons;
        tableValues = "(" + KEY_TIME_ORDER + " INTEGER," +
                KEY_TIME_FROM + " TEXT," +
                KEY_TIME_TO + " TEXT)";
        db.execSQL("CREATE TABLE " + Values.TABLE_TIME + tableValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String drop_table = "DROP TABLE IF EXISTS ";
        db.execSQL(drop_table + Values.TABLE_MONDAY);
        db.execSQL(drop_table + Values.TABLE_TUESDAY);
        db.execSQL(drop_table + Values.TABLE_WEDNESDAY);
        db.execSQL(drop_table + Values.TABLE_THURSDAY);
        db.execSQL(drop_table + Values.TABLE_FRIDAY);
        db.execSQL(drop_table + Values.TABLE_TIME);
        onCreate(db);
    }

    public void addLesson(Lesson lesson, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, lesson.getName());
        cv.put(KEY_TEACHER, lesson.getTeacher());
        cv.put(KEY_ROOM, lesson.getRoom());
        cv.put(KEY_ORDER, lesson.getOrder());
        cv.put(KEY_WEEK, lesson.getWeek());
        db.insert(table, null, cv);
        Log.d("DB","Lesson added successful!");
        db.close();
    }

    public List<Lesson> getLessons(String table,String week) {
        List<Lesson> lessons = new ArrayList<>();
        Lesson lesson;
        SQLiteDatabase db = this.getReadableDatabase();
        // Sort is from 1 to 4 lessons with up or down week
        String sqlQuery = "select L.name as Name, window as Window, T.lessonid as Sequence, " +
                "teacher as Teacher, " +
                "room as Room, week as Week, timefrom as TimeFrom, timeto as TimeTo " +
                "from " + table + " as L " +
                "inner join timetable as T " +
                "on L.sequence = T.lessonid " +
                "where week = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[] {week});

        if (cursor.moveToFirst()) {
            do {
                lesson = new Lesson();
                lesson.setName(cursor.getString(cursor.getColumnIndex("Name")));
                lesson.setTeacher(cursor.getString(cursor.getColumnIndex("Teacher")));
                lesson.setRoom(cursor.getString(cursor.getColumnIndex("Room")));
                lesson.setWeek(cursor.getString(cursor.getColumnIndex("Week")));
                lesson.setOrder(cursor.getInt(cursor.getColumnIndex("Sequence")));
                lesson.setWindow(cursor.getInt(cursor.getColumnIndex("Window")));
                lesson.setTimeFrom(cursor.getString(cursor.getColumnIndex("TimeFrom")));
                lesson.setTimeTo(cursor.getString(cursor.getColumnIndex("TimeTo")));
                lessons.add(lesson);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lessons;
    }

    public List<Lesson> getAllLessons(String table) {
        List<Lesson> lessons = new ArrayList<>();
        Lesson lesson;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                lesson = new Lesson();
                lesson.setName(cursor.getString(COL_NAME));
                lesson.setTeacher(cursor.getString(COL_TEACHER));
                lesson.setRoom(cursor.getString(COL_ROOM));
                lesson.setWeek(cursor.getString(COL_WEEK));
                lesson.setOrder(cursor.getInt(COL_ORDER));
                lesson.setWindow(cursor.getInt(COL_WINDOW));
                lessons.add(lesson);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lessons;
    }

    public Lesson getLesson(String table,String week, int order) {
        Lesson lesson = new Lesson();
        SQLiteDatabase db = this.getReadableDatabase();
        // Sort is from 1 to 4 lessons with up or down week
        String sqlQuery = "select L.name as Name, T.lessonid as Sequence, teacher as Teacher, " +
                "room as Room, week as Week, window as Window, " +
                "timefrom as TimeFrom, timeto as TimeTo " +
                "from " + table + " as L " +
                "inner join timetable as T " +
                "on L.sequence = T.lessonid " +
                "where week = ? and lessonid = " + order;
        Cursor cursor = db.rawQuery(sqlQuery, new String[] {week});
        if (cursor.moveToFirst()) {
            do {
                Log.d("DataBaseHandler", "cursor working");
                lesson.setName(cursor.getString(cursor.getColumnIndex("Name")));
                lesson.setTeacher(cursor.getString(cursor.getColumnIndex("Teacher")));
                lesson.setRoom(cursor.getString(cursor.getColumnIndex("Room")));
                lesson.setWeek(cursor.getString(cursor.getColumnIndex("Week")));
                lesson.setOrder(cursor.getInt(cursor.getColumnIndex("Sequence")));
                lesson.setWindow(cursor.getInt(cursor.getColumnIndex("Window")));
                lesson.setTimeFrom(cursor.getString(cursor.getColumnIndex("TimeFrom")));
                lesson.setTimeTo(cursor.getString(cursor.getColumnIndex("TimeTo")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lesson;
    }

    public int updateLesson(Lesson lesson,String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, lesson.getName());
        cv.put(KEY_TEACHER, lesson.getTeacher());
        cv.put(KEY_ROOM, lesson.getRoom());
        cv.put(KEY_ORDER, lesson.getOrder());
        cv.put(KEY_WEEK, lesson.getWeek());
        cv.put(KEY_WINDOW, lesson.getWindow());
        int resultDay = db.update(table, cv, KEY_ORDER + " = " + lesson.getOrder() +
                        " AND " + KEY_WEEK + " = ?", new String[] { lesson.getWeek()});
        cv.clear();
        cv.put(KEY_TIME_FROM, lesson.getTimeFrom());
        cv.put(KEY_TIME_TO, lesson.getTimeTo());
        cv.put(KEY_TIME_ORDER, lesson.getOrder());
        int resultTime = db.update(Values.TABLE_TIME, cv, KEY_TIME_ORDER + " = " + lesson.getOrder(), null);
        db.close();
        Log.d("DB","Lesson was updated with values = { " + lesson.getName() +
                " " + lesson.getTeacher() + " " + lesson.getRoom() + " " + lesson.getWeek() + " " +
                lesson.getOrder() + " with result = " + resultDay + " and result time = " + resultTime);
        return resultDay;
    }

    public void setLessons(String table, List<Lesson> lessons) {
        deleteAllInTable(table);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (Lesson lesson : lessons) {
            cv.put(KEY_NAME, lesson.getName());
            cv.put(KEY_TEACHER, lesson.getTeacher());
            cv.put(KEY_ROOM, lesson.getRoom());
            cv.put(KEY_ORDER, lesson.getOrder());
            cv.put(KEY_WEEK, lesson.getWeek());
            cv.put(KEY_WINDOW, lesson.getWindow());
            db.insert(table, null, cv);
            cv.clear();
        }
        db.close();
        Log.d("DataBaseHandler", "setLessons done.");
    }

    public void setTimes(List<TimeLesson> times) {
        deleteAllInTable(Values.TABLE_TIME);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (TimeLesson time : times) {
            cv.put(KEY_TIME_ORDER, time.getOrder());
            cv.put(KEY_TIME_FROM, time.getTimeFrom());
            cv.put(KEY_TIME_TO, time.getTimeTo());
            db.insert(Values.TABLE_TIME, null, cv);
            cv.clear();
        }
        db.close();
    }

    public List<TimeLesson> getTimes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<TimeLesson> list = new ArrayList<>();
        Cursor cursor = db.query(Values.TABLE_TIME, null, null, null, null ,null ,null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new TimeLesson(cursor.getInt(COL_ID), cursor.getString(COL_TIMEFROM),
                        cursor.getString(COL_TIMETO)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteAllInTable(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table,null,null);
        db.close();
    }
}
