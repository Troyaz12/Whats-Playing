package com.example.android.whatsplaying_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.whatsplaying_2.sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{
    private String mSortOrder;
    private static final String MOVIEDETAIL_TAG = "MDTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSortOrder = Utility.getSortOrder(this);

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
        String sortOrder = Utility.getSortOrder(this);

        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            MainActivityFragment mf = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.movie_fragment);
            if ( null != mf ) {
                mf.onSortChanged();
            }
            MovieDetail.MovieDetailFragment mdf = (MovieDetail.MovieDetailFragment)getSupportFragmentManager().findFragmentByTag(MOVIEDETAIL_TAG);
            if(null!=mdf){
                MovieDetail.MovieDetailFragment fragment = new MovieDetail.MovieDetailFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment,MOVIEDETAIL_TAG)
                        .commit();
            }
            mSortOrder = sortOrder;
        }

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
