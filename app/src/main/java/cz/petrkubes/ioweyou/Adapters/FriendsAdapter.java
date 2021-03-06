package cz.petrkubes.ioweyou.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.petrkubes.ioweyou.Pojos.Friend;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Tools.Tools;

/**
 * Adapter for displaying friends in the friends tab
 *
 * @author Petr Kubes
 */

public class FriendsAdapter extends ArrayAdapter<Friend> {

    private ArrayList<Friend> friends;

    public FriendsAdapter(Context context, List<Friend> friends) {
        super(context, R.layout.item_debt, friends);
        this.friends = (ArrayList<Friend>) friends;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Friend friend = getItem(position);

        FriendsAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new FriendsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.item_friend, parent, false);

            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            viewHolder.txtStuff = (TextView) convertView.findViewById(R.id.txt_stuff);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FriendsAdapter.ViewHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object into the template view.
        if (friend != null) {
            viewHolder.txtName.setText(friend.name);
            viewHolder.txtStuff.setText(Tools.fromHtml(friend.debtsString));
        }

        // Return the completed view to render on screen
        return convertView;
    }

    @Nullable
    @Override
    public Friend getItem(int position) {
        return friends.get(position);
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    /**
     * View lookup cache
     */
    private static class ViewHolder {
        TextView txtName;
        TextView txtStuff;
    }
}
