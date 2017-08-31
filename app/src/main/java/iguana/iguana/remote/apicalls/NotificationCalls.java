package iguana.iguana.remote.apicalls;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;


import java.util.HashMap;
import java.util.Map;

import iguana.iguana.R;
import iguana.iguana.adapters.NotificationAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.notification_deleted;
import iguana.iguana.models.NotificationResult;
import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by moritz on 27.08.17.
 */

public class NotificationCalls {
    private View rootView;

    public NotificationCalls(View view) {
        this.rootView = view;
    }
    private APIService get_api_service(View view) {
        return ((MainActivity) view.getContext()).get_api_service();
    }


    public void getNotifications(Integer pa, NotificationAdapter ada) {
        final NotificationAdapter adapter = ada;
        final Integer page = pa;
        final View view = rootView;
        final ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressBar);

        Map options = new HashMap<String, String>();
        options.put("page", page);
        get_api_service(view).getNotifications().enqueue(new Callback<NotificationResult>() {
            @Override
            public void onResponse(Call<NotificationResult> call, Response<NotificationResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getNotifications(new Integer(page + 1), adapter);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationResult> call, Throwable t) {
                Toast.makeText(view.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);

            }
        });
    }

    public void deleteNotification(String iss) {
        final String issue = iss;
        get_api_service(rootView).deleteNotification(issue).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                EventBus.getDefault().postSticky(new notification_deleted(issue));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
            }
        });
    }

}
