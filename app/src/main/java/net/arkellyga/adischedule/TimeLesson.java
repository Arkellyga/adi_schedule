package net.arkellyga.adischedule;

public class TimeLesson {
    private String mTimeFrom;
    private String mTimeTo;
    private int mOrder;

    public TimeLesson() {}

    public TimeLesson(int order, String timeFrom, String timeTo) {
        mOrder = order;
        mTimeFrom = timeFrom;
        mTimeTo = timeTo;
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

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        mOrder = order;
    }
}
