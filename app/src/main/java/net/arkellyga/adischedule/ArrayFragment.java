package net.arkellyga.adischedule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ArrayFragment extends Fragment {

    static final String ARGUMENT_ARRAY_MODE = "arg_array_mode";
    static final String ARGUMENT_CURRENT_DAY = "arg_current_day";
    static final String ARGUMENT_CURRENT_WEEK = "arg_current_week";
    static final String DIALOG_EDIT = "dialog_edit";
    static final int REQUEST_EDITOR = 1;

    private boolean arrayMode;
    private RecyclerView mRecyclerView;
    private LessonAdapter mAdapter;
    private DataBaseHandler db;
    private int mCurrentDay;
    private String mCurrentWeek;
    private ImageView mImageViewChange;
    private TextView mTvCurrentWeek;

    private OnFragmentChangeWeekListener mListener;

    static ArrayFragment newInstance(boolean mode, int currentDay, String currentWeek) {
        ArrayFragment fragment = new ArrayFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_CURRENT_WEEK, currentWeek);
        arguments.putBoolean(ARGUMENT_ARRAY_MODE,mode);
        arguments.putInt(ARGUMENT_CURRENT_DAY, currentDay);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DataBaseHandler(getContext());
        arrayMode = getArguments().getBoolean(ARGUMENT_ARRAY_MODE,false);
        mCurrentDay = getArguments().getInt(ARGUMENT_CURRENT_DAY, 0);
        mCurrentWeek = getArguments().getString(ARGUMENT_CURRENT_WEEK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_array,null);
        mImageViewChange = (ImageView) v.findViewById(R.id.image_view_change_array);
        mTvCurrentWeek = (TextView) v.findViewById(R.id.text_view_current_week_array);
        mImageViewChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentChangeWeek(mCurrentWeek, arrayMode);
                //setCurrentWeek();
                //changeLessonsList();
            }
        });
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        mImageViewChange.startAnimation(anim);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_array);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(25));
        // odd is down week, even is up week;
        List<Lesson> lessons = db.getLessons(DayHelper.getTableDayByNumber(mCurrentDay), mCurrentWeek);
        setCurrentWeek();
        mAdapter = new LessonAdapter(lessons);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    private void setCurrentWeek() {
        if (mCurrentWeek.equals("up")) {
            mCurrentWeek = "down";
            mTvCurrentWeek.setText(getResources().getString(R.string.up_week));
        }
        else {
            mCurrentWeek = "up";
            mTvCurrentWeek.setText(getResources().getString(R.string.down_week));
        }
    }

    private void changeLessonsList() {
        mAdapter.setLessons(db.getLessons(DayHelper.getTableDayByNumber(mCurrentDay), mCurrentWeek));
        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_EDITOR:
                    changeLessonsList();
                    break;
                default:
                    break;
            }
        }
    }

    interface OnFragmentChangeWeekListener {

        void onFragmentChangeWeek(String week, boolean mode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentChangeWeekListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " нет реализации");
        }
    }

    private class LessonHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mLessonName;
        private TextView mTeacher;
        private TextView mTime;
        private Lesson mLesson;
        private ImageView mImgButton;

        public LessonHolder(View itemView) {
            super(itemView);
            mLessonName = (TextView) itemView.findViewById(R.id.text_view_lesson_and_room_lesson);
            mLessonName.setTypeface(null, Typeface.BOLD);
            mTeacher = (TextView) itemView.findViewById(R.id.text_view_teacher_lesson);
            mTeacher.setTypeface(null, Typeface.ITALIC);
            mTime = (TextView) itemView.findViewById(R.id.text_view_time_lesson);
            if (arrayMode) {
                mImgButton = new ImageView(getContext());
                mImgButton.setImageResource(R.drawable.ic_edit);
                mImgButton.setId(R.id.recycler_view_edit_button);
                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,0.2f);
                ((LinearLayout) itemView).addView(mImgButton,lParams);
                mImgButton.setOnClickListener(this);
            }
        }

        public void bindCrime(Lesson lesson) {
            mLesson = lesson;
            mLessonName.setText((mLesson.getOrder() + 1) + ". " + mLesson.getName() +
                                " (" + mLesson.getRoom() + ")");
            mTeacher.setText(mLesson.getTeacher());
            mTime.setText(lesson.getTimeFrom() + " - " + lesson.getTimeTo());
        }

        @Override
        public void onClick(View v) {
            DialogFragment lessonEditor = LessonEditorFragment.newInstance(mCurrentDay, getAdapterPosition(),
                     mLesson.getName(), mLesson.getTeacher(), mLesson.getRoom(),
                    mLesson.getTimeFrom(), mLesson.getTimeTo(), mLesson.getWindow(), mLesson.getWeek());
            lessonEditor.setTargetFragment(ArrayFragment.this,REQUEST_EDITOR);
            lessonEditor.show(getFragmentManager(),DIALOG_EDIT);
        }
    }

    private class LessonAdapter extends RecyclerView.Adapter<LessonHolder> {

        private List<Lesson> mLessons;

        public LessonAdapter(List<Lesson> lessons) {
            mLessons = lessons;
        }

        @Override
        public LessonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View v = inflater.inflate(R.layout.lesson,parent,false);
            return new LessonHolder(v);
        }

        @Override
        public void onBindViewHolder(LessonHolder holder, final int position) {
            Lesson lesson = mLessons.get(position);
            holder.bindCrime(lesson);
        }

        @Override
        public int getItemCount() {
            return mLessons.size();
        }

        public void setLessons(List<Lesson> lessons) {
            mLessons = lessons;
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int mSpace;

        public SpacesItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            //добавить переданное кол-во пикселей отступа снизу
            outRect.bottom = mSpace;
        }

    }
}
