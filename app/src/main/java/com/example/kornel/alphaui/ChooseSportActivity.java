package com.example.kornel.alphaui;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ChooseSportActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private List<String> mGpsActivities;
    private List<String> mNonGpsActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sport);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mGpsActivities = new ArrayList<>();
        mNonGpsActivities = new ArrayList<>();

        for (GpsBasedActivity activity : GpsBasedActivity.values()) {
            mGpsActivities.add(activity.toString());
        }
        for (NonGpsBasedActivity activity : NonGpsBasedActivity.values()) {
            mNonGpsActivities.add(activity.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    // // A placeholder fragment containing a simple view.
    // public static class PlaceholderFragment extends Fragment implements
    //         ActivityAdapter.ListItemClickListener {
    //     private static final String ARG_SECTION_NUMBER = "section_number";
    //
    //     private ActivityAdapter mActivityAdapter;
    //     private RecyclerView mRecyclerView;
    //
    //     private List<String> mActivitiesList = new ArrayList<>();
    //
    //
    //     public PlaceholderFragment() {
    //     }
    //
    //     // Returns a new instance of this fragment for the given section number.
    //     public static PlaceholderFragment newInstance(int sectionNumber) {
    //         Log.d("kurr", "newInstance: ");
    //         PlaceholderFragment fragment = new PlaceholderFragment();
    //         Bundle args = new Bundle();
    //         args.putInt(ARG_SECTION_NUMBER, sectionNumber);
    //         fragment.setArguments(args);
    //         return fragment;
    //     }
    //
    //     @Override
    //     public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                              Bundle savedInstanceState) {
    //         Log.d("kurr", "onCreateView: ");
    //         View rootView = inflater.inflate(R.layout.fragment_choose_sport, container, false);
    //
    //         mRecyclerView = rootView.findViewById(R.id.activitiesRecyclerView);
    //         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    //         mRecyclerView.setLayoutManager(linearLayoutManager);
    //         mRecyclerView.setHasFixedSize(true);
    //
    //         // TODO change 100
    //
    //         mActivitiesList.add("22");
    //         mActivitiesList.add("22");
    //         mActivitiesList.add("22");
    //         mActivitiesList.add("22");
    //          mActivityAdapter = new ActivityAdapter(this, mActivitiesList);
    //         mRecyclerView.setAdapter(mActivityAdapter);
    //
    //
    //         return rootView;
    //     }
    //
    //     @Override
    //     public void onListItemClick(int clickedItemIndex) {
    //         // if (mToast != null) {
    //         //     mToast.cancel();
    //         // }
    //         Log.d("kur", "onListItemClick: ");
    //         String msg = "Clicked: " + clickedItemIndex;
    //         Log.d("kur", "onListItemClick: " + msg);
    //         // mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
    //         // mToast.show();
    //     }
    // }


    // A {@link FragmentPagerAdapter} that returns a fragment corresponding to
    // one of the sections/tabs/pages.
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            GpsBasedFragment gpsBasedFragment = new GpsBasedFragment();

            if (position == 0) {
                gpsBasedFragment.setActivitiesList(mGpsActivities);
            } else {
                gpsBasedFragment.setActivitiesList(mNonGpsActivities);
            }

            // return PlaceholderFragment.newInstance(position + 1);
            return gpsBasedFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
