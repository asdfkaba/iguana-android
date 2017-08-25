package iguana.iguana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import iguana.iguana.models.Comment;
import iguana.iguana.models.Project;


public class CommentAdapter extends BaseAdapter<Comment> {


    public CommentAdapter(Context context, OnViewHolderClick listener) {
        super(context, listener, null);
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.comment_list_entry, viewGroup, false);
        return view;
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
        }
    }

}
