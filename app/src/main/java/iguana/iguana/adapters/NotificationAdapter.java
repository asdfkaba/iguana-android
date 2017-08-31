package iguana.iguana.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import iguana.iguana.R;
import iguana.iguana.models.Comment;
import iguana.iguana.models.Notification;

import static android.R.id.list;

/**
 * Created by moritz on 30.08.17.
 */

public class NotificationAdapter extends BaseAdapter<Notification> {

    public NotificationAdapter(Context context, OnViewHolderClick listener, OnViewHolderLongClick long_listener) {
        super(context, listener, long_listener);
    }

    public void replace_item(String notification) {
        int idx = -1;
        int i = 0;
        for (Notification noti:this.items)   {
            if (noti.getIssue().startsWith(notification)){
                idx = i;
                break;
            }
            i++;
        }
        if (idx >= 0) {
            items.remove(idx);
            notifyItemRemoved(idx);
        }
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.notification_list_entry, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(Notification item, BaseAdapter.ViewHolder viewHolder) throws ParseException {
        TextView issue = (TextView) viewHolder.getView(R.id.issue);
        TextView types = (TextView) viewHolder.getView(R.id.types);
        types.setText("");
        issue.setText(item.getIssue());
        issue.setTypeface(null, Typeface.BOLD);
        System.out.println(item.getType());
        Set<String> unique = new HashSet<String>(item.getType());
        types.setLines(unique.size());
        for (String key : unique) {
            if (key.equals("NewIssue"))
                issue.append(" NEW ISSUE");
            else
                types.append(key + ": "+ Collections.frequency(item.getType(), key)+ System.getProperty("line.separator"));
        }


    }
}
