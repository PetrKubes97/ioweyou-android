package cz.petrkubes.ioweyou.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import cz.petrkubes.ioweyou.Pojos.Debt;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Tools.Tools;

import static android.view.View.GONE;

/**
 * Adapter for displaying debts connected to a specific friend in Friends tab after clicking on a friend
 *
 * @author Petr Kubes
 */

public class FriendsDebtsAdapter extends ArrayAdapter<Debt> {

    private ArrayList<Debt> debts;
    private Integer userId;
    private ArrayList<Debt> selectedDebts;

    public FriendsDebtsAdapter(Context context, ArrayList<Debt> objects, Integer userId) {
        super(context, R.layout.item_friends_debts, objects);
        this.debts = objects;
        this.userId = userId;
        selectedDebts = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final Debt debt = getItem(position);
        final FriendsDebtsAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new FriendsDebtsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.item_friends_debts, parent, false);

            viewHolder.txtWhat = (TextView) convertView.findViewById(R.id.txt_what);
            viewHolder.txtCreatedAt = (TextView) convertView.findViewById(R.id.txt_created_at);
            viewHolder.txtNote = (TextView) convertView.findViewById(R.id.txt_note);
            viewHolder.chckbSelected = (CheckBox) convertView.findViewById(R.id.chck_selected);
            viewHolder.layout = convertView.findViewById(R.id.friend_debt_item_layout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FriendsDebtsAdapter.ViewHolder) convertView.getTag();
        }

        if (debt != null) {
            // Populate the data from the data object via the viewHolder object into the template view.
            viewHolder.txtWhat.setText(debt.what);
            viewHolder.txtNote.setText(debt.note);
            viewHolder.txtCreatedAt.setText(Tools.formatDate(debt.createdAt));
            viewHolder.chckbSelected.setChecked(selectedDebts.contains(debt));

            // Disable buttons for locked debts
            if (debt.managerId != null && !debt.managerId.equals(userId)) {
                viewHolder.chckbSelected.setVisibility(GONE);
            } else {
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!selectedDebts.contains(debt)) {
                            selectedDebts.add(debt);
                            viewHolder.chckbSelected.setChecked(true);
                        } else {
                            selectedDebts.remove(debt);
                            viewHolder.chckbSelected.setChecked(false);
                        }
                    }
                });
            }

            // Set color of owned items
            if (debt.creditorId != null && debt.creditorId.equals(userId)) {
                viewHolder.txtWhat.setTextColor(getContext().getResources().getColor(R.color.green));
            } else {
                viewHolder.txtWhat.setTextColor(getContext().getResources().getColor(R.color.red));
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }

    @Nullable
    @Override
    public Debt getItem(int position) {
        return debts.get(position);
    }

    @Override
    public int getCount() {
        return debts.size();
    }

    public ArrayList<Debt> getSelectedDebts() {
        return selectedDebts;
    }

    /**
     * View lookup cache
     */
    private static class ViewHolder {
        TextView txtCreatedAt;
        TextView txtWhat;
        TextView txtNote;
        CheckBox chckbSelected;
        View layout;
    }

}
