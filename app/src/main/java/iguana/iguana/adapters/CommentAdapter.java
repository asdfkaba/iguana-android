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

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import iguana.iguana.R;
import iguana.iguana.fragments.comment.CommentEditFragment;
import iguana.iguana.fragments.issue.IssueEditFragment;
import iguana.iguana.models.Comment;
import iguana.iguana.models.Project;


public class CommentAdapter extends BaseAdapter<Comment> {


    public CommentAdapter(Context context, OnViewHolderClick listener, OnViewHolderLongClick long_listener) {
        super(context, listener, long_listener);
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.comment_list_entry, viewGroup, false);
        return view;
    }

    public void replace_item(Comment item, boolean deleted) {
        int idx = -1;
        int i = 0;
        for (Comment comment:this.items)   {
            if (comment.getNameShort().equals(item.getNameShort()) && comment.getIssueNumber() == item.getIssueNumber() && comment.getSeqnum().equals(item.getSeqnum())) {
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
        } else {
            items.add(items.size(), item);
            notifyItemInserted(items.size());
        }
    }

    @Override
    protected void bindView(Comment item, BaseAdapter.ViewHolder viewHolder)  {
        if (item != null) {
            TextView info = (TextView) viewHolder.getView(R.id.comment_info);
            TextView text = (TextView) viewHolder.getView(R.id.comment_text);
            DateTime dt = new DateTime(item.getWhen());
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm");
            String test = fmt.print(dt);
            text.setText(item.getText());
            info.setText(item.getCreator() + " on " + test);
            ImageButton edit = (ImageButton) viewHolder.getView(R.id.edit);
            final Comment comment = item;
            if (item.isSelected()) {
                edit.setVisibility(View.VISIBLE);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager frag;
                        frag = ((Activity) v.getContext()).getFragmentManager();
                        CommentEditFragment fragment = new CommentEditFragment();
                        Bundle d = new Bundle();
                        d.putParcelable("comment", comment);
                        fragment.setArguments(d);
                        FragmentTransaction ft = frag.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, "comment_edit");
                        ft.addToBackStack("comment_edit");
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

}
