package com.example.android.whatsplaying_2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.whatsplaying_2.sync.MovieSyncAdapter;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements WhatsHot.Callback{
    private static final String MOVIEDETAIL_TAG = "MDTAG";
    private boolean mTwoPane;

    private SectionPagerAdapter mSectionPagerAdapter;

    private ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MovieSyncAdapter.initializeSyncAdapter(this);

        if (findViewById(R.id.movie_detail_container)!=null){
            //two pane mode tablet computers
            mTwoPane = true;

            if (savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetail.MovieDetailFragment(), MOVIEDETAIL_TAG)
                        .commit();
            }
        }else{
            mTwoPane=false;
        }
        Context context = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                .build()
        );

        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_whatshot);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_stars);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_favorite);


    }

    private void setupViewPager(ViewPager mViewPager) {

        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WhatsHot());
        adapter.addFragment(new HighestRated());
        adapter.addFragment(new WhatsHot());
        mViewPager.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this,SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onResume(){
        super.onResume();
    }
    public void onItemSelected(Uri contentUri){  //need to figure out how to add sortOrder
        if(mTwoPane){
        //for two pane mode, show detail view in this activity by changing the detail frament through a fragment transaction
            Bundle args = new Bundle();
            args.putParcelable(MovieDetail.MovieDetailFragment.DETAIL_URI, contentUri);

            MovieDetail.MovieDetailFragment fragment = new MovieDetail.MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment,MOVIEDETAIL_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, MovieDetail.class)
                .setData(contentUri);
            startActivity(intent);
        }


    }
}
