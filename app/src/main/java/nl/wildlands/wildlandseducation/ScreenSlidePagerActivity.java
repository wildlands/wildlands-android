package nl.wildlands.wildlandseducation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;

import java.util.ArrayList;

import nl.wildlands.wildlandseducation.Pinpoint.Page;
import nl.wildlands.wildlandseducation.SQLite.PinpointsDataSource;


public class ScreenSlidePagerActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    int numberOfPages;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    private PinpointsDataSource pinpointsDataSource;
    private ArrayList<Page> allPages;
    private ArrayList<Page> pages;
    private WebView wv;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customviewpager);
        numberOfPages = 2;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Instantiate a ViewPager and a PagerAdapter.
        pinpointsDataSource = new PinpointsDataSource(this.getApplicationContext());
        pinpointsDataSource.open();
        pages = new ArrayList<Page>();
        allPages = pinpointsDataSource.getAllPages();

        long id = -1;
        Bundle button =getIntent().getExtras();
        if(button!=null)
        {
            id =button.getLong("BUTTON");
            Log.d("id", String.valueOf(id));
        }
        for(Page page: allPages)
        {
            if(page.getId() == id)
            {
                pages.add(page);
            }
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

               Fragment view =     new ScreenSlidePageFragment();
            return view;
        }

        @Override
        public int getCount() {
            return numberOfPages;
        }
    }
}