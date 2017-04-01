package cz.petrkubes.ioweyou.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.petrkubes.ioweyou.Pojos.Debt;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Tools.Tools;

/**
 * Adapter for displaying info about debts in myDebts and theirDebts tabs
 *
 * @author Petr Kubes
 */

public class DebtsAdapter extends ArrayAdapter<Debt> {

    private ArrayList<Debt> debts;
    private boolean myDebts = true;

    public DebtsAdapter(Context context, List<Debt> debts, boolean myDebts) {
        super(context, R.layout.item_debt, debts);
        this.debts = (ArrayList<Debt>) debts;
        this.myDebts = myDebts;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Debt debt = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.item_debt, parent, false);

            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            viewHolder.txtWhat = (TextView) convertView.findViewById(R.id.txt_who);
            viewHolder.txtNote = (TextView) convertView.findViewById(R.id.txt_note);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txt_status);
            viewHolder.txtId = (TextView) convertView.findViewById(R.id.txt_id);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (debt != null) {
            // Populate the data from the data object via the viewHolder object into the template view.
            viewHolder.txtName.setText(debt.who);
            viewHolder.txtWhat.setText(debt.what);
            viewHolder.txtNote.setText(debt.note);
            viewHolder.txtDate.setText(Tools.formatDate(debt.createdAt));
            viewHolder.txtStatus.setText(debt.status);
            viewHolder.txtId.setText(String.format(Locale.getDefault(), "#%d", debt.id));

            // Set color of owned items
            if (myDebts) {
                viewHolder.txtWhat.setTextColor(getContext().getResources().getColor(R.color.red));
            } else {
                viewHolder.txtWhat.setTextColor(getContext().getResources().getColor(R.color.green));
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

    /**
     * View lookup cache
     */
    private static class ViewHolder {
        TextView txtId;
        TextView txtName;
        TextView txtWhat;
        TextView txtNote;
        TextView txtDate;
        TextView txtStatus;
    }
}
