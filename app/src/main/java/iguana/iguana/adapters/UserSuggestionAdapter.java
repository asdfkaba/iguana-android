package iguana.iguana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import iguana.iguana.R;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.rtoken_invalid;
import iguana.iguana.models.Project;
import iguana.iguana.models.Token;
import iguana.iguana.models.User;
import iguana.iguana.models.UserResult;
import iguana.iguana.remote.ApiUtils;
import iguana.iguana.remote.apicalls.UserCalls;
import retrofit2.Call;

/**
 * Created by moritz on 17.09.17.
 */

public class UserSuggestionAdapter extends android.widget.BaseAdapter implements Filterable, ListAdapter {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<User> resultList = new ArrayList<User>();
    private Project project;


    public UserSuggestionAdapter(Context context, Project proj) {
        mContext = context;
        project = proj;
    }

    public int getCount() {
            return resultList.size();
        }



    @Override
    public User getItem(int index) {
            return resultList.get(index);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).getUsername());
            ((TextView) convertView.findViewById(R.id.text2)).setText(getItem(position).getFirst_name()+getItem(position).getLast_name());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<User> users = findUsers(mContext, constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = users;
                        filterResults.count = users.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        resultList = (List<User>) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetChanged();
                    }
                }};
            return filter;
        }

        /**
         * Returns a search result for the given book title.
         */
        private List<User> findUsers(Context context, String q) {
            HashMap options = new HashMap();
            options.put("username__contains", q);
            Call<UserResult> result = ((MainActivity) context).get_api_service().getUsers(options);
            retrofit2.Response<UserResult> response = null;
            try {
                response = result.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response == null)
                new ArrayList<User>();
            if (response.isSuccessful()) {
                List<User> temp = response.body().getResults();
                for (User user: temp) {
                    if (project.getMembers().contains(user.getUsername()))
                        temp.remove(user);
                }
                return temp;
            } else {
                new ArrayList<User>();
            }
            return new ArrayList<User>();
        }
}
