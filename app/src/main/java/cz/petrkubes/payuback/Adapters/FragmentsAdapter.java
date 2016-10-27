package cz.petrkubes.payuback.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cz.petrkubes.payuback.Fragments.DebtsFragment;
import cz.petrkubes.payuback.Fragments.FeedFragment;
import cz.petrkubes.payuback.Fragments.FriendsFragment;

/**
 * Created by petr on 22.10.16.
 */

public class FragmentsAdapter extends FragmentPagerAdapter {

    private Fragment fragment;
    private Bundle args;

    public FragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {


        switch (i) {
            case 0:
                fragment = new DebtsFragment();
                args = new Bundle();
                args.putBoolean(DebtsFragment.ARG_OBJECT, true);
                fragment.setArguments(args);
                return fragment;
            case 1:
                fragment = new DebtsFragment();
                args = new Bundle();
                args.putBoolean(DebtsFragment.ARG_OBJECT, true);
                fragment.setArguments(args);
                return fragment;
            case 2:
                fragment = new FeedFragment();
                return fragment;
            case 3:
                fragment = new FriendsFragment();
                return fragment;
        }

        Fragment fragment = new DebtsFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(DebtsFragment.ARG_OBJECT, i + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My debts";
            case 1:
                return "Their debts";
            case 2:
                return "Action feed";
            case 3:
                return "Friends";
            default:
                return "Error";
        }
    }
}