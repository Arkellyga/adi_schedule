package net.arkellyga.adischedule;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LessonEditorFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    private EditText mEtLesson;
    private EditText mEtRoom;
    private EditText mEtTeacher;
    private TextView mTvTimeFrom;
    private TextView mTvTimeTo;
    private CheckBox mCbUpWeek;
    private CheckBox mCbDownWeek;
    private CheckBox mCbWindow;
    private DataBaseHandler db;
    private static final String ARGS_DIALOG_ORDER = "args_order";
    private static final String ARGS_DIALOG_DAY = "args_day";
    private static final String ARGS_DIALOG_LESSON = "args_lesson";
    private static final String ARGS_DIALOG_TEACHER = "args_teacher";
    private static final String ARGS_DIALOG_ROOM = "args_room";
    private static final String ARGS_DIALOG_TIME_FROM = "args_time_from";
    private static final String ARGS_DIALOG_TIME_TO = "args_time_to";
    private static final String ARGS_DIALOG_WEEK = "args_week";
    private static final String ARGS_DIALOG_WINDOW = "args_window";
    private String mCurrentDay;
    private int mOrder;
    private String mLesson;
    private String mTeacher;
    private String mRoom;
    private String mTimeFrom;
    private String mTimeTo;
    private String mWeek;
    private int mWindow;
    private boolean mTimeSwitch;
    private boolean mTimeSet;

    static LessonEditorFragment newInstance(int currentDay, int order, String lesson,
                                            String teacher, String room,
                                            String timeFrom, String timeTo,
                                            int window, String currentWeek) {
        LessonEditorFragment fragment = new LessonEditorFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_DIALOG_DAY, currentDay);
        args.putInt(ARGS_DIALOG_ORDER, order);
        args.putInt(ARGS_DIALOG_WINDOW, window);
        args.putString(ARGS_DIALOG_LESSON, lesson);
        args.putString(ARGS_DIALOG_TEACHER, teacher);
        args.putString(ARGS_DIALOG_ROOM, room);
        args.putString(ARGS_DIALOG_TIME_FROM, timeFrom);
        args.putString(ARGS_DIALOG_TIME_TO, timeTo);
        args.putString(ARGS_DIALOG_WEEK, currentWeek);
        fragment.setArguments(args);
        fragment.setStyle(STYLE_NO_TITLE, 0);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.editor);
        View v = inflater.inflate(R.layout.fragment_lesson_dialog,null);
        mCurrentDay = DayHelper.getTableDayByNumber(getArguments().getInt(ARGS_DIALOG_DAY));
        mOrder = getArguments().getInt(ARGS_DIALOG_ORDER);
        mLesson = getArguments().getString(ARGS_DIALOG_LESSON);
        mTeacher= getArguments().getString(ARGS_DIALOG_TEACHER);
        mRoom = getArguments().getString(ARGS_DIALOG_ROOM);
        mTimeFrom = getArguments().getString(ARGS_DIALOG_TIME_FROM);
        mTimeTo = getArguments().getString(ARGS_DIALOG_TIME_TO);
        mWeek = getArguments().getString(ARGS_DIALOG_WEEK);
        mWindow = getArguments().getInt(ARGS_DIALOG_WINDOW);
        mTimeSet = false;
        setupUI(v);
        db = new DataBaseHandler(getActivity().getApplicationContext());
        return v;
    }

    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
    }

    private void setTime(boolean timeSet) {
        mTimeSwitch = timeSet;
        Calendar calendar = Calendar.getInstance();
        int hour = 0;
        int minute = 0;
        SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            if (mTimeSwitch)
                calendar.setTime(f.parse(mTvTimeFrom.getText().toString()));
            else
                calendar.setTime(f.parse(mTvTimeTo.getText().toString()));
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new TimePickerDialog(getContext(), mTimeSetListener, hour, minute, true).show();
    }

    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time = ((hourOfDay < 10) ? "0" + hourOfDay : hourOfDay) +
                    ":" + ((minute < 10) ? "0" + minute : minute);
            if (mTimeSwitch) {
                mTvTimeFrom.setText(time);
                mTimeFrom = time;
            } else {
                    mTvTimeTo.setText(time);
                    mTimeTo = time;
            }
            mTimeSet = true;
        }
    };

    public void onClick(View v) {

        if (v.getId() == R.id.button_ok_dialog) {
            String name = mEtLesson.getText().toString();
            String teacher = mEtTeacher.getText().toString();
            String room = mEtRoom.getText().toString();
            mWindow = mCbWindow.isChecked() ? 0 : 1;
            Lesson lesson = new Lesson(name, teacher, room,"", mOrder, mWindow, mTimeFrom, mTimeTo);

            if (mCbUpWeek.isChecked() && mCbDownWeek.isChecked()) {
                lesson.setWeek("up");
                db.updateLesson(lesson,mCurrentDay);
                lesson.setWeek("down");
            } else if (mCbUpWeek.isChecked()) {
                lesson.setWeek("up");
            } else if (mCbDownWeek.isChecked()) {
                lesson.setWeek("down");
            } else {
                Toast.makeText(getContext(),"Вы не выбрали неделю",Toast.LENGTH_SHORT).show();
                dismiss();
            }
            db.updateLesson(lesson,mCurrentDay);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
            if (mTimeSet) {
                boolean alarmEnabled = PreferenceManager.getDefaultSharedPreferences(
                        getContext()).getBoolean(PrefFragment.KEY_PREF_NOTIFIES, true);
                Log.d("LessonEditor","Alarm is " + alarmEnabled);
                if (alarmEnabled)
                    NotificationService.updateServiceAlarm(getContext().getApplicationContext(), mOrder, mTimeFrom);
            }
            dismiss();
        } else if (v.getId() == R.id.button_clear_dialog) {
            mEtLesson.setText("");
            mEtRoom.setText("");
            mEtTeacher.setText("");
        } else
            dismiss();
    }

    private void setupUI(View v) {
        mEtLesson = (EditText) v.findViewById(R.id.edit_text_lesson_dialog);
        mEtRoom = (EditText) v.findViewById(R.id.edit_text_room_dialog);
        mEtTeacher = (EditText) v.findViewById(R.id.edit_text_teacher_dialog);
        mCbUpWeek = (CheckBox) v.findViewById(R.id.checkbox_up_week_dialog);
        mCbDownWeek = (CheckBox) v.findViewById(R.id.checkbox_down_week_dialog);
        mCbWindow = (CheckBox) v.findViewById(R.id.checkbox_window_dialog);
        if (mWeek.equals("up"))
            mCbUpWeek.setChecked(true);
        else
            mCbDownWeek.setChecked(true);
        if (mWindow == 0)
            mCbWindow.setChecked(true);
        mTvTimeFrom = (TextView) v.findViewById(R.id.text_view_time_picker_from_dialog);
        mTvTimeTo = (TextView) v.findViewById(R.id.text_view_time_picker_to_dialog);
        mTvTimeFrom.setText(mTimeFrom);
        mTvTimeTo.setText(mTimeTo);
        mTvTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(true);
            }
        });
        mTvTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(false);
            }
        });
        mEtLesson.setText(mLesson);
        mEtRoom.setText(mRoom);
        mEtTeacher.setText(mTeacher);
        v.findViewById(R.id.button_ok_dialog).setOnClickListener(this);
        v.findViewById(R.id.button_cancel_dialog).setOnClickListener(this);
        v.findViewById(R.id.button_clear_dialog).setOnClickListener(this);
    }
}
