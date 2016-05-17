package com.example.android.whatsplaying_2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.whatsplaying_2.sync.MovieSyncAdapter;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {
    private String mSortOrder;
    private final String MAINACTIVITY_TAG = "MATAG";
    public Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSortOrder = Utility.getSortOrder(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainActivityFragment(), MAINACTIVITY_TAG)
                    .commit();
        }

        final Context context =this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        //  .enableDumpapp(new SampleDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build()
        );

        MovieSyncAdapter.initializeSyncAdapter(this);

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
            MainActivityFragment mf = (MainActivityFragment)getSupportFragmentManager().findFragmentByTag(MAINACTIVITY_TAG);
            if ( null != mf ) {
                mf.onSortChanged();
            }
            mSortOrder = sortOrder;
        }

    }
}
