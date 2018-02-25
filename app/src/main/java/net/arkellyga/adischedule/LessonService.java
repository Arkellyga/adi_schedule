package net.arkellyga.adischedule;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class LessonService extends RemoteViewsService {

    public LessonService() {

    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new LessonFactory(getApplicationContext(), intent, new DataBaseHandler(getApplicationContext()));
    }
}