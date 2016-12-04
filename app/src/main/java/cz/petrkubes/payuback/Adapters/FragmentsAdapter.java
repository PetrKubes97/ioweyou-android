package cz.petrkubes.payuback.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import cz.petrkubes.payuback.Const;
import cz.petrkubes.payuback.Fragments.DebtsFragment;
import cz.petrkubes.payuback.Fragments.ActionsFragment;
import cz.petrkubes.payuback.Fragments.FriendsFragment;
import cz.petrkubes.payuback.Fragments.UpdateableFragment;

/**
 * Created by petr on 22.10.16.
 */

public class FragmentsAdapter extends FragmentStatePagerAdapter {

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
                args.putBoolean(DebtsFragment.ARG_MY, true);
                fragment.setArguments(args);
                return fragment;
            case 1:
                fragment = new DebtsFragment();
                args = new Bundle();
                args.putBoolean(DebtsFragment.ARG_MY, false);
                fragment.setArguments(args);
                return fragment;
            case 2:
                fragment = new ActionsFragment();
                return fragment;
            case 3:
                fragment = new FriendsFragment();
                return fragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof UpdateableFragment) {
            Log.d(Const.TAG, "Calling update in adapter");
            ((UpdateableFragment) object).update();
        }

        return super.getItemPosition(object);
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