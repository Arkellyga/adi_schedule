package net.arkellyga.adischedule;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ArrayFragment.OnFragmentChangeWeekListener {

    Toolbar mToolbar;
    Fragment mActuallyFragment;
    FragmentTransaction mFragTransaction;
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    PagerTabStrip mPagerTabStrip;
    LinearLayout mLinearLayout;
    private boolean mViewPagerMode;
    private String mCurrentWeek;
    private int mCurrentDay;
    SharedPreferences mSp;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = PreferenceManager.getDefaultSharedPreferences(this);
        ThemeUtils.onActivityCreateSetTheme(this, mSp.getString("pref_theme", "0"));
        setContentView(R.layout.activity_main);
        mLinearLayout = (LinearLayout) findViewById(R.id.fragmentLayoutMain);
        checkAlarms();
        onFirstStartup();
        setupToolbar();
        // Up if % 2 == 0, down if % 2 != 0
        mCurrentWeek = DayHelper.getCurrentWeek();
        mCurrentDay = DayHelper.getCurrentDay();
        mViewPagerMode = Values.ARRAY_READ_MODE;
        setupViewPager();
    }

    private void checkAlarms() {
        if (mSp.getBoolean(PrefFragment.KEY_PREF_NOTIFIES, true)) {
            boolean alarms = NotificationService.checkServiceAlarms(getApplicationContext());
            if (!alarms)
                NotificationService.updateAllServiceAlarms(getApplicationContext());
        }
    }

    private void onFirstStartup() {
        mSp = getPreferences(MODE_PRIVATE);
        if (mSp.getBoolean(Values.SHARED_KEY_FIRST_STARTUP, true)) {
            Toast.makeText(this, "Первая загрузка может потребовать немного времени", Toast.LENGTH_SHORT).show();
            DataBaseHandler db = new DataBaseHandler(getApplicationContext());
            String[] days = { Values.TABLE_MONDAY, Values.TABLE_TUESDAY, Values.TABLE_WEDNESDAY,
                    Values.TABLE_THURSDAY, Values.TABLE_FRIDAY};
            for (int i = 0; i < 5; i++) {
                db.addLesson(new Lesson("Заполни меня", "Преподаватель", "Аудитория", "up", 0, 1), days[i]);
                db.addLesson(new Lesson("Заполни меня", "Преподаватель", "Аудитория", "up", 1, 1), days[i]);
                db.addLesson(new Lesson("Заполни меня", "Преподаватель", "Аудитория", "up", 2, 1), days[i]);
                db.addLesson(new Lesson("Заполни меня", "Преподаватель", "Аудитория", "up", 3, 1), days[i]);
                // Up and down week. Refactor this.
                db.addLesson(new Lesson("Заполни меня", "Преподаватель", "Аудитория", "down", 0, 1), days[i]);
                db.addLesson(new Lesson("Заполни меня", "Преподаватель", "Аудитория", "down", 1, 1), days[i]);
                db.addLesson(new Lesson("Заполни меня", "Преподаватель", "Аудитория", "down", 2, 1), days[i]);
                db.addLesson(new Lesson("Заполни меня", "Преподаватель", "Аудитория", "down", 3, 1), days[i]);
            }
            List<TimeLesson> list = new ArrayList<>();
            String[] timeFrom = new String[] {"8:00", "9:55", "11:45", "13:35"};
            String[] timeTo = new String[] {"9:35", "11:30", "13:20", "15:10"};
            for (int i = 0; i < 4; i++) {
                list.add(new TimeLesson(i, timeFrom[i], timeTo[i]));
            }
            db.setTimes(list);
            SharedPreferences.Editor ed = mSp.edit();
            ed.putBoolean(Values.SHARED_KEY_FIRST_STARTUP, false);
            ed.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_objects:
                setupViewPager();
                return true;
            case R.id.menu_edit_lessons:
                if (mViewPagerMode)
                    mViewPagerMode = Values.ARRAY_READ_MODE;
                else
                    mViewPagerMode = Values.ARRAY_EDIT_MODE;
                setupViewPager();
                return true;
            case R.id.menu_alarms:
                NotificationService.deleteServiceAlarms(getApplicationContext());
                Toast.makeText(this, "alarms was deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_profile:
                Log.d("MENU","Profile was added.");
                setFragment(new ProfileFragment());
                return true;
            case R.id.menu_statistics:
                Toast.makeText(this, "Пока в процессе.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_settings:
                setFragment(new PrefFragment());
                Log.d("MainActivity","menu_settings set.");
                return true;
            case R.id.menu_about:
                ThemeUtils.changeToTheme(this, ThemeUtils.THEME_BLUE);
                Toast.makeText(this, "about", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setFragment(Fragment fragment) {
        mLinearLayout.removeAllViews();
        mFragTransaction = getFragmentManager().beginTransaction();
        mActuallyFragment = fragment;
        mFragTransaction.replace(R.id.fragmentLayoutMain,mActuallyFragment);
        mFragTransaction.commit();
    }
    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(mToolbar);
        mToolbar.setLogo(R.drawable.ic_launcher_old);
        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_menu));
    }
    private void setupViewPager() {
        mLinearLayout.removeAllViews();
        //Задаем параметры для ViewPager
        mLinearLayout.addView(LayoutInflater.from(this).inflate(R.layout.fragment_viewpager,null));
        mViewPager = (ViewPager) mLinearLayout.findViewById(R.id.view_pager_main);
        mPagerTabStrip = (PagerTabStrip) mLinearLayout.findViewById(R.id.pager_tab_strip_main);
        mPagerTabStrip.setDrawFullUnderline(false);
        mPagerAdapter = new ArrayFragmentPagerAdapter(getSupportFragmentManager(),mViewPagerMode);
        mViewPager.setAdapter(mPagerAdapter);
        // Set current day of week.
        mViewPager.setCurrentItem(mCurrentDay);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentDay = position;
                Log.d("mViewPager.Listener", "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onFragmentChangeWeek(String week, boolean mode) {
        mCurrentWeek = week;
        mViewPagerMode = mode;
        setupViewPager();
    }

    private class ArrayFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private boolean mMode;

        public ArrayFragmentPagerAdapter(FragmentManager fm, boolean mode) {
            super(fm);
            mMode = mode;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return ArrayFragment.newInstance(mMode, position, mCurrentWeek);
        }

        @Override
        public int getCount() {
            // Five days in week.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Понедельник";
                case 1:
                    return "Вторник";
                case 2:
                    return "Среда";
                case 3:
                    return "Четверг";
                case 4:
                    return "Пятница";
                default:
                    return "Error";
            }
        }
    }
}
