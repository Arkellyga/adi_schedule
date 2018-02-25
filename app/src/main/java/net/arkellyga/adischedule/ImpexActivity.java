package net.arkellyga.adischedule;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ImpexActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnExport;
    ListView lvBackup;
    private String[] mFiles;
    private final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Array/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        ThemeUtils.onActivityCreateSetTheme(this, sp.getString("pref_theme", "0"));
        setContentView(R.layout.activity_impex);
        btnExport = (Button) findViewById(R.id.button_export_impex);
        lvBackup = (ListView) findViewById(R.id.list_view_backup_impex);
        getFiles();
        if (mFiles != null) {
            lvBackup.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mFiles));
            lvBackup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    confirmDialog(mFiles[position]);
                }
            });
        }
        btnExport.setOnClickListener(this);
    }

    private void getFiles() {
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files == null)
            return;
        String[] result = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            result[i] = path + files[i].getName();
        }
        mFiles = result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_export_impex:
                exportLessons();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successful_export), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void confirmDialog(final String file) {
        (new AlertDialog.Builder(this))
                .setTitle(getResources().getString(R.string.confirm_import_title))
                .setMessage(getResources().getString(R.string.confirm_import))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        importLessons(file);
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.successful_import), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void importLessons(String file) {
        String TAG = "ImpexActivity";
        try {
            DataBaseHandler db = new DataBaseHandler(this);
            XmlPullParser parser;
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(new FileReader(file));
            String day = "";
            List<Lesson> lessons = new ArrayList<>();
            Lesson lesson = new Lesson();
            List<TimeLesson> times = new ArrayList<>();
            TimeLesson tLesson = new TimeLesson();
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (parser.getEventType()) {
                    case XmlPullParser.START_TAG:
                        switch (parser.getName()) {
                            case "day":
                                day = parser.getAttributeValue(0);
                                break;
                            case "lesson":
                                lesson = new Lesson();
                                lesson.setOrder(Integer.parseInt(parser.getAttributeValue(0)));
                                lesson.setWeek(parser.getAttributeValue(1));
                                break;
                            case "name":
                                parser.next();
                                lesson.setName(parser.getText());
                                break;
                            case "teacher":
                                parser.next();
                                lesson.setTeacher(parser.getText());
                                break;
                            case "room":
                                parser.next();
                                lesson.setRoom(parser.getText());
                                break;
                            case "time":
                                tLesson = new TimeLesson();
                                tLesson.setOrder(Integer.parseInt(parser.getAttributeValue(0)));
                                tLesson.setTimeFrom(parser.getAttributeValue(1));
                                tLesson.setTimeTo(parser.getAttributeValue(2));
                                break;
                        }
                        Log.d(TAG, day);
                        break;
                    case XmlPullParser.END_TAG:
                        switch (parser.getName()) {
                            case "day":
                                db.setLessons(day, lessons);
                                lessons = new ArrayList<>();
                                break;
                            case "lesson":
                                lessons.add(lesson);
                                break;
                            case "timetable":
                                db.setTimes(times);
                                break;
                            case "time":
                                times.add(tLesson);
                                break;
                        }
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void exportLessons() {
        try {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            DataBaseHandler db = new DataBaseHandler(getApplicationContext());
            List<Lesson> lessons;
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "array");
            String[] days = new String[] {Values.TABLE_MONDAY, Values.TABLE_TUESDAY,
                    Values.TABLE_WEDNESDAY, Values.TABLE_THURSDAY, Values.TABLE_FRIDAY};
            for (String day : days) {
                lessons = db.getAllLessons(day);
                serializer.startTag("", "day");
                serializer.attribute("","type", day);
                for (Lesson lesson : lessons) {
                    serializer.startTag("","lesson");
                    serializer.attribute("","order", String.valueOf(lesson.getOrder()));
                    serializer.attribute("","type", lesson.getWeek());
                    serializer.startTag("","name");
                    serializer.text(lesson.getName());
                    serializer.endTag("","name");
                    serializer.startTag("","teacher");
                    serializer.text(lesson.getTeacher());
                    serializer.endTag("","teacher");
                    serializer.startTag("","room");
                    serializer.text(lesson.getRoom());
                    serializer.endTag("","room");
                    serializer.endTag("","lesson");
                }
                serializer.endTag("","day");
            }
            List<TimeLesson> times = db.getTimes();
            serializer.startTag("","timetable");
            serializer.attribute("","type","time");
            for (TimeLesson time : times) {
                serializer.startTag("","time");
                serializer.attribute("","order", String.valueOf(time.getOrder()));
                serializer.attribute("","from", time.getTimeFrom());
                serializer.attribute("","to", time.getTimeTo());
                serializer.endTag("","time");
            }
            serializer.endTag("","timetable");
            serializer.endDocument();
            //Save file in ...
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, getResources().getString(R.string.save_file_error_export), Toast.LENGTH_SHORT).show();
                return;
            }
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/Array/");
            Log.d("Impex", "mkdirs is " + sdPath.mkdirs());
            Calendar calendar = Calendar.getInstance();
            File file = new File(sdPath, "Lessons_" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "." +
                    calendar.get(Calendar.MONTH) + "_" +
                    calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                    calendar.get(Calendar.MINUTE) + ".xml");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(writer.toString());
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
