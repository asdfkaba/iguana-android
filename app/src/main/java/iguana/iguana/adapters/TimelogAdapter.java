package iguana.iguana.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import iguana.iguana.R;
import iguana.iguana.fragments.comment.CommentEditFragment;
import iguana.iguana.fragments.timelog.TimelogEditFragment;
import iguana.iguana.models.Comment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Timelog;


public class TimelogAdapter extends BaseAdapter<Timelog> {

    public TimelogAdapter(Context context, OnViewHolderClick listener, OnViewHolderLongClick long_listener) {
        super(context, listener, long_listener);
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.timelog_list_entry, viewGroup, false);
        return view;
    }

    public void replace_item(Timelog item, boolean deleted) {
        int idx = -1;
        int i = 0;
        for (Timelog log:this.items)   {
            if (log.getNameShort().equals(item.getNameShort()) && log.getIssueNumber() == item.getIssueNumber() && log.getNumber().equals(item.getNumber())) {
                idx = i;
                break;
            }
            i++;
        }
        if (idx >= 0) {
            if (deleted) {
                items.remove(idx);
                notifyItemRemoved(idx);
            } else {
                items.set(idx, item);
                notifyItemChanged(idx);
            }
        }  else {
            items.add(items.size(), item);
            notifyItemInserted(items.size());
        }
    }

    @Override
    protected void bindView(Timelog item, BaseAdapter.ViewHolder viewHolder) {
        if (item != null) {
            TextView time = (TextView) viewHolder.getView(R.id.time);
            TextView created_at = (TextView) viewHolder.getView(R.id.created_at);
            TextView issue = (TextView) viewHolder.getView(R.id.issue);
            time.setText(item.getTime());
            DateTime dt = new DateTime(item.getCreatedAt());
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm");
            String test = fmt.print(dt);
            created_at.setText(test);
            issue.setText(" on " + item.getIssue().split(" ")[0] );
            ImageButton edit = (ImageButton) viewHolder.getView(R.id.edit);
            final Timelog timelog = item;
            if (item.isSelected()) {
                edit.setVisibility(View.VISIBLE);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager frag;
                        frag = ((Activity) v.getContext()).getFragmentManager();
                        TimelogEditFragment fragment = new TimelogEditFragment();
                        Bundle d = new Bundle();
                        d.putParcelable("timelog", timelog);
                        fragment.setArguments(d);
                        FragmentTransaction ft = frag.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, "timelog_edit");
                        ft.addToBackStack("timelog_edit");
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                });
            } else {
                edit.setVisibility(View.GONE);
                edit.setOnClickListener(null);
            }
        }
    }

    public void addAll(List<Timelog> list) {
        items.addAll(list);
        Comparator<Timelog> comp;
        comp = new Comparator<Timelog>() {
            @Override
            public int compare(Timelog log1, Timelog log2) {
                return log2.getCreatedAt().compareTo(log1.getCreatedAt());
            }
        };
        Collections.sort(items, comp);
    }



}
