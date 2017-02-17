package cz.petrkubes.ioweyou.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import cz.petrkubes.ioweyou.Fragments.ActionsFragment;
import cz.petrkubes.ioweyou.Fragments.DebtsFragment;
import cz.petrkubes.ioweyou.Fragments.FriendsFragment;
import cz.petrkubes.ioweyou.Fragments.UpdateableFragment;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Tools.Const;

/**
 * Adapter for displaying displaying fragments
 *
 * @author Petr Kubes
 */

public class FragmentsAdapter extends FragmentPagerAdapter {

    private Fragment fragment;
    private Bundle args;
    private Context context;

    public FragmentsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
            Log.d(Const.TAG, "Calling update in adapter: " + object.toString());
            ((UpdateableFragment) object).update();
        }

        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.my_debts);
            case 1:
                return context.getString(R.string.their_debts);
            case 2:
                return context.getString(R.string.action_feed);
            case 3:
                return context.getString(R.string.friends);
            default:
                return "Error";
        }
    }
}