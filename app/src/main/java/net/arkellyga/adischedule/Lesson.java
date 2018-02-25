package net.arkellyga.adischedule;

public class Lesson {

    private String mName;
    private String mTeacher;
    private String mRoom;
    private String mWeek;
    private String mTimeFrom;
    private String mTimeTo;
    private int mOrder;
    private int mId;
    private int mWindow;

    public Lesson(String name, String teacher, String room, String week,
                  int order, int window, String timeFrom, String timeTo) {
        mName = name;
        mTeacher = teacher;
        mRoom = room;
        mWeek = week;
        mOrder = order;
        mWindow = window;
        mTimeFrom = timeFrom;
        mTimeTo = timeTo;
    }

    public Lesson(String name, String teacher, String room, String week,
                  int order, int window) {
        mName = name;
        mTeacher = teacher;
        mRoom = room;
        mWeek = week;
        mOrder = order;
        mWindow = window;
    }

    public Lesson() {}

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTeacher() {
        return mTeacher;
    }

    public void setTeacher(String teacher) {
        mTeacher = teacher;
    }

    public String getRoom() {
        return mRoom;
    }

    public void setRoom(String room) {
        mRoom = room;
    }

    public String getWeek() {
        return mWeek;
    }

    public void setWeek(String week) {
        mWeek = week;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        mOrder = order;
    }

    public String getTimeFrom() {
        return mTimeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        mTimeFrom = timeFrom;
    }

    public String getTimeTo() {
        return mTimeTo;
    }

    public void setTimeTo(String timeTo) {
        mTimeTo = timeTo;
    }

    public int getWindow() {
        return mWindow;
    }

    public void setWindow(int window) {
        mWindow = window;
    }
}
