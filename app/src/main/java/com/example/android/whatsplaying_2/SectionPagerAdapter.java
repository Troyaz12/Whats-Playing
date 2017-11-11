package com.example.android.whatsplaying_2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TroysMacBook on 11/8/17.
 */

public class SectionPagerAdapter extends FragmentPagerAdapter{

    private final List<Fragment> mFragmentList = new ArrayList<>();


    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {




        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }


    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }










}
