package com.example.android.whatsplaying_2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by TroysMacBook on 5/6/16.
 */
public class MovieSyncService extends Service{

    private static final Object mSyncAdapterLock = new Object();
    private static MovieSyncAdapter mMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (mSyncAdapterLock) {
            if (mMovieSyncAdapter == null) {
                mMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMovieSyncAdapter.getSyncAdapterBinder();
    }



}
